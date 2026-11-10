package com.farell.tienda.zapatillas.controladoresREST;

// necesario para registrar/identificar usuarios
// en la parte publica

import com.farell.tienda.zapatillas.model.Usuario;
import com.farell.tienda.zapatillas.servicios.ServicioUsuarios;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("restUsuarios/")
public class UsuariosREST {

    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @PostMapping("registrarUsuario")
    public String registrarUsuario(@RequestParam Map<String, String> formData,
                                   @RequestParam(name = "avatar", required = false) MultipartFile avatar,
                                   HttpServletRequest request){
        String nombre = formData.get("nombre");
        String email = formData.get("email");
        String pass = formData.get("pass");
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank()
                || pass == null || pass.isBlank()) {
            return "datos incorrectos";
        }
        email = email.trim();
        if (servicioUsuarios.obtenerUsuarioPorEmail(email) != null) {
            return "email o pass incorrectos";
        }

        Gson gson = new Gson();
        JsonElement json = gson.toJsonTree(Map.of(
                "nombre", nombre.trim(),
                "email", email,
                "pass", pass
        ));
        Usuario usuario = gson.fromJson(json, Usuario.class);
        if (avatar != null && !avatar.isEmpty()) {
            String tipo = avatar.getContentType();
            if (tipo == null || !tipo.startsWith("image/")) {
                return "El avatar debe ser una imagen.";
            }
            try {
                usuario.setAvatar(avatar.getBytes());
            } catch (IOException e) {
                return "No se pudo leer el avatar.";
            }
        }
        servicioUsuarios.registrarUsuario(usuario);
        request.getSession().setAttribute("usuario_identificado", usuario);
        return "ok," + usuario.getNombre();
    }

    //recordatorio: todos estos metodos se llaman desde el js de index.html
    @RequestMapping("identificarUsuario")
    public String identificarUsuario(
            @RequestParam("email") String email,
            @RequestParam("pass") String pass,
            HttpServletRequest request){
        //comprobar si hay un usuario con este email y pass en bd
        //si es asi, los datos son correctos
        //marcamos al usuario en cuestion que está identificado
        //usando una variable de sesion como hicimos con el interceptor
        //de administracion
        Usuario u = servicioUsuarios.obtenerUsuarioPorEmailYpass(email,pass);
        String respuesta = "";
        if( u != null ){
            request.getSession().setAttribute("usuario_identificado", u );
            respuesta = "ok," + u.getNombre() ;
        }else{
            respuesta = "email o pass incorrectos";
        }
        return respuesta;
    }

    @GetMapping("sesion")
    public Map<String, Object> sesion(HttpServletRequest request) {
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario_identificado");
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("identificado", usuario != null);
        if (usuario != null) {
            respuesta.put("id", usuario.getId());
            respuesta.put("nombre", usuario.getNombre());
            respuesta.put("email", usuario.getEmail());
        }
        return respuesta;
    }

    @PostMapping("cerrarSesion")
    public String cerrarSesion(HttpServletRequest request) {
        request.getSession().removeAttribute("usuario_identificado");
        return "ok";
    }

}
