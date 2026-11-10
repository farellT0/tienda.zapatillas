package com.farell.tienda.zapatillas.controllers;

import com.farell.tienda.zapatillas.servicios.ServicioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/")
public class ControladorUsuarios {

    @Autowired
    private ServicioUsuarios servicioUsuarios;

    @RequestMapping("gestionarUsuarios")
    public String gestionarUsuarios(Model model) {
        model.addAttribute("usuarios", servicioUsuarios.obtenerUsuarios());
        return "admin/gestionar_usuarios";
    }
}
