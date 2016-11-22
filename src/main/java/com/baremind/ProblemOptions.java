package com.baremind;

import com.baremind.data.ProblemOption;
import com.baremind.data.User;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by User on 2016/9/19.
 */
@Path("problem-options")
public class ProblemOptions {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, ProblemOption.class, ProblemOption::convertToMap, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, ProblemOption.class, ProblemOption::convertToMap);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, ProblemOption entity) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.create(sessionId, entity, null);
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, ProblemOption newData) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.updateById(sessionId, id, newData, ProblemOption.class, (exist, problemsOption) -> {
                    Long problemId = problemsOption.getProblemId();
                    if (problemId != null) {
                        exist.setProblemId(problemId);
                    }

                    String name = problemsOption.getName();
                    if (name != null) {
                        exist.setName(name);
                    }

                    Long imageId = problemsOption.getImageId();
                    if (imageId != null) {
                        exist.setImageId(imageId);
                    }

                    Integer index = problemsOption.getIndex();
                    if (index != null) {
                        exist.setIndex(index);
                    }

                    Integer order = problemsOption.getOrder();
                    if (order != null) {
                        exist.setOrder(order);
                    }
                }, null);
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.deleteById(sessionId, id, ProblemOption.class);
            }
        }
        return result;
    }
}
