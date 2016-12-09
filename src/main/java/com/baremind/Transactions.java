package com.baremind;

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
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            User user = JPAEntry.getLoginUser(sessionId);
            Long userId = user.getId();
            result = Response.status(409).build(); //source error
            entity.setId(IdGenerator.getNewId());
            entity.setUserId(userId);
            Date now = new Date();
            entity.setTimestamp(now);
            EntityManager em = JPAEntry.getNewEntityManager();
            em.getTransaction().begin();
            if (entity.getCount() == null) {
                if (entity.getSourceType() == null) { //recharge
                    TransferObject dst = Users.getByTypeAndId(entity.getObjectType(), entity.getObjectId());
                    if (dst != null) {
                        dst.setAmount(dst.getAmount() + entity.getMoney());
                        em.merge(dst);
                        entity.setCount(0L);
                        em.persist(entity);
                        result = Response.ok(entity).build();
                    }
                } else { //transfer
                    result = Response.status(408).build(); //amount error
                    TransferObject src = Users.getByTypeAndId(entity.getSourceType(), entity.getSourceId());
                    if (src.getAmount() >= entity.getMoney()) {
                        em.getTransaction().begin();
                        TransferObject dst = Users.getByTypeAndId(entity.getObjectType(), entity.getObjectId());
                        dst.setAmount(dst.getAmount() + entity.getMoney());
                        em.merge(dst);
                        src.setAmount(src.getAmount() - entity.getMoney());
                        em.merge(src);
                        entity.setCount(0L);
                        em.persist(entity);
                        result = Response.ok(entity).build();
                    }
                }
            } else { //purchase
                TransferObject resource = Resources.getByTypeAndId(entity.getObjectType(), entity.getObjectId());
                TransferObject src = Users.getByTypeAndId(entity.getSourceType(), entity.getSourceId());
                result = Response.status(408).build(); //amount error
                if (resource.getAmount() == entity.getMoney() && src.getAmount() > entity.getMoney()) {
                    src.setAmount(src.getAmount() - entity.getMoney());
                    em.merge(src);
                    em.persist(entity);
                    result = Response.ok(entity).build();
                }
            }

            em.getTransaction().commit();
        }
        return result;
    }

    //query
    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Transaction.class, null);
    }
}
