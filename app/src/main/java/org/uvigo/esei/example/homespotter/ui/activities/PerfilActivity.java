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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase que representa la actividad del perfil del usuario.
 * Muestra y permite editar la información del usuario autenticado.
 */
public class PerfilActivity extends BaseActivity {

    // Variables para los componentes de la interfaz de usuario
    private TextView fullnameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView tlfnoTextView; // Campo adicional para mostrar el teléfono
    private TextView passwordTextView;
    private CheckBox showPasswordCheckBox;
    private Button editProfileButton;
    private Button endSessionButton;
    private ImageView profileImageView;

    private SharedPreferences sharedPreferences; // Preferencias para datos del usuario
    private UsuarioEntity usuarios; // Gestión de datos del usuario en la base de datos
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Ejecutor para tareas de fondo

    /**
     * Método llamado al crear la actividad.
     * Inicializa los componentes de la interfaz de usuario y configura los listeners.
     *
     * @param savedInstanceState el estado previamente guardado de la actividad (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBottomNavigationIcon(R.id.nav_profile, R.drawable.ic_profile_selected);

        // Inicializar los componentes de la interfaz
        fullnameTextView = findViewById(R.id.fullname);
        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
        tlfnoTextView = findViewById(R.id.tlfno);
        passwordTextView = findViewById(R.id.password);
        showPasswordCheckBox = findViewById(R.id.checkbox_show_password);
        editProfileButton = findViewById(R.id.btn_modify);
        endSessionButton = findViewById(R.id.btn_end_session);
        profileImageView = findViewById(R.id.profile_image);

        // Inicializar preferencias compartidas y la base de datos
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        usuarios = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());

        // Cargar los datos del perfil
        loadProfileData();

        // Configurar el botón de edición de perfil
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, ModifyProfileActivity.class);
            startActivity(intent);
        });

        // Configurar el CheckBox para alternar visibilidad de la contraseña
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordTextView.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            } else {
                passwordTextView.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        // Configurar el botón para cerrar sesión
        endSessionButton.setOnClickListener(v -> logout());
    }

    /**
     * Método para cargar los datos del perfil desde la base de datos.
     */
    private void loadProfileData() {
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            return; // Salir si no hay usuario autenticado
        }

        // Cargar datos en un hilo de fondo
        executorService.execute(() -> {
            ContentValues filters = new ContentValues();
            filters.put("id_usuario", userId);
            Cursor cursor = usuarios.buscar(filters);

            if (cursor != null && cursor.moveToFirst()) {
                String fullname = cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("nombre_usuario"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String tlfno = cursor.getString(cursor.getColumnIndexOrThrow("tlfno"));
                String profile = cursor.getString(cursor.getColumnIndexOrThrow("foto_perfil"));

                cursor.close();

                // Actualizar la interfaz en el hilo principal
                runOnUiThread(() -> {
                    fullnameTextView.setText(fullname);
                    usernameTextView.setText(username);
                    emailTextView.setText(email);
                    tlfnoTextView.setText(tlfno);
                    passwordTextView.setText(password);

                    if (profile != null && !profile.isEmpty()) {
                        Glide.with(this)
                                .load(profile)
                                .placeholder(R.drawable.ic_profile_default)
                                .into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_profile_default);
                    }
                });
            } else {
                runOnUiThread(() ->
                        Toast.makeText(PerfilActivity.this, this.getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Devuelve el identificador del recurso de diseño asociado a esta actividad.
     *
     * @return el ID del recurso de diseño (R.layout.activity_profile).
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_profile;
    }

    /**
     * Método llamado al iniciar la actividad.
     * Verifica si el usuario está autenticado.
     */
    @Override
    protected void onStart() {
        super.onStart();

        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Método llamado cuando se vuelve de otra actividad.
     * Recarga los datos del perfil si la edición fue exitosa.
     *
     * @param requestCode el código de solicitud.
     * @param resultCode el resultado devuelto por la actividad.
     * @param data los datos adicionales.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadProfileData();
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     * Elimina los datos de sesión y redirige al LoginActivity.
     */
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle(this.getString(R.string.logout_title))
                .setMessage(this.getString(R.string.logout_message))
                .setPositiveButton(this.getString(R.string.yes), (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    Toast.makeText(this, this.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(this.getString(R.string.no), null)
                .show();
    }

    /**
     * Método llamado al destruir la actividad.
     * Libera los recursos del ejecutor de hilos.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    /**
     * Método llamado al reanudar la actividad.
     * Recarga los datos del perfil.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadProfileData();
    }
}
