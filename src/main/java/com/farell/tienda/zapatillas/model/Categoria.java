package com.farell.tienda.zapatillas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tabla_categorias")
public class Categoria {

    private String nombre;
    private String descripcion;

    //voy a especificar la asociacion de Categoria
    //con Zapatilla
    // asociaciones en hibernate/jpa:
    // OneToOne: 1 cat solo puede ser asignada a 1 prod
    // OneToMany: 1 cat puede estar asociada a muchos productos
    // ManyToOne: muchas cat pueden estar asociadas a 1 prod
    // ManyToMany: muchas cat pueden ser asignadas a muchos productos

    @OneToMany(mappedBy = "categoria")
    private List<Zapatilla> zapatillas = new ArrayList<Zapatilla>();

    @Id
    @GeneratedValue
    private int id;

    public Categoria() {
    }

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @JsonIgnore
    public List<Zapatilla> getZapatillas() {
        return zapatillas;
    }

    public void setZapatillas(List<Zapatilla> zapatillas) {
        this.zapatillas = zapatillas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
