package com.baremind;

import com.baremind.data.ValidationCode;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;

import javax.persistence.EntityManager;
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

    static String get(String p) {
        return get(JPAEntry.getEntityManager(), p);
    }

    static String get(EntityManager em, String p) {
        String result = null;
        ValidationCode validationCode = JPAEntry.getObject(em, ValidationCode.class, "phoneNumber" , p);
        if (validationCode != null) {
            result = validationCode.getValidCode();
        }
        return result;
    }

    static void inc(String p) {
        EntityManager em = JPAEntry.getNewEntityManager();
        em.getTransaction().begin();
        ValidationCode count = JPAEntry.getObject(em, ValidationCode.class, "phoneNumber", p);
        if (count != null) {
            Long currentCount = Long.parseLong(count.getValidCode()) + 1L;
            count.setValidCode(currentCount.toString());
            em.merge(count);
        } else {
            count = new ValidationCode();
            count.setId(IdGenerator.getNewId());
            count.setPhoneNumber(p);
            count.setValidCode("1");
            count.setTimestamp(new Date());
            em.persist(count);
        }
        em.getTransaction().commit();
        em.close();

    }

    static Integer validation(String p, String v) {
        Integer result = 0;
        Date now = new Date();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("phoneNumber", p);
        conditions.put("validCode", v);
        List<ValidationCode> validationCodeList = JPAEntry.getList(ValidationCode.class, conditions);
        for (ValidationCode aValidationCodeList : validationCodeList) {
            result = 1;
            Date sendTime = aValidationCodeList.getTimestamp();
            if (now.getTime() < 60 * 3 * 1000 + sendTime.getTime()) {
                result = 2;
                break;
            }
        }
        JPAEntry.genericDelete(ValidationCode.class, conditions);
        return result;
    }
}
