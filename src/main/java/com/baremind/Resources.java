package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by fixopen on 2/12/2016.
 */
@Path("resources")
public class Resources {
    static String join(List<String> ids) {
        String result = "";
        boolean isFirst = true;
        for (String id : ids) {
            if (!isFirst) {
                result += ", ";
            }
            result += id;
            isFirst = false;
        }
        return result;
    }

    public static <T> List<T> getList(EntityManager em, List<String> ids, Class<T> type) {
        return getList(em, "id", ids, type);
    }

    public static <T> List<T> getList(EntityManager em, String columnName, List<String> ids, Class<T> type) {
        List<T> result = null;
        if (!ids.isEmpty()) {
            String query = "SELECT o FROM " + type.getSimpleName() + " o WHERE o." + columnName + " IN ( " + join(ids) + " )";
            TypedQuery<T> pq = em.createQuery(query, type);
            result = pq.getResultList();
        }
        return result;
    }

    public static <T> T findItem(List<T> container, Predicate<T> p) {
        T result = null;
        for (T item : container) {
            if (p.test(item)) {
                result = item;
                break;
            }
        }
        return result;
    }

    public static <T> List<T> findItems(List<T> container, Predicate<T> p) {
        return container.stream().filter(p).collect(Collectors.toList());
    }

    public static Long findUntypedItem(List<Object[]> container, Long id) {
        Long result = null;
        for (Object[] item : container) {
            if (((Long)item[0]).longValue() == id.longValue()) {
                result = (Long)item[1];
                break;
            }
        }
        return result;
    }

    public static List<Object[]> findUntypedItems(List<Object[]> container, Long id) {
        return container.stream().filter((item) -> ((Long)item[0]).longValue() == id.longValue()).collect(Collectors.toList());
    }

    static Resource getByTypeAndId(EntityManager em, String type, Long id) {
        Resource result = null;
        switch (type) {
            case "knowledgePoint":
                result = JPAEntry.getObject(em, KnowledgePoint.class, "id", id);
                break;
            case "video":
                result = JPAEntry.getObject(em, Scheduler.class, "id", id);
                break;
            case "liveVideo":
                result = JPAEntry.getObject(em, Scheduler.class, "id", id);
                break;
        }
        return result;

    }

    @GET
    @Path("{type}/{id}/sale-info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaleInfo(@CookieParam("sessionId") String sessionId,  @PathParam("type") String type, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Map<String, Object> r = new HashMap<>();
            EntityManager em = JPAEntry.getEntityManager();
            Resource resource = getByTypeAndId(em, type, id);
            r.put("price", resource.getAmount());
            r.put("subjectId", resource.getSubjectId());
            User user = JPAEntry.getLoginUser(sessionId);
            r.put("user_amount", user.getAmount());
            List<Card> cards = JPAEntry.getList(Card.class, "userId", user.getId());
            r.put("cards", cards);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = Response.ok(gson.toJson(r)).build();
        }
        return result;
    }

    @GET
    @Path("{type}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId,  @PathParam("type") String type, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            result = Response.status(403).build();
            User user = JPAEntry.getLoginUser(sessionId);
            Map<String, Object> filter = new HashMap<>();
            filter.put("userId", user.getId());
            filter.put("objectType", type);
            filter.put("objectId", id);
            List<Transaction> ts = JPAEntry.getList(Transaction.class, filter);
            List<Consumption> cs = JPAEntry.getList(Consumption.class, filter);

            Long total = 0L;
            Map<Transaction, Long> transactionCount = new HashMap<>();
            for (Transaction t : ts) {
                Long count = t.getCount();
                transactionCount.put(t, count);
                total += count;
            }

            if (cs.size() < total) {
                for (Consumption c : cs) {
                    transactionCount.forEach((t, count) -> {
                        if (t.getId().longValue() == c.getTransactionId().longValue()) {
                            transactionCount.put(t, count - 1);
                        }
                    });
                }

                Transaction findTransaction = null;
                for (Map.Entry<Transaction, Long> item : transactionCount.entrySet()) {
                    if (item.getValue() > 0L) {
                        findTransaction = item.getKey();
                        break;
                    }
                }

                EntityManager em = JPAEntry.getEntityManager();
                Resource s = getByTypeAndId(em, type, id);
                if (s != null) {
                    Consumption consumption = new Consumption();
                    consumption.setId(IdGenerator.getNewId());
                    consumption.setUserId(user.getId());
                    consumption.setObjectId(id);
                    consumption.setObjectType(type);
                    consumption.setTimestamp(new Date());
                    consumption.setTransactionId(findTransaction.getId());
                    JPAEntry.genericPost(consumption);
                    JPAEntry.log(user.getId(), "read", type, id);
                    result = Response.ok(s.getContent()).build();
                }
            }
        }
        return result;
    }
}
