package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FavoritosEntity {
    private SQLiteDatabase db;
    private static final String NOMBRE_TABLA = "TABLA_FAVORITOS";
    private static final String COL_ID_USUARIO = "id_usuario";
    private static final String COL_ID_VIVIENDA = "id_vivienda";

    public FavoritosEntity(SQLiteDatabase db){
        this.db = db;
    }

    public boolean insertar(int id_usuario, int id_vivienda){
        boolean toret = false;
        if (id_usuario <= 0 || id_vivienda <= 0) {
            Log.e("FavoritosEntity.insertar", "Parámetros inválidos para insertar.");
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(COL_ID_USUARIO, id_usuario);
        values.put(COL_ID_VIVIENDA, id_vivienda);


        try{
            db.beginTransaction();
            db.insert(NOMBRE_TABLA, null, values);
            db.setTransactionSuccessful();
            toret = true;
        }catch (SQLException exc){
            Log.e("FavoritosEntity.insertar", "insertando una tupla: " + exc.getMessage());
        }finally {
            db.endTransaction();
        }
        return toret;
    }

    public boolean eliminar(int id_usuario, int id_vivienda){
        boolean toret = false;

        try{
            db.beginTransaction();
            int filasEliminadas = db.delete(
                    NOMBRE_TABLA,
                    "id_usuario = ? AND id_vivienda = ?",
                    new String[]{String.valueOf(id_usuario),String.valueOf(id_vivienda)}
            );

            if(filasEliminadas == 0){
                Log.e("FavoritosEntity.eliminar", "Error al eliminar el favorito con id: " + id_usuario + id_vivienda);
            }else{
                db.setTransactionSuccessful();
                toret = true;
            }
        }catch (SQLException exc){
            Log.e("FavoritosEntity.eliminar", "Error en eliminar la tupla");
        }finally {
            db.endTransaction();
        }
        return toret;
    }

}
