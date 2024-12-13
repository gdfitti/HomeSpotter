package org.uvigo.esei.example.homespotter.models;

public class Mensaje {
    private int idMensaje;
    private int remitenteId;
    private int destinatarioId;
    private String contenido;
    private String fecha;
    private boolean leido;

    // Constructor
    public Mensaje(int idMensaje, int remitenteId, int destinatarioId, String contenido, String fecha, boolean leido) {
        this.idMensaje = idMensaje;
        this.remitenteId = remitenteId;
        this.destinatarioId = destinatarioId;
        this.contenido = contenido;
        this.fecha = fecha;
        this.leido = leido;
    }

    // Getters y setters
    public int getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    public int getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(int remitenteId) {
        this.remitenteId = remitenteId;
    }

    public int getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(int destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }
}
