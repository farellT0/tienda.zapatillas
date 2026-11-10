package com.farell.tienda.zapatillas.servicios;

import java.util.List;
import java.util.Map;

public interface ServicioCarrito {

    void agregarProducto(long idUsuario, long idProducto, int cantidad, double talla);

    // operaciones que devuelven json:
    List<Map<String,Object>> obtenerProductosCarrito(long idUsuario);

    boolean eliminarProducto(long idUsuario, long idProductoCarrito);
}
