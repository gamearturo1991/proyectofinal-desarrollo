package mx.swb.delegate;

import mx.swb.DAO.CajaDAO;
import mx.swb.entity.Caja;
import mx.swb.integration.ServiceLocator;

import java.util.List;

public class DelegateCaja {

    private final CajaDAO cajaDAO;

    public DelegateCaja() {
        this.cajaDAO = ServiceLocator.getInstanceCajaDAO();
    }

    public Caja findCajaAbiertaPorUsuario(Integer usuarioId) {
        return cajaDAO.findCajaAbiertaPorUsuario(usuarioId);
    }

    public List<Caja> listarTodas() {
        return cajaDAO.findAll();
    }

    public Caja buscarPorId(Integer id) {
        return cajaDAO.findById(id);
    }

    public void guardar(Caja caja) {
        cajaDAO.save(caja);
    }
}
