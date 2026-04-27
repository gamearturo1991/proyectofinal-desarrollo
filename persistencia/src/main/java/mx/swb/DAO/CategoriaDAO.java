package mx.swb.DAO;

import jakarta.persistence.EntityManager;
import mx.swb.entity.Categoria;
import mx.swb.persistence.AbstractDAO;

public class CategoriaDAO extends AbstractDAO<Categoria> {

    private final EntityManager entityManager;

    public CategoriaDAO(EntityManager em) {
        super(Categoria.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
