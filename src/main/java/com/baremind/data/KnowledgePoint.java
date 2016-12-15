package com.baremind.data;

import com.baremind.Logs;
import com.baremind.Resources;
import com.baremind.utils.JPAEntry;


import javax.persistence.*;
import javax.persistence.Entity;
import java.util.*;
import java.util.stream.Collectors;

import static com.baremind.Resources.getList;

/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "knowledge_points")
public class KnowledgePoint implements com.baremind.data.Entity, Resource {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "volume_id")
    private Long volumeId;

    @Column(name = "price")
    private Long price;

    @Column(name = "name")
    private String name;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "show_time")
    private Date showTime;

    @Column(name = "discount")
    private double discount;

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date show) {
        showTime = show;
    }

    public static Map<String, Object> convertToMap(KnowledgePoint kp, List<Object[]> likeCount, List<Object[]> likedCount, List<Object[]> readCount, List<Object[]> contentType) {
        Map<String, Object> kpm = new HashMap<>();
        kpm.put("id", kp.getId());
        kpm.put("volumeId", kp.getVolumeId());
        kpm.put("name", kp.getName());
        Date showTime = kp.getShowTime();
        kpm.put("showTime", showTime);
        kpm.put("likeCount", Resources.findUntypedItem(likeCount, kp.getId()));
        kpm.put("readCount", Resources.findUntypedItem(readCount, kp.getId()));
        kpm.put("liked", Resources.findUntypedItem(likedCount, kp.getId()) != null);
        List<Object[]> stats = Resources.findUntypedItems(contentType, kp.getId());
        kpm.put("type", "pk");
        for (Object[] s : stats) {
            if (!s[1].equals("problem")) {
                if ((Long)s[2] > 0L) {
                    kpm.put("type", "normal");
                    break;
                }
            }
        }
        long total = 0L;
        for (Object[] s : stats) {
            total += (Long)s[2];
        }
        if (total == 0L) {
            kpm = null;
        }
        return kpm;
    }

    public static Map<String, Object> convertToMap(KnowledgePoint kp, Long userId, Date now, Date yesterday) {
        Map<String, Object> kpm = new HashMap<>();
        kpm.put("id", kp.getId());
        kpm.put("volumeId", kp.getVolumeId());
        kpm.put("name", kp.getName());
        kpm.put("showTime", kp.getShowTime());

        kpm.put("likeCount", 0);
        Long likeCount = Logs.getStatsCount("knowledge-point", kp.getId(), "like");
        if (likeCount != null) {
            kpm.put("likeCount", likeCount);
        }
        kpm.put("liked", Logs.has(userId, "knowledge-point", kp.getId(), "like"));
        kpm.put("readCount", 0);
        Long readCount = Logs.getStatsCount("knowledge-point", kp.getId(), "read");
        if (readCount != null) {
            kpm.put("readCount", readCount);
        }

        String stateType = "old";
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM KnowledgePoint l WHERE l.volumeId = :volumeId AND l.id = :id  AND l.showTime > :yesterday AND l.showTime < :now";
        TypedQuery<Long> q = em.createQuery(stats, Long.class);
        q.setParameter("volumeId", kp.getVolumeId());
        q.setParameter("id", kp.getId());
        q.setParameter("yesterday", yesterday);
        q.setParameter("now", now);
        Long count = q.getSingleResult();
        if (count > 0) {
            stateType = "new";
        }
        String type = "normal";
        String query = "SELECT m.objectType FROM KnowledgePointContentMap m GROUP BY m.objectType";
        TypedQuery<String> pq = em.createQuery(query, String.class);
        List<String> sl = pq.getResultList();
        if (sl.size() == 1) {
            if (sl.get(0).equals("problem")) {
                type = "pk";
            }
        }
        switch (stateType) {
            case "old":
                switch (type) {
                    case "normal":
                        kpm.put("type", "normalOld");
                        break;
                    case "pk":
                        kpm.put("type", "pkOld");
                        break;
                }
                break;
            case "new":
                switch (type) {
                    case "normal":
                        kpm.put("type", "normalNew");
                        break;
                    case "pk":
                        kpm.put("type", "pkNew");
                        break;
                }
                break;
        }
        return kpm;
    }

    @Override
    public Long getAmount() {
        return (long) (price * discount);
    }

    @Override
    public Long getSubjectId() {
        Long result = null;
        Volume v = JPAEntry.getObject(Volume.class, "id", getVolumeId());
        if (v != null) {
            Subject s = JPAEntry.getObject(Subject.class, "id", v.getSubjectId());
            if (s != null) {
                result = s.getId();
            }
        }
        return result;
    }

    @Override
    public void setAmount(Long a) {
        //do nothing
    }

    @Override
    public Map<String, Object> getContent() {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("knowledgePointId", id);
        Map<String, String> orders = new HashMap<>();
        orders.put("order", "ASC");
        List<KnowledgePointContentMap> maps = JPAEntry.getList(KnowledgePointContentMap.class, conditions, orders);

        List<String> textIds = new ArrayList<>();
        List<String> imageIds = new ArrayList<>();
        List<String> videoIds = new ArrayList<>();
        List<String> problemIds = new ArrayList<>();
        List<String> imageTextIds = new ArrayList<>();
        List<String> quoteIds = new ArrayList<>();
        List<String> pinyinIds = new ArrayList<>();

        for (KnowledgePointContentMap item : maps) {
            switch (item.getObjectType()) {
                case "text":
                    textIds.add(item.getObjectId().toString());
                    break;
                case "image":
                    imageIds.add(item.getObjectId().toString());
                    break;
                case "video":
                    videoIds.add(item.getObjectId().toString());
                    break;
                case "problem":
                    problemIds.add(item.getObjectId().toString());
                    break;
                case "imageText":
                    imageTextIds.add(item.getObjectId().toString());
                    break;
                case "quote":
                    quoteIds.add(item.getObjectId().toString());
                    break;
                case "pinyinText":
                    pinyinIds.add(item.getObjectId().toString());
                    break;
            }
        }

        EntityManager em = JPAEntry.getEntityManager();

        List<Text> textObjects = getList(em, textIds, Text.class);

        List<Image> imageObjects = getList(em, imageIds, Image.class);

        List<Video> videoObjects = getList(em, videoIds, Video.class);

        List<Problem> problemObjects = getList(em, problemIds, Problem.class);
        List<ProblemOption> problemOptionObjects = Resources.getList(em, "problemId", problemIds, ProblemOption.class);
        List<ProblemStandardAnswer> problemStandardAnswerObjects = Resources.getList(em, "problemId", problemIds, ProblemStandardAnswer.class);

        List<ImageText> imageTextObject = getList(em, imageTextIds, ImageText.class);

        List<Quote> quoteObject = getList(em, quoteIds, Quote.class);

        List<PinyinText> pinyinTextObject = getList(em, pinyinIds, PinyinText.class);

        List<Object> orderedContents = new ArrayList<>();
        List<Object> orderedProblems = new ArrayList<>();
        List<Object> orderedQuotes = new ArrayList<>();

        for (final KnowledgePointContentMap item : maps) {
            switch (item.getObjectType()) {
                case "text":
                    if (textObjects != null) {
                        Text t = Resources.findItem(textObjects, (Text text) -> text.getId().longValue() == item.getObjectId().longValue());
                        Map<String, Object> tm = Text.convertToMap(t);
                        orderedContents.add(tm);
                    }
                    break;
                case "image":
                    if (imageObjects != null) {
                        Image i = Resources.findItem(imageObjects, (image) -> image.getId().longValue() == item.getObjectId().longValue());
                        Map<String, Object> im = Image.convertToMap(i);
                        orderedContents.add(im);
                    }
                    break;
                case "imageText":
                    if (imageTextObject != null) {
                        ImageText it = Resources.findItem(imageTextObject, (imageText) -> imageText.getId().longValue() == item.getObjectId().longValue());
                        Map<String, Object> itm = ImageText.convertToMap(it);
                        orderedContents.add(itm);
                    }
                    break;
                case "pinyinText":
                    if (pinyinTextObject != null) {
                        PinyinText pt = Resources.findItem(pinyinTextObject, (pinyinText) -> pinyinText.getId().longValue() == item.getObjectId().longValue());
                        Map<String, Object> qm = PinyinText.convertToMap(pt);
                        orderedContents.add(qm);
                    }
                    break;
                case "problem":
                    if (problemObjects != null || problemOptionObjects != null || problemStandardAnswerObjects != null) {
                        Problem problemItem = Resources.findItem(problemObjects, (problem) -> problem.getId().longValue() == item.getObjectId().longValue());
                        List<ProblemOption> problemOptions = Resources.findItems(problemOptionObjects, (ProblemOption problemoption) -> problemoption.getProblemId().longValue() == item.getObjectId().longValue());
                        List<ProblemStandardAnswer> problemStandardAnswers = Resources.findItems(problemStandardAnswerObjects, (problemstandardanswers) -> problemstandardanswers.getProblemId().longValue() == item.getObjectId().longValue());
                        Map<String, Object> pm = Problem.convertToMap(problemItem, problemOptions, problemStandardAnswers);
                        orderedProblems.add(pm);
                    }
                    break;
                case "quote":
                    if (quoteObject != null) {
                        Quote q = Resources.findItem(quoteObject, (quote) -> quote.getId().longValue() == item.getObjectId().longValue());
                        orderedQuotes.add(q);
                    }
                    break;
            }
        }

        Map<String, Object> totalResult = new HashMap<>();
        totalResult.put("title", getName());
        totalResult.put("quotes", orderedQuotes);
        totalResult.put("contents", orderedContents);

        if ((videoObjects != null) && !videoObjects.isEmpty()) {
            Video video = videoObjects.get(0);
            Map<String, Object> vm = Video.convertToMap(video);
            totalResult.put("video", vm);
        }

        Map<String, Object> interaction = new HashMap<>();
        interaction.put("likeCount", Logs.getStatsCount("knowledge-point", id, "like"));
        interaction.put("readCount", Logs.getStatsCount("knowledge-point", id, "read"));
        totalResult.put("interaction", interaction);

        totalResult.put("problems", orderedProblems);

        conditions = new HashMap<>();
        conditions.put("objectType", "knowledge-point");
        conditions.put("objectId", id);
        List<Comment> comments = JPAEntry.getList(Comment.class, conditions);
        List<Map<String, Object>> commentMaps = comments.stream().map(Comment::convertToMap).collect(Collectors.toList());
        totalResult.put("comments", commentMaps);
        return totalResult;
    }
}
