package com.farell.tienda.zapatillas.servicios;

import com.farell.tienda.zapatillas.model.Zapatilla;

import java.util.List;
import java.util.Map;

public interface ServicioZapatillas {

    void registrarZapatilla(Zapatilla z);

    List<Zapatilla> obtenerZapatillas();

    List<Zapatilla> obtenerZapatillasPorTexto(String texto, int comienzo, int resultadosPorPagina);

    int obtenerTotalZapatillas(String texto);

    Zapatilla obtenerZapatillaPorId(long id);

    byte[] obtenerImagenZapatilla(long id, int numero);

    List<Integer> obtenerNumerosImagenes(long id);

    void borrarZapatilla(long id) throws Exception;

    void guardarCambiosZapatilla(Zapatilla zapatillaEditar) throws Exception;

    List<Map<String, Object>> obtenerJSONzapatillas();

    Map<String, Object> obtenerJSONZapatillaPorId(Long id);
}
