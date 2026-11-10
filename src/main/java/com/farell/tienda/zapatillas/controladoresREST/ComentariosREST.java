package com.farell.tienda.zapatillas.controladoresREST;

import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.servicios.ServicioComentarios;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("restComentarios/")
public class ComentariosREST {

    @Autowired
    private ServicioComentarios servicioComentarios;

    @GetMapping("listar")
    public List<Map<String, Object>> listar(@RequestParam("idZapatilla") Long idZapatilla) {
        return servicioComentarios.obtenerComentarios(idZapatilla);
    }

    @PostMapping("agregar")
    public ResponseEntity<String> agregar(
            @RequestParam("idZapatilla") Long idZapatilla,
            @RequestParam("texto") String texto,
            HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario_identificado");
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifícate para poder comentar.");
        }
        try {
            servicioComentarios.agregarComentario(idZapatilla, usuario.getId(), texto);
            return ResponseEntity.ok("Comentario publicado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
