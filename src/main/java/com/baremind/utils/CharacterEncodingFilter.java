package com.baremind.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CharacterEncodingFilter implements ContainerRequestFilter {
    @Context
    HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        request.setCharacterEncoding("UTF-8");
    }

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
}
