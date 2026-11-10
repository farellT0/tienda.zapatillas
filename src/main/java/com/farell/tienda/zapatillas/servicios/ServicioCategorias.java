package com.farell.tienda.zapatillas.servicios;

import com.farell.tienda.zapatillas.model.Categoria;

import java.util.List;

public interface ServicioCategorias {

    List<Categoria> obtenerCategorias();

    void registrarCategoria(Categoria categoria);

    boolean borrarCategoria(int id);
}
