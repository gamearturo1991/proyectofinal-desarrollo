package mx.swb.DAO;

import jakarta.persistence.EntityManager;
import mx.swb.persistence.AbstractDAO;
import mx.swb.entity.MovimientoInventario;
import mx.swb.entity.TipoMovimientoInventario;

import java.util.List;

public class MovimientoInventarioDAO extends AbstractDAO<MovimientoInventario> {

    private final EntityManager entityManager;

    public MovimientoInventarioDAO(EntityManager em) {
        super(MovimientoInventario.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    // Busca el tipo de movimiento por nombre (ej. "salida_venta")
    public TipoMovimientoInventario findTipoPorNombre(String nombre) {
        return execute(em -> {
            try {
                return em.createQuery(
                        "SELECT t FROM TipoMovimientoInventario t WHERE t.nombre = :nombre",
                        TipoMovimientoInventario.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
            } catch (jakarta.persistence.NoResultException e) {
                return null;
            }
        });
    }

    // Ultimos movimientos de un producto
    public List<MovimientoInventario> findUltimosPorProducto(Integer productoId, int limite) {
        return execute(em ->
            em.createQuery(
                    "SELECT m FROM MovimientoInventario m " +
                        "WHERE m.producto.id = :productoId " +
                        "ORDER BY m.fechaCreado DESC",
                    MovimientoInventario.class)
                .setParameter("productoId", productoId)
                .setMaxResults(limite)
                .getResultList()
        );
    }
}
