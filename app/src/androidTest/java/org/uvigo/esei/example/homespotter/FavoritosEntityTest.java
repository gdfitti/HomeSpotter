package org.uvigo.esei.example.homespotter;

import org.uvigo.esei.example.homespotter.database.FavoritosEntity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FavoritosEntityTest {
    private SQLiteDatabase db;
    private FavoritosEntity favoritosEntity;

    @Before
    public void setUp() {
        // Crear una base de datos en memoria para pruebas
        Context context = ApplicationProvider.getApplicationContext();
        SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(context, null, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE TABLA_FAVORITOS (" +
                        "id_usuario INTEGER NOT NULL, " +
                        "id_vivienda INTEGER NOT NULL, " +
                        "PRIMARY KEY (id_usuario, id_vivienda));");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS TABLA_FAVORITOS");
                onCreate(db);
            }
        };
        db = dbHelper.getWritableDatabase();
        favoritosEntity = new FavoritosEntity(db);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertarFavorito() {
        boolean resultado = favoritosEntity.insertar(1, 100);
        assertTrue("El favorito debería haberse insertado correctamente.", resultado);

        Cursor cursor = db.query("TABLA_FAVORITOS", null, "id_usuario = ? AND id_vivienda = ?", new String[]{"1", "100"}, null, null, null);
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("El ID del usuario debería ser 1.", 1, cursor.getInt(cursor.getColumnIndex("id_usuario")));
        assertEquals("El ID de la vivienda debería ser 100.", 100, cursor.getInt(cursor.getColumnIndex("id_vivienda")));
        cursor.close();
    }

    @Test
    public void testEliminarFavorito() {
        // Insertar un favorito antes de eliminarlo
        favoritosEntity.insertar(1, 100);

        // Eliminar el favorito
        boolean eliminado = favoritosEntity.eliminar(1, 100);
        assertTrue("El favorito debería haberse eliminado correctamente.", eliminado);

        // Verificar que el favorito ya no existe
        Cursor cursor = db.query("TABLA_FAVORITOS", null, "id_usuario = ? AND id_vivienda = ?", new String[]{"1", "100"}, null, null, null);
        assertFalse("El cursor no debería contener registros.", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testInsertarFavoritoParametrosInvalidos() {
        // Intentar insertar con un ID de usuario inválido
        boolean resultado = favoritosEntity.insertar(0, 100);
        assertFalse("El favorito no debería haberse insertado con un ID de usuario inválido.", resultado);

        // Intentar insertar con un ID de vivienda inválido
        resultado = favoritosEntity.insertar(1, 0);
        assertFalse("El favorito no debería haberse insertado con un ID de vivienda inválido.", resultado);
    }

    @Test
    public void testEliminarFavoritoInexistente() {
        // Intentar eliminar un favorito que no existe
        boolean eliminado = favoritosEntity.eliminar(1, 100);
        assertFalse("No debería eliminarse un favorito inexistente.", eliminado);
    }
}
