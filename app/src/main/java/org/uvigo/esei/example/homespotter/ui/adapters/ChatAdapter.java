package org.uvigo.esei.example.homespotter.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.models.Chat;

import java.util.List;

/**
 * ChatAdapter
 *
 * Adaptador para un RecyclerView que muestra una lista de chats.
 * Cada elemento incluye el nombre del usuario, el último mensaje, la fecha y su foto de perfil.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    // Lista de chats a mostrar
    private List<Chat> chats;

    // Listener para manejar clics en los chats
    private OnChatClickListener listener;

    /**
     * Interfaz para manejar los clics en los elementos de la lista de chats.
     */
    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    /**
     * Constructor del adaptador.
     *
     * @param chats Lista de chats.
     * @param listener Listener para manejar los clics en los chats.
     */
    public ChatAdapter(List<Chat> chats, OnChatClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    /**
     * Crea una nueva vista para un elemento de la lista de chats.
     *
     * @param parent Vista padre.
     * @param viewType Tipo de vista (no se usa en este caso).
     * @return Un nuevo ViewHolder para la vista del chat.
     */
    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    /**
     * Vincula un chat a un ViewHolder, configurando sus datos y eventos.
     *
     * @param holder ViewHolder del chat.
     * @param position Posición del chat en la lista.
     */
    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat, listener);
    }

    /**
     * Devuelve el número de elementos en la lista de chats.
     *
     * @return Cantidad de chats.
     */
    @Override
    public int getItemCount() {
        return chats.size();
    }

    /**
     * ViewHolder para representar un chat en el RecyclerView.
     */
    static class ChatViewHolder extends RecyclerView.ViewHolder {

        // Elementos de la vista del chat
        private TextView textViewUserName, textViewLastMessage, textViewDate;
        private ImageView imageViewFoto;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista del chat.
         */
        public ChatViewHolder(View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textViewChatUserName);
            textViewLastMessage = itemView.findViewById(R.id.textViewChatLastMessage);
            textViewDate = itemView.findViewById(R.id.textViewChatDate);
            imageViewFoto = itemView.findViewById(R.id.imageViewChatAvatar);
        }

        /**
         * Vincula los datos de un chat a la vista.
         *
         * @param chat Objeto Chat con los datos.
         * @param listener Listener para manejar clics en el chat.
         */
        public void bind(Chat chat, OnChatClickListener listener) {
            textViewUserName.setText(chat.getNombreUsuario());
            textViewLastMessage.setText(chat.getUltimoMensaje());
            textViewDate.setText(chat.getFechaUltimoMensaje());

            // Cargar la foto de perfil con Glide
            Glide.with(itemView.getContext())
                    .load(chat.getFotoUsuario())
                    .placeholder(R.drawable.ic_profile_default) // Imagen por defecto si falla la carga
                    .into(imageViewFoto);

            // Configurar el clic en el elemento del chat
            itemView.setOnClickListener(v -> listener.onChatClick(chat));
        }
    }
}
