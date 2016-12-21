package com.baremind;

import com.baremind.data.Comment;
import com.baremind.data.Log;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("comments")
public class Comments {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Comment.class, Comment::convertToMap, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Comment.class, Comment::convertToMap);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Comment entity) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            entity.setUserId(JPAEntry.getLoginUser(sessionId).getId());
            Date now = new Date();
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = Response.ok(gson.toJson(entity)).build();
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Comment newData) {
        //@@check self comment
        return Impl.updateById(sessionId, id, newData, Comment.class, (exist, comment) -> {
            Long userId = comment.getUserId();
            if (userId != null) {
                exist.setUserId(userId);
            }

            String objectType = comment.getObjectType();
            if (objectType != null) {
                exist.setObjectType(objectType);
            }

            Long objectId = comment.getObjectId();
            if (objectId != null) {
                exist.setObjectId(objectId);
            }

            String content = comment.getContent();
            if (content != null) {
                exist.setContent(content);
            }

            Date updateTime = comment.getUpdateTime();
            if (updateTime != null) {
                Date now = new Date();
                exist.setUpdateTime(now);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        //@@check self comment
        return Impl.deleteById(sessionId, id, Comment.class);
    }

    @PUT
    @Path("{id}/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Log log = Logs.insert(JPAEntry.getSession(sessionId).getUserId(), "comment", id, "like");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @PUT
    @Path("{id}/unlike")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Long count = Logs.deleteLike(JPAEntry.getSession(sessionId).getUserId(), "comment", id);
            result = Response.ok("{\"count\":" + count.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/like-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikeCount(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Long likeCount = Logs.getStatsCount("comment", id, "like");
            result = Response.ok("{\"count\":" + likeCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/is-self-like")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfLike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Boolean has = Logs.has(JPAEntry.getSession(sessionId).getUserId(), "comment", id, "like");
            result = Response.ok("{\"like\":" + has.toString() + "}").build();
        }
        return result;
    }
}
