package com.farell.tienda.zapatillas.model;

import jakarta.persistence.*;

@Entity
public class ProductoPedido {

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "zapatilla_id")
    private Zapatilla zapatilla;

    private int cantidad;
    private Double talla;

    @Id
    @GeneratedValue
    private Long id;

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Zapatilla getZapatilla() {
        return zapatilla;
    }

    public void setZapatilla(Zapatilla zapatilla) {
        this.zapatilla = zapatilla;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
