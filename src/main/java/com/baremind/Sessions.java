package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("sessions")
public class Sessions {
    static class LoginInfo {
        private String type;
        private String info;
        private String key;
        private String deviceType;
        private String deviceNo;
        private String openId;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceNo() {
            return deviceNo;
        }

        public void setDeviceNo(String deviceNo) {
            this.deviceNo = deviceNo;
        }

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginInfo loginInfo) {
        Response result = Response.status(404).build();
        Date now = new Date();
        User user = null;
        Map<String, Object> conditions = new HashMap<>();
        switch (loginInfo.getType()) {
            case "validationCode":
                conditions.put("phoneNumber", loginInfo.getInfo());
                conditions.put("validCode", loginInfo.getKey());
                List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, conditions);

                if (!validationCodes.isEmpty()) {
                    result = Response.status(405).build();
                    Date sendTime = validationCodes.get(0).getTimestamp();
                    if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                        user = JPAEntry.getObject(User.class, "telephone", loginInfo.getInfo());
                        if (user == null) {
                            user = new User();
                            user.setId(IdGenerator.getNewId());
                            user.setCreateTime(now);
                            user.setTelephone(loginInfo.getInfo());
                            user.setUpdateTime(now);
                            //create user via openId
                            WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "openId", loginInfo.getOpenId());
                            if (wechatUser == null) {
                                PublicAccounts.WechatUserInfo userInfo = PublicAccounts.getUserInfo(loginInfo.getOpenId());
                                wechatUser = new WechatUser();
                                wechatUser.setId(IdGenerator.getNewId());
                                wechatUser.setUserId(user.getId());
                                wechatUser.setOpenId(userInfo.getOpenid());
                                wechatUser.setUnionId(userInfo.getOpenid());

                                wechatUser.setCity(userInfo.getCity());
                                wechatUser.setProvince(userInfo.getProvince());
                                wechatUser.setCountry(userInfo.getCountry());

                                //wechatUser.setPrivilege(userInfo.getPrivilege());
                                //wechatUser.setToken(user.getToken());
                                //wechatUser.setRefreshToken();
                                //wechatUser.setExpiry();

                                wechatUser.setHead(userInfo.getHeadimgurl());
                                wechatUser.setInfo(userInfo.getInfo());
                                wechatUser.setNickname(userInfo.getNickname());
                                wechatUser.setSex(userInfo.getSex());
                                wechatUser.setSubscribe(userInfo.getSubscribe());
                                wechatUser.setSubscribeTime(userInfo.getSubscribe_time());
                                wechatUser.setLanguage(userInfo.getLanguage());
                                wechatUser.setRemark(userInfo.getRemark());
                                wechatUser.setGroupId(userInfo.getGroupid());
                            }
                            user.setName(wechatUser.getNickname());
                            user.setSex(wechatUser.getSex());
                            user.setHead(wechatUser.getHead());

                            user.setCreateTime(now);
                            user.setUpdateTime(now);
                            user.setIsAdministrator(false);
                            user.setSite("http://www.xiaoyuzhishi.com");
                            user.setAmount(0.0f);

                            JPAEntry.genericPost(user);
                        }
                    }
                }
                break;
            case "telephone":
                conditions.put("telephone", loginInfo.getInfo());
                conditions.put("password", loginInfo.getKey());
                user = JPAEntry.getObject(User.class, conditions);
                if (user == null) {
                    //create user via openId
                }
                break;
            case "name":
                conditions.put("loginName", loginInfo.getInfo());
                conditions.put("password", loginInfo.getKey());
                user = JPAEntry.getObject(User.class, conditions);
                break;
        }
        if (user != null) {
            Map<String, Object> deviceConditions = new HashMap<>();
            conditions.put("platform", loginInfo.getDeviceType());
            conditions.put("platformIdentity", loginInfo.getDeviceNo());
            Device device = JPAEntry.getObject(Device.class, deviceConditions);
            if (device == null) {
                device = new Device();
                device.setId(IdGenerator.getNewId());
                device.setPlatform(loginInfo.getDeviceType());
                device.setPlatformIdentity(loginInfo.getDeviceNo());
                device.setUserId(user.getId());
                JPAEntry.genericPost(device);
            }
            Session session = PublicAccounts.putSession(now, user.getId(), device.getId());
            result = Response.ok()
                .cookie(new NewCookie("userId", user.getId().toString(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                .cookie(new NewCookie("sessionId", session.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                .build();
        }

        return result;
    }

//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response login(User user) {
//        Response result = Response.status(404).build();
//        if (user.getTelephone() != null && user.getPassword() != null) {
//            Map<String, Object> conditions = new HashMap<>();
//            conditions.put("telephone", user.getTelephone());
//            conditions.put("password", user.getPassword());
//            User existUser = JPAEntry.getObject(User.class, conditions);
//            if (existUser != null) {
//                Session s = PublicAccounts.putSession(new Date(), existUser.getId());
//                result = Response.ok()
//                    .cookie(new NewCookie("userId", existUser.getId().toString(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
//                    .cookie(new NewCookie("sessionId", s.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
//                    .build();
//            }
//        }
//        return result;
//    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSession(@CookieParam("sessionId") String sessionId, Session session) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            session.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(session);
            result = Response.ok(session).build();
        }
        return result;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response destroySession(@CookieParam("sessionId") String sessionId, Session session) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            session.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(session);
            result = Response.ok(session).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSessions(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Session> sessions = JPAEntry.getList(Session.class, filterObject);
            if (!sessions.isEmpty()) {
                result = Response.ok(new Gson().toJson(sessions)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSessionById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Session session = JPAEntry.getObject(Session.class, "id", id);
            if (session != null) {
                result = Response.ok(new Gson().toJson(session)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSession(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Session session) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Session existsession = JPAEntry.getObject(Session.class, "id", id);
            if (existsession != null) {
                String identity = session.getIdentity();
                if (identity != null) {
                    existsession.setIdentity(identity);
                }
                Long userId = session.getUserId();
                if (userId != null) {
                    existsession.setUserId(userId);
                }

                Long deviceId = session.getDeviceId();
                if (deviceId != null) {
                    existsession.setDeviceId(deviceId);
                }

                String ip = session.getIp();
                if (ip != null) {
                    existsession.setIp(ip);
                }

                Date lastOperationTime = session.getLastOperationTime();
                if (lastOperationTime != null) {
                    existsession.setLastOperationTime(lastOperationTime);
                }

                JPAEntry.genericPut(existsession);
                result = Response.ok(existsession).build();
            }
        }
        return result;
    }
}
