package com.baremind;

import com.baremind.data.ValidationCode;
import com.baremind.utils.JPAEntry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fixopen on 22/12/2016.
 */

@Path("validation-codes")
public class ValidationCodes {
    @GET //根据id查询
    @Path("{info}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("info") String info, @PathParam("key") String key) {
        return Response.ok("{\"state\":\"" + validation(info, key).toString() + "\"}").build();
    }

    static Boolean validation(String p, String v) {
        Boolean result = false;
        Date now = new Date();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("phoneNumber", p);
        conditions.put("validCode", v);
        List<ValidationCode> validationCodeList = JPAEntry.getList(ValidationCode.class, conditions);
        for (ValidationCode aValidationCodeList : validationCodeList) {
            Date sendTime = aValidationCodeList.getTimestamp();
            if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                result = true;
                break;
            }
        }
        JPAEntry.genericDelete(ValidationCode.class, conditions);
        return result;
    }
}
