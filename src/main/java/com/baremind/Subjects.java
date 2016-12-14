package com.baremind;

import com.baremind.data.Log;
import com.baremind.data.Subject;
import com.baremind.utils.Impl;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("subjects")
public class Subjects {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Subject.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Subject.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Subject entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Subject newData) {
        return Impl.updateById(sessionId, id, newData, Subject.class, (exist, subject) -> {
            String no = subject.getNo();
            if (no != null) {
                exist.setNo(no);
            }
            String name = subject.getName();
            if (name != null) {
                exist.setName(name);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Subject.class);
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subjectPopup(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Log log = Logs.insert(Long.parseLong(sessionId), "subject", id, "popup");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @GET
    @Path("{id}/popup")
    @Produces(MediaType.APPLICATION_JSON)
    public Response isPopup(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Long popCount = Logs.getUserStatsCount(Long.parseLong(sessionId), "subject", id, "popup");
            Boolean r = false;
            if (popCount > 0) {
                r = true;
            }
            result = Response.ok("{\"popup\":" + r + "}").build();
        }
        return result;
    }
}
