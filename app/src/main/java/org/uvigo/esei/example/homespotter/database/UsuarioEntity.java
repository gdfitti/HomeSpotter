package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase UsuarioEntity
 *
 * Esta clase gestiona la interacción con la tabla "TABLA_USUARIO" de la base de datos.
 * Proporciona métodos para insertar, modificar, eliminar y buscar usuarios en la base de datos.
 *
 * Estructura de la tabla "TABLA_USUARIO":
 * - id_usuario: ID único del usuario (clave primaria).
 * - email: Correo electrónico del usuario (único y obligatorio).
 * - nombre_usuario: Nombre del usuario (obligatorio).
 * - foto_perfil: URL de la foto de perfil del usuario.
 * - password: Contraseña del usuario (obligatorio).
 * - tlfno: Teléfono del usuario.
 *
 * Métodos principales:
 * - insertar(ContentValues values): Inserta un nuevo usuario en la base de datos.
 * - modificar(int id_usuario, ...): Modifica los datos de un usuario existente.
 * - eliminar(int id_usuario): Elimina un usuario de la base de datos.
 * - buscarPorId(int id_usuario): Busca un usuario por su ID.
 * - buscar(ContentValues filtros): Realiza una búsqueda dinámica basada en filtros.
 * - getContentValues(...): Construye un objeto ContentValues con los datos del usuario.
 */
public class UsuarioEntity{
    private static SQLiteDatabase db;
    private static final String NOMBRE_TABLA = "TABLA_USUARIO";
    private static final String COL_ID_USUARIO = "id_usuario";
    private static final String COL_EMAIL = "email";
    private static final String COL_NOMBRE_USUARIO = "nombre_usuario";
    private static final String COL_NOMBRE_COMPLETO = "nombre_completo";
    private static final String COL_FOTO = "foto_perfil";
    private static final String COL_PASSWRD = "password";

    public enum insertUsuarioEstado{
        USUARIO_EXISTENTE,
        ERROR,
        COMPLETADO
    }

    /**
     * Constructor de la clase.
     *
     * @param db Instancia de SQLiteDatabase para interactuar con la base de datos.
     */
    public UsuarioEntity(SQLiteDatabase db){
        this.db = db;
    }

    /**
     * Inserta un nuevo usuario en la tabla "TABLA_USUARIO".
     *
     * @param values Objeto ContentValues con los datos del usuario.
     * @return Estado de la inserción (`USUARIO_EXISTENTE`, `ERROR`, `COMPLETADO`).
     */
    public insertUsuarioEstado insertar(ContentValues values) {
        if (values.getAsString(COL_NOMBRE_USUARIO) == null || values.getAsString(COL_EMAIL) == null
                || values.getAsString(COL_PASSWRD) == null || values.getAsString(COL_NOMBRE_COMPLETO) == null) {
            Log.e("UsuarioEntity", "Los campos obligatorios no pueden ser nulos.");
            return insertUsuarioEstado.ERROR; // Devuelve ERROR si algún campo obligatorio es nulo
        }
        Cursor cursor = null;
        insertUsuarioEstado estado = null;

        try{
            db.beginTransaction();
            cursor = db.query(
                    NOMBRE_TABLA,
                    new String[]{COL_ID_USUARIO},
                    COL_EMAIL + " = ?",
                    new String[]{values.getAsString("email")},
                    null,
                    null,
                    null
            );

            if(cursor.getCount() > 0){
                Log.e("UsuarioEntity.instertarUsuario", "El usuario ya está registrado");
                estado = insertUsuarioEstado.USUARIO_EXISTENTE;
            }else{
                db.insert(NOMBRE_TABLA, null, values);
                db.setTransactionSuccessful();
                estado = insertUsuarioEstado.COMPLETADO;
            }

        }catch(SQLException exc){
            Log.e("UsuarioEntity.insertarUsuario", "al insertar los datos");
            estado = insertUsuarioEstado.ERROR;
        }finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
            db.endTransaction();
        }

        return estado;
    }

    /**
     * Modifica los datos de un usuario existente.
     *
     * @param id_usuario ID del usuario a modificar.
     * @param nuevoNombreUsuario Nuevo nombre del usuario.
     * @param nuevoNombreCompleto Nuevo nombre completo.
     * @param nuevoEmail Nuevo correo electrónico.
     * @param nuevaPassword Nueva contraseña.
     * @param nuevaFotoPerfil Nueva URL de la foto de perfil.
     * @param nuevoTelefono Nuevo número de teléfono.
     * @return `true` si la modificación fue exitosa, `false` en caso contrario.
     */
    public boolean modificar(int id_usuario, String nuevoNombreUsuario, String nuevoNombreCompleto, String nuevoEmail, String nuevaPassword, String nuevaFotoPerfil, String nuevoTelefono) {
        ContentValues values = new ContentValues();
        boolean toret = false;

        // Solo agregamos al ContentValues los campos que no son null
        if (nuevoNombreUsuario != null) values.put("nombre_usuario", nuevoNombreUsuario);
        if (nuevoNombreCompleto != null) values.put("nombre_completo", nuevoNombreCompleto);
        if (nuevoEmail != null) values.put("email", nuevoEmail);
        if (nuevaPassword != null) values.put("password", nuevaPassword);
        if (nuevaFotoPerfil != null) values.put("foto_perfil", nuevaFotoPerfil);
        if (nuevoTelefono != null) values.put("tlfno", nuevoTelefono);

        try {
            db.beginTransaction();
            // Actualizar el registro en la base de datos
            if (nuevoEmail != null) {
                Cursor cursor = db.query(
                        NOMBRE_TABLA,
                        new String[]{"id_usuario"},
                        COL_EMAIL + " = ? AND "+COL_ID_USUARIO+" != ?", // Verifica que no sea el mismo usuario
                        new String[]{nuevoEmail, String.valueOf(id_usuario)},
                        null,
                        null,
                        null
                );
                if (cursor.getCount() > 0) {
                    Log.e("UsuarioEntity.modificarUsuario", "El nuevo email ya está registrado por otro usuario.");
                }
                if(cursor != null && !cursor.isClosed()){
                    cursor.close();
                }

            }
            // Actualizar el registro en la base de datos
            int filasAfectadas = db.update(
                    "TABLA_USUARIO",
                    values,
                    COL_ID_USUARIO + " = ?",
                    new String[]{String.valueOf(id_usuario)}
            );

            if (filasAfectadas == 1) {
                Log.i("UsuarioEntity.modificarUsuario", "Usuario actualizado con éxito.");
                db.setTransactionSuccessful();
                toret = true;
            } else {
                Log.i("UsuarioEntity.modificarUsuario", "No se encontró el usuario con id: " + id_usuario);
            }
        } catch (SQLException exc) {
            Log.e("UsuarioEntity.modificarUsuario", "Error al actualizar usuario: " + exc.getMessage());

        }finally {
            db.endTransaction();
        }

        return toret;
    }

    /**
     * Elimina un usuario de la tabla "TABLA_USUARIO".
     *
     * @param id_usuario ID del usuario a eliminar.
     * @return `true` si la eliminación fue exitosa, `false` en caso contrario.
     */
    public boolean eliminar(int id_usuario) {
        boolean toret = false;

        try {
            db.beginTransaction();

            // Intentar eliminar el usuario
            int filasEliminadas = db.delete(
                    "TABLA_USUARIO",        // Nombre de la tabla
                    COL_ID_USUARIO + " = ?",       // Cláusula WHERE
                    new String[]{String.valueOf(id_usuario)} // Argumentos para el WHERE
            );

            if (filasEliminadas > 0) {
                Log.i("UsuarioEntity.eliminarUsuario", "Usuario con ID " + id_usuario + " eliminado con éxito.");
                db.setTransactionSuccessful();
                toret = true;
            } else {
                Log.i("UsuarioEntity.eliminarUsuario", "No se encontró el usuario con ID: " + id_usuario);
            }
        } catch (SQLException exc) {
            Log.e("UsuarioEntity.eliminarUsuario", "Error al eliminar usuario: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }

        return toret;
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id_usuario ID del usuario a buscar.
     * @return Cursor con el resultado de la búsqueda.
     */
    public Cursor buscarPorId(int id_usuario) {
        return db.query(
                NOMBRE_TABLA,
                null,
                COL_ID_USUARIO + " = ?",
                new String[]{String.valueOf(id_usuario)},
                null,
                null,
                null
        );
    }

    /**
     * Realiza una búsqueda dinámica de usuarios basándose en filtros.
     *
     * @param filtros Objeto ContentValues con los criterios de búsqueda.
     * @return Cursor con el resultado de la búsqueda.
     */
    public Cursor buscar(ContentValues filtros) {
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

    public int getUltimoUsuarioId(){
        int ultimoId = -1;
        Cursor cursor = null;

        try {
            // Consulta para obtener el último ID de vivienda insertado
            cursor = db.rawQuery("SELECT MAX(id_usuario) AS ultimo_id FROM TABLA_USUARIO", null);
            if (cursor != null && cursor.moveToFirst()) {
                ultimoId = cursor.getInt(cursor.getColumnIndexOrThrow("ultimo_id"));
            }
        } catch (Exception e) {
            Log.e("UsuarioEntity", "Error al obtener el último ID de usuario: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return ultimoId;
    }

    /**
     * Construye un objeto ContentValues con los datos de un usuario.
     *
     * @param nombre Nombre del usuario.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @param fotoPerfil URL de la foto de perfil.
     * @param telefono Número de teléfono.
     * @return Objeto ContentValues con los datos del usuario.
     */
    public ContentValues getContentValues(String nombre, String nombreCompleto, String email, String password, String fotoPerfil, String telefono){
        ContentValues values = new ContentValues();

        values.put("nombre_usuario", nombre);
        values.put("nombre_completo", nombreCompleto);
        values.put("email", email);
        values.put("password", password);
        values.put("foto_perfil", fotoPerfil);
        values.put("tlfno", telefono);

        return values;
    }
}
