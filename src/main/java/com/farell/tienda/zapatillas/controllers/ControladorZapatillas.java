package com.farell.tienda.zapatillas.controllers;

import com.farell.tienda.zapatillas.model.Zapatilla;
import com.farell.tienda.zapatillas.servicios.ServicioCategorias;
import com.farell.tienda.zapatillas.servicios.ServicioZapatillas;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("admin/")
public class ControladorZapatillas {

    //pido a spring que me de el objeto que tiene de la unica clase que tiene el implements de ServicioZapatillas
    //dicho objeto, es el objeto de la clase que tiene @Service class ServicioZapatillasImplJPA implements ServicioZapatillas
    @Autowired
    private ServicioZapatillas servicioZapatillas;
    @Autowired
    private ServicioCategorias servicioCategorias;

    @RequestMapping("gestionarZapatillas")
    public String gestionarZapatillas(
            @RequestParam(name = "titulo", defaultValue = "") String titulo,
            @RequestParam(name = "comienzo", defaultValue = "0") Integer comienzo,
            Model model) {
        int pagina = Math.max(comienzo, 0);
        model.addAttribute("zapatillas",
                servicioZapatillas.obtenerZapatillasPorTexto(titulo, pagina, 10));
        model.addAttribute("siguiente", pagina + 10);
        model.addAttribute("anterior", pagina - 10);
        model.addAttribute("total", servicioZapatillas.obtenerTotalZapatillas(titulo));
        model.addAttribute("titulo", titulo);
        return "admin/gestionar_productos";
    }

    @RequestMapping("registrarZapatilla")
    public String registrarZapatilla(Model model) {
        Zapatilla nueva = new Zapatilla();
        nueva.setPrecio(1);
        nueva.setTalla(42);
        model.addAttribute("nueva", nueva);
        // necesito obtener las categorias
        // para meterlas en este model de spring
        // y que le lleguen al html del return
        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
        return "admin/registro_producto";
    }

    @RequestMapping("guardarZapatilla")
    public String guardarZapatilla(
            @ModelAttribute("nueva")
            @Valid
            Zapatilla nuevaZapatilla,
            BindingResult resultadoValidacion,
            Model model) {
        System.out.println("recibido para registrar: " + nuevaZapatilla);
        if (resultadoValidacion.hasErrors()) {
            // si la zapatilla tiene errores, mandamos al usuario a
            // el formulario de nuevo (con las categorias para el desplegable)
            model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
            return "admin/registro_producto";
        }
        int numeroFotos = contarFotosSubidas(nuevaZapatilla);
        if (numeroFotos == 0 || numeroFotos > Zapatilla.MAXIMO_IMAGENES) {
            resultadoValidacion.rejectValue("fotosSubidas", "fotos.invalidas",
                    "Selecciona entre 1 y " + Zapatilla.MAXIMO_IMAGENES + " imágenes.");
            model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
            return "admin/registro_producto";
        }
        servicioZapatillas.registrarZapatilla(nuevaZapatilla);
        return "admin/registro_producto_ok";
    }

    @RequestMapping("borrarZapatilla")
    public String borrarZapatilla(@RequestParam("id") Long id, Model model) throws Exception {
        //repaso: model es una variable donde meto lo que quiera que le llegue
        //a la vista indicada en el return
        System.out.println("id recibida para borrar registro: " + id);
        servicioZapatillas.borrarZapatilla(id);
        return gestionarZapatillas("", 0, model);
    }

    @RequestMapping("editarZapatilla")
    public String editarZapatilla(@RequestParam("id") Long id, Model model) {
        Zapatilla zEditar = servicioZapatillas.obtenerZapatillaPorId(id);
        zEditar.setIdCategoria(zEditar.getCategoria().getId());

        model.addAttribute("zapatillaEditar", zEditar);
        model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
        model.addAttribute("numerosImagenes", servicioZapatillas.obtenerNumerosImagenes(id));
        return "admin/editar_producto";
    }

    //el metodo va a atender la ruta guardarCambiosZapatilla
    // a esa ruta se llega al hacer click en el GUARDAR CAMBIOS del form de edicion
    @RequestMapping("guardarCambiosZapatilla")
    public String guardarCambiosZapatilla(
            @ModelAttribute("zapatillaEditar") //th:object del form
            @Valid //para validar con las reglas puestas con @ en Zapatilla.java
            Zapatilla zapatillaEditar, //objeto que contiene lo introducido en el form
            BindingResult resultadoValidacion,
            Model model) throws Exception { //model en estos metodos, es para meterle lo que queramos que le llegue a la vista devuelta en el return
        if (resultadoValidacion.hasErrors()) {
            model.addAttribute("categorias", servicioCategorias.obtenerCategorias());
            model.addAttribute("numerosImagenes",
                    servicioZapatillas.obtenerNumerosImagenes(zapatillaEditar.getId()));
            return "admin/editar_producto";
        } else {
            servicioZapatillas.guardarCambiosZapatilla(zapatillaEditar);
            return gestionarZapatillas("", 0, model);
        }
    }

    private int contarFotosSubidas(Zapatilla zapatilla) {
        List<MultipartFile> fotos = zapatilla.getFotosSubidas();
        if (fotos == null) {
            return 0;
        }
        return (int) fotos.stream().filter(foto -> foto != null && !foto.isEmpty()).count();
    }

}//end class
