package mx.swb.delegate;

import mx.swb.DAO.ProductoDAO;
import mx.swb.entity.Producto;
import mx.swb.integration.ServiceLocator;

public class DelegateProducto {

    private final ProductoDAO productoDAO;

    public DelegateProducto() {
        this.productoDAO = ServiceLocator.getInstanceProductoDAO();
    }

    public Producto buscarPorSku(String sku) {
        return productoDAO.findByOneParameterUnique(sku, "sku");
    }

    public Producto buscarPorCodigoBarras(String codigoBarras) {
        return productoDAO.findByOneParameterUnique(codigoBarras, "codigoBarras");
    }

    public void guardar(Producto producto) {
        productoDAO.save(producto);
    }
}
