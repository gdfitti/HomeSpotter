package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase MensajesEntity
 *
 * Proporciona métodos para gestionar los mensajes almacenados en la tabla "TABLA_MENSAJES".
 * Incluye operaciones de inserción, consulta, actualización y eliminación.
 */
public class MensajesEntity {
    // Nombre de la tabla y columnas
    private static final String NOMBRE_TABLA = "TABLA_MENSAJES";
    private static final String COL_ID_MENSAJE = "id_mensaje";
    private static final String COL_REMITENTE_ID = "remitente_id";
    private static final String COL_DESTINATARIO_ID = "destinatario_id";
    private static final String COL_CONTENIDO = "contenido";
    private static final String COL_FECHA = "fecha";
    private static final String COL_LEIDO = "leido";

    private SQLiteDatabase db;

    /**
     * Constructor de la clase.
     *
     * @param db Instancia de la base de datos SQLite.
     */
    public MensajesEntity(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta un mensaje en la base de datos.
     *
     * @param remitenteId ID del remitente.
     * @param destinatarioId ID del destinatario.
     * @param contenido Contenido del mensaje.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(int remitenteId, int destinatarioId, String contenido) {
        if (remitenteId <= 0 || destinatarioId <= 0 || contenido == null || contenido.isEmpty()) {
            Log.e("MensajesEntity", "Parámetros inválidos para insertar mensaje.");
            return false;
        }

        boolean toret = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaActual = LocalDateTime.now().format(formatter);

        ContentValues values = new ContentValues();
        values.put(COL_REMITENTE_ID, remitenteId);
        values.put(COL_DESTINATARIO_ID, destinatarioId);
        values.put(COL_CONTENIDO, contenido);
        values.put(COL_FECHA, fechaActual);
        values.put(COL_LEIDO, 0); // Por defecto, el mensaje no está leído

        try {
            db.beginTransaction();
            db.insert(NOMBRE_TABLA, null, values);
            db.setTransactionSuccessful();
            toret = true;
        } catch (SQLException exc) {
            Log.e("MensajesEntity.insertar", "Error SQL: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    /**
     * Obtiene todos los mensajes relacionados con un usuario.
     *
     * @param usuarioId ID del usuario.
     * @return Cursor con los mensajes encontrados.
     */
    public Cursor obtenerMensajes(int usuarioId) {
        if (usuarioId <= 0) {
            Log.e("MensajesEntity", "ID de usuario inválido.");
            return null;
        }

        return db.query(
                NOMBRE_TABLA,
                null,
                COL_DESTINATARIO_ID + " = ? OR " + COL_REMITENTE_ID + " = ?",
                new String[]{String.valueOf(usuarioId), String.valueOf(usuarioId)},
                null, null,
                COL_FECHA + " DESC" // Ordenar por fecha descendente
        );
    }

    /**
     * Obtiene el último mensaje entre un remitente y un destinatario.
     *
     * @param remitenteId ID del remitente.
     * @param destinatarioId ID del destinatario.
     * @return Cursor con el último mensaje encontrado.
     */
    public Cursor obtenerUltimoMensaje(int remitenteId, int destinatarioId) {
        if (remitenteId <= 0 || destinatarioId <= 0) {
            Log.e("MensajesEntity", "ID de remitente o destinatario inválidos.");
            return null;
        }

        return db.query(
                NOMBRE_TABLA,
                new String[]{"MAX(" + COL_FECHA + ") AS ultima_fecha", COL_CONTENIDO + " AS contenido"},
                COL_REMITENTE_ID + " = ? AND " + COL_DESTINATARIO_ID + " = ?",
                new String[]{String.valueOf(remitenteId), String.valueOf(destinatarioId)},
                null, null,
                COL_FECHA + " DESC",
                "1" // Limitar a un único resultado
        );
    }

    /**
     * Obtiene todos los mensajes entre dos usuarios.
     *
     * @param remitenteId ID del remitente.
     * @param destinatarioId ID del destinatario.
     * @return Cursor con los mensajes encontrados.
     */
    public Cursor obtenerMensajesEntreUsuarios(int remitenteId, int destinatarioId) {
        return db.query(
                NOMBRE_TABLA,
                new String[]{"id_mensaje", "remitente_id", "destinatario_id", "contenido", "fecha", "leido"},
                "(remitente_id = ? AND destinatario_id = ?) OR (remitente_id = ? AND destinatario_id = ?)",
                new String[]{
                        String.valueOf(remitenteId), String.valueOf(destinatarioId),
                        String.valueOf(destinatarioId), String.valueOf(remitenteId)
                },
                null, null,
                "fecha ASC"
        );
    }

    /**
     * Marca un mensaje como leído.
     *
     * @param mensajeId ID del mensaje.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean marcarComoLeido(int mensajeId) {
        if (mensajeId <= 0) {
            Log.e("MensajesEntity", "ID de mensaje inválido para marcar como leído.");
            return false;
        }

        boolean toret = false;
        ContentValues values = new ContentValues();
        values.put(COL_LEIDO, 1);

        try {
            db.beginTransaction();
            int filasActualizadas = db.update(
                    NOMBRE_TABLA,
                    values,
                    COL_ID_MENSAJE + " = ?",
                    new String[]{String.valueOf(mensajeId)}
            );

            if (filasActualizadas > 0) {
                db.setTransactionSuccessful();
                toret = true;
            } else {
                Log.e("MensajesEntity.marcarComoLeido", "No se encontró el mensaje para actualizar.");
            }
        } catch (SQLException exc) {
            Log.e("MensajesEntity.marcarComoLeido", "Error SQL: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    /**
     * Elimina un mensaje por su ID.
     *
     * @param mensajeId ID del mensaje.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminar(int mensajeId) {
        if (mensajeId <= 0) {
            Log.e("MensajesEntity", "ID de mensaje inválido para eliminar.");
            return false;
        }

        boolean toret = false;
        try {
            db.beginTransaction();
            int filasEliminadas = db.delete(
                    NOMBRE_TABLA,
                    COL_ID_MENSAJE + " = ?",
                    new String[]{String.valueOf(mensajeId)}
            );

            if (filasEliminadas > 0) {
                db.setTransactionSuccessful();
                toret = true;
            } else {
                Log.e("MensajesEntity.eliminar", "No se encontró el mensaje para eliminar.");
            }
        } catch (SQLException exc) {
            Log.e("MensajesEntity.eliminar", "Error SQL: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }
}
