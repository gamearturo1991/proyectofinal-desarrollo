package mx.swb.negocio;

import jakarta.persistence.EntityManager;
import mx.swb.delegate.DelegateLote;
import mx.swb.delegate.DelegateMovimientoInventario;
import mx.swb.delegate.DelegateVenta;
import mx.swb.entity.*;
import mx.swb.integration.ServiceLocator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class VentaService {

    private final DelegateVenta delegateVenta;
    private final DelegateLote delegateLote;
    private final DelegateMovimientoInventario delegateMovimientoInventario;

    // Clase ItemVenta para cada linea del carrito
    public static class ItemVenta {
        private final Integer productoId;
        private final String nombreProducto;
        private final Integer cantidad;
        private final BigDecimal precioUnitario;

        public ItemVenta(Integer productoId, String nombreProducto, Integer cantidad, BigDecimal precioUnitario) {
            this.productoId = productoId;
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        public Integer getProductoId(){
            return productoId;
        }

        public String getNombreProducto(){
            return nombreProducto;
        }

        public Integer getCantidad(){
            return cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }
    }

    public VentaService() {
        this.delegateVenta = new DelegateVenta();
        this.delegateLote = new DelegateLote();
        this.delegateMovimientoInventario = new DelegateMovimientoInventario();
    }

    public Venta registrarVenta(
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
        EntityManager em = ServiceLocator.getEntityManager();
        Usuario usuario = em.find(Usuario.class, usuarioId);

        // Validaciones
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("La venta debe tener al menos un producto");

        if (metodoPago == null)
            throw new IllegalArgumentException("El metodo de pago es obligatorio");

        if (metodoPago == Venta.MetodoPago.efectivo) {
            if (montoRecibido == null || montoRecibido.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("El monto recibido es obligatorio para pago en efectivo");
        }

        for (ItemVenta item : items) {
            if (item.getCantidad() <= 0)
                throw new IllegalArgumentException("La cantidad de " + item.getNombreProducto() + " debe ser mayor a 0");

            int stockDisponible = delegateLote.obtenerStockActual(item.getProductoId());
            if (stockDisponible < item.getCantidad())
                throw new IllegalStateException("Stock insuficiente para " + item.getNombreProducto() + " — disponible: " + stockDisponible + ", solicitado: " + item.getCantidad());
        }

        // Calculos
        BigDecimal subtotal = items.stream().map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad()))).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = descuentoGlobal != null ? descuentoGlobal : BigDecimal.ZERO;
        BigDecimal baseImpuesto = subtotal.subtract(descuento).max(BigDecimal.ZERO);
        BigDecimal impuestoPct = impuestoPorcentaje != null ? impuestoPorcentaje : BigDecimal.ZERO;
        BigDecimal impuesto = baseImpuesto.multiply(impuestoPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = baseImpuesto.add(impuesto);

        BigDecimal cambio = BigDecimal.ZERO;
        if (metodoPago == Venta.MetodoPago.efectivo && montoRecibido != null) {
            if (montoRecibido.compareTo(total) < 0)
                throw new IllegalArgumentException("El monto recibido es menor al total a cobrar ($" + total + ")");
            cambio = montoRecibido.subtract(total);
        }

        // tipo de movimiento salida_venta
        TipoMovimientoInventario tipoSalida =
            delegateMovimientoInventario.buscarTipoMovimiento("salida_venta");
        if (tipoSalida == null)
            throw new IllegalStateException("No se encontro el tipo de movimiento 'salida_venta'. " + "Verifica que los datos iniciales esten insertados en tipo_movimiento_inventarios.");

        // Construccion de la Venta
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setCaja(crearRefCaja(cajaId));
        venta.setClienteNombre(clienteNombre != null && !clienteNombre.isBlank() ? clienteNombre.trim() : null);
        venta.setClienteDocumento(clienteDocumento);
        venta.setSubtotal(subtotal);
        venta.setDescuento(descuento);
        venta.setImpuesto(impuesto);
        venta.setTotal(total);
        venta.setMetodoPago(metodoPago);
        venta.setMontoRecibido(metodoPago == Venta.MetodoPago.efectivo ? montoRecibido : null);
        venta.setMontoCambio(metodoPago == Venta.MetodoPago.efectivo ? cambio : null);
        venta.setEstado(Venta.Estado.completada);
        delegateVenta.guardarVenta(venta);

        // Detalles y Movimientos por Item
        for (ItemVenta item : items) {
            Producto producto = crearRefProducto(item.getProductoId());
            int stockAntes = delegateLote.obtenerStockActual(item.getProductoId());
            int restante = item.getCantidad();
            Lote loteUsado = null;

            // Descontar stock
            List<Lote> lotes = delegateLote.obtenerLotesActivosPorProducto(item.getProductoId());
            for (Lote lote : lotes) {
                if (restante <= 0) break;
                int descontar = Math.min(restante, lote.getCantidadActual());
                lote.setCantidadActual(lote.getCantidadActual() - descontar);
                delegateLote.actualizarLote(lote);
                restante  -= descontar;
                loteUsado  = lote;
            }

            // Detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setLote(loteUsado);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setDescuentoUnitario(BigDecimal.ZERO);
            detalle.setSubtotal(item.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(item.getCantidad())));

            // movimiento de inventario
            int stockDespues = stockAntes - item.getCantidad();
            MovimientoInventario mov = new MovimientoInventario();
            mov.setUsuario(usuario);
            mov.setProducto(producto);
            mov.setTipoMovimiento(tipoSalida);
            mov.setLote(loteUsado);
            mov.setVenta(venta);
            mov.setCantidad(item.getCantidad());
            mov.setPrecioUnitario(item.getPrecioUnitario());
            mov.setStockAntes(stockAntes);
            mov.setStockDespues(stockDespues);
            mov.setObservacion("Venta registrada en caja");

            venta.getDetalles().add(detalle);
            delegateMovimientoInventario.guardar(mov);
        }

        return venta;
    }

    public List<Venta> obtenerVentasHoy() {
        return delegateVenta.obtenerVentasHoy();
    }

    public List<Venta> obtenerVentasPorCaja(Integer cajaId) {
        return delegateVenta.obtenerVentasPorCaja(cajaId);
    }

    private Integer crearRefUsuario(Integer id) {
        return id;
    }

    private Caja crearRefCaja(Integer id) {
        Caja caja = new Caja();
        caja.setId(id);
        return caja;
    }

    private Producto crearRefProducto(Integer id) {
        Producto producto = new Producto();
        producto.setId(id);
        return producto;
    }
}
