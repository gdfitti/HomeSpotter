package org.uvigo.esei.example.homespotter;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uvigo.esei.example.homespotter.database.MensajesEntity;

import static org.junit.Assert.*;

public class MessageEntityTest {
    private SQLiteDatabase db;
    private MensajesEntity mensajesEntity;

    @Before
    public void setUp() {
        // Crear un contexto de prueba
        Context context = ApplicationProvider.getApplicationContext();

        // Crear una base de datos en memoria para pruebas
        SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(context, null, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE TABLA_MENSAJES (" +
                        "id_mensaje INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "remitente_id INTEGER NOT NULL, " +
                        "destinatario_id INTEGER NOT NULL, " +
                        "contenido TEXT NOT NULL, " +
                        "fecha TEXT NOT NULL, " +
                        "leido INTEGER NOT NULL DEFAULT 0);");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS TABLA_MENSAJES;");
                onCreate(db);
            }
        };

        db = dbHelper.getWritableDatabase();
        mensajesEntity = new MensajesEntity(db);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertarMensaje() {
        boolean resultado = mensajesEntity.insertar(1, 2, "Hola, este es un mensaje de prueba.");
        assertTrue("El mensaje debería haberse insertado correctamente.", resultado);

        Cursor cursor = db.query(
                "TABLA_MENSAJES",
                null,
                "remitente_id = ? AND destinatario_id = ?",
                new String[]{"1", "2"},
                null, null, null
        );

        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("El contenido del mensaje debería coincidir.",
                "Hola, este es un mensaje de prueba.",
                cursor.getString(cursor.getColumnIndex("contenido")));
        cursor.close();
    }

    @Test
    public void testObtenerMensajes() {
        // Insertar dos mensajes con diferente contenido
        mensajesEntity.insertar(1, 2, "Primer mensaje");
        try {
            Thread.sleep(1000); // Pausa de 1 segundo
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mensajesEntity.insertar(1, 2, "Segundo mensaje");

        // Recuperar los mensajes para el destinatario con ID 2
        Cursor cursor = mensajesEntity.obtenerMensajes(2);
        assertNotNull("El cursor no debería ser nulo.", cursor);

        // Comprobar que hay dos mensajes
        assertEquals("El cursor debería contener dos registros.", 2, cursor.getCount());

        // Validar que el mensaje más reciente es el último insertado
        cursor.moveToFirst();
        assertEquals("El primer mensaje debería ser el último insertado.",
                "Segundo mensaje", // Este fue el mensaje más reciente
                cursor.getString(cursor.getColumnIndex("contenido")));
        cursor.close();
    }

    @Test
    public void testMarcarComoLeido() {
        mensajesEntity.insertar(1, 2, "Mensaje sin leer");
        Cursor cursor = db.query(
                "TABLA_MENSAJES",
                new String[]{"id_mensaje"},
                null, null, null, null, null
        );

        assertTrue("Debería existir un mensaje en la base de datos.", cursor.moveToFirst());
        int idMensaje = cursor.getInt(cursor.getColumnIndex("id_mensaje"));
        cursor.close();

        boolean resultado = mensajesEntity.marcarComoLeido(idMensaje);
        assertTrue("El mensaje debería haberse marcado como leído.", resultado);

        Cursor cursorLeido = db.query(
                "TABLA_MENSAJES",
                new String[]{"leido"},
                "id_mensaje = ?",
                new String[]{String.valueOf(idMensaje)},
                null, null, null
        );

        assertTrue("El cursor debería contener el mensaje actualizado.", cursorLeido.moveToFirst());
        assertEquals("El mensaje debería estar marcado como leído.", 1, cursorLeido.getInt(cursorLeido.getColumnIndex("leido")));
        cursorLeido.close();
    }

    @Test
    public void testEliminarMensaje() {
        mensajesEntity.insertar(1, 2, "Mensaje a eliminar");
        Cursor cursor = db.query(
                "TABLA_MENSAJES",
                new String[]{"id_mensaje"},
                null, null, null, null, null
        );

        assertTrue("Debería existir un mensaje en la base de datos.", cursor.moveToFirst());
        int idMensaje = cursor.getInt(cursor.getColumnIndex("id_mensaje"));
        cursor.close();

        boolean resultado = mensajesEntity.eliminar(idMensaje);
        assertTrue("El mensaje debería haberse eliminado correctamente.", resultado);

        Cursor cursorEliminado = db.query(
                "TABLA_MENSAJES",
                null,
                "id_mensaje = ?",
                new String[]{String.valueOf(idMensaje)},
                null, null, null
        );

        assertFalse("El cursor no debería contener registros.", cursorEliminado.moveToFirst());
        cursorEliminado.close();
    }
}
