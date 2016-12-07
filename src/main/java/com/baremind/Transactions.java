package com.baremind;

import com.baremind.data.Card;
import com.baremind.data.Transaction;
import com.baremind.data.TransferObject;
import com.baremind.data.User;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

import static com.baremind.Resources.getAmount;

/**
 * Created by fixopen on 2/12/2016.
 */
@Path("transactions")
public class Transactions {
    //create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Transaction entity) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            EntityManager em = JPAEntry.getNewEntityManager();
            Date now = new Date();
            entity.setTimestamp(now);
            Long sourceID = entity.getSourceId();
            String sourceType = entity.getSourceType();
            Long userId = JPAEntry.getLoginId(sessionId);
            Long objectId = entity.getObjectId();
            String objectType = entity.getObjectType();
            Long count = entity.getCount();
            Long money = (Long) getAmount(objectId,objectType);
            Long Transfer_amount = entity.getMoney();
            User user = JPAEntry.getObject(User.class, "id", userId);

            Boolean transfer = false;
            //src -> dst
           if(entity.getCount() == null && entity.getSourceId() != null){
               if(entity.getSourceType().equals("user") && entity.getObjectType().equals("card")){
                   Card card = JPAEntry.getObject(Card.class, "id", objectId);
                   if (card.getUserId().longValue() == userId.longValue()) {
                        transfer = transfer(em, user, card, Transfer_amount, userId, sourceID, sourceType, now, objectId, objectType, count);
                   } else {
                       return result = Response.status(409).build(); //不是你的卡
                   }

               }else if(entity.getSourceType().equals("card") && entity.getObjectType().equals("user")){
                   Card card = JPAEntry.getObject(Card.class, "id", sourceID);
                   if (card.getUserId().longValue() == userId.longValue()) {
                       transfer = transfer(em, card, user, Transfer_amount, userId, sourceID, sourceType, now, objectId, objectType, count);
                   } else {
                       return result = Response.status(409).build();//不是你的卡
                   }
               }else if(entity.getSourceType().equals("card") && entity.getObjectType().equals("card")){
                   Card beicard = JPAEntry.getObject(Card.class, "id", sourceID);//被
                   Card mucard = JPAEntry.getObject(Card.class, "id", objectId);//目标
                   if (beicard.getUserId().longValue() == userId.longValue()) {
                       transfer = transfer(em, beicard, mucard, Transfer_amount, userId,sourceID,sourceType, now, objectId, objectType, count);
                   } else {
                       return result = Response.status(409).build();//不是你的卡
                   }
               }
           }else if(entity.getCount() == null && entity.getSourceId() == null && entity.getSourceType().equals("recharge")){
                if(objectType.equals("user")){
                    transfer = recharge(em, user, Transfer_amount, userId, sourceID, sourceType, now, objectId, objectType, count);
                }else if(objectType.equals("card")){
                    Card card = JPAEntry.getObject(Card.class, "id", objectId);
                    transfer = recharge(em, card, Transfer_amount, userId, sourceID, sourceType, now, objectId, objectType, count);
                }
           }else if(entity.getCount() !=null){
                if(sourceType.equals("user")){
                    for (int i = 0; i < count; i++) {
                        transfer = purchase(em, user, money, userId, sourceID, sourceType, now, objectId, objectType, count);
                    }
                }else if(sourceType.equals("card")){
                    Card card = JPAEntry.getObject(Card.class, "id", sourceID);
                    for (int i = 0; i < count; i++) {
                        transfer = purchase(em, card, money, userId, sourceID, sourceType, now, objectId, objectType, count);
                    }
                }
           }
            if (transfer == true) {
                em.close();
                result = Response.ok().build();
            } else {
                result = Response.status(408).build();
                //余额不足
            }
           // result = Impl.create(sessionId, entity, null);
        }
        return result;
    }

    private static <T extends TransferObject> boolean purchase(EntityManager em, T src, Long money, Long userId, Long sourceId, String sourceType, Date now, Long objectId, String objectType, Long count) {
        boolean result = false;
        if (src.getAmount() >= money) {
            em.getTransaction().begin();

            src.setAmount(src.getAmount() - money);
            em.merge(src);

            postTransferObject(userId, sourceId, money, now, objectId, sourceType, objectType, count);

            em.getTransaction().commit();

            result = true;
        }
        return result;
    }

    private static <U extends TransferObject> boolean recharge(EntityManager em, U dst, Long money, Long userId, Long sourceId, String sourceType, Date now, Long objectId, String objectType, Long count) {
        boolean result = false;
            if(count == null){
                count = 0l;
            }
            em.getTransaction().begin();

            dst.setAmount(dst.getAmount() + money);
            em.merge(dst);

            postTransferObject(userId, sourceId, money, now, objectId, sourceType, objectType, count);

            em.getTransaction().commit();

            result = true;

        return result;
    }

    private static <T extends TransferObject, U extends TransferObject> boolean transfer(EntityManager em, T src, U dst, Long money, Long userId, Long sourceId, String sourceType, Date now, Long objectId, String objectType, Long count) {
        boolean result = false;
        if (src.getAmount() >= money) {
            if(count == null){
                count = 0l;
            }
            em.getTransaction().begin();

            dst.setAmount(dst.getAmount() + money);
            em.merge(dst);
            src.setAmount(src.getAmount() - money);
            em.merge(src);

            postTransferObject(userId, sourceId, money, now, objectId, sourceType, objectType, count);

            em.getTransaction().commit();

            result = true;
        }
        return result;
    }


    public static void postTransferObject(Long userId, Long sourceId, Long money, Date now, Long objectId, String sourceType, String objectType, Long count) {
        Transaction transactions = new Transaction();
        transactions.setId(IdGenerator.getNewId());
        transactions.setUserId(userId);
        transactions.setCount(count);
        transactions.setMoney(money);
        transactions.setTimestamp(now);
        transactions.setSourceId(sourceId);
        transactions.setSourceType(sourceType);
        transactions.setObjectId(objectId);
        transactions.setObjectType(objectType);
        JPAEntry.genericPost(transactions);
    }

    //query
    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.getById(sessionId, id, Transaction.class, null);
            }
        }
        return result;
    }
}
