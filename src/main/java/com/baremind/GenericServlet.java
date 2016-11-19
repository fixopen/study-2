package com.baremind;

import com.baremind.data.Entity;
import com.baremind.data.User;
import com.baremind.utils.Impl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.function.BiConsumer;

/**
 * Created by fixopen on 9/11/2016.
 */
public class GenericServlet {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public <T> Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter, Class<T> type) {
        return Impl.get(sessionId, filter, null, type, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public <T> Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Class<T> type) {
        return Impl.getById(sessionId, id, type, null);
    }

    @GET //根据id查询
    @Path("self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.getUserSelf(sessionId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public <T extends Entity> Response create(@CookieParam("sessionId") String sessionId, T entity) {
        return Impl.create(sessionId, entity, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public <T> Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, T newData, Class<T> type, BiConsumer<T, T> update) {
        return Impl.updateById(sessionId, id, newData, type, update, null);
    }

    @PUT //根据token修改
    @Path("self")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSelf(@CookieParam("sessionId") String sessionId, User newData, BiConsumer<User, User> update) {
        return Impl.updateUserSelf(sessionId, newData, update);
    }

    @DELETE
    @Path("{id}")
    public <T> Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Class<T> type) {
        return Impl.deleteById(sessionId, id, type);
    }

    @DELETE
    @Path("self")
    public Response deleteSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.deleteUserSelf(sessionId);
    }
}
