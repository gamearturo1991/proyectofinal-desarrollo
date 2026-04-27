package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.swb.entity.Producto;
import mx.swb.facade.FacadeProducto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("productosUI")
@SessionScoped
public class ProductosUI implements Serializable {

    private FacadeProducto facadeProducto;
    private List<Producto> productos = new ArrayList<>();
    private Producto productoSeleccionado;

    @PostConstruct
    public void init() {
        facadeProducto = new FacadeProducto();
        cargarProductos();
    }

    public void cargarProductos() {
        productos = facadeProducto.listarActivos();
    }

    public void darDeBaja() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            facadeProducto.darDeBaja(productoSeleccionado.getId());
            ctx.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "Producto dado de baja correctamente", null));
            cargarProductos();
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Error al dar de baja el producto", e.getMessage()));
        }
    }

    // ---------- Getters y Setters ----------

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }

    public Producto getProductoSeleccionado() { return productoSeleccionado; }
    public void setProductoSeleccionado(Producto productoSeleccionado) {
        this.productoSeleccionado = productoSeleccionado;
    }
}
