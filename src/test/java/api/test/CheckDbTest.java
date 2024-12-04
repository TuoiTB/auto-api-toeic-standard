package api.test;

import api.model.user.dto.DbAddresses;
import api.model.user.dto.DbUser;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;

public class CheckDbTest {
    private static SessionFactory sessionFactory;
    @Test
    void CheckDatabaseConnection(){
        final StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .build();
        try {
            sessionFactory =
                    new MetadataSources(registry)
                            .addAnnotatedClass(DbUser.class)
                            .addAnnotatedClass(DbAddresses.class)
                            .buildMetadata()
                            .buildSessionFactory();
            sessionFactory.inTransaction(session -> {
                session.createSelectionQuery("from DbUser", DbUser.class)
                        .getResultList()
                        .forEach(customers -> System.out.println(customers.getId()));
            });
            sessionFactory.inTransaction(session -> {
                session.createSelectionQuery("from DbAddresses", DbAddresses.class)
                        .getResultList()
                        .forEach(addresses -> System.out.println(addresses.getId()));

            });
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we
            // had trouble building the SessionFactory so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
