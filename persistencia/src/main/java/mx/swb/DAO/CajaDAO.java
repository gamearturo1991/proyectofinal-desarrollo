package mx.swb.DAO;

import jakarta.persistence.EntityManager;
import mx.swb.entity.Caja;
import mx.swb.persistence.AbstractDAO;
import java.util.List;

public class CajaDAO extends AbstractDAO<Caja> {

    private final EntityManager entityManager;

    public CajaDAO(EntityManager em) {
        super(Caja.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    // Obtener la caja activa (estado = abierta) de un usuario (opcional)
    public Caja findCajaAbiertaPorUsuario(Integer usuarioId) {
        return execute(em -> {
            try {
                return em.createQuery("SELECT c FROM Caja c WHERE c.usuario.id = :usuarioId AND c.estado = mx.swb.entity.Caja.Estado.abierta", Caja.class)
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();
            } catch (Exception e) {
                return null;
            }
        });
    }

    // Obtener todas las cajas (para listado)
    public List<Caja> findAll() {
        return execute(em -> em.createQuery("SELECT c FROM Caja c ORDER BY c.fechaApertura DESC", Caja.class).getResultList());
    }

    public Caja findById(Integer id) {
        return find(id).orElse(null);
    }
}
