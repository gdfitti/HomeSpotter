package org.uvigo.esei.example.homespotter.vivienda;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.FavoritosEntity;
import org.uvigo.esei.example.homespotter.vivienda.Vivienda;

import java.util.List;

public class ViviendaAdapter extends ArrayAdapter<Vivienda> {
    private int idUsuario;
    private List<Integer> favoritos;
    private FavoritosEntity favoritosEntity;
    public ViviendaAdapter(@NonNull Context context, @NonNull List<Vivienda> properties, SQLiteDatabase db, int idUsuario) {
        super(context, 0, properties);
        this.idUsuario = idUsuario;
        this.favoritosEntity = new FavoritosEntity(db);
        this.favoritos = favoritosEntity.obtenerFavoritosPorUsuario(idUsuario); // Cargar favoritos al inicializar
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_property, parent, false);
        }

        // Obtener la propiedad actual
        Vivienda vivienda = getItem(position);

        // Vincular datos con la vista
        ImageView propertyImage = convertView.findViewById(R.id.property_image);
        TextView propertyTitle = convertView.findViewById(R.id.property_title);
        TextView propertyPrice = convertView.findViewById(R.id.property_price);
        TextView propertyAddress = convertView.findViewById(R.id.property_address);
        ImageButton favoriteButton = convertView.findViewById(R.id.btn_favorite);


        if (vivienda != null) {
            // Vincular datos de texto
            propertyTitle.setText(vivienda.getTitulo());
            propertyPrice.setText(String.format("%.2f€", vivienda.getPrecio()));
            propertyAddress.setText(vivienda.getDireccion());

            // Cargar la imagen desde la lista de URLs
            if (vivienda.getFotos() != null && !vivienda.getFotos().isEmpty()) {
                String imageUrl = vivienda.getFotos().get(0); // Primera URL de la lista
                Glide.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.loading_placeholder) // Placeholder mientras carga
                        .error(R.drawable.error_placeholder) // Imagen si ocurre un error
                        .into(propertyImage);
            } else {
                // Mostrar una imagen predeterminada si no hay fotos
                propertyImage.setImageResource(R.drawable.default_image);
            }
            boolean isFavorite = favoritos.contains(vivienda.getId());
            favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favourites_selected : R.drawable.ic_favourites);
            favoriteButton.setTag(isFavorite);

            // Configurar acción del botón de favoritos
            favoriteButton.setOnClickListener(v -> {
                int viviendaId = vivienda.getId();
                boolean currentlyFavorite = (boolean) (favoriteButton.getTag() != null && (boolean) favoriteButton.getTag());
                if (!currentlyFavorite) {
                    // Cambiar a estado "favorito"
                    favoriteButton.setImageResource(R.drawable.ic_favourites_selected);
                    favoritosEntity.insertar(idUsuario,viviendaId);
                    favoriteButton.setTag(true);
                    Toast.makeText(getContext(), vivienda.getTitulo() + " añadido a favoritos", Toast.LENGTH_SHORT).show();

                } else {
                    // Cambiar a estado "no favorito"
                    favoriteButton.setImageResource(R.drawable.ic_favourites);
                    favoritosEntity.eliminar(idUsuario,viviendaId);
                    favoriteButton.setTag(false);
                    Toast.makeText(getContext(), vivienda.getTitulo() + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }

}
