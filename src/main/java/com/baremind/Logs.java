package com.baremind;

import com.baremind.data.Log;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("logs")
public class Logs {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLike(@CookieParam("userId") String userId, Log log) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            log.setId(IdGenerator.getNewId());
            log.setUserId(Long.parseLong(userId));
            log.setCreateTime(new Date());
            JPAEntry.genericPost(log);
            result = Response.ok(log).build();
        }
        return result;
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
        return insert(JPAEntry.getLoginId(sessionId), objectType, objectId, action);
    }

    public static Long deleteLike(Long userId, String objectType, Long objectId) {
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
    public Response getLikeCount(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        return getLogsCount(userId, "knowledge-point", id, "like");
    }



    @GET
    @Path("{objectType}/{objectId}/{action}/count")
    @Produces(MediaType.APPLICATION_JSON)
    public static Response getLogsCount(@CookieParam("userId") String userId, @PathParam("objectType") String objectType, @PathParam("objectId") Long objectId, @PathParam("action") String action) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long count = getStatsCount(objectType, objectId, action);
            result = Response.ok("{\"count\":" + count.toString() + "}").build();
        }
        return result;
    }

    public static Long getStatsCount(String objectType, Long objectId, String action) {
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM Log l WHERE l.action = '" + action + "' AND l.objectType = '" + objectType + "' AND l.objectId = " + objectId.toString();
        Query q = em.createQuery(stats, Long.class);
        return (Long) q.getSingleResult();
    }

    public static Long getUserStatsCount(Long userId, String objectType, Long objectId, String action) {
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM Log l WHERE l.userId = " + userId.toString() + " AND l.action = '" + action + "' and l.objectType = '" + objectType + "' AND l.objectId = " + objectId.toString();
        Query q = em.createQuery(stats, Long.class);
        return (Long) q.getSingleResult();
    }

    public static Boolean has(Long userId, String objectType, Long objectId, String action) {
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM Log l WHERE l.userId = " + userId.toString() + " AND l.action = '" + action + "' and l.objectType = '" + objectType + "' AND l.objectId = " + objectId.toString();
        Query q = em.createQuery(stats, Long.class);
        return (Long) q.getSingleResult() > 0l;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikes(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Log> logs = JPAEntry.getList(Log.class, filterObject);
            if (!logs.isEmpty()) {
                result = Response.ok(new Gson().toJson(logs)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikeById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Log log = JPAEntry.getObject(Log.class, "id", id);
            if (log != null) {
                result = Response.ok(new Gson().toJson(log)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLike(@CookieParam("userId") String aUserId, @PathParam("id") Long id, Log log) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(aUserId)) {
            result = Response.status(404).build();
            Log existlike = JPAEntry.getObject(Log.class, "id", id);
            if (existlike != null) {
                String objectType = log.getObjectType();
                if (objectType != null) {
                    existlike.setObjectType(objectType);
                }
                Long userId = log.getUserId();
                if (userId != null) {
                    existlike.setUserId(userId);
                }

                Date createTime = log.getCreateTime();
                if (createTime != null) {
                    existlike.setCreateTime(createTime);
                }

                Long objectId = log.getObjectId();
                if (objectId != null) {
                    existlike.setObjectId(objectId);
                }

                JPAEntry.genericPut(existlike);
                result = Response.ok(existlike).build();
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteLog(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(Log.class, "id", id);
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }
}
