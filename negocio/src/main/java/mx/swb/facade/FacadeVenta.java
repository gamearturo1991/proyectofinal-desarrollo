package mx.swb.facade;

import mx.swb.entity.Venta;
import mx.swb.negocio.ItemEdicionDTO;
import mx.swb.negocio.VentaService;
import mx.swb.negocio.VentaService.ItemVenta;

import java.math.BigDecimal;
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

    public void editarVenta(Integer ventaId, List<ItemEdicionDTO> items, BigDecimal montoRecibido) {
        VentaService service = new VentaService();
        service.editarVenta(ventaId, items, montoRecibido);
    }

    public List<Venta> obtenerHoy() {
        return service.obtenerVentasHoy();
    }

    public List<Venta> obtenerTodasVentas() {
        return service.obtenerTodasVentas();
    }
}
