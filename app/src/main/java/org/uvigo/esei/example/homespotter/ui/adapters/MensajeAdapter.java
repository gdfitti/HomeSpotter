package org.uvigo.esei.example.homespotter.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.models.Mensaje;

import java.util.List;

/**
 * MensajeAdapter
 *
 * Adaptador para un RecyclerView que muestra una lista de mensajes en un chat.
 * Distingue entre mensajes enviados y recibidos para aplicar diferentes diseños.
 */
public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {
    private List<Mensaje> mensajes; // Lista de mensajes a mostrar
    private int currentUserId; // ID del usuario actual para diferenciar mensajes enviados y recibidos

    /**
     * Constructor del adaptador.
     *
     * @param mensajes Lista de mensajes a mostrar.
     */
    public MensajeAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    /**
     * Establece el ID del usuario actual.
     *
     * @param userId ID del usuario actual.
     */
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    /**
     * Determina el tipo de vista para un mensaje según su remitente.
     *
     * @param position Posición del mensaje en la lista.
     * @return 1 si el mensaje fue enviado por el usuario actual, 2 si fue recibido.
     */
    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = mensajes.get(position);
        return mensaje.getRemitenteId() == currentUserId ? 1 : 2; // 1: enviado, 2: recibido
    }

    /**
     * Crea una nueva vista para un elemento del RecyclerView.
     *
     * @param parent Vista padre.
     * @param viewType Tipo de vista (1 para enviado, 2 para recibido).
     * @return Un nuevo ViewHolder con la vista inflada.
     */
    @Override
    public MensajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_mensaje_enviado : R.layout.item_mensaje_recibido;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MensajeViewHolder(itemView);
    }

    /**
     * Vincula los datos de un mensaje a su vista.
     *
     * @param holder ViewHolder donde se mostrará el mensaje.
     * @param position Posición del mensaje en la lista.
     */
    @Override
    public void onBindViewHolder(MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.bind(mensaje);
    }

    /**
     * Devuelve el número de elementos en la lista de mensajes.
     *
     * @return Cantidad de mensajes.
     */
    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    /**
     * ViewHolder para representar un mensaje en el RecyclerView.
     * Puede ser un mensaje enviado o recibido según el diseño inflado.
     */
    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewContenido; // Texto del contenido del mensaje
        private TextView textViewFecha; // Texto de la fecha del mensaje

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista del mensaje.
         */
        public MensajeViewHolder(View itemView) {
            super(itemView);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
        }

        /**
         * Vincula los datos de un mensaje a la vista.
         *
         * @param mensaje Objeto Mensaje con los datos a mostrar.
         */
        public void bind(Mensaje mensaje) {
            textViewContenido.setText(mensaje.getContenido());
            textViewFecha.setText(mensaje.getFecha());
        }
    }
}
