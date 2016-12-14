package com.baremind;

import com.baremind.algorithm.Securities;
import com.baremind.data.Comment;
import com.baremind.data.Session;
import com.baremind.data.User;
import com.baremind.utils.Hex;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//import javax.websocket.Endpoint;

/**
 * Created by fixopen on 27/9/2016.
 */
@Path("class-rooms")
public class ClassRooms/* extends Endpoint*/ {
    private static Long classRoomId = 0L;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response join(@Context HttpServletRequest request) {
        User user = new User();
        Long userId = IdGenerator.getNewId();
        user.setId(userId);
        user.setHead("");
        char[] origin = {'0', '0', '0', '0', '0', '0'};
        Random rand = new Random();
        int randValue = rand.nextInt(1000000);
        String randomName = Integer.toString(randValue);
        int length = randomName.length();
        user.setName(new String(origin, 0, 6 - length) + randomName);
        user.setSex(0);
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setIsAdministrator(false);
        user.setSite("http://www.xiaoyuzhishi.com");
        user.setAmount(-1l);
        JPAEntry.genericPost(user);

        String nowString = now.toString();
        byte[] sessionIdentity = Securities.digestor.digest(nowString);
        String sessionString = Hex.bytesToHex(sessionIdentity);
        Session s = new Session();
        Long sessionId = IdGenerator.getNewId();
        s.setId(sessionId);
        s.setUserId(userId);
        s.setIdentity(sessionString);
        s.setLastOperationTime(now);
        JPAEntry.genericPost(s);

        JPAEntry.log(userId, "join", "class-room", classRoomId);

        Response result = null;
        try {
            //result = Response.temporaryRedirect(new URI("/client/direct-play.html")).cookie(new NewCookie("sessionId", sessionString, "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false)).build();
            result = Response.seeOther(new URI("/content/direct-play.html?self=" + user.getName())).cookie(new NewCookie("sessionId", sessionString, "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false)).build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //String filePath = request.getServletContext().getRealPath("/client/direct-play.html");
        //return Response.ok(new File(filePath), "text/html").cookie(new NewCookie("sessionId", sessionString, "/api", null, null, NewCookie.DEFAULT_MAX_AGE, false)).build();
        return result;
    }

    @POST
    @Path("messages")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postMessage(@CookieParam("sessionId") String sessionId, Comment comment) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            comment.setId(IdGenerator.getNewId());
            comment.setUserId(JPAEntry.getLoginUser(sessionId).getId());
            comment.setObjectType("class-room");
            comment.setObjectId(classRoomId);
            Date now = new Date();
            comment.setCreateTime(now);
            comment.setUpdateTime(now);
            JPAEntry.genericPost(comment);
            result = Impl.finalResult(comment, null);
        }
        return result;
    }

    @GET
    @Path("messages") ///since/{timestamp}
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessage(@CookieParam("sessionId") String sessionId) { //, @PathParam("timestamp") Date timestamp
        Map<String, Object> filterObject = new HashMap<>();
        filterObject.put("objectType", "class-room");
        filterObject.put("objectId", classRoomId);
        //filterObject.put("createTIme", timestamp);
        Map<String, String> orders = new HashMap<>();
        orders.put("createTime", "ASC");
        return Impl.get(sessionId, filterObject, orders, Comment.class, null, null);
    }
}
