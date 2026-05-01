package mx.swb.delegate;

import mx.swb.DAO.LoteDAO;
import mx.swb.entity.Lote;
import mx.swb.entity.Producto;
import mx.swb.integration.ServiceLocator;

import java.util.List;

import mx.swb.DAO.ProductoDAO;

public class DelegateLote {
    private final LoteDAO loteDAO;
    private final ProductoDAO productoDAO;

    public DelegateLote() {
        this.loteDAO = ServiceLocator.getInstanceLoteDAO();
        this.productoDAO = ServiceLocator.getInstanceProductoDAO();
    }

    public List<Lote> buscarConFiltros(String busqueda, String filtroEstado) {
        return loteDAO.buscarConFiltros(busqueda, filtroEstado);
    }

    public List<Producto> listarProductosActivos() {
        return loteDAO.productosActivos();
    }

    public List<Lote> obtenerLotesActivosPorProducto(Integer productoId) {
        return loteDAO.findActivosPorProducto(productoId);
    }

    public int obtenerStockActual(Integer productoId) {
        return loteDAO.stockActualProducto(productoId);
    }

    public void actualizarLote(Lote lote) {
        loteDAO.update(lote);
    }

    public List<Producto> listarProductosActivosConStock() {
        List<Producto> productos = productoDAO.findActivos();

        for (Producto p : productos) {
            int stock = loteDAO.stockActualProducto(p.getId());
            p.setStockActual(stock);
        }
        return productos;
    }

    public void guardar(Lote lote) {
        loteDAO.save(lote);
    }
}
