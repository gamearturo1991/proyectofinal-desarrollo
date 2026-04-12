package mx.swb.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("refresqueria_pu");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close(EntityManager em) {
        if (em != null && em.isOpen()) em.close();
    }
}
