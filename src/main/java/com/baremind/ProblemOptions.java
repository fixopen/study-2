package com.baremind;

import com.baremind.data.ProblemOption;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/9/19.
 */
@Path("problem-options")
public class ProblemOptions {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCSV(@Context HttpServletRequest request, @CookieParam("userId") String userId, ProblemOption problemOption) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            problemOption.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(problemOption);
            result = Response.ok(new Gson().toJson(problemOption)).build();
        }
        return result;
    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMedia(@CookieParam("userId") String userId, ProblemOption problemsOption) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            problemsOption.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(problemsOption);
            result = Response.ok(problemsOption).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedias(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<ProblemOption> problemsOptions = JPAEntry.getList(ProblemOption.class, filterObject);
            if (!problemsOptions.isEmpty()) {
                result = Response.ok(new Gson().toJson(problemsOptions)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediaById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            ProblemOption problemsOption = JPAEntry.getObject(ProblemOption.class, "id", id);
            if (problemsOption != null) {
                result = Response.ok(new Gson().toJson(problemsOption)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMedia(@CookieParam("userId") String userId, @PathParam("id") Long id, ProblemOption problemsOption) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = new HashMap<>(1);
            filterObject.put("id", id);
            ProblemOption existmedia = JPAEntry.getObject(ProblemOption.class, "id", id);
            if (existmedia != null) {
                Long problemId = problemsOption.getProblemId();
                if (problemId != null) {
                    existmedia.setProblemId(problemId);
                }

                String name = problemsOption.getName();
                if (name != null) {
                    existmedia.setName(name);
                }

                Long imageId = problemsOption.getImageId();
                if (imageId != null) {
                    existmedia.setImageId(imageId);
                }

                Integer index = problemsOption.getIndex();
                if (index != null) {
                    existmedia.setIndex(index);
                }

                Integer order = problemsOption.getOrder();
                if (order != null) {
                    existmedia.setOrder(order);
                }

                JPAEntry.genericPut(existmedia);
                result = Response.ok(existmedia).build();
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteOption(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(ProblemOption.class, "id", id);
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }
}
