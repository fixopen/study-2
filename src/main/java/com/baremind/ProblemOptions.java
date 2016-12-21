package com.baremind;

import com.baremind.data.Image;
import com.baremind.data.ProblemOption;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2016/9/19.
 */
@Path("problem-options")
public class ProblemOptions {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        List<ProblemOption> r = JPAEntry.getList(ProblemOption.class, Impl.getFilters(filter));
        List<String> imageIds = new ArrayList<>();
        for (ProblemOption ri : r) {
            if (ri.getImageId() != null) {
                imageIds.add(ri.getImageId().toString());
            }
        }
        EntityManager em = JPAEntry.getEntityManager();
        List<Image> optionImages = Resources.getList(em, imageIds, Image.class);
        return Impl.get(sessionId, filter, null, ProblemOption.class, option -> ProblemOption.convertToMap(option, optionImages), null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, ProblemOption.class, ProblemOption::convertToMap);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, ProblemOption entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, ProblemOption newData) {
        return Impl.updateById(sessionId, id, newData, ProblemOption.class, (exist, problemsOption) -> {
            Long problemId = problemsOption.getProblemId();
            if (problemId != null) {
                exist.setProblemId(problemId);
            }

            String name = problemsOption.getName();
            if (name != null) {
                exist.setName(name);
            }

            Long imageId = problemsOption.getImageId();
            if (imageId != null) {
                exist.setImageId(imageId);
            }

            Integer index = problemsOption.getIndex();
            if (index != null) {
                exist.setIndex(index);
            }

            Integer order = problemsOption.getOrder();
            if (order != null) {
                exist.setOrder(order);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, ProblemOption.class);
    }
}
