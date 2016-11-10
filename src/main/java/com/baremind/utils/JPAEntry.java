package com.baremind.utils;

import com.baremind.Logs;
import com.baremind.data.Session;
import com.baremind.data.User;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by fixopen on 18/8/15.
 */
public class JPAEntry {
    private static final String PERSISTENCE_UNIT_NAME = "supportData";
    private static EntityManagerFactory factory;
    private static EntityManager entityManager;

    public static EntityManager getEntityManager() {
        if (entityManager == null) {
            try {
                factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                entityManager = factory.createEntityManager();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entityManager;
    }

    public static EntityManager getNewEntityManager() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return factory.createEntityManager();
    }

    public static <T> T getObject(Class<T> type, String fieldName, Object fieldValue) {
        HashMap<String, Object> condition = new HashMap<>(1);
        condition.put(fieldName, fieldValue);
        return getObject(type, condition);
//        T result = null;
//        EntityManager em = getEntityManager();
//        String jpql = "SELECT a FROM " + type.getSimpleName() + " a WHERE a." + fieldName + " = :variable";
//        try {
//            result = em.createQuery(jpql, type)
//                .setParameter("variable", fieldValue)
//                .getSingleResult();
//        } catch (NoResultException e) {
//            //do noting
//        } catch (NonUniqueResultException e) {
//            List<T> t = em.createQuery(jpql, type)
//                .setParameter("variable", fieldValue)
//                .getResultList();
//            result = t.get(0);
//        }
//        return result;
    }

    //SELECT a FROM KnowledgePoint a WHERE a.volumeId = :volumeId AND a.showTime < :showTime
    //setParameter("showTime", v)
    public static <T> T getObject(Class<T> type, Map<String, Object> conditions) {
        T result = null;
//        Logs.insert(8l, "perf", 32l, new Date().toString());
        EntityManager em = getEntityManager();
//        Logs.insert(8l, "perf", 32l, new Date().toString());
        String jpql = "SELECT a FROM " + type.getSimpleName() + " a ";
        boolean isFirst = true;
        if (conditions != null) {
            for (Map.Entry<String, Object> item : conditions.entrySet()) {
                if (isFirst) {
                    jpql += " WHERE ";
                    isFirst = false;
                } else {
                    jpql += " AND ";
                }
                String op = "=";
                if (item.getValue() instanceof Condition) {
                    op = ((Condition) item.getValue()).getOp();
                }
                jpql += "a." + item.getKey() + op + " :" + item.getKey();
            }
        }
        try {
            TypedQuery<T> q = em.createQuery(jpql, type);
            if (conditions != null) {
                for (Map.Entry<String, Object> item : conditions.entrySet()) {
                    Object value = item.getValue();
                    if (item.getValue() instanceof Condition) {
                        value = ((Condition) item.getValue()).getValue();
                    }
                    q.setParameter(item.getKey(), value);
                }
            }
//            Logs.insert(8l, "perf", 32l, new Date().toString());
            result = q.getSingleResult();
//            Logs.insert(8l, "perf", 32l, new Date().toString());
        } catch (NoResultException e) {
            //do noting
        } catch (NonUniqueResultException e) {
            TypedQuery<T> q = em.createQuery(jpql, type);
            if (conditions != null) {
                for (Map.Entry<String, Object> item : conditions.entrySet()) {
                    q.setParameter(item.getKey(), item.getValue());
                }
            }
            List<T> t = q.getResultList();
            result = t.get(0);
        }
        return result;
    }

    public static <T> List<T> getList(Class<T> type, String fieldName, Object fieldValue) {
        HashMap<String, Object> condition = new HashMap<>(1);
        condition.put(fieldName, fieldValue);
        return getList(type, condition);
    }

    public static <T> List<T> getList(Class<T> type, Map<String, Object> conditions) {
        return getList(type, conditions, null);
    }

    public static <T> List<T> getList(Class<T> type, Map<String, Object> conditions, Map<String, String> orders) {
        EntityManager em = getEntityManager();
        return getList(em, type, conditions, orders);
    }

    public static <T> List<T> getList(EntityManager em, Class<T> type, Map<String, Object> conditions, Map<String, String> orders) {
        String jpql = "SELECT o FROM " + type.getSimpleName() + " o";
        boolean isFirst = true;
        if (conditions != null) {
            for (Map.Entry<String, Object> item : conditions.entrySet()) {
                if (isFirst) {
                    jpql += " WHERE ";
                    isFirst = false;
                } else {
                    jpql += " AND ";
                }
                String op = "=";
                if (item.getValue() instanceof Condition) {
                    op = ((Condition) item.getValue()).getOp();
                }
                jpql += "o." + item.getKey() + " " + op + " :" + item.getKey();
            }
        }
        if (orders != null) {
            isFirst = true;
            for (Map.Entry<String, String> order : orders.entrySet()) {
                if (isFirst) {
                    jpql += " ORDER BY ";
                    isFirst = false;
                } else {
                    jpql += ", ";
                }
                jpql += "o." + order.getKey() + " " + order.getValue();
            }
        }
        final TypedQuery<T> q = em.createQuery(jpql, type);
        if (conditions != null) {
            conditions.forEach((key, value) -> {
                Object v = value;
                if (value instanceof Condition) {
                    v = ((Condition) value).getValue();
                }
                q.setParameter(key, v);
            });
            //for (Map.Entry<String, Object> item : conditions.entrySet()) {
            //    q.setParameter(item.getKey(), item.getValue());
            //}
        }
        return q.getResultList();
    }

    public static void genericPost(Object o) {
        EntityManager em = JPAEntry.getNewEntityManager();
        em.getTransaction().begin();
        em.persist(o);
        em.getTransaction().commit();
        em.close();
    }

    public static void genericPut(Object o) {
        EntityManager em = JPAEntry.getNewEntityManager();
        em.getTransaction().begin();
        em.merge(o);
        em.getTransaction().commit();
        em.close();
    }

    public static long genericDelete(Class type, Map<String, Object> conditions) {
        EntityManager em = JPAEntry.getNewEntityManager();
        em.getTransaction().begin();
        List os = JPAEntry.getList(em, type, conditions, null);
        long result = os.size();
        for (Object o : os) {
            em.remove(o);
        }
        em.getTransaction().commit();
        em.close();
        return result;
    }

    public static long genericDelete(Class type, String name, Object value) {
        Map<String, Object> conditions = new HashMap<>();
        conditions.put(name, value);
        return genericDelete(type, conditions);
    }

    public static boolean isLogining(String userId) {
        final Map<String, Boolean> r = new HashMap<>();
        r.put("value", false);
        isLogining(userId, a -> {
            //a.setLastOperationTime(new Date());
            //genericPut(a);
            r.put("value", true);
        });
        return r.get("value");
    }

    public static void isLogining(String userId, Consumer<User> touchFunction) {
        Long id = Long.parseLong(userId);
        User u = getObject(User.class, "id", id);
        if (u != null) {
            Session s = getObject(Session.class, "userId", id);
            if (s != null) {
                touchFunction.accept(u);
            }
        }
    }

    public static Long getLoginId(String sessionId) {
        final Map<String, Long> r = new HashMap<>();
        r.put("value", 0l);
        isLogining(sessionId, a -> {
            //a.setLastOperationTime(new Date());
            //genericPut(a);
            r.put("value", a.getId());
        });
        return r.get("value");
    }

    public static void log(Long userId, String action, String objectType, Long objectId) {
        Logs.insert(userId, objectType, objectId, action);
//        Log log = new Log();
//        log.setId(IdGenerator.getNewId());
//        log.setUserId(userId);
//        log.setAction(action);
//        log.setObjectType(objectType);
//        log.setObjectId(objectId);
//        log.setCreateTime(new Date());
//        genericPost(log);
    }
}
