package util;

import com.privasia.jpa.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionImpl;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HibernateUtils implements AutoCloseable {

    public static void main(String[] args) {
        System.setProperty("org.jboss.logging.provider", "jdk");
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.WARNING);

        List<Class> cls = Arrays.asList(Employee.class);
        try (HibernateUtils t = new HibernateUtils(cls)) {
            t.saveOrUpdate(new Employee("YONG", "KianOn", 1000));
        } catch (Exception e) {
            println(e.getMessage());
        }
    }

    public synchronized int execute(String sql) {
        AtomicInteger rows = new AtomicInteger();
        try {
            sess.doWork(conn -> {
                Statement stmt = conn.createStatement();
                rows.set(stmt.executeUpdate(sql));
                conn.commit();
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return rows.get();
    }

    public synchronized Connection getConnection() {
        return ((SessionImpl) sess).connection();
    }

    public void saveOrUpdate_threadsafe(Object entity) {
        try {
            Session s = factory.openSession();
            Transaction tx = s.beginTransaction();
            s.saveOrUpdate(entity);
            tx.commit();
            s.clear();
            s.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public synchronized void save(Object... entities) {
        try {
            Transaction tx = sess.beginTransaction();
            for (Object o : entities) sess.save(o);
            tx.commit();
            sess.clear();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public synchronized void saveOrUpdate(Object... entities) {
        //perf_reason if (entities == null) return;
        try {
            Transaction tx = sess.beginTransaction();
            for (Object o : entities) sess.saveOrUpdate(o);
            tx.commit();
            sess.clear();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public HibernateUtils(List<Class> cls) {
        cfg = new Configuration().configure(getClass().getResource("/hibernate.cfg.xml"));
        for (Class c : cls) cfg.addAnnotatedClass(c);
        StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
        serviceRegistryBuilder.applySettings(cfg.getProperties());
        ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
        factory = cfg.buildSessionFactory(serviceRegistry);
        sess = factory.openSession();
    }

    public SessionFactory getFactory() {
        return factory;
    }

    public Session getSession_NotThreadSafe() {
        return sess;
    }

    @Override
    public void close() {
        if (sess != null) sess.close();
        if (factory != null) factory.close();
    }

    private static void println(Object o) {
        System.out.println(o);
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HibernateUtils.class);
    private Configuration cfg;
    private SessionFactory factory;
    private Session sess;
}
