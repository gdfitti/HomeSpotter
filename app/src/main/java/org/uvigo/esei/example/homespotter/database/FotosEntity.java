package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase FotosEntity
 *
 * Gestiona la tabla de fotos en la base de datos de la aplicación HomeSpotter.
 * Proporciona métodos para insertar, eliminar y consultar fotos asociadas a viviendas.
 */
public class FotosEntity {

    private SQLiteDatabase db;
    private static final String NOMBRE_TABLA = "TABLA_FOTOS";
    private static final String COL_ID_FOTO = "id_foto";
    private static final String COL_ID_VIVIENDA = "vivienda_id";
    private static final String COL_URL_FOTO = "url_foto";

    /**
     * Constructor de la clase FotosEntity.
     *
     * @param db Instancia de SQLiteDatabase para operar sobre la base de datos.
     */
    public FotosEntity(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta una nueva foto asociada a una vivienda en la base de datos.
     *
     * @param id_vivienda ID de la vivienda a la que pertenece la foto.
     * @param urlFoto     URL de la foto a insertar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(int id_vivienda, String urlFoto) {
        boolean toret = false;

        if (id_vivienda <= 0 || urlFoto == null || urlFoto.isEmpty()) {
            Log.e("FotosEntity", "Parámetros inválidos para insertar.");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COL_ID_VIVIENDA, id_vivienda);
        values.put(COL_URL_FOTO, urlFoto);

        try {
            db.beginTransaction();
            db.insert(NOMBRE_TABLA, null, values);
            db.setTransactionSuccessful();
            toret = true;
        } catch (SQLException exc) {
            Log.e("FotosEntity.insertar", "Error al insertar foto: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    /**
     * Elimina una foto específica de la base de datos.
     *
     * @param idFoto ID de la foto a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminar(int idFoto) {
        boolean toret = false;

        if (idFoto <= 0) {
            Log.e("FotosEntity", "ID de foto inválido para eliminar.");
            return false;
        }

        try {
            db.beginTransaction();
            int filasEliminadas = db.delete(
                    NOMBRE_TABLA,
                    COL_ID_FOTO + " = ?",
                    new String[]{String.valueOf(idFoto)}
            );

            if (filasEliminadas > 0) {
                db.setTransactionSuccessful();
                toret = true;
            } else {
                Log.e("FotosEntity.eliminar", "No se encontró la foto para eliminar: id_foto = " + idFoto);
            }
        } catch (SQLException exc) {
            Log.e("FotosEntity.eliminar", "Error al eliminar foto: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    /**
     * Obtiene un cursor con las fotos asociadas a una vivienda específica.
     *
     * @param viviendaId ID de la vivienda.
     * @return Cursor con los datos de las fotos o null si ocurre un error.
     */
    public Cursor obtenerFotosPorVivienda(int viviendaId) {
        if (viviendaId <= 0) {
            Log.e("FotosEntity", "ID de vivienda inválido para consultar fotos.");
            return null;
        }

        return db.query(
                NOMBRE_TABLA,
                null,
                COL_ID_VIVIENDA + " = ?",
                new String[]{String.valueOf(viviendaId)},
                null, null, null
        );
    }

    /**
     * Obtiene una lista de URLs de fotos asociadas a una vivienda específica.
     *
     * @param viviendaId ID de la vivienda.
     * @return Lista de URLs de fotos o null si ocurre un error.
     */
    public List<String> obtenerListaFotos(int viviendaId) {
        if (viviendaId <= 0) {
            Log.e("FotosEntity", "ID de vivienda inválido para consultar fotos.");
            return null;
        }

        List<String> fotos = new ArrayList<>();
        Cursor cursor = null;

        try {
            // Obtener el cursor con las fotos
            cursor = obtenerFotosPorVivienda(viviendaId);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Agregar cada URL de foto a la lista
                    fotos.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_URL_FOTO)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("FotosEntity", "Error al obtener lista de fotos: " + e.getMessage(), e);
        } finally {
            // Cerrar el cursor si no es nulo
            if (cursor != null) {
                cursor.close();
            }
        }

        return fotos;
    }
}
