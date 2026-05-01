package mx.swb.facade;

import jakarta.persistence.EntityManager;
import mx.swb.delegate.DelegateCaja;
import mx.swb.delegate.DelegateVenta;
import mx.swb.entity.Caja;
import mx.swb.entity.Usuario;
import mx.swb.entity.Venta;
import mx.swb.integration.ServiceLocator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FacadeCaja {

    private final DelegateCaja delegate = new DelegateCaja();

    public Caja abrirCaja(Integer usuarioId, BigDecimal montoApertura, String observaciones) {
        if (montoApertura == null || montoApertura.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de apertura debe ser mayor a cero");
        }
        if (obtenerCajaAbierta() != null) {
            throw new IllegalStateException("Ya existe una caja abierta, ciérrela antes de abrir otra");
        }
        EntityManager em = ServiceLocator.getEntityManager();
        Usuario usuario = em.getReference(Usuario.class, usuarioId);
        Caja nueva = new Caja();
        nueva.setUsuario(usuario);
        nueva.setFechaApertura(LocalDate.now());
        nueva.setMontoApertura(montoApertura);
        nueva.setEstado(Caja.Estado.abierta);
        nueva.setObservaciones(observaciones);
        delegate.guardar(nueva);
        return nueva;
    }

    public void cerrarCaja(Integer cajaId, BigDecimal montoCierre, String observaciones) {
        Caja caja = delegate.buscarPorId(cajaId);
        if (caja != null && caja.getEstado() == Caja.Estado.abierta) {
            caja.setFechaCierre(LocalDate.now());
            caja.setMontoCierre(montoCierre);
            caja.setEstado(Caja.Estado.cerrada);
            if (observaciones != null) caja.setObservaciones(observaciones);
            delegate.guardar(caja);
        }
    }

    public Caja obtenerCajaAbierta() {
        return delegate.findCajaAbiertaPorUsuario(1);
    }

    public List<Caja> listarTodas() {
        return delegate.listarTodas();
    }

    public int contarVentasDelDia(Integer cajaId) {
        DelegateVenta delegateVenta = new DelegateVenta();
        List<Venta> ventas = delegateVenta.obtenerVentasPorCaja(cajaId);
        LocalDate hoy = LocalDate.now();
        return (int) ventas.stream()
            .filter(v -> v.getFechaCreado().toLocalDate().equals(hoy))
            .count();
    }
}
