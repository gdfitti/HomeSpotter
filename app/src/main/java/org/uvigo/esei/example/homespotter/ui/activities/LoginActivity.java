package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.UsuarioEntity;

/**
 * LoginActivity
 *
 * Esta clase representa la actividad de inicio de sesión en la aplicación.
 * Gestiona la autenticación del usuario, validando sus credenciales contra
 * una base de datos local y almacenando la información de la sesión en
 * SharedPreferences.
 *
 * Características principales:
 * - Permite el inicio de sesión mediante un nombre de usuario y una contraseña.
 * - Guarda datos de sesión (ID y nombre de usuario) usando SharedPreferences.
 * - Redirige al usuario a PerfilActivity tras un inicio de sesión exitoso o si
 *   ya hay una sesión activa.
 * - Ofrece la posibilidad de registrarse redirigiendo a RegisterActivity.
 */
public class LoginActivity extends BaseActivity {

    /**
     * Entidad para interactuar con la tabla de usuarios en la base de datos.
     */
    private UsuarioEntity usuarios;

    /**
     * Campos de texto para ingresar el nombre de usuario y la contraseña.
     */
    private EditText userName, userPassword;

    /**
     * Preferencias compartidas para guardar la información de sesión del usuario.
     */
    private SharedPreferences sharedPreferences;

    // Constantes utilizadas para SharedPreferences
    private static final String PREF_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "user_id";
    private static final String USERNAME_KEY = "username";

    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz de usuario, inicializa la base de datos y establece
     * los listeners para los botones de inicio de sesión y registro.
     *
     * @param savedInstanceState Estado guardado previamente (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBottomNavigationIcon(R.id.nav_profile, R.drawable.ic_profile_selected);

        // Inicializar la entidad de usuarios con la base de datos
        usuarios = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());

        // Inicializar las preferencias compartidas
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Referenciar los elementos de la interfaz de usuario
        userName = findViewById(R.id.username);
        userPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);

        // Configurar el botón de inicio de sesión
        btnLogin.setOnClickListener(v -> {
            // Obtener los valores ingresados por el usuario
            String username = userName.getText().toString().trim();
            String password = userPassword.getText().toString().trim();

            // Validar los campos de entrada
            if (username.isEmpty()) {
                Toast.makeText(LoginActivity.this, getString(R.string.enter_username), Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
            } else {
                // Intentar iniciar sesión
                loginUser(username, password);
            }
        });

        // Configurar el botón de registro para navegar a RegisterActivity
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Especifica el diseño asociado a esta actividad.
     *
     * @return El ID del recurso de diseño de la actividad.
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    /**
     * Realiza el inicio de sesión del usuario, validando las credenciales
     * ingresadas contra la base de datos.
     *
     * @param username Nombre de usuario ingresado.
     * @param password Contraseña ingresada.
     */
    private void loginUser(String username, String password) {
        // Crear filtros para buscar el usuario en la base de datos
        ContentValues filters = new ContentValues();
        filters.put("nombre_usuario", username);
        filters.put("password", password);

        // Consultar la base de datos
        Cursor cursor = usuarios.buscar(filters);

        if (cursor != null && cursor.moveToFirst()) {
            // Obtener datos del usuario si las credenciales son válidas
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
            String savedUsername = cursor.getString(cursor.getColumnIndexOrThrow("nombre_usuario"));

            // Guardar los datos del usuario en SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(USER_ID_KEY, userId);
            editor.putString(USERNAME_KEY, savedUsername);
            editor.apply();

            // Navegar a PerfilActivity
            Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Mostrar mensaje de error si las credenciales son inválidas
            Toast.makeText(LoginActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
        }

        // Cerrar el cursor si no es nulo
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Método llamado al iniciar la actividad.
     * Verifica si hay una sesión activa. Si existe, redirige automáticamente
     * a PerfilActivity.
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si hay una sesión activa
        int userId = sharedPreferences.getInt(USER_ID_KEY, -1);
        if (userId != -1) {
            // Si hay una sesión activa, navegar automáticamente a PerfilActivity
            Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
            startActivity(intent);
            finish();
        }
    }
}