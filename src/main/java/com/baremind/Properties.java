package com.baremind;

import com.baremind.data.Property;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("properties")
public class Properties {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Property.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Property.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Property entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Property newData) {
        return Impl.updateById(sessionId, id, newData, Property.class, (exist, property) -> {
            String name = property.getName();
            if (name != null) {
                exist.setName(name);
            }

            String value = property.getValue();
            if (value != null) {
                exist.setValue(value);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Property.class);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("scheduler-counter")
    public Response incCount() {
        Property property = JPAEntry.getObject(Property.class, "name", "scheduler-counter");
        long long_value = Long.parseLong(property.getValue());
        String value = String.valueOf(long_value + 1);
        property.setValue(value);
        JPAEntry.genericPut(property);
        return Response.ok().build();
    }

    static String getProperty(String name) {
        String result = null;
        Property property = JPAEntry.getObject(Property.class, "name", name);
        if (property != null) {
            result = property.getValue();
        }
        return result;
    }

    static void setProperty(String name, String value) {
        Property property = JPAEntry.getObject(Property.class, "name", name);
        if (property != null) {
            property.setValue(value);
            JPAEntry.genericPut(property);
        } else {
            property = new Property();
            property.setId(IdGenerator.getNewId());
            property.setName(name);
            property.setValue(value);
            JPAEntry.genericPost(property);
        }
    }
}
