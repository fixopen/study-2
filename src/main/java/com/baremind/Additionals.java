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
    public Response createAdditionals(@CookieParam("sessionId") String sessionId, Additional additional) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            additional.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(additional);
            result = Response.ok(additional).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdditionals(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public Response getAdditionalById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public Response updateAdditionals(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Additional additional) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Additional existadditional = JPAEntry.getObject(Additional.class, "id", id);
            if (existadditional != null) {
                String name = additional.getName();
                if (name != null) {
                    existadditional.setName(name);
                }
                Long objectId = additional.getObjectId();
                if (objectId != null) {
                    existadditional.setObjectId(objectId);
                }
                String tableName = additional.getTableName();
                if (tableName != null) {
                    existadditional.getTableName();
                }
                String value = additional.getValue();
                if (value != null) {
                    existadditional.setValue(value);
                }
                existadditional.setObjectId(additional.getObjectId());
                existadditional.setTableName(additional.getTableName());
                existadditional.setValue(additional.getValue());
                JPAEntry.genericPut(existadditional);
                result = Response.ok(existadditional).build();
            }
        }
        return result;
    }

}
