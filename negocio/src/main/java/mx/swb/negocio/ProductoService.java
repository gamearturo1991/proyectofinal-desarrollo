package mx.swb.negocio;

import mx.swb.delegate.DelegateProducto;
import mx.swb.entity.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class ProductoService {
    private final DelegateProducto delegateProducto;

    public ProductoService() {
        this.delegateProducto = new DelegateProducto();
    }

    public Producto registrarProducto(
        String codigoBarras,
        String sku,
        String nombre,
        String descripcion,
        Producto.UnidadMedida unidadMedida,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal stockMinimo,
        BigDecimal stockMaximo,
        Boolean controlaCaducidad,
        Integer diasAlertaCaducidad,
        Boolean requiereLote,
        Boolean activo
    ) {
        validarProducto(codigoBarras, sku, nombre, descripcion, unidadMedida,
            precioCompra, precioVenta, stockMinimo, stockMaximo,
            controlaCaducidad, diasAlertaCaducidad, requiereLote, activo, null);

        Producto p = new Producto();
        p.setCodigoBarras(codigoBarras);
        p.setSku(sku.trim());
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setUnidadMedida(unidadMedida != null ? unidadMedida : Producto.UnidadMedida.pieza);
        p.setPrecioCompra(precioCompra != null ? precioCompra : BigDecimal.ZERO);
        p.setPrecioVenta(precioVenta);
        p.setStockMinimo(stockMinimo);
        p.setStockMaximo(stockMaximo);
        p.setControlaCaducidad(controlaCaducidad != null ? controlaCaducidad : false);
        p.setDiasAlertaCaducidad(diasAlertaCaducidad != null ? diasAlertaCaducidad : 30);
        p.setRequiereLote(requiereLote != null ? requiereLote : false);
        p.setActivo(activo != null ? activo : true);

        boolean estaActivo = activo != null ? activo : true;
        if (!estaActivo) {
            p.setFechaBaja(ZonedDateTime.now(ZoneId.of("America/Tijuana")).toLocalDateTime());
        } else {
            p.setFechaBaja(null);
        }

        delegateProducto.guardar(p);

        return p;
    }

    private void validarProducto(
        String codigoBarras,
        String sku,
        String nombre,
        String descripcion,
        Producto.UnidadMedida unidadMedida,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal stockMinimo,
        BigDecimal stockMaximo,
        Boolean controlaCaducidad,
        Integer diasAlertaCaducidad,
        Boolean requiereLote,
        Boolean activo,
        LocalDateTime fechaBaja
    ) {
        if (sku == null || sku.isBlank())
            throw new IllegalArgumentException("El SKU es obligatorio");

        if (sku.trim().length() > 50)
            throw new IllegalArgumentException("El SKU no puede superar 50 caracteres");

        if (delegateProducto.buscarPorSku(sku.trim()) != null)
            throw new IllegalStateException("El SKU ya existe, ingresa uno diferente");

        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (nombre.length() > 200)
            throw new IllegalArgumentException("El nombre no puede superar 200 caracteres");

        if (unidadMedida == null)
            throw new IllegalArgumentException("La unidad de medida es obligatoria");

        if (precioVenta == null || precioVenta.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0");

        if (precioCompra != null && precioCompra.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El precio de compra no puede ser negativo");

        if (codigoBarras != null && !codigoBarras.isBlank()) {
            if (codigoBarras.length() > 50)
                throw new IllegalArgumentException("El codigo de barras no puede superar 50 caracteres");

            if (delegateProducto.buscarPorCodigoBarras(codigoBarras.trim()) != null)
                throw new IllegalStateException("El codigo de barras ya existe, ingresa uno diferente");
        }

        if (descripcion != null && descripcion.length() > 255)
            throw new IllegalArgumentException("La descripcion no puede superar 255 caracteres");

        if (stockMinimo != null && stockMinimo.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El stock minimo no puede ser negativo");

        if (stockMaximo != null && stockMaximo.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El stock maximo no puede ser negativo");

        if (stockMinimo != null && stockMaximo != null)
            if (stockMinimo.compareTo(stockMaximo) > 0)
                throw new IllegalArgumentException("El stock minimo no puede ser mayor al maximo");

        if (controlaCaducidad != null && controlaCaducidad)
            if (diasAlertaCaducidad == null || diasAlertaCaducidad < 0)
                throw new IllegalArgumentException("Los dias de alerta de caducidad deben ser >= 0");

        if (fechaBaja != null && fechaBaja.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("La fecha de baja no puede ser futura");

        if (requiereLote != null && requiereLote && (controlaCaducidad == null || !controlaCaducidad))
            throw new IllegalArgumentException("Si requiere lote, debe controlar caducidad");
    }

    public void darDeBaja(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }
        delegateProducto.darDeBaja(id);
    }
}
public List<Producto> listarActivos() {
    return delegateProducto.listarActivos();
}
