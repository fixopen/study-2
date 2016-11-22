package com.baremind.utils;

import com.baremind.data.Entity;
import com.baremind.data.Session;
import com.baremind.data.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Created by fixopen on 19/11/2016.
 */
public class Impl {
    private static String[] ops = {"IS NOT ", "IS ", "< ", "<= ", "> ", ">= ", "!= ", "BETWEEN ", "IN "};

    private static String[] split(String value) {
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

    private static <T> Response finalResult(T entity, PostProcessor<T> postProcessor) {
        Response result;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        if (postProcessor != null) {
            result = Response.ok(gson.toJson(postProcessor.convert(entity))).build();
        } else {
            result = Response.ok(gson.toJson(entity)).build();
        }
        return result;
    }

    public static <T> Response get(String sessionId, String filter, Map<String, String> orders, Class<T> type, PostProcessor<T> postProcessor, Predicate<T> accept) {
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
            List<T> entities = JPAEntry.getList(type, filterObject, orders);
            if (!entities.isEmpty()) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                if (postProcessor != null) {
                    List<Map<String, Object>> r = postProcessor.process(entities, accept);
                    result = Response.ok(gson.toJson(r)).build();
                } else {
                    result = Response.ok(gson.toJson(entities)).build();
                }
            }
        }
        return result;
    }

    public static <T> Response getById(String sessionId, Long id, Class<T> type, PostProcessor<T> postProcessor) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin.getIsAdministrator()) {
                result = Response.status(404).build();
                T entity = JPAEntry.getObject(type, "id", id);
                if (entity != null) {
                    result = finalResult(entity, postProcessor);
                }
            }
        }
        return result;
    }

    public static <T extends Entity> Response create(String sessionId, T entity, PostProcessor<T> postProcessor) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            entity.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(entity);
            result = finalResult(entity, postProcessor);
        }
        return result;
    }

    public static <T> Response deleteById(String sessionId, Long id, Class<T> type) {
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

    public static <T> Response updateById(String sessionId, Long id, T newData, Class<T> type, BiConsumer<T, T> update, PostProcessor<T> postProcessor) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", Long.parseLong(sessionId));
            if (admin.getIsAdministrator()) {
                result = Response.status(404).build();
                T existData = JPAEntry.getObject(type, "id", id);
                if (existData != null) {
                    update.accept(existData, newData);
                    JPAEntry.genericPut(existData);
                    result = finalResult(existData, postProcessor);
                }
            }
        }
        return result;
    }

    public static Response getUserSelf(String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            User entity = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (entity != null) {
                result = finalResult(entity, null);
            }
        }
        return result;
    }

    public static Response getSelf(String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Session entity = JPAEntry.getObject(Session.class, "identity", sessionId);
            if (entity != null) {
                result = finalResult(entity, null);
            }
        }
        return result;
    }

    public static Response updateUserSelf(String sessionId, User newData, BiConsumer<User, User> update) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            User existData = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (existData != null) {
                update.accept(existData, newData);
                JPAEntry.genericPut(existData);
                result = finalResult(existData, null);
            }
        }
        return result;
    }

    public static Response updateSelf(String sessionId, Session newData, BiConsumer<Session, Session> update) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Session existData = JPAEntry.getObject(Session.class, "identity", sessionId);
            if (existData != null) {
                update.accept(existData, newData);
                JPAEntry.genericPut(existData);
                result = finalResult(existData, null);
            }
        }
        return result;
    }

    public static Response deleteUserSelf(String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }

    public static Response deleteSelf(String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(User.class, "identity", sessionId);
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }
}
