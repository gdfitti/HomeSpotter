package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase ViviendaEntity
 *
 * Esta clase gestiona la interacción con la tabla "TABLA_VIVIENDA" de la base de datos.
 * Proporciona métodos para insertar, modificar, eliminar y buscar viviendas.
 *
 * Estructura de la tabla "TABLA_VIVIENDA":
 * - id_vivienda: ID único de la vivienda (clave primaria).
 * - tipo_vivienda: Tipo de vivienda (casa, apartamento, etc.).
 * - precio: Precio de la vivienda.
 * - direccion: Dirección de la vivienda.
 * - estado: Estado de la vivienda (disponible, ocupado, etc.).
 * - contacto: Información de contacto.
 * - descripcion: Descripción de la vivienda.
 * - propietario_id: ID del propietario de la vivienda (clave foránea).
 *
 * Métodos principales:
 * - insertar(...): Inserta una nueva vivienda en la base de datos.
 * - modificarVivienda(...): Modifica los datos de una vivienda existente.
 * - eliminar(int id_vivienda): Elimina una vivienda de la base de datos.
 * - buscarPorId(int id_vivienda): Busca una vivienda por su ID.
 * - buscar(ContentValues filtros, Double precioMin, Double precioMax): Busca viviendas con filtros dinámicos y rango de precios.
 */
public class ViviendaEntity {
    private static SQLiteDatabase db;
    private static final String NOMBRE_TABLA = "TABLA_VIVIENDA";
    private static final String COL_ID_VIVIENDA = "id_vivienda";
    private static final String COL_TITULO = "titulo";
    private static final String COL_TIPO = "tipo_vivienda";
    private static final String COL_PRECIO = "precio";
    private static final String COL_DIREC = "direccion";
    private static final String COL_ESTADO = "estado";
    private static final String COL_CONTACTO = "contacto";
    private static final String COL_DESCR = "descripcion";
    private static final String COL_PROP_ID = "propietario_id";

    /**
     * Constructor de la clase.
     *
     * @param db Instancia de SQLiteDatabase para interactuar con la base de datos.
     */
    public ViviendaEntity(SQLiteDatabase db){
        this.db = db;
    }

    /**
     * Inserta una nueva vivienda en la tabla "TABLA_VIVIENDA".
     *
     * @param id_vivienda ID de la vivienda (opcional para autoincremento).
     * @param tipoVivienda Tipo de la vivienda.
     * @param precio Precio de la vivienda.
     * @param direccion Dirección de la vivienda.
     * @param estado Estado de la vivienda.
     * @param contacto Información de contacto.
     * @param descripcion Descripción de la vivienda.
     * @param propietarioId ID del propietario de la vivienda.
     * @return `true` si la inserción fue exitosa, `false` en caso contrario.
     */
    public boolean insertar(String titulo, String tipoVivienda, double precio, String direccion, String estado, String contacto, String descripcion, int propietarioId) {
        ContentValues values = new ContentValues();
        boolean toret = false;

        values.put(COL_TITULO, titulo);
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

    /**
     * Modifica los datos de una vivienda existente.
     *
     * @param idVivienda ID de la vivienda a modificar.
     * @param nuevoTipo Nuevo tipo de la vivienda.
     * @param nuevoPrecio Nuevo precio de la vivienda.
     * @param nuevaDireccion Nueva dirección de la vivienda.
     * @param nuevoEstado Nuevo estado de la vivienda.
     * @param nuevoContacto Nuevo contacto de la vivienda.
     * @param nuevaDescripcion Nueva descripción de la vivienda.
     * @return `true` si la modificación fue exitosa, `false` en caso contrario.
     */
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

    /**
     * Elimina una vivienda de la tabla "TABLA_VIVIENDA".
     *
     * @param id_vivienda ID de la vivienda a eliminar.
     * @return `true` si la eliminación fue exitosa, `false` en caso contrario.
     */
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

    /**
     * Busca una vivienda por su ID.
     *
     * @param id_vivienda ID de la vivienda a buscar.
     * @return Cursor con los datos de la vivienda encontrada.
     */
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

    /**
     * Realiza una búsqueda dinámica de viviendas basándose en filtros y rango de precios.
     *
     * @param filtros Filtros dinámicos para la búsqueda (tipo, estado, etc.).
     * @param precioMin Precio mínimo para la búsqueda.
     * @param precioMax Precio máximo para la búsqueda.
     * @return Cursor con los resultados de la búsqueda.
     */
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

    public Cursor buscarPorPropietario(int idPropietario) {
        String query = "SELECT * FROM TABLA_VIVIENDA WHERE propietario_id = ?";
        String[] selectionArgs = new String[]{String.valueOf(idPropietario)};
        return db.rawQuery(query, selectionArgs);
    }

    public int obtenerUltimaVivienda(){
        int ultimoId = -1;
        Cursor cursor = null;

        try {
            // Consulta para obtener el último ID de vivienda insertado
            cursor = db.rawQuery("SELECT MAX(id_vivienda) AS ultimo_id FROM TABLA_VIVIENDA", null);
            if (cursor != null && cursor.moveToFirst()) {
                ultimoId = cursor.getInt(cursor.getColumnIndexOrThrow("ultimo_id"));
            }
        } catch (Exception e) {
            Log.e("ViviendaEntity", "Error al obtener el último ID de vivienda: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return ultimoId;
    }


}
