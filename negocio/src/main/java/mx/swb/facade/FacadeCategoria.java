package mx.swb.facade;

import mx.swb.integration.ServiceLocator;
import mx.swb.negocio.CategoriaService;
import mx.swb.entity.Categoria;
import java.util.List;

public class FacadeCategoria {

    private final CategoriaService categoriaService;

    public FacadeCategoria() {
        this.categoriaService = new CategoriaService(
            ServiceLocator.getInstanceCategoriaDAO()
        );
    }

    public void registrar(String nombre, String descripcion, Boolean activo) {
        categoriaService.registrar(nombre, descripcion, activo);
    }

    public List<Categoria> listarTodas() {
        return categoriaService.listarTodas();
    }

    public Categoria buscarPorId(Integer id) {
        return categoriaService.buscarPorId(id);
    }

    public void actualizar(Integer id, String nombre, String descripcion, Boolean activo) {
        categoriaService.actualizar(id, nombre, descripcion, activo);
    }

    public void eliminar(Integer id) {
        categoriaService.eliminar(id);
    }
}
