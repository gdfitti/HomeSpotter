package org.uvigo.esei.example.homespotter.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.MensajesEntity;
import org.uvigo.esei.example.homespotter.models.Mensaje;
import org.uvigo.esei.example.homespotter.ui.adapters.MensajeAdapter;

import java.util.ArrayList;
import java.util.List;

public class MensajeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private Button buttonSend;
    private MensajeAdapter adapter;
    private List<Mensaje> mensajes;

    private MensajesEntity mensajesEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        mensajes = new ArrayList<>();
        adapter = new MensajeAdapter(mensajes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        mensajesEntity = new MensajesEntity(DBManager.getInstance(this).getWritableDatabase());
        cargarMensajes();

        buttonSend.setOnClickListener(v -> {
            String contenido = editTextMessage.getText().toString();
            if (!contenido.isEmpty()) {
                int remitenteId = 1; // Cambia por el ID real del usuario
                int destinatarioId = 2; // Cambia por el ID real del destinatario

                mensajesEntity.insertar(remitenteId, destinatarioId, contenido);
                editTextMessage.setText("");
                cargarMensajes();
            }
        });
    }

    private void cargarMensajes() {
        Cursor cursor = mensajesEntity.obtenerMensajes(2); // Cambia por el ID del destinatario
        if (cursor != null) {
            mensajes.clear();
            while (cursor.moveToNext()) {
                int id_mensaje = cursor.getInt(cursor.getColumnIndexOrThrow("id_mensaje"));
                int remitente_id = cursor.getInt(cursor.getColumnIndexOrThrow("remitente_id"));
                int destinatario_id = cursor.getInt(cursor.getColumnIndexOrThrow("destinatario_id"));
                String contenido = cursor.getString(cursor.getColumnIndexOrThrow("contenido"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                boolean leido = cursor.getInt(cursor.getColumnIndexOrThrow("leido")) == 1;
                mensajes.add(new Mensaje(id_mensaje, remitente_id, destinatario_id, contenido, fecha, leido));
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }
}

// cambiar 1 y 2 por funciones dinámicas que identifiquen tanto al remitente como al destinatario

