package mx.swb.integration;

import mx.swb.facade.FacadeProducto;

public class ServiceFacadeLocator {

    private static FacadeProducto facadeProducto;

    public static FacadeProducto getInstanceFacadeUsuario() {
        if (facadeProducto == null) {
            facadeProducto = new FacadeProducto();
        }
        return facadeProducto;
    }
}
