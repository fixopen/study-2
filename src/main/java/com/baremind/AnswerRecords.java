package com.baremind;

import com.baremind.data.AnswerRecord;
import com.baremind.utils.Impl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2016/9/27.
 */

@Path("answer-records")
public class AnswerRecords {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Map<String, String> orders = new HashMap<>();
        orders.put("order", "ASC");
        return Impl.get(sessionId, filter, orders, AnswerRecord.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, AnswerRecord.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, AnswerRecord entity) {
        return Impl.create(sessionId, entity, null, null);
    }


    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, AnswerRecord newData) {
        return Impl.updateById(sessionId, id, newData, AnswerRecord.class, (exist, answerRecord) -> {
            Integer answer = answerRecord.getIndex();
            if (answer != null) {
                exist.setIndex(answer);
            }
            Date commitTime = answerRecord.getCreateTime();
            if (commitTime != null) {
                exist.setCreateTime(commitTime);
            }
            Long problemId = answerRecord.getProblemId();
            if (problemId != null) {
                exist.setProblemId(problemId);
            }
            Long userId = answerRecord.getUserId();
            if (userId != null) {
                exist.setProblemId(userId);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, AnswerRecord.class);
    }
}
