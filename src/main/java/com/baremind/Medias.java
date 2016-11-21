package com.baremind;

import com.baremind.data.Image;
import com.baremind.data.Media;
import com.baremind.data.User;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@Path("medias")
public class Medias {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Media.class, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Media.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Media entity) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.create(sessionId, entity, null);
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Media newData) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.updateById(sessionId, id, newData, Media.class, (exist, media) -> {
                    String ext = media.getExt();
                    if (ext != null) {
                        exist.setExt(ext);
                    }

                    String mimeType = media.getMimeType();
                    if (mimeType != null) {
                        exist.setMimeType(mimeType);
                    }

                    String name = media.getName();
                    if (name != null) {
                        exist.setName(name);
                    }

                    Long size = media.getSize();
                    if (size != null) {
                        exist.setSize(size);
                    }

                    String storePath = media.getStorePath();
                    if (storePath != null) {
                        exist.setStorePath(storePath);
                    }
                }, null);
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.deleteById(sessionId, id, Media.class);
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response deleteContentById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                Media m = JPAEntry.getObject(Media.class, "id", id);
                String physicalPath = Properties.getPropertyValue("physicalPath");
//                String p = physicalPath + m.getStorePath();
//                File f = new File(p);
//                boolean success = f.delete();
//                if (success) {
//                    //java.nio.file.Path
//                    result = Impl.deleteById(sessionId, id, Media.class);
//                }
                java.nio.file.Path path = FileSystems.getDefault().getPath(physicalPath, m.getStorePath());
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static Random random = new Random();

    private static int rand(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    @GET //根据条件查询
    @Path("validation-picture")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValidationPicture() throws IOException {
        int w = 120;
        int h = 50;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        g.setColor(new Color(rand(50, 250), rand(50, 250), rand(50, 250)));
        g.fillRect(0, 0, w, h);

        String s = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";
        String validationCode = "";
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(rand(50, 180), rand(50, 180), rand(50, 180)));
            g.setFont(new Font("黑体", Font.PLAIN, 40));
            char c = s.charAt(random.nextInt(s.length()));
            g.drawString(String.valueOf(c), 10 + i * 30, rand(h - 30, h));
            validationCode += c;
        }
        for (int i = 0; i < 25; i++) {
            g.setColor(new Color(rand(50, 180), rand(50, 180), rand(50, 180)));
            g.drawLine(rand(0, w), rand(0, h), rand(0, w), rand(0, h));
        }
        String physicalPath = "/Users/fixopen/IdeaProjects/study/target/study/images/";
        String fileName = physicalPath + validationCode + ".png";
        File file = new File(fileName);
        ImageIO.write(img, "png", file);
        String virtualPath = "/images/";
        String path = virtualPath + validationCode + ".png";
        return Response.ok(new Gson().toJson(path)).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadMedia(@Context HttpServletRequest request, @CookieParam("sessionId") String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            try {
                Part p = request.getPart("file");
                String contentType = p.getContentType();
                InputStream inputStream = p.getInputStream();
                long now = new Date().getTime();
                String postfix = contentType.substring(contentType.lastIndexOf("/") + 1);
                if (!Objects.equals(postfix, "jpeg") || !Objects.equals(postfix, "gif") || !Objects.equals(postfix, "ai") || !Objects.equals(postfix, "png")) {
                    String fileName = now + "." + postfix;
                    String pyshicalpath = Properties.getPropertyValue("physicalpath");
                    String uploadedFileLocation = pyshicalpath + fileName;

                    File file = new File(uploadedFileLocation);
                    FileOutputStream w = new FileOutputStream(file);
                    CharacterEncodingFilter.saveFile(w, inputStream);

                    Image image = new Image();
                    image.setId(IdGenerator.getNewId());
                    image.setExt(postfix);
                    image.setMimeType(contentType);
                    image.setName(fileName);
                    image.setSize(p.getSize());
                    String virtualPath = Properties.getPropertyValue("virtualpath") + fileName;
                    image.setStorePath(virtualPath);
                    JPAEntry.genericPost(image);

                    result = Response.ok(new Gson().toJson(image)).build();
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
