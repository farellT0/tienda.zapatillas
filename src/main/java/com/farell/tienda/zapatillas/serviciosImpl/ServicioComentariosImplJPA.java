package com.farell.tienda.zapatillas.serviciosImpl;

import com.farell.tienda.zapatillas.model.Comentario;
import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.model.Zapatilla;
import com.farell.tienda.zapatillas.servicios.ServicioComentarios;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServicioComentariosImplJPA implements ServicioComentarios {

    private static final int MAXIMO_CARACTERES = 500;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Map<String, Object>> obtenerComentarios(Long idZapatilla) {
        List<Comentario> comentarios = entityManager.createQuery(
                        "select c from Comentario c where c.zapatilla.id = :id order by c.id desc",
                        Comentario.class)
                .setParameter("id", idZapatilla)
                .getResultList();
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Comentario comentario : comentarios) {
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("id", comentario.getId());
            fila.put("nombre", comentario.getUsuario().getNombre());
            fila.put("texto", comentario.getTexto());
            fila.put("fecha", comentario.getFecha().format(FORMATO_FECHA));
            resultado.add(fila);
        }
        return resultado;
    }

    @Override
    public void agregarComentario(Long idZapatilla, Long idUsuario, String texto) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }
        if (limpio.length() > MAXIMO_CARACTERES) {
            throw new IllegalArgumentException(
                    "El comentario no puede superar los " + MAXIMO_CARACTERES + " caracteres.");
        }

        Zapatilla zapatilla = entityManager.find(Zapatilla.class, idZapatilla);
        Usuario usuario = entityManager.find(Usuario.class, idUsuario);
        if (zapatilla == null || usuario == null) {
            throw new IllegalArgumentException("No se pudo publicar el comentario.");
        }

        Comentario comentario = new Comentario();
        comentario.setZapatilla(zapatilla);
        comentario.setUsuario(usuario);
        comentario.setTexto(limpio);
        comentario.setFecha(LocalDateTime.now());
        entityManager.persist(comentario);
    }
}
