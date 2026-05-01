package mx.swb.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "codigo_barras", length = 50, unique = true)
    private String codigoBarras;

    @NotNull
    @Size(max = 50)
    @Column(name = "sku", length = 50, nullable = false, unique = true)
    private String sku;

    @NotNull
    @Size(max = 200)
    @Column(name = "nombre", length = 200, nullable = false)
    private String nombre;

    @Size(max = 255)
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    public enum UnidadMedida {
        pieza, kg, litro, metro, caja, paquete
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;

    @NotNull
    @Column(name = "precio_compra", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @NotNull
    @Column(name = "precio_venta", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioVenta;

    @Column(name = "stock_minimo", precision = 12, scale = 2)
    private BigDecimal stockMinimo;

    @Column(name = "stock_maximo", precision = 12, scale = 2)
    private BigDecimal stockMaximo;

    @NotNull
    @Column(name = "controla_caducidad", nullable = false)
    private Boolean controlaCaducidad = false;

    @NotNull
    @Column(name = "dias_alerta_caducidad", nullable = false)
    private Integer diasAlertaCaducidad = 30;

    @NotNull
    @Column(name = "requiere_lote", nullable = false)
    private Boolean requiereLote = false;

    @NotNull
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @NotNull
    @Column(name = "fecha_creado", nullable = false, updatable = false)
    private LocalDateTime fechaCreado;

    @NotNull
    @Column(name = "fecha_actualizado", nullable = false)
    private LocalDateTime fechaActualizado;

    @Transient
    private int stockActual;

    @PrePersist
    public void prePersist() {
        ZoneId tijuana = ZoneId.of("America/Tijuana");
        LocalDateTime ahora = ZonedDateTime.now(tijuana).toLocalDateTime();

        this.fechaCreado     = ahora;
        this.fechaActualizado = ahora;

        // limpiar campos opcionales unicos
        if (this.codigoBarras != null && this.codigoBarras.trim().isEmpty()) {
            this.codigoBarras = null;
        }
        if (this.sku != null) {
            this.sku = this.sku.trim();
        }
        if (this.nombre != null) {
            this.nombre = this.nombre.trim();
        }
    }

    @PreUpdate
    public void preUpdate() {
        ZoneId tijuana = ZoneId.of("America/Tijuana");
        this.fechaActualizado = ZonedDateTime.now(tijuana).toLocalDateTime();

        // limpiar tambien en actualizacion
        if (this.codigoBarras != null && this.codigoBarras.trim().isEmpty()) {
            this.codigoBarras = null;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(BigDecimal stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public Boolean getControlaCaducidad() {
        return controlaCaducidad;
    }

    public void setControlaCaducidad(Boolean controlaCaducidad) {
        this.controlaCaducidad = controlaCaducidad;
    }

    public Integer getDiasAlertaCaducidad() {
        return diasAlertaCaducidad;
    }

    public void setDiasAlertaCaducidad(Integer diasAlertaCaducidad) {
        this.diasAlertaCaducidad = diasAlertaCaducidad;
    }

    public Boolean getRequiereLote() {
        return requiereLote;
    }

    public void setRequiereLote(Boolean requiereLote) {
        this.requiereLote = requiereLote;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(LocalDateTime fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public LocalDateTime getFechaCreado() {
        return fechaCreado;
    }

    public void setFechaCreado(LocalDateTime fechaCreado) {
        this.fechaCreado = fechaCreado;
    }

    public LocalDateTime getFechaActualizado() {
        return fechaActualizado;
    }

    public void setFechaActualizado(LocalDateTime fechaActualizado) {
        this.fechaActualizado = fechaActualizado;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }
}
