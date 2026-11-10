package com.farell.tienda.zapatillas.servicios;

import com.farell.tienda.zapatillas.model.Usuario;

import java.util.List;

public interface ServicioUsuarios {

    List<Usuario> obtenerUsuarios();

    void registrarUsuario(Usuario u);

    Usuario obtenerUsuarioPorEmail(String email);

    Usuario obtenerUsuarioPorId(Long id);

    Usuario obtenerUsuarioPorEmailYpass(String email, String pass);

}
