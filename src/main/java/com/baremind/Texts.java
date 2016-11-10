package com.baremind;


import com.baremind.data.Text;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/9/12.
 */
@Path("texts")
public class Texts {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createText(@CookieParam("userId") String userId, Text text) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            text.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(text);
            result = Response.ok(text).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTexts(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            //{"subjectId":1,"grade":20}
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Text> contents = JPAEntry.getList(Text.class, filterObject);
            if (!contents.isEmpty()) {
                result = Response.ok(new Gson().toJson(contents)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTextById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Text text = JPAEntry.getObject(Text.class, "id", id);
            if (text != null) {
                result = Response.ok(new Gson().toJson(text)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateText(@CookieParam("userId") String userId, @PathParam("id") Long id, Text text) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Text existvolume = JPAEntry.getObject(Text.class, "id", id);
            if (existvolume != null) {
                String content = text.getContent();
                if (content != null) {
                    existvolume.setContent(content);
                }
                JPAEntry.genericPut(existvolume);
                result = Response.ok(existvolume).build();
            }
        }
        return result;
    }
}  


