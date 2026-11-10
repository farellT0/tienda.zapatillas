package com.farell.tienda.zapatillas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tabla_zapatillas")
public class Zapatilla {

    public static final int MAXIMO_IMAGENES = 5;

    //una forma muy comoda e interesente para hacer validaciones
    // es usar un standar que se llama JSR 303
    // se basa en poner anotaciones de validacion a cada campo

    @Size(min = 2, max = 30, message = "marca debe tener entre 2 y 30 caracteres")
    @NotEmpty(message = "debes insertar una marca")
    // una expresion regular es una forma de indicar
    // que queremos o no en un campo de entrada de usuario
    @Pattern(regexp = "[a-záéíóúñA-ZÁÉÍÓÚÑüÜ 0-9]+",
            message = "marca solo puede tener letras numeros y espacios")
    private String marca;

    @Size(min = 2, max = 40, message = "modelo debe tener entre 2 y 40 caracteres")
    @NotEmpty(message = "debes insertar un modelo")
    private String modelo;

    @Min(value = 1, message = "precio minimo 1 euro")
    @Max(value = 99999, message = "el precio maximo es 99999 euros")
    private double precio;

    @Min(value = 16, message = "talla minima 16")
    @Max(value = 50, message = "talla maxima 50")
    private double talla;

    @NotEmpty(message = "debes indicar un color")
    private String color;
       
    @NotEmpty(message = "debes indicar un genero")
    private String genero;

    private String descripcion;

    private String sku; //stock keeping unit //numero de control del producto

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //este campo va a ser solo
    //gestiar la subida de archivo, el input type="file"
    // esto es solo para recibir facilmente la imagen del form
    // @Transient es para decir que el campo sea ignorado de cara a la base de datos
    @Transient
    private List<MultipartFile> fotosSubidas = new ArrayList<>();

    //el siguiente campo es para almacenar la imagen
    //en base de datos
    @Lob // para tipos de columna, p.ej blob o clob, para guardar imagenes/archivos/arrays de bytes
    private byte[] imagenPortada;

    //En asociaciones podemos especificar dos opciones para
    // fetch
    // LAZY (vago) - Cuando se pida una zapatilla, no se va a pedir ningun dato de categoria a la base de datos
    // EAGER (ansioso) - Cuando se pida una zapatilla, por defecto se pide a la base de datos la informacion de su categoria
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Categoria categoria;

    //para gestionar la id del form y para otros usos
    @Transient
    private int idCategoria;

    @ElementCollection
    @CollectionTable(name = "tabla_zapatillas_imagenes",
            joinColumns = @JoinColumn(name = "zapatilla_id"))
    @Column(name = "imagen", nullable = false)
    @Lob
    private List<byte[]> imagenesAdicionales = new ArrayList<>();

    public Zapatilla() {
    }

    public Zapatilla(String marca, String modelo, double precio, double talla, String color, String genero, String descripcion) {
        this.marca = marca;
        this.modelo = modelo;
        this.precio = precio;
        this.talla = talla;
        this.color = color;
        this.genero = genero;
        this.descripcion = descripcion;
    }

    public Zapatilla(String marca, String modelo, double precio, double talla, String color, String genero, String descripcion, String sku) {
        this(marca, modelo, precio, talla, color, genero, descripcion);
        this.sku = sku;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getTalla() {
        return talla;
    }

    public void setTalla(double talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @JsonIgnore
    public Categoria getCategoria() {
        return categoria;
    }

    public String getCategoriaNombre() {
        return categoria == null ? null : categoria.getNombre();
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonIgnore
    public List<MultipartFile> getFotosSubidas() {
        return fotosSubidas;
    }

    public void setFotosSubidas(List<MultipartFile> fotosSubidas) {
        this.fotosSubidas = fotosSubidas;
    }

    public byte[] getImagenPortada() {
        return imagenPortada;
    }

    public void setImagenPortada(byte[] imagenPortada) {
        this.imagenPortada = imagenPortada;
    }

    @JsonIgnore
    public List<byte[]> getImagenesAdicionales() {
        return imagenesAdicionales;
    }

    public void setImagenesAdicionales(List<byte[]> imagenesAdicionales) {
        this.imagenesAdicionales = imagenesAdicionales;
    }

    public int getNumeroImagenes() {
        int portada = imagenPortada == null || imagenPortada.length == 0 ? 0 : 1;
        return portada + imagenesAdicionales.size();
    }

    @Override
    public String toString() {
        return "Zapatilla{" +
                "marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", precio=" + precio +
                ", talla=" + talla +
                ", color='" + color + '\'' +
                ", genero='" + genero + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", id=" + id +
                '}';
    }
}
