package com.baremind;

import com.baremind.data.Additional;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

//GET .../entities/id
//GET .../entities?filter={"tablename":"111"}


@Path("additionals")
public class Additionals {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAdditional(@CookieParam("userId") String userId, Additional additional) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            additional.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(additional);
            result = Response.ok(additional).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdditionals(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Additional> additionals = JPAEntry.getList(Additional.class, filterObject);
            if (!additionals.isEmpty()) {
                result = Response.ok(new Gson().toJson(additionals)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdditionalById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Additional additional = JPAEntry.getObject(Additional.class, "id", id);
            if (additional != null) {
                result = Response.ok(new Gson().toJson(additional)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAdditional(@CookieParam("userId") String userId, @PathParam("id") Long id, Additional additional) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Additional existAdditional = JPAEntry.getObject(Additional.class, "id", id);
            if (existAdditional != null) {
                String tableName = additional.getObjectType();
                if (tableName != null) {
                    existAdditional.setObjectType(tableName);
                }
                Long objectId = additional.getObjectId();
                if (objectId != null) {
                    existAdditional.setObjectId(objectId);
                }
                String name = additional.getName();
                if (name != null) {
                    existAdditional.setName(name);
                }
                String value = additional.getValue();
                if (value != null) {
                    existAdditional.setValue(value);
                }
                JPAEntry.genericPut(existAdditional);
                result = Response.ok(existAdditional).build();
            }
        }
        return result;
    }
}
