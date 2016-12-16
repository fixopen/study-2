package com.baremind;

import com.baremind.data.Log;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Path("logs")
public class Logs {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Log.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Log.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Log entity) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            entity.setId(IdGenerator.getNewId());
            entity.setUserId(Long.parseLong(sessionId));
            entity.setCreateTime(new Date());
            JPAEntry.genericPost(entity);
            result = Impl.finalResult(entity, null);
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Log newData) {
        return Impl.updateById(sessionId, id, newData, Log.class, (exist, log) -> {
            String objectType = log.getObjectType();
            if (objectType != null) {
                exist.setObjectType(objectType);
            }
            Long userId = log.getUserId();
            if (userId != null) {
                exist.setUserId(userId);
            }

            Date createTime = log.getCreateTime();
            if (createTime != null) {
                exist.setCreateTime(createTime);
            }

            Long objectId = log.getObjectId();
            if (objectId != null) {
                exist.setObjectId(objectId);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Log.class);
    }

    public static Log insert(Long userId, String objectType, Long objectId, String action) {
        Log log = new Log();
        log.setId(IdGenerator.getNewId());
        log.setUserId(userId);
        log.setCreateTime(new Date());
        log.setObjectType(objectType);
        log.setObjectId(objectId);
        log.setAction(action);
        JPAEntry.genericPost(log);
        return log;
    }

    public static Log insert(String sessionId, String objectType, Long objectId, String action) {
        return insert(JPAEntry.getLoginUser(sessionId).getId(), objectType, objectId, action);
    }

    static Long deleteLike(Long userId, String objectType, Long objectId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("userId", userId);
        filter.put("objectType", objectType);
        filter.put("objectId", objectId);
        filter.put("action", "like");
        return JPAEntry.genericDelete(Log.class, filter);
    }

    @GET
    @Path("{id}/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikeCount(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return getLogsCount(sessionId, "knowledge-point", id, "like");
    }

    @GET
    @Path("{objectType}/{objectId}/{action}/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLogsCount(@CookieParam("sessionId") String sessionId, @PathParam("objectType") String objectType, @PathParam("objectId") Long objectId, @PathParam("action") String action) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            Long count = getStatsCount(objectType, objectId, action);
            result = Response.ok("{\"count\":" + count.toString() + "}").build();
        }
        return result;
    }

    public static Long getStatsCount(String objectType, Long objectId, String action) {
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM Log l WHERE l.action = '" + action + "' AND l.objectType = '" + objectType + "' AND l.objectId = " + objectId.toString();
        TypedQuery<Long> q = em.createQuery(stats, Long.class);
        return q.getSingleResult();
    }

    static Long getUserStatsCount(Long userId, String objectType, Long objectId, String action) {
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM Log l WHERE l.userId = " + userId.toString() + " AND l.action = '" + action + "' and l.objectType = '" + objectType + "' AND l.objectId = " + objectId.toString();
        TypedQuery<Long> q = em.createQuery(stats, Long.class);
        return q.getSingleResult();
    }

    public static Boolean has(Long userId, String objectType, Long objectId, String action) {
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM Log l WHERE l.userId = " + userId.toString() + " AND l.action = '" + action + "' and l.objectType = '" + objectType + "' AND l.objectId = " + objectId.toString();
        TypedQuery<Long> q = em.createQuery(stats, Long.class);
        return q.getSingleResult() > 0L;
    }
}
