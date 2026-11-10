package com.farell.tienda.zapatillas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ControladorAdmin {

    @RequestMapping("loginAdmin")
    public String loginAdmin(
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        model.addAttribute("errorLogin", error != null);
        return "admin/login_admin";
    }

    @RequestMapping("admin/inicioAdmin")
    public String inicioAdmin() {
        return "admin/inicio_admin";
    }

    @RequestMapping("cerrarSesionAdmin")
    public String cerrarSesionAdmin(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        return "redirect:/loginAdmin";
    }
}