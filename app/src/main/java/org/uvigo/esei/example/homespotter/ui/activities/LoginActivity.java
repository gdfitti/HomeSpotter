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

public class LoginActivity extends BaseActivity {

    private UsuarioEntity usuarios;
    private EditText userName, userPassword;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "user_id";
    private static final String USERNAME_KEY = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBottomNavigationIcon(R.id.nav_profile,R.drawable.ic_profile_selected);

        // Inicializar DatabaseHelper y obtener la base de datos
        usuarios = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());


        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Referenciar los elementos de la UI
        userName = findViewById(R.id.username);
        userPassword = findViewById(R.id.password);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_register);

        // Configurar el botón de inicio de sesión
        btnLogin.setOnClickListener(v -> {
            String username = userName.getText().toString().trim();
            String password = userPassword.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor ingresa tu nombre de usuario", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor ingresa tu contraseña", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(username, password);
            }
        });

        // Configurar el botón de registro
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    private void loginUser(String username, String password) {
        ContentValues filters = new ContentValues();
        filters.put("nombre_usuario", username);
        filters.put("password", password);

        Cursor cursor = usuarios.buscar(filters);

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
            String savedUsername = cursor.getString(cursor.getColumnIndexOrThrow("nombre_usuario"));

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(USER_ID_KEY, userId);
            editor.putString(USERNAME_KEY, savedUsername);
            editor.apply();

            Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
        if(cursor != null){
            cursor.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        int userId = sharedPreferences.getInt(USER_ID_KEY, -1);
        if (userId != -1) {
            Intent intent = new Intent(LoginActivity.this, PerfilActivity.class);
            startActivity(intent);
            finish();
        }
    }
}




