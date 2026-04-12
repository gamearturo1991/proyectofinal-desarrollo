package mx.swb.facade;

import mx.swb.entity.Producto;
import mx.swb.integration.ServiceLocator;
import mx.swb.negocio.ProductoService;

import java.math.BigDecimal;

public class FacadeProducto {

    public Producto registrar(
        String codigoBarras,
        String sku,
        String nombre,
        String descripcion,
        Producto.UnidadMedida unidadMedida,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal stockMinimo,
        BigDecimal stockMaximo,
        Boolean controlaCaducidad,
        Integer diasAlertaCaducidad,
        Boolean requiereLote,
        Boolean activo
    ) {
        ProductoService service = new ProductoService(
            ServiceLocator.getInstanceProductoDAO().getEntityManager()
        );

        return service.registrarProducto(
            codigoBarras,
            sku,
            nombre,
            descripcion,
            unidadMedida,
            precioCompra,
            precioVenta,
            stockMinimo,
            stockMaximo,
            controlaCaducidad,
            diasAlertaCaducidad,
            requiereLote,
            activo
        );
    }
}
