package mx.swb.integration;

import jakarta.persistence.EntityManager;
import mx.swb.DAO.*;
import mx.swb.persistence.HibernateUtil;

public class ServiceLocator {

    private static ProductoDAO productoDAO;
    private static LoteDAO loteDAO;
    private static MovimientoInventarioDAO movimientoInventarioDAO;
    private static VentaDAO ventaDAO;
    private static CajaDAO cajaDAO;

    public static EntityManager getEntityManager(){
        return HibernateUtil.getEntityManager();
    }

    public static ProductoDAO getInstanceProductoDAO(){
        if (productoDAO == null) {
            productoDAO = new ProductoDAO(getEntityManager());
            return productoDAO;
        } else{
            return productoDAO;
        }
    }

    public static LoteDAO getInstanceLoteDAO(){
        if (loteDAO == null) {
            loteDAO = new LoteDAO(getEntityManager());
            return loteDAO;
        } else{
            return loteDAO;
        }
    }

    public static MovimientoInventarioDAO getInstanceMovimientoInventarioDAO(){
        if (movimientoInventarioDAO == null) {
            movimientoInventarioDAO = new MovimientoInventarioDAO(getEntityManager());
            return movimientoInventarioDAO;
        } else{
            return movimientoInventarioDAO;
        }
    }

    public static VentaDAO getInstanceVentaDAO(){
        if (ventaDAO == null) {
            ventaDAO = new VentaDAO(getEntityManager());
            return ventaDAO;
        } else{
            return ventaDAO;
        }
    }

    public static CajaDAO getInstanceCajaDAO(){
        if (cajaDAO == null) {
            cajaDAO = new CajaDAO(getEntityManager());
            return cajaDAO;
        } else{
            return cajaDAO;
        }
    }
}
