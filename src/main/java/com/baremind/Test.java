package com.baremind;

/**
 * Created by User on 2016/11/1.
 */

import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
/*import com.sun.media.*;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;*/

@Path("util-img")
public class Test {
    public static Random random = new Random();

    public static int r(int min, int max) {
        int num = 0;
        num = random.nextInt(max - min) + min;
        return num;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVolumes(@QueryParam("filter") @DefaultValue("") String filter) throws IOException {
        //public static String main(String[] args)  {
        Response result = Response.status(401).build();
        // TODO Auto-generated method stub
        //在内存中创建一副图片
        int w = 120;
        int h = 50;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        //在图片上画一个矩形当背景
        Graphics g = img.getGraphics();
        g.setColor(new Color(r(50, 250), r(50, 250), r(50, 250)));
        g.fillRect(0, 0, w, h);

        String str = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM";
        String yzm = "";
        for (int i = 0; i < 4; i++) {
            g.setColor(new Color(r(50, 180), r(50, 180), r(50, 180)));

            g.setFont(new Font("黑体", Font.PLAIN, 40));

            char c = str.charAt(r(0, str.length()));
            // System.out.println("=======c"+c);
            g.drawString(String.valueOf(c), 10 + i * 30, r(h - 30, h));
            //System.out.println("=======1"+c);
            yzm += c;
        }
        //System.out.println("=======yzm"+yzm);
        //画随机线
        for (int i = 0; i < 25; i++) {
            g.setColor(new Color(r(50, 180), r(50, 180), r(50, 180)));
            g.drawLine(r(0, w), r(0, h), r(0, w), r(0, h));
        }
        //把内存中创建的图像输出到文件中
        //String pyshicalpath = Properties.getPropertyValue("physicalpath");
        //String pyshicalpath = Properties.getPropertyValue("testphysicalpath");
        String pyshicalpath = "/Users/fixopen/Downloads/";
        String uploadedFileLocation = pyshicalpath + "vcode.png";
        File file = new File(uploadedFileLocation);
        // FileOutputStream w = new FileOutputStream(file);
       /*try{
            com.sun.jimi.core.Jimi.putImage("image/jpeg", img, file);
        }catch(Exception e){
            e.printStackTrace();
        }*/
        ImageIO.write(img, "png", file);

//        JAI.create(img, file, "PNG", null);
        // System.out.println("图片输出完成");
//        String path = Properties.getPropertyValue("testvirtualpath") + "vcode.png";
        String path = Properties.getPropertyValue("virtualpath") + "vcode.png";

        result = Response.ok(new Gson().toJson(yzm + "/" + path)).build();
        return result;
    }
}
