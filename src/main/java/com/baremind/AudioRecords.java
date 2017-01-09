package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fixopen on 10/11/2016.
 */
@Path("audio-records")
public class AudioRecords {
    Long getUserId(HttpServletRequest request) {
        Long userId = null;
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals("sessionId")) {
                Session s = JPAEntry.getSession(c.getValue());
                if (s != null) {
                    userId = s.getUserId();
                }
                break;
            }
        }
        return userId;
    }

    @GET
    @Path("/{subjectNo}/{gradeNo}/{bookNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryBook(@Context HttpServletRequest request, @PathParam("subjectNo") String subjectNo, @PathParam("gradeNo") String gradeNo, @PathParam("bookNo") String bookNo) {
        Response result = Response.status(404).build();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("subjectNo", subjectNo);
        conditions.put("gradeNo", gradeNo);
        conditions.put("bookNo", bookNo);
        Book english = JPAEntry.getObject(Book.class, conditions);
        if (english != null) {
            Long userId = getUserId(request);
            result = Response.ok(new Gson().toJson(Book.convertToMap(english, userId))).build();
        }
        return result;
    }

    @GET
    @Path("/{subjectNo}/{gradeNo}/{bookNo}/{pageNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryBook(@Context HttpServletRequest request, @PathParam("subjectNo") String subjectNo, @PathParam("gradeNo") String gradeNo, @PathParam("bookNo") String bookNo, @PathParam("pageNo") String pageNo) {
        Response result = Response.status(404).build();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("subjectNo", subjectNo);
        conditions.put("gradeNo", gradeNo);
        conditions.put("bookNo", bookNo);
        Book english = JPAEntry.getObject(Book.class, conditions);
        if (english != null) {
            Long userId = getUserId(request);
            result = Response.ok(new Gson().toJson(Book.convertToMap(english, pageNo, userId))).build();
        }
        return result;
    }

    @GET
    @Path("/{subjectNo}/{gradeNo}/{bookNo}/name")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryBookName(@PathParam("subjectNo") String subjectNo, @PathParam("gradeNo") String gradeNo, @PathParam("bookNo") String bookNo) {
        Response result = Response.status(404).build();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("subjectNo", subjectNo);
        conditions.put("gradeNo", gradeNo);
        conditions.put("bookNo", bookNo);
        Book english = JPAEntry.getObject(Book.class, conditions);
        if (english != null) {
            result = Response.ok(new Gson().toJson(english)).build();
        }
        return result;
    }

    @GET
    @Path("/{subjectNo}/{gradeNo}/{bookNo}/{pageNo}/{unitNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryRecord(@PathParam("subjectNo") String subjectNo, @PathParam("gradeNo") String gradeNo, @PathParam("bookNo") String bookNo, @PathParam("pageNo") String pageNo, @PathParam("unitNo") String unitNo) {
        Response result = Response.status(404).build();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("subjectNo", subjectNo);
        conditions.put("gradeNo", gradeNo);
        conditions.put("bookNo", bookNo);
        conditions.put("pageNo", pageNo);
        conditions.put("unitNo", unitNo);
        AudioRecord audioRecord = JPAEntry.getObject(AudioRecord.class, conditions);
        if (audioRecord != null) {
            result = Response.ok(new Gson().toJson(audioRecord)).build();
        }
        return result;
    }

    @PUT
    @Path("/{subjectNo}/{gradeNo}/{bookNo}/like")
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@Context HttpServletRequest request, @PathParam("subjectNo") String subjectNo, @PathParam("gradeNo") String gradeNo, @PathParam("bookNo") String bookNo) {
        Response result = Response.status(404).build();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("subjectNo", subjectNo);
        conditions.put("gradeNo", gradeNo);
        conditions.put("bookNo", bookNo);
        Book book = JPAEntry.getObject(Book.class, conditions);
        if (book != null) {
            Log log = Logs.insert(getUserId(request), "book", book.getId(), "like");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @PUT
    @Path("/{subjectNo}/{gradeNo}/{bookNo}/unlike")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@Context HttpServletRequest request, @PathParam("subjectNo") String subjectNo, @PathParam("gradeNo") String gradeNo, @PathParam("bookNo") String bookNo) {
        Response result = Response.status(404).build();
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("subjectNo", subjectNo);
        conditions.put("gradeNo", gradeNo);
        conditions.put("bookNo", bookNo);
        Book book = JPAEntry.getObject(Book.class, conditions);
        if (book != null) {
            Long count = Logs.deleteLike(getUserId(request), "book", book.getId());
            result = Response.ok("{\"state\": \"ok\"}").build();
        }
        return result;
    }

    @POST //import
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, "application/xml"})
    public Response importXmlRecordsViaBareContent(byte[] contents) {
        EnglishBook r = new EnglishBook.Builder(contents).build();
        Map<String, Object> condition = new HashMap<>();
        condition.put("subjectNo", r.getSubjectNo());
        condition.put("gradeNo", r.getGradeNo());
        condition.put("bookNo", r.getNo());
        Book n = JPAEntry.getObject(Book.class, condition);
        if (n == null) {
            n = new Book();
            n.setId(IdGenerator.getNewId());
            n.setSubjectNo(r.getSubjectNo());
            n.setGradeNo(r.getGradeNo());
            n.setBookNo(r.getNo());
            n.setName(r.getName());
            JPAEntry.genericPost(n);
        }
        Long bookId = n.getId();
        r.getPages().forEach((page) -> {
            String pageNo = page.getNo();
            page.getUnits().forEach((unit) -> {
                AudioRecord audioRecord = new AudioRecord();
                audioRecord.setId(IdGenerator.getNewId());
                audioRecord.setBookId(bookId);
//                audioRecord.setSubjectNo(n.getSubjectNo());
//                audioRecord.setGradeNo(n.getGradeNo());
//                audioRecord.setBookNo(n.getBookNo());
                audioRecord.setPageNo(pageNo);
                audioRecord.setUnitNo(unit.getNo());
                EnglishBook.Page.Unit.Rectangle bounds = unit.getBounds();
                if (bounds != null) {
                    audioRecord.setLeft(bounds.getLeft());
                    audioRecord.setTop(bounds.getTop());
                    audioRecord.setRight(bounds.getRight());
                    audioRecord.setBottom(bounds.getBottom());
                }
                audioRecord.setChinese(unit.getContent());
                JPAEntry.genericPost(audioRecord);
            });
        });
        return Response.ok("{\"state\":\"ok\"}").build();
    }

//    @POST //import
//    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, "text/csv"})
//    public Response importRecordsViaBareContent(/*@CookieParam("userId") String userId, */byte[] contents) {
//        String uploadedFileLocation = "tempFilename.csv";
//        //Response result = Response.status(401).build();
//        //if (JPAEntry.isLogining(userId)) {
//        CharacterEncodingFilter.writeToFile(contents, uploadedFileLocation);
//        parseAndInsert(uploadedFileLocation);
//        //}
//        return Response.ok("{\"state\":\"ok\"}").build();
//    }
//
//    private void parseAndInsert(String csvFilename) {
//        try {
//            EntityManager em = JPAEntry.getNewEntityManager();
//            em.getTransaction().begin();
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilename)));
//            String record;
//            boolean isFirstLine = true;
//            while ((record = br.readLine()) != null) {
//                if (isFirstLine) {
//                    isFirstLine = false;
//                    continue;
//                }
//                String[] fields = record.split(",");
//                Long id = IdGenerator.getNewId();
//                URI uri = UriBuilder.fromUri(fields[1]).build();
//                String path = uri.getPath();
//                String subjectNo = path.substring(13, 15);
//                String gradeNo = path.substring(15, 17);
//                String bookNo = "";
//                String pageNo = "";
//                String unitNo = "";
//                String query = uri.getQuery();
//                String[] queryParameters = query.split("&");
//                for (String queryParameter : queryParameters) {
//                    String[] pair = queryParameter.split("=");
//                    switch (pair[0]) {
//                        case "book":
//                            bookNo = pair[1];
//                            break;
//                        case "page":
//                            pageNo = pair[1];
//                            break;
//                        case "unit":
//                            unitNo = pair[1];
//                            break;
//                    }
//                }
//                String command = "INSERT INTO audio_records (id, subject_no, grade_no, book_no, page_no, unit_no, start_time, end_time) VALUES (" + id.toString() + ", '" + subjectNo + "', '" + gradeNo + "', '" + bookNo + "', '" + pageNo + "', '" + unitNo + "', " + fields[2] + ", " + fields[3] + ")";
//                Query q = em.createNativeQuery(command);
//                q.executeUpdate();
//            }
//            em.getTransaction().commit();
//            em.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        /*
//        * no,url,start,end
//        1,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01,0,0
//        2,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=00&unit=10,10550,12400
//        3,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=10&unit=10,13550,15750
//        4,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=20&unit=10,23000,26555
//        5,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=30&unit=10,22900,25520
//        6,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=40&unit=10,27850,29600
//        7,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=50&unit=10,32850,35650
//        8,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=60&unit=10,37250,39700
//        9,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=70&unit=10,42500,45000
//        10,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=80&unit=10,47350,49100
//        11,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=90&unit=10,59800,61500
//        12,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=100&unit=10,56750,58300
//        13,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=110&unit=10,62350,64800
//        14,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=120&unit=10,65550,67700
//        15,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=130&unit=10,70100,72350
//        16,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=140&unit=10,74500,76060
//        17,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=150&unit=10,79800,82000
//        */
//    }
//
//    @POST //import
//    @Path("names")
//    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, "text/csv"})
//    public Response importNamesViaBareContent(/*@CookieParam("userId") String userId, */byte[] contents) {
//        String uploadedFileLocation = "tempFilename.csv";
//        //Response result = Response.status(401).build();
//        //if (JPAEntry.isLogining(userId)) {
//        CharacterEncodingFilter.writeToFile(contents, uploadedFileLocation);
//        parseAndInsertForName(uploadedFileLocation);
//        //}
//        return Response.ok("{\"state\":\"ok\"}").build();
//    }
//
//    private void parseAndInsertForName(String csvFilename) {
//        try {
//            EntityManager em = JPAEntry.getNewEntityManager();
//            em.getTransaction().begin();
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilename)));
//            String record;
//            while ((record = br.readLine()) != null) {
//                String nos = record.substring(0, 6);
//                String name = record.substring(7);
//                Long id = IdGenerator.getNewId();
//                String subjectNo = nos.substring(0, 2);
//                String gradeNo = nos.substring(2, 4);
//                String bookNo = nos.substring(4);
//                String command = "INSERT INTO books (id, subject_no, grade_no, book_no, name) VALUES (" + id.toString() + ", '" + subjectNo + "', '" + gradeNo + "', '" + bookNo + "', '" + name + "')";
//                Query q = em.createNativeQuery(command);
//                q.executeUpdate();
//            }
//            em.getTransaction().commit();
//            em.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        /*
//        042001,My family
//        042002,A polite child
//        042003,Winter clothes
//        042004,Bear is body
//        042005,What fruit can you find?
//        042006,Can you see some colors?
//        042007,A magic pencil case
//        042008,Different shapes
//        042009,How many balloons?
//        042010,What can they be?
//        042011,Go on a picnic
//        042012,What is the dog doing?
//        042013,Jack, Jack, what do you see
//        042014,A day of Amy
//        042015,Which one is your face today?
//        042016,We are having fun
//        052001,My First Phonics Book 1
//        052002,My First Phonics Book 2
//        052003,My First Phonics Book 3
//        052004,My First Phonics Book 4
//        052005,My First Phonics Book 5
//        052006,My First Phonics Book 6
//        052007,My First Phonics Book 7
//        052008,My First Phonics Book 8
//        052009,My First Phonics Book 9
//        052010,My First Phonics Book 10
//        052011,My First Phonics Book 11
//        052012,My First Phonics Book 12
//        052013,My First Phonics Book 13
//        052014,My First Phonics Book 14
//        052015,My First Phonics Book 15
//        052016,My First Phonics Book 16
//        */
//    }
}
