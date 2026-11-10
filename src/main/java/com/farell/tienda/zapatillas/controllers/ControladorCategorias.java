package com.farell.tienda.zapatillas.controllers;

import com.farell.tienda.zapatillas.servicios.ServicioCategorias;
import com.farell.tienda.zapatillas.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("admin/")
public class ControladorCategorias {

    @Autowired
    private ServicioCategorias servicioCategorias;

    @RequestMapping("gestionarCategorias")
    public String gestionarCategorias(
            Model model,
            @RequestParam(required = false) String error) {
        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
        model.addAttribute("error", error);
        return "admin/gestionar_categorias";
    }

    @GetMapping("registrarCategoria")
    public String registrarCategoria() {
        return "admin/registro_categoria";
    }

    @PostMapping("guardarCategoria")
    public String guardarCategoria(
            @RequestParam String nombre,
            @RequestParam(required = false, defaultValue = "") String descripcion,
            Model model) {
        if (nombre.isBlank()) {
            model.addAttribute("errorNombre", "El nombre es obligatorio.");
            model.addAttribute("nombre", nombre);
            model.addAttribute("descripcion", descripcion);
            return "admin/registro_categoria";
        }

        servicioCategorias.registrarCategoria(new Categoria(nombre.trim(), descripcion.trim()));
        return "redirect:/admin/gestionarCategorias";
    }

    @PostMapping("borrarCategoria")
    public String borrarCategoria(@RequestParam int id, Model model) {
        if (!servicioCategorias.borrarCategoria(id)) {
            model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
            model.addAttribute("error", "No se puede borrar la categoría: no existe o tiene zapatillas asociadas.");
            return "admin/gestionar_categorias";
        }
        return "redirect:/admin/gestionarCategorias";
    }
}
