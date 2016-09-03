package com.baremind;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }

    @POST
    public void test() {
//        WechatUsers.WechatUserInfo us = new WechatUsers.WechatUserInfo();
//
//        WechatUser user = new WechatUser();
//        user.setId(IdGenerator.getNewId());
//        user.setOpenId(us.openid);
//        user.setCity(us.city);
//        user.setCountry(us.country);
//        //user.setExpiry();
//        user.setHead(us.headimgurl);
//        user.setInfo(responseBody);
//        user.setNickname(us.nickname);
//        //user.setPrivilege();
//        user.setProvince(us.province);
//        //user.setRefId();
//        //user.setRefreshToken();
//        user.setSex(us.sex);
//        user.setSubscribeTime(us.subscribe_time);
//        user.setSubscribe(us.subscribe);
//        user.setLanguage(us.language);
//        user.setRemark(us.remark);
//        user.setHeadimgurl(us.headimgurl);
//        user.setGroupId(us.groupid);
//        //user.setToken();
//        user.setUnionId(us.unionid);
//        //user.setUserId();
//
//        User u = new User();
//        u.setId(IdGenerator.getNewId());
//        u.setHead(us.headimgurl);
//        u.setName(us.nickname);
//        //u.setLoginName(us.nickname);
//        u.setSex(us.sex);
//
//        EntityManager em = JPAEntry.getEntityManager();
//        em.getTransaction().begin();
//        em.persist(u);
//        em.persist(user);
//        em.getTransaction().commit();
    }
}
