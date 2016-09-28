package com.baremind;

import com.baremind.data.AnswerRecord;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/9/27.
 */

@Path("answer-records")
public class AnswerRecords {

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAnsewrRecords(@CookieParam("sessionId") String sessionId, AnswerRecord answerRecord) {

        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            answerRecord.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(answerRecord);
            result = Response.ok(answerRecord).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnswerRecord(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<AnswerRecord> answerRecords = JPAEntry.getList(AnswerRecord.class, filterObject, orders);
            if (!answerRecords.isEmpty()) {
                result = Response.ok(new Gson().toJson(answerRecords)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAnswerRecordById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            AnswerRecord answerRecord = JPAEntry.getObject(AnswerRecord.class, "id", id);
            if (answerRecord != null) {
                result = Response.ok(new Gson().toJson(answerRecord)).build();
            }
        }
        return result;
    }
    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAnswerRecord(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, AnswerRecord answerRecord) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            AnswerRecord existAnswerRecord = JPAEntry.getObject(AnswerRecord.class, "id", id);
            if (existAnswerRecord != null) {
                Long answer = answerRecord.getAnswer();
                if (answer != null) {
                    existAnswerRecord.setAnswer(answer);
                }
                Date commitTime = answerRecord.getCommitTime();
                if (commitTime != null) {
                    existAnswerRecord.setCommitTime(commitTime);
                }
                Long problemId = answerRecord.getProblemId();
                if (problemId != null) {
                    existAnswerRecord.setProblemId(problemId);
                }
                Long userId = answerRecord.getUserId();
                if (userId != null) {
                    existAnswerRecord.setProblemId(userId);
                }
                JPAEntry.genericPut(existAnswerRecord);
                result = Response.ok(existAnswerRecord).build();
            }
        }
        return result;
    }

}
