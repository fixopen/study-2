package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fixopen on 2/12/2016.
 */
@Path("resources")
public class Resources {
    private static Object getResource(String type, Long id) {
        Object result = null;
        switch (type) {
            case "knowledgePoint":
                result = JPAEntry.getObject(KnowledgePoint.class, "id", id);
                break;
            case "video":
                result = JPAEntry.getObject(Scheduler.class, "id", id);
                break;
        }
        return result;
    }

    @GET
    @Path("{type}/{id}/sale-info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaleInfo(@CookieParam("sessionId") String sessionId,  @PathParam("type") String type, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Object resource = getResource(type, id);
            Long userId = JPAEntry.getLoginId(sessionId);
            User user = JPAEntry.getObject(User.class, "id", userId);
            List<Card> cards = JPAEntry.getList(Card.class, "userId", userId);
            Map<String, Object> r = new HashMap<>();
            r.put("resource", resource);
            r.put("user", user);
            r.put("cards", cards);
            result = Response.ok(r).build();
        }
        return result;
    }

    @GET
    @Path("{type}/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId,  @PathParam("type") String type, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(403).build();
            Long userId = JPAEntry.getLoginId(sessionId);
            Map<String, Object> filter = new HashMap<>();
            filter.put("userId", userId);
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

                Consumption consumption = new Consumption();
                consumption.setId(IdGenerator.getNewId());
                consumption.setUserId(userId);
                consumption.setObjectId(id);
                consumption.setObjectType(type);
                consumption.setTimestamp(new Date());
                consumption.setTransactionId(findTransaction.getId());
                JPAEntry.genericPost(consumption);

                Object resource = getResource(type, id);
                result = Response.ok(resource).build();
            }
        }
        return result;
    }
}
