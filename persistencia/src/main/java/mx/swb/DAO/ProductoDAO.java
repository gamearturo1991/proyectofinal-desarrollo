package mx.swb.DAO;

import jakarta.persistence.EntityManager;
import mx.swb.persistence.AbstractDAO;
import mx.swb.entity.Producto;

public class ProductoDAO extends AbstractDAO<Producto> {    private final EntityManager entityManager;

    public ProductoDAO(EntityManager em) {
        super(Producto.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void darDeBaja(Integer id) {
        find(id).ifPresent(producto -> {
            producto.setActivo(false);
            producto.setFechaBaja(java.time.LocalDateTime.now());
            update(producto);
        });
    }

}  // ← este es el único } de cierre de la clase
