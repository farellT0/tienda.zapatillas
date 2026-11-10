package com.farell.tienda.zapatillas.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tabla_comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Zapatilla zapatilla;

    @ManyToOne(optional = false)
    private Usuario usuario;

    @Column(nullable = false, length = 500)
    private String texto;

    private LocalDateTime fecha;

    public Comentario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Zapatilla getZapatilla() {
        return zapatilla;
    }

    public void setZapatilla(Zapatilla zapatilla) {
        this.zapatilla = zapatilla;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
