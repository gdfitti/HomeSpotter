package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.UsuarioEntity;
import org.uvigo.esei.example.homespotter.imgbb.ImageUploader;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * ModifyProfileActivity
 *
 * Esta actividad permite al usuario modificar su perfil, incluyendo nombre, usuario,
 * correo electrónico, contraseña, teléfono y foto de perfil.
 *
 * Funcionalidades principales:
 * - Cargar los datos del perfil desde la base de datos.
 * - Permitir la edición y actualización de los datos del perfil.
 * - Subir una nueva imagen de perfil usando ImgBB.
 */
public class ModifyProfileActivity extends AppCompatActivity {
    private static final String TAG = "ModifyProfileActivity";

    // Elementos de la interfaz de usuario
    private EditText fullnameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText tlfnoEditText; // EditText para teléfono
    private EditText passwordEditText;
    private ImageView profileImageView;

    // Clase para manejar la subida de imágenes
    private ImageUploader imageUploader;

    // URL de la imagen actual y nueva
    private String deleteUrl = null;
    private String newImageUrl = null;
    private String currentImageUrl = null;

    // Entidades de base de datos y preferencias compartidas
    private SharedPreferences sharedPreferences;
    private UsuarioEntity usuarios;

    // ActivityResultLauncher para seleccionar imágenes
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String imagePath = getRealPathFromURI(uri);
                    if (imagePath != null) {
                        uploadImageToImgBB(imagePath);
                    } else {
                        Log.e(TAG, "Failed to get image path.");
                        Toast.makeText(this, this.getString(R.string.error_image_path), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz, carga los datos del perfil y establece los listeners de los botones.
     *
     * @param savedInstanceState Estado guardado previamente (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        // Inicializar los elementos de la UI
        fullnameEditText = findViewById(R.id.fullname);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        tlfnoEditText = findViewById(R.id.tlfno);
        passwordEditText = findViewById(R.id.password);
        profileImageView = findViewById(R.id.profile_image);

        CheckBox showPasswordCheckBox = findViewById(R.id.checkbox_show_password);
        Button saveChangesButton = findViewById(R.id.btn_save);
        Button cancelButton = findViewById(R.id.btn_cancel);
        Button changePhotoButton = findViewById(R.id.btn_replace_photo);

        imageUploader = new ImageUploader();

        // Inicializar SharedPreferences y base de datos
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        usuarios = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());

        // Cargar los datos del perfil
        loadProfileData();

        // Listener para guardar los cambios
        saveChangesButton.setOnClickListener(v -> saveProfileChanges());

        // Listener para cancelar y cerrar la actividad
        cancelButton.setOnClickListener(v -> finish());

        // Listener para mostrar/ocultar contraseña
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            passwordEditText.setSelection(passwordEditText.getText().length());
        });

        // Listener para cambiar la foto de perfil
        changePhotoButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    /**
     * Carga los datos del perfil desde la base de datos y los muestra en la interfaz.
     */
    private void loadProfileData() {
        int userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Log.e(TAG, "Invalid userId.");
            return;
        }

        ContentValues filters = new ContentValues();
        filters.put("id_usuario", userId);
        Cursor cursor = usuarios.buscar(filters);

        if (cursor != null && cursor.moveToFirst()) {
            fullnameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre_completo")));
            usernameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre_usuario")));
            emailEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            tlfnoEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("tlfno")));
            passwordEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            currentImageUrl = cursor.getString(cursor.getColumnIndexOrThrow("foto_perfil"));

            if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(currentImageUrl)
                        .placeholder(R.drawable.ic_profile_default)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile_default);
            }
            cursor.close();
        } else {
            Toast.makeText(this, this.getString(R.string.error_profile_charging), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sube la imagen seleccionada a ImgBB.
     *
     * @param imagePath Ruta de la imagen seleccionada.
     */
    private void uploadImageToImgBB(String imagePath) {
        imageUploader.uploadImage(imagePath, new ImageUploader.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl, String deleteUrl) {
                updateProfileImage(imageUrl);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ModifyProfileActivity.this, "Error al suvir la foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Actualiza la imagen de perfil en la interfaz.
     *
     * @param imageUrl URL de la nueva imagen.
     */
    private void updateProfileImage(String imageUrl) {
        currentImageUrl = imageUrl;
        Glide.with(this).load(currentImageUrl).into(profileImageView);
        Toast.makeText(this, this.getString(R.string.profile_image_updated), Toast.LENGTH_SHORT).show();
    }

    /**
     * Guarda los cambios realizados en el perfil en la base de datos.
     */
    private void saveProfileChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.confirm_modifications))
                .setMessage(this.getString(R.string.modifications_message))
                .setPositiveButton(this.getString(R.string.yes), (dialog, which) -> {
                    String newFullname = fullnameEditText.getText().toString().trim();
                    String newUsername = usernameEditText.getText().toString().trim();
                    String newEmail = emailEditText.getText().toString().trim();
                    String newPassword = passwordEditText.getText().toString().trim();
                    String newTlfno = tlfnoEditText.getText().toString().trim();

                    if (newFullname.isEmpty() || newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || newTlfno.isEmpty()) {
                        Toast.makeText(ModifyProfileActivity.this, this.getString(R.string.fill), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int userId = sharedPreferences.getInt("user_id", -1);
                    if (userId != -1) {
                        boolean isUpdated = usuarios.modificar(
                                userId,
                                newUsername,
                                newFullname,
                                newEmail,
                                newPassword,
                                newImageUrl != null ? newImageUrl : currentImageUrl,
                                newTlfno
                        );

                        if (isUpdated) {
                            Toast.makeText(ModifyProfileActivity.this, this.getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(ModifyProfileActivity.this, this.getString(R.string.error_profile_update), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(this.getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Obtiene la ruta real de un URI de contenido.
     *
     * @param contentUri URI del contenido.
     * @return Ruta absoluta del archivo, o null si falla.
     */
    private String getRealPathFromURI(Uri contentUri) {
        try {
            File tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(contentUri);
            OutputStream outputStream = Files.newOutputStream(tempFile.toPath());

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }
}
