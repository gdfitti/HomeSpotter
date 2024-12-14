package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.UsuarioEntity;

public class PerfilActivity extends BaseActivity {

    private TextView fullnameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView tlfnoTextView; // Nuevo campo para el teléfono
    private TextView passwordTextView;
    private CheckBox showPasswordCheckBox;
    private Button editProfileButton;
    private Button endSessionButton;
    private ImageView profileImageView;

    private SharedPreferences sharedPreferences;
    private UsuarioEntity usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBottomNavigationIcon(R.id.nav_profile, R.drawable.ic_profile_selected);

        // Inicializar los componentes de la UI
        fullnameTextView = findViewById(R.id.fullname);
        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
        tlfnoTextView = findViewById(R.id.tlfno); // Inicializamos el TextView para teléfono
        passwordTextView = findViewById(R.id.password);
        showPasswordCheckBox = findViewById(R.id.checkbox_show_password);
        editProfileButton = findViewById(R.id.btn_modify);
        endSessionButton = findViewById(R.id.btn_end_session);
        profileImageView = findViewById(R.id.profile_image);
        // Inicializar SharedPreferences y DB
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        usuarios = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());

        // Cargar los datos del perfil
        loadProfileData();

        // Configurar el botón de editar perfil
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, ModifyProfileActivity.class);
            startActivity(intent); // Usamos el método startActivityForResult para recibir los cambios
        });

        // Alternar la visibilidad de la contraseña al marcar o desmarcar el CheckBox
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Mostrar la contraseña
                passwordTextView.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            } else {
                // Ocultar la contraseña (mostrar puntos)
                passwordTextView.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        endSessionButton.setOnClickListener(v -> {
            logout();
        });
    }

    // Método para cargar los datos del perfil desde la base de datos
    private void loadProfileData() {
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            return;
        }

        ContentValues filters = new ContentValues();
        filters.put("id_usuario", userId);
        Cursor cursor = usuarios.buscar(filters);

        if (cursor != null && cursor.moveToFirst()) {
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("nombre_usuario"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String tlfno = cursor.getString(cursor.getColumnIndexOrThrow("tlfno")); // Cargar teléfono
            String profile = cursor.getString(cursor.getColumnIndexOrThrow("foto_perfil")); // Cargar teléfono

            // Mostrar los datos en los TextViews
            fullnameTextView.setText(fullname);
            usernameTextView.setText(username);
            emailTextView.setText(email);
            tlfnoTextView.setText(tlfno); // Mostrar teléfono
            passwordTextView.setText(password); // Se puede ocultar en caso necesario

            if (profile != null && !profile.isEmpty()) {
                Glide.with(this)
                        .load(profile)
                        .placeholder(R.drawable.ic_profile_default) // Imagen de carga predeterminada// Imagen en caso de error// Recorte en forma de círculo
                        .into(profileImageView); // Tu ImageView para la foto de perfil
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile_default); // Imagen por defecto
            }
            cursor.close();
        } else {
            Toast.makeText(PerfilActivity.this, "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si el usuario está autenticado
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Si hemos vuelto de la pantalla de edición, recargamos los datos
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadProfileData();
        }
    }
    private void logout() {
        // Confirmar la acción con un diálogo
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Eliminar los datos de sesión de SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Borra todos los datos
                    editor.apply();

                    // Redirigir al LoginActivity
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el stack de actividades
                    startActivity(intent);

                    // Mostrar mensaje y finalizar actividad
                    Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", null) // No hacer nada si el usuario cancela
                .show();
    }
}

