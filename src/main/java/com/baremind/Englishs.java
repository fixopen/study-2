package com.baremind;

import com.baremind.data.English;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fixopen on 10/11/2016.
 */
@Path("englishs")
public class Englishs {
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
        English english = JPAEntry.getObject(English.class, conditions);
        if (english != null) {
            result = Response.ok(new Gson().toJson(english)).build();
        }
        return result;
    }

    @POST //import
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_PLAIN, "text/csv"})
    public Response importCardsViaBareContent(/*@CookieParam("userId") String userId, */byte[] contents) {
        String uploadedFileLocation = "tempFilename.csv";
        //Response result = Response.status(401).build();
        //if (JPAEntry.isLogining(userId)) {
            CharacterEncodingFilter.writeToFile(contents, uploadedFileLocation);
            parseAndInsert(uploadedFileLocation);
            Response result = Response.ok("{\"state\":\"ok\"}").build();
        //}
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
                URI uri = UriBuilder.fromUri(fields[1]).build();
                String path = uri.getPath();
                String subjectNo = path.substring(13, 15);
                String gradeNo = path.substring(15, 17);
                String bookNo = "";
                String pageNo = "";
                String unitNo = "";
                String query = uri.getQuery();
                String[] queryParameters = query.split("&");
                for (String queryParameter: queryParameters) {
                    String[] pair = queryParameter.split("=");
                    switch (pair[0]) {
                        case "book":
                            bookNo = pair[1];
                            break;
                        case "page":
                            pageNo = pair[1];
                            break;
                        case "unit":
                            unitNo = pair[1];
                            break;
                    }
                }
                String command = "INSERT INTO englishs (id, subject_no, grade_no, book_no, page_no, unit_no, start_time, end_time) VALUES (" + id.toString() + ", '" + subjectNo + "', '" + gradeNo + "', '" + bookNo + "', '" + pageNo + "', '" + unitNo + "', " + fields[2] + ", " + fields[3] + ")";
                Query q = em.createNativeQuery(command);
                q.executeUpdate();
            }
            em.getTransaction().commit();
            em.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        * no,url,start,end
        1,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01,0,0
        2,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=00&unit=10,10550,12400
        3,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=10&unit=10,13550,15750
        4,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=20&unit=10,23000,26555
        5,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=30&unit=10,22900,25520
        6,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=40&unit=10,27850,29600
        7,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=50&unit=10,32850,35650
        8,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=60&unit=10,37250,39700
        9,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=70&unit=10,42500,45000
        10,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=80&unit=10,47350,49100
        11,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=90&unit=10,59800,61500
        12,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=100&unit=10,56750,58300
        13,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=110&unit=10,62350,64800
        14,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=120&unit=10,65550,67700
        15,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=130&unit=10,70100,72350
        16,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=140&unit=10,74500,76060
        17,http://www.xiaoyuzhishi.com/k/e/p-zhibo-0420.html?book=01&page=150&unit=10,79800,82000
        */
    }
}
