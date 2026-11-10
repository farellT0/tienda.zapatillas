package com.farell.tienda.zapatillas.controladorImagen;

import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.servicios.ServicioUsuarios;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

//Controlador que sirve el avatar del usuario indicado

@Controller
public class ControladorImagenUsuario {

    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @RequestMapping("mostrar_imagen_usuario")
    public void mostrarImagen(@RequestParam("id") Long id,
                              HttpServletResponse response) throws IOException {
        Usuario usuario = servicioUsuarios.obtenerUsuarioPorId(id);
        byte[] info = usuario == null ? null : usuario.getAvatar();
        if (info == null || info.length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.setContentType(detectarTipoContenido(info));
        response.getOutputStream().write(info);
        response.getOutputStream().close();
    }

    private String detectarTipoContenido(byte[] imagen) {
        if (imagen.length >= 4 && (imagen[0] & 0xFF) == 0x89 && imagen[1] == 'P') {
            return "image/png";
        }
        if (imagen.length >= 3 && imagen[0] == 'G' && imagen[1] == 'I' && imagen[2] == 'F') {
            return "image/gif";
        }
        if (imagen.length >= 12 && imagen[0] == 'R' && imagen[1] == 'I' && imagen[8] == 'W') {
            return "image/webp";
        }
        return "image/jpeg";
    }
}
