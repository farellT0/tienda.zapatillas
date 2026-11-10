package com.farell.tienda.zapatillas.controladoresREST;

import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.servicios.ServicioCarrito;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("restCarrito/")
public class CarritoREST {

    @Autowired
    private ServicioCarrito servicioCarrito;

    @PostMapping("agregarProducto")
    public String agregarProducto(
            @RequestParam("id") long id,
            @RequestParam("cantidad") int cantidad,
            @RequestParam("talla") double talla,
            HttpServletRequest request){
        Usuario u = (Usuario)
                request.getSession().getAttribute("usuario_identificado");
        if (u == null) {
            return "identificate primero";
        }
        servicioCarrito.agregarProducto(
                u.getId(),
                id,
                cantidad,
                talla
        );
        return "Producto añadido al carrito.";
    }

    @GetMapping("listar")
    public List<Map<String, Object>> listar(HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario_identificado");
        if (usuario == null) {
            return List.of();
        }
        return servicioCarrito.obtenerProductosCarrito(usuario.getId());
    }

    @PostMapping("eliminarProducto")
    public String eliminarProducto(
            @RequestParam("idLinea") long idLinea,
            HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario_identificado");
        if (usuario == null) {
            return "identificate primero";
        }
        if (!servicioCarrito.eliminarProducto(usuario.getId(), idLinea)) {
            return "No se encontró ese producto en tu carrito.";
        }
        return "Producto eliminado del carrito.";
    }
}
