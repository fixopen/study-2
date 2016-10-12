package com.baremind;

import com.baremind.data.Problem;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("problems")
public class Problems {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProblem(@CookieParam("sessionId") String sessionId, Problem problem) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            problem.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(problem);
            result = Response.ok(problem).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProblems(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Problem> problems = JPAEntry.getList(Problem.class, filterObject);
            if (!problems.isEmpty()) {
                result = Response.ok(new Gson().toJson(problems)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProblemById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Problem problem = JPAEntry.getObject(Problem.class, "id", id);
            if (problem != null) {
                result = Response.ok(new Gson().toJson(problem)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProblem(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Problem problem) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Problem existproblem = JPAEntry.getObject(Problem.class, "id", id);
            if (existproblem != null) {
                Long knowledgePointId = problem.getKnowledgePointId();
                if (knowledgePointId != null) {
                    existproblem.setKnowledgePointId(knowledgePointId);
                }

                String title = problem.getName();
                if (title != null) {
                    existproblem.setName(title);
                }

                Long imageId = problem.getImageId();
                if (imageId != null) {
                    existproblem.setImageId(imageId);
                }

                Long videoId = problem.getVideoId();
                if (videoId != null) {
                    existproblem.setVideoId(videoId);
                }
                JPAEntry.genericPut(existproblem);
                result = Response.ok(existproblem).build();
            }
        }
        return result;
    }
}
