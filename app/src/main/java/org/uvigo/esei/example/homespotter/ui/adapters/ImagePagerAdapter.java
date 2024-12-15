package org.uvigo.esei.example.homespotter.ui.adapters;

import android.content.Context;
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
 * ImagePagerAdapter
 *
 * Adaptador para un RecyclerView que muestra una lista de imágenes en un carrusel o paginador.
 * Utiliza Glide para cargar las imágenes de forma eficiente.
 */
public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {
    private Context context; // Contexto para inflar las vistas y cargar imágenes
    private List<String> imageUrls; // Lista de URLs de las imágenes a mostrar

    /**
     * Constructor del adaptador.
     *
     * @param context Contexto de la actividad o fragmento.
     * @param imageUrls Lista de URLs de imágenes a mostrar en el paginador.
     */
    public ImagePagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    /**
     * Crea una nueva vista para un elemento en el RecyclerView.
     *
     * @param parent Vista padre.
     * @param viewType Tipo de vista (no se usa en este caso).
     * @return Un nuevo ViewHolder con la vista inflada.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada imagen
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * Vincula los datos (URLs de las imágenes) con la vista.
     *
     * @param holder ViewHolder donde se mostrará la imagen.
     * @param position Posición de la imagen en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Cargar la imagen usando Glide
        Glide.with(context)
                .load(imageUrl) // URL de la imagen
                .placeholder(R.drawable.loading_placeholder) // Imagen de carga
                .error(R.drawable.error_placeholder) // Imagen si hay error
                .into(holder.imageView); // Asignar la imagen al ImageView
    }

    /**
     * Devuelve el número de elementos en la lista de imágenes.
     *
     * @return El tamaño de la lista de imágenes.
     */
    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    /**
     * ViewHolder para representar una imagen en el RecyclerView.
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView; // ImageView para mostrar la imagen

        /**
         * Constructor para el ViewHolder.
         *
         * @param itemView Vista del elemento en el RecyclerView.
         */
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image); // Referenciar el ImageView
        }
    }
}
