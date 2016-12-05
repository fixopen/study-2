package com.baremind;

import com.baremind.data.Transaction;
import com.baremind.data.User;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

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
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Date now = new Date();
            entity.setTimestamp(now);
            //src -> dst

            //src is null => recharge
            //dst.amount += money
            //post t-r

            //dst is resource => purchase
            //src.amount -= money
            //post t-r

            //dst is amount|card => transfer
            //src.amount -= money
            //dst.amount += money
            //post t-r

            result = Impl.create(sessionId, entity, null);
        }
        return result;
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
