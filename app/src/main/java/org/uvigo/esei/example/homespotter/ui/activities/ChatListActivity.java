package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.MensajesEntity;
import org.uvigo.esei.example.homespotter.database.UsuarioEntity;
import org.uvigo.esei.example.homespotter.models.Chat;
import org.uvigo.esei.example.homespotter.models.Usuario;
import org.uvigo.esei.example.homespotter.ui.adapters.ChatAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ChatListActivity
 *
 * Actividad que muestra una lista de chats asociados al usuario actual.
 * Recupera los mensajes de la base de datos y los organiza en chats,
 * mostrando el nombre, foto y último mensaje de cada conversación.
 */
public class ChatListActivity extends BaseActivity {

    // RecyclerView que muestra la lista de chats
    private RecyclerView recyclerView;

    // Adaptador para manejar los datos de los chats
    private ChatAdapter adapter;

    // Lista de chats a mostrar
    private List<Chat> chats;

    // Entidades para interactuar con la base de datos
    private MensajesEntity mensajesEntity;
    private UsuarioEntity usuarioEntity;

    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz gráfica y recupera los chats del usuario.
     *
     * @param savedInstanceState Estado guardado previamente (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBottomNavigationIcon(R.id.nav_messages, R.drawable.ic_message_selected);

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Texto mostrado si no hay chats disponibles
        TextView noChatsMessage = findViewById(R.id.noChatsMessage);

        // Inicializar las entidades de base de datos
        mensajesEntity = new MensajesEntity(DBManager.getInstance(this).getWritableDatabase());
        usuarioEntity = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());

        // Obtener el ID del usuario actual desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int usuarioId = sharedPreferences.getInt("user_id", -1);

        // Recuperar los chats del usuario
        List<Chat> chats = obtenerChats(usuarioId);

        // Mostrar un mensaje si no hay chats, o mostrar la lista si los hay
        if (chats.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noChatsMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noChatsMessage.setVisibility(View.GONE);
        }

        // Configurar el adaptador para el RecyclerView
        adapter = new ChatAdapter(chats, chat -> {
            // Configurar la acción al seleccionar un chat
            Intent intent = new Intent(ChatListActivity.this, MensajeActivity.class);
            intent.putExtra("chat_id", chat.getIdChat());
            intent.putExtra("chat_user_name", chat.getNombreUsuario());
            intent.putExtra("chat_photo", chat.getFotoUsuario());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * Recupera los chats asociados al usuario actual.
     *
     * @param usuarioId ID del usuario actual.
     * @return Lista de chats del usuario.
     */
    public List<Chat> obtenerChats(int usuarioId) {
        List<Chat> chats = new ArrayList<>();
        Set<Integer> remitentes = new HashSet<>();

        // Obtener los mensajes asociados al usuario
        Cursor cursorMensajes = mensajesEntity.obtenerMensajes(usuarioId);

        if (cursorMensajes != null && cursorMensajes.moveToFirst()) {
            do {
                int columnIndexRem = cursorMensajes.getColumnIndex("remitente_id");
                int columnIndexDes = cursorMensajes.getColumnIndex("destinatario_id");

                // Procesar remitentes
                if (columnIndexRem != -1 && cursorMensajes.getInt(columnIndexRem) != usuarioId) {
                    int remitenteId = cursorMensajes.getInt(columnIndexRem);

                    if (!remitentes.contains(remitenteId)) {
                        remitentes.add(remitenteId);

                        // Obtener datos del remitente
                        Usuario remitente = usuarioEntity.obtenerUsuarioPorId(remitenteId);
                        String nombreUsuario = remitente.getNombre();
                        String fotoUsuario = remitente.getFoto();

                        // Obtener información del último mensaje
                        String[] datosUltimoMensaje = obtenerUltimoMensaje(remitenteId, usuarioId);

                        // Crear un objeto Chat y añadirlo a la lista
                        Chat chat = new Chat(remitenteId, nombreUsuario, datosUltimoMensaje[0], datosUltimoMensaje[1], fotoUsuario);
                        chats.add(chat);
                    }
                }
                // Procesar destinatarios
                else if (columnIndexDes != -1 && cursorMensajes.getInt(columnIndexRem) == usuarioId) {
                    int destinatarioId = cursorMensajes.getInt(columnIndexDes);

                    if (!remitentes.contains(destinatarioId)) {
                        remitentes.add(destinatarioId);

                        // Obtener datos del destinatario
                        Usuario destinatario = usuarioEntity.obtenerUsuarioPorId(destinatarioId);
                        String nombreUsuario = destinatario.getNombre();
                        String fotoUsuario = destinatario.getFoto();

                        // Obtener información del último mensaje
                        String[] datosUltimoMensaje = obtenerUltimoMensaje(usuarioId, destinatarioId);

                        // Crear un objeto Chat y añadirlo a la lista
                        Chat chat = new Chat(destinatarioId, nombreUsuario, datosUltimoMensaje[0], datosUltimoMensaje[1], fotoUsuario);
                        chats.add(chat);
                    }
                }
            } while (cursorMensajes.moveToNext());
            cursorMensajes.close();
        } else {
            Log.e("ChatListActivity", "No se encontraron mensajes para el usuario " + usuarioId);
        }

        return chats;
    }

    /**
     * Recupera el contenido y la fecha del último mensaje entre dos usuarios.
     *
     * @param remitenteId ID del remitente.
     * @param destinatarioId ID del destinatario.
     * @return Array con el contenido y la fecha del último mensaje.
     */
    private String[] obtenerUltimoMensaje(int remitenteId, int destinatarioId) {
        Cursor cursorUltimoMensaje = mensajesEntity.obtenerUltimoMensaje(remitenteId, destinatarioId);
        String fechaUltimoMensaje = null;
        String contenidoUltimoMensaje = "...";

        if (cursorUltimoMensaje != null && cursorUltimoMensaje.moveToFirst()) {
            int columnIndexFecha = cursorUltimoMensaje.getColumnIndex("ultima_fecha");
            int columnIndexContenido = cursorUltimoMensaje.getColumnIndex("contenido");

            if (columnIndexFecha != -1) {
                fechaUltimoMensaje = cursorUltimoMensaje.getString(columnIndexFecha);
            }

            if (columnIndexContenido != -1) {
                contenidoUltimoMensaje = cursorUltimoMensaje.getString(columnIndexContenido);
            }

            cursorUltimoMensaje.close();
        }

        return new String[]{contenidoUltimoMensaje, fechaUltimoMensaje};
    }

    /**
     * Especifica el diseño asociado a esta actividad.
     *
     * @return ID del recurso de diseño de la actividad.
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_chat_list;
    }
}
