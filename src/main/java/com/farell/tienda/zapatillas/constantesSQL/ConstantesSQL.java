package com.farell.tienda.zapatillas.constantesSQL;

public class ConstantesSQL {

    // numero_imagenes: la portada (si existe) mas las imagenes adicionales
    public final static String SQL_OBTENER_ZAPATILLAS_JSON =
            "select tz.id, tz.marca, tz.modelo, tz.precio, tz.talla, tz.color, tz.genero, tz.descripcion, c.nombre as categoria, " +
            "(case when tz.imagen_portada is null then 0 else 1 end) + " +
            "(select count(*) from tabla_zapatillas_imagenes as ti where ti.zapatilla_id = tz.id) as numero_imagenes " +
            "from TABLA_ZAPATILLAS as tz, CATEGORIA as c where tz.categoria_id = c.id";

    public static final String SQL_OBTENER_ZAPATILLA_JSON =
            "select tz.id, tz.marca, tz.modelo, tz.precio, tz.talla, tz.color, tz.genero, tz.descripcion, c.nombre as categoria, " +
            "(case when tz.imagen_portada is null then 0 else 1 end) + " +
            "(select count(*) from tabla_zapatillas_imagenes as ti where ti.zapatilla_id = tz.id) as numero_imagenes " +
            "from TABLA_ZAPATILLAS as tz, CATEGORIA as c where tz.categoria_id = c.id and tz.id = :id";
}
