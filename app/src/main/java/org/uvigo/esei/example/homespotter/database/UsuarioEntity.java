package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UsuarioEntity{
    private static SQLiteDatabase db;
    private static final String NOMBRE_TABLA = "TABLA_USUARIO";

    public enum insertUsuarioEstado{
        USUARIO_EXISTENTE,
        ERROR,
        COMPLETADO
    }

    public UsuarioEntity(SQLiteDatabase db){
        this.db = db;
    }

    public insertUsuarioEstado insertar(ContentValues values) {
        Cursor cursor = null;
        insertUsuarioEstado estado = null;

        try{
            db.beginTransaction();
            cursor = db.query(
                    NOMBRE_TABLA,
                    new String[]{"id_usuario"},
                    "email = ?",
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


    public boolean modificar(int id_usuario, String nuevoNombre, String nuevoEmail, String nuevaPassword, String nuevaFotoPerfil, String nuevoTelefono) {
        ContentValues values = new ContentValues();
        boolean toret = false;

        // Solo agregamos al ContentValues los campos que no son null
        if (nuevoNombre != null) values.put("nombre_usuario", nuevoNombre);
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
                        "email = ? AND id_usuario != ?", // Verifica que no sea el mismo usuario
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
                    "id_usuario = ?",
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


    public boolean eliminar(int id_usuario) {
        boolean toret = false;

        try {
            db.beginTransaction();

            // Intentar eliminar el usuario
            int filasEliminadas = db.delete(
                    "TABLA_USUARIO",        // Nombre de la tabla
                    "id_usuario = ?",       // Cláusula WHERE
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


    public Cursor buscarPorId(int id_usuario) {
        return db.query(
                NOMBRE_TABLA,
                null,
                "id_usuario = ?",
                new String[]{String.valueOf(id_usuario)},
                null,
                null,
                null
        );
    }

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

    public ContentValues getContentValues(String nombre, String email, String password, String fotoPerfil, String telefono){
        ContentValues values = new ContentValues();

        values.put("nombre_usuario", nombre);
        values.put("email", email);
        values.put("password", password);
        values.put("foto_perfil", fotoPerfil);
        values.put("tlfno", telefono);

        return values;
    }
}
