package com.baremind.utils;

import com.baremind.data.Entity;
import com.baremind.data.Session;
import com.baremind.data.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

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

    public static <T> Response finalResult(T entity, PostProcessor<T> postProcessor) {
        Response result;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        if (postProcessor != null) {
            result = Response.ok(gson.toJson(postProcessor.convert(entity))).build();
        } else {
            result = Response.ok(gson.toJson(entity)).build();
        }
        return result;
    }

    public static <T> Response finalResult(List<T> entities, PostProcessor<T> postProcessor, Predicate<T> accept) {
        Response result;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        if (postProcessor != null) {
            result = Response.ok(gson.toJson(postProcessor.process(entities, accept))).build();
        } else {
            result = Response.ok(gson.toJson(entities)).build();
        }
        return result;
    }

    public static Response validationUser(String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.getLoginUser(sessionId) != null) {
            result = Response.status(202).build();
        }
        return result;
    }

    public static Response validationAdmin(String sessionId) {
        Response result = Response.status(401).build();
        User admin = JPAEntry.getLoginUser(sessionId);
        if (admin != null && admin.getIsAdministrator() != null && admin.getIsAdministrator()) {
            result = Response.status(202).build();
        }
        return result;
    }

    public static <T> Response genericQueryIdProcessor(Long id, Class<T> type, PostProcessor<T> postProcessor) {
        Response result = Response.status(404).build();
        T entity = JPAEntry.getObject(type, "id", id);
        if (entity != null) {
            result = finalResult(entity, postProcessor);
        }
        return result;
    }

    private static <T> Response genericQueryProcessor(Map<String, Object> filterObject, Map<String, String> orders, Class<T> type, PostProcessor<T> postProcessor, Predicate<T> accept) {
        Response result = Response.status(404).build();
        List<T> entities = JPAEntry.getList(type, filterObject, orders);
        if (!entities.isEmpty()) {
            result = finalResult(entities, postProcessor, accept);
        }
        return result;
    }

    public static <T> Response get(String sessionId, String filter, Map<String, String> orders, Class<T> type, PostProcessor<T> postProcessor, Predicate<T> accept) {
        Response result = validationUser(sessionId);
        if (result.getStatus() == 202) {
            final Map<String, Object> filterObject = getFilters(filter);
            if (filterObject != null) {
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
            }
            result = genericQueryProcessor(filterObject, orders, type, postProcessor, accept);
        }
        return result;
    }

    public static <T> Response get(String sessionId, Map<String, Object> filterObject, Map<String, String> orders, Class<T> type, PostProcessor<T> postProcessor, Predicate<T> accept) {
        Response result = validationUser(sessionId);
        if (result.getStatus() == 202) {
            result = genericQueryProcessor(filterObject, orders, type, postProcessor, accept);
        }
        return result;
    }

    public static <T> Response getById(String sessionId, Long id, Class<T> type, PostProcessor<T> postProcessor) {
        Response result = validationUser(sessionId);
        if (result.getStatus() == 202) {
            result = genericQueryIdProcessor(id, type, postProcessor);
        }
        return result;
    }

    public static <T extends Entity> Response create(String sessionId, T entity, UnaryOperator<T> fillEntity, PostProcessor<T> postProcessor) {
        Response result = validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            entity.setId(IdGenerator.getNewId());
            if (fillEntity != null) {
                entity = fillEntity.apply(entity);
            }
            JPAEntry.genericPost(entity);
            result = finalResult(entity, postProcessor);
        }
        return result;
    }

    public static <T> Response deleteById(String sessionId, Long id, Class<T> type) {
        Response result = validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(type, "id", id);
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }

    public static <T> Response updateById(String sessionId, Long id, T newData, Class<T> type, BiConsumer<T, T> update, PostProcessor<T> postProcessor) {
        Response result = validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            result = Response.status(404).build();
            T existData = JPAEntry.getObject(type, "id", id);
            if (existData != null) {
                update.accept(existData, newData);
                JPAEntry.genericPut(existData);
                result = finalResult(existData, postProcessor);
            }
        }
        return result;
    }

    public static Response getUserSelf(String sessionId) {
        Response result = Response.status(401).build();
        User user = JPAEntry.getLoginUser(sessionId);
        if (user != null) {
            result = finalResult(user, null);
        }
        return result;
    }

    public static Response getSelf(String sessionId) {
        Response result = Response.status(401).build();
        Session session = JPAEntry.getSession(sessionId);
        if (session != null) {
            result = finalResult(session, null);
        }
        return result;
    }

    public static Response updateUserSelf(String sessionId, User newData, BiConsumer<User, User> update) {
        Response result = Response.status(401).build();
        User user = JPAEntry.getLoginUser(sessionId);
        if (user != null) {
            update.accept(user, newData);
            JPAEntry.genericPut(user);
            result = finalResult(user, null);
        }
        return result;
    }

    public static Response updateSelf(String sessionId, Session newData, BiConsumer<Session, Session> update) {
        Response result = Response.status(401).build();
        Session session = JPAEntry.getSession(sessionId);
        if (session != null) {
            update.accept(session, newData);
            JPAEntry.genericPut(session);
            result = finalResult(session, null);
        }
        return result;
    }

    public static Response deleteUserSelf(String sessionId) {
        Response result = Response.status(401).build();
        User user = JPAEntry.getLoginUser(sessionId);
        if (user != null) {
            long count = JPAEntry.genericDelete(User.class, "id", user.getId());
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }

    public static Response deleteSelf(String sessionId) {
        Response result = Response.status(401).build();
        Session session = JPAEntry.getSession(sessionId);
        if (session != null) {
            long count = JPAEntry.genericDelete(User.class, "identity", sessionId);
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }

    public static Map<String, Object> getFilters(String filter) {
        Map<String, Object> result = null;
        if (!Objects.equals(filter, "")) {
            try {
                String rawFilter = URLDecoder.decode(filter, StandardCharsets.UTF_8.toString());
                result = new Gson().fromJson(rawFilter, new TypeToken<Map<String, Object>>() {
                }.getType());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
