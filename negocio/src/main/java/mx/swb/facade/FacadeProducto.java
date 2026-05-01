package mx.swb.facade;

import mx.swb.delegate.DelegateLote;
import mx.swb.entity.Producto;
import mx.swb.negocio.ProductoService;
import mx.swb.negocio.VentaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FacadeProducto {

    private ProductoService service;

    public FacadeProducto() {
        service = new ProductoService();
    }

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
        Boolean activo,
        Integer cantidadInicial,
        LocalDate fechaCaducidadLote,
        String numeroLote
    ) {
        return service.registrarProducto(
            codigoBarras, sku, nombre, descripcion, unidadMedida,
            precioCompra, precioVenta, stockMinimo, stockMaximo,
            controlaCaducidad, diasAlertaCaducidad, requiereLote, activo, cantidadInicial, fechaCaducidadLote, numeroLote
        );
    }

    public List<Producto> listarProductosActivosConStock() {
        DelegateLote delegate = new DelegateLote();
        return delegate.listarProductosActivosConStock();
    }
}
