package mx.swb.facade;

import mx.swb.entity.Producto;
import mx.swb.negocio.ProductoService;

import java.math.BigDecimal;
import java.util.List;

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

        ProductoService service = new ProductoService();
        return service.registrarProducto(
            codigoBarras, sku, nombre, descripcion, unidadMedida,
            precioCompra, precioVenta, stockMinimo, stockMaximo,
            controlaCaducidad, diasAlertaCaducidad, requiereLote, activo
        );
    }
    public void darDeBaja(Integer id) {
        ProductoService service = new ProductoService();
        service.darDeBaja(id);
    }

}
public List<Producto> listarActivos() {
    ProductoService service = new ProductoService();
    return service.listarActivos();
}
