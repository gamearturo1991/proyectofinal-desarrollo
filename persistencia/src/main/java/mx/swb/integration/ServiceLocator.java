package mx.swb.integration;

import jakarta.persistence.EntityManager;
import mx.swb.DAO.ProductoDAO;
import mx.swb.DAO.CategoriaDAO;
import mx.swb.persistence.HibernateUtil;

public class ServiceLocator {

    private static ProductoDAO productoDAO;
    private static CategoriaDAO categoriaDAO;

    private static EntityManager getEntityManager(){
        return HibernateUtil.getEntityManager();
    }

    public static ProductoDAO getInstanceProductoDAO(){
        if (productoDAO == null) {
            productoDAO = new ProductoDAO(getEntityManager());
            return productoDAO;
        } else {
            return productoDAO;
        }
    }

    public static CategoriaDAO getInstanceCategoriaDAO(){
        if (categoriaDAO == null) {
            categoriaDAO = new CategoriaDAO(getEntityManager());
            return categoriaDAO;
        } else {
            return categoriaDAO;
        }
    }
}
