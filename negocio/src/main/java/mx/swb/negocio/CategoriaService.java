package mx.swb.negocio;

import mx.swb.DAO.CategoriaDAO;
import mx.swb.entity.Categoria;
import java.util.List;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    public void registrar(String nombre, String descripcion, Boolean activo) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setActivo(activo);
        categoriaDAO.save(c);
    }

    public List<Categoria> listarTodas() {
        return categoriaDAO.findAll();
    }

    public Categoria buscarPorId(Integer id) {
        return categoriaDAO.find(id).orElse(null);
    }

    public void actualizar(Integer id, String nombre, String descripcion, Boolean activo) {
        categoriaDAO.find(id).ifPresent(c -> {
            c.setNombre(nombre);
            c.setDescripcion(descripcion);
            c.setActivo(activo);
            categoriaDAO.save(c);
        });
    }

    public void eliminar(Integer id) {
        categoriaDAO.find(id).ifPresent(c -> categoriaDAO.delete(c));
    }
}
