package com.farell.tienda.zapatillas.model;

import com.farell.tienda.zapatillas.constantesPedidos.EstadosPedido;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    @ManyToOne(optional = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.EAGER)
    private List<ProductoPedido> productosPedido = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EstadosPedido estado;

    private String nombreCompleto;
    private String direccion;
    private String provincia;
    private String telefono;
    private String codigoPostal;
    private String ciudad;
    private String titularTarjeta;
    private String numeroTarjeta;
    private String tipoTarjeta;
    private String fechaCaducidad;
    private String usoPrincipal;
    private boolean embalajeRegalo;
    private String comentarios;

    @Id
    @GeneratedValue
    private Long id;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ProductoPedido> getProductosPedido() {
        return productosPedido;
    }

    public void setProductosPedido(List<ProductoPedido> productosPedido) {
        this.productosPedido = productosPedido;
    }

    public EstadosPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadosPedido estado) {
        this.estado = estado;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getTitularTarjeta() {
        return titularTarjeta;
    }

    public void setTitularTarjeta(String titularTarjeta) {
        this.titularTarjeta = titularTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(String fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getUsoPrincipal() {
        return usoPrincipal;
    }

    public void setUsoPrincipal(String usoPrincipal) {
        this.usoPrincipal = usoPrincipal;
    }

    public boolean isEmbalajeRegalo() {
        return embalajeRegalo;
    }

    public void setEmbalajeRegalo(boolean embalajeRegalo) {
        this.embalajeRegalo = embalajeRegalo;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
