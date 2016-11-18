package com.baremind;

import com.baremind.data.Entity;
import com.baremind.data.User;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.Condition;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by fixopen on 9/11/2016.
 */
public class GenericServlet {
    private String[] ops = {"IS NOT ", "IS ", "< ", "<= ", "> ", ">= ", "!= ", "BETWEEN ", "IN "};

    private String[] split(String value) {
        String[] result = {"", "", ""};
        for (String op : ops) {
            if (value.startsWith(op)) {
                result[0] = op.trim();
                int pos = value.indexOf("::");
                if (pos != -1) {
                    result[1] = value.substring(op.length(), pos);
                    result[2] = value.substring(pos + 2);
                } else {
                    result[1] = value.substring(op.length());
                }
                break;
            }
        }
        return result;
    }

    @GET
    @Produces("application/json")
    public <T> Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter, Class<T> type) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            final Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            filterObject.forEach((key, value) -> {
                if (value instanceof String) {
                    String[] opAndValue = split((String) value);
                    if (!opAndValue[0].equals("")) {
                        Object val = null;
                        if (!opAndValue[1].equals("NULL")) {
                            String typeName = opAndValue[2];
                            switch (typeName) {
                                case "timestamp":
                                    val = new Date(Long.parseLong(opAndValue[1]));
                                    break;
                                case "integer":
                                case "int":
                                    val = Integer.parseInt(opAndValue[1]);
                                    break;
                                case "long":
                                    val = Long.parseLong(opAndValue[1]);
                                    break;
                                case "bool":
                                    val = Boolean.parseBoolean(opAndValue[1]);
                                    break;
                                default:
                                    val = opAndValue[1];
                                    break;
                            }
                        }
                        Condition c = new Condition(opAndValue[0], val);
                        filterObject.put(key, c);
                    }
                }
            });
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
    public <T> Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Class<T> type) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", Long.parseLong(sessionId));
            if (admin.getIsAdministrator()) {
                result = Response.status(404).build();
                T entity = JPAEntry.getObject(type, "id", id);
                if (entity != null) {
                    result = Response.ok(new Gson().toJson(entity)).build();
                }
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf(@CookieParam("sessionId") String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            User entity = JPAEntry.getObject(User.class, "id", Long.parseLong(sessionId));
            if (entity != null) {
                result = Response.ok(new Gson().toJson(entity)).build();
            }
        }
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public <T extends Entity> Response create(@CookieParam("sessionId") String sessionId, T entity) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public <T> Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, T newData, Class<T> type, BiConsumer<T, T> update) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", Long.parseLong(sessionId));
            if (admin.getIsAdministrator()) {
                result = Response.status(404).build();
                T existData = JPAEntry.getObject(type, "id", id);
                if (existData != null) {
                    update.accept(existData, newData);
                    JPAEntry.genericPut(existData);
                    result = Response.ok(existData).build();
                }
            }
        }
        return result;
    }

    @PUT //根据token修改
    @Path("self")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSelf(@CookieParam("sessionId") String sessionId, User newData, BiConsumer<User, User> update) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            User existData = JPAEntry.getObject(User.class, "id", Long.parseLong(sessionId));
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
    public <T> Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Class<T> type) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", Long.parseLong(sessionId));
            if (admin.getIsAdministrator()) {
                result = Response.status(404).build();
                long count = JPAEntry.genericDelete(type, "id", id);
                if (count > 0) {
                    result = Response.ok().build();
                }
            }
        }
        return result;
    }

    @DELETE
    @Path("self")
    public Response deleteSelf(@CookieParam("sessionId") String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(User.class, "id", Long.parseLong(sessionId));
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }
}
