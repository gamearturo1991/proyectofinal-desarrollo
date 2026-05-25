package mx.swb.DAO;

import jakarta.persistence.EntityManager;
import mx.swb.entity.Producto;
import mx.swb.persistence.AbstractDAO;
import mx.swb.entity.Lote;

import java.util.List;

public class LoteDAO extends AbstractDAO<Lote> {

    private final EntityManager entityManager;

    public LoteDAO(EntityManager em) {
        super(Lote.class);
        this.entityManager = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    // Lotes activos de un producto ordenados por fecha de caducidad
    public List<Lote> findActivosPorProducto(Integer productoId) {
        return execute(em ->
            em.createQuery(
                    "SELECT l FROM Lote l " +
                        "WHERE l.producto.id = :productoId " +
                        "AND l.activo = true " +
                        "AND l.cantidadActual > 0 " +
                        "ORDER BY l.fechaCaducidad ASC NULLS LAST, l.id ASC",
                    Lote.class)
                .setParameter("productoId", productoId)
                .getResultList()
        );
    }

    // Stock total actual de un producto sumando todos sus lotes activos
    public int stockActualProducto(Integer productoId) {
        return execute(em -> {
            Long result = em.createQuery(
                    "SELECT COALESCE(SUM(l.cantidadActual), 0) FROM Lote l " +
                        "WHERE l.producto.id = :productoId AND l.activo = true",
                    Long.class)
                .setParameter("productoId", productoId)
                .getSingleResult();
            return result.intValue();
        });
    }

    // Buscar lotes con filtros dinámicos
    public List<Lote> buscarConFiltros(String busqueda, String filtroEstado) {
        return execute(em -> {
            String jpql = "SELECT l FROM Lote l JOIN l.producto p WHERE 1=1";
            // Búsqueda por código de lote o nombre de producto
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                jpql += " AND (l.codigoLote LIKE :busqueda OR p.nombre LIKE :busqueda)";
            }
            // Filtro de estado
            if ("activos".equals(filtroEstado)) {
                jpql += " AND l.cantidadActual > 0";
            } else if ("porVencer".equals(filtroEstado)) {
                jpql += " AND l.fechaCaducidad BETWEEN CURRENT_DATE AND CURRENT_DATE + 30 DAY";
            } else if ("vencidos".equals(filtroEstado)) {
                jpql += " AND l.fechaCaducidad < CURRENT_DATE";
            }
            jpql += " ORDER BY l.fechaCaducidad ASC NULLS LAST";

            var query = em.createQuery(jpql, Lote.class);
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                String like = "%" + busqueda.trim() + "%";
                query.setParameter("busqueda", like);
            }
            return query.getResultList();
        });
    }


    // Obtener todos los productos activos
    public List<Producto> productosActivos() {
        return execute(em ->
            em.createQuery("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.nombre", Producto.class)
                .getResultList()
        );
    }

    // Se usa en la devolución de edición cuando no hay lotes activos
    public List<Lote> findTodosPorProducto(Integer productoId) {
        return execute(em ->
            em.createQuery(
                    "SELECT l FROM Lote l " +
                        "WHERE l.producto.id = :productoId " +
                        "ORDER BY l.activo DESC, l.id DESC",
                    Lote.class)
                .setParameter("productoId", productoId)
                .getResultList()
        );
    }
}
