package com.farell.tienda.zapatillas.responseData;

import java.util.List;
import java.util.Map;

public class ResumenPedido {

    private String nombreCompleto;
    private String direccion;
    private String provincia;
    private String telefono;
    private String codigoPostal;
    private String ciudad;
    private String titularTarjeta;
    private String tipoTarjeta;
    private String tarjetaEnmascarada;
    private String fechaCaducidad;
    private String usoPrincipal;
    private boolean embalajeRegalo;
    private String comentarios;
    private List<Map<String, Object>> zapatillas;
    private String totalTexto;

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

    public String getTarjetaEnmascarada() {
        return tarjetaEnmascarada;
    }

    public void setTarjetaEnmascarada(String tarjetaEnmascarada) {
        this.tarjetaEnmascarada = tarjetaEnmascarada;
    }

    public List<Map<String, Object>> getZapatillas() {
        return zapatillas;
    }

    public void setZapatillas(List<Map<String, Object>> zapatillas) {
        this.zapatillas = zapatillas;
    }

    public String getTotalTexto() {
        return totalTexto;
    }

    public void setTotalTexto(String totalTexto) {
        this.totalTexto = totalTexto;
    }
}
