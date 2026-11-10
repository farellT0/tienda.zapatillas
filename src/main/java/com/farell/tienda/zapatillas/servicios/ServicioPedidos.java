package com.farell.tienda.zapatillas.servicios;

import com.farell.tienda.zapatillas.constantesPedidos.EstadosPedido;
import com.farell.tienda.zapatillas.model.Pedido;
import com.farell.tienda.zapatillas.responseData.ResumenPedido;

import java.util.List;

public interface ServicioPedidos {

    List<Pedido> obtenerPedidos();

    List<Pedido> obtenerPedidosUsuario(Long idUsuario);

    Pedido obtenerPedidoPorId(Long id);

    void actualizarEstadoPedido(Long id, EstadosPedido estado);

    void procesarPaso1(String nombreCompleto, String direccion, String provincia,
                       String telefono, String codigoPostal, String ciudad, Long idUsuario);

    void procesarPaso2(String titular, String numero, String tipoTarjeta,
                       String fechaCaducidad, Long idUsuario);

    ResumenPedido obtenerResumenPedido(Long idUsuario);

    void confirmarPedido(Long idUsuario);
}
