package org.uvigo.esei.example.homespotter.ui.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.uvigo.esei.example.homespotter.R;

import java.util.List;

/**
 * Adaptador para mostrar una lista de fotos en un RecyclerView.
 * Utiliza la biblioteca Glide para cargar y mostrar imágenes desde URIs.
 * Cada imagen se muestra en un diseño definido por `R.layout.item_image`.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<Uri> photoUris; // Lista de URIs de las fotos a mostrar.

    /**
     * Constructor para inicializar el adaptador con una lista de URIs de fotos.
     *
     * @param photoUris Lista de URIs de las fotos.
     */
    public PhotoAdapter(List<Uri> photoUris) {
        this.photoUris = photoUris;
    }

    /**
     * Crea y devuelve un nuevo {@link PhotoViewHolder}.
     * Infla el diseño del elemento de la lista desde `R.layout.item_image`.
     *
     * @param parent   El ViewGroup padre donde se añadirá el nuevo ViewHolder.
     * @param viewType El tipo de vista (no utilizado en este caso).
     * @return Un nuevo objeto {@link PhotoViewHolder}.
     */
    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new PhotoViewHolder(view);
    }

    /**
     * Vincula los datos de la URI de una foto a un ViewHolder.
     * Carga la imagen en el {@link ImageView} correspondiente utilizando Glide.
     *
     * @param holder   El ViewHolder al que se deben vincular los datos.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri photoUri = photoUris.get(position);

        // Cargar la imagen desde la URI utilizando Glide.
        Glide.with(holder.itemView.getContext())
                .load(photoUri) // URI de la imagen a cargar.
                .placeholder(R.drawable.loading_placeholder) // Imagen a mostrar mientras se carga.
                .error(R.drawable.error_placeholder) // Imagen a mostrar si ocurre un error.
                .into(holder.imageView); // ImageView donde se cargará la imagen.
    }

    /**
     * Devuelve el número total de elementos en la lista de fotos.
     *
     * @return El tamaño de la lista de URIs de fotos.
     */
    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    /**
     * ViewHolder para los elementos de la lista de fotos.
     * Contiene una referencia al {@link ImageView} que muestra cada foto.
     */
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        /**
         * Constructor para inicializar el ViewHolder.
         *
         * @param itemView La vista del elemento de la lista.
         */
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Referencia al ImageView dentro del diseño del elemento.
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
