package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.baremind.ValidationCodes.validation;

@Path("sessions")
public class Sessions {
    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Impl.validationAdmin(sessionId);
        if (result.getStatus() == 202) {
            result = Impl.genericQueryIdProcessor(id, Session.class, null);
        }
        return result;
    }

    @GET //根据id查询
    @Path("self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.getSelf(sessionId);
    }

    private static class LoginInfo {
        private String type;
        private String info;
        private String key;
        private String deviceType;
        private String deviceNo;
        private String wechatUserId;
        private String code;

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

        String getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(String deviceType) {
            this.deviceType = deviceType;
        }

        String getDeviceNo() {
            return deviceNo;
        }

        public void setDeviceNo(String deviceNo) {
            this.deviceNo = deviceNo;
        }

        public String getWechatUserId() {
            return wechatUserId;
        }

        public void setWechatUserId(String wechatUserId) {
            this.wechatUserId = wechatUserId;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    private User insertUser(Date now, String telephone, WechatUser wechatUser) {
        User user = new User();
        user.setId(IdGenerator.getNewId());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setTelephone(telephone);
        user.setIsAdministrator(false);
        user.setSite("http://www.xiaoyuzhishi.com");
        user.setAmount(0L);
        PublicAccounts.fillUserByWechatUser(user, wechatUser);
        JPAEntry.genericPost(user);
        return user;
    }

    private void loginFailure(String ipAddr) {
        EntityManager em = JPAEntry.getNewEntityManager();
        em.getTransaction().begin();
        ValidationCode count = JPAEntry.getObject(em, ValidationCode.class, "phoneNumber", ipAddr + "-count");
        if (count != null) {
            Long currentCount = Long.parseLong(count.getValidCode()) + 1L;
            count.setValidCode(currentCount.toString());
            em.merge(count);
        } else {
            count = new ValidationCode();
            count.setId(IdGenerator.getNewId());
            count.setPhoneNumber(ipAddr + "-count");
            count.setValidCode("1");
            count.setTimestamp(new Date());
            em.persist(count);
        }
        em.getTransaction().commit();
        em.close();
    }

    private Response loginImpl(String ipAddr, LoginInfo loginInfo) {
        Response result = Response.status(404).build();
        boolean isPass = true;
        String count = ValidationCodes.get(ipAddr + "-count");
        if (count != null) {
            if (Long.parseLong(count) > 5L) {
                String code = loginInfo.getCode();
                if (code == "") {
                    isPass = false;
                    result = Response.status(410).build();
                } else {
                    String getCode = ValidationCodes.get(ipAddr);
                    if (getCode == null) {
                        isPass = false;
                        result = Response.status(408).build();
                    } else {
                        if (!getCode.equals(code)) {
                            isPass = false;
                            result = Response.status(415).build();
                        }
                        JPAEntry.genericDelete(ValidationCode.class, "phoneNumber", ipAddr);
                    }
                }
            }
        }
        if (isPass) {
            Date now = new Date();
            User user = null;
            Map<String, Object> conditions = new HashMap<>();
            switch (loginInfo.getType()) {
                case "validationCode":
                    switch (validation(loginInfo.getInfo(), loginInfo.getKey())) {
                        case 0:
                            break;
                        case 1:
                            result = Response.status(405).build();
                            break;
                        case 2:
                            user = JPAEntry.getObject(User.class, "telephone", loginInfo.getInfo());
                            break;
                        default:
                            break;
                    }
                    if (user == null) {
                        //WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "id", loginInfo.getWechatUserId());
                        //user = insertUser(now, loginInfo.getInfo(), wechatUser);
                        loginFailure(ipAddr);
                    } else {
                        JPAEntry.genericDelete(ValidationCode.class, "phoneNumber", ipAddr + "-count");
                    }
                    break;
                case "telephone":
                    conditions.put("telephone", loginInfo.getInfo());
                    conditions.put("password", loginInfo.getKey());
                    user = JPAEntry.getObject(User.class, conditions);
                    if (user == null) {
                        loginFailure(ipAddr);
                    } else {
                        JPAEntry.genericDelete(ValidationCode.class, "phoneNumber", ipAddr + "-count");
                    }
                    break;
                case "name":
                    conditions.put("loginName", loginInfo.getInfo());
                    conditions.put("password", loginInfo.getKey());
                    user = JPAEntry.getObject(User.class, conditions);
                    if (user == null) {
                        loginFailure(ipAddr);
                    } else {
                        JPAEntry.genericDelete(ValidationCode.class, "phoneNumber", ipAddr + "-count");
                    }
                    break;
            }
            Session session = resultCook(user, loginInfo.getDeviceType(), loginInfo.getDeviceNo(), now);
            if (session != null) {
                result = Response.ok(session)
                        .cookie(new NewCookie("sessionId", session.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                        .build();
            }
        }
        return result;
    }

    static Session resultCook(User user, String deviceType, String deviceNo, Date now) {
        Session session = null;
        if (user != null) {
            WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "openId", deviceNo);
            if (wechatUser != null) {
                if (wechatUser.getUserId() == null) {
                    wechatUser.setUserId(user.getId());
                    JPAEntry.genericPut(wechatUser);
                }
            }
            Map<String, Object> deviceConditions = new HashMap<>();
            deviceConditions.put("platform", deviceType);
            deviceConditions.put("platformIdentity", deviceNo);
            Device device = JPAEntry.getObject(Device.class, deviceConditions);
            if (device == null) {
                device = new Device();
                device.setId(IdGenerator.getNewId());
                device.setPlatform(deviceType);
                device.setPlatformIdentity(deviceNo);
                device.setPlatformNotificationToken("");
                device.setUserId(user.getId());
                JPAEntry.genericPost(device);
            }
            session = PublicAccounts.putSession(now, user.getId(), device.getId());
        }
        return session;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response recreate(@Context HttpServletRequest req, LoginInfo loginInfo) {
        return loginImpl(req.getRemoteAddr(), loginInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Context HttpServletRequest req, LoginInfo loginInfo) {
        return loginImpl(req.getRemoteAddr(), loginInfo);
    }

    private static class Updater implements BiConsumer<Session, Session> {
        @Override
        public void accept(Session exist, Session session) {
            String identity = session.getIdentity();
            if (identity != null) {
                exist.setIdentity(identity);
            }
            Long userId = session.getUserId();
            if (userId != null) {
                exist.setUserId(userId);
            }
            Long deviceId = session.getDeviceId();
            if (deviceId != null) {
                exist.setDeviceId(deviceId);
            }
            String ip = session.getIp();
            if (ip != null) {
                exist.setIp(ip);
            }
            Date lastOperationTime = session.getLastOperationTime();
            if (lastOperationTime != null) {
                exist.setLastOperationTime(lastOperationTime);
            }
        }
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Session newData) {
        return Impl.updateById(sessionId, id, newData, Session.class, new Updater(), null);
    }

    @PUT //根据token修改
    @Path("self")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSelf(@CookieParam("sessionId") String sessionId, Session newData) {
        return Impl.updateSelf(sessionId, newData, new Updater());
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Session.class);
    }

    @DELETE
    @Path("self")
    public Response deleteSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.deleteSelf(sessionId);
    }
}
