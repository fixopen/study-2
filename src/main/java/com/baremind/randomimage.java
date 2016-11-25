package com.baremind;

import com.baremind.utils.VerifyCodeUtils;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

/**
 * Created by User on 2016/11/24.
 */

@Path("randomimage")
public class randomimage extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    static final long serialVersionUID = 1L;

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String service() throws IOException {
        //生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);

        //生成图片
        int w = 200, h = 80;
        File dir = new File("F:/verifies");
        File file = new File(dir, verifyCode + ".jpg");
        VerifyCodeUtils.outputImage(w, h, file, verifyCode);
        //存入会话session

        // Cookie("rand",verifyCode.toLowerCase());
        //HttpSession session = request.getSession(true);
        //  session.setAttribute("rand", verifyCode.toLowerCase());
        return verifyCode;
    }
}
