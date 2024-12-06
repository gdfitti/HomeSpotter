package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FotosEntity {
    private SQLiteDatabase db;
    private static final String NOMBRE_TABLA = "TABLA_FOTOS";
    private static final String COL_ID_FOTO = "id_foto";
    private static final String COL_ID_VIVIENDA = "vivienda_id";
    private static final String COL_URL_FOTO = "url_foto";

    public FotosEntity (SQLiteDatabase db){
        this.db = db;
    }

    public boolean insertar (int id_vivienda, String urlFoto){
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
        }catch (SQLException exc){
            Log.e("FotosEntity.insertar", "Error al insertar foto: " + exc.getMessage());
        }finally {
            db.endTransaction();
        }
        return toret;
    }

    public boolean eliminar (int idFoto){
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
}
