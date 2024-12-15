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

public class ModifyProfileActivity extends AppCompatActivity {
    private static final String TAG = "ModifyProfileActivity";
    private EditText fullnameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText tlfnoEditText; // EditText para teléfono
    private EditText passwordEditText;
    private ImageView profileImageView;
    private ImageUploader imageUploader;
    private String deleteUrl = null;
    private String newImageUrl = null;

    private SharedPreferences sharedPreferences;
    private UsuarioEntity usuarios;

    private String currentImageUrl = null;
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String imagePath = getRealPathFromURI(uri);
                    if (imagePath != null) {
                        uploadImageToImgBB(imagePath);
                    } else {
                        Log.e(TAG, "Failed to get image path.");
                        Toast.makeText(this, "No se pudo obtener la ruta de la imagen.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
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
        profileImageView = findViewById(R.id.profile_image);
        CheckBox showPasswordCheckBox = findViewById(R.id.checkbox_show_password);
        Button saveChangesButton = findViewById(R.id.btn_save);
        Button cancelButton = findViewById(R.id.btn_cancel);
        Button changePhotoButton = findViewById(R.id.btn_replace_photo);
        imageUploader = new ImageUploader();

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

        changePhotoButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void loadProfileData() {
        int userId = sharedPreferences.getInt("user_id", -1);
        Log.d(TAG, "Loading profile data for userId: " + userId);

        if (userId == -1) {
            Log.e(TAG, "Invalid userId.");
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
            currentImageUrl = cursor.getString(cursor.getColumnIndexOrThrow("foto_perfil")); // Cargar teléfono
            deleteUrl = cursor.getString(cursor.getColumnIndexOrThrow("delete_url"));
            Log.d(TAG, "Profile loaded: fullname=" + fullname + ", username=" + username + ", email=" + email);

            fullnameEditText.setText(fullname);
            usernameEditText.setText(username);
            emailEditText.setText(email);
            tlfnoEditText.setText(tlfno); // Mostrar teléfono en EditText
            passwordEditText.setText(password);

            if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(currentImageUrl)
                        .placeholder(R.drawable.ic_profile_default) // Imagen de carga predeterminada// Imagen en caso de error// Recorte en forma de círculo
                        .into(profileImageView); // Tu ImageView para la foto de perfil
            } else {
                profileImageView.setImageResource(R.drawable.ic_profile_default); // Imagen por defecto
            }
            cursor.close();
        } else {
            Log.e(TAG, "Failed to load profile data.");
            Toast.makeText(ModifyProfileActivity.this, this.getString(R.string.error_profile_charging), Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadImageToImgBB(String imagePath) {
        Log.d(TAG, "Uploading image to ImgBB: " + imagePath);
        imageUploader.uploadImage(imagePath, new ImageUploader.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl, String deleteUrl) {
                if (imageUrl != null && deleteUrl != null) {
                    updateProfileImage(imageUrl);
                } else {
                    Toast.makeText(ModifyProfileActivity.this, "Error: No se recibió la URL de la imagen o deleteUrl.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error uploading image: " + error);
                Toast.makeText(ModifyProfileActivity.this, "Error al subir la imagen: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileImage(String imageUrl) {
        Log.d(TAG, "Updating profile image: imageUrl=" + imageUrl + ", deleteUrl=" + deleteUrl);

        currentImageUrl = imageUrl;

        // Mostrar la nueva imagen
        Glide.with(this).load(currentImageUrl).into(profileImageView);
        Toast.makeText(this, "Imagen de perfil actualizada.", Toast.LENGTH_SHORT).show();
    }

    private void saveProfileChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.confirm_modifications))
                .setMessage(this.getString(R.string.modifications_message))
                .setPositiveButton(this.getString(R.string.yes), (dialog, which) -> {
                    Log.d(TAG, "Saving profile changes.");
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

    private String getRealPathFromURI(Uri contentUri) {
        try {
            // Crear un archivo temporal en el almacenamiento externo
            File tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(contentUri);
            OutputStream outputStream = Files.newOutputStream(tempFile.toPath());

            // Copiar los datos del InputStream al archivo temporal
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return tempFile.getAbsolutePath(); // Ruta del archivo temporal
        } catch (Exception e) {
            return null;
        }
    }

}
