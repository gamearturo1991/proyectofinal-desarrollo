// ── FacadeVenta.java ──  mx/swb/facade/FacadeVenta.java
package mx.swb.facade;

import mx.swb.delegate.DelegateVenta;
import mx.swb.entity.Venta;
import mx.swb.negocio.VentaService;
import mx.swb.negocio.VentaService.ItemVenta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FacadeVenta {

    private VentaService service;

    public FacadeVenta() {
        service = new VentaService();
    }

    public Venta registrar(
        List<ItemVenta> items,
        Integer usuarioId,
        Integer cajaId,
        String clienteNombre,
        String clienteDocumento,
        BigDecimal descuentoGlobal,
        BigDecimal impuestoPorcentaje,
        Venta.MetodoPago metodoPago,
        BigDecimal montoRecibido
    ) {
        return service.registrarVenta(
            items, usuarioId, cajaId,
            clienteNombre, clienteDocumento,
            descuentoGlobal, impuestoPorcentaje,
            metodoPago, montoRecibido
        );
    }

    public List<Venta> obtenerHoy() {
        return service.obtenerVentasHoy();
    }
}
