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

    public ViviendaEntity(SQLiteDatabase db){
        this.db = db;
    }


    public boolean insertar(int id_vivienda, String tipoVivienda, double precio, String direccion, String estado, String contacto, String fotos, String descripcion, int propietarioId) {
        ContentValues values = new ContentValues();
        boolean toret = false;

        values.put("tipo_vivienda", tipoVivienda);
        values.put("precio", precio);
        values.put("direccion", direccion);
        values.put("estado", estado);
        values.put("contacto", contacto);
        values.put("fotos", fotos);
        values.put("descripcion", descripcion);
        values.put("propietario_id", propietarioId);

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
    public boolean modificarVivienda(int idVivienda, String nuevoTipo, Double nuevoPrecio, String nuevaDireccion, String nuevoEstado, String nuevoContacto, String nuevaDescripcion, String nuevaFoto) {
        ContentValues values = new ContentValues();
        boolean toret = false;

        // Solo agregamos al ContentValues los campos que no son null
        if (nuevoTipo != null) values.put("tipo_vivienda", nuevoTipo);
        if (nuevoPrecio != null) values.put("precio", nuevoPrecio);
        if (nuevaDireccion != null) values.put("direccion", nuevaDireccion);
        if (nuevoEstado != null) values.put("estado", nuevoEstado);
        if (nuevoContacto != null) values.put("contacto", nuevoContacto);
        if (nuevaFoto != null) values.put("fotos", nuevaFoto);
        if (nuevaDescripcion != null) values.put("descripcion", nuevaDescripcion);


        try {
            db.beginTransaction();

            // Actualizar el registro en la base de datos
            int filasAfectadas = db.update(
                    NOMBRE_TABLA,
                    values,
                    "id_vivienda = ?",
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
                "id_vivienda = ?",
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
                whereClause.append("precio BETWEEN ? AND ?");
                whereArgs.add(String.valueOf(precioMin));
                whereArgs.add(String.valueOf(precioMax));
            } else if (precioMin != null) {
                whereClause.append("precio >= ?");
                whereArgs.add(String.valueOf(precioMin));
            } else if (precioMax != null) {
                whereClause.append("precio <= ?");
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
