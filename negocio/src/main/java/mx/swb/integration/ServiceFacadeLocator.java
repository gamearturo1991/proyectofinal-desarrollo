package mx.swb.integration;

import jakarta.persistence.EntityManager;
import mx.swb.DAO.*;
import mx.swb.entity.Caja;
import mx.swb.persistence.HibernateUtil;

public class ServiceFacadeLocator {

    private static ProductoDAO productoDAO;
    private static VentaDAO ventaDAO;
    private static LoteDAO loteDAO;
    private static MovimientoInventarioDAO movimientoInventarioDAO;
    private static CajaDAO cajaDAO;


    public static EntityManager getEntityManager() {
        return HibernateUtil.getEntityManager();
    }

    public static ProductoDAO getInstanceProductoDAO() {
        if (productoDAO == null)
            productoDAO = new ProductoDAO(getEntityManager());
        return productoDAO;
    }

    public static VentaDAO getInstanceVentaDAO() {
        if (ventaDAO == null)
            ventaDAO = new VentaDAO(getEntityManager());
        return ventaDAO;
    }

    public static LoteDAO getInstanceLoteDAO() {
        if (loteDAO == null)
            loteDAO = new LoteDAO(getEntityManager());
        return loteDAO;
    }

    public static MovimientoInventarioDAO getInstanceMovimientoInventarioDAO() {
        if (movimientoInventarioDAO == null)
            movimientoInventarioDAO = new MovimientoInventarioDAO(getEntityManager());
        return movimientoInventarioDAO;
    }

    public static CajaDAO getInstanceCajaDAO() {
        if (cajaDAO == null)
            cajaDAO = new CajaDAO(getEntityManager());
        return cajaDAO;
    }
}

