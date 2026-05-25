package mx.swb.DAO;

import jakarta.persistence.EntityManager;
import mx.swb.persistence.AbstractDAO;
import mx.swb.entity.Venta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        return execute(em -> {
            ZoneId tijuana = ZoneId.of("America/Tijuana");
            LocalDateTime inicio = LocalDate.now(tijuana).atStartOfDay();
            LocalDateTime fin    = inicio.plusDays(1);
            return em.createQuery(
                    "SELECT DISTINCT v FROM Venta v " +
                        "LEFT JOIN FETCH v.detalles d " +
                        "LEFT JOIN FETCH d.producto " +
                        "WHERE v.fechaCreado >= :inicio " +
                        "AND v.fechaCreado < :fin " +
                        "ORDER BY v.fechaCreado DESC",
                    Venta.class)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
        });
    }

    public List<Venta> obtenerTodasVentas() {
        return execute(em -> {
            return em.createQuery(
                    "SELECT DISTINCT v FROM Venta v " +
                        "LEFT JOIN FETCH v.detalles d " +
                        "LEFT JOIN FETCH d.producto " +
                        "ORDER BY v.fechaCreado DESC",
                    Venta.class)
                .getResultList();
        });
    }
}
