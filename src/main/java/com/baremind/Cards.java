package com.baremind;

import com.baremind.data.Card;
import com.baremind.data.User;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Path("cards")
public class Cards {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, Card.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Card.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Card entity) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.create(sessionId, entity, null);
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Card newData) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.updateById(sessionId, id, newData, Card.class, (exist, card) -> {
                    String duration = card.getDuration();
                    if (duration != null) {
                        exist.setDuration(duration);
                    }

                    Date activeTime = card.getActiveTime();
                    if (activeTime != null) {
                        exist.getActiveTime();
                    }

                    Date endTime = card.getEndTime();
                    if (endTime != null) {
                        exist.setEndTime(endTime);
                    }

                    String no = card.getNo();
                    if (no != null) {
                        exist.setNo(no);
                    }

                    String password = card.getPassword();
                    if (password != null) {
                        exist.setPassword(password);
                    }

                    Long subject = card.getSubjectId();
                    if (subject != null) {
                        exist.setSubjectId(subject);
                    }

                    Long userId = card.getUserId();
                    if (userId != null) {
                        exist.setUserId(userId);
                    }

                    Double amount = card.getAmount();
                    if (amount != null) {
                        exist.setAmount(amount);
                    }
                }, null);
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.deleteById(sessionId, id, Card.class);
            }
        }
        return result;
    }

    private static final String[] serials = new String[]{"1"};

    @POST
    @Path("generate/{subjectNo}/{grade}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cardsGenerator(@CookieParam("sessionId") String sessionId, @PathParam("subjectNo") String subjectNo, @PathParam("grade") String grade, byte[] contents) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            try {
                Map<String, Object> q = new Gson().fromJson(new String(contents, StandardCharsets.UTF_8.toString()), new TypeToken<Map<String, Object>>() {
                }.getType());
                Long start = (Long) q.get("start");
                Long count = (Long) q.get("count");
                for (int i = 0; i < count; ++i) {
                    String serialNo = start.toString(); //7-char
                    String no = subjectNo + grade + serials[0] + serialNo;
                    //generate card no
                    //generate 8-char random number
                    //record to database
                    //write to file
                }
                result = Response.ok("{\"state\":\"ok\"}").build();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                result = Response.status(400).build();
            }
        }
        return result;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadCards(@Context HttpServletRequest request, @CookieParam("sessionId") String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            try {
                Part p = request.getPart("file");
                InputStream inputStream = p.getInputStream();
                long now = new Date().getTime();
                String postfix = ".csv";
                String fileName = now + "." + postfix;
                String uploadedFileLocation = "" + fileName;

                File file = new File(uploadedFileLocation);
                FileOutputStream w = new FileOutputStream(file);
                CharacterEncodingFilter.saveFile(w, inputStream);

                parseAndInsert(uploadedFileLocation);
                result = Response.ok("{\"state\":\"ok\"}").build();
            } catch (IOException | ServletException e) { /*FileNotFoundException*/
                e.printStackTrace();
            }
        }
        return result;
    }

    @POST //import
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, "text/csv"})
    public Response importCardsViaBareContent(@CookieParam("sessionId") String sessionId, byte[] contents) {
        String uploadedFileLocation = "tempFilename.csv";
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            CharacterEncodingFilter.writeToFile(contents, uploadedFileLocation);
            parseAndInsert(uploadedFileLocation);
            result = Response.ok("{\"state\":\"ok\"}").build();
        }
        return result;
    }

    private void parseAndInsert(String csvFilename) {
        try {
            EntityManager em = JPAEntry.getNewEntityManager();
            em.getTransaction().begin();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilename)));
            String record;
            boolean isFirstLine = true;
            while ((record = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] fields = record.split(",");
                Long id = IdGenerator.getNewId();
                String command = "INSERT INTO cards (id, no, password) VALUES (" + id.toString() + ", '" + fields[1] + "', '" + fields[2] + "')";
                Query q = em.createNativeQuery(command);
                q.executeUpdate();
            }
            em.getTransaction().commit();
            em.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
