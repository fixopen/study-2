package com.baremind;

import com.baremind.data.Card;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.List;
import java.util.Map;

@Path("cards")
public class Cards {
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
    public Response postCSV(@Context HttpServletRequest request, @CookieParam("sessionId") String sessionId) {
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
    public Response importCardsViaBareContent(@CookieParam("userId") String userId, byte[] contents) {
        String uploadedFileLocation = "tempFilename.csv";
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            writeToFile(contents, uploadedFileLocation);
            parseAndInsert(uploadedFileLocation);
            result = Response.ok("{\"state\":\"ok\"}").build();
        }
        return result;
    }

    // save uploaded file to new location
    private void writeToFile(byte[] data, String uploadedFileLocation) {
        try {
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            out.write(data);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @POST // 添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCards(@CookieParam("userId") String userId, Card card) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            card.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(card);
            result = Response.ok(card).build();
        }
        return result;
    }


    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCards(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Cards> cards = JPAEntry.getList(Cards.class, filterObject);
            if (!cards.isEmpty()) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                result = Response.ok(gson.toJson(cards)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCardById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Card card = JPAEntry.getObject(Card.class, "id", id);
            if (card != null) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                result = Response.ok(gson.toJson(card)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("userId/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCardByuserId(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Card card = JPAEntry.getObject(Card.class, "userId", id);
            if (card != null) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                result = Response.ok(gson.toJson(card)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCard(@CookieParam("userId") String aUserId, @PathParam("id") Long id, Card card) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(aUserId)) {
            result = Response.status(404).build();
            Card existcard = JPAEntry.getObject(Card.class, "id", id);
            if (existcard != null) {
                String duration = existcard.getDuration();
                if (duration != null) {
                    existcard.setDuration(duration);
                }

                Date activeTime = existcard.getActiveTime();
                if (activeTime != null) {
                    existcard.getActiveTime();
                }

                Date endTime = existcard.getEndTime();
                if (endTime != null) {
                    existcard.setEndTime(endTime);
                }

                String no = existcard.getNo();
                if (no != null) {
                    existcard.setNo(no);
                }

                String password = existcard.getPassword();
                if (password != null) {
                    existcard.setPassword(password);
                }

                Long subject = existcard.getSubjectId();
                if (subject != null) {
                    existcard.setSubjectId(subject);
                }

                Long userId = existcard.getUserId();
                if (userId != null) {
                    existcard.setUserId(userId);
                }

                Double amount = existcard.getAmount();
                if (amount != null) {
                    existcard.setAmount(amount);
                }
                JPAEntry.genericPut(existcard);
                result = Response.ok(existcard).build();
            }
        }
        return result;
    }
}
