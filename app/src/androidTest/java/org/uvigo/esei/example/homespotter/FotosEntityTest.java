package org.uvigo.esei.example.homespotter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uvigo.esei.example.homespotter.database.FotosEntity;

import static org.junit.Assert.*;

public class FotosEntityTest {
    private SQLiteDatabase db;
    private FotosEntity fotosEntity;

    @Before
    public void setUp() {
        // Crear una base de datos en memoria para pruebas
        Context context = ApplicationProvider.getApplicationContext();
        SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(context, null, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE TABLA_FOTOS (" +
                        "id_foto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "vivienda_id INTEGER NOT NULL, " +
                        "url_foto TEXT NOT NULL);");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS TABLA_FOTOS");
                onCreate(db);
            }
        };
        db = dbHelper.getWritableDatabase();
        fotosEntity = new FotosEntity(db);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertarFoto() {
        boolean resultado = fotosEntity.insertar(1, "https://example.com/foto1.jpg");
        assertTrue("La foto debería haberse insertado correctamente.", resultado);

        Cursor cursor = db.query("TABLA_FOTOS", null, "url_foto = ?", new String[]{"https://example.com/foto1.jpg"}, null, null, null);
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("La URL de la foto debería coincidir.", "https://example.com/foto1.jpg", cursor.getString(cursor.getColumnIndex("url_foto")));
        cursor.close();
    }

    @Test
    public void testEliminarFoto() {
        // Insertar una foto para eliminar
        fotosEntity.insertar(1, "https://example.com/foto2.jpg");

        // Recuperar el ID de la foto insertada
        Cursor cursor = db.query("TABLA_FOTOS", new String[]{"id_foto"}, "url_foto = ?", new String[]{"https://example.com/foto2.jpg"}, null, null, null);
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        int idFoto = cursor.getInt(cursor.getColumnIndex("id_foto"));
        cursor.close();

        // Eliminar la foto
        boolean eliminado = fotosEntity.eliminar(idFoto);
        assertTrue("La foto debería haberse eliminado correctamente.", eliminado);

        // Verificar que la foto ya no existe
        cursor = db.query("TABLA_FOTOS", null, "id_foto = ?", new String[]{String.valueOf(idFoto)}, null, null, null);
        assertFalse("El cursor no debería contener registros.", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testObtenerFotosPorVivienda() {
        // Insertar varias fotos para una vivienda
        fotosEntity.insertar(1, "https://example.com/foto1.jpg");
        fotosEntity.insertar(1, "https://example.com/foto2.jpg");
        fotosEntity.insertar(2, "https://example.com/foto3.jpg");

        // Obtener fotos para la vivienda con ID 1
        Cursor cursor = fotosEntity.obtenerFotosPorVivienda(1);
        assertNotNull("El cursor no debería ser nulo.", cursor);
        assertEquals("Debería haber dos fotos asociadas a la vivienda con ID 1.", 2, cursor.getCount());

        // Verificar las URLs de las fotos
        cursor.moveToFirst();
        assertEquals("La primera URL debería ser https://example.com/foto1.jpg.", "https://example.com/foto1.jpg", cursor.getString(cursor.getColumnIndex("url_foto")));
        cursor.moveToNext();
        assertEquals("La segunda URL debería ser https://example.com/foto2.jpg.", "https://example.com/foto2.jpg", cursor.getString(cursor.getColumnIndex("url_foto")));
        cursor.close();
    }
}
