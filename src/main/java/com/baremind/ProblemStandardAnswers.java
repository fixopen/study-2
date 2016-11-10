package com.baremind;

import com.baremind.data.ProblemStandardAnswer;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/9/19.
 */
@Path("problem-standard-answers")
public class ProblemStandardAnswers {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProblemStandardAnswer(@CookieParam("userId") String userId, ProblemStandardAnswer problemsStandardAnswer) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            problemsStandardAnswer.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(problemsStandardAnswer);
            result = Response.ok(problemsStandardAnswer).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProblemStandardAnswers(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<ProblemStandardAnswer> problemsStandardAnswer = JPAEntry.getList(ProblemStandardAnswer.class, filterObject);
            if (!problemsStandardAnswer.isEmpty()) {
                result = Response.ok(new Gson().toJson(problemsStandardAnswer)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProblemStandardAnswerById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            ProblemStandardAnswer problemsStandardAnswer = JPAEntry.getObject(ProblemStandardAnswer.class, "id", id);
            if (problemsStandardAnswer != null) {
                result = Response.ok(new Gson().toJson(problemsStandardAnswer)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProblemStandardAnswer(@CookieParam("userId") String userId, @PathParam("id") Long id, ProblemStandardAnswer problemsStandardAnswer) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = new HashMap<>(1);
            filterObject.put("id", id);
            ProblemStandardAnswer existmedia = JPAEntry.getObject(ProblemStandardAnswer.class, "id", id);
            if (existmedia != null) {
                Long problemId = problemsStandardAnswer.getProblemId();
                if (problemId != null) {
                    existmedia.setProblemId(problemId);
                }

                Integer name = problemsStandardAnswer.getIndex();
                if (name != 0) {
                    existmedia.setIndex(name);
                }

                JPAEntry.genericPut(existmedia);
                result = Response.ok(existmedia).build();
            }
        }
        return result;
    }
}
