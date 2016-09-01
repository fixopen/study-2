package com.baremind;

import com.baremind.data.User;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Path("users")
public class Users {
    static String hostname = "http://222.73.117.158";
    static String username = "jiekou-clcs-13";
    static String password = "THYnk464hu";

    static void queryBalance() {
        //http://IP:PORT/msg/QueryBalance?account=a&pswd=p
        
//        return body
//        20130303180000,0
//        1234567,1000
//        1234531,2000
//        0 提交成功
//        101 无此用户
//        102 密码错
//        103 查询过快（10秒查询一次）
//        
    }

    public static class SendMessageResult {
        public String time;
        public String code;
        public String messageId;
    }

    public static SendMessageResult sendMessage(String telephoneNumber, String validInfo) {
        //platform: http://222.73.117.158/msg/index.jsp
        //username: jiekou-clcs-13
        //password: THYnk464hu
        //http://222.73.117.158:80/msg/HttpBatchSendSM?account=a&pswd=p&mobile=m&msg=m&needstatus=true

        //resptime,respstatus
        //msgid
//        respstatus
//        * 代码 说明
//        0 提交成功
//        101 无此用户
//        102 密码错
//        103 提交过快（提交速度超过流速限制）
//        104 系统忙（因平台侧原因，暂时无法处理提交的短信）
//        105 敏感短信（短信内容包含敏感词）
//        106 消息长度错（>536或<=0）
//        107 包含错误的手机号码
//        108 手机号码个数错（群发>50000或<=0;单发>200或<=0）
//        109 无发送额度（该用户可用短信数已使用完）
//        110 不在发送时间内
//        111 超出该账户当月发送额度限制
//        112 无此产品，用户没有订购该产品
//        113 extno格式错（非数字或者长度不对）
//        115 自动审核驳回
//        116 签名不合法，未带签名（用户必须带签名的前提下）
//        117 IP地址认证错,请求调用的IP地址不是系统登记的IP地址
//        118 用户没有相应的发送权限
//        119 用户已过期
//        120 测试内容不是白名单
        

        SendMessageResult result = new SendMessageResult();
        // Default instance of client
        Client client = ClientBuilder.newClient();
        // + "HttpBatchSendSM?account=" + username + "&pswd=" + password + "&mobile=" + telephoneNumber + "&msg=" + validInfo + "&needstatus=true"
        Response response = client.target(hostname)
            .path("/msg/HttpBatchSendSM")
            .queryParam("account", username)
            .queryParam("pswd", password)
            .queryParam("mobile", telephoneNumber)
            .queryParam("msg", validInfo)
            .queryParam("needstatus", true)
            .request("text/plain").get();
        String responseBody = response.readEntity(String.class);
        if (responseBody.contains("\n")) {
            String[] lines = responseBody.split("\n");
            if (lines.length == 2) {
                String[] timeCode = lines[0].split(",");
                result.time = timeCode[0];
                result.code = timeCode[1];
            }
            result.messageId = lines[1];
        }
        return result;
    }

    public static class SmsState {
        public String receiver;
        public String password;
        public String messageId;
        public String reportTime;
        public String mobile;
        public String status;
    }

    @GET
    @Path("smsStateNotification")
    public SmsState smsStateNotification() {
        //http://pushUrl?receiver=admin&pswd=12345&msgid=12345&reportTime=1012241002&mobile=13900210021&status=DELIVRD
        
//        DELIVRD 短消息转发成功
//        EXPIRED 短消息超过有效期
//        UNDELIV 短消息是不可达的
//        UNKNOWN 未知短消息状态
//        REJECTD 短消息被短信中心拒绝
//        DTBLACK 目的号码是黑名单号码
//        ERR:104 系统忙
//        REJECT 审核驳回
//        其他 网关内部状态
//        
        return null;
    }

    public static class SmsReceiverState {
        public String receiver;
        public String password;
        public String message;
        public String moTime;
        public String mobile;
        public String destinationCode;
    }

    @GET
    @Path("smsReceiver")
    public SmsReceiverState smsReceiver() {
        //http://pushMoUrl?receiver=admin&pswd=12345&moTime=1208212205&mobile=13800210021&msg=hello&destcode=10657109012345
        return null;
    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@CookieParam("sessionId") String sessionId, User user) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            user.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(user);
            result = Response.ok(user).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<User> users = JPAEntry.getList(User.class, filterObject);
            if (!users.isEmpty()) {
                result = Response.ok(new Gson().toJson(users)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            User user = JPAEntry.getObject(User.class, "id", id);
            if (user != null) {
                result = Response.ok(new Gson().toJson(user)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, User user) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            User existuser = JPAEntry.getObject(User.class, "id", id);
            if (existuser != null) {
                String amount = user.getAmount();
                if (amount != null) {
                    existuser.setAmount(amount);
                }
                Date birthday = user.getBirthday();
                if (birthday != null) {
                    existuser.setBirthday(birthday);
                }
                String classname = user.getClassname();
                if (classname != null) {
                    existuser.setClassname(classname);
                }
                Date createTime = user.getCreateTime();
                if (createTime != null) {
                    existuser.setCreateTime(createTime);
                }
                String description = user.getDescription();
                if (description != null) {
                    existuser.setDescription(description);
                }
                String email = user.getEmail();
                if (email != null) {
                    existuser.setEmail(email);
                }
                String grade = user.getGrade();
                if (grade != null) {
                    existuser.setGrade(grade);
                }
                String head = user.getHead();
                if (head != null) {
                    existuser.setHead(head);
                }
                Boolean isAdministrator = user.getIsAdministrator();
                if (isAdministrator != null) {
                    existuser.setIsAdministrator(isAdministrator);
                }
                String location = user.getLocation();
                if (location != null) {
                    existuser.setLocation(location);
                }
                String loginName = user.getLoginName();
                if (loginName != null) {
                    existuser.setLoginName(loginName);
                }
                String name = user.getName();
                if (name != null) {
                    existuser.setName(name);
                }
                String password = user.getPassword();
                if (password != null) {
                    existuser.setPassword(password);
                }
                String school = user.getSchool();
                if (school != null) {
                    existuser.setSchool(school);
                }
                Long sex = user.getSex();
                if (sex != 0) {
                    existuser.setSex(sex);
                }
                String telephone = user.getTelephone();
                if (telephone != null) {
                    existuser.setTelephone(telephone);
                }
                String timezone = user.getTimezone();
                if (timezone != null) {
                    existuser.setTimezone(timezone);
                }
                Date updateTime = user.getUpdateTime();
                if (updateTime != null) {
                    existuser.setUpdateTime(updateTime);
                }
                int site = user.getSite();
                if (site != 0) {
                    existuser.getSite();
                }
                JPAEntry.genericPut(existuser);
                result = Response.ok(existuser).build();
            }
        }
        return result;
    }
}
