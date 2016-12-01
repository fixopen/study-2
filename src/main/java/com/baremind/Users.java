package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.BiConsumer;

import static com.baremind.utils.Impl.finalResult;
import static org.bouncycastle.asn1.x509.X509ObjectIdentifiers.id;

@Path("users")
public class Users {
    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.getById(sessionId, id, User.class, null);
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("self")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.getUserSelf(sessionId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, User entity) {
        Response result = Response.status(401).build();
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.create(sessionId, entity, null);
            }
        }
        return result;
    }

    @POST
    @Path("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(User entity) { //admin bootstrap
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        JPAEntry.genericPost(entity);
        return finalResult(entity, null);
    }

    private static class Updater implements BiConsumer<User, User> {
        @Override
        public void accept(User existUser, User userData) {
            Long amount = userData.getAmount();
            if (amount != null) {
                existUser.setAmount(amount);
            }

            Date birthday = userData.getBirthday();
            if (birthday != null) {
                existUser.setBirthday(birthday);
            }
            String classname = userData.getClassname();
            if (classname != null) {
                existUser.setClassname(classname);
            }
            String description = userData.getDescription();
            if (description != null) {
                existUser.setDescription(description);
            }
            String email = userData.getEmail();
            if (email != null) {
                existUser.setEmail(email);
            }
            String grade = userData.getGrade();
            if (grade != null) {
                existUser.setGrade(grade);
            }
            String head = userData.getHead();
            if (head != null) {
                existUser.setHead(head);
            }
            Boolean isAdministrator = userData.getIsAdministrator();
            if (isAdministrator != null) {
                existUser.setIsAdministrator(isAdministrator);
            }
            String location = userData.getAddress();
            if (location != null) {
                existUser.setAddress(location);
            }
            String loginName = userData.getLoginName();
            if (loginName != null) {
                existUser.setLoginName(loginName);
            }
            String name = userData.getName();
            if (name != null) {
                existUser.setName(name);
            }
            String password = userData.getPassword();
            if (password != null) {
                existUser.setPassword(password);
            }
            String school = userData.getSchool();
            if (school != null) {
                existUser.setSchool(school);
            }
            Integer sex = userData.getSex();
            if (sex != null) {
                existUser.setSex(sex);
            }
            String telephone = userData.getTelephone();
            if (telephone != null) {
                existUser.setTelephone(telephone);
            }
            String timezone = userData.getTimezone();
            if (timezone != null) {
                existUser.setTimezone(timezone);
            }
            Date now = new Date();
            existUser.setUpdateTime(now);
            String site = userData.getSite();
            if (site != null) {
                existUser.getSite();
            }
        }
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, User newData) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.updateById(sessionId, id, newData, User.class, new Updater(), null);
            }
        }
        return result;
    }

    @PUT //根据token修改
    @Path("self")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSelf(@CookieParam("sessionId") String sessionId, User newData) {
        return Impl.updateUserSelf(sessionId, newData, new Updater());
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.deleteById(sessionId, id, User.class);
            }
        }
        return result;
    }

    private static class Notice {
        private String objectType; //被消费的类型（知识点、直播等等）
        private Long objectId; //被消费的类型（知识点、直播等等）
        private String subjectType;//要消费的类型(学习卡、账户)
        private Long subjectId;//学习卡的Id
        private String to;//转入卡或者账户
       // private Transfer[] from;//把账户、卡、多张卡的金额转出
        private String from;//把账户、卡、多张卡的金额转出

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public Long getObjectId() {
            return objectId;
        }

        public void setObjectId(Long objectId) {
            this.objectId = objectId;
        }

        /*public Transfer[] getFrom() {
            return from;
        }

        public void setFrom(Transfer[] from) {
            this.from = from;
        }*/

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        private static class Transfer {
            private Long id;//学习卡的Id
            private String amount;//要消费的金额

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }
        }

        public String getObjectType() {
            return objectType;
        }

        public void setObjectType(String objectType) {
            this.objectType = objectType;
        }

        public String getSubjectType() {
            return subjectType;
        }

        public void setSubjectType(String subjectType) {
            this.subjectType = subjectType;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
        }


    }

    @PUT
    @Path("notice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response noticeUser(@CookieParam("sessionId") String sessionId, Notice notice) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Map noticeMap = new HashMap();
            String type = notice.objectType;
            User user = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            noticeMap.put("amount", amount(type,notice.objectId));
            noticeMap.put("account", user.getAmount());
            List<Card> cards = JPAEntry.getList(Card.class, "userId", JPAEntry.getLoginId(sessionId));
            noticeMap.put("cards", cards);

            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = Response.ok(gson.toJson(noticeMap)).build();
        }
        return result;
    }


    //得到应该消费的金额
    private Long amount(String type,Long objectId) {
        Long amount = null;
        switch (type) {
            case "knowledgePoint":
                KnowledgePoint id = JPAEntry.getObject(KnowledgePoint.class, "id", objectId);
                amount = id.getPrice();
                break;
            case "scheduler":
                Scheduler scheduler = JPAEntry.getObject(Scheduler.class, "id", objectId);
                amount =scheduler.getPrice();
        }
        return amount;
    }

    //扣除
    private String deductionAmount(Long id, Long amount, String type) {
        String accountAmountStruts = "";
        if (type == "account") {
            User user = JPAEntry.getObject(User.class, "id", id);//得到账户
            Long userAmount = user.getAmount();//得到账户余额
            Long accountAmount = userAmount - amount;

            if (accountAmount > 0) {
                //成功
                user.setAmount(accountAmount);
                JPAEntry.genericPut(user);
                accountAmountStruts = "200";
            } else {
                //失败账户余额不足
                accountAmountStruts = "404";
            }
        } else {
            Card card = JPAEntry.getObject(Card.class, "id", id);//得到指定卡
            Long cardAmount = card.getAmount();//得到指定卡余额
            Long cardsAmount = cardAmount - amount;
            if (cardsAmount > 0) {
                //成功
                card.setAmount(cardsAmount);
                JPAEntry.genericPut(card);
                accountAmountStruts = "200";
            } else {
                //失败学习卡余额不足
                accountAmountStruts = "405";
            }
        }

        return accountAmountStruts;
    }

    //转入/转出
    @PUT
    @Path("transfer/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transfertransfer(@CookieParam("sessionId") String sessionId, Notice notice) {
        Response result = Response.status(401).build();
//        Notice.Transfer[] from = notice.from;
        String from = notice.from;
        String to = notice.to;
        Long id = Long.valueOf(to);
        if (JPAEntry.isLogining(sessionId)) {
            //for (int i = 0; i < from.length; i++) {     //对要转出的数据进行循环
                Card tocard = JPAEntry.getObject(Card.class, "id", id);//根据指定卡id得到指定卡的数据
                User user = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));//根据id得到账户
                Integer formamount = Integer.valueOf(from);//要转出的金额

                Long userAmount = user.getAmount();//账户余额
                Long tocardAmount;
                if(tocard.getAmount() == null){
                    tocardAmount = Long.valueOf(0);
                }else{
                    tocardAmount = tocard.getAmount();//指定卡余额
                }

            if (formamount > userAmount) {//转出金额大于账户余额
                result = Response.status(408).build();
            } else {
                user.setAmount(userAmount - formamount);//账户余额 = 原余额 - 转出的钱
                JPAEntry.genericPut(user);
                tocard.setAmount(tocardAmount + formamount);//指定卡余额 = 原卡余额+转入的钱
                JPAEntry.genericPut(tocard);
                result = Response.ok().build();
            }
        }
        return result;
    }
//                if (from[i].getId() == null) {//转出账户的金额 && 装入到指定卡金额

//            }
       // }



      /*  if (to == "account") {//把账户的钱给账户（不支持） && 把转出卡的钱给账户
            for (int i = 0; i < from.length; i++) {
                if (from[i].getId() == null) {//把账户里面钱发给账户
                    System.out.println("把账户里面钱发给账户，");
                } else {
                    Card card = JPAEntry.getObject(Card.class, "id", from[i].getId());//根据转出卡的ID得到转出卡的数据
                    User user = JPAEntry.getObject(User.class, "id", id);//需要转入的账户
                    Integer formamount = Integer.valueOf(from[i].getAmount());//转出的金额
                    Integer cardAmount = card.getAmount();//转出卡余额
                    if (formamount > cardAmount) {//转出金额 > 转出卡余额
                        Struts = "408";
                        break;
                    } else {
                        card.setAmount(cardAmount - formamount);//卡余额 = 原卡余额 - 转出的钱
                        JPAEntry.genericPut(card);

                        user.setAmount(formamount + user.getAmount());//账户余额 = 原余额 + 转入的钱
                        JPAEntry.genericPut(user);
                    }

                }
            }
        } else {//把账户的钱给指定卡 || 把转出卡的钱给指定卡*/

//                Card card = JPAEntry.getObject(Card.class, "id", from[i].getId());//根据转出卡的ID得到转出卡的数据

//                Integer cardAmount = card.getAmount();//卡余额
                /*else { //转出学习卡的金额 && 转入到指定学习卡
                    if (formamount > cardAmount) {//转出金额大于学习卡余额
                        Struts = "410";
                        break;
                    } else {
                        card.setAmount(cardAmount - formamount);//卡余额 = 原卡余额- 转出的钱
                        JPAEntry.genericPut(card);
                        tocard.setAmount(formamount + tocardAmount);//指定卡余额 = 原余额 + 转入的钱
                        JPAEntry.genericPut(tocard);
                    }

                }*/

//        }


    @PUT
    @Path("deductMoney")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String DeductMoneyUser(@CookieParam("sessionId") String sessionId, Notice notice) {
        String AmountStruts = "";
        if (JPAEntry.isLogining(sessionId)) {
            String type = notice.objectType;//要消费那些类型
            Long amount = amount(type,notice.objectId);//根据消费类型得到消费金额
            if (notice.subjectId == null) {//扣除账户余额
                AmountStruts = deductionAmount(JPAEntry.getLoginId(sessionId), amount, "account");
            } else {//扣除指定卡余额
                AmountStruts = deductionAmount(notice.getSubjectId(), amount, "card");
            }

       /*     Scheduler scheduler = JPAEntry.getObject(Scheduler.class, "id",  notice.objectId);//得到账户
            String playbackLink = "";
            String directSeedingLink = "";

            if(scheduler.getContentLink() != null){
                playbackLink = scheduler.getContentLink();
            }else{
                 directSeedingLink = scheduler.getDirectLink();
            }*/
            /*if(AmountStruts == "200"){
               if(notice.objectType == "directSeeding"){
                  return directSeedingLink;
               }
                if(notice.objectType == "playback"){
                   return playbackLink;
                }
                if(notice.objectType == "knowledgePoint"){
                    KnowledgePoint knowledgePoint = JPAEntry.getObject(KnowledgePoint.class, "id", notice.objectId);//得到账户
                    //return knowledgePoint;
                }
            }*/
        }
        return AmountStruts;
    }


    @DELETE
    @Path("self")
    public Response deleteSelf(@CookieParam("sessionId") String sessionId) {
        return Impl.deleteUserSelf(sessionId);
    }

    @GET
    @Path("telephones/{telephone}/code")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryValidCode(@PathParam("telephone") String telephone) {
        Response result = Response.ok("{\"state\":\"ok\"}").build();
        Random rand = new Random();
        int x = rand.nextInt(899999);
        int y = x + 100000;
        String sjs = String.valueOf(y);
        Date now = new Date();
        ValidationCode message = new ValidationCode();
        message.setId(IdGenerator.getNewId());
        message.setPhoneNumber(telephone);
        message.setValidCode(sjs);
        message.setTimestamp(now);
        JPAEntry.genericPost(message);
        SendMessageResult r = sendMessage(telephone, "《小雨知时》" + sjs + "(动态验证码),请在3分钟内使用");
        if (r.messageId == null) {
            switch (r.code) {
                case "0": //提交成功
                    break;
                case "101": //无此用户
                    result = Response.status(523).entity("{\"state\":\"无此用户\"}").build();
                    break;
                case "102": //密码错
                    result = Response.status(523).entity("{\"state\":\"密码错\"}").build();
                    break;
                case "103": //提交过快（提交速度超过流速限制）
                    result = Response.status(523).entity("{\"state\":\"提交过快（提交速度超过流速限制）\"}").build();
                    break;
                case "104": //系统忙（因平台侧原因，暂时无法处理提交的短信）
                    result = Response.status(523).entity("{\"state\":\"系统忙（因平台侧原因，暂时无法处理提交的短信）\"}").build();
                    break;
                case "105": //敏感短信（短信内容包含敏感词）
                    result = Response.status(523).entity("{\"state\":\"敏感短信（短信内容包含敏感词)\"}").build();
                    break;
                case "106": //消息长度错（>536或<=0）
                    result = Response.status(523).entity("{\"state\":\"消息长度错（>536或<=0）\"}").build();
                    break;
                case "107": //包含错误的手机号码
                    result = Response.status(523).entity("{\"state\":\"包含错误的手机号码\"}").build();
                    break;
                case "108": //手机号码个数错（群发>50000或<=0;单发>200或<=0）
                    result = Response.status(523).entity("{\"state\":\"手机号码个数错（群发>50000或<=0;单发>200或<=0）\"}").build();
                    break;
                case "109": //无发送额度（该用户可用短信数已使用完）
                    result = Response.status(523).entity("{\"state\":\"无发送额度（该用户可用短信数已使用完）\"}").build();
                    break;
                case "110": //不在发送时间内
                    result = Response.status(523).entity("{\"state\":\"不在发送时间内\"}").build();
                    break;
                case "111": //超出该账户当月发送额度限制
                    result = Response.status(523).entity("{\"state\":\"超出该账户当月发送额度限制\"}").build();
                    break;
                case "112": //无此产品，用户没有订购该产品
                    result = Response.status(523).entity("{\"state\":\"无此产品，用户没有订购该产品\"}").build();
                    break;
                case "113": //extno格式错（非数字或者长度不对）
                    result = Response.status(523).entity("{\"state\":\"extno格式错（非数字或者长度不对）\"}").build();
                    break;
                case "115": //自动审核驳回
                    result = Response.status(523).entity("{\"state\":\"自动审核驳回\"}").build();
                    break;
                case "116": //签名不合法，未带签名（用户必须带签名的前提下）
                    result = Response.status(523).entity("{\"state\":\"签名不合法，未带签名（用户必须带签名的前提下）\"}").build();
                    break;
                case "117": //IP地址认证错,请求调用的IP地址不是系统登记的IP地址
                    result = Response.status(523).entity("{\"state\":\"IP地址认证错,请求调用的IP地址不是系统登记的IP地址\"}").build();
                    break;
                case "118": //用户没有相应的发送权限
                    result = Response.status(523).entity("{\"state\":\"用户没有相应的发送权限\"}").build();
                    break;
                case "119": //用户已过期
                    result = Response.status(523).entity("{\"state\":\"用户已过期\"}").build();
                    break;
                case "120": //测试内容不是白名单
                    result = Response.status(523).entity("{\"state\":\"测试内容不是白名单\"}").build();
                    break;
            }
        } else {
            result = Response.ok("{\"messageId\": \"" + r.messageId + "\"}").build();
        }
        return result;
    }

    private static class SendMessageResult {
        private String time;
        private String code;
        private String messageId;
    }

    private static SendMessageResult sendMessage(String phoneNumber, String validInfo) {
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
        String hostname = "https://sapi.253.com";
        String username = "zhibo1";
        String password = "Tch243450";
        Response response = client.target(hostname)
                .path("/msg/HttpBatchSendSM")
                .queryParam("account", username)
                .queryParam("pswd", password)
                .queryParam("mobile", phoneNumber)
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
        } else {
            String[] timeCode = responseBody.split(",");
            result.time = timeCode[0];
            result.code = timeCode[1];
        }
        return result;
    }

    private static class ActiveCard {
        private String cardNo;
        private String password;
        private String phoneNumber;
        private String validCode;

        String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardCode) {
            this.cardNo = cardCode;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        String getValidCode() {
            return validCode;
        }

        public void setValidCode(String validCode) {
            this.validCode = validCode;
        }
    }

    @GET
    @Path("{id}/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCards(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(404).build();
        User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
        if (admin != null && admin.getIsAdministrator()) {
            List<Card> cards = JPAEntry.getList(Card.class, "userId", id);
            if (!cards.isEmpty()) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                //Gson gson = new GsonBuilder().registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter()).create();
                // result = Response.ok(gson.toJson(scheduler)).build();
                result = Response.ok(gson.toJson(cards)).build();
            }
        }
        return result;
    }

    @GET
    @Path("self/cards")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfCards(@CookieParam("sessionId") String sessionId) {
        Response result = Response.status(404).build();
        List<Card> cards = JPAEntry.getList(Card.class, "userId", JPAEntry.getLoginId(sessionId));
        if (!cards.isEmpty()) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = Response.ok(gson.toJson(cards)).build();
        }
        return result;
    }

    private Response activeCardImpl(Long id, ActiveCard ac) {
        Response result = Response.status(412).build();
        User user = JPAEntry.getObject(User.class, "id", id);
        if (user != null) {
            //Logs.insert(id, "log", logId, "user exist");
            Map<String, Object> validCodeConditions = new HashMap<>();
            String phoneNumber = ac.getPhoneNumber();
            //Logs.insert(id, "log", logId, "create map");
            validCodeConditions.put("phoneNumber", phoneNumber);
            //Logs.insert(id, "log", logId, "put phone number");
            validCodeConditions.put("validCode", ac.getValidCode());
            //Logs.insert(id, "log", logId, "start query validation_codes table");
            List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, validCodeConditions);
            //Logs.insert(id, "log", logId, "end query validation_codes table");
            switch (validationCodes.size()) {
                case 0:
                    //Logs.insert(id, "log", logId, "validation code not exist");
                    result = Response.status(401).build();
                    break;
                case 1:
                    //Logs.insert(id, "log", logId, "validation code exist");
                    Date now = new Date();
                    Date sendTime = validationCodes.get(0).getTimestamp();
                    if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                        //Logs.insert(id, "log", logId, "validation code success");
                        Map<String, Object> cardConditions = new HashMap<>();
                        cardConditions.put("no", ac.getCardNo());
                        cardConditions.put("password", ac.getPassword());
                        List<Card> cs = JPAEntry.getList(Card.class, cardConditions);
                        switch (cs.size()) {
                            case 0:
                                //Logs.insert(id, "log", logId, "card not exists");
                                result = Response.status(404).build();
                                break;
                            case 1:
                                //Logs.insert(id, "log", logId, "card exists");
                                User telephoneUser = JPAEntry.getObject(User.class, "telephone", phoneNumber);
                                if (telephoneUser != null && telephoneUser.getId().longValue() != id.longValue()) {
                                    WechatUser wechatUser = JPAEntry.getObject(WechatUser.class, "userId", id);
                                    WechatUser errorWechatUser = JPAEntry.getObject(WechatUser.class, "userId", telephoneUser.getId());
                                    if (wechatUser.getOpenId().equals(errorWechatUser.getOpenId())) {
                                        List<Card> bindedCards = JPAEntry.getList(Card.class, "userId", id);
                                        EntityManager em = JPAEntry.getNewEntityManager();
                                        em.getTransaction().begin();
                                        for (Card card : bindedCards) {
                                            card.setUserId(id);
                                            em.merge(card);
                                        }
                                        em.remove(errorWechatUser);
                                        em.remove(telephoneUser);
                                        em.getTransaction().commit();
                                        em.close();
                                        Logs.insert(telephoneUser.getId(), "move-card", telephoneUser.getId(), phoneNumber);
                                    }
                                }
                                user.setTelephone(phoneNumber);
                                JPAEntry.genericPut(user);
                                Card c = cs.get(0);
                                if (c.getActiveTime() == null) {
                                    //Logs.insert(id, "log", logId, "card success");
                                    c.setActiveTime(now);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(now);
                                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                                    Date oneYearAfter = cal.getTime();
                                    c.setEndTime(oneYearAfter);
                                    c.setAmount(588L);
                                    c.setUserId(id);
                                    JPAEntry.genericPut(c);
                                    result = Response.ok(c).build();
                                } else {
                                    //Logs.insert(id, "log", logId, "card already active");
                                    result = Response.status(405).build();
                                }
                                break;
                            default:
                                //Logs.insert(id, "log", logId, "card multiple exists");
                                break;
                        }
                    } else {
                        //Logs.insert(id, "log", logId, "validation code timeout");
                        result = Response.status(410).build();
                    }
                    break;
                default:
                    //Logs.insert(id, "log", logId, "validation code multiple exist");
                    result = Response.status(520).build();
                    break;
            }
            //JPAEntry.genericDelete(ValidationCode.class, "phoneNumber", phoneNumber);
            //Logs.insert(id, "log", logId, "remove validation codes");
        }
        return result;
    }

    @POST
    @Path("{id}/cards")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeCard(@PathParam("id") Long id, ActiveCard ac) {
        //Random rand = new Random();
        //Long logId = rand.nextLong();
        //Logs.insert(id, "log", logId, "start");
        return activeCardImpl(id, ac);
    }

    @GET
    @Path("{sessionId}/user")
    public Long Transformation(@CookieParam("sessionId") String sessionId) {
        Long userId = JPAEntry.getLoginId(sessionId);
        return userId;
    }

    @POST
    @Path("self/cards")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeCard(@CookieParam("sessionId") String sessionId, ActiveCard ac) {
        //Random rand = new Random();
        //Long logId = rand.nextLong();
        //Logs.insert(id, "log", logId, "start");
        Long userId = JPAEntry.getLoginId(sessionId);
        return activeCardImpl(userId, ac);
    }

    //@@security weak
    @POST
    @Path("update/material")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryValidationStats(ActiveCard ac) {
        Response result = Response.status(412).build();
        Map<String, Object> validationCodeConditions = new HashMap<>();
        validationCodeConditions.put("phoneNumber", ac.getPhoneNumber());
        validationCodeConditions.put("validCode", ac.getValidCode());
        List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, validationCodeConditions);
        switch (validationCodes.size()) {
            case 0:
                result = Response.status(401).build();
                break;
            case 1:
                Date now = new Date();
                Date sendTime = validationCodes.get(0).getTimestamp();
                if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                    result = Response.ok().build();
                } else {
                    result = Response.status(405).build();
                }
                break;
        }
        return result;
    }

    @POST
    @Path("cards")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeCard(ActiveCard ac) {
        Response result = Response.status(412).build();
        Map<String, Object> validationCodeConditions = new HashMap<>();
        validationCodeConditions.put("phoneNumber", ac.getPhoneNumber());
        validationCodeConditions.put("validCode", ac.getValidCode());
        List<ValidationCode> validationCodes = JPAEntry.getList(ValidationCode.class, validationCodeConditions);
        switch (validationCodes.size()) {
            case 0:
                result = Response.status(401).build();
                break;
            case 1:
                Date now = new Date();
                Date sendTime = validationCodes.get(0).getTimestamp();
                if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                    Map<String, Object> condition = new HashMap<>();
                    condition.put("no", ac.getCardNo());
                    condition.put("password", ac.getPassword());
                    List<Card> cs = JPAEntry.getList(Card.class, condition);
                    switch (cs.size()) {
                        case 0:
                            result = Response.status(404).build();
                            break;
                        case 1:
                            Card c = cs.get(0);
                            if (c.getActiveTime() == null) {
                                c.setActiveTime(now);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(now);
                                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
                                Date oneYearAfter = cal.getTime();
                                c.setEndTime(oneYearAfter);
                                c.setAmount(588L);
                                User user = JPAEntry.getObject(User.class, "telephone", ac.getPhoneNumber());
                                if (user == null) {
                                    user = new User();
                                    user.setId(IdGenerator.getNewId());
                                    user.setTelephone(ac.getPhoneNumber());
                                    user.setLoginName(ac.getPhoneNumber());
                                    user.setCreateTime(now);
                                    user.setUpdateTime(now);
                                    user.setName("");
                                    user.setSex(0);
                                    JPAEntry.genericPost(user);
                                    c.setUserId(user.getId());
                                    JPAEntry.genericPut(c);
                                } else {
                                    c.setUserId(user.getId());
                                    JPAEntry.genericPut(c);
                                }
                                Session s = PublicAccounts.putSession(new Date(), user.getId(), 0L); //@@deviceId is temp zero
                                result = Response.ok(c)
                                        .cookie(new NewCookie("userId", user.getId().toString(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                                        .cookie(new NewCookie("sessionId", s.getIdentity(), "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false))
                                        .build();
                            } else {
                                result = Response.status(405).build();
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    result = Response.status(410).build();
                }
                break;
            default:
                result = Response.status(520).build();
                break;
        }
        return result;
    }

    static void queryBalance() {
        //http://IP:PORT/msg/QueryBalance?account=a&pswd=p

//        return body
//        20130303180000,0
//        102 密码错
//        103 查询过快（10秒查询一次）
//        
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

    @GET
    @Path("smsReceiver")
    public SmsReceiverState smsReceiver() {
        //http://pushMoUrl?receiver=admin&pswd=12345&moTime=1208212205&mobile=13800210021&msg=hello&destcode=10657109012345
        return null;
    }

    private static class SmsState {
        public String receiver;
        public String password;
        public String messageId;
        public String reportTime;
        public String mobile;
        public String status;
    }

    private static class SmsReceiverState {
        public String receiver;
        public String password;
        public String message;
        public String moTime;
        public String mobile;
        public String destinationCode;
    }
}
