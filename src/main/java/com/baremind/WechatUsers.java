package com.baremind;

import com.baremind.data.WechatUser;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("wechat-users")
public class WechatUsers {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createWechatUser(@CookieParam("sessionId") String sessionId, WechatUser wechatUser) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            wechatUser.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(wechatUser);
            result = Response.ok(wechatUser).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWechatUsers(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<WechatUser> wechatUsers = JPAEntry.getList(WechatUser.class, filterObject);
            if (!wechatUsers.isEmpty()) {
                result = Response.ok(new Gson().toJson(wechatUsers)).build();
            }
        }
        return result;
    }

    @GET//根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWechatUserById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "id", id);
            if (wechatUser != null) {
                result = Response.ok(new Gson().toJson(wechatUser)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateWechatUser(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, WechatUser wechatUser) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            WechatUser existwechatUser = JPAEntry.getObject(WechatUser.class, "id", id);
            if (existwechatUser != null) {
                String city = wechatUser.getCity();
                if (city != null) {
                    existwechatUser.setCity(city);
                }
                String country = wechatUser.getCountry();
                if (country != null) {
                    existwechatUser.setCity(city);
                }
                Date expiry = wechatUser.getExpiry();
                if (expiry != null) {
                    existwechatUser.setExpiry(expiry);
                }
                String head = wechatUser.getHead();
                if (head != null) {
                    existwechatUser.setHead(head);
                }
                String info = wechatUser.getInfo();
                if (info != null) {
                    existwechatUser.setInfo(info);
                }
                String nickname = wechatUser.getNickname();
                if (nickname != null) {
                    existwechatUser.setNickname(nickname);
                }
                String openId = wechatUser.getOpenId();
                if (openId != null) {
                    existwechatUser.setOpenId(openId);
                }
                String[] privilege = wechatUser.getPrivilege();
                if (privilege != null) {
                    existwechatUser.setPrivilege(privilege);
                }
                String province = wechatUser.getProvince();
                if (province != null) {
                    existwechatUser.setProvince(province);
                }
                String refId = wechatUser.getRefId();
                if (refId != null) {
                    existwechatUser.setRefId(refId);
                }
                String refreshToken = wechatUser.getRefreshToken();
                if (refreshToken != null) {
                    existwechatUser.setRefreshToken(refreshToken);
                }
                Long sex = wechatUser.getSex();
                if (sex != null) {
                    existwechatUser.setSex(sex);
                }
                String token = wechatUser.getToken();
                if (token != null) {
                    existwechatUser.setToken(token);
                }
                String unionId = wechatUser.getUnionId();
                if (unionId != null) {
                    existwechatUser.setUnionId(unionId);
                }
                Long userId = wechatUser.getUserId();
                if (userId != null) {
                    existwechatUser.setUnionId(unionId);
                }
                JPAEntry.genericPut(existwechatUser);
                result = Response.ok(existwechatUser).build();
            }
        }
        return result;
    }
}
