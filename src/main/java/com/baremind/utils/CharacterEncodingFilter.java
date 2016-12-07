package com.baremind.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class CharacterEncodingFilter implements ContainerRequestFilter {
    @Context
    HttpServletRequest request;

    public static Map<String, Object> getFilters(String filter) {
        Map<String, Object> result = null;
        if (!Objects.equals(filter, "")) {
            try {
                String rawFilter = URLDecoder.decode(filter, StandardCharsets.UTF_8.toString());
                result = new Gson().fromJson(rawFilter, new TypeToken<Map<String, Object>>() {
                }.getType());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

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
