package com.baremind;

import com.baremind.data.ProblemStandardAnswer;
import com.baremind.utils.Impl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by User on 2016/9/19.
 */
@Path("problem-standard-answers")
public class ProblemStandardAnswers {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, ProblemStandardAnswer.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, ProblemStandardAnswer.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, ProblemStandardAnswer entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, ProblemStandardAnswer newData) {
        return Impl.updateById(sessionId, id, newData, ProblemStandardAnswer.class, (exist, problemsStandardAnswer) -> {
            Long problemId = problemsStandardAnswer.getProblemId();
            if (problemId != null) {
                exist.setProblemId(problemId);
            }

            Integer name = problemsStandardAnswer.getIndex();
            if (name != 0) {
                exist.setIndex(name);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, ProblemStandardAnswer.class);
    }
}
