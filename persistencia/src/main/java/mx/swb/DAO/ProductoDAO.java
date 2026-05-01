package mx.swb.DAO;

import jakarta.persistence.EntityManager;
import mx.swb.persistence.AbstractDAO;
import mx.swb.entity.Producto;

import java.util.List;

public class ProductoDAO extends AbstractDAO<Producto> {

    private final EntityManager entityManager;

    public ProductoDAO(EntityManager em) {
        super(Producto.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    // productos activos
    public List<Producto> findActivos() {
        return execute(em ->
            em.createQuery(
                    "SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.nombre ASC",
                    Producto.class)
                .getResultList()
        );
    }
}
