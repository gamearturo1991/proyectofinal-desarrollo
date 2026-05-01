package ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import mx.swb.entity.Venta;
import mx.swb.facade.FacadeVenta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("ventasUI")
@SessionScoped
public class VentasUI implements Serializable {

    private FacadeVenta facadeVenta;
    private List<Venta> ventas = new ArrayList<>();

    @PostConstruct
    public void init() {
        facadeVenta = new FacadeVenta();
        cargarVentas();
    }

    public void cargarVentas() {
        try {
            ventas = facadeVenta.obtenerHoy();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error al cargar ventas", e.getMessage()));
        }
    }
}
