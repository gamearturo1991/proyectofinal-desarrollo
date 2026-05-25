package mx.swb.delegate;

import mx.swb.DAO.DetalleVentaDAO;
import mx.swb.DAO.VentaDAO;
import mx.swb.entity.*;
import mx.swb.integration.ServiceLocator;

import java.util.List;
import java.util.Optional;

public class DelegateVenta {

    private final VentaDAO        ventaDAO;
    private final DetalleVentaDAO detalleVentaDAO;

    public DelegateVenta() {
        this.ventaDAO        = ServiceLocator.getInstanceVentaDAO();
        this.detalleVentaDAO = ServiceLocator.getInstanceDetalleVentaDAO();
    }

    // ── Venta ──

    public void guardarVenta(Venta venta) {
        ventaDAO.save(venta);
    }

    public void actualizarVenta(Venta venta) {
        ventaDAO.update(venta);
    }

    public Venta buscarVentaPorId(Integer ventaId) {
        Optional<Venta> opt = ventaDAO.find(ventaId);
        return opt.orElse(null);
    }

    public List<Venta> obtenerVentasHoy() {
        return ventaDAO.findHoy();
    }

    public List<Venta> obtenerTodasVentas() {
        return ventaDAO.obtenerTodasVentas();
    }

    public List<Venta> obtenerVentasPorCaja(Integer cajaId) {
        return ventaDAO.findByCaja(cajaId);
    }

    // ── DetalleVenta ──

    public DetalleVenta buscarDetallePorId(Integer detalleId) {
        Optional<DetalleVenta> opt = detalleVentaDAO.find(detalleId);
        return opt.orElse(null);
    }

    public void actualizarDetalle(DetalleVenta detalle) {
        detalleVentaDAO.update(detalle);
    }

    public void eliminarDetalle(DetalleVenta detalle) {
        detalleVentaDAO.delete(detalle);
    }

    public void guardarDetalle(DetalleVenta detalle) {
        detalleVentaDAO.save(detalle);
    }

}
