package com.baremind;

import com.baremind.data.Video;
import com.baremind.data.Volume;
import com.baremind.utils.Impl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("videos")
public class Videos {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Video.class, Video::convertToMap, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Volume.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Video entity) {
        return Impl.create(sessionId, entity, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Video newData) {
        return Impl.updateById(sessionId, id, newData, Video.class, (exist, video) -> {
            String ext = video.getExt();
            if (ext != null) {
                exist.setExt(ext);
            }

            String mimeType = video.getMimeType();
            if (mimeType != null) {
                exist.setMimeType(mimeType);
            }

            Long size = video.getSize();
            if (size != null) {
                exist.setSize(size);
            }

            String name = video.getName();
            if (name != null) {
                exist.setName(name);
            }

            String storePath = video.getStorePath();
            if (storePath != null) {
                exist.setStorePath(storePath);
            }

            Long cover = video.getCover();
            if (cover != 0) {
                exist.setCover(cover);
            }

            Double bitRate = video.getBitRate();
            if (bitRate != null) {
                exist.setBitRate(bitRate);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Volume.class);
    }
}
