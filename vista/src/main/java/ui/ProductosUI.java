package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
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

    @PostConstruct
    public void init() {
        facadeProducto = new FacadeProducto();

    }

    // ---------- Getters y Setters ----------

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}
