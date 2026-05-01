package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.swb.entity.Caja;
import mx.swb.entity.Producto;
import mx.swb.entity.Venta;
import mx.swb.facade.FacadeCaja;
import mx.swb.facade.FacadeProducto;
import mx.swb.facade.FacadeVenta;
import mx.swb.negocio.VentaService.ItemVenta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("ventaBeanUI")
@ViewScoped
public class VentaBeanUI implements Serializable {

    // carrito
    private List<ItemCarrito> carrito = new ArrayList<>();

    // datos de la venta
    private String clienteNombre;
    private BigDecimal descuentoGlobal = BigDecimal.ZERO;
    private BigDecimal impuestoPorcentaje = BigDecimal.ZERO;
    private Venta.MetodoPago metodoPago = Venta.MetodoPago.efectivo;
    private BigDecimal montoRecibido = BigDecimal.ZERO;

    private BigDecimal montoApertura;
    private BigDecimal montoCierre;
    private String observacionesCaja;

    // id de sesion
    private Integer usuarioId = 1;

    private Venta ultimaVenta;

    private FacadeVenta facadeVenta;
    private FacadeProducto facadeProducto;
    private FacadeCaja facadeCaja;

    private Caja cajaActiva;
    private List<Producto> productosCatalogo;
    private String filtroNombre;

    // clase item del carrito
    public static class ItemCarrito implements Serializable {

        private Integer productoId;
        private String nombre;
        private BigDecimal precioUnitario;
        private Integer cantidad;

        public ItemCarrito(Integer productoId, String nombre, BigDecimal precioUnitario, Integer cantidad) {
            this.productoId = productoId;
            this.nombre = nombre;
            this.precioUnitario = precioUnitario;
            this.cantidad = cantidad;
        }

        public BigDecimal getSubtotal() {
            return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
        }

        public Integer getProductoId(){
            return productoId;
        }
        public void setProductoId(Integer id){
            this.productoId = id;
        }
        public String getNombre(){
            return nombre;
        }
        public void setNombre(String n){
            this.nombre = n;
        }
        public BigDecimal getPrecioUnitario(){
            return precioUnitario;
        }
        public void setPrecioUnitario(BigDecimal p){
            this.precioUnitario = p;
        }
        public Integer getCantidad(){
            return cantidad;
        }
        public void setCantidad(Integer c){
            this.cantidad = c;
        }
    }

    @PostConstruct
    public void init() {
        facadeVenta = new FacadeVenta();
        facadeProducto = new FacadeProducto();
        facadeCaja = new FacadeCaja();
        cajaActiva = facadeCaja.obtenerCajaAbierta();
        if (cajaActiva == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "No hay caja abierta", "Abra una caja antes de vender."));
        }
        cargarProductosCatalogo();
    }

    public void cargarProductosCatalogo() {
        try {
            List<Producto> todos = facadeProducto.listarProductosActivosConStock();
            if (filtroNombre != null && !filtroNombre.trim().isEmpty()) {
                String filtro = filtroNombre.trim().toLowerCase();
                productosCatalogo = todos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(filtro) ||
                        (p.getCodigoBarras() != null && p.getCodigoBarras().contains(filtro)))
                    .collect(Collectors.toList());
            } else {
                productosCatalogo = todos;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cargar productos", e.getMessage()));
        }
    }

    public void filtrarProductos() {
        cargarProductosCatalogo();
    }

    //  Operaciones del carrito

    public void agregarProducto(Integer productoId, String nombre, double precio) {
        BigDecimal precioDecimal = BigDecimal.valueOf(precio);
        for (ItemCarrito item : carrito) {
            if (item.getProductoId().equals(productoId)) {
                item.setCantidad(item.getCantidad() + 1);
                return;
            }
        }
        carrito.add(new ItemCarrito(productoId, nombre, precioDecimal, 1));
    }

    // Suma 1 unidad al producto en el carrito.
    public void aumentarCantidad(Integer productoId) {
        for (ItemCarrito item : carrito) {
            if (item.getProductoId().equals(productoId)) {
                item.setCantidad(item.getCantidad() + 1);
                return;
            }
        }
    }

    // Resta 1 unidad. Si llega a 0 elimina el item del carrito.
    public void disminuirCantidad(Integer productoId) {
        for (int i = 0; i < carrito.size(); i++) {
            if (carrito.get(i).getProductoId().equals(productoId)) {
                if (carrito.get(i).getCantidad() <= 1) {
                    carrito.remove(i);
                } else {
                    carrito.get(i).setCantidad(carrito.get(i).getCantidad() - 1);
                }
                return;
            }
        }
    }

    // Elimina completamente el producto del carrito sin importar la cantidad.
    public void eliminarItem(Integer productoId) {
        carrito.removeIf(item -> item.getProductoId().equals(productoId));
    }

    // Vacía el carrito y reinicia todos los campos del formulario.
    public void limpiarCarrito() {
        carrito.clear();
        clienteNombre = null;
        descuentoGlobal = BigDecimal.ZERO;
        impuestoPorcentaje = BigDecimal.ZERO;
        metodoPago = Venta.MetodoPago.efectivo;
        montoRecibido = BigDecimal.ZERO;
        ultimaVenta = null;
    }

    //  Calculos en tiempo real

    public BigDecimal getSubtotal() {
        return carrito.stream().map(ItemCarrito::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getDescuentoAplicado() {
        return descuentoGlobal != null
            ? descuentoGlobal.setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getImpuestoCalculado() {
        if (impuestoPorcentaje == null
            || impuestoPorcentaje.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal base = getSubtotal()
            .subtract(getDescuentoAplicado())
            .max(BigDecimal.ZERO);

        return base.multiply(impuestoPorcentaje)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        BigDecimal base = getSubtotal()
            .subtract(getDescuentoAplicado())
            .max(BigDecimal.ZERO);
        return base.add(getImpuestoCalculado())
            .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCambio() {
        if (metodoPago != Venta.MetodoPago.efectivo
            || montoRecibido == null
            || montoRecibido.compareTo(BigDecimal.ZERO) <= 0)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return montoRecibido.subtract(getTotal())
            .max(BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);
    }

    // Total de unidades en el carrito (suma de cantidades)
    public int getTotalItems() {
        return carrito.stream().mapToInt(ItemCarrito::getCantidad).sum();
    }

    //  Registro de venta
    public void registrarVenta() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (cajaActiva == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No hay una caja abierta. Debe abrir caja antes de vender.", null));
            return;
        }

        // validaciones de UI --
        if (carrito.isEmpty()) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                "El carrito esta vacio. Agrega al menos un producto.", null));
            return;
        }

        if (metodoPago == null) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Selecciona un metodo de pago.", null));
            return;
        }

        if (metodoPago == Venta.MetodoPago.efectivo) {
            if (montoRecibido == null
                || montoRecibido.compareTo(BigDecimal.ZERO) <= 0) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ingresa el monto recibido.", null));
                return;
            }
            if (montoRecibido.compareTo(getTotal()) < 0) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El monto recibido ($"
                        + montoRecibido.setScale(2, RoundingMode.HALF_UP)
                        + ") es menor al total ($" + getTotal() + ").", null));
                return;
            }
        }

        try {
            List<ItemVenta> items = new ArrayList<>();
            for (ItemCarrito ic : carrito) {
                items.add(new ItemVenta(
                    ic.getProductoId(),
                    ic.getNombre(),
                    ic.getCantidad(),
                    ic.getPrecioUnitario()
                ));
            }

            ultimaVenta = facadeVenta.registrar(
                items,
                usuarioId,
                cajaActiva.getId(),
                clienteNombre,
                null,
                descuentoGlobal,
                impuestoPorcentaje,
                metodoPago,
                montoRecibido
            );

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Venta registrada correctamente. Folio #" + ultimaVenta.getId(),
                null));

            limpiarCarrito();

        } catch (IllegalArgumentException | IllegalStateException e) {
            ctx.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR, e.getMessage(), null));

        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Error inesperado al registrar la venta",
                e.getMessage()));
        }
    }

    //  Helpers metodo de pago
    public Venta.MetodoPago[] getMetodosPago() {
        return Venta.MetodoPago.values();
    }

    public boolean isEfectivo() {
        return Venta.MetodoPago.efectivo.equals(metodoPago);
    }

    public List<ItemCarrito> getCarrito() {
        return carrito;
    }

    public void setCarrito(List<ItemCarrito> carrito) {
        this.carrito = carrito;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public BigDecimal getDescuentoGlobal() {
        return descuentoGlobal;
    }

    public void setDescuentoGlobal(BigDecimal descuentoGlobal) {
        this.descuentoGlobal = descuentoGlobal;
    }

    public BigDecimal getImpuestoPorcentaje() {
        return impuestoPorcentaje;
    }

    public void setImpuestoPorcentaje(BigDecimal impuestoPorcentaje) {
        this.impuestoPorcentaje = impuestoPorcentaje;
    }

    public Venta.MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(Venta.MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public BigDecimal getMontoApertura() {
        return montoApertura;
    }

    public void setMontoApertura(BigDecimal montoApertura) {
        this.montoApertura = montoApertura;
    }

    public BigDecimal getMontoCierre() {
        return montoCierre;
    }

    public void setMontoCierre(BigDecimal montoCierre) {
        this.montoCierre = montoCierre;
    }

    public Caja getCajaActiva() {
        return cajaActiva;
    }

    public void setCajaActiva(Caja cajaActiva) {
        this.cajaActiva = cajaActiva;
    }

    public List<Producto> getProductosCatalogo() {
        return productosCatalogo;
    }

    public void setProductosCatalogo(List<Producto> productosCatalogo) {
        this.productosCatalogo = productosCatalogo;
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public void setFiltroNombre(String filtroNombre) {
        this.filtroNombre = filtroNombre;
    }

    public int getVentasDelDia() {
        if (cajaActiva == null || cajaActiva.getId() == null) {
            return 0;
        }
        return facadeCaja.contarVentasDelDia(cajaActiva.getId());
    }

    public void abrirCaja() {
        try {
            Caja nueva = facadeCaja.abrirCaja(usuarioId, montoApertura, observacionesCaja);
            this.cajaActiva = nueva;
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Caja abierta correctamente", null));
            montoApertura = null;
            observacionesCaja = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al abrir caja", e.getMessage()));
        }
    }

    public void cerrarCaja() {
        if (cajaActiva == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "No hay caja abierta", null));
            return;
        }
        if (montoCierre == null || montoCierre.compareTo(BigDecimal.ZERO) <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Monto de cierre inválido", null));
            return;
        }
        try {
            facadeCaja.cerrarCaja(cajaActiva.getId(), montoCierre, observacionesCaja);
            cajaActiva = null;  // ya no hay caja activa
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Caja cerrada correctamente", null));
            montoCierre = null;
            observacionesCaja = null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al cerrar caja", e.getMessage()));
        }
    }
}
