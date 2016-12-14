package com.baremind;


import com.baremind.data.ImageText;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.Impl;

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
import java.util.Date;
import java.util.Objects;

/**
 * Created by User on 2016/9/20.
 */
@Path("image-texts")
public class ImageTexts {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, ImageText.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, ImageText.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, ImageText entity) {
        return Impl.create(sessionId, entity,  null,null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, ImageText newData) {
        return Impl.updateById(sessionId, id, newData, ImageText.class, (exist, imageText) -> {
            Long size = imageText.getImageId();
            if (size != null) {
                exist.setImageId(size);
            }

            String content = imageText.getContent();
            if (content != null) {
                exist.setContent(content);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, ImageText.class);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadImageText(@Context HttpServletRequest request, @CookieParam("sessionId") String sessionId) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            try {
                Part p = request.getPart("file");
                String contentType = p.getContentType();
                InputStream inputStream = p.getInputStream();
                long now = new Date().getTime();
                String postfix = contentType.substring(contentType.lastIndexOf("/") + 1);
                if (!Objects.equals(postfix, "jpg") || !Objects.equals(postfix, "jpeg") || !Objects.equals(postfix, "gif") || !Objects.equals(postfix, "ai") || !Objects.equals(postfix, "pdg")) {
                    String fileName = now + "." + postfix;
                    String pyshicalpath = Properties.getProperty("physicalpath");
                    String uploadedFileLocation = pyshicalpath + fileName;
                    File file = new File(uploadedFileLocation);
                    FileOutputStream w = new FileOutputStream(file);
                    CharacterEncodingFilter.saveFile(w, inputStream);

//                    Image image = new Image();
//                    image.setId(IdGenerator.getNewId());
//                    image.setExt(postfix);
//                    image.setMimeType(contentType);
//                    image.setName(fileName);
//                    image.setSize(p.getSize());
//                    String virtualPath = Properties.getProperty("testvirtualpath") + fileName;
//                    image.setStorePath(virtualPath);
//
//                    ImageText imageText = new ImageText();
//                    imageText.setId(IdGenerator.getNewId());
//                    imageText.setImageId(image.getId());
//                    String content = request.getParameter("content");
//                    content = new String(content.getBytes("ISO-8859-1"), "UTF-8");
//                    ImageText imageText = new ImageText();
//                    imageText.setId(IdGenerator.getNewId());
//                    imageText.setExt(postfix);
//                    imageText.setMimeType(contentType);
//                    imageText.setName(fileName);
//                    imageText.setSize(p.getSize());
//                    String virtualPath = Properties.getPropertyValue("testvirtualpath") + fileName;
//                    imageText.setStorePath(virtualPath);
//                    imageText.setContent(content);
//                    JPAEntry.genericPost(imageText);

//                    result = Response.ok(new Gson().toJson(imageText)).build();
                } else {
                    result = Response.status(415).build();
                    //上传图片的格式不正确
                }
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
