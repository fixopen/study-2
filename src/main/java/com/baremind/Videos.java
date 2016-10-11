package com.baremind;


import com.baremind.data.Video;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Path("videos")
public class Videos {

    /*@POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCSV(@Context HttpServletRequest request, @CookieParam("sessionId") String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            try {
                Part p = request.getPart("file");
                String contentType = p.getContentType();
                InputStream inputStream = p.getInputStream();
                long now = new Date().getTime();
                String postfix = contentType.substring(contentType.lastIndexOf("/") + 1);
                if (!Objects.equals(postfix, "jpg") || !Objects.equals(postfix, "jpeg") || !Objects.equals(postfix, "gif") || !Objects.equals(postfix, "ai") || !Objects.equals(postfix, "pdg")) {
                    String fileName = now + "." + postfix;
                    String pyshicalpath = Properties.getPropertyValue("physicalpath");
                    String uploadedFileLocation = pyshicalpath + fileName;
                    File file = new File(uploadedFileLocation);
                    FileOutputStream w = new FileOutputStream(file);
                    CharacterEncodingFilter.saveFile(w, inputStream);

                    String content = request.getParameter("content");
                    content = new String(content.getBytes("ISO-8859-1"), "UTF-8");
                    Video video = new Video();
                    video.setId(IdGenerator.getNewId());
                    String virtualPath = Properties.getPropertyValue("virtualpath") + fileName;
                    video.setStorePath(virtualPath);
                    video.setCover();
                    *//*imageText.setExt(postfix);
                    imageText.setMimeType(contentType);
                    imageText.setName(fileName);
                    imageText.setSize(p.getSize());*//*

                    *//*imageText.setStorePath(virtualPath);*//*
                    imageText.setContent(content);
                    JPAEntry.genericPost(imageText);

                    result = Response.ok(new Gson().toJson(imageText)).build();
                } else {
                    result = Response.status(415).build();
                    //上传图片的格式不正确
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServletException e) {

                e.printStackTrace();
            }
        }
        return result;
    }*/

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

                Long cover = video.getCover();
                if (cover != 0) {
                    existvideo.setCover(cover);
                }

               /* Long duration = video.getDuration();
                if (duration != 0) {
                    existvideo.setDuration(duration);
                }
*/
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
