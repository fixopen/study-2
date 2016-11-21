package com.baremind;

import com.baremind.data.Additional;
import com.baremind.utils.Impl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//GET .../entities/id
//GET .../entities?filter={"tablename":"111"}


@Path("additionals")
public class Additionals {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Additional.class, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Additional.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Additional entity) {
        return Impl.create(sessionId, entity, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Additional newData) {
        return Impl.updateById(sessionId, id, newData, Additional.class, (exist, additional) -> {
            String tableName = additional.getObjectType();
            if (tableName != null) {
                exist.setObjectType(tableName);
            }
            Long objectId = additional.getObjectId();
            if (objectId != null) {
                exist.setObjectId(objectId);
            }
            String name = additional.getName();
            if (name != null) {
                exist.setName(name);
            }
            String value = additional.getValue();
            if (value != null) {
                exist.setValue(value);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Additional.class);
    }
}
