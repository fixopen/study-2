package com.baremind;

import com.baremind.data.Log;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("logs")
public class Logs {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLike(@CookieParam("sessionId") String sessionId, Log log) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            log.setId(IdGenerator.getNewId());
            log.setCreateTime(new Date());
            JPAEntry.genericPost(log);
            result = Response.ok(log).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikes(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public Response getLikeById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public Response updateLike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Log log) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public Response deleteLike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Log l = JPAEntry.getObject(Log.class, "id", id);
            if (l != null) {
                EntityManager em = JPAEntry.getEntityManager();
                em.getTransaction().begin();
                em.remove(l);
                em.getTransaction().commit();
                result = Response.ok(200).build();
            }
        }
        return result;
    }

}
