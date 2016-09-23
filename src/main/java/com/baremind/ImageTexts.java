package com.baremind;


import com.baremind.data.ImageText;
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

/**
 * Created by User on 2016/9/20.
 */
@Path("image-texts")
public class ImageTexts {

    @POST
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
                    content = new String(content.getBytes("ISO-8859-1"),"UTF-8");
                    ImageText imageText = new ImageText();
                    imageText.setId(IdGenerator.getNewId());
                    imageText.setExt(postfix);
                    imageText.setMimeType(contentType);
                    imageText.setName(fileName);
                    imageText.setSize(p.getSize());
                    String virtualPath = Properties.getPropertyValue("virtualpath") + fileName;
                    imageText.setStorePath(virtualPath);
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
    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createImage(@CookieParam("sessionId") String sessionId, ImageText imageText) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            imageText.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(imageText);
            result = Response.ok(imageText).build();
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
            List<ImageText> imageTexts = JPAEntry.getList(ImageText.class, filterObject);
            if (!imageTexts.isEmpty()) {
                result = Response.ok(new Gson().toJson(imageTexts)).build();
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
            ImageText imageText = JPAEntry.getObject(ImageText.class, "id", id);
            if (imageText != null) {
                result = Response.ok(new Gson().toJson(imageText)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateImage(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, ImageText imageText) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            ImageText existimage = JPAEntry.getObject(ImageText.class, "id", id);
            if (existimage != null) {
                String ext = imageText.getExt();
                if (ext != null) {
                    existimage.setName(ext);
                }

                Integer mainColor = imageText.getMainColor();
                if (mainColor != null) {
                    existimage.getMainColor();
                }

                String mimeType = imageText.getMimeType();
                if (mimeType != null) {
                    existimage.setMimeType(mimeType);
                }

                Long size = imageText.getSize();
                if (size != null) {
                    existimage.setSize(size);
                }

                String storePath = imageText.getStorePath();
                if (storePath != null) {
                    existimage.setStorePath(storePath);
                }

                String name = imageText.getName();
                if (name != null) {
                    existimage.setName(name);
                }

                String content = imageText.getContent();
                if (content != null) {
                    existimage.setContent(content);
                }
                JPAEntry.genericPut(existimage);
                result = Response.ok(existimage).build();
            }
        }
        return result;
    }
}
