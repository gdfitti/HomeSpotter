package org.uvigo.esei.example.homespotter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Clase DBManager
 *
 * Clase que gestiona la base de datos de la aplicación HomeSpotter.
 * Proporciona métodos para crear, actualizar y poblar la base de datos con datos iniciales.
 * Implementa el patrón Singleton para garantizar que solo exista una instancia de la base de datos durante la ejecución.
 *
 * Tablas creadas:
 * - TABLA_USUARIO: Almacena información sobre los usuarios de la aplicación.
 * - TABLA_VIVIENDA: Almacena propiedades inmobiliarias.
 * - TABLA_FOTOS: Almacena URLs de fotos relacionadas con las propiedades.
 * - TABLA_FAVORITOS: Relaciona usuarios con sus propiedades favoritas.
 * - TABLA_MENSAJES: Almacena mensajes entre usuarios relacionados con propiedades.
 *
 * Métodos principales:
 * - {@link #onCreate(SQLiteDatabase)}: Crea las tablas necesarias y datos iniciales.
 * - {@link #onUpgrade(SQLiteDatabase, int, int)}: Actualiza la estructura de la base de datos.
 * - {@link #insertarUsuariosPorDefecto(SQLiteDatabase)}: Inserta usuarios iniciales.
 * - {@link #insertarPropiedadesPorDefecto(SQLiteDatabase)}: Inserta propiedades iniciales.
 * - {@link #insertarFotos(SQLiteDatabase)}: Inserta fotos relacionadas con propiedades.
 */
public class DBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "home_spotter.db";
    private static final int DATABASE_VERSION = 1;

    private static DBManager instance;

    /**
     * Constructor privado para implementar el patrón Singleton.
     *
     * @param c Contexto de la aplicación.
     */
    private DBManager(Context c) {
        super(c.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Método para obtener una instancia única de DBManager.
     *
     * @param c Contexto de la aplicación.
     * @return Instancia única de DBManager.
     */
    public static synchronized DBManager getInstance(Context c) {
        if (instance == null) {
            instance = new DBManager(c);
        }
        return instance;
    }

    /**
     * Crea las tablas de la base de datos y agrega datos iniciales.
     *
     * @param db Instancia de SQLiteDatabase.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBManager", "Creando BBDD " + DATABASE_NAME + " v" + DATABASE_VERSION);
        try {
            db.beginTransaction();

            // Crear tabla TABLA_USUARIO
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_USUARIO (" +
                    "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre_completo TEXT NOT NULL, " +
                    "nombre_usuario TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "foto_perfil TEXT, " +
                    "tlfno TEXT);");

            // Crear tabla TABLA_VIVIENDA
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_VIVIENDA (" +
                    "id_vivienda INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titulo TEXT NOT NULL, " +
                    "tipo_vivienda TEXT NOT NULL, " +
                    "precio REAL NOT NULL, " +
                    "direccion TEXT NOT NULL, " +
                    "estado TEXT NOT NULL, " +
                    "contacto TEXT NOT NULL, " +
                    "descripcion TEXT, " +
                    "propietario_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (propietario_id) REFERENCES TABLA_USUARIO (id_usuario) ON DELETE CASCADE);");

            // Crear tabla TABLA_FOTOS
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_FOTOS (" +
                    "id_foto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "vivienda_id INTEGER NOT NULL, " +
                    "url_foto TEXT NOT NULL, " +
                    "FOREIGN KEY (vivienda_id) REFERENCES TABLA_VIVIENDA (id_vivienda) ON DELETE CASCADE);");

            // Crear tabla TABLA_FAVORITOS
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_FAVORITOS (" +
                    "usuario_id INTEGER NOT NULL, " +
                    "vivienda_id INTEGER NOT NULL, " +
                    "PRIMARY KEY (usuario_id, vivienda_id), " +
                    "FOREIGN KEY (usuario_id) REFERENCES TABLA_USUARIO (id_usuario) ON DELETE CASCADE, " +
                    "FOREIGN KEY (vivienda_id) REFERENCES TABLA_VIVIENDA (id_vivienda) ON DELETE CASCADE);");

            // Crear tabla TABLA_MENSAJES
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_MENSAJES (" +
                    "id_mensaje INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "remitente_id INTEGER NOT NULL, " +
                    "destinatario_id INTEGER NOT NULL, " +
                    "vivienda_id INTEGER, " +
                    "contenido TEXT NOT NULL, " +
                    "fecha TEXT NOT NULL, " +
                    "leido INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY (remitente_id) REFERENCES TABLA_USUARIO (id_usuario) ON DELETE CASCADE, " +
                    "FOREIGN KEY (destinatario_id) REFERENCES TABLA_USUARIO (id_usuario) ON DELETE CASCADE, " +
                    "FOREIGN KEY (vivienda_id) REFERENCES TABLA_VIVIENDA (id_vivienda) ON DELETE SET NULL);");

            // Insertar datos iniciales
            insertarUsuariosPorDefecto(db);
            insertarPropiedadesPorDefecto(db);
            insertarFotos(db);

            db.setTransactionSuccessful();
        } catch (SQLException exc) {
            Log.e("DBManager.onCreate", "Error creando tablas: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Configura la base de datos para habilitar claves foráneas.
     *
     * @param db Instancia de SQLiteDatabase.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Actualiza la estructura de la base de datos al cambiar la versión.
     *
     * @param db Instancia de SQLiteDatabase.
     * @param v1 Versión antigua de la base de datos.
     * @param v2 Versión nueva de la base de datos.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        Log.i("DBManager", "Actualizando BBDD de la versión " + v1 + " a " + v2);

        // Eliminar tablas existentes
        db.execSQL("DROP TABLE IF EXISTS TABLA_MENSAJES;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_FAVORITOS;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_FOTOS;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_VIVIENDA;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_USUARIO;");

        // Volver a crear las tablas
        onCreate(db);
    }

    /**
     * Inserta usuarios iniciales en la base de datos.
     *
     * @param db Instancia de SQLiteDatabase.
     */
    private void insertarUsuariosPorDefecto(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        try {
            db.beginTransaction();
            // Insertar usuarios predeterminados
            // Usuario 1
            values.put("nombre_usuario", "monica");
            values.put("nombre_completo", "Monica Perez");
            values.put("email", "monica@homespotter.com");
            values.put("password", "password1");
            values.put("foto_perfil", "https://i.ibb.co/C5czCdt/Perfil1.jpg");
            values.put("tlfno", "123456789");
            db.insert("TABLA_USUARIO", null, values);
            values.clear();
            // Otros usuarios...
            db.setTransactionSuccessful();
        } catch (SQLException exc) {
            Log.e("DBManager.insertarUsuarios", "Error al crear usuarios: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Inserta propiedades iniciales en la base de datos.
     *
     * @param db Instancia de SQLiteDatabase.
     */
    private void insertarPropiedadesPorDefecto(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        try {
            db.beginTransaction();
            // Insertar propiedades predeterminadas
            values.put("tipo_vivienda", "Casa");
            values.put("precio", 200000.00);
            values.put("titulo", "Casa en Berres");
            values.put("direccion", "Calle Principal 123, 36684, Berres");
            values.put("estado", "Disponible");
            values.put("contacto", "monica@homespotter.com");
            values.put("descripcion", "Casa amplia con jardín.");
            values.put("propietario_id", 1);
            db.insert("TABLA_VIVIENDA", null, values);
            values.clear();
            db.setTransactionSuccessful();
        } catch (SQLException exc) {
            Log.e("DBManager.insertarPropiedades", "Error al crear propiedades: " + exc.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Inserta fotos iniciales en la base de datos.
     *
     * @param db Instancia de SQLiteDatabase.
     */
    private void insertarFotos(SQLiteDatabase db) {
        FotosEntity fotosEntity = new FotosEntity(db);
        fotosEntity.insertar(1, "https://i.ibb.co/N9fJyVq/chale.png");
        fotosEntity.insertar(2, "https://i.ibb.co/8xxwgyv/Apartamento.jpg");
    }
}
