package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.swb.entity.DetalleVenta;
import mx.swb.entity.Producto;
import mx.swb.entity.Venta;
import mx.swb.facade.FacadeVenta;
import mx.swb.facade.FacadeProducto;
import mx.swb.negocio.ItemEdicionDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("ventaEditarBeanUI")
@ViewScoped
public class VentaEditarBeanUI implements Serializable {

    // Lista principal
    private List<Venta> ventas = new ArrayList<>();

    // Filtros
    private String busqueda = "";
    private String filtroMetodo = "";
    private String filtroEstado = "";

    // Edición
    private Venta ventaSeleccionada;
    private boolean panelAbierto = false;

    // Usa ItemEdicionDTO del paquete de negocio
    private List<ItemEdicionDTO> itemsEdicion = new ArrayList<>();

    // Agregar en edición
    private Integer productoAgregarId;
    private Integer cantidadAgregar = 1;
    private List<Producto> catalogoProductos = new ArrayList<>();
    private String busquedaProducto = "";
    private List<Producto> productosEncontrados = new ArrayList<>();
    private boolean busquedaRealizada = false;

    // Monto recibido en edición (solo efectivo)
    private BigDecimal montoRecibidoEdicion = BigDecimal.ZERO;

    private FacadeVenta facadeVenta;
    private FacadeProducto facadeProducto;

    @PostConstruct
    public void init() {
        facadeVenta = new FacadeVenta();
        facadeProducto = new FacadeProducto();
        cargarVentas();
        cargarCatalogo();
    }

    public void cargarVentas() {
        try {
            ventas = facadeVenta.obtenerTodasVentas();
            System.out.println("[VentaEditarBeanUI] Ventas cargadas: " + ventas.size());
        } catch (Exception e) {
            System.err.println("[VentaEditarBeanUI] Error: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cargar ventas: " + e.getMessage(), null));
        }
    }

    private void cargarCatalogo() {
        try {
            catalogoProductos = facadeProducto.listarProductosActivosConStock();
        } catch (Exception e) {
            catalogoProductos = new ArrayList<>();
            FacesContext ctx = FacesContext.getCurrentInstance();
            if (ctx != null) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cargar catálogo de productos: " + e.getMessage(), null));
            }
        }
    }

    //  Panel de edición
    public void seleccionarParaEditar(Venta venta) {
        this.ventaSeleccionada = venta;
        this.panelAbierto = true;
        this.cantidadAgregar = 1;
        this.productoAgregarId = null;
        this.montoRecibidoEdicion = venta.getMontoRecibido() != null ? venta.getMontoRecibido() : BigDecimal.ZERO;
        construirItemsEdicion(venta);
        cargarCatalogo();
    }

    private void construirItemsEdicion(Venta venta) {
        itemsEdicion = new ArrayList<>();
        if (venta.getDetalles() == null) return;
        for (DetalleVenta d : venta.getDetalles()) {
            itemsEdicion.add(new ItemEdicionDTO(
                d.getId(),
                d.getProducto() != null ? d.getProducto().getId()     : null,
                d.getProducto() != null ? d.getProducto().getNombre() : "Producto",
                d.getPrecioUnitario(),
                d.getCantidad(),
                d.getCantidad()
            ));
        }
    }

    public void buscarProductos() {
        busquedaRealizada = true;
        String termino = busquedaProducto != null ? busquedaProducto.trim().toLowerCase() : "";
        if (termino.isEmpty()) {
            productosEncontrados = new ArrayList<>();
            return;
        }
        try {
            List<Producto> todos = facadeProducto.listarProductosActivosConStock();
            productosEncontrados = todos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(termino))
                .limit(10)
                .collect(Collectors.toList());
        } catch (Exception e) {
            productosEncontrados = new ArrayList<>();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al buscar productos: " + e.getMessage(), null));
        }
    }

    public void cerrarPanel() {
        panelAbierto = false;
        ventaSeleccionada = null;
        itemsEdicion.clear();
        productosEncontrados = new ArrayList<>();
        busquedaProducto = "";
        busquedaRealizada = false;
        productoAgregarId = null;
    }

    //  Operaciones sobre items
    public void aumentarItem(Integer productoId) {
        for (ItemEdicionDTO item : itemsEdicion) {
            if (item.getProductoId().equals(productoId) && !item.isEliminado()) {
                item.setCantidad(item.getCantidad() + 1);
                return;
            }
        }
    }

    public void disminuirItem(Integer productoId) {
        for (ItemEdicionDTO item : itemsEdicion) {
            if (item.getProductoId().equals(productoId) && !item.isEliminado()) {
                int nueva = item.getCantidad() - 1;
                if (nueva <= 0) { item.setEliminado(true); item.setCantidad(0); }
                else             { item.setCantidad(nueva); }
                return;
            }
        }
    }

    public void quitarItem(Integer productoId) {
        for (ItemEdicionDTO item : itemsEdicion) {
            if (item.getProductoId().equals(productoId)) {
                item.setEliminado(true);
                item.setCantidad(0);
                return;
            }
        }
    }

    public void agregarProductoEdicion() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (productoAgregarId == null) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                "Selecciona un producto para agregar.", null));
            return;
        }
        if (cantidadAgregar == null || cantidadAgregar <= 0) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                "La cantidad debe ser mayor a 0.", null));
            return;
        }

        // Si ya existe, reactivar y sumar cantidad
        for (ItemEdicionDTO item : itemsEdicion) {
            if (item.getProductoId().equals(productoAgregarId)) {
                item.setEliminado(false);
                item.setCantidad(item.getCantidad() + cantidadAgregar);
                productoAgregarId = null;
                cantidadAgregar   = 1;
                return;
            }
        }

        // Producto nuevo: buscarlo en el catálogo
        Producto prod = catalogoProductos.stream()
            .filter(p -> p.getId().equals(productoAgregarId))
            .findFirst().orElse(null);

        if (prod == null) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Producto no encontrado en el catalogo.", null));
            return;
        }

        itemsEdicion.add(new ItemEdicionDTO(
            null,
            prod.getId(),
            prod.getNombre(),
            prod.getPrecioVenta(),
            0,
            cantidadAgregar
        ));

        productoAgregarId = null;
        cantidadAgregar   = 1;
    }

    //  Guardar edición
    public void guardarEdicion() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        if (ventaSeleccionada == null) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "No hay venta seleccionada.", null));
            return;
        }

        boolean hayActivos = itemsEdicion.stream()
            .anyMatch(i -> !i.isEliminado() && i.getCantidad() > 0);
        if (!hayActivos) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                "La venta debe tener al menos un producto activo.", null));
            return;
        }

        try {
            facadeVenta.editarVenta(ventaSeleccionada.getId(), itemsEdicion, montoRecibidoEdicion);

            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Venta #" + ventaSeleccionada.getId() + " actualizada correctamente.", null));

            cargarVentas();
            cerrarPanel();

        } catch (IllegalArgumentException | IllegalStateException e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                e.getMessage(), null));
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Error inesperado al guardar: " + e.getMessage(), null));
        }
    }

    //  Cálculos panel edición
    public BigDecimal getSubtotalEdicion() {
        return itemsEdicion.stream()
            .filter(i -> !i.isEliminado())
            .map(ItemEdicionDTO::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCambioEdicion() {
        if (ventaSeleccionada == null
                || ventaSeleccionada.getMetodoPago() != Venta.MetodoPago.efectivo
                || montoRecibidoEdicion == null) return BigDecimal.ZERO;
        return montoRecibidoEdicion.subtract(getTotalEdicion()).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalEdicion() {
        if (ventaSeleccionada == null) return BigDecimal.ZERO;
        BigDecimal desc = ventaSeleccionada.getDescuento() != null
            ? ventaSeleccionada.getDescuento() : BigDecimal.ZERO;
        BigDecimal imp  = ventaSeleccionada.getImpuesto()  != null
            ? ventaSeleccionada.getImpuesto()  : BigDecimal.ZERO;
        return getSubtotalEdicion().subtract(desc).max(BigDecimal.ZERO)
            .add(imp).setScale(2, RoundingMode.HALF_UP);
    }

    //  Cálculos resumen superior
    public BigDecimal getTotalDia() {
        return ventas.stream()
            .filter(v -> v.getEstado() == Venta.Estado.completada)
            .map(Venta::getTotal).filter(t -> t != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);
    }

    public long getTotalEfectivo() {
        return ventas.stream()
            .filter(v -> v.getMetodoPago() == Venta.MetodoPago.efectivo
                && v.getEstado()     == Venta.Estado.completada)
            .count();
    }

    public long getTotalNoEfectivo() {
        return ventas.stream()
            .filter(v -> v.getMetodoPago() != Venta.MetodoPago.efectivo
                && v.getEstado()     == Venta.Estado.completada)
            .count();
    }

    //  Filtrado
    public List<Venta> getVentasFiltradas() {
        return ventas.stream().filter(v -> {
            if (busqueda != null && !busqueda.isBlank()) {
                String b = busqueda.toLowerCase().trim();
                boolean ok = String.valueOf(v.getId()).contains(b)
                    || (v.getClienteNombre() != null
                    && v.getClienteNombre().toLowerCase().contains(b));
                if (!ok) return false;
            }
            if (filtroMetodo != null && !filtroMetodo.isBlank())
                if (!filtroMetodo.equalsIgnoreCase(v.getMetodoPago().name())) return false;
            if (filtroEstado != null && !filtroEstado.isBlank())
                if (!filtroEstado.equalsIgnoreCase(v.getEstado().name())) return false;
            return true;
        }).collect(Collectors.toList());
    }

    //  Helpers de vista
    public List<ItemEdicionDTO> getItemsVisibles() {
        return itemsEdicion.stream()
            .filter(i -> !i.isEliminado())
            .collect(Collectors.toList());
    }

    public boolean isHayCambios() {
        return itemsEdicion.stream()
            .anyMatch(i -> i.getDeltaCantidad() != 0 || i.isEliminado());
    }

    public String getLabelMetodo(String m) {
        if (m == null) return "";
        switch (m.toLowerCase()) {
            case "efectivo":      return "Efectivo";
            case "tarjeta":       return "Tarjeta";
            case "transferencia": return "Transferencia";
            default:              return m;
        }
    }

    public String getBadgeMetodo(String m) {
        if (m == null) return "badge";
        switch (m.toLowerCase()) {
            case "efectivo":      return "badge badge-efectivo";
            case "tarjeta":       return "badge badge-tarjeta";
            case "transferencia": return "badge badge-transferencia";
            default:              return "badge";
        }
    }

    public String getBadgeEstado(String e) {
        if (e == null) return "badge";
        switch (e.toLowerCase()) {
            case "completada": return "badge badge-completada";
            case "cancelada":  return "badge badge-cancelada";
            case "pendiente":  return "badge badge-pendiente";
            default:           return "badge";
        }
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }

    public String getFiltroMetodo() {
        return filtroMetodo;
    }

    public void setFiltroMetodo(String filtroMetodo) {
        this.filtroMetodo = filtroMetodo;
    }

    public String getFiltroEstado() {
        return filtroEstado;
    }

    public void setFiltroEstado(String filtroEstado) {
        this.filtroEstado = filtroEstado;
    }

    public Venta getVentaSeleccionada() {
        return ventaSeleccionada;
    }

    public void setVentaSeleccionada(Venta ventaSeleccionada) {
        this.ventaSeleccionada = ventaSeleccionada;
    }

    public boolean isPanelAbierto() {
        return panelAbierto;
    }

    public void setPanelAbierto(boolean panelAbierto) {
        this.panelAbierto = panelAbierto;
    }

    public List<ItemEdicionDTO> getItemsEdicion() {
        return itemsEdicion;
    }

    public void setItemsEdicion(List<ItemEdicionDTO> itemsEdicion) {
        this.itemsEdicion = itemsEdicion;
    }

    public Integer getProductoAgregarId() {
        return productoAgregarId;
    }

    public void setProductoAgregarId(Integer productoAgregarId) {
        this.productoAgregarId = productoAgregarId;
    }

    public Integer getCantidadAgregar() {
        return cantidadAgregar;
    }

    public void setCantidadAgregar(Integer cantidadAgregar) {
        this.cantidadAgregar = cantidadAgregar;
    }

    public List<Producto> getCatalogoProductos() {
        return catalogoProductos;
    }

    public void setCatalogoProductos(List<Producto> catalogoProductos) {
        this.catalogoProductos = catalogoProductos;
    }

    public FacadeVenta getFacadeVenta() {
        return facadeVenta;
    }

    public void setFacadeVenta(FacadeVenta facadeVenta) {
        this.facadeVenta = facadeVenta;
    }

    public FacadeProducto getFacadeProducto() {
        return facadeProducto;
    }

    public void setFacadeProducto(FacadeProducto facadeProducto) {
        this.facadeProducto = facadeProducto;
    }

    public BigDecimal getMontoRecibidoEdicion() {
        return montoRecibidoEdicion;
    }

    public void setMontoRecibidoEdicion(BigDecimal montoRecibidoEdicion) {
        this.montoRecibidoEdicion = montoRecibidoEdicion;
    }

    public String getBusquedaProducto() {
        return busquedaProducto;
    }

    public void setBusquedaProducto(String busquedaProducto) {
        this.busquedaProducto = busquedaProducto;
    }

    public List<Producto> getProductosEncontrados() {
        return productosEncontrados;
    }

    public void setProductosEncontrados(List<Producto> productosEncontrados) {
        this.productosEncontrados = productosEncontrados;
    }

    public boolean isBusquedaRealizada() {
        return busquedaRealizada;
    }

    public void setBusquedaRealizada(boolean busquedaRealizada) {
        this.busquedaRealizada = busquedaRealizada;
    }
}
