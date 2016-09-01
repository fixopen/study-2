package com.baremind;

import com.baremind.data.Image;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("images")
public class Images {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createImage(@CookieParam("sessionId") String sessionId, Image image) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            image.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(image);
            result = Response.ok(image).build();
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
            List<Image> images = JPAEntry.getList(Image.class, filterObject);
            if (!images.isEmpty()) {
                result = Response.ok(new Gson().toJson(images)).build();
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
            Image image = JPAEntry.getObject(Image.class, "id", id);
            if (image != null) {
                result = Response.ok(new Gson().toJson(image)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateImage(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Image image) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Image existimage = JPAEntry.getObject(Image.class, "id", id);
            if (existimage != null) {
                String ext = image.getExt();
                if (ext != null) {
                    existimage.setName(ext);
                }

                Integer mainColor = image.getMainColor();
                if (mainColor != null) {
                    existimage.getMainColor();
                }

                String mimeType = image.getMimeType();
                if (mimeType != null) {
                    existimage.setMimeType(mimeType);
                }

                Long size = image.getSize();
                if (size != null) {
                    existimage.setSize(size);
                }

                String storePath = image.getStorePath();
                if (storePath != null) {
                    existimage.setStorePath(storePath);
                }

                String name = image.getName();
                if (name != null) {
                    existimage.setName(name);
                }
                JPAEntry.genericPut(existimage);
                result = Response.ok(existimage).build();
            }
        }
        return result;
    }
}
