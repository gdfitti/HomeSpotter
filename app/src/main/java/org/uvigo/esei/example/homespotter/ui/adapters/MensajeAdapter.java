package org.uvigo.esei.example.homespotter.ui.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.models.Mensaje;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {
    private List<Mensaje> mensajes;

    public MensajeAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public MensajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.bind(mensaje);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewContenido;
        private TextView textViewFecha;

        public MensajeViewHolder(View itemView) {
            super(itemView);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
        }

        public void bind(Mensaje mensaje) {
            textViewContenido.setText(mensaje.getContenido());
            textViewFecha.setText(mensaje.getFecha());
        }
        // PARA CUANDO FUNCIONE LO DE USURIARIOS
        //@Override
        //public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        //    Message message = messages.get(position);
        //    holder.messageText.setText(message.getContent());
        //
        //    // Verifica si el mensaje es enviado por el usuario
        //    if (message.getSenderId() == currentUserId) {
        //        holder.messageLayout.setBackgroundColor(Color.GREEN); // Color verde para los mensajes enviados
        //    } else {
        //        holder.messageLayout.setBackgroundColor(Color.GRAY); // Color por defecto para los mensajes recibidos
        //    }
        //}
    }
}

