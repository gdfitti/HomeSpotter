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
import org.uvigo.esei.example.homespotter.ui.activities.ViviendaActivity;
import org.uvigo.esei.example.homespotter.ui.activities.ViviendaDetailActivity;
import org.uvigo.esei.example.homespotter.database.FavoritosEntity;
import org.uvigo.esei.example.homespotter.models.Vivienda;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViviendaAdapter extends ArrayAdapter<Vivienda> {
    private final int idUsuario;
    private final FavoritosEntity favoritosEntity;
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

        Vivienda vivienda = getItem(position);

        ImageView propertyImage = convertView.findViewById(R.id.property_image);
        TextView propertyTitle = convertView.findViewById(R.id.property_title);
        TextView propertyPrice = convertView.findViewById(R.id.property_price);
        TextView propertyAddress = convertView.findViewById(R.id.property_address);
        ImageButton favoriteButton = convertView.findViewById(R.id.btn_favorite);

        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("es", "ES"));
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(0);
        String priceString = "";
        String addressString = "";

        if (vivienda != null) {
            priceString = getContext().getString(R.string.price) + ": " + numberFormat.format(vivienda.getPrecio()) + "€";
            addressString = getContext().getString(R.string.address)+ ": " + vivienda.getDireccion();
            propertyTitle.setText(vivienda.getTitulo());
            propertyPrice.setText(priceString);
            propertyAddress.setText(addressString);

            if (vivienda.getFotos() != null && !vivienda.getFotos().isEmpty()) {
                String imageUrl = vivienda.getFotos().get(0);
                Glide.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.loading_placeholder)
                        .error(R.drawable.error_placeholder)
                        .into(propertyImage);
            } else {
                propertyImage.setImageResource(R.drawable.default_image);
            }

            if (vivienda.getPropietarioId() == idUsuario) {
                favoriteButton.setVisibility(View.GONE); // Ocultar el botón si es del usuario actual
            } else {
                favoriteButton.setVisibility(View.VISIBLE);
                favoriteButton.setImageResource(vivienda.isFavorite() ? R.drawable.ic_favorites_selected : R.drawable.ic_favorites_default);
                favoriteButton.setOnClickListener(v -> {
                    int viviendaId = vivienda.getId();
                    boolean currentlyFavorite = vivienda.isFavorite();
                    if(idUsuario <= 0){
                        Toast.makeText(getContext(),getContext().getString(R.string.user_not_logged),Toast.LENGTH_LONG).show();
                    } else if (!currentlyFavorite) {
                        favoriteButton.setImageResource(R.drawable.ic_favorites_selected);
                        favoritosEntity.insertar(idUsuario, viviendaId);
                        vivienda.setFavorite(true);
                        Toast.makeText(getContext(), vivienda.getTitulo() + " añadido a favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        favoriteButton.setImageResource(R.drawable.ic_favorites_default);
                        favoritosEntity.eliminar(idUsuario, viviendaId);
                        vivienda.setFavorite(false);
                        Toast.makeText(getContext(), vivienda.getTitulo() + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    }
                });
            }

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
