package com.baremind;
import com.baremind.data.Entity;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by fixopen on 9/11/2016.
 */
@Path("/")
public class BaseServlet {
    @GET
    @Produces("application/json")
    public <T> Response get(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter, Class<T> type) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<T> entities = JPAEntry.getList(type, filterObject);
            if (!entities.isEmpty()) {
                result = Response.ok(new Gson().toJson(entities)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public <T> Response getById(@CookieParam("userId") String userId, @PathParam("id") Long id, Class<T> type) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            T entity = JPAEntry.getObject(type, "id", id);
            if (entity != null) {
                result = Response.ok(new Gson().toJson(entity)).build();
            }
        }
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public <T extends Entity> Response createLog(@CookieParam("userId") String userId, T entity) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            entity.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(entity);
            result = Response.ok(entity).build();
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public <T> Response updateById(@CookieParam("userId") String aUserId, @PathParam("id") Long id, T newData, Class<T> type, BiConsumer<T, T> update) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(aUserId)) {
            result = Response.status(404).build();
            T existData = JPAEntry.getObject(type, "id", id);
            if (existData != null) {
                update.accept(existData, newData);
                JPAEntry.genericPut(existData);
                result = Response.ok(existData).build();
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public <T> Response deleteLog(@CookieParam("userId") String userId, @PathParam("id") Long id, Class<T> type) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(type, "id", id);
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }

//    public static void main(String[] args) throws IOException {
//        HttpServer server = HttpServerFactory.create("http://localhost:9998/");
//        server.start();
//
//        System.out.println("Server running");
//        System.out.println("Visit: http://localhost:9998/helloworld");
//        System.out.println("Hit return to stop...");
//        System.in.read();
//        System.out.println("Stopping server");
//        server.stop(0);
//        System.out.println("Server stopped");
//    }
}
