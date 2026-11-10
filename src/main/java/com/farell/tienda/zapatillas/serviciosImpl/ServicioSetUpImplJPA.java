package com.farell.tienda.zapatillas.serviciosImpl;

import com.farell.tienda.zapatillas.model.Categoria;
import com.farell.tienda.zapatillas.model.Carrito;
import com.farell.tienda.zapatillas.model.Configuracion;
import com.farell.tienda.zapatillas.model.ProductoCarrito;
import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.model.Zapatilla;
import com.farell.tienda.zapatillas.servicios.ServicioPedidos;
import com.farell.tienda.zapatillas.servicios.ServicioSetUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
@Transactional
public class ServicioSetUpImplJPA implements ServicioSetUp {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ServicioPedidos servicioPedidos;

    @Override
    public void prepararRegistrosIniciales() {
        // preparar unos registros si la bd esta vacia
        Configuracion configuracion = null;
        // la idea presentada se basa que va a haber un solo registro
        // en la tabla de Configuracion, ese registro nos indicara
        // si la base de datos ya tiene sus registros iniciales
        // como por ejemplo: productos, categorias, usuarios, pedidos etc.

        //en sql seria como: select * from configuracion as c
        try {
            configuracion =
                    (Configuracion)
                            entityManager.createQuery(
                                            "select c from Configuracion c").
                                    getSingleResult();
        }catch (NoResultException e){
            System.out.println(
                    "no se encontro un unico registro en la tabla de Configuracion");
        }
        if( configuracion == null || ! configuracion.isRegistrosIniciales() ){
            //hago todos los registros iniciales
            //categorias
            Categoria cRunning = new Categoria("Running", "Zapatillas para correr");
            Categoria cBaloncesto = new Categoria("Baloncesto", "Zapatillas de baloncesto");
            Categoria cCasual = new Categoria("Casual", "Zapatillas para el dia a dia");
            Categoria cSkate = new Categoria("Skate", "Zapatillas de skate");
            entityManager.persist(cRunning);
            entityManager.persist(cBaloncesto);
            entityManager.persist(cCasual);
            entityManager.persist(cSkate);
            //productos
            //el primer registro tendra id 1
            Zapatilla zAirMax = new Zapatilla("Nike", "Air Max 90", 129.99, 42, "Blanco", "Hombre", "Un clasico con la camara de aire visible", "COD-0001");
            zAirMax.setCategoria(cCasual);
            zAirMax.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/1.jpg"));
            entityManager.persist(zAirMax);

            Zapatilla zUltraboost = new Zapatilla("Adidas", "Ultraboost 22", 189.0, 39, "Negro", "Mujer", "Amortiguacion para correr largas distancias", "COD-0002");
            zUltraboost.setCategoria(cRunning);
            zUltraboost.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/2.jpg"));
            entityManager.persist(zUltraboost);

            Zapatilla zConverse = new Zapatilla("Converse", "Chuck Taylor All Star", 65.0, 40, "Blanco", "Unisex", "La lona de siempre, vale para todo", "COD-0003");
            zConverse.setCategoria(cCasual);
            zConverse.setImagenPortada(leerImagenInicial(
                    "http://localhost:8080/imagenes_productos_iniciales/3.jpg"));
            entityManager.persist(zConverse);

            Usuario u1 = new Usuario("Pepe", "pepe@gmail.com", "123");
            entityManager.persist(u1);
            entityManager.flush();

            Carrito carrito = new Carrito();
            carrito.setUsuario(u1);
            u1.setCarrito(carrito);
            entityManager.persist(carrito);

            ProductoCarrito productoCarrito = new ProductoCarrito();
            productoCarrito.setCarrito(carrito);
            productoCarrito.setZapatilla(zAirMax);
            productoCarrito.setCantidad(1);
            productoCarrito.setTalla(42.0);
            entityManager.persist(productoCarrito);

            servicioPedidos.procesarPaso1(
                    "Pepe Pérez", "Calle Mayor 10", "Madrid",
                    "+34 600123456", "28001", "Madrid", u1.getId());
            servicioPedidos.procesarPaso2(
                    "Pepe Pérez", "4111111111111111", "VISA", "12/30", u1.getId());

            boolean tieneProductosEnCarrito = !entityManager.createQuery(
                            "SELECT pc FROM ProductoCarrito pc WHERE pc.carrito.id = :idCarrito",
                            ProductoCarrito.class)
                    .setParameter("idCarrito", carrito.getId())
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
            if (tieneProductosEnCarrito) {
                servicioPedidos.confirmarPedido(u1.getId());
            }

            //una vez realizados los registros iniciales
            //guardo en registro en la tabla Configuracion
            // indicando que todo esta ok
            configuracion = new Configuracion();
            configuracion.setRegistrosIniciales(true);
            entityManager.persist(configuracion);
        }//end if

        // para bases de datos creadas antes de existir el modulo de pedidos
        crearPedidoDeEjemploSiNoHay();
    }//end prepararRegistrosIniciales

    // crea un pedido de ejemplo solo si la tabla de pedidos esta vacia
    private void crearPedidoDeEjemploSiNoHay() {
        Long totalPedidos = entityManager.createQuery(
                "select count(p) from Pedido p", Long.class).getSingleResult();
        if (totalPedidos > 0) {
            return;
        }

        List<Zapatilla> zapatillas = entityManager.createQuery(
                        "select z from Zapatilla z order by z.id", Zapatilla.class)
                .setMaxResults(1)
                .getResultList();
        if (zapatillas.isEmpty()) {
            return; // sin productos no se puede montar un pedido
        }
        Zapatilla zapatilla = zapatillas.get(0);

        // usuario Pepe: se reutiliza si ya existe
        List<Usuario> usuarios = entityManager.createQuery(
                        "select u from Usuario u where u.email = :email", Usuario.class)
                .setParameter("email", "pepe@gmail.com")
                .getResultList();
        Usuario u1;
        if (usuarios.isEmpty()) {
            u1 = new Usuario("Pepe", "pepe@gmail.com", "123");
            entityManager.persist(u1);
            entityManager.flush();
        } else {
            u1 = usuarios.get(0);
        }

        // carrito de Pepe: se reutiliza si ya existe
        List<Carrito> carritos = entityManager.createQuery(
                        "select c from Carrito c where c.usuario.id = :idUsuario", Carrito.class)
                .setParameter("idUsuario", u1.getId())
                .getResultList();
        Carrito carrito;
        if (carritos.isEmpty()) {
            carrito = new Carrito();
            carrito.setUsuario(u1);
            u1.setCarrito(carrito);
            entityManager.persist(carrito);
        } else {
            carrito = carritos.get(0);
        }

        // si el carrito esta vacio, se le pone una zapatilla
        if (carrito.getProductosCarrito().isEmpty()) {
            ProductoCarrito linea = new ProductoCarrito();
            linea.setCarrito(carrito);
            linea.setZapatilla(zapatilla);
            linea.setCantidad(1);
            linea.setTalla(zapatilla.getTalla());
            entityManager.persist(linea);
        }
        entityManager.flush();

        servicioPedidos.procesarPaso1(
                "Pepe Pérez", "Calle Mayor 10", "Madrid",
                "+34 600123456", "28001", "Madrid", u1.getId());
        servicioPedidos.procesarPaso2(
                "Pepe Pérez", "4111111111111111", "VISA", "12/30", u1.getId());
        servicioPedidos.confirmarPedido(u1.getId());
    }//end crearPedidoDeEjemploSiNoHay

    private byte[] leerImagenInicial(String ruta_imagen){
        byte[] info = null;
        try {
            URL url = new URL(ruta_imagen);
            info = IOUtils.toByteArray(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return info;
    }

}//end class
