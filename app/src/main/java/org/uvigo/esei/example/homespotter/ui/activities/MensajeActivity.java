package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.MensajesEntity;
import org.uvigo.esei.example.homespotter.models.Mensaje;
import org.uvigo.esei.example.homespotter.ui.adapters.MensajeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * MensajeActivity
 *
 * Actividad para gestionar la visualización y el envío de mensajes entre dos usuarios.
 * Permite ver el historial de mensajes, enviar nuevos mensajes y manejar eventos en la interfaz.
 */
public class MensajeActivity extends BaseActivity {

    // Elementos de la interfaz de usuario
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;

    // Adaptador para gestionar la lista de mensajes
    private MensajeAdapter adapter;

    // Lista de mensajes a mostrar
    private List<Mensaje> mensajes;

    // Entidad para interactuar con la base de datos de mensajes
    private MensajesEntity mensajesEntity;

    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz, inicializa la lista de mensajes y establece los eventos de clic.
     *
     * @param savedInstanceState Estado guardado previamente (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBottomNavigationIcon(R.id.nav_messages, R.drawable.ic_message_selected);

        // Inicializar elementos de la interfaz
        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Configurar RecyclerView con un adaptador
        mensajes = new ArrayList<>();
        adapter = new MensajeAdapter(mensajes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Inicializar la entidad para manejar mensajes
        mensajesEntity = new MensajesEntity(DBManager.getInstance(this).getWritableDatabase());

        // Cargar mensajes existentes
        cargarMensajes();

        // Configurar el botón de envío
        buttonSend.setOnClickListener(v -> {
            String contenido = editTextMessage.getText().toString();
            if (!contenido.isEmpty()) {
                // Obtener IDs del remitente y destinatario
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                int remitenteId = sharedPreferences.getInt("user_id", -1);
                int destinatarioId = getIntent().getIntExtra("chat_id", -1);

                if (destinatarioId > 0 && remitenteId > 0) {
                    // Insertar mensaje en la base de datos y recargar mensajes
                    mensajesEntity.insertar(remitenteId, destinatarioId, contenido);
                    editTextMessage.setText("");
                    cargarMensajes();
                }
            }
        });

        // Configurar la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_messages);
        setSupportActionBar(toolbar);

        // Configurar el botón "Atrás" en la Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        // Configurar el nombre y la foto del destinatario
        TextView username = findViewById(R.id.username_messages);
        ImageView photo = findViewById(R.id.photo_messages);

        username.setText(getIntent().getStringExtra("chat_user_name"));
        String imageUrl = getIntent().getStringExtra("chat_photo");
        Glide.with(this).load(imageUrl).into(photo);
    }

    /**
     * Carga los mensajes entre el usuario actual y el destinatario.
     * Actualiza la lista de mensajes y notifica al adaptador.
     */
    private void cargarMensajes() {
        // Obtener ID del usuario actual desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("user_id", -1);

        // Obtener ID del destinatario desde el Intent
        int destinatarioId = getIntent().getIntExtra("chat_id", -1);

        // Obtener mensajes desde la base de datos
        Cursor cursor = mensajesEntity.obtenerMensajesEntreUsuarios(currentUserId, destinatarioId);

        if (cursor != null) {
            mensajes.clear();
            while (cursor.moveToNext()) {
                // Crear objeto Mensaje a partir de los datos del cursor
                int id_mensaje = cursor.getInt(cursor.getColumnIndexOrThrow("id_mensaje"));
                int remitente_id = cursor.getInt(cursor.getColumnIndexOrThrow("remitente_id"));
                int destinatario_id = cursor.getInt(cursor.getColumnIndexOrThrow("destinatario_id"));
                String contenido = cursor.getString(cursor.getColumnIndexOrThrow("contenido"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                boolean leido = cursor.getInt(cursor.getColumnIndexOrThrow("leido")) == 1;

                mensajes.add(new Mensaje(id_mensaje, remitente_id, destinatario_id, contenido, fecha, leido));
            }
            cursor.close();

            // Configurar el ID del usuario actual en el adaptador y notificar cambios
            adapter.setCurrentUserId(currentUserId);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Devuelve el diseño asociado a esta actividad.
     *
     * @return ID del recurso de diseño de la actividad.
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_messages;
    }

    /**
     * Maneja el evento de selección de opciones en el menú.
     *
     * @param item Elemento seleccionado del menú.
     * @return true si se maneja correctamente, false en caso contrario.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Regresar a la actividad anterior
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
