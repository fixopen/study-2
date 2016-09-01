package com.baremind;

import com.baremind.data.Property;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("properties")
public class Properties {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProperty(@CookieParam("sessionId") String sessionId, Property property) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            property.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(property);
            result = Response.ok(property).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperties(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Property> properties = JPAEntry.getList(Property.class, filterObject);
            if (!properties.isEmpty()) {
                result = Response.ok(new Gson().toJson(properties)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPropertyById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Property property = JPAEntry.getObject(Property.class, "id", id);
            if (property != null) {
                result = Response.ok(new Gson().toJson(property)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProperty(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Property property) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Property existproperty = JPAEntry.getObject(Property.class, "id", id);
            if (existproperty != null) {
                String name = property.getName();
                if (name != null) {
                    existproperty.setName(name);
                }

                String value = property.getValue();
                if (value != null) {
                    existproperty.setValue(value);
                }

                JPAEntry.genericPut(existproperty);
                result = Response.ok(existproperty).build();
            }
        }
        return result;
    }
}
