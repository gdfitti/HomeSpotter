package org.uvigo.esei.example.homespotter.database;
//Por acabar y documentar
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "home_spotter.db";
    private static final int DATABASE_VERSION = 1;
    private static DBManager instance;

    private DBManager(Context c){
        super(c.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBManager getInstance(Context c){
        if (instance == null){
            instance = new DBManager(c);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i( "DBManager", "Creando BBDD " + DATABASE_NAME + " v" + DATABASE_VERSION);
        try{
            db.beginTransaction();
            // Crear TABLA_USUARIO
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_USUARIO (" +
                    "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre_usuario TEXT NOT NULL, email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, foto_perfil TEXT, tlfno TEXT);");

            // Crear TABLA_VIVIENDA
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_VIVIENDA (" +
                    "id_vivienda INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tipo_vivienda TEXT NOT NULL, precio REAL NOT NULL, " +
                    "direccion TEXT NOT NULL, estado TEXT NOT NULL, " +
                    "contacto TEXT NOT NULL, fotos TEXT, descripcion TEXT, " +
                    "propietario_id INTEGER NOT NULL, FOREIGN KEY (propietario_id) REFERENCES TABLA_USUARIO (id_usuario));");

            // Crear TABLA_FILTRO_DE_BUSQUEDA
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_FILTRO_DE_BUSQUEDA (" +
                    "id_busqueda INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ubicacion TEXT, rango_precio TEXT, tipo_vivienda TEXT, " +
                    "caracteristicas TEXT, usuario_id INTEGER NOT NULL, FOREIGN KEY (usuario_id) REFERENCES TABLA_USUARIO (id_usuario));");

            // Crear TABLA_NOTIFICACION
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_NOTIFICACION (" +
                    "id_notificacion INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "mensaje TEXT NOT NULL, fecha TEXT NOT NULL, estado TEXT, tipo TEXT, " +
                    "usuario_id INTEGER NOT NULL, FOREIGN KEY (usuario_id) REFERENCES TABLA_USUARIO (id_usuario));");

            // Crear TABLA_COMENTARIO
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_COMENTARIO (" +
                    "id_mensaje INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "contenido TEXT NOT NULL, fecha TEXT NOT NULL, " +
                    "usuario_id INTEGER NOT NULL, vivienda_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (usuario_id) REFERENCES TABLA_USUARIO (id_usuario), " +
                    "FOREIGN KEY (vivienda_id) REFERENCES TABLA_VIVIENDA (id_vivienda));");

            // Crear TABLA_FAVORITOS
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_FAVORITOS (" +
                    "usuario_id INTEGER NOT NULL, vivienda_id INTEGER NOT NULL, " +
                    "PRIMARY KEY (usuario_id, vivienda_id), " +
                    "FOREIGN KEY (usuario_id) REFERENCES TABLA_USUARIO (id_usuario), " +
                    "FOREIGN KEY (vivienda_id) REFERENCES TABLA_VIVIENDA (id_vivienda));");
            db.setTransactionSuccessful();

        }catch(SQLException exc){
            Log.e("DBManager.onCreate", "Creando las tablas" +": "+ exc.getMessage());
        }finally {
            db.endTransaction();
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2) {
        Log.i("DBManager", "Actualizando BBDD de la versión " + v1 + " a " + v2);

        // Eliminar tablas existentes
        db.execSQL("DROP TABLE IF EXISTS TABLA_USUARIO;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_VIVIENDA;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_FILTRO_DE_BUSQUEDA;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_NOTIFICACION;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_COMENTARIO;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_FAVORITOS;");

        // Volver a crear las tablas
        onCreate(db);
    }

    public long insertarUsuario(String nombre, String email, String password, String fotoPerfil, String telefono) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_usuario", nombre);
        values.put("email", email);
        values.put("password", password);
        values.put("foto_perfil", fotoPerfil);
        values.put("tlfno", telefono);
        return db.insert("TABLA_USUARIO", null, values);
    }

}
