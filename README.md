# SNEAKER HOUSE · tienda online de zapatillas

Proyecto de clase (DAW) hecho con **Spring Boot 4.1.1**, Java 17+, JPA/Hibernate, H2, Thymeleaf (administración) y jQuery + Mustache + Tailwind (parte pública como SPA). Basado en el ejemplo `tienda.online` (videojuegos) y ampliado.

## Funciones
- Catálogo con buscador (marca/modelo) y paginación
- Detalle con varias fotos y comentarios (leer: público; comentar: con sesión)
- Registro/login con avatar opcional, carrito y pedido en pasos, "Mis pedidos"
- Administración: zapatillas, categorías, usuarios y pedidos
- Idiomas: español, inglés y francés (`?idioma=es|en|fr`)

## Ejecutar
1. Copia `secreto.properties.ejemplo` a `secreto.properties` y define `ADMIN_PASSWORD`.
2. Desde la raíz del proyecto: `./mvnw spring-boot:run` (o ejecuta `Application` en IntelliJ con *Working directory* en esta carpeta).
3. Público: http://localhost:8080 · Administración: http://localhost:8080/admin/inicioAdmin

La base de datos H2 se crea en `./data` y se carga con datos de ejemplo en el primer arranque (usuario de prueba `pepe@gmail.com`).

Memoria del proyecto: [`docs/Memoria_SneakerHouse.pdf`](docs/Memoria_SneakerHouse.pdf)
