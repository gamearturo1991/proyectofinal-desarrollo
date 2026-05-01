package mx.swb.delegate;

import mx.swb.DAO.VentaDAO;
import mx.swb.entity.Venta;
import mx.swb.integration.ServiceLocator;

import java.util.List;

public class DelegateVenta {

    private final VentaDAO ventaDAO;

    public DelegateVenta() {
        this.ventaDAO = ServiceLocator.getInstanceVentaDAO();
    }

    public void guardarVenta(Venta venta) {
        ventaDAO.save(venta);
    }

    public List<Venta> obtenerVentasHoy() {
        return ventaDAO.findHoy();
    }

    public List<Venta> obtenerVentasPorCaja(Integer cajaId) {
        return ventaDAO.findByCaja(cajaId);
    }
}
