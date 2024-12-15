package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.UsuarioEntity;
import org.uvigo.esei.example.homespotter.imgbb.ImageUploader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * RegisterActivity
 *
 * Actividad que permite a los usuarios registrarse en la aplicación.
 * Los usuarios pueden completar sus datos, seleccionar una foto de perfil opcional
 * y guardar su información en la base de datos.
 */
public class RegisterActivity extends AppCompatActivity {

    // Elementos de la interfaz de usuario
    private ImageView profileImageView;
    private Uri selectedImageUri; // URI de la imagen seleccionada
    private UsuarioEntity usuarios; // Entidad para interactuar con la base de datos de usuarios

    // Campos de entrada de texto
    private EditText fullName, userName, email, password, contact;

    // ActivityResultLauncher para seleccionar imágenes
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    manejarSeleccionImagen(uri); // Maneja la imagen seleccionada
                }
            });

    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz de usuario y los listeners de los botones.
     *
     * @param savedInstanceState Estado guardado previamente (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usuarios = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());

        profileImageView = findViewById(R.id.profile_image);
        Button btnSelectImage = findViewById(R.id.btn_select_image);

        // Configurar el botón para seleccionar una imagen
        btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        // Referenciar los elementos de la UI
        fullName = findViewById(R.id.fullname);
        userName = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        contact = findViewById(R.id.contact);
        Button cancelBtn = findViewById(R.id.btn_cancel);
        Button btnRegister = findViewById(R.id.btn_register);

        // Configurar el botón de registro
        btnRegister.setOnClickListener(view -> {
            String name = fullName.getText().toString().trim();
            String username = userName.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            String userContact = contact.getText().toString().trim();

            // Validar entradas y registrar usuario
            if (validateInputs(name, username, userEmail, userPassword)) {
                if (selectedImageUri != null) {
                    subirImagenAImgBB(name, username, userEmail, userPassword, userContact);
                } else {
                    registrarUsuario(name, username, userEmail, userPassword, null, userContact);
                }
            }
        });

        // Configurar el botón de cancelar
        cancelBtn.setOnClickListener(view -> showCancelDialog());
    }

    /**
     * Muestra un diálogo de confirmación para cancelar el registro.
     */
    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.cancel))
                .setMessage(this.getString(R.string.sure_cancel))
                .setPositiveButton(this.getString(R.string.yes), (dialogInterface, i) -> {
                    Toast.makeText(RegisterActivity.this, "Registro Cancelado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(this.getString(R.string.no), null)
                .create().show();
    }

    /**
     * Maneja la selección de una imagen por parte del usuario.
     *
     * @param uri URI de la imagen seleccionada.
     */
    private void manejarSeleccionImagen(Uri uri) {
        profileImageView.setImageURI(uri); // Establece la URI seleccionada como imagen en el ImageView
        selectedImageUri = uri; // Guarda el URI seleccionado
    }

    /**
     * Valida las entradas del usuario.
     *
     * @param name Nombre completo.
     * @param username Nombre de usuario.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @return true si las entradas son válidas, false en caso contrario.
     */
    private boolean validateInputs(String name, String username, String email, String password) {
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Sube la imagen seleccionada a ImgBB y registra al usuario en la base de datos.
     *
     * @param name Nombre completo.
     * @param username Nombre de usuario.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @param contact Contacto.
     */
    private void subirImagenAImgBB(String name, String username, String email, String password, String contact) {
        try {
            File file = getFileFromUri(selectedImageUri);
            ImageUploader imageUploader = new ImageUploader();

            imageUploader.uploadImage(file.getAbsolutePath(), new ImageUploader.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl, String deleteUrl) {
                    registrarUsuario(name, username, email, password, imageUrl, contact);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(RegisterActivity.this, "Error al subir la imagen: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "Error al procesar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Convierte un URI en un archivo temporal para su procesamiento.
     *
     * @param uri URI de la imagen seleccionada.
     * @return Archivo temporal.
     * @throws IOException Si ocurre un error al procesar el URI.
     */
    private File getFileFromUri(Uri uri) throws IOException {
        File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
        tempFile.deleteOnExit();

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            if (inputStream == null) {
                throw new IOException("No se pudo abrir el InputStream para el Uri");
            }
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    /**
     * Registra al usuario en la base de datos.
     *
     * @param name Nombre completo.
     * @param username Nombre de usuario.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @param photoUrl URL de la foto de perfil.
     * @param contact Contacto.
     */
    private void registrarUsuario(String name, String username, String email, String password, String photoUrl, String contact) {
        ContentValues values = new ContentValues();
        values.put("nombre_completo", name);
        values.put("nombre_usuario", username);
        values.put("email", email);
        values.put("password", password);
        values.put("foto_perfil", photoUrl);
        values.put("tlfno", contact);

        UsuarioEntity.insertUsuarioEstado result = usuarios.insertar(values);
        int userId = usuarios.getUltimoUsuarioId();

        if (result == UsuarioEntity.insertUsuarioEstado.COMPLETADO) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("user_id", userId);
            editor.putString("username", username);
            editor.apply();

            Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (result == UsuarioEntity.insertUsuarioEstado.USUARIO_EXISTENTE) {
            Toast.makeText(this, "Usuario ya existente en la App", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
        }
    }
}
