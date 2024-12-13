package org.uvigo.esei.example.homespotter.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import org.uvigo.esei.example.homespotter.ui.activities.ViviendaDetailActivity;
import org.uvigo.esei.example.homespotter.database.FavoritosEntity;
import org.uvigo.esei.example.homespotter.models.Vivienda;

import java.util.ArrayList;
import java.util.List;

public class ViviendaAdapter extends ArrayAdapter<Vivienda> {
    private int idUsuario;
    private FavoritosEntity favoritosEntity;
    public ViviendaAdapter(@NonNull Context context, @NonNull List<Vivienda> properties, SQLiteDatabase db, int idUsuario) {
        super(context, 0, properties);
        this.idUsuario = idUsuario;
        this.favoritosEntity = new FavoritosEntity(db);
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
            boolean isFavorite = vivienda.isFavorite();
            favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favorites_selected : R.drawable.ic_favorites_default);
            favoriteButton.setTag(isFavorite);

            // Configurar acción del botón de favoritos
            favoriteButton.setOnClickListener(v -> {
                int viviendaId = vivienda.getId();
                boolean currentlyFavorite = vivienda.isFavorite();
                if(idUsuario <= 0){
                    Toast.makeText(getContext(), "Inicia Sesión para añadir a Favoritos", Toast.LENGTH_LONG).show();
                } else if (!currentlyFavorite) {
                    // Cambiar a estado "favorito"
                    favoriteButton.setImageResource(R.drawable.ic_favorites_selected);
                    favoritosEntity.insertar(idUsuario,viviendaId);
                    vivienda.setFavorite(true);
                    Toast.makeText(getContext(), vivienda.getTitulo() + " añadido a favoritos", Toast.LENGTH_SHORT).show();

                } else {
                    // Cambiar a estado "no favorito"
                    favoriteButton.setImageResource(R.drawable.ic_favorites_default);
                    favoritosEntity.eliminar(idUsuario,viviendaId);
                    vivienda.setFavorite(false);
                    Toast.makeText(getContext(), vivienda.getTitulo() + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
                }
            });
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ViviendaDetailActivity.class);
                intent.putExtra("viviendaId", vivienda.getId());
                intent.putExtra("userId", idUsuario);
                intent.putExtra("titulo", vivienda.getTitulo());
                intent.putExtra("propietarioId", vivienda.getPropietarioId());
                intent.putExtra("precio", vivienda.getPrecio());
                intent.putExtra("direccion", vivienda.getDireccion());
                intent.putExtra("descripcion", vivienda.getDescripcion());
                intent.putExtra("favorito", vivienda.isFavorite());
                intent.putStringArrayListExtra("fotos", new ArrayList<>(vivienda.getFotos()));
                ((Activity) getContext()).startActivityForResult(intent, 1);
            });
        }

        return convertView;
    }

}
