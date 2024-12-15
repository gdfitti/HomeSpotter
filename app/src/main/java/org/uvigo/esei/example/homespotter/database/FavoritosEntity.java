package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase FavoritosEntity
 *
 * Gestiona las operaciones CRUD relacionadas con la tabla de favoritos
 * en la base de datos. Permite agregar, eliminar y obtener los favoritos
 * de los usuarios.
 */
public class FavoritosEntity {
    private SQLiteDatabase db;

    // Nombre de la tabla y columnas
    private static final String NOMBRE_TABLA = "TABLA_FAVORITOS";
    private static final String COL_ID_USUARIO = "usuario_id";
    private static final String COL_ID_VIVIENDA = "vivienda_id";

    /**
     * Constructor de la clase.
     *
     * @param db Base de datos SQLite para realizar las operaciones.
     */
    public FavoritosEntity(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * Inserta un nuevo favorito en la tabla de favoritos.
     *
     * @param usuarioId ID del usuario.
     * @param viviendaId ID de la vivienda.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(int usuarioId, int viviendaId) {
        boolean toret = false;

        if (usuarioId <= 0 || viviendaId <= 0) {
            Log.e("FavoritosEntity.insertar", "Parámetros inválidos para insertar.");
            return false;
        }

        // Verificar si ya existe el favorito
        Cursor cursor = db.query(
                NOMBRE_TABLA,
                new String[]{COL_ID_USUARIO, COL_ID_VIVIENDA},
                COL_ID_USUARIO + " = ? AND " + COL_ID_VIVIENDA + " = ?",
                new String[]{String.valueOf(usuarioId), String.valueOf(viviendaId)},
                null, null, null
        );

        if (cursor != null && cursor.getCount() > 0) {
            Log.i("FavoritosEntity.insertar", "El registro ya existe: usuario_id = " + usuarioId + ", vivienda_id = " + viviendaId);
            cursor.close();
            return false;
        }

        if (cursor != null) {
            cursor.close();
        }

        // Insertar el favorito si no existe
        ContentValues values = new ContentValues();
        values.put(COL_ID_USUARIO, usuarioId);
        values.put(COL_ID_VIVIENDA, viviendaId);

        try {
            db.beginTransaction();
            db.insert(NOMBRE_TABLA, null, values);
            db.setTransactionSuccessful();
            toret = true;
        } catch (SQLException exc) {
            Log.e("FavoritosEntity.insertar", "Error al insertar: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    /**
     * Elimina un favorito de la tabla.
     *
     * @param id_usuario ID del usuario.
     * @param id_vivienda ID de la vivienda.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminar(int id_usuario, int id_vivienda) {
        boolean toret = false;

        try {
            db.beginTransaction();
            int filasEliminadas = db.delete(
                    NOMBRE_TABLA,
                    COL_ID_USUARIO + " = ? AND " + COL_ID_VIVIENDA + " = ?",
                    new String[]{String.valueOf(id_usuario), String.valueOf(id_vivienda)}
            );

            if (filasEliminadas == 0) {
                Log.e("FavoritosEntity.eliminar", "Error al eliminar el favorito con id: " + id_usuario + ", " + id_vivienda);
            } else {
                db.setTransactionSuccessful();
                toret = true;
            }
        } catch (SQLException exc) {
            Log.e("FavoritosEntity.eliminar", "Error en eliminar la tupla: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }
        return toret;
    }

    /**
     * Obtiene una lista de IDs de viviendas favoritas para un usuario específico.
     *
     * @param id_usuario ID del usuario.
     * @return Lista de IDs de viviendas favoritas del usuario.
     */
    public List<Integer> obtenerFavoritosPorUsuario(int id_usuario) {
        List<Integer> listaFavoritos = new ArrayList<>();

        if (id_usuario <= 0) {
            Log.e("FavoritosEntity.obtenerFavoritosPorUsuario", "ID de usuario inválido.");
            return listaFavoritos;
        }

        Cursor cursor = null;
        try {
            cursor = db.query(
                    NOMBRE_TABLA,                 // Tabla
                    new String[]{COL_ID_VIVIENDA}, // Columnas a seleccionar
                    COL_ID_USUARIO + " = ?",      // Condición WHERE
                    new String[]{String.valueOf(id_usuario)}, // Valores de la condición
                    null,                         // GROUP BY
                    null,                         // HAVING
                    null                          // ORDER BY
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id_vivienda = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_VIVIENDA));
                    listaFavoritos.add(id_vivienda);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("FavoritosEntity.obtenerFavoritosPorUsuario", "Error al obtener favoritos: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return listaFavoritos;
    }
}
