package com.baremind;

import com.baremind.data.Tag;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("tags")
public class Tags {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTag(@CookieParam("sessionId") String sessionId, Tag tag) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            tag.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(tag);
            result = Response.ok(tag).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTags(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Tag> tags = JPAEntry.getList(Tag.class, filterObject);
            if (!tags.isEmpty()) {
                result = Response.ok(new Gson().toJson(tags)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTagById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Tag tag = JPAEntry.getObject(Tag.class, "id", id);
            if (tag != null) {
                result = Response.ok(new Gson().toJson(tag)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTag(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Tag tag) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Tag existtag = JPAEntry.getObject(Tag.class, "id", id);
            if (existtag != null) {
                String name = tag.getName();
                if (name != null) {
                    existtag.setName(name);
                }
                JPAEntry.genericPut(existtag);
                result = Response.ok(existtag).build();
            }
        }
        return result;
    }
}
