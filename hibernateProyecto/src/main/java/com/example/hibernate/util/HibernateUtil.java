package com.example.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    // Método para construir la SessionFactory
    private static SessionFactory buildSessionFactory() {
        try {
            // Configuración de Hibernate desde el archivo hibernate.cfg.xml
            return new Configuration().configure("hibernate.cfg.xml")
                    .addAnnotatedClass(com.example.hibernate.model.Dispositivo.class)
                    .addAnnotatedClass(com.example.hibernate.model.Incidencia.class)
                    .buildSessionFactory();
        } catch (Throwable ex) {
            // En caso de error, imprimir el mensaje y lanzar una excepción
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Obtener la SessionFactory
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Cerrar la SessionFactory
    public static void shutdown() {
        getSessionFactory().close();
    }
}
