package com.baremind;

import com.baremind.data.KnowledgePoint;
import com.baremind.data.Log;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


@Path("knowledge-points")
public class KnowledgePoints {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        List<KnowledgePoint> r = JPAEntry.getList(KnowledgePoint.class, Impl.getFilters(filter));
        List<String> ids = new ArrayList<>();
        for (KnowledgePoint ri : r) {
            ids.add(ri.getId().toString());
        }
        EntityManager em = JPAEntry.getEntityManager();
        String contentCountQuery = "SELECT m.knowledgePointId, m.objectType, count(m) FROM KnowledgePointContentMap m WHERE m.knowledgePointId IN (" + Resources.join(ids) + ") GROUP BY m.knowledgePointId, m.objectType";
        Query cq = em.createQuery(contentCountQuery);
        final List<Object[]> contentStats = cq.getResultList();
        String likeCountQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'knowledge-point' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'like' GROUP BY l.objectId";
        Query lq = em.createQuery(likeCountQuery);
        final List<Object[]> likeStats = lq.getResultList();
        String readCountQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'knowledge-point' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'read' GROUP BY l.objectId";
        Query rq = em.createQuery(readCountQuery);
        final List<Object[]> readStats = rq.getResultList();
        String likedQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'knowledge-point' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'read' AND l.userId = " + JPAEntry.getLoginUser(sessionId).getId().toString() + " GROUP BY l.objectId";
        Query ldq = em.createQuery(likedQuery);
        final List<Object[]> likedStats = ldq.getResultList();

        Map<String, String> orders = new HashMap<>();
        orders.put("order", "ASC");
        return Impl.get(sessionId, filter, orders, KnowledgePoint.class, knowledgePoint -> KnowledgePoint.convertToMap(knowledgePoint, likeStats, likedStats, readStats, contentStats), null);

//        Map<String, String> orders = new HashMap<>();
//        orders.put("order", "ASC");
//        final Date now = new Date();
//        final Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
//        return Impl.get(sessionId, filter, orders, KnowledgePoint.class, knowledgePoint -> KnowledgePoint.convertToMap(knowledgePoint, JPAEntry.getLoginId(sessionId), now, yesterday), (knowledgePoint) -> {
//            boolean result = true;
//            EntityManager em = JPAEntry.getEntityManager();
//            String stats = "SELECT COUNT(m) FROM KnowledgePointContentMap m WHERE m.knowledgePointId = " + knowledgePoint.getId().toString();
//            TypedQuery<Long> q = em.createQuery(stats, Long.class);
//            Long c = q.getSingleResult();
//            if (c == 0L) {
//                result = false;
//            }
//            return result;
//        });
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        final Date now = new Date();
        final Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
        return Impl.getById(sessionId, id, KnowledgePoint.class, (knowledgePoint) -> KnowledgePoint.convertToMap(knowledgePoint, JPAEntry.getLoginUser(sessionId).getId(), now, yesterday));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, KnowledgePoint entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, KnowledgePoint newData) {
        return Impl.updateById(sessionId, id, newData, KnowledgePoint.class, (exist, knowledgePoint) -> {
            Long volumeId = knowledgePoint.getVolumeId();
            if (volumeId != null) {
                exist.setVolumeId(volumeId);
            }
            String title = knowledgePoint.getName();
            if (title != null) {
                exist.setName(title);
            }
            Date showTime = knowledgePoint.getShowTime();
            if (showTime != null) {
                exist.setShowTime(showTime);
            }
            int order = knowledgePoint.getOrder();
            if (order != 0) {
                exist.setOrder(order);
            }
            Date show = knowledgePoint.getShowTime();
            if (show != null) {
                exist.setShowTime(show);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, KnowledgePoint.class);
    }

    @PUT
    @Path("{id}/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Log log = Logs.insert(JPAEntry.getSession(sessionId).getUserId(), "knowledge-point", id, "like");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @PUT
    @Path("{id}/unlike")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Long count = Logs.deleteLike(JPAEntry.getSession(sessionId).getUserId(), "knowledge-point", id);
            result = Response.ok("{\"count\":" + count.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/like-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikeCount(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Long likeCount = Logs.getStatsCount("knowledge-point", id, "like");
            result = Response.ok("{\"count\":" + likeCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/is-self-like")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfLike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Boolean has = Logs.has(JPAEntry.getSession(sessionId).getUserId(), "knowledge-point", id, "like");
            result = Response.ok("{\"like\":" + has.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/read-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadCount(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Long readCount = Logs.getStatsCount("knowledge-point", id, "read");
            result = Response.ok("{\"count\":" + readCount.toString() + "}").build();
        }
        return result;
    }


    @GET
    @Path("{id}/contents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContentsById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            result = Response.status(404).build();
            KnowledgePoint p = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (p != null) {
                Logs.insert(JPAEntry.getSession(sessionId).getUserId(),"knowledge-point",id,"read");
                result = Response.ok(new Gson().toJson(p.getContent()), "application/json; charset=utf-8").build();
            }
        }
        return result;
    }
}

