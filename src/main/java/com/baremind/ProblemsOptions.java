package com.baremind;

import com.baremind.data.Media;
import com.baremind.data.ProblemsOption;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/9/19.
 */
@Path("problems-options")
public class ProblemsOptions {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMedia(@CookieParam("sessionId") String sessionId, ProblemsOption problemsOption) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            problemsOption.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(problemsOption);
            result = Response.ok(problemsOption).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedias(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<ProblemsOption> problemsOptions = JPAEntry.getList(ProblemsOption.class, filterObject);
            if (!problemsOptions.isEmpty()) {
                result = Response.ok(new Gson().toJson(problemsOptions)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediaById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            ProblemsOption problemsOption = JPAEntry.getObject(ProblemsOption.class, "id", id);
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
    public Response updateMedia(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, ProblemsOption problemsOption) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = new HashMap<>(1);
            filterObject.put("id", id);
            ProblemsOption existmedia = JPAEntry.getObject(ProblemsOption.class, "id", id);
            if (existmedia != null) {
                String name = problemsOption.getName();
                if (name != null) {
                    existmedia.setName(name);
                }

                Long problemsId = problemsOption.getProblemsId();
                if (problemsId != null) {
                    existmedia.setProblemsId(problemsId);
                }

                JPAEntry.genericPut(existmedia);
                result = Response.ok(existmedia).build();
            }
        }
        return result;
    }
}