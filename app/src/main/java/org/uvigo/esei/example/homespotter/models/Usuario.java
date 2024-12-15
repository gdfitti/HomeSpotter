package org.uvigo.esei.example.homespotter.models;

/**
 * Modelo de datos para un Usuario.
 *
 * Representa los datos básicos de un usuario en la aplicación, incluyendo su
 * identificador, nombre, correo electrónico, contraseña, foto de perfil y número de teléfono.
 */
public class Usuario {

    // Identificador único del usuario
    private int id;

    // Nombre completo del usuario
    private String nombre;

    // Correo electrónico del usuario
    private String email;

    // Contraseña del usuario
    private String password;

    // URL de la foto de perfil del usuario
    private String foto;

    // Número de teléfono del usuario
    private String tlfno;

    /**
     * Constructor de la clase Usuario.
     *
     * @param id       Identificador único del usuario.
     * @param nombre   Nombre completo del usuario.
     * @param email    Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param foto     URL de la foto de perfil del usuario.
     * @param tlfno    Número de teléfono del usuario.
     */
    public Usuario(int id, String nombre, String email, String password, String foto, String tlfno) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.foto = foto;
        this.tlfno = tlfno;
    }

    /**
     * Obtiene el identificador del usuario.
     *
     * @return ID del usuario.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param id Nuevo ID del usuario.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre completo del usuario.
     *
     * @return Nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre completo del usuario.
     *
     * @param nombre Nuevo nombre del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return Correo electrónico del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email Nuevo correo electrónico del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return Contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param password Nueva contraseña del usuario.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene la URL de la foto de perfil del usuario.
     *
     * @return URL de la foto del usuario.
     */
    public String getFoto() {
        return foto;
    }

    /**
     * Establece la URL de la foto de perfil del usuario.
     *
     * @param foto Nueva URL de la foto del usuario.
     */
    public void setFoto(String foto) {
        this.foto = foto;
    }

    /**
     * Obtiene el número de teléfono del usuario.
     *
     * @return Número de teléfono del usuario.
     */
    public String getTlfno() {
        return tlfno;
    }

    /**
     * Establece el número de teléfono del usuario.
     *
     * @param tlfno Nuevo número de teléfono del usuario.
     */
    public void setTlfno(String tlfno) {
        this.tlfno = tlfno;
    }
}
