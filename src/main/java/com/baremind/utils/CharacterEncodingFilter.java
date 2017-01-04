package com.baremind.utils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.io.*;

public class CharacterEncodingFilter implements ContainerRequestFilter {
    @Context
    HttpServletRequest request;

    public static void saveFile(FileOutputStream w, InputStream servletInputStream) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        for (; ; ) {
            int receiveLength = servletInputStream.read(buffer);
            if (receiveLength == -1) {
                break;
            }
            w.write(buffer, 0, receiveLength);
        }
        servletInputStream.close();
        w.close();
    }

    public static void writeToFile(byte[] data, String uploadedFileLocation) {
        try {
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        request.setCharacterEncoding("UTF-8");
    }
}
