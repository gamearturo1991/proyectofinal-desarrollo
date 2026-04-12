package ui;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.swb.entity.Producto;
import mx.swb.facade.FacadeProducto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Named("productoBeanUI")
@ViewScoped
public class ProductoBeanUI implements Serializable {

    // campos de la entidad Producto
    private String codigoBarras;
    private String sku;
    private String nombre;
    private String descripcion;
    private Producto.UnidadMedida unidadMedida;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal stockMinimo;
    private BigDecimal stockMaximo;
    private Boolean controlaCaducidad = false;
    private Integer diasAlertaCaducidad = 30;
    private Boolean requiereLote = false;
    private Boolean activo = true;

    // campos del lote inicial -- no van en la entidad Producto
    private Integer cantidadInicial = 0;
    private LocalDate fechaCaducidadLote;
    private String numeroLote;

    private FacadeProducto facadeProducto;

    @PostConstruct
    public void init() {
        facadeProducto = new FacadeProducto();
    }

    public void guardar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        boolean hayErrores = false;

        // --- validaciones de campos obligatorios ---

        if (sku == null || sku.trim().isEmpty()) {
            ctx.addMessage("formProducto:sku",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El SKU es obligatorio", null));
            hayErrores = true;
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            ctx.addMessage("formProducto:nombre",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El nombre es obligatorio", null));
            hayErrores = true;
        }

        if (precioVenta == null) {
            ctx.addMessage("formProducto:precioVenta",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El precio de venta es obligatorio", null));
            hayErrores = true;
        }

        if (precioCompra == null) {
            ctx.addMessage("formProducto:precioCompra",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El precio de costo es obligatorio", null));
            hayErrores = true;
        }

        if (unidadMedida == null) {
            ctx.addMessage("formProducto:unidadMedida",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "La unidad de medida es obligatoria", null));
            hayErrores = true;
        }

        if (hayErrores) {
            ctx.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Hay campos obligatorios sin completar: SKU, Nombre, Precio de Venta, Precio de Costo y Unidad de Medida",
                    null));
            return;
        }

        // --- llamada al facade que usa ProductoService con todas sus validaciones ---
        try {
            // codigo de barras vacio se convierte a null
            String cb = (codigoBarras != null && codigoBarras.trim().isEmpty())
                ? null : codigoBarras;

            facadeProducto.registrar(
                cb,
                sku.trim(),
                nombre.trim(),
                descripcion,
                unidadMedida,
                precioCompra,
                precioVenta,
                stockMinimo,
                stockMaximo,
                controlaCaducidad,
                diasAlertaCaducidad,
                requiereLote,
                activo
            );

            ctx.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Producto guardado correctamente", null));

            limpiar();

        } catch (IllegalArgumentException e) {
            // errores de validacion de datos (formato, rango, longitud, etc.)
            mapearMensajeAcampo(ctx, e.getMessage());

        } catch (IllegalStateException e) {
            // errores de duplicados (SKU, codigo de barras, nombre ya existen)
            mapearMensajeAcampoDuplicado(ctx, e.getMessage());

        } catch (Exception e) {
            ctx.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error inesperado al guardar el producto", e.getMessage()));
        }
    }

    /**
     * Mapea los mensajes de IllegalArgumentException al campo correspondiente
     */
    private void mapearMensajeAcampo(FacesContext ctx, String mensaje) {
        if (mensaje == null) return;

        if (mensaje.contains("SKU")) {
            ctx.addMessage("formProducto:sku",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else if (mensaje.contains("nombre")) {
            ctx.addMessage("formProducto:nombre",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else if (mensaje.contains("precio de venta")) {
            ctx.addMessage("formProducto:precioVenta",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else if (mensaje.contains("precio de compra")) {
            ctx.addMessage("formProducto:precioCompra",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else if (mensaje.contains("unidad de medida")) {
            ctx.addMessage("formProducto:unidadMedida",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else if (mensaje.contains("stock m")) {
            ctx.addMessage("formProducto:stockMinimo",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else if (mensaje.contains("stock m\u00e1ximo") || mensaje.contains("stock maximo")) {
            ctx.addMessage("formProducto:stockMaximo",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else if (mensaje.contains("d\u00edas de alerta") || mensaje.contains("dias de alerta")) {
            ctx.addMessage("formProducto:diasAlerta",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else if (mensaje.contains("c\u00f3digo de barras") || mensaje.contains("codigo de barras")) {
            ctx.addMessage("formProducto:codigoBarras",
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));

        } else {
            // mensaje generico si no coincide con ningun campo
            ctx.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));
        }
    }

    /**
     * Mapea los mensajes de IllegalStateException (duplicados) al campo correspondiente
     */
    private void mapearMensajeAcampoDuplicado(FacesContext ctx, String mensaje) {
        if (mensaje == null) return;

        if (mensaje.contains("SKU")) {
            ctx.addMessage("formProducto:sku",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El SKU ya existe, ingresa uno diferente", null));

        } else if (mensaje.contains("c\u00f3digo de barras") || mensaje.contains("codigo de barras")) {
            ctx.addMessage("formProducto:codigoBarras",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El Codigo de Barras ya existe, ingresa uno diferente", null));

        } else if (mensaje.contains("nombre")) {
            ctx.addMessage("formProducto:nombre",
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "El nombre ya existe, ingresa uno diferente", null));

        } else {
            ctx.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, mensaje, null));
        }
    }

    public void limpiar() {
        codigoBarras        = null;
        sku                 = null;
        nombre              = null;
        descripcion         = null;
        unidadMedida        = null;
        precioCompra        = null;
        precioVenta         = null;
        stockMinimo         = null;
        stockMaximo         = null;
        controlaCaducidad   = false;
        diasAlertaCaducidad = 30;
        requiereLote        = false;
        activo              = true;
        cantidadInicial     = 0;
        fechaCaducidadLote  = null;
        numeroLote          = null;
    }

    public Producto.UnidadMedida[] getUnidadesMedida() {
        return Producto.UnidadMedida.values();
    }

    public String getLabelUnidad(Producto.UnidadMedida u) {
        switch (u) {
            case pieza:   return "Pieza";
            case kg:      return "Kilogramo";
            case litro:   return "Litro";
            case metro:   return "Metro";
            case caja:    return "Caja";
            case paquete: return "Paquete";
            default:      return u.name();
        }
    }

    // ---------- Getters y Setters ----------

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Producto.UnidadMedida getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(Producto.UnidadMedida unidadMedida) { this.unidadMedida = unidadMedida; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public BigDecimal getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(BigDecimal stockMinimo) { this.stockMinimo = stockMinimo; }

    public BigDecimal getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(BigDecimal stockMaximo) { this.stockMaximo = stockMaximo; }

    public Boolean getControlaCaducidad() { return controlaCaducidad; }
    public void setControlaCaducidad(Boolean controlaCaducidad) { this.controlaCaducidad = controlaCaducidad; }

    public Integer getDiasAlertaCaducidad() { return diasAlertaCaducidad; }
    public void setDiasAlertaCaducidad(Integer diasAlertaCaducidad) { this.diasAlertaCaducidad = diasAlertaCaducidad; }

    public Boolean getRequiereLote() { return requiereLote; }
    public void setRequiereLote(Boolean requiereLote) { this.requiereLote = requiereLote; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Integer getCantidadInicial() { return cantidadInicial; }
    public void setCantidadInicial(Integer cantidadInicial) { this.cantidadInicial = cantidadInicial; }

    public LocalDate getFechaCaducidadLote() { return fechaCaducidadLote; }
    public void setFechaCaducidadLote(LocalDate fechaCaducidadLote) { this.fechaCaducidadLote = fechaCaducidadLote; }

    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
}
