package com.baremind;

import com.baremind.data.Property;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("properties")
public class Properties {
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("scheduler-counter")
    public Response incCount() {
        Property property = JPAEntry.getObject(Property.class, "id", 5);
        int long_value = Integer.parseInt(property.getValue()) +1 ;
        String  value =  String.valueOf(long_value);
        property.setValue(value);
        JPAEntry.genericPut(property);
        return Response.ok().build();
    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProperty(@CookieParam("userId") String userId, Property property) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            property.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(property);
            result = Response.ok(property).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperties(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
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
    public Response getPropertyById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Property property = JPAEntry.getObject(Property.class, "id", id);
            if (property != null) {
                result = Response.ok(new Gson().toJson(property)).build();
            }
        }
        return result;
    }

    public static String getPropertyValue(String name) {
        String result = null;
        Property property = JPAEntry.getObject(Property.class, "name", name);
        if (property != null) {
            result = property.getValue();
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProperty(@CookieParam("userId") String userId, @PathParam("id") Long id, Property property) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
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
