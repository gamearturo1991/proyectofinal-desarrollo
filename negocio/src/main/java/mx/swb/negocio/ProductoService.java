package mx.swb.negocio;

import jakarta.persistence.EntityManager;
import mx.swb.DAO.ProductoDAO;
import mx.swb.entity.Producto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService(EntityManager em) {
        this.productoDAO = new ProductoDAO(em);
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

        // Funcion de validacion de atributos
        validarProducto(
            codigoBarras,
            sku,
            nombre,
            descripcion,
            unidadMedida,
            precioCompra,
            precioVenta,
            stockMinimo,
            stockMaximo,
            controlaCaducidad,
            diasAlertaCaducidad,
            requiereLote,
            activo,
            null
        );

        // Creación de Producto
        Producto p = new Producto();

        // Setteo de atributos
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
        p.setFechaBaja(null);

        // Guradado
        productoDAO.save(p);

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

        if (productoDAO.findByOneParameterUnique(sku.trim(), "sku") != null)
            throw new IllegalStateException("Ya existe un producto con el SKU: " + sku);

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
                throw new IllegalArgumentException("El código de barras no puede superar 50 caracteres");

            if (productoDAO.findByOneParameterUnique(codigoBarras.trim(), "codigoBarras") != null)
                throw new IllegalStateException("Ya existe un producto con ese código de barras");
        }

        if (descripcion != null && descripcion.length() > 255)
            throw new IllegalArgumentException("La descripción no puede superar 255 caracteres");

        if (stockMinimo != null && stockMinimo.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");

        if (stockMaximo != null && stockMaximo.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El stock máximo no puede ser negativo");

        if (stockMinimo != null && stockMaximo != null) {
            if (stockMinimo.compareTo(stockMaximo) > 0)
                throw new IllegalArgumentException("El stock mínimo no puede ser mayor al máximo");
        }

        if (controlaCaducidad != null && controlaCaducidad) {
            if (diasAlertaCaducidad == null || diasAlertaCaducidad < 0)
                throw new IllegalArgumentException("Los días de alerta de caducidad deben ser >= 0");
        }

        if (fechaBaja != null && fechaBaja.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("La fecha de baja no puede ser futura");

        if (requiereLote != null && requiereLote && (controlaCaducidad == null || !controlaCaducidad)) {
            throw new IllegalArgumentException("Si requiere lote, debe controlar caducidad");
        }
    }
}
