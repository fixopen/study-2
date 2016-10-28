package com.baremind;

import com.baremind.data.Comment;
import com.baremind.data.Log;
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

@Path("comments")
public class Comments {
    @PUT
    @Path("{id}/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Log log = Logs.insert(Long.parseLong(userId), "comment", id, "like");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @PUT
    @Path("{id}/unlike")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long count = Logs.deleteLike(Long.parseLong(userId), "comment", id);
            result = Response.ok("{\"count\":" + count.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/like-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikeCount(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long likeCount = Logs.getStatsCount("comment", id, "like");
            result = Response.ok("{\"count\":" + likeCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/is-self-like")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfLike(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Boolean has = Logs.has(Long.parseLong(userId), "comment", id, "like");
            result = Response.ok("{\"like\":" + has.toString() + "}").build();
        }
        return result;
    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComment(@CookieParam("userId") String userId, Comment comment) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            comment.setId(IdGenerator.getNewId());
            comment.setUserId(Long.parseLong(userId));
            Date now = new Date();
            comment.setCreateTime(now);
            comment.setUpdateTime(now);
            JPAEntry.genericPost(comment);
            result = Response.ok(comment).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Comment> comments = JPAEntry.getList(Comment.class, filterObject);
            if (!comments.isEmpty()) {
                result = Response.ok(new Gson().toJson(comments)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCommentById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Comment comment = JPAEntry.getObject(Comment.class, "id", id);
            if (comment != null) {
                result = Response.ok(new Gson().toJson(comment)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateComment(@CookieParam("userId") String aUserId, @PathParam("id") Long id, Comment comment) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(aUserId)) {
            result = Response.status(404).build();
            Comment existComment = JPAEntry.getObject(Comment.class, "id", id);
            if (existComment != null) {
                Long userId = comment.getUserId();
                if (userId != null) {
                    existComment.setUserId(userId);
                }

                String objectType = comment.getObjectType();
                if (objectType != null) {
                    existComment.setObjectType(objectType);
                }

                Long objectId = comment.getObjectId();
                if (objectId != null) {
                    existComment.setObjectId(objectId);
                }

                String content = comment.getContent();
                if (content != null) {
                    existComment.setContent(content);
                }

                Date updateTime = comment.getUpdateTime();
                if (updateTime != null) {
                    Date now = new Date();
                    existComment.setUpdateTime(now);
                }

                JPAEntry.genericPut(existComment);
                result = Response.ok(existComment).build();
            }
        }
        return result;
    }
}
