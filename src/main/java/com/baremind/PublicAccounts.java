package com.baremind;

import com.baremind.algorithm.Securities;
import com.baremind.data.Session;
import com.baremind.data.User;
import com.baremind.data.WechatUser;
import com.baremind.utils.Hex;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by fixopen on 2/9/2016.
 */
@Path("public-account")
public class PublicAccounts {
    private static String hostname = "https://api.weixin.qq.com";
    private static String accessToken = "";
    private static String appID = "wx60171bc59d149903";
    private static String secret = "b632624815e77a67567fb31543f0a15f";
    //private static String appID = "wx92dec5e98645bd1d";
    //private static String secret = "d3b30c3ae79c322bc54c93d0ff75210b";
    private static String token = "xiaoyuzhishi20160928";
    //private static String token = "xiaoyuzhishi20160907";

    private static class AccessToken {
        private String access_token;
        private int expires_in;
    }

    //获取接口调用凭证
    private static void getTokenFromWechatPlatform() {
        //http请求方式: GET
        //https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/token")
                .queryParam("grant_type", "client_credential")
                .queryParam("appid", appID)
                .queryParam("secret", secret)
                .request().get();
        String responseBody = response.readEntity(String.class);
        if (responseBody.contains("access_token")) {
            //{"access_token":"ACCESS_TOKEN","expires_in":7200}
            AccessToken t = new Gson().fromJson(responseBody, AccessToken.class);
            accessToken = t.access_token;
        }
    }

    private static void prepare() {
        if (accessToken.equals("")) {
            getTokenFromWechatPlatform();
        }
    }

    private static class CustomMenu {
        static class MenuItem {
            private String type;
            private String name;
            private String key;
            private String url;
            private MenuItem[] sub_button;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public MenuItem[] getSub_button() {
                return sub_button;
            }

            public void setSub_button(MenuItem[] sub_button) {
                this.sub_button = sub_button;
            }
        }

        private MenuItem[] button;

        public MenuItem[] getButton() {
            return button;
        }

        public void setButton(MenuItem[] button) {
            this.button = button;
        }
    }

    private static class GenericResult {
        private int errcode;
        private String errmsg;

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }
    }

    @POST
    @Path("custom-menu")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomMenu(byte[] menu) {
        Response result = Response.status(500).build();
        prepare();
        //POST https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN
        try {
            CustomMenu t = new Gson().fromJson(new String(menu, "UTF-8"), CustomMenu.class);
            Client client = ClientBuilder.newClient();
            Entity<CustomMenu> em = Entity.json(t);
            Response response = client.target(hostname)
                    .path("/cgi-bin/menu/create")
                    .queryParam("access_token", accessToken)
                    .request(MediaType.APPLICATION_JSON).post(em);
            GenericResult r = response.readEntity(GenericResult.class);
            if (r.errcode == 0) {
                result = Response.ok(r).build();
            } else {
                result = Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String sign(String[] origin) {
        String sign = "";
        Arrays.sort(origin);
        String v = "";
        for (String anOrigin : origin) {
            v += anOrigin;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(v.getBytes("utf-8"));
            sign = Hex.bytesToHex(digest);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sign;
    }

    @GET
    @Path("config/{url}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfig(@PathParam("url") String url) {
        Response result = Response.status(500).build();
        String ticket = Properties.getProperty("ticket");
        String timestamp = Long.toString(new Date().getTime());
        String nonceStr = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            try {
                byte[] digest = md.digest(new Date().toString().getBytes("utf-8"));
                nonceStr = Hex.bytesToHex(digest);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String[] params = {ticket, timestamp, nonceStr, url};
        String sign = sign(params);
        Map<String, Object> r = new HashMap<>();
        r.put("appId", appID);
        r.put("timestamp", timestamp);
        r.put(nonceStr, nonceStr);
        r.put("signature", sign);
        result = Response.ok(r).build();
        return result;
    }

    private static class Ticket {
        private String ticket;
    }

    @GET
    @Path("ticket")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshTicket() {
        //https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi
        prepare();
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("cgi-bin/ticket/getticket")
                .queryParam("access_token", accessToken)
                .queryParam("type", "jsapi")
                .request().get();
        String body = response.readEntity(String.class);
        if (body.contains("ticket")) {
            //{"access_token":"ACCESS_TOKEN","expires_in":7200}
            Ticket ticket = new Gson().fromJson(body, Ticket.class);
            Properties.setProperty("ticket", ticket.ticket);
        }
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response validToken(@Context HttpServletRequest request) {
        Response result = Response.status(400).build();
        Map<String, String> args = new HashMap<>();
        String queryString = request.getQueryString();
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            args.put(pair[0], pair[1]);
        }
        String[] origin = {args.get("timestamp"), args.get("nonce"), token};
        Arrays.sort(origin);
        String v = origin[0] + origin[1] + origin[2];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(v.getBytes("utf-8"));
            String sign = Hex.bytesToHex(digest);
            if (sign.equals(args.get("signature"))) {
                result = Response.ok(args.get("echostr")).build();
            }
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            result = Response.status(500).build();
        }
        return result;
    }

    private static class WechatPush {
        private String ToUserName;
        private String FromUserName;
        private String CreateTime;
        private String MsgType;
        private Map<String, String> Infos = new HashMap<>();

        String getToUserName() {
            return ToUserName;
        }

        void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        String getFromUserName() {
            return FromUserName;
        }

        void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String createTime) {
            CreateTime = createTime;
        }

        String getMsgType() {
            return MsgType;
        }

        void setMsgType(String msgType) {
            MsgType = msgType;
        }

        Map<String, String> getInfos() {
            return Infos;
        }

        public void setInfos(Map<String, String> infos) {
            Infos = infos;
        }
    }

    private static class WechatXmlHandler extends DefaultHandler {
        private WechatPush data;
        private String currentTag;
        private String currentData = "";

        WechatXmlHandler(WechatPush p) {
            super();
            data = p;
        }

        public void startDocument() {
        }


        public void endDocument() {
        }


        public void startElement(String uri, String name, String qName, Attributes atts) {
            currentTag = name;
            currentData = "";
        }


        public void endElement(String uri, String name, String qName) {
            currentTag = "";
            currentData = "";
        }


        public void characters(char ch[], int start, int length) {
            currentData += new String(ch, start, length);
            switch (currentTag) {
                case "ToUserName":
                    data.setToUserName(currentData);
                    break;
                case "FromUserName":
                    data.setFromUserName(currentData);
                    break;
                case "CreateTime":
                    data.setCreateTime(currentData);
                    break;
                case "MsgType":
                    data.setMsgType(currentData);
                    break;
                default:
                    if (!currentTag.equals("")) {
                        data.Infos.put(currentTag, currentData);
                    }
                    break;
            }
        }

    }

    @POST
    public Response processAll(@Context HttpServletRequest request, byte[] contents) {
        Response result = Response.status(400).build();
        Map<String, String> args = new HashMap<>();
        String queryString = request.getQueryString();
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            args.put(pair[0], pair[1]);
        }
        String[] origin = {args.get("timestamp"), args.get("nonce"), token};
        Arrays.sort(origin);
        String v = origin[0] + origin[1] + origin[2];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            try {
                byte[] digest = md.digest(v.getBytes("utf-8"));
                String sign = Hex.bytesToHex(digest);
                if (sign.equals(args.get("signature"))) {
                    //String contentType = request.getContentType();
                    //String accept = request.getHeader("Accept");
                    WechatPush p = new WechatPush();
                    try {
                        XMLReader xr = XMLReaderFactory.createXMLReader();
                        WechatXmlHandler handler = new WechatXmlHandler(p);
                        xr.setContentHandler(handler);
                        xr.setErrorHandler(handler);
                        try {
                            xr.parse(new InputSource(new ByteArrayInputStream(contents)));
                            switch (p.getMsgType()) {
                                case "event":
                                    String event = p.getInfos().get("Event");
                                    switch (event) {
                                        case "CLICK":
//                                            String eventKey = p.getInfos().get("EventKey");
//                                            switch (eventKey) {
//                                                case "ID_USER":
//                                                    //点击菜单拉取消息时的事件推送
//                                                    result = userClickMine(p);
//                                                    break;
//                                                case "ID_ACTIVE":
//                                                    result = activeCard(p);
//                                                    break;
//                                                case "ID_DIRECT_PLAY":
//                                                    result = directPlay(p);
//                                                    break;
//                                                default:
//                                                    result = Response.ok().build();
//                                                    break;
//                                            }
                                            break;
                                        case "subscribe":
                                            result = follow(p);
                                            break;
                                        default:
                                            result = Response.ok().build();
                                            break;
                                    }
                                    break;
                                case "text":
                                    result = Response.ok().build();
                                    break;
                                case "image":
                                    result = Response.ok().build();
                                    break;
                                case "voice":
                                    result = Response.ok().build();
                                    break;
                                case "video":
                                    result = Response.ok().build();
                                    break;
                                case "shortvideo":
                                    result = Response.ok().build();
                                    break;
                                case "location":
                                    result = Response.ok().build();
                                    break;
                                case "link":
                                    result = Response.ok().build();
                                    break;
                                case "music":
                                    result = Response.ok().build();
                                    break;
                                default:
                                    result = Response.ok().build();
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    static class WechatUserInfo {
        private int subscribe;
        private String openid;
        private String nickname;
        private Integer sex;
        private String language;
        private String city;
        private String province;
        private String country;
        private String headimgurl;
        private int subscribe_time;
        private String unionid;
        private String remark;
        private int groupid;
        private String info;

        int getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(int subscribe) {
            this.subscribe = subscribe;
        }

        String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public Integer getSex() {
            return sex;
        }

        public void setSex(Integer sex) {
            this.sex = sex;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }

        int getSubscribe_time() {
            return subscribe_time;
        }

        public void setSubscribe_time(int subscribe_time) {
            this.subscribe_time = subscribe_time;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        int getGroupid() {
            return groupid;
        }

        public void setGroupid(int groupid) {
            this.groupid = groupid;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    static WechatUserInfo getUserInfo(String openId) {
        // http请求方式: GET（请使用https协议）
        //https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
        prepare();
        WechatUserInfo result = null;
        boolean isContinue = true;
        int repeatCount = 0;
        while (isContinue) {
            ++repeatCount;
            if (repeatCount > 5) {
                break;
            }
            Client client = ClientBuilder.newClient();
            Response response = client.target(hostname)
                    .path("/cgi-bin/user/info")
                    .queryParam("access_token", accessToken)
                    .queryParam("openid", openId)
                    .queryParam("lang", "zh_CN")
                    .request().get();
            String responseBody = response.readEntity(String.class);
            if (responseBody.contains("openid")) {
                //{"access_token":"ACCESS_TOKEN","expires_in":7200}
                result = new Gson().fromJson(responseBody, WechatUserInfo.class);
                result.setInfo(responseBody);
                isContinue = false;
            } else {
                if (responseBody.contains("errcode")) {
                    GenericResult rc = new Gson().fromJson(responseBody, GenericResult.class);
                    switch (rc.errcode) {
                        case 40014:
                            getTokenFromWechatPlatform();
                            isContinue = true;
                            break;
                        default:
                            Logs.insert(0L, "wechatError", 100L, responseBody);
                            isContinue = false;
                            break;
                    }
                }
            }
        }
        return result;
    }

    private static Map<String, Object> getTokenByCode(String code) {
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
            .path("/sns/oauth2/access_token")
            .queryParam("appid", appID)
            .queryParam("secret", secret)
            .queryParam("code", code)
            .queryParam("grant_type", "authorization_code")
            .request().get();
        String responseBody = response.readEntity(String.class);
        return new Gson().fromJson(responseBody, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    private static WechatUserInfo getUserInfo(String token, String openId) {
        // http请求方式: GET（请使用https协议）
        //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
        WechatUserInfo result = null;
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/sns/userinfo")
                .queryParam("access_token", token)
                .queryParam("openid", openId)
                .queryParam("lang", "zh_CN")
                .request().get();
        String responseBody = response.readEntity(String.class);
        if (responseBody.contains("openid")) {
            //{"access_token":"ACCESS_TOKEN","expires_in":7200}
            result = new Gson().fromJson(responseBody, WechatUserInfo.class);
            result.setInfo(responseBody);
        } else {
            Logs.insert(144L, "log", 144L, "errorInfo = " + responseBody);
        }
        return result;
    }

    static void fillWechatUserByUserInfo(WechatUser wechatUser, WechatUserInfo userInfo) {
        wechatUser.setOpenId(userInfo.getOpenid());
        wechatUser.setUnionId(userInfo.getUnionid());

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

    static void fillWechatUserByTokenInfo(WechatUser wechatUser, Map<String, Object> tokenInfo) {
        for (String key : tokenInfo.keySet()) {
            switch (key) {
                case "access_token":
                    wechatUser.setToken((String) tokenInfo.get(key));
                    break;
                case "expires_in":
                    Date expiry = new Date(new Date().getTime() + ((Double) tokenInfo.get(key)).longValue());
                    wechatUser.setExpiry(expiry);
                    break;
                case "refresh_token":
                    wechatUser.setRefreshToken((String) tokenInfo.get(key));
                    break;
                case "scope":
                    break;
                case "openid":
                    wechatUser.setOpenId((String) tokenInfo.get(key));
                    break;
                case "unionid":
                    wechatUser.setUnionId((String) tokenInfo.get(key));
                    break;
                default:
                    break;
            }
        }
    }

    static void fillUserByWechatUser(User user, WechatUser wechatUser) {
        user.setName(wechatUser.getNickname());
        user.setSex(wechatUser.getSex());
        user.setHead(wechatUser.getHead());

        wechatUser.setUserId(user.getId());
    }

//    private static User fillUserByUserInfo(Date now, WechatUserInfo userInfo) {
//        long userId = IdGenerator.getNewId();
//        User user = new User();
//        user.setId(userId);
//        if (userInfo.headimgurl == null) {
//            user.setHead("");
//        } else {
//            user.setHead(userInfo.headimgurl);
//        }
//        if (userInfo.nickname == null) {
//            user.setName("");
//        } else {
//            user.setName(userInfo.nickname);
//        }
//        //u.setLoginName(us.nickname);
//        if (userInfo.sex == null) {
//            user.setSex(0);
//        } else {
//            user.setSex(userInfo.sex);
//        }
//        user.setCreateTime(now);
//        user.setUpdateTime(now);
//        user.setIsAdministrator(false);
//        user.setSite("http://www.xiaoyuzhishi.com");
//        user.setAmount(0.0f);
//        return user;
//    }

//    private static WechatUser fillWechatUserByUserInfo(Long userId, WechatUserInfo userInfo) {
//        WechatUser wechatUser = new WechatUser();
//        wechatUser.setId(IdGenerator.getNewId());
//        //Logs.insert(0l, "debug", 12l, wechatUser.getId().toString());
//        wechatUser.setUserId(userId);
//        wechatUser.setOpenId(userInfo.openid);
//        wechatUser.setCity(userInfo.city);
//        wechatUser.setCountry(userInfo.country);
//        //user.setExpiry();
//        wechatUser.setHead(userInfo.headimgurl);
//        wechatUser.setInfo(userInfo.getInfo());
//        wechatUser.setNickname(userInfo.nickname);
//        //user.setPrivilege();
//        wechatUser.setProvince(userInfo.province);
//        //user.setRefId();
//        //user.setRefreshToken();
//        //user.setSex(p.Infos.get(sex));
//        wechatUser.setSex(userInfo.sex);
//        wechatUser.setSubscribe(userInfo.subscribe);
//        wechatUser.setSubscribeTime(userInfo.subscribe_time);
//        wechatUser.setLanguage(userInfo.language);
//        wechatUser.setRemark(userInfo.remark);
//        wechatUser.setGroupId(userInfo.groupid);
//        //user.setToken();
//        wechatUser.setUnionId(userInfo.unionid);
//        return wechatUser;
//    }

//    static User insertUserByOpenId(Date now, String openId) {
//        WechatUserInfo userInfo = getUserInfo(openId);
//        User user = null;
//        if (userInfo != null) {
//            user = fillUserByUserInfo(now, userInfo);
//            WechatUser wechatUser = fillWechatUserByUserInfo(user.getId(), userInfo);
//
//            EntityManager em = JPAEntry.getNewEntityManager();
//            em.getTransaction().begin();
//            em.persist(wechatUser);
//            em.persist(user);
//            em.getTransaction().commit();
//            em.close();
//        }
//        return user;
//    }

    static Session putSession(Date now, Long userId, Long deviceId) {
        String nowString = now.toString() + Long.toString(now.getTime());
        byte[] sessionIdentity = Securities.digestor.digest(nowString);
        String sessionString = Hex.bytesToHex(sessionIdentity);

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("userId", userId);
        conditions.put("deviceId", deviceId);
        Session s = JPAEntry.getObject(Session.class, conditions);
        if (s == null) {
            s = new Session();
            Long sessionId = IdGenerator.getNewId();
            s.setId(sessionId);
            s.setUserId(userId);
            s.setDeviceId(deviceId);
            s.setIdentity(sessionString);
            s.setLastOperationTime(now);
            JPAEntry.genericPost(s);
        } else {
            s.setIdentity(sessionString);
            s.setLastOperationTime(now);
            JPAEntry.genericPut(s);
        }
        return s;
    }

    static Session getSession(String sessionId) {
        return JPAEntry.getObject(Session.class, "identity", sessionId);
    }

//    private static String generate(WechatPush p, String content) {
//        String openId = p.getFromUserName();
//        long secondCount = new Date().getTime() / 1000;
//        String currentEpochTime = Long.toString(secondCount);
//        String result = "<xml>\n" +
//                "   <ToUserName><![CDATA[" + openId + "]]></ToUserName>\n" +
//                "   <FromUserName><![CDATA[" + p.getToUserName() + "]]></FromUserName>\n" +
//                "   <CreateTime>" + currentEpochTime + "</CreateTime>\n" +
//                "   <MsgType><![CDATA[text]]></MsgType>\n" +
//                "   <Content><![CDATA[" + content + "]]></Content>\n" +
//                "</xml>";
//        return result;
//    }

//    private static String processAndGenerate(WechatPush p, String title, String content, String baseUrl) {
//        String openId = p.getFromUserName();
//        WechatUser dbWechatUser = JPAEntry.getObject(WechatUser.class, "openId", openId);
//        Date now = new Date();
//        Long userId;
//        if (dbWechatUser == null) {
//            User user = insertUserByOpenId(now, openId);
//            userId = user.getId();
//        } else {
//            userId = dbWechatUser.getUserId();
//        }
//
//        Session s = putSession(now, userId, 0L); //@@deviceId is temp zero
//        long secondCount = now.getTime() / 1000;
//        String currentEpochTime = Long.toString(secondCount);
//
//        return "<xml>\n" +
//                "   <ToUserName><![CDATA[" + openId + "]]></ToUserName>\n" +
//                "   <FromUserName><![CDATA[" + p.getToUserName() + "]]></FromUserName>\n" +
//                "   <CreateTime>" + currentEpochTime + "</CreateTime>\n" +
//                "   <MsgType><![CDATA[news]]></MsgType>\n" +
//                "   <ArticleCount>1</ArticleCount>\n" +
//                "   <Articles>\n" +
//                "       <item>\n" +
//                "           <Title><![CDATA[" + title + "]]></Title> \n" +
//                "           <Description><![CDATA[" + content + "]]></Description>\n" +
//                "           <Url><![CDATA[" + baseUrl + "?openid=" + openId + "]]></Url>\n" +
//                "       </item>\n" +
//                "   </Articles>\n" +
//                "</xml>";
//    }

//    private Response activeCard(WechatPush p) {
//        String baseUrl = "http://www.xiaoyuzhishi.com/user/active-card.html";
//        String result = processAndGenerate(p, "激活新卡", "点击链接将进入卡激活页面", baseUrl);
//        return Response.ok(result).build();
//    }

//    private Response userClickMine(WechatPush p) {
//        String result = generate(p, "系统不断升级中,请稍晚几天再激活。不影响学生上直播课。请关注微信号的公告提示。");
//        return Response.ok(result).build();
//    }

//    private Response directPlay(WechatPush p) {
//        String baseUrl = "http://www.xiaoyuzhishi.com/content/direct-play.html";
//        String result = processAndGenerate(p, "欢迎", "点击链接将进入卡激活页面", baseUrl);
//        return Response.ok(result).build();
//    }

//    private static void appendTokenInfo(WechatUser dbWechatUser, WechatUser tokenInfo) {
//        Date expiry = tokenInfo.getExpiry();
//        if (expiry != null) {
//            dbWechatUser.setExpiry(expiry);
//        }
//        String head = tokenInfo.getRefreshToken();
//        if (head != null) {
//            dbWechatUser.setRefreshToken(head);
//        }
//        String token = tokenInfo.getToken();
//        if (token != null) {
//            dbWechatUser.setToken(token);
//        }
//        String unionId = tokenInfo.getUnionId();
//        if (unionId != null) {
//            dbWechatUser.setUnionId(unionId);
//        }
//    }

//    private static User getOrInsertUserByTokenInfo(Date now, WechatUser tokenInfo) {
//        User user = null;
//        WechatUser dbWechatUser = JPAEntry.getObject(WechatUser.class, "openId", tokenInfo.getOpenId());
//        if (dbWechatUser == null) {
//            WechatUserInfo userInfo = getUserInfo(tokenInfo.getToken(), tokenInfo.getOpenId());
//            if (userInfo != null) {
//                user = fillUserByUserInfo(now, userInfo);
//                dbWechatUser = fillWechatUserByUserInfo(user.getId(), userInfo);
//                appendTokenInfo(dbWechatUser, tokenInfo);
//
//                EntityManager em = JPAEntry.getNewEntityManager();
//                em.getTransaction().begin();
//                em.persist(dbWechatUser);
//                em.persist(user);
//                em.getTransaction().commit();
//                em.close();
//            }
//        } else {
//            appendTokenInfo(dbWechatUser, tokenInfo);
//            JPAEntry.genericPut(dbWechatUser);
//            user = JPAEntry.getObject(User.class, "id", dbWechatUser.getUserId());
//        }
//        return user;
//    }

    private Response follow(WechatPush p) {
        String openId = p.getFromUserName();
        WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "openId", openId);
        if (wechatUser == null) {
            WechatUserInfo userInfo = getUserInfo(openId);
            wechatUser = new WechatUser();
            wechatUser.setId(IdGenerator.getNewId());
            //wechatUser.setUserId(user.getId());
            PublicAccounts.fillWechatUserByUserInfo(wechatUser, userInfo);
            JPAEntry.genericPost(wechatUser);
        }
        return Response.ok().build();
    }

//    @GET
//    @Path("card")
//    @Produces(MediaType.TEXT_HTML)
//    public Response card(@Context HttpServletRequest request, @QueryParam("code") String code) {
//        Map<String, Object> wu = getTokenByCode(code);
//        WechatUser wechatUser = convertTokenInfo(wu);
//
//        Response result = null;
//        Date now = new Date();
//        User user = getOrInsertUserByTokenInfo(now, wechatUser);
//        if (user != null) {
//            Long userId = user.getId();
//            Session s = putSession(now, userId, 0L); //@@deviceId is temp zero
//            try {
//                //result = Response.seeOther(new URI("http://www.xiaoyuzhishi.com/user/active-card.html?userid=" + userId.toString() + "&sessionid=" + s.getIdentity())).build();
//                result = Response.seeOther(new URI("http://www.xiaoyuzhishi.com/user/first-active-card.html?userid=" + userId.toString() + "&sessionid=" + s.getIdentity())).build();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }

//    @GET
//    @Path("account")
//    @Produces(MediaType.TEXT_HTML)
//    public Response account(@Context HttpServletRequest request, @QueryParam("code") String code) {
//        Map<String, Object> wu = getTokenByCode(code);
//        WechatUser wechatUser = convertTokenInfo(wu);
//
//        Response result = null;
//        Date now = new Date();
//        User user = getOrInsertUserByTokenInfo(now, wechatUser);
//        if (user != null) {
//            Long userId = user.getId();
//            Session s = putSession(now, userId, 0L); //@@deviceId is temp zero
//            try {
//                result = Response.seeOther(new URI("http://www.xiaoyuzhishi.com/user/basic-info.html?userid=" + userId.toString() + "&sessionid=" + s.getIdentity())).build();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }

    @GET
    @Path("user")
    @Produces(MediaType.TEXT_HTML)
    public Response user(@Context HttpServletRequest request, @QueryParam("code") String code) {
        Response result = null;
        Map<String, Object> tokenInfo = getTokenByCode(code);
        User user = null;
        String openId = (String)tokenInfo.get("openid");
        WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "openId", openId);
        if (wechatUser == null) {
            wechatUser = new WechatUser();
            wechatUser.setId(IdGenerator.getNewId());
            fillWechatUserByTokenInfo(wechatUser, tokenInfo);
            WechatUserInfo userInfo = getUserInfo((String)tokenInfo.get("access_token"), openId);
            if (userInfo != null) {
                fillWechatUserByUserInfo(wechatUser, userInfo);
                EntityManager em = JPAEntry.getNewEntityManager();
                em.getTransaction().begin();
                em.persist(wechatUser);
                em.getTransaction().commit();
                em.close();
            }
        } else {
            user = JPAEntry.getObject(User.class, "id", wechatUser.getUserId());
            fillWechatUserByTokenInfo(wechatUser, tokenInfo);
            EntityManager em = JPAEntry.getNewEntityManager();
            em.getTransaction().begin();
            em.merge(wechatUser);
            em.getTransaction().commit();
            em.close();
        }

        Session s = null;
        if (user != null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    s = getSession(cookie.getValue());
                    break;
                }
            }
        }
        try {
            if (s == null) {
                result = Response.seeOther(new URI("http://www.xiaoyuzhishi.com/user.html?openId=" + openId)).build();
            } else {
                result = Response.seeOther(new URI("http://www.xiaoyuzhishi.com/user.html?sessionId=" + s.getIdentity())).build();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

//    @GET
//    @Path("chinese")
//    @Produces(MediaType.TEXT_HTML)
//    public Response chinese(@Context HttpServletRequest request, @QueryParam("code") String code) {
//        Map<String, Object> wu = getTokenByCode(code);
//        WechatUser wechatUser = convertTokenInfo(wu);
//
//        Response result = null;
//        Date now = new Date();
//        User user = getOrInsertUserByTokenInfo(now, wechatUser);
//        if (user != null) {
//            Long userId = user.getId();
//            Session s = putSession(now, userId, 0L); //@@deviceId is temp zero
//            try {
//                result = Response.seeOther(new URI("http://www.xiaoyuzhishi.com/content.html?userid=" + userId.toString() + "&sessionid=" + s.getIdentity() + "&subject=chinese")).build();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }
//
//    @GET
//    @Path("math")
//    @Produces(MediaType.TEXT_HTML)
//    public Response math(@Context HttpServletRequest request, @QueryParam("code") String code) {
//        Map<String, Object> wu = getTokenByCode(code);
//        WechatUser wechatUser = convertTokenInfo(wu);
//
//        Response result = null;
//        Date now = new Date();
//        User user = getOrInsertUserByTokenInfo(now, wechatUser);
//        if (user != null) {
//            Long userId = user.getId();
//            Session s = putSession(now, userId, 0L); //@@deviceId is temp zero
//            try {
//                result = Response.seeOther(new URI("http://www.xiaoyuzhishi.com/content.html?userid=" + userId.toString() + "&sessionid=" + s.getIdentity() + "&subject=math")).build();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }
//
//    @GET
//    @Path("video")
//    @Produces(MediaType.TEXT_HTML)
//    public Response video(@Context HttpServletRequest request, @QueryParam("code") String code) {
//        Map<String, Object> wu = getTokenByCode(code);
//        WechatUser wechatUser = convertTokenInfo(wu);
//
//        Response result = null;
//        Date now = new Date();
//        User user = getOrInsertUserByTokenInfo(now, wechatUser);
//        if (user != null) {
//            Long userId = user.getId();
//            Session s = putSession(now, userId, 0L); //@@deviceId is temp zero
//            try {
//                result = Response.seeOther(new URI("http://www.xiaoyuzhishi.com/content/videos.html?userid=" + userId.toString() + "&sessionid=" + s.getIdentity())).build();
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }

    //获取微信服务器ID
    //public static

    public static int errorProc(String r) {
        int result = 0;
        if (r.contains("errcode")) {
            GenericResult rc = new Gson().fromJson(r, GenericResult.class);
            result = rc.errcode;
            switch (rc.errcode) {
                case 0:
                    break;
                case 40002: // access-token
                case 41001:
                    getTokenFromWechatPlatform();
                    result = 0;
                    break;
                case 40003: // openid
                    break;
            }
        }
        return result;
    }

    //获取接口调用凭证
    private static String[] getUserList(String nextOpenid) {
        prepare();
        // http请求方式: GET（请使用https协议）
        // https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID
        prepare();
        ArrayList<String> result = new ArrayList<>();
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/user/get")
                .queryParam("access_token", accessToken)
                .queryParam("next_openid", nextOpenid)
                .request().get();
        //{"total":2,"count":2,"data":{"openid":["","OPENID1","OPENID2"]},"next_openid":"NEXT_OPENID"}
        String responseBody = response.readEntity(String.class);
        if (responseBody.contains("data")) {
            //{"access_token":"ACCESS_TOKEN","expires_in":7200}
            UserList us = new Gson().fromJson(responseBody, UserList.class);
            result.add(us.next_openid);
            if (us.count < 10000) {
                result.set(0, null);
            }
            for (String openId : us.data.openid) {
                result.add(openId);
            }
        }
        String[] a = new String[result.size()];
        return result.toArray(a);
    }

//    @POST
//    @Path("followers")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getFollowers() {
//        String maxOpenid = "";
//        Date now = new Date();
//        while (true) {
//            String[] openids = getUserList(maxOpenid);
//            boolean isFirst = true;
//            for (String openId : openids) {
//                if (isFirst) {
//                    isFirst = false;
//                } else {
//                    WechatUser dbWechatUser = JPAEntry.getObject(WechatUser.class, "openId", openId);
//                    if (dbWechatUser == null) {
//                        insertUserByOpenId(now, openId);
//                    }
//                }
//            }
//            maxOpenid = openids[0];
//            if (maxOpenid == null) {
//                break;
//            }
//        }
//        return Response.ok().build();
//    }

    @POST
    @Path("follow")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response follow(JAXBElement<Follow> follow) {
        Follow f = follow.getValue();
        if (f.Event.equals("subscribe")) {
            WechatUserInfo us = getUserInfo(f.FromUserName);
            //666
            WechatUser user = new WechatUser();
            user.setId(IdGenerator.getNewId());
            user.setOpenId(us.openid);
            user.setCity(us.city);
            user.setCountry(us.country);
            //user.setExpiry();
            user.setHead(us.headimgurl);
            user.setInfo(us.toString());
            user.setNickname(us.nickname);
            //user.setPrivilege();
            user.setProvince(us.province);
            //user.setRefId();
            //user.setRefreshToken();
            user.setSex(us.sex.intValue());
            user.setSubscribeTime(us.subscribe_time);
            user.setSubscribe(us.subscribe);
            user.setLanguage(us.language);
            user.setRemark(us.remark);
            user.setGroupId(us.groupid);
            //user.setToken();
            user.setUnionId(us.unionid);
            long userId = IdGenerator.getNewId();
            User u = new User();
            u.setId(userId);
            u.setHead(us.headimgurl);
            u.setName(us.nickname);
            //u.setLoginName(us.nickname);
            u.setSex(us.sex.intValue());
            Date now = new Date();
            u.setCreateTime(now);
            u.setUpdateTime(now);
            u.setIsAdministrator(false);
            u.setSite("http://www.xiaoyuzhishi.com");
            u.setAmount(0.0f);
            user.setUserId(userId);

            EntityManager em = JPAEntry.getNewEntityManager();
            em.getTransaction().begin();
            em.persist(user);
            em.persist(u);
            em.getTransaction().commit();
            em.close();
        }
        return Response.ok().build();
    }

    @POST
    @Path("click-menu")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_HTML)
    public Response clickMenu(JAXBElement<ClickEvent> clickEvent) {
        //没有处理，记得要做处理
        ClickEvent ce = clickEvent.getValue();
        //step1: get user.id from openid
        //666
        WechatUser u = JPAEntry.getObject(WechatUser.class, "openId", ce.FromUserName);
        //step2: generate sessionId //now.tostring().md5()
        Date now = new Date();
        String nowString = now.toString();
        byte[] sessionId = Securities.digestor.digest(nowString);
        String sessionString = Hex.bytesToHex(sessionId);
        //step3: sessionId and user.id => sessions
        Session s = new Session();
        s.setId(IdGenerator.getNewId());
        s.setUserId(u.getUserId());
        s.setIdentity(sessionString);
        s.setLastOperationTime(now);
        JPAEntry.genericPost(s);

        return Response.ok(new File("E:\\projects\\study-2\\src\\main\\webapp\\validationCode.html"), "text/html").cookie(new NewCookie("sessionId", sessionString, "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false)).build();
        //return Response.ok(new File("/data/program/swtomcat/webapps/ROOT/validationCode.html"), "text/html").cookie(new NewCookie("sessionId", sessionString, "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false)).build();
    }

    //自定义菜单查询接口
    private GenericResult getWechatServerIpList() {
        //https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN
        //{"ip_list":["127.0.0.1","127.0.0.1"]}
        GenericResult result = new GenericResult();
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/get")
                .queryParam("access_token", accessToken)
                .request().get();
        IPList ipList = response.readEntity(IPList.class);
        return result;
    }

    //自定义菜单创建接口
    private GenericResult createCustomMenu(Entity<?> menu) {
        //POST https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN
        //{"errcode":0,"errmsg":"ok"}
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/menu/create")
                .queryParam("access_token", accessToken)
                .request().post(menu);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //自定义菜单删除接口
    private GenericResult deleteCustomMenu() {
        //https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN
        //{"errcode":0,"errmsg":"ok"}
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/menu/delete")
                .queryParam("access_token", accessToken)
                .request().get();
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //创建个性化菜单
    private GenericResult cretePersonalityMenu(Entity<?> menu) {
        // https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=ACCESS_TOKEN

        //{"errcode":0,"errmsg":"ok"}
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/menu/addconditional")
                .queryParam("access_token", accessToken)
                .request().post(menu);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //删除个性化菜单
    private DelResult deletePersonalityMenu(Entity<?> menu) {
        // https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=ACCESS_TOKEN
        //{"errcode":0,"errmsg":"ok"}
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/menu/delconditional")
                .queryParam("access_token", accessToken)
                .request().post(menu);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    @POST
    @Path("click-link-menu")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clickLinkMenu(@CookieParam("sessionId") String sessionId, ClickLink clickLink) {
        //没有处理，记得要做处理
        //step1: get user.id from openid
        //step2: record to sessions table
        //step3: cookie and user.id => sessions
        return null;
    }

    @POST
    @Path("scan-code-push")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response scancodePush(@CookieParam("sessionId") String sessionId, ScancodePush scancodePush) {
        //没有处理，记得要做处理
        return null;
    }

    //扫码推事件且弹出“消息接收中”提示框的事件推送
    @POST
    @Path("scan-code-wait-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response scancode_waitmsg(@CookieParam("sessionId") String sessionId, ScancodePush scancodePush) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("picture-system-photo")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pic_sysphoto(@CookieParam("sessionId") String sessionId, PicSysphoto picSysphoto) {
        //没有处理，记得要做处理
        return null;
    }

    //弹出拍照或者相册发图的事件推送
    @POST
    @Path("picture-photo-or-album")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pic_photo_or_album(@CookieParam("sessionId") String sessionId, PicSysphoto picSysphoto) {
        //没有处理，记得要做处理
        return null;
    }

    //：弹出微信相册发图器的事件推送
    @POST
    @Path("pictures")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pic_weixin(@CookieParam("sessionId") String sessionId, PicSysphoto picSysphoto) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("locations")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response location_select(@CookieParam("sessionId") String sessionId, LocationSelect locationSelect) {
        //没有处理，记得要做处理
        return null;
    }

    //测试个性化菜单匹配结果
    private GenericResult testPersonalityMenu(Entity<?> menu) {
        //https://api.weixin.qq.com/cgi-bin/menu/trymatch?access_token=ACCESS_TOKEN
        //{"errcode":0,"errmsg":"ok"}
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/menu/trymatch")
                .queryParam("access_token", accessToken)
                .request().post(menu);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //获取自定义菜单配置接口
    private GenericResult testPersonalityMenu() {
        //https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=ACCESS_TOKEN//{"errcode":0,"errmsg":"ok"}
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/get_current_selfmenu_info")
                .queryParam("access_token", accessToken)
                .request().get();
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    @POST
    @Path("text-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response textmessage(@CookieParam("sessionId") String sessionId, TextMessage textMessage) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("picture-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response prcturemessage(@CookieParam("sessionId") String sessionId, PictureMessage pictureMessage) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("voice-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response voiceMeessage(@CookieParam("sessionId") String sessionId, VoiceMeessage voiceMeessage) {
        //没有处理，记得要做处理
        return null;
    }

    //扫码推事件的事件推送

    @POST
    @Path("video-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response videoMessage(@CookieParam("sessionId") String sessionId, VideoMessage videoMessage) {
        //没有处理，记得要做处理
        return null;
    }

    // 接受消息 ：小视频消息
    @POST
    @Path("small-video-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response smallvoideMeessage(@CookieParam("sessionId") String sessionId, VideoMessage videoMessage) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("small-voice-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response smallvoiceMeessage(@CookieParam("sessionId") String sessionId, LocationInformation locationInformation) {
        //没有处理，记得要做处理
        return null;
    }

    //弹出系统拍照发图的事件推送

    @POST
    @Path("link-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response smallvoiceMeessage(@CookieParam("sessionId") String sessionId, LinkMessage linkMessage) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("scan-code-claim")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response notconcerned(@CookieParam("sessionId") String sessionId, Scanning scanning) {
        //没有处理，记得要做处理
        return null;
    }

    // 扫描带参数二维码事件
    //如果用户已经关注公众号，则微信会将带场景值扫描事件推送给开发者。
    @POST
    @Path("scan-code")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response concerned(@CookieParam("sessionId") String sessionId, Scanning scanning) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("position")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response position(@CookieParam("sessionId") String sessionId, Position position) {
        //没有处理，记得要做处理
        //conflict to Jumplink
        return null;
    }

    // 弹出地理位置选择器的事件推送

    @POST
    @Path("menu")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response menu(@CookieParam("sessionId") String sessionId, Menu menu) {
        //没有处理，记得要做处理
        return null;
    }

    // 自定义菜单事件
    @POST
    @Path("jump-link")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Jumplink(@CookieParam("sessionId") String sessionId, Menu menu) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("return-text-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replytextmessage(@CookieParam("sessionId") String sessionId, ReplyTextMessage replyTextMessage) {

        return null;
    }

    @POST
    @Path("return-picture-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replypicturemessage(@CookieParam("sessionId") String sessionId, ReplyPictureMessage replyPictureMessage) {
        //没有处理，记得要做处理
        return null;
    }

    //回复语音消息
    @POST
    @Path("return-voice-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyvoicemessage(@CookieParam("sessionId") String sessionId, ReplyPictureMessage replyPictureMessage) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("return-video-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyvoidemessage(@CookieParam("sessionId") String sessionId, ReplyVoideMessage replyVoideMessage) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("return-music-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replymusicmessage(@CookieParam("sessionId") String sessionId, ReplyMusicMessage replyMusicMessage) {
        //没有处理，记得要做处理
        return null;
    }

    @POST
    @Path("return-image-text-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyimagetextmessage(@CookieParam("sessionId") String sessionId, ReplyImageTextMessage replyImageTextMessage) {
        //没有处理，记得要做处理
        return null;
    }

    //添加客服帐号
    private GenericResult Addcustomerserviceaccount(Entity<?> menu) {
        //https://api.weixin.qq.com/customservice/kfaccount/add?access_token=ACCESS_TOKEN
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/customservice/kfaccount/add")
                .queryParam("access_token", accessToken)
                .request().post(menu);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //修改客服帐号
    private GenericResult Updatecustomerserviceaccount(Entity<?> menu) {
        //https://api.weixin.qq.com/customservice/kfaccount/update?access_token=ACCESS_TOKEN
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/customservice/kfaccount/update")
                .queryParam("access_token", accessToken)
                .request().post(menu);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //删除客服帐号
    private GenericResult Delcustomerserviceaccount(Entity<?> menu) {
        //https://api.weixin.qq.com/customservice/kfaccount/del?access_token=ACCESS_TOKEN
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/customservice/kfaccount/del")
                .queryParam("access_token", accessToken)
                .request().post(menu);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //设置客服帐号的头像
    //http请求方式: POST/FORM
    private GenericResult uploadheadimg(Entity<?> menu) {
        //http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg?access_token=ACCESS_TOKEN&kf_account=KFACCOUNT
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/customservice/kfaccount/uploadheadimg")
                .queryParam("access_token", accessToken)
                .request().post(menu);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    private kfResult getkflist() {
        // http请求方式: GET
        // https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=ACCESS_TOKEN
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/customservice/kfaccount/getkflist")
                .queryParam("access_token", accessToken)
                .request().get();
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    // 接受消息 ：地理位置消息

    private GenericResult send(Entity<? extends Message> message) {
        //http请求方式: POST
        //https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/customservice/kfaccount/send")
                .queryParam("access_token", accessToken)
                .request().post(message);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //上传图文消息内的图片获取URL【订阅号与服务号认证后均可用】
    private UrlResult uploadimg(Entity<?> message) {
        //http请求方式: POST
        //https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN
        //调用示例（使用curl命令，用FORM表单方式上传一个图片）：
        // curl -F media=@test.jpg "https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=ACCESS_TOKEN"
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/media/uploadimg")
                .queryParam("access_token", accessToken)
                .request().post(message);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    // 接受消息 ：链接消息

    private ArticlesResult Uploadgraphicsandtextmessagematerial(Entity<Articles> articles) {
        //http请求方式: POST
        //https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=ACCESS_TOKEN
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/media/uploadnews")
                .queryParam("access_token", accessToken)
                .request().post(articles);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    private ArticlesResult Packetgroup(Entity<Articles> articles) {
        //http请求方式: POST
        //https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
                .path("/cgi-bin/message/custom/sendall")
                .queryParam("access_token", accessToken)
                .request().post(articles);
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    /*
    ?signature=xxx&timestamp=123456&nonce=123&echostr=....

    <xml>
        <ToUserName><![CDATA[toUser]]></ToUserName>
        <FromUserName><![CDATA[fromUser]]></FromUserName>
        <CreateTime>1348831860</CreateTime>
        <MsgType><![CDATA[text]]></MsgType>
        <Content><![CDATA[this is a test]]></Content>
        <MsgId>1234567890123456</MsgId>
    </xml>
    */
    /*
    <xml>
        <ToUserName><![CDATA[toUser]]></ToUserName>
        <FromUserName><![CDATA[fromUser]]></FromUserName>
        <CreateTime>12345678</CreateTime>
        <MsgType><![CDATA[text]]></MsgType>
        <Content><![CDATA[你好]]></Content>
    </xml>
    */
    public static class DeveloperValidation {
        private String ToUserName;
        private String FromUserName;
        private Long CreateTime;
        private String MsgType;
        private String Content;
        private Long MsgId;

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public Long getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(Long createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }

        public Long getMsgId() {
            return MsgId;
        }

        public void setMsgId(Long msgId) {
            MsgId = msgId;
        }

    }

    private static class UserList {
        private int total;
        private int count;
        private UserData data;
        private String next_openid;

        public static class UserData {
            private String[] openid;
        }
    }

    // 关注/取消关注事件
    private static class Follow {
        public String ToUserName;//	开发者微信号
        public String FromUserName;//	发送方帐号（一个OpenID）
        public int CreateTime;//	消息创建时间 （整型）
        public String MsgType;//	消息类型，event
        public String Event;//	事件类型，subscribe(订阅)、unsubscribe(取消订阅)

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }
    }

    private static class DelResult {
        public String menuid;
    }

    private static class IPList {
        private String[] ip_list;

        public String[] getIp_list() {
            return ip_list;
        }

        public void setIp_list(String[] ip_list) {
            this.ip_list = ip_list;
        }
    }

    public static class ServerList {
        private Server[] kf_list;

        public static class Server {
            private String kf_account;
            private String kf_nick;
            private String kf_id;
            private String kf_headimgurl;
        }
    }

    //点击菜单拉取消息时的事件推送
    private static class ClickEvent {
        public String ToUserName;//开发者微信号
        public String FromUserName;//发送方帐号（一个OpenID）
        public int CreateTime;//消息创建时间 （整型）
        public String MsgType;//消息类型，event
        public String Event;//事件类型，CLICK
        public String EventKey;//事件KEY值，与自定义菜单接口中KEY值对应

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }

        public String getEventKey() {
            return EventKey;
        }

        public void setEventKey(String eventKey) {
            EventKey = eventKey;
        }
    }

    //点击菜单跳转链接时的事件推送
    private static class ClickLink {

        public String ToUserName;    //开发者微信号
        public String FromUserName;    //发送方帐号（一个OpenID）
        public int CreateTime;    //消息创建时间 （整型）
        public String MsgType;    //消息类型，event
        public String Event;    //事件类型，VIEW
        public String EventKey;    //事件KEY值，设置的跳转URL
        public String MenuID;    //指菜单ID，如果是个性化菜单，则可以通过这个字段，知道是哪个规则的菜单被点击了。

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }

        public String getEventKey() {
            return EventKey;
        }

        public void setEventKey(String eventKey) {
            EventKey = eventKey;
        }

        public String getMenuID() {
            return MenuID;
        }

        public void setMenuID(String menuID) {
            MenuID = menuID;
        }

    }

    private static class ScancodePush {

        public String ToUserName;    //开发者微信号
        public String FromUserName;    //发送方帐号（一个OpenID）
        public int CreateTime;    //消息创建时间（整型）
        public String MsgType;    //消息类型，event
        public String Event;    //事件类型，scancode_push
        public String EventKey;    //事件KEY值，由开发者在创建菜单时设定
        public String ScanCodeInfo;    //扫描信息
        public String ScanType;    //扫描类型，一般是qrcode
        public String ScanResult;    //扫描结果，即二维码对应的字符串信息

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }

        public String getEventKey() {
            return EventKey;
        }

        public void setEventKey(String eventKey) {
            EventKey = eventKey;
        }

        public String getScanCodeInfo() {
            return ScanCodeInfo;
        }

        public void setScanCodeInfo(String scanCodeInfo) {
            ScanCodeInfo = scanCodeInfo;
        }

        public String getScanType() {
            return ScanType;
        }

        public void setScanType(String scanType) {
            ScanType = scanType;
        }

        public String getScanResult() {
            return ScanResult;
        }

        public void setScanResult(String scanResult) {
            ScanResult = scanResult;
        }

    }

    private static class PicSysphoto {

        public String ToUserName;//	开发者微信号
        public String FromUserName;//	发送方帐号（一个OpenID）
        public int CreateTime;//	消息创建时间 （整型）
        public String MsgType;//	消息类型，event
        public String Event;//	事件类型，pic_sysphoto
        public String EventKey;//	事件KEY值，由开发者在创建菜单时设定
        public String SendPicsInfo;//	发送的图片信息
        public String Count;//	发送的图片数量
        public String PicList;//	图片列表
        public String PicMd5Sum;//	图片的MD5值，开发者若需要，可用于验证接收到图片

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }

        public String getEventKey() {
            return EventKey;
        }

        public void setEventKey(String eventKey) {
            EventKey = eventKey;
        }

        public String getSendPicsInfo() {
            return SendPicsInfo;
        }

        public void setSendPicsInfo(String sendPicsInfo) {
            SendPicsInfo = sendPicsInfo;
        }

        public String getCount() {
            return Count;
        }

        public void setCount(String count) {
            Count = count;
        }

        public String getPicList() {
            return PicList;
        }

        public void setPicList(String picList) {
            PicList = picList;
        }

        public String getPicMd5Sum() {
            return PicMd5Sum;
        }

        public void setPicMd5Sum(String picMd5Sum) {
            PicMd5Sum = picMd5Sum;
        }

    }

    private static class LocationSelect {

        public String ToUserName;    //开发者微信号
        public String FromUserName;    //发送方帐号（一个OpenID）
        public int CreateTime;    //消息创建时间 （整型）
        public String MsgType;    //消息类型，event
        public String Event;    //事件类型，location_select
        public String EventKey;    //事件KEY值，由开发者在创建菜单时设定
        public String SendLocationInfo;    //发送的位置信息
        public String Location_X;    //X坐标信息
        public String Location_Y;    //Y坐标信息
        public String Scale;    //精度，可理解为精度或者比例尺、越精细的话 scale越高
        public String Label;    //地理位置的字符串信息
        public String Poiname;    //朋友圈POI的名字，可能为空

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }

        public String getEventKey() {
            return EventKey;
        }

        public void setEventKey(String eventKey) {
            EventKey = eventKey;
        }

        public String getSendLocationInfo() {
            return SendLocationInfo;
        }

        public void setSendLocationInfo(String sendLocationInfo) {
            SendLocationInfo = sendLocationInfo;
        }

        public String getLocation_X() {
            return Location_X;
        }

        public void setLocation_X(String location_X) {
            Location_X = location_X;
        }

        public String getLocation_Y() {
            return Location_Y;
        }

        public void setLocation_Y(String location_Y) {
            Location_Y = location_Y;
        }

        public String getScale() {
            return Scale;
        }

        public void setScale(String scale) {
            Scale = scale;
        }

        public String getLabel() {
            return Label;
        }

        public void setLabel(String label) {
            Label = label;
        }

        public String getPoiname() {
            return Poiname;
        }

        public void setPoiname(String poiname) {
            Poiname = poiname;
        }

    }

    // 接受消息 ：文本消息
    private static class TextMessage {

        public String ToUserName;//	开发者微信号
        public String FromUserName;//发送方帐号（一个OpenID）
        public int CreateTime;//	消息创建时间 （整型）
        public String MsgType;//	text
        public String Content;//	文本消息内容
        public int MsgId;//	消息id，64位整型

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int msgId) {
            MsgId = msgId;
        }
    }

    // 接受消息 ：图片消息
    private static class PictureMessage {

        public String ToUserName;//	开发者微信号
        public String FromUserName;//	发送方帐号（一个OpenID）
        public int CreateTime;//	消息创建时间 （整型）
        public String MsgType;//	image
        public String PicUrl;//	图片链接
        public int MediaId;//	图片消息媒体id，可以调用多媒体文件下载接口拉取数据。
        public int MsgId;//	消息id，64位整型

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getPicUrl() {
            return PicUrl;
        }

        public void setPicUrl(String picUrl) {
            PicUrl = picUrl;
        }

        public int getMediaId() {
            return MediaId;
        }

        public void setMediaId(int mediaId) {
            MediaId = mediaId;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int msgId) {
            MsgId = msgId;
        }
    }

    // 接受消息 ：语音消息
    private static class VoiceMeessage {

        public String ToUserName;    //开发者微信号
        public String FromUserName;    //发送方帐号（一个OpenID）
        public int CreateTime;    //消息创建时间 （整型）
        public String MsgType;    //语音为voice
        public int MediaId;    //语音消息媒体id，可以调用多媒体文件下载接口拉取数据。
        public String Format;    //语音格式，如amr，speex等
        public int MsgID;    //消息id，64位整型

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public int getMediaId() {
            return MediaId;
        }

        public void setMediaId(int mediaId) {
            MediaId = mediaId;
        }

        public String getFormat() {
            return Format;
        }

        public void setFormat(String format) {
            Format = format;
        }

        public int getMsgID() {
            return MsgID;
        }

        public void setMsgID(int msgID) {
            MsgID = msgID;
        }


    }

    // 接受消息 ：视频消息
    private static class VideoMessage {

        public String ToUserName;    //开发者微信号
        public String FromUserName;    //发送方帐号（一个OpenID）
        public int CreateTime;    //消息创建时间 （整型）
        public String MsgType;    //视频为video
        public int MediaId;    //视频消息媒体id，可以调用多媒体文件下载接口拉取数据。
        public int ThumbMediaId;    //视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
        public int MsgId;//消息id，64位整型

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public int getMediaId() {
            return MediaId;
        }

        public void setMediaId(int mediaId) {
            MediaId = mediaId;
        }

        public int getThumbMediaId() {
            return ThumbMediaId;
        }

        public void setThumbMediaId(int thumbMediaId) {
            ThumbMediaId = thumbMediaId;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int msgId) {
            MsgId = msgId;
        }


    }

    private static class LocationInformation {

        public String ToUserName;//	开发者微信号
        public String FromUserName;//	发送方帐号（一个OpenID）
        public int CreateTime;//	消息创建时间 （整型）
        public String MsgType;    //location
        public String Location_X;    //地理位置维度
        public String Location_Y;    //地理位置经度
        public String Scale;    //地图缩放大小
        public String Label;    //地理位置信息
        public int MsgId;    //消息id，64位整型

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getLocation_X() {
            return Location_X;
        }

        public void setLocation_X(String location_X) {
            Location_X = location_X;
        }

        public String getLocation_Y() {
            return Location_Y;
        }

        public void setLocation_Y(String location_Y) {
            Location_Y = location_Y;
        }

        public String getScale() {
            return Scale;
        }

        public void setScale(String scale) {
            Scale = scale;
        }

        public String getLabel() {
            return Label;
        }

        public void setLabel(String label) {
            Label = label;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int msgId) {
            MsgId = msgId;
        }


    }

    private static class LinkMessage {

        public String ToUserName;//	接收方微信号
        public String FromUserName;//	发送方微信号，若为普通用户，则是一个OpenID
        public int CreateTime;//	消息创建时间
        public String MsgType;    //消息类型，link
        public String Title;    //消息标题
        public String Description;    //消息描述
        public String Url;    //消息链接
        public int MsgId;//消息id，64位整型

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }

        public int getMsgId() {
            return MsgId;
        }

        public void setMsgId(int msgId) {
            MsgId = msgId;
        }


    }

    // 扫描带参数二维码事件
    //如果用户还未关注公众号，则用户可以关注公众号，关注后微信会将带场景值关注事件推送给开发者。
    private static class Scanning {
        public String ToUserName;    //开发者微信号
        public String FromUserName;    //发送方帐号（一个OpenID）
        public int CreateTime;    //消息创建时间 （整型）
        public String MsgType;    //消息类型，event
        public String Event;    //事件类型，subscribe
        public String EventKey;    //事件KEY值，qrscene_为前缀，后面为二维码的参数值
        public String Ticket;    //二维码的ticket，可用来换取二维码图片

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }

        public String getEventKey() {
            return EventKey;
        }

        public void setEventKey(String eventKey) {
            EventKey = eventKey;
        }

        public String getTicket() {
            return Ticket;
        }

        public void setTicket(String ticket) {
            Ticket = ticket;
        }
    }

    // 上报地理位置事件
    private static class Position {
        public String ToUserName;    //开发者微信号
        public String FromUserName;    //发送方帐号（一个OpenID）
        public String CreateTime;    //消息创建时间 （整型）
        public String MsgType;    //消息类型，event
        public String Event;    //事件类型，LOCATION
        public String Latitude;    //地理位置纬度
        public String Longitude;    //地理位置经度
        public String Precision;    //地理位置精度

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }

        public String getLatitude() {
            return Latitude;
        }

        public void setLatitude(String latitude) {
            Latitude = latitude;
        }

        public String getLongitude() {
            return Longitude;
        }

        public void setLongitude(String longitude) {
            Longitude = longitude;
        }

        public String getPrecision() {
            return Precision;
        }

        public void setPrecision(String precision) {
            Precision = precision;
        }
    }

    // 自定义菜单事件
    private static class Menu {
        public String ToUserName;    //开发者微信号
        public String FromUserName;    //发送方帐号（一个OpenID）
        public int CreateTime;    //消息创建时间 （整型）
        public String MsgType;    //消息类型，event
        public String Event;    //事件类型，CLICK
        public String EventKey;    //事件KEY值，与自定义菜单接口中KEY值对应

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getEvent() {
            return Event;
        }

        public void setEvent(String event) {
            Event = event;
        }

        public String getEventKey() {
            return EventKey;
        }

        public void setEventKey(String eventKey) {
            EventKey = eventKey;
        }
    }

    //回复文本消息
    private static class ReplyTextMessage {
        public String ToUserName;    //是	接收方帐号（收到的OpenID）
        public String FromUserName;    //是	开发者微信号
        public String CreateTime;    //是	消息创建时间 （整型）
        public String MsgType;    //是	text
        public String Content;    //是	回复的消息内容（换行：在content中能够换行，微信客户端就支持换行显示）

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getContent() {
            return Content;
        }

        public void setContent(String content) {
            Content = content;
        }
    }

    //回复图片消息
    private static class ReplyPictureMessage {
        public String ToUserName;    //是	接收方帐号（收到的OpenID）
        public String FromUserName;//	是	开发者微信号
        public int CreateTime;    //是	消息创建时间 （整型）
        public String MsgType;    //是	image
        public int MediaId;    //是	通过素材管理接口上传多媒体文件，得到的id。

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public int getMediaId() {
            return MediaId;
        }

        public void setMediaId(int mediaId) {
            MediaId = mediaId;
        }
    }

    //回复视频消息
    private static class ReplyVoideMessage {
        public String ToUserName;    //是	接收方帐号（收到的OpenID）
        public String FromUserName;    //是	开发者微信号
        public String CreateTime;    //是	消息创建时间 （整型）
        public String MsgType;    //是	video
        public String MediaId;//	是	通过素材管理接口上传多媒体文件，得到的id
        public String Title;    //否	视频消息的标题
        public String Description;    //否	视频消息的描述

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getMediaId() {
            return MediaId;
        }

        public void setMediaId(String mediaId) {
            MediaId = mediaId;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }
    }

    //回复音乐消息
    private static class ReplyMusicMessage {
        public String ToUserName;    //是	接收方帐号（收到的OpenID）
        public String FromUserName;    //是	开发者微信号
        public int CreateTime;    //是	消息创建时间 （整型）
        public String MsgType;    //是	music
        public String Title;    //否	音乐标题
        public String Description;    //否	音乐描述
        public String MusicURL;    //否	音乐链接
        public String HQMusicUrl;    //否	高质量音乐链接，WIFI环境优先使用该链接播放音乐
        public int ThumbMediaId;    //否	缩略图的媒体id，通过素材管理接口上传多媒体文件，得到的id

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getMusicURL() {
            return MusicURL;
        }

        public void setMusicURL(String musicURL) {
            MusicURL = musicURL;
        }

        public String getHQMusicUrl() {
            return HQMusicUrl;
        }

        public void setHQMusicUrl(String HQMusicUrl) {
            this.HQMusicUrl = HQMusicUrl;
        }

        public int getThumbMediaId() {
            return ThumbMediaId;
        }

        public void setThumbMediaId(int thumbMediaId) {
            ThumbMediaId = thumbMediaId;
        }
    }

    //回复图文消息
    private static class ReplyImageTextMessage {
        public String ToUserName;    //是	接收方帐号（收到的OpenID）
        public String FromUserName;    //是	开发者微信号
        public int CreateTime;    //是	消息创建时间 （整型）
        public String MsgType;    //是	news
        public String ArticleCount;    //是	图文消息个数，限制为10条以内
        public String Articles;    //是	多条图文消息信息，默认第一个item为大图,注意，如果图文数超过10，则将会无响应
        public String Title;//否	图文消息标题
        public String Description;    //否	图文消息描述
        public String PicUrl;    //否	图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
        public String Url;    //否	点击图文消息跳转链接

        public String getToUserName() {
            return ToUserName;
        }

        public void setToUserName(String toUserName) {
            ToUserName = toUserName;
        }

        public String getFromUserName() {
            return FromUserName;
        }

        public void setFromUserName(String fromUserName) {
            FromUserName = fromUserName;
        }

        public int getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(int createTime) {
            CreateTime = createTime;
        }

        public String getMsgType() {
            return MsgType;
        }

        public void setMsgType(String msgType) {
            MsgType = msgType;
        }

        public String getArticleCount() {
            return ArticleCount;
        }

        public void setArticleCount(String articleCount) {
            ArticleCount = articleCount;
        }

        public String getArticles() {
            return Articles;
        }

        public void setArticles(String articles) {
            Articles = articles;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getPicUrl() {
            return PicUrl;
        }

        public void setPicUrl(String picUrl) {
            PicUrl = picUrl;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }
    }

    //获取所有客服账号
    private static class kfResult {
        public server[] kf_list;

        public server[] getKf_list() {
            return kf_list;
        }

        public void setKf_list(server[] kf_list) {
            this.kf_list = kf_list;
        }

        public static class server {
            public String kf_account;
            public String kf_nick;
            public String kf_id;
            public String kf_headimgurl;

            public String getKf_account() {
                return kf_account;
            }

            public void setKf_account(String kf_account) {
                this.kf_account = kf_account;
            }

            public String getKf_nick() {
                return kf_nick;
            }

            public void setKf_nick(String kf_nick) {
                this.kf_nick = kf_nick;
            }

            public String getKf_id() {
                return kf_id;
            }

            public void setKf_id(String kf_id) {
                this.kf_id = kf_id;
            }

            public String getKf_headimgurl() {
                return kf_headimgurl;
            }

            public void setKf_headimgurl(String kf_headimgurl) {
                this.kf_headimgurl = kf_headimgurl;
            }
        }
    }

    //客服接口-发消息
    static class Message {
        private String touser;
        private String msgtype;
        private String customservice;

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public String getCustomservice() {
            return customservice;
        }

        public void setCustomservice(String customservice) {
            this.customservice = customservice;
        }
    }

    public static class TextsMessage extends Message {
        private Text text;

        public Text getText() {
            return text;
        }

        public void setText(Text text) {
            this.text = text;
        }

        public static class Text {
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }

    public static class PicturesMessage extends Message {
        private Image image;

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        private static class Image {
            private String media_id;

            public String getMedia_id() {
                return media_id;
            }

            public void setMedia_id(String media_id) {
                this.media_id = media_id;
            }
        }
    }

    public static class VoiceMessage extends Message {
        public Voice voice;

        public Voice getVoice() {
            return voice;
        }

        public void setVoice(Voice voice) {
            this.voice = voice;
        }

        private static class Voice {
            private String media_id;

            public String getMedia_id() {
                return media_id;
            }

            public void setMedia_id(String media_id) {
                this.media_id = media_id;
            }
        }
    }

    public static class VideosMessage extends Message {
        public Video video;

        public Video getVideo() {
            return video;
        }

        public void setVideo(Video video) {
            this.video = video;
        }

        private static class Video {
            private String media_id;
            private String thumb_media_id;
            private String title;
            private String description;

            public String getMedia_id() {
                return media_id;
            }

            public void setMedia_id(String media_id) {
                this.media_id = media_id;
            }

            public String getThumb_media_id() {
                return thumb_media_id;
            }

            public void setThumb_media_id(String thumb_media_id) {
                this.thumb_media_id = thumb_media_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }

    public static class MusicMessage extends Message {
        public Music music;

        public Music getMusic() {
            return music;
        }

        public void setMusic(Music music) {
            this.music = music;
        }

        public static class Music {
            public String title;
            public String description;
            public String musicurl;
            public String hqmusicurl;
            public String thumb_media_id;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getMusicurl() {
                return musicurl;
            }

            public void setMusicurl(String musicurl) {
                this.musicurl = musicurl;
            }

            public String getHqmusicurl() {
                return hqmusicurl;
            }

            public void setHqmusicurl(String hqmusicurl) {
                this.hqmusicurl = hqmusicurl;
            }

            public String getThumb_media_id() {
                return thumb_media_id;
            }

            public void setThumb_media_id(String thumb_media_id) {
                this.thumb_media_id = thumb_media_id;
            }
        }
    }

    public static class NewsMessage extends Message {
        public News news;

        public News getNews() {
            return news;
        }

        public void setNews(News news) {
            this.news = news;
        }

        private static class News {
            public article[] articles;

            public article[] getArticles() {
                return articles;
            }

            public void setArticles(article[] articles) {
                this.articles = articles;
            }

            public static class article {
                public String title;
                public String description;
                public String url;
                public String picurl;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getPicurl() {
                    return picurl;
                }

                public void setPicurl(String picurl) {
                    this.picurl = picurl;
                }
            }
        }
    }

    public static class MpnewsMessage extends Message {
        public Mpnews mpnews;

        public Mpnews getMpnews() {
            return mpnews;
        }

        public void setMpnews(Mpnews mpnews) {
            this.mpnews = mpnews;
        }

        private static class Mpnews {
            public String media_id;

            public String getMedia_id() {
                return media_id;
            }

            public void setMedia_id(String media_id) {
                this.media_id = media_id;
            }
        }
    }

    public static class WxcardMessage extends Message {
        public Wxcard wxcard;

        public Wxcard getWxcard() {
            return wxcard;
        }

        public void setWxcard(Wxcard wxcard) {
            this.wxcard = wxcard;
        }

        private static class Wxcard {
            public String card_id;
            public String card_ext;

            public String getCard_id() {
                return card_id;
            }

            public void setCard_id(String card_id) {
                this.card_id = card_id;
            }

            public String getCard_ext() {
                return card_ext;
            }

            public void setCard_ext(String card_ext) {
                this.card_ext = card_ext;
            }
        }
    }

    private static class UrlResult {
        public String url;
    }

    private static class ArticlesResult {
        public String type;
        public String media_id;
        public int created_at;
    }

    //上传图文消息素材
    private static class Articles {
        public static Article[] articles;

        private static class Article {
            public String thumb_media_id;
            public String author;
            public String title;
            public String content_source_url;
            public String content;
            public String digest;
            public int show_cover_pic;
        }
    }

    //根据分组进行群发【订阅号与服务号认证后均可用】
    public static class packet {
        public Filter filter;
        public String msgtype;

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }

        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public static class Filter {
            public Boolean is_to_all;
            public int group_id;

            public Boolean getIs_to_all() {
                return is_to_all;
            }

            public void setIs_to_all(Boolean is_to_all) {
                this.is_to_all = is_to_all;
            }

            public int getGroup_id() {
                return group_id;
            }

            public void setGroup_id(int group_id) {
                this.group_id = group_id;
            }
        }


    }

    static class MenuBase {
        private String type;
        private String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ClickMenu extends MenuBase {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class LinkMenu extends MenuBase {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class MenuContainer extends MenuBase {
        private MenuBase[] sub_button;

        public MenuBase[] getSub_button() {
            return sub_button;
        }

        public void setSub_button(MenuBase[] sub_button) {
            this.sub_button = sub_button;
        }
    }

    public static class RootMenu {
        private MenuBase[] button;

        public MenuBase[] getButton() {
            return button;
        }

        public void setButton(MenuBase[] button) {
            this.button = button;
        }
    }
}