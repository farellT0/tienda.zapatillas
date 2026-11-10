package com.farell.tienda.zapatillas.servicios;

import java.util.List;
import java.util.Map;

public interface ServicioComentarios {

    List<Map<String, Object>> obtenerComentarios(Long idZapatilla);

    void agregarComentario(Long idZapatilla, Long idUsuario, String texto);
}
