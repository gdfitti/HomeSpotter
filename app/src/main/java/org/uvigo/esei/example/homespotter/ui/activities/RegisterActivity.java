package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private Uri selectedImageUri; // Uri de la imagen seleccionada
    private UsuarioEntity usuarios;

    private EditText fullName, userName, email, password, contact;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    manejarSeleccionImagen(uri); // Maneja la imagen seleccionada
                }
            });

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
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = fullName.getText().toString().trim();
                String username = userName.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();
                String userContact = contact.getText().toString().trim();

                if (validateInputs(name, username, userEmail, userPassword)) {
                    if (selectedImageUri != null) {
                        subirImagenAImgBB(name, username, userEmail, userPassword, userContact);
                    } else {
                        registrarUsuario(name, username, userEmail, userPassword,null, userContact);
                    }
                }
            }
        });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showCancelDialog();
                    }
                });
    }

    private void showCancelDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.cancel))
                .setMessage(this.getString(R.string.sure_cancel));
        builder.setPositiveButton(this.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(this.getString(R.string.no),null);
        builder.create().show();
    }

    private void manejarSeleccionImagen(Uri uri) {
        profileImageView.setImageURI(uri); // Establece la URI seleccionada como imagen en el ImageView
        selectedImageUri = uri; // Guarda el Uri seleccionado
    }

    private boolean validateInputs(String name, String username, String email, String password) {
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void subirImagenAImgBB(String name, String username, String email, String password, String contact) {
        try {
            File file = getFileFromUri(selectedImageUri);
            ImageUploader imageUploader = new ImageUploader();

            imageUploader.uploadImage(file.getAbsolutePath(), new ImageUploader.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl, String deleteUrl) {
                    // Guardar el enlace generado por ImgBB en la base de datos
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

    private void registrarUsuario(String name, String username, String email, String password, String photoUrl, String contact) {
        ContentValues values = new ContentValues();
        values.put("nombre_completo", name);
        values.put("nombre_usuario", username);
        values.put("email", email);
        values.put("password", password);
        values.put("foto_perfil", photoUrl); // Guardar la URL de la imagen
        values.put("tlfno", contact);

        UsuarioEntity.insertUsuarioEstado result = usuarios.insertar(values);

        if (result == UsuarioEntity.insertUsuarioEstado.COMPLETADO) {
            Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (result == UsuarioEntity.insertUsuarioEstado.USUARIO_EXISTENTE) {
            Toast.makeText(this, "Usuario ya existente en la App", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
        }
    }
}
