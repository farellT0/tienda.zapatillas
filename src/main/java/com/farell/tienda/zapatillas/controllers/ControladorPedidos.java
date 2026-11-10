package com.farell.tienda.zapatillas.controllers;

import com.farell.tienda.zapatillas.constantesPedidos.EstadosPedido;
import com.farell.tienda.zapatillas.model.Pedido;
import com.farell.tienda.zapatillas.servicios.ServicioPedidos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Controller
@RequestMapping("admin/")
public class ControladorPedidos {

    @Autowired
    private ServicioPedidos servicioPedidos;

    @RequestMapping("gestionarPedidos")
    public String gestionarPedidos(Model model) {
        model.addAttribute("pedidos", servicioPedidos.obtenerPedidos());
        return "admin/gestionar_pedidos";
    }

    @RequestMapping("verDetallesPedido")
    public String verDetallesPedido(@RequestParam("id") Long id, Model model) {
        Pedido pedido = servicioPedidos.obtenerPedidoPorId(id);
        if (pedido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el pedido indicado.");
        }
        double total = pedido.getProductosPedido().stream()
                .mapToDouble(producto -> producto.getZapatilla().getPrecio() * producto.getCantidad())
                .sum();
        model.addAttribute("pedido", pedido);
        model.addAttribute("estadosPedido", EstadosPedido.values());
        model.addAttribute("numeroTarjetaEnmascarado", enmascararTarjeta(pedido.getNumeroTarjeta()));
        model.addAttribute("totalPedido", total);
        return "admin/pedidos_detalle";
    }

    @RequestMapping("actualizarEstadoPedido")
    public String actualizarEstadoPedido(
            @RequestParam("id") Long id,
            @RequestParam("estado") EstadosPedido estado) {
        servicioPedidos.actualizarEstadoPedido(id, estado);
        return "redirect:/admin/verDetallesPedido?id=" + id;
    }

    private String enmascararTarjeta(String numero) {
        if (numero == null || numero.isBlank()) {
            return "No indicado";
        }
        String digitos = numero.replaceAll("\\s+", "");
        String ultimos = digitos.length() <= 4 ? digitos : digitos.substring(digitos.length() - 4);
        return "•••• •••• •••• " + ultimos;
    }
}
