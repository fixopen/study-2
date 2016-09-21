package com.baremind.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CharacterEncodingFilter implements ContainerRequestFilter {
    @Context
    HttpServletRequest request;

    public static Map<String, Object> getFilters(String filter) {
        Map<String, Object> result = null;
        if (filter != "") {
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

<<<<<<< HEAD
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        request.setCharacterEncoding("UTF-8");
=======
    public static void saveFile(FileOutputStream w, InputStream servletInputStream) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        for (; ; ) {
            int receiveLength = servletInputStream.read(buffer);
            if (receiveLength == -1) {
                break;
            }
            w.write(buffer, 0, receiveLength);
        }
        w.close();
>>>>>>> 3c103f9a47921cec408eb3dd28748ee69820f375
    }
}
