package com.farell.tienda.zapatillas.controladoresREST;

import com.farell.tienda.zapatillas.model.Zapatilla;
import com.farell.tienda.zapatillas.responseData.InfoZapatillas;
import com.farell.tienda.zapatillas.servicios.ServicioZapatillas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//Un servicio REST
//se le puede llamar tambien API
//esta archivo seria el API de zapatillas

//@RestController no devuelven vistas/htmls sino json
@RestController
@RequestMapping("restZapatillas/")
public class ZapatillasREST {

    @Autowired
    private ServicioZapatillas servicioZapatillas;

    @RequestMapping("listar")
    public InfoZapatillas listar(
            @RequestParam(name = "texto", defaultValue = "") String texto,
            @RequestParam(name = "comienzo", defaultValue = "0") Integer comienzo) {
        InfoZapatillas info = new InfoZapatillas();
        info.setTotalZapatillas(servicioZapatillas.obtenerTotalZapatillas(texto));
        info.setZapatillas(servicioZapatillas.obtenerZapatillasPorTexto(texto, comienzo, 10));
        return info;
    }

    //para testear:
//   http://localhost:8080/restZapatillas/obtener-zapatilla?id=1
    @RequestMapping("obtener-zapatilla")
    public Zapatilla obtenerZapatilla(@RequestParam("id") Long id) {
        //estos return de un @RestController devuelven json
        return servicioZapatillas.obtenerZapatillaPorId(id);
    }

}
