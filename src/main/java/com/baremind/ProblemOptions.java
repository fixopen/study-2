package com.baremind;

import com.baremind.data.ImageText;
import com.baremind.data.ProblemOption;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by User on 2016/9/19.
 */
@Path("problem-options")
public class ProblemOptions {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postCSV(@Context HttpServletRequest request, @CookieParam("userId") String userId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            try {
                Part p = request.getPart("file");
                String contentType = p.getContentType();
                InputStream inputStream = p.getInputStream();
                long now = new Date().getTime();
                String postfix = contentType.substring(contentType.lastIndexOf("/") + 1);
                if (!Objects.equals(postfix, "jpg") || !Objects.equals(postfix, "jpeg") || !Objects.equals(postfix, "gif") || !Objects.equals(postfix, "ai") || !Objects.equals(postfix, "pdg")) {
                    String fileName = now + "." + postfix;
                    String pyshicalpath = Properties.getPropertyValue("testphysicalpath");
                    String uploadedFileLocation = pyshicalpath + fileName;
                    File file = new File(uploadedFileLocation);
                    FileOutputStream w = new FileOutputStream(file);
                    CharacterEncodingFilter.saveFile(w, inputStream);

                    String content = request.getParameter("content");
                    content = new String(content.getBytes("ISO-8859-1"), "UTF-8");
                    String problem = request.getParameter("problemId");
                    /*problem = new String(problemId.getBytes("ISO-8859-1"), "UTF-8");*/
                    /*Long problemId = (Long)*/
                    Long problemId = Long.parseLong(problem);
                    ImageText imageText = new ImageText();
                    imageText.setId(IdGenerator.getNewId());
                    imageText.setExt(postfix);
                    imageText.setMimeType(contentType);
                    imageText.setName(fileName);
                    imageText.setSize(p.getSize());
                    String virtualPath = Properties.getPropertyValue("testvirtualpath") + fileName;
                    imageText.setStorePath(virtualPath);
                    imageText.setContent(content);
                    JPAEntry.genericPost(imageText);
                    ProblemOption problemOption = new ProblemOption();
                    problemOption.setId(IdGenerator.getNewId());
                    problemOption.setName(content);
                    problemOption.setImageId(imageText.getId());
                    problemOption.setProblemId(problemId);
                   // problemOption.setOrder();
                    JPAEntry.genericPost(problemOption);
                   result = Response.ok(new Gson().toJson(problemOption)).build();
                } else {
                    result = Response.status(415).build();
                    //上传图片的格式不正确
                }
            } catch (IOException | ServletException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMedia(@CookieParam("userId") String userId, ProblemOption problemsOption) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            problemsOption.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(problemsOption);
            result = Response.ok(problemsOption).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMedias(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<ProblemOption> problemsOptions = JPAEntry.getList(ProblemOption.class, filterObject);
            if (!problemsOptions.isEmpty()) {
                result = Response.ok(new Gson().toJson(problemsOptions)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMediaById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            ProblemOption problemsOption = JPAEntry.getObject(ProblemOption.class, "id", id);
            if (problemsOption != null) {
                result = Response.ok(new Gson().toJson(problemsOption)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateMedia(@CookieParam("userId") String userId, @PathParam("id") Long id, ProblemOption problemsOption) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = new HashMap<>(1);
            filterObject.put("id", id);
            ProblemOption existmedia = JPAEntry.getObject(ProblemOption.class, "id", id);
            if (existmedia != null) {
                String name = problemsOption.getName();
                if (name != null) {
                    existmedia.setName(name);
                }

                Long problemId = problemsOption.getProblemId();
                if (problemId != null) {
                    existmedia.setProblemId(problemId);
                }

                Long order = problemsOption.getOrder();
                if (order != 0) {
                    existmedia.setOrder(order);
                }

                JPAEntry.genericPut(existmedia);
                result = Response.ok(existmedia).build();
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteOption(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(ProblemOption.class, "id", id);
            if (count > 0) {
                result = Response.ok().build();
            }
        }
        return result;
    }
}
