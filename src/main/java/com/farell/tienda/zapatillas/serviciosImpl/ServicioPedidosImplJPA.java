package com.farell.tienda.zapatillas.serviciosImpl;

import com.farell.tienda.zapatillas.constantesPedidos.EstadosPedido;
import com.farell.tienda.zapatillas.model.*;
import com.farell.tienda.zapatillas.responseData.ResumenPedido;
import com.farell.tienda.zapatillas.servicios.ServicioPedidos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServicioPedidosImplJPA implements ServicioPedidos {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Pedido> obtenerPedidos() {
        return entityManager.createQuery(
                        "SELECT p FROM Pedido p ORDER BY p.id DESC", Pedido.class)
                .getResultList();
    }

    @Override
    public List<Pedido> obtenerPedidosUsuario(Long idUsuario) {
        return entityManager.createQuery(
                        "SELECT p FROM Pedido p WHERE p.usuario.id = :idUsuario " +
                                "AND p.estado <> :estado ORDER BY p.id DESC", Pedido.class)
                .setParameter("idUsuario", idUsuario)
                .setParameter("estado", EstadosPedido.EnProceso)
                .getResultList();
    }

    @Override
    public Pedido obtenerPedidoPorId(Long id) {
        return entityManager.find(Pedido.class, id);
    }

    @Override
    public void actualizarEstadoPedido(Long id, EstadosPedido estado) {
        Pedido pedido = obtenerPedidoPorId(id);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe el pedido indicado.");
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado del pedido no es válido.");
        }
        pedido.setEstado(estado);
    }

    @Override
    public void procesarPaso1(String nombreCompleto, String direccion, String provincia,
                              String telefono, String codigoPostal, String ciudad, Long idUsuario) {
        validarTexto(nombreCompleto, "El nombre completo");
        validarTexto(direccion, "La dirección");
        validarTexto(provincia, "La provincia");
        validarTexto(telefono, "El teléfono");
        validarTexto(codigoPostal, "El código postal");
        validarTexto(ciudad, "La ciudad");
        if (!provincia.trim().matches("\\p{L}+( \\p{L}+)*")) {
            throw new IllegalArgumentException("La provincia solo puede contener letras.");
        }
        if (!ciudad.trim().matches("\\p{L}+( \\p{L}+)*")) {
            throw new IllegalArgumentException("La ciudad solo puede contener letras.");
        }
        // 9 digitos, con prefijo de pais opcional delante (ej. "+34 600123456")
        if (!telefono.trim().matches("(\\+(34|351|33|49|39|44|1) )?\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 dígitos.");
        }
        if (!codigoPostal.trim().matches("\\d{5}")) {
            throw new IllegalArgumentException("El código postal debe tener exactamente 5 dígitos.");
        }
        Pedido pedido = obtenerPedidoActual(idUsuario, true);
        pedido.setNombreCompleto(nombreCompleto.trim());
        pedido.setDireccion(direccion.trim());
        pedido.setProvincia(provincia.trim());
        pedido.setTelefono(telefono.trim());
        pedido.setCodigoPostal(codigoPostal.trim());
        pedido.setCiudad(ciudad.trim());
    }

    @Override
    public void procesarPaso2(String titular, String numero, String tipoTarjeta,
                              String fechaCaducidad, Long idUsuario) {
        validarTexto(titular, "El titular de la tarjeta");
        validarTexto(numero, "El número de tarjeta");
        validarTexto(tipoTarjeta, "El tipo de tarjeta");
        validarTexto(fechaCaducidad, "La fecha de caducidad");
        if (!numero.trim().matches("[0-9]{13,19}")) {
            throw new IllegalArgumentException("El número de tarjeta debe tener entre 13 y 19 dígitos.");
        }
        if (!"VISA".equals(tipoTarjeta) && !"MASTER CARD".equals(tipoTarjeta)) {
            throw new IllegalArgumentException("El tipo de tarjeta debe ser VISA o MASTER CARD.");
        }
        if (!fechaCaducidad.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new IllegalArgumentException("La fecha de caducidad debe tener formato MM/AA.");
        }
        Pedido pedido = obtenerPedidoActual(idUsuario, true);
        pedido.setTitularTarjeta(titular.trim());
        pedido.setNumeroTarjeta(numero.trim());
        pedido.setTipoTarjeta(tipoTarjeta);
        pedido.setFechaCaducidad(fechaCaducidad);
    }

    @Override
    public ResumenPedido obtenerResumenPedido(Long idUsuario) {
        Pedido pedido = obtenerPedidoActual(idUsuario, true);
        if (pedido.getNombreCompleto() == null || pedido.getDireccion() == null
                || pedido.getProvincia() == null || pedido.getTitularTarjeta() == null
                || pedido.getNumeroTarjeta() == null || pedido.getTipoTarjeta() == null
                || pedido.getTelefono() == null || pedido.getCodigoPostal() == null
                || pedido.getCiudad() == null || pedido.getFechaCaducidad() == null) {
            throw new IllegalArgumentException("Completa los datos de envío y de pago antes de consultar el resumen.");
        }

        List<ProductoCarrito> lineas = obtenerLineasCarrito(idUsuario);
        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("No hay productos en el carrito para realizar el pedido.");
        }

        ResumenPedido resumen = new ResumenPedido();
        resumen.setNombreCompleto(pedido.getNombreCompleto());
        resumen.setDireccion(pedido.getDireccion());
        resumen.setProvincia(pedido.getProvincia());
        resumen.setTelefono(pedido.getTelefono());
        resumen.setCodigoPostal(pedido.getCodigoPostal());
        resumen.setCiudad(pedido.getCiudad());
        resumen.setTitularTarjeta(pedido.getTitularTarjeta());
        resumen.setTipoTarjeta(pedido.getTipoTarjeta());
        resumen.setTarjetaEnmascarada(enmascararTarjeta(pedido.getNumeroTarjeta()));
        resumen.setFechaCaducidad(pedido.getFechaCaducidad());

        List<Map<String, Object>> zapatillas = new ArrayList<>();
        double total = 0;
        for (ProductoCarrito linea : lineas) {
            Zapatilla zapatilla = linea.getZapatilla();
            double subtotal = zapatilla.getPrecio() * linea.getCantidad();
            total += subtotal;
            Map<String, Object> fila = new HashMap<>();
            fila.put("marca", zapatilla.getMarca());
            fila.put("modelo", zapatilla.getModelo());
            fila.put("talla", linea.getTalla() == null ? zapatilla.getTalla() : linea.getTalla());
            fila.put("cantidad", linea.getCantidad());
            fila.put("subtotal", subtotal);
            fila.put("subtotalTexto", String.format(java.util.Locale.ROOT, "%.2f", subtotal).replace(".", ","));
            zapatillas.add(fila);
        }
        resumen.setZapatillas(zapatillas);
        resumen.setTotalTexto(String.format(java.util.Locale.ROOT, "%.2f", total).replace(".", ","));
        return resumen;
    }

    @Override
    public void confirmarPedido(Long idUsuario) {
        Pedido pedido = obtenerPedidoActual(idUsuario, false);
        List<ProductoCarrito> lineas = obtenerLineasCarrito(idUsuario);
        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("No hay productos en el carrito para confirmar el pedido.");
        }
        if (pedido.getNombreCompleto() == null || pedido.getDireccion() == null
                || pedido.getProvincia() == null || pedido.getTitularTarjeta() == null
                || pedido.getNumeroTarjeta() == null || pedido.getTipoTarjeta() == null
                || pedido.getTelefono() == null || pedido.getCodigoPostal() == null
                || pedido.getCiudad() == null || pedido.getFechaCaducidad() == null) {
            throw new IllegalArgumentException("Completa los datos de envío y de pago antes de confirmar el pedido.");
        }

        for (ProductoCarrito linea : lineas) {
            ProductoPedido productoPedido = new ProductoPedido();
            productoPedido.setPedido(pedido);
            productoPedido.setZapatilla(linea.getZapatilla());
            productoPedido.setCantidad(linea.getCantidad());
            productoPedido.setTalla(linea.getTalla());
            entityManager.persist(productoPedido);
            pedido.getProductosPedido().add(productoPedido);
            entityManager.remove(linea);
        }
        pedido.setEstado(EstadosPedido.Completado);
    }

    private Pedido obtenerPedidoActual(Long idUsuario, boolean crearSiNoExiste) {
        Usuario usuario = entityManager.find(Usuario.class, idUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("No existe el usuario identificado.");
        }
        List<Pedido> pedidos = entityManager.createQuery(
                        "SELECT p FROM Pedido p WHERE p.usuario.id = :idUsuario AND p.estado = :estado ORDER BY p.id DESC",
                        Pedido.class)
                .setParameter("idUsuario", idUsuario)
                .setParameter("estado", EstadosPedido.EnProceso)
                .setMaxResults(1)
                .getResultList();
        if (!pedidos.isEmpty()) {
            return pedidos.get(0);
        }
        if (!crearSiNoExiste) {
            throw new IllegalArgumentException("No hay un pedido en proceso para confirmar.");
        }
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadosPedido.EnProceso);
        entityManager.persist(pedido);
        return pedido;
    }

    private List<ProductoCarrito> obtenerLineasCarrito(Long idUsuario) {
        return entityManager.createQuery(
                        "SELECT pc FROM ProductoCarrito pc WHERE pc.carrito.usuario.id = :idUsuario",
                        ProductoCarrito.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
    }

    private String enmascararTarjeta(String numero) {
        String digitos = numero.replaceAll("\\s+", "");
        String ultimos = digitos.length() <= 4 ? digitos : digitos.substring(digitos.length() - 4);
        return "•••• •••• •••• " + ultimos;
    }
}
