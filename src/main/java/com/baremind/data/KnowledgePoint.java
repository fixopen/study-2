package com.baremind.data;

import com.baremind.Logs;
import com.baremind.Resources;
import com.baremind.utils.JPAEntry;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Entity;
/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "knowledge_points")
public class KnowledgePoint {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "volume_id")
    private Long volumeId;

    @Column(name = "grade")
    private int grade;

    @Column(name = "title")
    private String title;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "store_path")
    private String storePath;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "show_time")
    private Date showTime;

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public static class ContentStats {
        @Column(name = "knowledgePointId")
        Long id;

        @Column(name = "type")
        String type;

        @Column(name = "count")
        Long count;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    public static class BaseStats {
        @Column(name = "objectId")
        Long id;

        @Column(name = "count")
        Long count;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    private static Long find(List<Object[]> likeCount, Long id) {
        Long result = null;
        for (Object[] item : likeCount) {
            if (((Long)item[0]).longValue() == id.longValue()) {
                result = (Long)item[1];
                break;
            }
        }
        return result;
    }

    private static List<Object[]> finds(List<Object[]> contents, Long id) {
        return contents.stream().filter(item -> ((Long)item[0]).longValue() == id.longValue()).collect(Collectors.toList());
    }

    public static Map<String, Object> convertToMap(KnowledgePoint kp, List<Object[]> likeCount, List<Object[]> likedCount, List<Object[]> readCount, List<Object[]> contentType) {
        Map<String, Object> kpm = new HashMap<>();
        kpm.put("id", kp.getId());
        kpm.put("volumeId", kp.getVolumeId());
        kpm.put("name", kp.getTitle());
        Date showTime = kp.getShowTime();
        kpm.put("showTime", showTime);
        kpm.put("order", kp.getOrder());
        Long lc = find(likeCount, kp.getId());
        kpm.put("likeCount", lc);
        kpm.put("readCount", find(readCount, kp.getId()));
        kpm.put("liked", find(likedCount, kp.getId()) != null);
        List<Object[]> stats = finds(contentType, kp.getId());

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
        kpm.put("name", kp.getTitle());
        kpm.put("showTime", kp.getShowTime());
        kpm.put("order", kp.getOrder());
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
        String query = "SELECT m.type FROM KnowledgePointContentMap m GROUP BY m.type";
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

   /* @Override
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
        List<ProblemOption> problemOptionObjects = Resources.getListByColumn(em, "problemId", problemIds, ProblemOption.class);
        List<ProblemStandardAnswer> problemStandardAnswerObjects = Resources.getListByColumn(em, "problemId", problemIds, ProblemStandardAnswer.class);

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
    }*/
}
