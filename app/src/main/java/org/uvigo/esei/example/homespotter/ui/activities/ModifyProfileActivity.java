package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.UsuarioEntity;

public class ModifyProfileActivity extends AppCompatActivity {
    private EditText fullnameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText tlfnoEditText; // EditText para teléfono
    private EditText passwordEditText;
    private CheckBox showPasswordCheckBox;
    private Button saveChangesButton;
    private Button cancelButton;

    private SharedPreferences sharedPreferences;
    private UsuarioEntity usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile); // Asegúrate de tener el layout correcto

        // Inicializar los componentes de la UI
        fullnameEditText = findViewById(R.id.fullname);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        tlfnoEditText = findViewById(R.id.tlfno); // EditText para teléfono
        passwordEditText = findViewById(R.id.password);
        showPasswordCheckBox = findViewById(R.id.checkbox_show_password);
        saveChangesButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);

        // Inicializar SharedPreferences y DB
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        usuarios = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());

        // Cargar los datos del perfil en los campos de edición
        loadProfileData();

        // Guardar los cambios cuando el usuario haga clic en "Guardar cambios"
        saveChangesButton.setOnClickListener(v -> saveProfileChanges());

        // Volver a la pantalla anterior sin guardar cambios
        cancelButton.setOnClickListener(v -> finish());

        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si el checkbox está marcado, se muestra la contraseña
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                // Si no está marcado, la contraseña se oculta (en forma de puntos)
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            // Mover el cursor al final del texto después de cambiar el tipo de input
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }

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

            // Rellenar los campos de edición con los datos existentes
            fullnameEditText.setText(fullname);
            usernameEditText.setText(username);
            emailEditText.setText(email);
            tlfnoEditText.setText(tlfno); // Mostrar teléfono en EditText
            passwordEditText.setText(password);

            cursor.close();
        } else {
            Toast.makeText(ModifyProfileActivity.this, "No se pudo cargar los datos del perfil", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfileChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmar modificación")
                .setMessage("¿Estás seguro de que quieres guardar los cambios en tu perfil?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    String newFullname = fullnameEditText.getText().toString().trim();
                    String newUsername = usernameEditText.getText().toString().trim();
                    String newEmail = emailEditText.getText().toString().trim();
                    String newPassword = passwordEditText.getText().toString().trim();
                    String newTlfno = tlfnoEditText.getText().toString().trim(); // Obtener el nuevo teléfono

                    if (newFullname.isEmpty() || newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || newTlfno.isEmpty()) {
                        Toast.makeText(ModifyProfileActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
                        int userId = sharedPreferences.getInt("user_id", -1);
                        if (userId != -1) {
                            // Actualizar los datos en la base de datos
                            boolean isUpdated = usuarios.modificar(userId, newUsername, newFullname, newEmail, newPassword,"", newTlfno);
                            if (isUpdated) {
                                Toast.makeText(ModifyProfileActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();

                                // Regresar a la pantalla de perfil
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(ModifyProfileActivity.this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Cierra el diálogo sin realizar ninguna acción
                    dialog.dismiss();
                })
                .show();


    }
}
