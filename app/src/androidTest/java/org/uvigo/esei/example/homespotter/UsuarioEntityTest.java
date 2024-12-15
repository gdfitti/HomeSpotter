package org.uvigo.esei.example.homespotter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uvigo.esei.example.homespotter.database.UsuarioEntity;

import static org.junit.Assert.*;

public class UsuarioEntityTest {
    private SQLiteDatabase db;
    private UsuarioEntity usuarioEntity;

    @Before
    public void setUp() {
        // Crear un contexto de prueba y una base de datos en memoria
        Context context = ApplicationProvider.getApplicationContext();
        SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(context, null, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE TABLA_USUARIO (" +
                        "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre_usuario TEXT NOT NULL, " +
                        "email TEXT UNIQUE NOT NULL, " +
                        "password TEXT NOT NULL, " +
                        "foto_perfil TEXT, " +
                        "tlfno TEXT);");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS TABLA_USUARIO");
                onCreate(db);
            }
        };
        db = dbHelper.getWritableDatabase();
        usuarioEntity = new UsuarioEntity(db);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertarUsuario_Completado() {
        ContentValues values = usuarioEntity.getContentValues("juanpe", "Juan Pérez", "juan@example.com", "password123", "foto.jpg", "123456789");
        UsuarioEntity.insertUsuarioEstado resultado = usuarioEntity.insertar(values);
        assertEquals("El estado debería ser COMPLETADO.", UsuarioEntity.insertUsuarioEstado.COMPLETADO, resultado);

        Cursor cursor = db.query(
                "TABLA_USUARIO",
                null,
                "email = ?",
                new String[]{"juan@example.com"},
                null, null, null
        );
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("El nombre del usuario debería ser Juan Pérez.", "Juan Pérez", cursor.getString(cursor.getColumnIndex("nombre_usuario")));
        cursor.close();
    }

    @Test
    public void testInsertarUsuario_UsuarioExistente() {
        // Insertar el usuario por primera vez
        ContentValues values = usuarioEntity.getContentValues("juanpe","Juan Pérez", "juan@example.com", "password123", "foto.jpg", "123456789");
        usuarioEntity.insertar(values);

        // Intentar insertar nuevamente el mismo usuario
        UsuarioEntity.insertUsuarioEstado resultado = usuarioEntity.insertar(values);
        assertEquals("El estado debería ser USUARIO_EXISTENTE.", UsuarioEntity.insertUsuarioEstado.USUARIO_EXISTENTE, resultado);
    }

    @Test
    public void testInsertarUsuario_Error() {
        // Intentar insertar un usuario con un email nulo (simulación de error)
        ContentValues values = usuarioEntity.getContentValues("juanpe","Juan Pérez", null, "password123", "foto.jpg", "123456789");
        UsuarioEntity.insertUsuarioEstado resultado = usuarioEntity.insertar(values);
        assertEquals("El estado debería ser ERROR.", UsuarioEntity.insertUsuarioEstado.ERROR, resultado);
    }

    @Test
    public void testModificarUsuario() {
        ContentValues values = usuarioEntity.getContentValues("juanpe","Juan Pérez", "juan@example.com", "password123", "foto.jpg", "123456789");
        usuarioEntity.insertar(values);

        boolean actualizado = usuarioEntity.modificar(1, "juanpe","Juan Actualizado", "juan_new@example.com", null, null, null);
        assertTrue("El usuario debería haberse actualizado.", actualizado);

        Cursor cursor = db.query(
                "TABLA_USUARIO",
                null,
                "id_usuario = ?",
                new String[]{"1"},
                null, null, null
        );
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("El nombre del usuario debería haberse actualizado.", "Juan Actualizado", cursor.getString(cursor.getColumnIndex("nombre_usuario")));
        cursor.close();
    }

    @Test
    public void testEliminarUsuario() {
        ContentValues values = usuarioEntity.getContentValues("juanpe","Juan Pérez", "juan@example.com", "password123", "foto.jpg", "123456789");
        usuarioEntity.insertar(values);

        boolean eliminado = usuarioEntity.eliminar(1);
        assertTrue("El usuario debería haberse eliminado.", eliminado);

        Cursor cursor = db.query(
                "TABLA_USUARIO",
                null,
                "id_usuario = ?",
                new String[]{"1"},
                null, null, null
        );
        assertFalse("El cursor no debería contener registros.", cursor.moveToFirst());
        cursor.close();
    }
}
