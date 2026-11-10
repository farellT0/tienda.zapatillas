package com.farell.tienda.zapatillas.serviciosImpl;

import com.farell.tienda.zapatillas.model.Carrito;
import com.farell.tienda.zapatillas.model.ProductoCarrito;
import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.model.Zapatilla;
import com.farell.tienda.zapatillas.servicios.ServicioCarrito;
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
public class ServicioCarritoImplJPA implements ServicioCarrito {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void agregarProducto(long idUsuario, long idProducto, int cantidad, double talla) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (!Double.isFinite(talla) || talla < 35 || talla > 48 || talla * 2 % 1 != 0) {
            throw new IllegalArgumentException("La talla debe estar entre 35 y 48, en incrementos de media talla.");
        }

        Usuario usuario = entityManager.find(Usuario.class, idUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("No existe el usuario.");
        }

        List<Carrito> carritos = entityManager.createQuery(
                        "SELECT c FROM Carrito c WHERE c.usuario.id = :idUsuario", Carrito.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
        Carrito c = carritos.isEmpty() ? null : carritos.get(0);
        if (c == null) {
            c = new Carrito();
            c.setUsuario(usuario);
            entityManager.persist(c);
        }

        Zapatilla zapatilla = entityManager.find(Zapatilla.class, idProducto);
        if (zapatilla == null) {
            throw new IllegalArgumentException("No existe el producto.");
        }

        List<ProductoCarrito> lineas = entityManager.createQuery(
                        "select pc from ProductoCarrito pc " +
                                "where pc.carrito.id = :idCarrito and pc.zapatilla.id = :idProducto",
                        ProductoCarrito.class)
                .setParameter("idCarrito", c.getId())
                .setParameter("idProducto", idProducto)
                .getResultList();
        for (ProductoCarrito linea : lineas) {
            double tallaGuardada = linea.getTalla() == null
                    ? zapatilla.getTalla()
                    : linea.getTalla();
            if (Double.compare(tallaGuardada, talla) == 0) {
                linea.setTalla(talla);
                linea.setCantidad(linea.getCantidad() + cantidad);
                return;
            }
        }

        ProductoCarrito nuevaLinea = new ProductoCarrito();
        nuevaLinea.setCarrito(c);
        nuevaLinea.setZapatilla(zapatilla);
        nuevaLinea.setCantidad(cantidad);
        nuevaLinea.setTalla(talla);
        entityManager.persist(nuevaLinea);
    }

    @Override
    public List<Map<String, Object>> obtenerProductosCarrito(long idUsuario) {
        List<ProductoCarrito> productosCarrito = entityManager.createQuery(
                        "select pc from ProductoCarrito pc where pc.carrito.usuario.id = :idUsuario",
                        ProductoCarrito.class)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (ProductoCarrito productoCarrito : productosCarrito) {
            Zapatilla zapatilla = productoCarrito.getZapatilla();
            Map<String, Object> fila = new HashMap<>();
            fila.put("idLinea", productoCarrito.getId());
            fila.put("id", zapatilla.getId());
            fila.put("marca", zapatilla.getMarca());
            fila.put("modelo", zapatilla.getModelo());
            fila.put("talla", productoCarrito.getTalla() == null
                    ? zapatilla.getTalla()
                    : productoCarrito.getTalla());
            fila.put("precio", zapatilla.getPrecio());
            fila.put("cantidad", productoCarrito.getCantidad());
            resultado.add(fila);
        }
        return resultado;
    }

    @Override
    public boolean eliminarProducto(long idUsuario, long idProductoCarrito) {
        List<ProductoCarrito> lineas = entityManager.createQuery(
                        "select pc from ProductoCarrito pc " +
                                "where pc.id = :idProductoCarrito and pc.carrito.usuario.id = :idUsuario",
                        ProductoCarrito.class)
                .setParameter("idProductoCarrito", idProductoCarrito)
                .setParameter("idUsuario", idUsuario)
                .getResultList();
        if (lineas.isEmpty()) {
            return false;
        }
        entityManager.remove(lineas.get(0));
        return true;
    }
}
