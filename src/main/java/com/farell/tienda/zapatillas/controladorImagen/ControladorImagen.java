package com.farell.tienda.zapatillas.controladorImagen;

import com.farell.tienda.zapatillas.servicios.ServicioZapatillas;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//Controlador que sirve para ofrecer la imagen indicada

@Controller
public class ControladorImagen {

    @Autowired
    private ServicioZapatillas servicioZapatillas;

    @RequestMapping("mostrar_imagen")
    public void mostrarImagen(@RequestParam("id") Long id,
                              @RequestParam(value = "numero", defaultValue = "1") int numero,
                              HttpServletResponse response) throws IOException {
        byte[] info = servicioZapatillas.obtenerImagenZapatilla(id, numero);
        if (info == null) {
            return;
        }
        response.setContentType(detectarTipoContenido(info));
        response.getOutputStream().write(info);
        response.getOutputStream().close();
    }

    private String detectarTipoContenido(byte[] imagen) {
        if (imagen.length >= 3
                && (imagen[0] & 0xFF) == 0xFF
                && (imagen[1] & 0xFF) == 0xD8
                && (imagen[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if (imagen.length >= 8
                && (imagen[0] & 0xFF) == 0x89
                && imagen[1] == 'P'
                && imagen[2] == 'N'
                && imagen[3] == 'G') {
            return "image/png";
        }
        if (imagen.length >= 6
                && (new String(imagen, 0, 6, StandardCharsets.US_ASCII)
                .equals("GIF87a")
                || new String(imagen, 0, 6, StandardCharsets.US_ASCII)
                .equals("GIF89a"))) {
            return "image/gif";
        }
        if (imagen.length >= 12
                && new String(imagen, 0, 4, StandardCharsets.US_ASCII)
                .equals("RIFF")
                && new String(imagen, 8, 4, StandardCharsets.US_ASCII)
                .equals("WEBP")) {
            return "image/webp";
        }
        return "application/octet-stream";
    }
}