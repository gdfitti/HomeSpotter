package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.database.SQLException;
import android.database.Cursor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MensajesEntity {
    private static final String NOMBRE_TABLA = "TABLA_MENSAJES";
    private static final String COL_ID_MENSAJE = "id_mensaje";
    private static final String COL_REMITENTE_ID = "remitente_id";
    private static final String COL_DESTINATARIO_ID = "destinatario_id";
    private static final String COL_CONTENIDO = "contenido";
    private static final String COL_FECHA = "fecha";
    private static final String COL_LEIDO = "leido";

    private SQLiteDatabase db;

    public MensajesEntity(SQLiteDatabase db) {
        this.db = db;
    }

    // Método para insertar un mensaje
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

        } catch(SQLException exc) {
            Log.e("MensajesEntity.insertar", "Error SQL: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    // Método para obtener mensajes de un usuario
    public Cursor obtenerMensajes(int destinatarioId) {
        if (destinatarioId <= 0) {
            Log.e("MensajesEntity", "ID de destinatario inválido.");
            return null;
        }

        return db.query(
                NOMBRE_TABLA,
                null,
                COL_DESTINATARIO_ID + " = ?",
                new String[]{String.valueOf(destinatarioId)},
                null, null,
                COL_FECHA + " DESC" // Ordenar por fecha descendente
        );
    }

    // Método para marcar un mensaje como leído
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

    // Método para eliminar un mensaje
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
