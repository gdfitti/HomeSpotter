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
import org.uvigo.esei.example.homespotter.database.ViviendaEntity;

import static org.junit.Assert.*;

public class ViviendaEntityTest {
    private SQLiteDatabase db;
    private ViviendaEntity viviendaEntity;

    @Before
    public void setUp() {
        // Crear una base de datos en memoria para pruebas
        Context context = ApplicationProvider.getApplicationContext();
        SQLiteOpenHelper dbHelper = new SQLiteOpenHelper(context, null, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE TABLA_VIVIENDA (" +
                        "id_vivienda INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "tipo_vivienda TEXT NOT NULL, " +
                        "precio REAL NOT NULL, " +
                        "direccion TEXT NOT NULL, " +
                        "estado TEXT NOT NULL, " +
                        "contacto TEXT NOT NULL, " +
                        "descripcion TEXT, " +
                        "propietario_id INTEGER NOT NULL);");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS TABLA_VIVIENDA");
                onCreate(db);
            }
        };
        db = dbHelper.getWritableDatabase();
        viviendaEntity = new ViviendaEntity(db);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void testInsertar() {
        boolean resultado = viviendaEntity.insertar(0, "Casa", 150000.0, "Calle 123", "Disponible", "123456789", "Casa familiar", 1);
        assertTrue("La vivienda debería haberse insertado correctamente.", resultado);

        Cursor cursor = db.query("TABLA_VIVIENDA", null, "tipo_vivienda = ?", new String[]{"Casa"}, null, null, null);
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("El tipo de vivienda debería ser Casa.", "Casa", cursor.getString(cursor.getColumnIndex("tipo_vivienda")));
        cursor.close();
    }

    @Test
    public void testModificarVivienda() {
        viviendaEntity.insertar(0, "Casa", 150000.0, "Calle 123", "Disponible", "123456789", "Casa familiar", 1);

        boolean actualizado = viviendaEntity.modificarVivienda(1, "Apartamento", 200000.0, null, null, null, "Descripción actualizada");
        assertTrue("La vivienda debería haberse actualizado correctamente.", actualizado);

        Cursor cursor = db.query("TABLA_VIVIENDA", null, "id_vivienda = ?", new String[]{"1"}, null, null, null);
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("El tipo de vivienda debería haberse actualizado a Apartamento.", "Apartamento", cursor.getString(cursor.getColumnIndex("tipo_vivienda")));
        assertEquals("La descripción debería haberse actualizado.", "Descripción actualizada", cursor.getString(cursor.getColumnIndex("descripcion")));
        cursor.close();
    }

    @Test
    public void testEliminar() {
        viviendaEntity.insertar(0, "Casa", 150000.0, "Calle 123", "Disponible", "123456789", "Casa familiar", 1);

        boolean eliminado = viviendaEntity.eliminar(1);
        assertTrue("La vivienda debería haberse eliminado correctamente.", eliminado);

        Cursor cursor = db.query("TABLA_VIVIENDA", null, "id_vivienda = ?", new String[]{"1"}, null, null, null);
        assertFalse("El cursor no debería contener registros.", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testBuscarPorId() {
        viviendaEntity.insertar(0, "Casa", 150000.0, "Calle 123", "Disponible", "123456789", "Casa familiar", 1);

        Cursor cursor = viviendaEntity.buscarPorId(1);
        assertNotNull("El cursor no debería ser nulo.", cursor);
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("El tipo de vivienda debería ser Casa.", "Casa", cursor.getString(cursor.getColumnIndex("tipo_vivienda")));
        cursor.close();
    }

    @Test
    public void testBuscarConFiltros() {
        viviendaEntity.insertar(0, "Casa", 150000.0, "Calle 123", "Disponible", "123456789", "Casa familiar", 1);
        viviendaEntity.insertar(0, "Apartamento", 200000.0, "Calle 456", "Ocupado", "987654321", "Apartamento moderno", 2);

        ContentValues filtros = new ContentValues();
        filtros.put("estado", "Disponible");

        Cursor cursor = viviendaEntity.buscar(filtros, 100000.0, 160000.0);
        assertNotNull("El cursor no debería ser nulo.", cursor);
        assertTrue("El cursor debería contener un registro.", cursor.moveToFirst());
        assertEquals("El tipo de vivienda debería ser Casa.", "Casa", cursor.getString(cursor.getColumnIndex("tipo_vivienda")));
        cursor.close();
    }
}
