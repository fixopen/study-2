package com.baremind;


import com.baremind.data.PinyinText;
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
@Path("pinyin-texts")
public class PinyinTexts {

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createImage(@CookieParam("sessionId") String sessionId, PinyinText pinyinText) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            pinyinText.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(pinyinText);
            result = Response.ok(pinyinText).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImages(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<PinyinText> pinyinTexts = JPAEntry.getList(PinyinText.class, filterObject);
            if (!pinyinTexts.isEmpty()) {
                result = Response.ok(new Gson().toJson(pinyinTexts)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImageById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            PinyinText pinyinText = JPAEntry.getObject(PinyinText.class, "id", id);
            if (pinyinText != null) {
                result = Response.ok(new Gson().toJson(pinyinText)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateImage(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, PinyinText pinyinText) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            PinyinText existimage = JPAEntry.getObject(PinyinText.class, "id", id);
            if (existimage != null) {


                String content = pinyinText.getContent();
                if (content != null) {
                    existimage.setContent(content);
                }

                String pinyin = pinyinText.getPinyin();
                if (pinyin != null) {
                    existimage.setPinyin(pinyin);
                }

                JPAEntry.genericPut(existimage);
                result = Response.ok(existimage).build();
            }
        }
        return result;
    }
}
