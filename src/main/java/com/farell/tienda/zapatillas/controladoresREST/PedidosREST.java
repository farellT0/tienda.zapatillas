package com.farell.tienda.zapatillas.controladoresREST;

import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.model.Pedido;
import com.farell.tienda.zapatillas.model.ProductoPedido;
import com.farell.tienda.zapatillas.responseData.ResumenPedido;
import com.farell.tienda.zapatillas.servicios.ServicioPedidos;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("restPedidos/")
public class PedidosREST {

    @Autowired
    private ServicioPedidos servicioPedidos;

    @PostMapping("paso1")
    public ResponseEntity<String> paso1(
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("direccion") String direccion,
            @RequestParam("provincia") String provincia,
            @RequestParam(value = "prefijoTelefono", defaultValue = "+34") String prefijoTelefono,
            @RequestParam("telefono") String telefono,
            @RequestParam("codigoPostal") String codigoPostal,
            @RequestParam("ciudad") String ciudad,
            HttpServletRequest request) {
        Usuario usuario = usuarioIdentificado(request);
        if (usuario == null) {
            return error("Debes identificarte antes de realizar un pedido.");
        }
        try {
            servicioPedidos.procesarPaso1(nombreCompleto, direccion, provincia,
                    prefijoTelefono + " " + telefono.trim(), codigoPostal, ciudad, usuario.getId());
            return ResponseEntity.ok("Datos de envío guardados.");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("paso2")
    public ResponseEntity<String> paso2(
            @RequestParam("titular") String titular,
            @RequestParam("numero") String numero,
            @RequestParam("tipoTarjeta") String tipoTarjeta,
            @RequestParam("fechaCaducidad") String fechaCaducidad,
            HttpServletRequest request) {
        Usuario usuario = usuarioIdentificado(request);
        if (usuario == null) {
            return error("Debes identificarte antes de realizar un pedido.");
        }
        try {
            servicioPedidos.procesarPaso2(titular, numero, tipoTarjeta, fechaCaducidad, usuario.getId());
            return ResponseEntity.ok("Datos de pago guardados.");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("resumen")
    public ResponseEntity<?> resumen(HttpServletRequest request) {
        Usuario usuario = usuarioIdentificado(request);
        if (usuario == null) {
            return error("Debes identificarte antes de realizar un pedido.");
        }
        try {
            ResumenPedido resumen = servicioPedidos.obtenerResumenPedido(usuario.getId());
            return ResponseEntity.ok(resumen);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("misPedidos")
    public ResponseEntity<?> misPedidos(HttpServletRequest request) {
        Usuario usuario = usuarioIdentificado(request);
        if (usuario == null) {
            return error("Debes identificarte antes de consultar tus pedidos.");
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Pedido pedido : servicioPedidos.obtenerPedidosUsuario(usuario.getId())) {
            Map<String, Object> pedidoJson = new HashMap<>();
            pedidoJson.put("id", pedido.getId());
            pedidoJson.put("estado", pedido.getEstado().name());
            List<Map<String, Object>> productos = new ArrayList<>();
            for (ProductoPedido linea : pedido.getProductosPedido()) {
                Map<String, Object> productoJson = new HashMap<>();
                productoJson.put("marca", linea.getZapatilla().getMarca());
                productoJson.put("modelo", linea.getZapatilla().getModelo());
                productoJson.put("talla", linea.getTalla());
                productoJson.put("cantidad", linea.getCantidad());
                productos.add(productoJson);
            }
            pedidoJson.put("productos", productos);
            resultado.add(pedidoJson);
        }
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("confirmar")
    public ResponseEntity<String> confirmar(HttpServletRequest request) {
        Usuario usuario = usuarioIdentificado(request);
        if (usuario == null) {
            return error("Debes identificarte antes de realizar un pedido.");
        }
        try {
            servicioPedidos.confirmarPedido(usuario.getId());
            return ResponseEntity.ok("Pedido realizado correctamente.");
        } catch (IllegalArgumentException e) {
            return error(e.getMessage());
        }
    }

    private Usuario usuarioIdentificado(HttpServletRequest request) {
        return (Usuario) request.getSession().getAttribute("usuario_identificado");
    }

    private ResponseEntity<String> error(String mensaje) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensaje);
    }
}
