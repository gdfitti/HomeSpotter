package org.uvigo.esei.example.homespotter.models;

import java.util.List;

/**
 * Clase Vivienda
 *
 * Representa un modelo de vivienda en la aplicación HomeSpotter.
 * Esta clase encapsula los detalles de una propiedad, como su tipo, precio,
 * ubicación, estado, propietario, estado de favorito y fotos asociadas.
 *
 * Atributos:
 * - `id`: Identificador único de la vivienda.
 * - `tipoVivienda`: Tipo de vivienda (ej., casa, apartamento).
 * - `titulo`: Título breve que describe la vivienda.
 * - `precio`: Precio de la vivienda.
 * - `direccion`: Dirección de la vivienda.
 * - `estado`: Estado de la vivienda (disponible, ocupado, etc.).
 * - `contacto`: Información de contacto del propietario o encargado.
 * - `descripcion`: Descripción detallada de la vivienda.
 * - `propietarioId`: Identificador del propietario de la vivienda.
 * - `favorite`: Indica si la vivienda está marcada como favorita por el usuario.
 * - `fotos`: Lista de URLs de fotos asociadas a la vivienda.
 *
 * Constructores:
 * - Vivienda(int id, String titulo, String tipoVivienda, ...): Constructor completo.
 * - Vivienda(String tipoVivienda, String titulo, ...): Constructor sin ID, útil para nuevas propiedades.
 *
 * Métodos principales:
 * - Getters y setters para todos los atributos.
 */
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
    private boolean favorite;
    private List<String> fotos; // URL de la imagen

    /**
     * Constructor completo.
     *
     * @param id Identificador único de la vivienda.
     * @param titulo Título de la vivienda.
     * @param tipoVivienda Tipo de la vivienda (casa, apartamento, etc.).
     * @param precio Precio de la vivienda.
     * @param direccion Dirección de la vivienda.
     * @param estado Estado actual de la vivienda (disponible, ocupado, etc.).
     * @param contacto Información de contacto del propietario o encargado.
     * @param descripcion Descripción detallada de la vivienda.
     * @param propietarioId ID del propietario de la vivienda.
     * @param favorite Indica si la vivienda está marcada como favorita.
     * @param fotos Lista de URLs de fotos asociadas.
     */
    public Vivienda(int id, String titulo, String tipoVivienda, double precio, String direccion, String estado, String contacto, String descripcion, int propietarioId, boolean favorite, List<String> fotos) {
        this.id = id;
        this.titulo = titulo;
        this.tipoVivienda = tipoVivienda;
        this.precio = precio;
        this.direccion = direccion;
        this.estado = estado;
        this.contacto = contacto;
        this.descripcion = descripcion;
        this.propietarioId = propietarioId;
        this.favorite = favorite;
        this.fotos = fotos;
    }

    /**
     * Constructor para nuevas propiedades (sin ID).
     *
     * @param tipoVivienda Tipo de la vivienda.
     * @param titulo Título de la vivienda.
     * @param precio Precio de la vivienda.
     * @param direccion Dirección de la vivienda.
     * @param estado Estado actual de la vivienda.
     * @param contacto Información de contacto.
     * @param descripcion Descripción detallada.
     * @param propietarioId ID del propietario de la vivienda.
     * @param fotoUrl Lista de URLs de fotos asociadas.
     */
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

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
