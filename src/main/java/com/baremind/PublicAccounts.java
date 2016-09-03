package com.baremind;

import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;

/**
 * Created by fixopen on 2/9/2016.
 */
@Path("public-account")
public class PublicAccounts {
    static String hostname = "https://api.weixin.qq.com";
    private static String accessToken = "";
    static String AppID = "wx92dec5e98645bd1d";
    static String AppSecret = "d3b30c3ae79c322bc54c93d0ff75210b";

    public static void setAccessToken(String token) {
        accessToken = token;
    }

    public static String getAccessToken() {
        return accessToken;
    }

    public static class AccessToken {
        private String access_token;
        private int expires_in;
    }

    //获取微信服务器ID
    //public static

    //获取接口调用凭证
    public static void queryToken() {
        //http请求方式: GET
        //https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
            .path("/cgi-bin/token")
            .queryParam("grant_type", "client_credential")
            .queryParam("appid", AppID)
            .queryParam("secret", AppSecret)
            .request().get();
        String responseBody = response.readEntity(String.class);
        if (responseBody.contains("access_token")) {
            //{"access_token":"ACCESS_TOKEN","expires_in":7200}
            AccessToken t = new Gson().fromJson(responseBody, AccessToken.class);
            accessToken = t.access_token;
        }
    }

    public static void prepare() {
        if (accessToken.equals("")) {
            queryToken();
        }
    }

    public static class UserList {
        public static class UserData {
            private String[] openid;
        }

        private int total;
        private int count;
        private UserData data;
        private String next_openid;
    }

    //获取接口调用凭证
    public static String[] getUserList(String nextOpenid) {
        prepare();
        // http请求方式: GET（请使用https协议）
        // https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID
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

    public static class WechatUserInfo {
        private int subscribe;
        private String openid;
        private String nickname;
        private Long sex;
        private String language;
        private String city;
        private String province;
        private String country;
        private String headimgurl;
        private int subscribe_time;
        private String unionid;
        private String remark;
        private int groupid;
    }

    public static WechatUserInfo getUserInfo(String openId) {
        prepare();
        // http请求方式: GET（请使用https协议）
        //https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
        WechatUserInfo result = null;
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
            .path("/cgi-bin/user/info")
            .queryParam("access_token", accessToken)
            .queryParam("openid", openId)
            .queryParam("lang", "zh_CN")
            .request().get();
        /*
        {
            "subscribe": 1,
            "openid": "o6_bmjrPTlm6_2sgVt7hMZOPfL2M",
            "nickname": "Band",
            "sex": 1,
            "language": "zh_CN",
            "city": "广州",
            "province": "广东",
            "country": "中国",
            "headimgurl":    "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
           "subscribe_time": 1382694957,
           "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
           "remark": "",
           "groupid": 0
        }
        */
        String responseBody = response.readEntity(String.class);
        if (responseBody.contains("openid")) {
            //{"access_token":"ACCESS_TOKEN","expires_in":7200}
            result = new Gson().fromJson(responseBody, WechatUserInfo.class);
        }
        return result;
    }

    public static class GenericResult {
        public int errcode;
        public String errmsg;
    }

    public static class DelResult {
        public String menuid;
    }

    public static class IPList {
        private String[] ip_list;

        public String[] getIp_list() {
            return ip_list;
        }

        public void setIp_list(String[] ip_list) {
            this.ip_list = ip_list;
        }
    }

    public static class CustomerServerList {
        public static class Server {
            private String kf_account;
            private String kf_nick;
            private String kf_id;
            private String kf_headimgurl;
        }

        private Server[] kf_list;
    }

    //自定义菜单查询接口
    private GenericResult getWechatServerIpList() {
        prepare();
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
        prepare();
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
        prepare();
        //https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN
        //{"errcode":0,"errmsg":"ok"}
        //{"errcode":40018,"errmsg":"invalid button name size"}
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
            .path("/cgi-bin/menu/delete")
            .queryParam("access_token", accessToken)
            .request().delete();
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //创建个性化菜单
    private GenericResult createPersonalityMenu(Entity<?> menu) {
        prepare();
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
        prepare();
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


    //点击菜单拉取消息时的事件推送
    public static class MenuClickEvent {
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

    @POST
    @Path("click-menu")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clickMenu(@CookieParam("sessionId") String sessionId, JAXBElement<MenuClickEvent> clickEvent) {
        MenuClickEvent e = clickEvent.getValue();
        switch (e.EventKey) {
            case "MENU_DIRECT_PLAY":
                break;
            case "MENU_OWNER":
                break;
            default:
                break;
        }
        return null;
    }


    //点击菜单跳转链接时的事件推送
    public static class LinkClickEvent {

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

    @POST
    @Path("click-link-menu")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clickLinkMenu(@CookieParam("sessionId") String sessionId, JAXBElement<LinkClickEvent> clickLink) {
        //没有处理，记得要做处理
        return null;
    }

    //扫码推事件的事件推送

    public static class QRCodeScanEvent {

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

    @POST
    @Path("QR-code")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response scancodePush(@CookieParam("sessionId") String sessionId, JAXBElement<QRCodeScanEvent> QRCodeScanEvent) {
        //没有处理，记得要做处理
        return null;
    }

    //扫码推事件且弹出“消息接收中”提示框的事件推送
    @POST
    @Path("QR-code-wait-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response scancode_waitmsg(@CookieParam("sessionId") String sessionId, JAXBElement<QRCodeScanEvent> QRCodeScanEvent) {
        //没有处理，记得要做处理
        return null;
    }

    //弹出系统拍照发图的事件推送

    public static class TakePhotoEvent {

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

    @POST
    @Path("photo")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pic_sysphoto(@CookieParam("sessionId") String sessionId, JAXBElement<TakePhotoEvent> takePhotoEvent) {
        //没有处理，记得要做处理
        return null;
    }

    //弹出拍照或者相册发图的事件推送
    @POST
    @Path("photo-or-album")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pic_photo_or_album(@CookieParam("sessionId") String sessionId, JAXBElement<TakePhotoEvent> takePhotoEvent) {
        //没有处理，记得要做处理
        return null;
    }

    //：弹出微信相册发图器的事件推送
    @POST
    @Path("wechat-album")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pic_weixin(@CookieParam("sessionId") String sessionId, JAXBElement<TakePhotoEvent> takePhotoEvent) {
        //没有处理，记得要做处理
        return null;
    }

    // 弹出地理位置选择器的事件推送

    public static class LocationSelectEvent {

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

    @POST
    @Path("location-selector")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response location_select(@CookieParam("sessionId") String sessionId, JAXBElement<LocationSelectEvent> locationSelectEvent) {
        //没有处理，记得要做处理
        return null;
    }


    //测试个性化菜单匹配结果
    private GenericResult testPersonalityMenu(Entity<?> menu) {
        prepare();
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
        prepare();
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

    // 接受消息 ：文本消息
    public static class TextMessage {

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

    @POST
    @Path("text-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response textmessage(@CookieParam("sessionId") String sessionId, JAXBElement<TextMessage> textMessage) {
        //没有处理，记得要做处理
        return null;
    }

    // 接受消息 ：图片消息
    public static class PictureMessage {

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

    @POST
    @Path("picture-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response prcturemessage(@CookieParam("sessionId") String sessionId, JAXBElement<PictureMessage> pictureMessage) {
        //没有处理，记得要做处理
        return null;
    }

    // 接受消息 ：语音消息
    public static class VoiceMessage {

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

    @POST
    @Path("voice-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response voiceMeessage(@CookieParam("sessionId") String sessionId, JAXBElement<VoiceMessage> voiceMessage) {
        //没有处理，记得要做处理
        return null;
    }

    // 接受消息 ：视频消息
    public static class VideoMessage {
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

    @POST
    @Path("video-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response videoMessage(@CookieParam("sessionId") String sessionId, JAXBElement<VideoMessage> videoMessage) {
        //没有处理，记得要做处理
        return null;
    }


    // 接受消息 ：小视频消息
    @POST
    @Path("small-video-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response smallVideoMessage(@CookieParam("sessionId") String sessionId, JAXBElement<VideoMessage> videoMessage) {
        //没有处理，记得要做处理
        return null;
    }

    // 接受消息 ：地理位置消息

    public static class LocationMessage {

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


//    @POST
//    @Path("small-voice-message")
//    @Consumes(MediaType.APPLICATION_XML)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response smallVoiceMessage(@CookieParam("sessionId") String sessionId, JAXBElement<LocationMessage> locationMessage) {
//        //没有处理，记得要做处理
//        return null;
//    }

    // 接受消息 ：链接消息

    public static class LinkMessage {

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

    @POST
    @Path("link-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response smallVoiceMessage(@CookieParam("sessionId") String sessionId, JAXBElement<LinkMessage> linkMessage) {
        //没有处理，记得要做处理
        return null;
    }

    // 关注/取消关注事件
    public static class Follow {
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

    @POST
    @Path("follow")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response follow(@CookieParam("sessionId") String sessionId, JAXBElement<Follow> follow) {
        //没有处理，记得要做处理
        return null;
    }

    // 扫描带参数二维码事件
    //如果用户还未关注公众号，则用户可以关注公众号，关注后微信会将带场景值关注事件推送给开发者。
    public static class ScanEvent {
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

    @POST
    @Path("scan-code-claim")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response notconcerned(@CookieParam("sessionId") String sessionId, JAXBElement<ScanEvent> scanEvent) {
        //没有处理，记得要做处理
        return null;
    }

    // 扫描带参数二维码事件
    //如果用户已经关注公众号，则微信会将带场景值扫描事件推送给开发者。
    @POST
    @Path("scan-code")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response concerned(@CookieParam("sessionId") String sessionId, JAXBElement<ScanEvent> scanEvent) {
        //没有处理，记得要做处理
        return null;
    }

    // 上报地理位置事件
    public static class PositionEvent {
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

    @POST
    @Path("position")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response position(@CookieParam("sessionId") String sessionId, JAXBElement<PositionEvent> position) {
        //没有处理，记得要做处理
        //conflict to Jumplink
        return null;
    }

    // 自定义菜单事件
    public static class Menu {
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

    //回复文本消息
    public static class ReplyTextMessage {
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

    @POST
    @Path("return-text-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replytextmessage(@CookieParam("sessionId") String sessionId, ReplyTextMessage replyTextMessage) {
        //没有处理，记得要做处理
        return null;
    }

    //回复图片消息
    public static class ReplyPictureMessage {
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

    //回复视频消息
    public static class ReplyVideoMessage {
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

    @POST
    @Path("return-video-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replyvoidemessage(@CookieParam("sessionId") String sessionId, ReplyVideoMessage replyVoideMessage) {
        //没有处理，记得要做处理
        return null;
    }

    //回复音乐消息
    public static class ReplyMusicMessage {
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

    @POST
    @Path("return-music-message")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response replymusicmessage(@CookieParam("sessionId") String sessionId, ReplyMusicMessage replyMusicMessage) {
        //没有处理，记得要做处理
        return null;
    }

    //回复图文消息
    public static class ReplyImageTextMessage {
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
        prepare();
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
        prepare();
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
        prepare();
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
        prepare();
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

    //获取所有客服账号
    public static class CustomerServers {
        public Server[] kf_list;

        public static class Server {
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

        public Server[] getKf_list() {
            return kf_list;
        }

        public void setKf_list(Server[] kf_list) {
            this.kf_list = kf_list;
        }
    }

    private CustomerServers getCustomerServers() {
        prepare();
        // http请求方式: GET
        // https://api.weixin.qq.com/cgi-bin/customservice/getkflist?access_token=ACCESS_TOKEN
        Client client = ClientBuilder.newClient();
        Response response = client.target(hostname)
            .path("/cgi-bin/customservice/getkflist")
            .queryParam("access_token", accessToken)
            .request().get();
        String responseBody = response.readEntity(String.class);
        GenericResult r = null;
        return null;
    }

    //客服接口-发消息
    public static class Message {
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
        public static class Text {
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }

        private Text text;

        public Text getText() {
            return text;
        }

        public void setText(Text text) {
            this.text = text;
        }
    }

    public static class PicturesMessage extends Message {
        private Image image;

        private static class Image {
            private String media_id;

            public String getMedia_id() {
                return media_id;
            }

            public void setMedia_id(String media_id) {
                this.media_id = media_id;
            }
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }
    }


    public static class VoicesMessage extends Message {
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

        public Music getMusic() {
            return music;
        }

        public void setMusic(Music music) {
            this.music = music;
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


    private GenericResult send(Entity<? extends Message> message) {
        prepare();
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

    public static class UrlResult {
        public String url;
    }

    //上传图文消息内的图片获取URL【订阅号与服务号认证后均可用】
    private UrlResult uploadimg(Entity<?> message) {
        prepare();
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

    public static class ArticlesResult {
        public String type;
        public String media_id;
        public int created_at;
    }

    //上传图文消息素材
    public static class Articles {
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

    private ArticlesResult Uploadgraphicsandtextmessagematerial(Entity<Articles> articles) {
        prepare();
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

    //根据分组进行群发【订阅号与服务号认证后均可用】
    public static class Packet {
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

    private ArticlesResult Packetgroup(Entity<Articles> articles) {
        prepare();
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

    //private GenericResult createCustomMenu(Entity<?> menu) {
        //POST https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN
        /*
        参数 	是否必须 	说明
        button 	是 	一级菜单数组，个数应为1~3个
        sub_button 	否 	二级菜单数组，个数应为1~5个
        type 	是 	菜单的响应动作类型
        name 	是 	菜单标题，不超过16个字节，子菜单不超过40个字节
        key 	click等点击类型必须 	菜单KEY值，用于消息接口推送，不超过128字节
        url 	view类型必须 	网页链接，用户点击菜单可打开链接，不超过1024字节
        media_id 	media_id类型和view_limited类型必须 	调用新增永久素材接口返回的合法media_id
        */
        /*
        1、click：点击推事件
        用户点击click类型按钮后，微信服务器会通过消息接口推送消息类型为event	的结构给开发者（参考消息接口指南），并且带上按钮中开发者填写的key值，开发者可以通过自定义的key值与用户进行交互；
        2、view：跳转URL
        用户点击view类型按钮后，微信客户端将会打开开发者在按钮中填写的网页URL，可与网页授权获取用户基本信息接口结合，获得用户基本信息。
        3、scancode_push：扫码推事件
        用户点击按钮后，微信客户端将调起扫一扫工具，完成扫码操作后显示扫描结果（如果是URL，将进入URL），且会将扫码的结果传给开发者，开发者可以下发消息。
        4、scancode_waitmsg：扫码推事件且弹出“消息接收中”提示框
        用户点击按钮后，微信客户端将调起扫一扫工具，完成扫码操作后，将扫码的结果传给开发者，同时收起扫一扫工具，然后弹出“消息接收中”提示框，随后可能会收到开发者下发的消息。
        5、pic_sysphoto：弹出系统拍照发图
        用户点击按钮后，微信客户端将调起系统相机，完成拍照操作后，会将拍摄的相片发送给开发者，并推送事件给开发者，同时收起系统相机，随后可能会收到开发者下发的消息。
        6、pic_photo_or_album：弹出拍照或者相册发图
        用户点击按钮后，微信客户端将弹出选择器供用户选择“拍照”或者“从手机相册选择”。用户选择后即走其他两种流程。
        7、pic_weixin：弹出微信相册发图器
        用户点击按钮后，微信客户端将调起微信相册，完成选择操作后，将选择的相片发送给开发者的服务器，并推送事件给开发者，同时收起相册，随后可能会收到开发者下发的消息。
        8、location_select：弹出地理位置选择器
        用户点击按钮后，微信客户端将调起地理位置选择工具，完成选择操作后，将选择的地理位置发送给开发者的服务器，同时收起位置选择工具，随后可能会收到开发者下发的消息。
        9、media_id：下发消息（除文本消息）
        用户点击media_id类型按钮后，微信服务器会将开发者填写的永久素材id对应的素材下发给用户，永久素材类型可以是图片、音频、视频、图文消息。请注意：永久素材id必须是在“素材管理/新增永久素材”接口上传后获得的合法id。
        10、view_limited：跳转图文消息URL
        用户点击view_limited类型按钮后，微信客户端将打开开发者在按钮中填写的永久素材id对应的图文消息URL，永久素材类型只支持图文消息。请注意：永久素材id必须是在“素材管理/新增永久素材”接口上传后获得的合法id。
        */
        /*
        {
             "button":[
             {
                  "type":"click",
                  "name":"今日歌曲",
                  "key":"V1001_TODAY_MUSIC"
              },
              {
                   "name":"菜单",
                   "sub_button":[
                   {
                       "type":"view",
                       "name":"搜索",
                       "url":"http://www.soso.com/"
                    },
                    {
                       "type":"view",
                       "name":"视频",
                       "url":"http://v.qq.com/"
                    },
                    {
                       "type":"click",
                       "name":"赞一下我们",
                       "key":"V1001_GOOD"
                    }]
               }]
         }
         */
         /*
         {
            "button": [
                {
                    "name": "扫码",
                    "sub_button": [
                        {
                            "type": "scancode_waitmsg",
                            "name": "扫码带提示",
                            "key": "rselfmenu_0_0",
                            "sub_button": [ ]
                        },
                        {
                            "type": "scancode_push",
                            "name": "扫码推事件",
                            "key": "rselfmenu_0_1",
                            "sub_button": [ ]
                        }
                    ]
                },
                {
                    "name": "发图",
                    "sub_button": [
                        {
                            "type": "pic_sysphoto",
                            "name": "系统拍照发图",
                            "key": "rselfmenu_1_0",
                           "sub_button": [ ]
                         },
                        {
                            "type": "pic_photo_or_album",
                            "name": "拍照或者相册发图",
                            "key": "rselfmenu_1_1",
                            "sub_button": [ ]
                        },
                        {
                            "type": "pic_weixin",
                            "name": "微信相册发图",
                            "key": "rselfmenu_1_2",
                            "sub_button": [ ]
                        }
                    ]
                },
                {
                    "name": "发送位置",
                    "type": "location_select",
                    "key": "rselfmenu_2_0"
                },
                {
                   "type": "media_id",
                   "name": "图片",
                   "media_id": "MEDIA_ID1"
                },
                {
                   "type": "view_limited",
                   "name": "图文消息",
                   "media_id": "MEDIA_ID2"
                }
            ]
        }
        */
        //{"errcode":0,"errmsg":"ok"}
        //{"errcode":40018,"errmsg":"invalid button name size"}
//        Client client = ClientBuilder.newClient();
//		Response response = client.target(hostname)
//			.path("/cgi-bin/menu/create")
//			.queryParam("access_token", accessToken)
//			.request("text/plain").post(menu);
//		String responseBody = response.readEntity(String.class);
//		GenericResult r = null;
//        return null;
//    }

}
