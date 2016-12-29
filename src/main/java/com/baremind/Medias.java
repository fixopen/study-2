package com.baremind;

import com.baremind.data.Image;
import com.baremind.data.Media;

import com.baremind.data.ValidationCode;
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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
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
        return Impl.get(sessionId, filter, null, Media.class, null, null);
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
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Media newData) {
        return Impl.updateById(sessionId, id, newData, Media.class, (exist, media) -> {
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

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Media.class);
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response deleteContentById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
                Media m = JPAEntry.getObject(Media.class, "id", id);
                String physicalPath = Properties.getProperty("physicalPath");
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
        return result;
    }

    private static Random random = new Random();

    static final String VERIFY_CODES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    private static int rand(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    @GET
    @Path("validation-picture")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getValidationPicture(@Context HttpServletRequest request) throws IOException {
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
//            g.drawLine(rand(0, w), rand(0, h), rand(0, w), rand(0, h));
        }
        ValidationCode code = new ValidationCode();
        code.setId(IdGenerator.getNewId());
        code.setPhoneNumber(request.getRemoteAddr());
        code.setValidCode(validationCode);
        code.setTimestamp(new Date());
        JPAEntry.genericPost(code);

        long now = new Date().getTime();
        String virtualDirectory = Properties.getProperty("realDirectory");
        String fileName = virtualDirectory + now + ".png";
        File file = new File(fileName);
        ImageIO.write(img, "png", file);
        String realDirectory = Properties.getProperty("virtualDirectory");
        String path = realDirectory + now + ".png";
        return Response.ok(new Gson().toJson(path)).build();
    }

    public static void outputImage(int width, int height, OutputStream os, String code) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, width, height);

        //绘制干扰线
//        g2.setColor(getRandColor(160, 200));// 设置线条的颜色
//        for (int i = 0; i < 20; i++) {
//            int x0 = random.nextInt(width - 1);
//            int y0 = random.nextInt(height - 1);
//            int x1 = random.nextInt(6) + 1;
//            int y1 = random.nextInt(12) + 1;
//            g2.drawLine(x0, y0, x0 + x1 + 40, y0 + y1 + 20);
//        }

        //添加噪点
//        float yawpRate = 0.05f;// 噪声率
//        int area = (int) (yawpRate * width * height);
//        for (int i = 0; i < area; i++) {
//            int x = random.nextInt(width);
//            int y = random.nextInt(height);
//            int rgb = getRandomIntColor();
//            image.setRGB(x, y, rgb);
//        }

//        Color c = getRandColor(200, 250);
//        shear(g2, width, height, c);// 使图片扭曲

//        g2.setColor(getRandColor(100, 160));
        int fontSize = height - 4;
        Font font = new Font("Algerian", Font.ITALIC, fontSize);
        g2.setFont(font);
        int verifySize = code.length();
        char[] chars = code.toCharArray();
        for (int i = 0; i < verifySize; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1), (width / verifySize) * i + fontSize / 2, height / 2);
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, ((width - 10) / verifySize) * i + 5, height / 2 + fontSize / 2 - 10);
        }

        g2.dispose();
        ImageIO.write(image, "jpg", os);
    }

    private static Color getRandColor(int start, int end) {
        if (start > 255)
            start = 255;
        if (end > 255)
            end = 255;
        int r = start + random.nextInt(end - start);
        int g = start + random.nextInt(end - start);
        int b = start + random.nextInt(end - start);
        return new Color(r, g, b);
    }

//    private static int getRandomIntColor() {
//        int[] rgb = getRandomRgb();
//        int color = 0;
//        for (int c : rgb) {
//            color = color << 8;
//            color = color | c;
//        }
//        return color;
//    }

//    private static int[] getRandomRgb() {
//        int[] rgb = new int[3];
//        for (int i = 0; i < 3; i++) {
//            rgb[i] = random.nextInt(255);
//        }
//        return rgb;
//    }

//    private static void shear(Graphics g, int width, int height, Color color) {
//        shearX(g, width, height, color);
//        shearY(g, width, height, color);
//    }

//    private static void shearX(Graphics g, int width, int height, Color color) {
//        int period = random.nextInt(2);
//
//        boolean borderGap = true;
//        int frames = 1;
//        int phase = random.nextInt(2);
//
//        for (int i = 0; i < height; i++) {
//            double d = (double) (period >> 1)
//                * Math.sin((double) i / (double) period
//                + (6.2831853071795862D * (double) phase)
//                / (double) frames);
//            g.copyArea(0, i, width, 1, (int) d, 0);
//            if (borderGap) {
//                g.setColor(color);
//                g.drawLine((int) d, i, 0, i);
//                g.drawLine((int) d + width, i, width, i);
//            }
//        }
//    }

//    private static void shearY(Graphics g, int width, int height, Color color) {
//        int period = random.nextInt(40) + 10; // 50;
//
//        boolean borderGap = true;
//        int frames = 20;
//        int phase = 7;
//        for (int i = 0; i < width; i++) {
//            double d = (double) (period >> 1)
//                * Math.sin((double) i / (double) period
//                + (6.2831853071795862D * (double) phase)
//                / (double) frames);
//            g.copyArea(i, 0, 1, height, 0, (int) d);
//            if (borderGap) {
//                g.setColor(color);
//                g.drawLine(i, (int) d, i, 0);
//                g.drawLine(i, (int) d + height, i, height);
//            }
//        }
//    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadMedia(@Context HttpServletRequest request, @CookieParam("sessionId") String sessionId) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            try {
                Part p = request.getPart("file");
                String contentType = p.getContentType();
                InputStream inputStream = p.getInputStream();
                long now = new Date().getTime();
                String postfix = contentType.substring(contentType.lastIndexOf("/") + 1);
                if (!Objects.equals(postfix, "jpeg") || !Objects.equals(postfix, "gif") || !Objects.equals(postfix, "ai") || !Objects.equals(postfix, "png")) {
                    String fileName = now + "." + postfix;
                    String physicalPath = Properties.getProperty("physicalpath");
                    String uploadedFileLocation = physicalPath + fileName;

                    File file = new File(uploadedFileLocation);
                    FileOutputStream w = new FileOutputStream(file);
                    CharacterEncodingFilter.saveFile(w, inputStream);

                    Image image = new Image();
                    image.setId(IdGenerator.getNewId());
                    image.setExt(postfix);
                    image.setMimeType(contentType);
                    image.setName(fileName);
                    image.setSize(p.getSize());
                    String virtualPath = Properties.getProperty("virtualpath") + fileName;
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
