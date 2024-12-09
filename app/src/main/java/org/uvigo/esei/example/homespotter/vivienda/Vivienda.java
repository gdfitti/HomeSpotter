package org.uvigo.esei.example.homespotter.vivienda;

import java.util.List;

public class Vivienda {
    private int id;
    private String tipoVivienda;
    private String titulo;
    private double precio;
    private String direccion;
    private String estado;
    private String contacto;
    private String descripcion;
    private int propietarioId;
    private List<String> fotos; // URL de la imagen

    // Constructor completo
    public Vivienda(int id, String titulo, String tipoVivienda, double precio, String direccion, String estado, String contacto, String descripcion, int propietarioId, List<String> fotos) {
        this.id = id;
        this.titulo = titulo;
        this.tipoVivienda = tipoVivienda;
        this.precio = precio;
        this.direccion = direccion;
        this.estado = estado;
        this.contacto = contacto;
        this.descripcion = descripcion;
        this.propietarioId = propietarioId;
        this.fotos = fotos;
    }

    // Constructor sin ID (para nuevas propiedades)
    public Vivienda(String tipoVivienda, String titulo, double precio, String direccion, String estado, String contacto, String descripcion, int propietarioId, List<String> fotoUrl) {
        this.titulo = titulo;
        this.tipoVivienda = tipoVivienda;
        this.precio = precio;
        this.direccion = direccion;
        this.estado = estado;
        this.contacto = contacto;
        this.descripcion = descripcion;
        this.propietarioId = propietarioId;
        this.fotos = fotos;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipoVivienda() {
        return tipoVivienda;
    }

    public void setTipoVivienda(String tipoVivienda) {
        this.tipoVivienda = tipoVivienda;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(int propietarioId) {
        this.propietarioId = propietarioId;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
