package org.uvigo.esei.example.homespotter.database;
//Por acabar y documentar
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
                    "titulo TEXT NOT NULL, " +
                    "tipo_vivienda TEXT NOT NULL, precio REAL NOT NULL, " +
                    "direccion TEXT NOT NULL, estado TEXT NOT NULL, " +
                    "contacto TEXT NOT NULL, descripcion TEXT, " +
                    "propietario_id INTEGER NOT NULL, FOREIGN KEY (propietario_id) REFERENCES TABLA_USUARIO (id_usuario) ON DELETE CASCADE) ;");

            // Crear TABLA_FOTOS
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_FOTOS ("+
                    "id_foto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "vivienda_id INTEGER NOT NULL, " +
                    "url_foto TEXT NOT NULL, " +
                    "FOREIGN KEY(vivienda_id) REFERENCES TABLA_VIVIENDA (id_vivienda) ON DELETE CASCADE);");

            // Crear TABLA_COMENTARIO
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_COMENTARIO (" +
                    "id_mensaje INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "contenido TEXT NOT NULL, fecha TEXT NOT NULL, " +
                    "usuario_id INTEGER NOT NULL, vivienda_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (usuario_id) REFERENCES TABLA_USUARIO (id_usuario) ON DELETE CASCADE, " +
                    "FOREIGN KEY (vivienda_id) REFERENCES TABLA_VIVIENDA (id_vivienda) ON DELETE CASCADE);");

            // Crear TABLA_FAVORITOS
            db.execSQL("CREATE TABLE IF NOT EXISTS TABLA_FAVORITOS (" +
                    "usuario_id INTEGER NOT NULL, vivienda_id INTEGER NOT NULL, " +
                    "PRIMARY KEY (usuario_id, vivienda_id), " +
                    "FOREIGN KEY (usuario_id) REFERENCES TABLA_USUARIO (id_usuario) ON DELETE CASCADE, " +
                    "FOREIGN KEY (vivienda_id) REFERENCES TABLA_VIVIENDA (id_vivienda) ON DELETE CASCADE);");

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

            insertarUsuariosPorDefecto(db);
            insertarPropiedadesPorDefecto(db);
            insertarFotos(db);
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
        db.execSQL("DROP TABLE IF EXISTS TABLA_MENSAJES;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_FAVORITOS;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_COMENTARIO;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_FOTOS;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_VIVIENDA;");
        db.execSQL("DROP TABLE IF EXISTS TABLA_USUARIO;");

        // Volver a crear las tablas
        onCreate(db);
    }

    private void insertarUsuariosPorDefecto(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        try{
            db.beginTransaction();
            // Usuario 1
            values.put("nombre_usuario", "Propietario 1");
            values.put("email", "propietario1@example.com");
            values.put("password", "password1");
            values.put("foto_perfil", "propietario1.jpg");
            values.put("tlfno", "123456789");
            db.insert("TABLA_USUARIO", null, values);


            // Usuario 2
            values.clear();
            values.put("nombre_usuario", "Propietario 2");
            values.put("email", "propietario2@example.com");
            values.put("password", "password2");
            values.put("foto_perfil", "propietario2.jpg");
            values.put("tlfno", "987654321");
            db.insert("TABLA_USUARIO", null, values);

            //Usuario3
            values.clear();
            values.put("nombre_usuario", "Manuel Fernández");
            values.put("email", "manu21@example.com");
            values.put("password", "password3");
            values.put("foto_perfil", "https://ibb.co/ZdQG0L9");
            values.put("tlfno", "987654321");
            db.insert("TABLA_USUARIO", null, values);
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e("DBManager.insertarUsuarios", "Error al crear los usuarios: " + exc.getMessage());
        }finally {
            db.endTransaction();
        }

    }

    private void insertarPropiedadesPorDefecto(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        try{
            db.beginTransaction();
            // Primera propiedad
            values.put("tipo_vivienda", "Casa");
            values.put("precio", 200000.00);
            values.put("titulo", "Casa en Berres");
            values.put("direccion", "Calle Principal 123");
            values.put("estado", "Disponible");
            values.put("contacto", "propietario1@example.com");
            values.put("descripcion", "Casa amplia con jardín.");
            values.put("propietario_id", 1); // Debe coincidir con un usuario existente
            db.insert("TABLA_VIVIENDA", null, values);

            values.clear();


            // Segunda propiedad
            values.clear();
            values.put("tipo_vivienda", "Apartamento");
            values.put("precio", 150000.00);
            values.put("titulo", "Apartamento en Coruña");
            values.put("direccion", "Avenida Siempreviva 742");
            values.put("estado", "Ocupado");
            values.put("contacto", "propietario2@example.com");
            values.put("descripcion", "Apartamento moderno en el centro.");
            values.put("propietario_id", 2); // Debe coincidir con otro usuario existente
            db.insert("TABLA_VIVIENDA", null, values);

            values.clear();

            // Vivienda 3
            values.clear();
            values.put("tipo_vivienda", "Estudio");
            values.put("precio", 100000.00);
            values.put("titulo", "Estudio céntrico");
            values.put("direccion", "Plaza Mayor 5");
            values.put("estado", "Disponible");
            values.put("contacto", "propietario3@example.com");
            values.put("descripcion", "Estudio perfecto para estudiantes.");
            values.put("propietario_id", 3);
            db.insert("TABLA_VIVIENDA", null, values);

            values.clear();
            values.put("url_foto", "https://i.ibb.co/ydXYLw7/Chal-en-Almer-a.webp");
            db.insert("TABLA_FOTOS", null, values);

            // Viviendas 4 a 10
            for (int i = 4; i <= 10; i++) {
                values.clear();
                values.put("tipo_vivienda", i % 2 == 0 ? "Casa" : "Apartamento");
                values.put("precio", 200000 + i * 10000.00);
                values.put("titulo", "Vivienda número " + i);
                values.put("direccion", "Dirección genérica " + i);
                values.put("estado", i % 2 == 0 ? "Disponible" : "Ocupado");
                values.put("contacto", "propietario" + (i % 3 + 1) + "@example.com");
                values.put("descripcion", "Descripción de la vivienda número " + i);
                values.put("propietario_id", 1);
                db.insert("TABLA_VIVIENDA", null, values);

                // Insertar fotos asociadas
                values.clear();
                values.put("url_foto", "https://i.ibb.co/Wg87SSg/Casa-Vilagarc-a.jpg");
                db.insert("TABLA_FOTOS", null, values);
            }
            db.setTransactionSuccessful();
        }catch (SQLException exc){
            Log.e("DBManager.insertarViviendas", "Error al crear las Viviendas: " + exc.getMessage());
        }finally {
            db.endTransaction();
        }

    }

    private void insertarFotos(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        FotosEntity fotosEntity = new FotosEntity(db);
        fotosEntity.insertar(1,"https://i.ibb.co/N9fJyVq/chale.png");
        fotosEntity.insertar(2,"https://i.ibb.co/8xxwgyv/Apartamento.jpg");
        fotosEntity.insertar(3,"https://i.ibb.co/Wg87SSg/Casa-Vilagarc-a.jpg");
        fotosEntity.insertar(4,"https://i.ibb.co/ZmPRpSn/Casa-Vigo-con-Vistas-al-Mar.jpg");
        fotosEntity.insertar(5,"https://i.ibb.co/Fhxyx5W/Apartamento-Pontevedra.png");
        fotosEntity.insertar(6,"https://i.ibb.co/7n02ZtS/Apartamento-Salamanca.png");
        fotosEntity.insertar(7,"https://i.ibb.co/8xxwgyv/Apartamento.jpg");
        fotosEntity.insertar(8,"https://i.ibb.co/8xxwgyv/Apartamento.jpg");
        fotosEntity.insertar(9,"https://i.ibb.co/8xxwgyv/Apartamento.jpg");
        fotosEntity.insertar(10,"https://i.ibb.co/8xxwgyv/Apartamento.jpg");

    }
}
