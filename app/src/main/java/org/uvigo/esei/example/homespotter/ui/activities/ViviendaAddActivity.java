package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.FotosEntity;
import org.uvigo.esei.example.homespotter.database.ViviendaEntity;
import org.uvigo.esei.example.homespotter.imgbb.ImageUploader;
import org.uvigo.esei.example.homespotter.ui.adapters.PhotoAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ViviendaAddActivity extends AppCompatActivity {
    private ViviendaEntity viviendaEntity;
    private FotosEntity fotosEntity;
    private final List<Uri> photoUris = new ArrayList<>();
    private PhotoAdapter photoAdapter;
    private int userId; // ID del usuario propietario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vivienda_add);

        // Configurar la base de datos
        viviendaEntity = new ViviendaEntity(DBManager.getInstance(this).getWritableDatabase());
        fotosEntity = new FotosEntity(DBManager.getInstance(this).getWritableDatabase());

        // Obtener el ID del usuario
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar campos de entrada
        EditText inputTitle = findViewById(R.id.input_title);
        EditText inputPrice = findViewById(R.id.input_price);
        EditText inputAddress = findViewById(R.id.input_address);
        EditText inputContact = findViewById(R.id.input_contact);
        EditText inputDescription = findViewById(R.id.input_description);
        Spinner spinnerTipoVivienda = findViewById(R.id.spinner_property_type);
        Spinner spinnerEstadoVivienda = findViewById(R.id.spinner_property_state);

        // Cargar opciones desde strings.xml para tipo de vivienda
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this,
                R.array.property_types,
                android.R.layout.simple_spinner_item
        );
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoVivienda.setAdapter(adapterTipo);

// Cargar opciones desde strings.xml para estado de vivienda
        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                this,
                R.array.property_state,
                android.R.layout.simple_spinner_item
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstadoVivienda.setAdapter(adapterEstado);

        // Configurar RecyclerView para las fotos
        RecyclerView recyclerView = findViewById(R.id.recycler_photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photoAdapter = new PhotoAdapter(photoUris);
        recyclerView.setAdapter(photoAdapter);

        // Lanzador para seleccionar imágenes
        ActivityResultLauncher<String[]> photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        int oldSize = photoUris.size();
                        photoUris.addAll(uris);
                        photoAdapter.notifyItemRangeInserted(oldSize, uris.size());
                    }
                }
        );

        // Botón para añadir fotos
        Button addPhotosButton = findViewById(R.id.btn_add_photos);
        addPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoPickerLauncher.launch(new String[]{"image/*"});
            }
        });

        // Botón para guardar la vivienda
        Button saveButton = findViewById(R.id.btn_save_property);
        saveButton.setOnClickListener(v -> {
            String title = inputTitle.getText().toString().trim();
            String type = spinnerTipoVivienda.getSelectedItem().toString();
            String priceStr = inputPrice.getText().toString().trim();
            String address = inputAddress.getText().toString().trim();
            String state = spinnerEstadoVivienda.getSelectedItem().toString();
            String contact = inputContact.getText().toString().trim();
            String description = inputDescription.getText().toString().trim();

            if (title.isEmpty() || type.isEmpty() || priceStr.isEmpty() || address.isEmpty() || state.isEmpty() || contact.isEmpty() || photoUris.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos y añade al menos una foto.", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Introduce un precio válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insertar la vivienda en la base de datos
            boolean success = viviendaEntity.insertar(title, type, price, address, state, contact, description, userId);
            if (success) {
                int viviendaId = viviendaEntity.obtenerUltimaVivienda();

                // Subir fotos y esperar a que todas estén subidas antes de finalizar
                uploadPhotosToImgBB(viviendaId, () -> {
                    Toast.makeText(ViviendaAddActivity.this, "Vivienda y fotos añadidas con éxito.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } else {
                Toast.makeText(this, "Error al añadir la vivienda. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para cancelar
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViviendaAddActivity.this);
                builder.setTitle(ViviendaAddActivity.this.getString(R.string.cancel))
                        .setMessage(ViviendaAddActivity.this.getString(R.string.sure_cancel))
                        .setPositiveButton(ViviendaAddActivity.this.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                builder.setNegativeButton(ViviendaAddActivity.this.getString(R.string.no), null);
                builder.create().show();
            }
        });
    }

    private void uploadPhotosToImgBB(int viviendaId, Runnable onComplete) {
        ImageUploader imageUploader = new ImageUploader();
        int totalFotos = photoUris.size();
        int[] fotosSubidas = {0};

        // Mostrar el ProgressBar
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        for (Uri uri : photoUris) {
            try {
                File file = getFileFromUri(uri);
                imageUploader.uploadImage(file.getAbsolutePath(), new ImageUploader.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl, String deleteUrl) {
                        fotosEntity.insertar(viviendaId, imageUrl); // Guarda el enlace en la base de datos
                        fotosSubidas[0]++;

                        // Si todas las fotos han sido subidas, oculta el ProgressBar y ejecuta el callback
                        if (fotosSubidas[0] == totalFotos) {
                            progressBar.setVisibility(View.GONE);
                            onComplete.run();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ViviendaAddActivity.this, "Error al subir una foto: " + error, Toast.LENGTH_SHORT).show();
                        Log.e("ViviendaAddActivity.uploadPhotosToImgBB","Error al subir una(s) foto(s) al servidor: "+error);
                    }
                });
            } catch (IOException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error al procesar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File getFileFromUri(Uri uri) throws IOException {
        // Crear un archivo temporal
        File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
        tempFile.deleteOnExit();

        // Usar ContentResolver para leer los datos del Uri
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
}
