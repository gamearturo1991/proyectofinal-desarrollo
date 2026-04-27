package ui;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import mx.swb.entity.Categoria;
import mx.swb.facade.FacadeCategoria;

import java.io.Serializable;
import java.util.List;

@Named("categoriaBean")
@ViewScoped
public class CategoriaBean implements Serializable {

    private String nombre;
    private String descripcion;
    private Boolean activo = true;
    private Integer categoriaSeleccionadaId;

    private FacadeCategoria facadeCategoria = new FacadeCategoria();

    public String guardarCategoria() {
        try {
            facadeCategoria.registrar(nombre, descripcion, activo);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "¡Éxito!", "Categoría guardada correctamente."));
            nombre = null;
            descripcion = null;
            activo = true;
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "No se pudo guardar la categoría."));
            return null;
        }
    }

    public void cargarCategoria(AjaxBehaviorEvent event) {
        if (categoriaSeleccionadaId != null) {
            Categoria cat = facadeCategoria.buscarPorId(categoriaSeleccionadaId);
            if (cat != null) {
                nombre = cat.getNombre();
                descripcion = cat.getDescripcion();
                activo = cat.getActivo();
            }
        } else {
            nombre = null;
            descripcion = null;
            activo = true;
        }
    }

    public String actualizarCategoria() {
        try {
            facadeCategoria.actualizar(categoriaSeleccionadaId, nombre, descripcion, activo);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "¡Éxito!", "Categoría actualizada correctamente."));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "No se pudo actualizar la categoría."));
            return null;
        }
    }

    public String eliminarCategoria() {
        try {
            facadeCategoria.eliminar(categoriaSeleccionadaId);
            categoriaSeleccionadaId = null;
            nombre = null;
            descripcion = null;
            activo = true;
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "¡Éxito!", "Categoría eliminada correctamente."));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Error", "No se pudo eliminar la categoría."));
            return null;
        }
    }

    public List<Categoria> getCategorias() {
        return facadeCategoria.listarTodas();
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Integer getCategoriaSeleccionadaId() { return categoriaSeleccionadaId; }
    public void setCategoriaSeleccionadaId(Integer categoriaSeleccionadaId) { this.categoriaSeleccionadaId = categoriaSeleccionadaId; }
}
