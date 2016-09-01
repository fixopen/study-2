package com.baremind;

import com.baremind.data.Media;
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

@Path("medias")
public class Medias {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMedia(@CookieParam("sessionId") String sessionId, Media media) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            media.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(media);
            result = Response.ok(media).build();
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
            List<Media> medias = JPAEntry.getList(Media.class, filterObject);
            if (!medias.isEmpty()) {
                result = Response.ok(new Gson().toJson(medias)).build();
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
            Media media = JPAEntry.getObject(Media.class, "id", id);
            if (media != null) {
                result = Response.ok(new Gson().toJson(media)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMedia(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Media media) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = new HashMap<>(1);
            filterObject.put("id", id);
            Media existmedia = JPAEntry.getObject(Media.class, "id", id);
            if (existmedia != null) {
                String ext = media.getExt();
                if (ext != null) {
                    existmedia.setExt(ext);
                }

                String mimeType = media.getMimeType();
                if (mimeType != null) {
                    existmedia.setMimeType(mimeType);
                }

                String name = media.getName();
                if (name != null) {
                    existmedia.setName(name);
                }

                Long size = media.getSize();
                if (size != null) {
                    existmedia.setSize(size);
                }

                String storePath = media.getStorePath();
                if (storePath != null) {
                    existmedia.setStorePath(storePath);
                }
                JPAEntry.genericPut(existmedia);
                result = Response.ok(existmedia).build();
            }
        }
        return result;
    }
}
