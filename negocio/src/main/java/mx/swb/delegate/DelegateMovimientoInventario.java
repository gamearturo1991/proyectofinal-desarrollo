package mx.swb.delegate;

import mx.swb.DAO.MovimientoInventarioDAO;
import mx.swb.entity.MovimientoInventario;
import mx.swb.entity.TipoMovimientoInventario;
import mx.swb.integration.ServiceLocator;

public class DelegateMovimientoInventario {
    private final MovimientoInventarioDAO movimientoInventarioDAO;

    public DelegateMovimientoInventario() {
        this.movimientoInventarioDAO = ServiceLocator.getInstanceMovimientoInventarioDAO();
    }

    public TipoMovimientoInventario buscarTipoMovimiento(String nombre) {
        return movimientoInventarioDAO.findTipoPorNombre(nombre);
    }

    public void guardar(MovimientoInventario movimiento) {
        movimientoInventarioDAO.save(movimiento);
    }
}

