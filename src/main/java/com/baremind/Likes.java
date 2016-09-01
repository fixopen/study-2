package com.baremind;

import com.baremind.data.Like;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("likes")
public class Likes {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLike(@CookieParam("sessionId") String sessionId, Like like) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            like.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(like);
            result = Response.ok(like).build();
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
            List<Like> likes = JPAEntry.getList(Like.class, filterObject);
            if (!likes.isEmpty()) {
                result = Response.ok(new Gson().toJson(likes)).build();
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
            Like like = JPAEntry.getObject(Like.class, "id", id);
            if (like != null) {
                result = Response.ok(new Gson().toJson(like)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Like like) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Like existlike = JPAEntry.getObject(Like.class, "id", id);
            if (existlike != null) {
                String objectType = like.getObjectType();
                if (objectType != null) {
                    existlike.setObjectType(objectType);
                }
                Long userId = like.getUserId();
                if (userId != null) {
                    existlike.setUserId(userId);
                }

                Date createTime = like.getCreateTime();
                if (createTime != null) {
                    existlike.setCreateTime(createTime);
                }

                Long objectId = like.getObjectId();
                if (objectId != null) {
                    existlike.setObjectId(objectId);
                }

                JPAEntry.genericPut(existlike);
                result = Response.ok(existlike).build();
            }
        }
        return result;
    }
}
