package com.example.hibernate.dao;

import com.example.hibernate.model.Dispositivo;
import com.example.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class DispositivoDAO {

    public void insertar(Dispositivo dispositivo) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(dispositivo);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<Dispositivo> obtenerTodos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Dispositivo", Dispositivo.class).list();
        }
    }
}
