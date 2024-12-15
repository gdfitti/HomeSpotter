package org.uvigo.esei.example.homespotter.models;

/**
 * Clase que representa un mensaje en la aplicación HomeSpotter.
 * Los mensajes pueden ser enviados entre usuarios, conteniendo información
 * como remitente, destinatario, contenido, fecha y estado de lectura.
 */
public class Mensaje {

    private int idMensaje; // ID único del mensaje.
    private int remitenteId; // ID del usuario remitente del mensaje.
    private int destinatarioId; // ID del usuario destinatario del mensaje.
    private String contenido; // Contenido del mensaje.
    private String fecha; // Fecha de envío del mensaje.
    private boolean leido; // Indica si el mensaje ha sido leído por el destinatario.

    /**
     * Constructor de la clase Mensaje.
     *
     * @param idMensaje      ID único del mensaje.
     * @param remitenteId    ID del remitente del mensaje.
     * @param destinatarioId ID del destinatario del mensaje.
     * @param contenido      Contenido del mensaje.
     * @param fecha          Fecha de envío del mensaje.
     * @param leido          Estado de lectura del mensaje (true si ha sido leído, false en caso contrario).
     */
    public Mensaje(int idMensaje, int remitenteId, int destinatarioId, String contenido, String fecha, boolean leido) {
        this.idMensaje = idMensaje;
        this.remitenteId = remitenteId;
        this.destinatarioId = destinatarioId;
        this.contenido = contenido;
        this.fecha = fecha;
        this.leido = leido;
    }

    /**
     * Obtiene el ID del mensaje.
     *
     * @return ID del mensaje.
     */
    public int getIdMensaje() {
        return idMensaje;
    }

    /**
     * Establece el ID del mensaje.
     *
     * @param idMensaje ID del mensaje.
     */
    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    /**
     * Obtiene el ID del remitente del mensaje.
     *
     * @return ID del remitente.
     */
    public int getRemitenteId() {
        return remitenteId;
    }

    /**
     * Establece el ID del remitente del mensaje.
     *
     * @param remitenteId ID del remitente.
     */
    public void setRemitenteId(int remitenteId) {
        this.remitenteId = remitenteId;
    }

    /**
     * Obtiene el ID del destinatario del mensaje.
     *
     * @return ID del destinatario.
     */
    public int getDestinatarioId() {
        return destinatarioId;
    }

    /**
     * Establece el ID del destinatario del mensaje.
     *
     * @param destinatarioId ID del destinatario.
     */
    public void setDestinatarioId(int destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    /**
     * Obtiene el contenido del mensaje.
     *
     * @return Contenido del mensaje.
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Establece el contenido del mensaje.
     *
     * @param contenido Contenido del mensaje.
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    /**
     * Obtiene la fecha de envío del mensaje.
     *
     * @return Fecha del mensaje.
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha de envío del mensaje.
     *
     * @param fecha Fecha del mensaje.
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Indica si el mensaje ha sido leído.
     *
     * @return true si el mensaje ha sido leído, false en caso contrario.
     */
    public boolean isLeido() {
        return leido;
    }

    /**
     * Establece el estado de lectura del mensaje.
     *
     * @param leido true si el mensaje ha sido leído, false en caso contrario.
     */
    public void setLeido(boolean leido) {
        this.leido = leido;
    }
}
