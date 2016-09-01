package com.baremind;

import com.baremind.data.Video;
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

@Path("videos")
public class Videos {
    @POST//添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVideo(@CookieParam("sessionId") String sessionId, Video video) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            video.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(video);
            result = Response.ok(video).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVideos(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Video> videos = JPAEntry.getList(Video.class, filterObject);
            if (!videos.isEmpty()) {
                result = Response.ok(new Gson().toJson(videos)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVideoById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Video video = JPAEntry.getObject(Video.class, "id", id);
            if (video != null) {
                result = Response.ok(new Gson().toJson(video)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVideo(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Video video) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = new HashMap<>(1);
            filterObject.put("id", id);
            Video existvideo = JPAEntry.getObject(Video.class, "id", id);
            if (existvideo != null) {
                String ext = video.getExt();
                if (ext != null) {
                    existvideo.setExt(ext);
                }

                String mimeType = video.getMimeType();
                if (mimeType != null) {
                    existvideo.setMimeType(mimeType);
                }

                Long size = video.getSize();
                if (size != null) {
                    existvideo.setSize(size);
                }

                String name = video.getName();
                if (name != null) {
                    existvideo.setName(name);
                }

                String storePath = video.getStorePath();
                if (storePath != null) {
                    existvideo.setStorePath(storePath);
                }

                int duration = video.getDuration();
                if (duration != 0) {
                    existvideo.setDuration(duration);
                }

                Double bitRate = video.getBitRate();
                if (bitRate != null) {
                    existvideo.setBitRate(bitRate);
                }
                JPAEntry.genericPut(existvideo);
                result = Response.ok(existvideo).build();
            }
        }
        return result;
    }
}
