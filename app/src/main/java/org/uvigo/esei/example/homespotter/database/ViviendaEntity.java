package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ViviendaEntity {
    private static SQLiteDatabase db;
    private static final String NOMBRE_TABLA = "TABLA_VIVIENDA";
    private static final String COL_ID_VIVIENDA = "id_vivienda";
    private static final String COL_TIPO = "tipo_vivienda";
    private static final String COL_PRECIO = "precio";
    private static final String COL_DIREC = "direccion";
    private static final String COL_ESTADO = "estado";
    private static final String COL_CONTACTO = "contacto";
    private static final String COL_DESCR = "descripcion";
    private static final String COL_PROP_ID = "propietario_id";

    public ViviendaEntity(SQLiteDatabase db){
        this.db = db;
    }


    public boolean insertar(int id_vivienda, String tipoVivienda, double precio, String direccion, String estado, String contacto, String descripcion, int propietarioId) {
        ContentValues values = new ContentValues();
        boolean toret = false;

        values.put(COL_TIPO, tipoVivienda);
        values.put(COL_PRECIO, precio);
        values.put(COL_DIREC, direccion);
        values.put(COL_ESTADO, estado);
        values.put(COL_CONTACTO, contacto);
        values.put(COL_DESCR, descripcion);
        values.put(COL_PROP_ID, propietarioId);

        try{
            db.beginTransaction();

            db.insert(NOMBRE_TABLA, null, values);
            db.setTransactionSuccessful();
            toret = true;
        }catch(SQLException exc){
            Log.e("ViviendaEntity.insertarVivienda", "Error al insertar una vivienda: " + exc.getMessage());
        }finally {
            db.endTransaction();
        }

        return toret;
    }

    //modificar Vivienda
    public boolean modificarVivienda(int idVivienda, String nuevoTipo, Double nuevoPrecio, String nuevaDireccion, String nuevoEstado, String nuevoContacto, String nuevaDescripcion) {
        ContentValues values = new ContentValues();
        boolean toret = false;

        // Solo agregamos al ContentValues los campos que no son null
        if (nuevoTipo != null) values.put(COL_TIPO, nuevoTipo);
        if (nuevoPrecio != null) values.put(COL_PRECIO, nuevoPrecio);
        if (nuevaDireccion != null) values.put(COL_DIREC, nuevaDireccion);
        if (nuevoEstado != null) values.put(COL_ESTADO, nuevoEstado);
        if (nuevoContacto != null) values.put(COL_CONTACTO, nuevoContacto);
        if (nuevaDescripcion != null) values.put(COL_DESCR, nuevaDescripcion);


        try {
            db.beginTransaction();

            // Actualizar el registro en la base de datos
            int filasAfectadas = db.update(
                    NOMBRE_TABLA,
                    values,
                    COL_ID_VIVIENDA + " = ?",
                    new String[]{String.valueOf(idVivienda)}
            );

            if (filasAfectadas == 1) {
                Log.i("ViviendaEntity.modificarVivienda", "Vivienda actualizada con éxito.");
                db.setTransactionSuccessful();
                toret = true;
            } else {
                Log.i("ViviendaEntity.modificarVivienda", "No se encontró la vivienda con id: " + idVivienda);
            }
        } catch (SQLException exc) {
            Log.e("ViviendaEntity.modificarVivienda", "Error al actualizar vivienda: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    public boolean eliminar(int id_vivienda) {
        boolean toret = false;

        try {
            db.beginTransaction();

            // Intentar eliminar el usuario
            int filasEliminadas = db.delete(
                    NOMBRE_TABLA,
                    "id_vivienda = ?",
                    new String[]{String.valueOf(id_vivienda)}
            );

            if (filasEliminadas > 0) {
                Log.i("ViviendaEntity.eliminarVivienda", "Viviebda con ID " + id_vivienda + " eliminado con éxito.");
                db.setTransactionSuccessful();
                toret = true;
            } else {
                Log.i("ViviendaEntity.eliminarVivienda", "No se encontró la Vivienda con ID: " + id_vivienda);
            }
        } catch (SQLException exc) {
            Log.e("ViviendaEntity.eliminarVivienda", "Error al eliminar Vivienda: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    public Cursor buscarPorId(int id_vivienda) {
        return db.query(
                NOMBRE_TABLA,
                null,
                COL_ID_VIVIENDA + " = ?",
                new String[]{String.valueOf(id_vivienda)},
                null,
                null,
                null
        );
    }

    public Cursor buscar(ContentValues filtros, Double precioMin, Double precioMax) {
        StringBuilder whereClause = new StringBuilder();
        List<String> whereArgs = new ArrayList<>();

        // Construir WHERE dinámico con ContentValues
        if (filtros != null && filtros.size() > 0) {
            for (String key : filtros.keySet()) {
                if (whereClause.length() > 0) {
                    whereClause.append(" AND ");
                }
                whereClause.append(key).append(" = ?");
                whereArgs.add(filtros.getAsString(key));
            }
        }

        // Agregar filtro de precio si se especifica
        if (precioMin != null || precioMax != null) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }

            if (precioMin != null && precioMax != null) {
                whereClause.append(COL_PRECIO + " BETWEEN ? AND ?");
                whereArgs.add(String.valueOf(precioMin));
                whereArgs.add(String.valueOf(precioMax));
            } else if (precioMin != null) {
                whereClause.append(COL_PRECIO + " >= ?");
                whereArgs.add(String.valueOf(precioMin));
            } else if (precioMax != null) {
                whereClause.append(COL_PRECIO + " <= ?");
                whereArgs.add(String.valueOf(precioMax));
            }
        }

        // Realizar la consulta
        return db.query(
                NOMBRE_TABLA,
                null, // Seleccionar todas las columnas
                whereClause.length() > 0 ? whereClause.toString() : null, // WHERE
                whereArgs.isEmpty() ? null : whereArgs.toArray(new String[0]), // WHERE args
                null, // groupBy
                null, // having
                null  // orderBy
        );
    }
}
