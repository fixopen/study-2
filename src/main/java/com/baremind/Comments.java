package com.baremind;

import com.baremind.data.Comment;
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
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComment(@CookieParam("sessionId") String sessionId, Comment comment) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            comment.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(comment);
            result = Response.ok(comment).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public Response getCommentById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public Response updateComment(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Comment comment) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Comment existComment = JPAEntry.getObject(Comment.class, "id", id);
            if (existComment != null) {
                String clientId = comment.getClientId();
                if (clientId != null) {
                    existComment.setClientId(clientId);
                }

                Long objectId = comment.getObjectId();
                if (objectId != null) {
                    existComment.setObjectId(objectId);
                }

                String objectType = comment.getObjectType();
                if (objectType != null) {
                    existComment.setObjectType(objectType);
                }

                String content = comment.getContent();
                if (content != null) {
                    existComment.setContent(content);
                }

                Date createTime = comment.getCreateTime();
                if (createTime != null) {
                    existComment.setCreateTime(createTime);
                }

                Date updateTime = comment.getUpdateTime();
                if (updateTime != null) {
                    existComment.setUpdateTime(updateTime);
                }

                Long userId = comment.getUserId();
                if (userId != null) {
                    existComment.setUserId(userId);
                }

                JPAEntry.genericPut(existComment);
                result = Response.ok(existComment).build();
            }
        }
        return result;
    }
}
