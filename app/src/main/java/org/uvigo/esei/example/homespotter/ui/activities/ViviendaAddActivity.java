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

/**
 * ViviendaAddActivity
 *
 * Esta actividad permite a los usuarios añadir una nueva vivienda a la base de datos.
 * Los usuarios pueden ingresar detalles de la vivienda, seleccionar fotos y guardar la vivienda.
 */
public class ViviendaAddActivity extends AppCompatActivity {
    private ViviendaEntity viviendaEntity; // Entidad para manejar la tabla de viviendas
    private FotosEntity fotosEntity; // Entidad para manejar la tabla de fotos
    private final List<Uri> photoUris = new ArrayList<>(); // Lista de URIs de las fotos seleccionadas
    private PhotoAdapter photoAdapter; // Adaptador para mostrar las fotos seleccionadas
    private int userId; // ID del usuario propietario de la vivienda

    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz de usuario y establece los listeners para las acciones.
     *
     * @param savedInstanceState Estado guardado previamente (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vivienda_add);

        // Inicializar entidades de base de datos
        viviendaEntity = new ViviendaEntity(DBManager.getInstance(this).getWritableDatabase());
        fotosEntity = new FotosEntity(DBManager.getInstance(this).getWritableDatabase());

        // Obtener el ID del usuario desde el Intent
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

        // Configurar adaptadores para los Spinners
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(
                this,
                R.array.property_types,
                android.R.layout.simple_spinner_item
        );
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoVivienda.setAdapter(adapterTipo);

        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(
                this,
                R.array.property_state,
                android.R.layout.simple_spinner_item
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstadoVivienda.setAdapter(adapterEstado);

        // Configurar RecyclerView para mostrar las fotos seleccionadas
        RecyclerView recyclerView = findViewById(R.id.recycler_photos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photoAdapter = new PhotoAdapter(photoUris);
        recyclerView.setAdapter(photoAdapter);

        // Lanzador para seleccionar múltiples fotos
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

        // Listener para el botón de añadir fotos
        Button addPhotosButton = findViewById(R.id.btn_add_photos);
        addPhotosButton.setOnClickListener(v -> photoPickerLauncher.launch(new String[]{"image/*"}));

        // Listener para el botón de guardar vivienda
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

                // Subir las fotos y guardar los enlaces en la base de datos
                uploadPhotosToImgBB(viviendaId, () -> {
                    Toast.makeText(ViviendaAddActivity.this, "Vivienda y fotos añadidas con éxito.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } else {
                Toast.makeText(this, "Error al añadir la vivienda. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });

        // Listener para el botón de cancelar
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(this.getString(R.string.cancel))
                    .setMessage(this.getString(R.string.sure_cancel))
                    .setPositiveButton(this.getString(R.string.yes), (dialogInterface, i) -> finish())
                    .setNegativeButton(this.getString(R.string.no), null)
                    .create().show();
        });
    }

    /**
     * Sube las fotos seleccionadas a ImgBB y guarda los enlaces en la base de datos.
     *
     * @param viviendaId ID de la vivienda asociada.
     * @param onComplete Callback que se ejecuta cuando todas las fotos han sido subidas.
     */
    private void uploadPhotosToImgBB(int viviendaId, Runnable onComplete) {
        ImageUploader imageUploader = new ImageUploader();
        int totalFotos = photoUris.size();
        int[] fotosSubidas = {0};

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        for (Uri uri : photoUris) {
            try {
                File file = getFileFromUri(uri);
                imageUploader.uploadImage(file.getAbsolutePath(), new ImageUploader.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl, String deleteUrl) {
                        fotosEntity.insertar(viviendaId, imageUrl);
                        fotosSubidas[0]++;
                        if (fotosSubidas[0] == totalFotos) {
                            progressBar.setVisibility(View.GONE);
                            onComplete.run();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ViviendaAddActivity.this, "Error al subir una foto: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Error al procesar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Convierte un URI en un archivo temporal.
     *
     * @param uri URI de la imagen.
     * @return Archivo temporal con los datos de la imagen.
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
}
