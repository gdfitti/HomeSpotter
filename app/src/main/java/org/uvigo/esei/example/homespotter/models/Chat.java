package org.uvigo.esei.example.homespotter.models;

/**
 * Modelo de datos para un Chat.
 *
 * Representa la información básica de un chat en la aplicación, incluyendo
 * el identificador del chat, el nombre del usuario, el último mensaje enviado,
 * la fecha del último mensaje y la foto de perfil del usuario.
 */
public class Chat {

    // Identificador único del chat
    private int idChat;

    // Nombre del usuario asociado al chat
    private String nombreUsuario;

    // Último mensaje enviado o recibido en el chat
    private String ultimoMensaje;

    // Fecha del último mensaje enviado o recibido
    private String fechaUltimoMensaje;

    // URL de la foto de perfil del usuario asociado al chat
    private String fotoUsuario;

    /**
     * Constructor de la clase Chat.
     *
     * @param idChat Identificador único del chat.
     * @param nombreUsuario Nombre del usuario asociado al chat.
     * @param ultimoMensaje Último mensaje enviado o recibido en el chat.
     * @param fechaUltimoMensaje Fecha del último mensaje.
     * @param fotoUsuario URL de la foto de perfil del usuario.
     */
    public Chat(int idChat, String nombreUsuario, String ultimoMensaje, String fechaUltimoMensaje, String fotoUsuario) {
        this.idChat = idChat;
        this.nombreUsuario = nombreUsuario;
        this.ultimoMensaje = ultimoMensaje;
        this.fechaUltimoMensaje = fechaUltimoMensaje;
        this.fotoUsuario = fotoUsuario;
    }

    /**
     * Obtiene el identificador del chat.
     *
     * @return ID del chat.
     */
    public int getIdChat() {
        return idChat;
    }

    /**
     * Obtiene el nombre del usuario asociado al chat.
     *
     * @return Nombre del usuario.
     */
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    /**
     * Obtiene el último mensaje enviado o recibido en el chat.
     *
     * @return Último mensaje del chat.
     */
    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    /**
     * Obtiene la fecha del último mensaje enviado o recibido.
     *
     * @return Fecha del último mensaje.
     */
    public String getFechaUltimoMensaje() {
        return fechaUltimoMensaje;
    }

    /**
     * Obtiene la URL de la foto de perfil del usuario asociado al chat.
     *
     * @return URL de la foto del usuario.
     */
    public String getFotoUsuario() {
        return fotoUsuario;
    }
}
