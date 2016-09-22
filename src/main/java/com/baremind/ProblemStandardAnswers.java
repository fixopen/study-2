package com.baremind;

import com.baremind.data.ProblemsOption;
import com.baremind.data.ProblemsStandardAnswer;
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
@Path("problems-standard-answers")
public class ProblemStandardAnswers {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMedia(@CookieParam("sessionId") String sessionId, ProblemsStandardAnswer problemsStandardAnswer) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            problemsStandardAnswer.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(problemsStandardAnswer);
            result = Response.ok(problemsStandardAnswer).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedias(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<ProblemsStandardAnswer> problemsStandardAnswer = JPAEntry.getList(ProblemsStandardAnswer.class, filterObject);
            if (!problemsStandardAnswer.isEmpty()) {
                result = Response.ok(new Gson().toJson(problemsStandardAnswer)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediaById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            ProblemsStandardAnswer problemsStandardAnswer = JPAEntry.getObject(ProblemsStandardAnswer.class, "id", id);
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
    public Response updateMedia(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, ProblemsStandardAnswer problemsStandardAnswer) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = new HashMap<>(1);
            filterObject.put("id", id);
            ProblemsStandardAnswer existmedia = JPAEntry.getObject(ProblemsStandardAnswer.class, "id", id);
            if (existmedia != null) {
                Long name = problemsStandardAnswer.getName();
                if (name != 0) {
                    existmedia.setName(name);
                }

                Long problemId = problemsStandardAnswer.getProblemId();
                if (problemId != null) {
                    existmedia.setProblemId(problemId);
                }

                JPAEntry.genericPut(existmedia);
                result = Response.ok(existmedia).build();
            }
        }
        return result;
    }
}
