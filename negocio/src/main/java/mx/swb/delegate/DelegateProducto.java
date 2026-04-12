package mx.swb.delegate;

import mx.swb.entity.Producto;
import mx.swb.integration.ServiceLocator;
import mx.swb.DAO.ProductoDAO;

public class DelegateProducto {

    public void guardar(Producto producto) {
        ProductoDAO productoDAO = ServiceLocator.getInstanceProductoDAO();
        productoDAO.save(producto);
    }
}

