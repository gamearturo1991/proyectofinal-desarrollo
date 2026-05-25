package mx.swb.negocio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemEdicionDTO implements Serializable {

    private Integer detalleId;       // null si es item nuevo (agregado en edición)
    private Integer productoId;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private Integer cantidadOriginal; // cantidad que tenía en la BD antes de editar
    private Integer cantidad;         // cantidad actual tras la edición
    private boolean eliminado = false;

    public ItemEdicionDTO() {}

    public ItemEdicionDTO(Integer detalleId, Integer productoId, String nombreProducto,
                          BigDecimal precioUnitario, Integer cantidadOriginal, Integer cantidad) {
        this.detalleId = detalleId;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidadOriginal = cantidadOriginal;
        this.cantidad = cantidad;
    }

    /**
      Subtotal calculado con la cantidad actual.
     */
    public BigDecimal getSubtotal() {
        if (precioUnitario == null || cantidad == null) return BigDecimal.ZERO;
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad))
            .setScale(2, RoundingMode.HALF_UP);
    }

    /**
      Diferencia respecto a la cantidad original:
       > 0 → se agregaron unidades  → descontar del lote
       < 0 → se quitaron unidades   → devolver al lote
       = 0 → sin cambios
     */
    public int getDeltaCantidad() {
        int orig = cantidadOriginal != null ? cantidadOriginal : 0;
        int act  = eliminado ? 0 : (cantidad != null ? cantidad : 0);
        return act - orig;
    }

    public Integer getDetalleId() {
        return detalleId;
    }

    public void setDetalleId(Integer detalleId) {
        this.detalleId = detalleId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidadOriginal() {
        return cantidadOriginal;
    }

    public void setCantidadOriginal(Integer cantidadOriginal) {
        this.cantidadOriginal = cantidadOriginal;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}
