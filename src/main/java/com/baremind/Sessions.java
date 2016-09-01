package com.baremind;

import com.baremind.data.Session;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("sessions")
public class Sessions {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSession(@CookieParam("sessionId") String sessionId, Session session) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            session.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(session);
            result = Response.ok(session).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSessions(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Session> sessions = JPAEntry.getList(Session.class, filterObject);
            if (!sessions.isEmpty()) {
                result = Response.ok(new Gson().toJson(sessions)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSessionById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Session session = JPAEntry.getObject(Session.class, "id", id);
            if (session != null) {
                result = Response.ok(new Gson().toJson(session)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSession(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Session session) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Session existsession = JPAEntry.getObject(Session.class, "id", id);
            if (existsession != null) {
                String identity = session.getIdentity();
                if (identity != null) {
                    existsession.setIdentity(identity);
                }
                Long userId = session.getUserId();
                if (userId != null) {
                    existsession.setUserId(userId);
                }

                Long deviceId = session.getDeviceId();
                if (deviceId != null) {
                    existsession.setDeviceId(deviceId);
                }

                String ip = session.getIp();
                if (ip != null) {
                    existsession.setIp(ip);
                }

                Date lastOperationTime = session.getLastOperationTime();
                if (lastOperationTime != null) {
                    existsession.setLastOperationTime(lastOperationTime);
                }

                JPAEntry.genericPut(existsession);
                result = Response.ok(existsession).build();
            }
        }
        return result;
    }
}
