package mx.swb.DAO;

import jakarta.persistence.EntityManager;
import mx.swb.persistence.AbstractDAO;
import mx.swb.entity.Venta;

import java.util.List;

public class VentaDAO extends AbstractDAO<Venta> {

    private final EntityManager entityManager;

    public VentaDAO(EntityManager em) {
        super(Venta.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    // Ventas de una caja especifica
    public List<Venta> findByCaja(Integer cajaId) {
        return execute(em ->
            em.createQuery(
                    "SELECT v FROM Venta v WHERE v.caja.id = :cajaId ORDER BY v.fechaCreado DESC",
                    Venta.class)
                .setParameter("cajaId", cajaId)
                .getResultList()
        );
    }

    // Ventas del dia
    public List<Venta> findHoy() {
        return execute(em ->
            em.createQuery(
                    "SELECT v FROM Venta v WHERE CAST(v.fechaCreado AS date) = CURRENT_DATE ORDER BY v.fechaCreado DESC",
                    Venta.class)
                .getResultList()
        );
    }
}
