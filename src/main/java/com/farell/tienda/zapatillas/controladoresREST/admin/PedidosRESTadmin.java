package com.farell.tienda.zapatillas.controladoresREST.admin;

import com.farell.tienda.zapatillas.constantesPedidos.EstadosPedido;
import com.farell.tienda.zapatillas.servicios.ServicioPedidos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// API REST de administracion para cambiar el estado de un pedido por AJAX.
// Esta bajo /admin/, asi que el InterceptorAdmin ya la protege con el login de admin
@RestController
@RequestMapping("admin/restPedido/")
public class PedidosRESTadmin {

    @Autowired
    private ServicioPedidos servicioPedidos;

    @RequestMapping("actualizarEstadoPedido")
    public String actualizarEstadoPedido(@RequestParam("id") Long id,
                                         @RequestParam("estado") EstadosPedido estado) {
        servicioPedidos.actualizarEstadoPedido(id, estado);
        return "ok";
    }
}
