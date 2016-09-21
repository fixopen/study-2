package com.baremind;

import com.baremind.data.Quote;
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
 * Created by User on 2016/9/20.
 */
@Path("quotes")
public class Quotes {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComment(@CookieParam("sessionId") String sessionId, Quote quote) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            quote.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(quote);
            result = Response.ok(quote).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubjects(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            //{"subjectId":1,"grade":20}
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Quote> quotes = JPAEntry.getList(Quote.class, filterObject);
            if (!quotes.isEmpty()) {
                result = Response.ok(new Gson().toJson(quotes)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVolumeById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Quote quote = JPAEntry.getObject(Quote.class, "id", id);
            if (quote != null) {
                result = Response.ok(new Gson().toJson(quote)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVolume(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Quote quote) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Quote existvolume = JPAEntry.getObject(Quote.class, "id", id);
            if (existvolume != null) {
                String content = quote.getContent();
                if (content != null) {
                    existvolume.setContent(content);
                }
                String source = quote.getSource();
                if (source != null) {
                    existvolume.setSource(source);
                }
                JPAEntry.genericPut(existvolume);
                result = Response.ok(existvolume).build();
            }
        }
        return result;
    }
}
