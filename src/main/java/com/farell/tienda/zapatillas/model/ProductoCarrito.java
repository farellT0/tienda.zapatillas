package com.farell.tienda.zapatillas.model;

import jakarta.persistence.*;

@Entity
public class ProductoCarrito {

    @ManyToOne
    @JoinColumn(name = "zapatilla_id")
    private Zapatilla zapatilla;

    @ManyToOne
    @JoinColumn(name = "carrito_id")
    private Carrito carrito;

    @Id
    @GeneratedValue
    private Long id;

    //campos extra
    private int cantidad;
    private Double talla;

    public Zapatilla getZapatilla() {
        return zapatilla;
    }

    public void setZapatilla(Zapatilla zapatilla) {
        this.zapatilla = zapatilla;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getTalla() {
        return talla;
    }

    public void setTalla(Double talla) {
        this.talla = talla;
    }
}
