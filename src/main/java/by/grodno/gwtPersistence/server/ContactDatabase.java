package by.grodno.gwtPersistence.server;

import by.grodno.gwtPersistence.shared.Patient;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

public class ContactDatabase {
    private static SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private static List<Patient> patients = new ArrayList<>();

    public static List<Patient> getPatientsList() {
        if (patients.isEmpty()) {
            Session session = sessionFactory.openSession();
            List<Patient> patientsList;
            Criteria criteria = session.createCriteria(Patient.class);
            criteria.addOrder(Order.asc("firstName"));
            patientsList = criteria.list();
            session.close();
            return patientsList;
        }
        return patients;
    }

    public static void addNewPatient(Patient patient) {
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.save(patient);
        session.getTransaction().commit();
        session.close();
    }

    public static void updatePatient(Patient patient) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.update(patient);
        tx.commit();
        session.close();
    }

    public static List<Patient> findPatientByFirsName(String firstname) {
        firstname = "%" + firstname + "%";
        Session session = sessionFactory.openSession();
        List<Patient> patientList;
        Criteria criteria = session.createCriteria(Patient.class);
        criteria.add(Restrictions.like("firstName", firstname));
        patientList = criteria.list();
        session.close();
        return patientList;
    }

    public static void deletePatient(Patient patient) {
        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        session.delete(patient);
        session.getTransaction().commit();
        session.close();
    }
}
