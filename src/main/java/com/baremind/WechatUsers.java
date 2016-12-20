package com.baremind;

import com.baremind.data.Device;
import com.baremind.data.Session;
import com.baremind.data.User;
import com.baremind.data.WechatUser;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

import static com.baremind.PublicAccounts.fillWechatUserByUserInfo;

@Path("wechat-users")
public class WechatUsers {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, WechatUser.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, WechatUser.class, null);
    }

    @GET //根据id查询
    @Path("related")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Session session = JPAEntry.getSession(sessionId);
            Device device = JPAEntry.getObject(Device.class, "id", session.getDeviceId());
            WechatUser self = JPAEntry.getObject(WechatUser.class, "openId", device.getPlatformIdentity());
            List<WechatUser> userList = JPAEntry.getList(WechatUser.class, "userId", session.getUserId());
            List<WechatUser> r = new ArrayList<>();
            r.add(self);
            for (WechatUser wu : userList) {
                if (wu.getId().longValue() != self.getId().longValue()) {
                    r.add(wu);
                }
            }
            result = Response.ok(new Gson().toJson(r)).build();
        }
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, WechatUser entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, WechatUser newData) {
        return Impl.updateById(sessionId, id, newData, WechatUser.class, (exist, wechatUser) -> {
            String city = wechatUser.getCity();
            if (city != null) {
                exist.setCity(city);
            }
            String country = wechatUser.getCountry();
            if (country != null) {
                exist.setCity(country);
            }
            Date expiry = wechatUser.getExpiry();
            if (expiry != null) {
                exist.setExpiry(expiry);
            }
            String head = wechatUser.getHead();
            if (head != null) {
                exist.setHead(head);
            }
            String info = wechatUser.getInfo();
            if (info != null) {
                exist.setInfo(info);
            }
            String nickname = wechatUser.getNickname();
            if (nickname != null) {
                exist.setNickname(nickname);
            }
            String openId = wechatUser.getOpenId();
            if (openId != null) {
                exist.setOpenId(openId);
            }
//            String[] privilege = wechatUser.getPrivilege();
//            if (privilege != null) {
//                exist.setPrivilege(privilege);
//            }
            String province = wechatUser.getProvince();
            if (province != null) {
                exist.setProvince(province);
            }
            String refreshToken = wechatUser.getRefreshToken();
            if (refreshToken != null) {
                exist.setRefreshToken(refreshToken);
            }
            Integer sex = wechatUser.getSex();
            if (sex != null) {
                exist.setSex(sex);
            }
            String token = wechatUser.getToken();
            if (token != null) {
                exist.setToken(token);
            }
            String unionId = wechatUser.getUnionId();
            if (unionId != null) {
                exist.setUnionId(unionId);
            }
            Long userIds = wechatUser.getUserId();
            if (userIds != null) {
                exist.setUserId(userIds);
            }
        }, null);
    }

    @PUT
    @Path("wechatsolution/{ids}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateByIds(@CookieParam("sessionId") String sessionId, @PathParam("ids") String ids) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            String[] id=ids.split(",");
            //split(正则表达式)
            List list = new ArrayList();
            for(int i=0;i<id.length;i++){
                WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "id", Long.parseLong(id[i]));
                wechatUser.setUserId(null);
                JPAEntry.genericPut(wechatUser);
            }
            result = Response.ok(new Gson().toJson(list)).build();
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, WechatUser.class);
    }

    @GET //根据open-id查询
    @Path("{openId}/identities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentitiesByOpenId(@PathParam("openId") String openId) {
        WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "openId", openId);
        if (wechatUser == null) {
            wechatUser = new WechatUser();
            wechatUser.setId(IdGenerator.getNewId());
            PublicAccounts.WechatUserInfo userInfo = PublicAccounts.getUserInfo(openId);
            if (userInfo != null) {
                fillWechatUserByUserInfo(wechatUser, userInfo);
                JPAEntry.genericPost(wechatUser);
            }
        }
        return Response.ok(wechatUser).build();
//        Date now = new Date();
//        Long userId;
//        String sessionId;
//        if (wechatUser == null) {
//            User user = PublicAccounts.insertUserByOpenId(now, openId);
//            userId = user.getId();
//            Session s = PublicAccounts.putSession(now, user.getId(), null); //@@deviceId is temp null
//            sessionId = s.getIdentity();
//        } else {
//            userId = wechatUser.getUserId();
//            Session s = PublicAccounts.putSession(now, userId, null); //@@deviceId is temp null
//            sessionId = s.getIdentity();
//        }
//        return Response.ok("{\"userId\":" + userId.toString() + ", \"sessionId\": \"" + sessionId + "\"}").build();
    }
}
