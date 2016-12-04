package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static com.baremind.KnowledgePoints.knowledgePointContent;

/**
 * Created by fixopen on 2/12/2016.
 */
@Path("resources")
public class Resources {
    private static Object getResource(String type, Long id, String sessionId) {
        Object result = null;
        switch (type) {
            case "knowledgePoint":
                String s = knowledgePointContent(id, sessionId);
                result = s;
                break;
            case "video":
                Scheduler scheduler = JPAEntry.getObject(Scheduler.class, "id", id);
                result = scheduler.getContentLink();
                break;
            case "liveVideo":
                Scheduler object = JPAEntry.getObject(Scheduler.class, "id", id);
                result = object.getDirectLink();
                break;
        }
        return result;
    }

    public static Object getAmount(Long id, String type) {
        Object result = null;
        Scheduler scheduler = JPAEntry.getObject(Scheduler.class, "id", id);
        switch (type) {
            case "knowledgePoint":
                KnowledgePoint knowledgePoint = JPAEntry.getObject(KnowledgePoint.class, "id", id);
                result = knowledgePoint.getPrice();
                break;
            case "video":
                    if(scheduler.getContentLink() !=null){
                        result = scheduler.getPrice();
                    }
                break;
            case "liveVideo":

                result = scheduler.getPrice();
                break;
        }
        return result;
    }



    /*@GET
    @Path("{type}/{id}/sale-info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSaleInfo(@CookieParam("sessionId") String sessionId,  @PathParam("type") String type, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Object resource = getResource(type, id, sessionId);
            result = Response.ok(resource).build();
        }
        return result;
    }*/

    @GET
    @Path("{id}/{type}/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo(@CookieParam("sessionId") String sessionId, @PathParam("type") String type, @PathParam("id") Long id){
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Object amount = getAmount(id,type);
            Long userId = JPAEntry.getLoginId(sessionId);
            User user = JPAEntry.getObject(User.class, "id", userId);
//            List<Card> cards = JPAEntry.getList(Card.class, "userId", userId);
            Map<String, Object> r = new HashMap<>();
            r.put("price", amount);
            r.put("user_amount", user.getAmount());
            List<Card> cards = JPAEntry.getList(Card.class, "userId", userId);
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

                Object resource = getResource(type, id,sessionId);
                result = Response.ok(resource).build();
            }
        }
        return result;
    }
}
