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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ViviendaAdapter
 *
 * Adaptador para una lista de viviendas. Muestra los detalles de cada vivienda, como el título,
 * el precio, la dirección, una imagen y un botón para añadir a favoritos.
 */
public class ViviendaAdapter extends ArrayAdapter<Vivienda> {
    private final int idUsuario; // ID del usuario actual
    private final FavoritosEntity favoritosEntity; // Entidad para manejar los favoritos en la base de datos

    /**
     * Constructor del adaptador.
     *
     * @param context Contexto de la actividad o fragmento.
     * @param properties Lista de viviendas a mostrar.
     * @param db Base de datos para manejar los favoritos.
     * @param idUsuario ID del usuario actual.
     */
    public ViviendaAdapter(@NonNull Context context, @NonNull List<Vivienda> properties, SQLiteDatabase db, int idUsuario) {
        super(context, 0, properties);
        this.idUsuario = idUsuario;
        this.favoritosEntity = new FavoritosEntity(db);
    }

    /**
     * Crea y configura la vista para un elemento de la lista de viviendas.
     *
     * @param position Posición del elemento en la lista.
     * @param convertView Vista existente que puede ser reutilizada (si no es null).
     * @param parent Vista padre.
     * @return La vista configurada para el elemento.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflar el layout si la vista no se está reutilizando
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_property, parent, false);
        }

        // Obtener la vivienda correspondiente a la posición actual
        Vivienda vivienda = getItem(position);

        // Referencias a los elementos de la vista
        ImageView propertyImage = convertView.findViewById(R.id.property_image);
        TextView propertyTitle = convertView.findViewById(R.id.property_title);
        TextView propertyPrice = convertView.findViewById(R.id.property_price);
        TextView propertyAddress = convertView.findViewById(R.id.property_address);
        ImageButton favoriteButton = convertView.findViewById(R.id.btn_favorite);

        // Configuración de los datos de la vivienda
        if (vivienda != null) {
            // Formatear el precio y la dirección
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("es", "ES"));
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(0);
            String priceString = getContext().getString(R.string.price) + ": " + numberFormat.format(vivienda.getPrecio()) + "€";
            String addressString = getContext().getString(R.string.address) + ": " + vivienda.getDireccion();

            // Asignar valores a los elementos de la vista
            propertyTitle.setText(vivienda.getTitulo());
            propertyPrice.setText(priceString);
            propertyAddress.setText(addressString);

            // Cargar la imagen de la vivienda
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

            // Configuración del botón de favoritos
            if (vivienda.getPropietarioId() == idUsuario) {
                favoriteButton.setVisibility(View.GONE); // Ocultar si el usuario es el propietario
            } else {
                favoriteButton.setVisibility(View.VISIBLE);
                favoriteButton.setImageResource(vivienda.isFavorite() ? R.drawable.ic_favorites_selected : R.drawable.ic_favorites_default);
                favoriteButton.setOnClickListener(v -> {
                    int viviendaId = vivienda.getId();
                    boolean currentlyFavorite = vivienda.isFavorite();

                    if (idUsuario <= 0) {
                        Toast.makeText(getContext(), getContext().getString(R.string.user_not_logged), Toast.LENGTH_LONG).show();
                    } else if (!currentlyFavorite) {
                        favoriteButton.setImageResource(R.drawable.ic_favorites_selected);
                        favoritosEntity.insertar(idUsuario, viviendaId); // Añadir a favoritos
                        vivienda.setFavorite(true);
                        Toast.makeText(getContext(), vivienda.getTitulo() + " añadido a favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        favoriteButton.setImageResource(R.drawable.ic_favorites_default);
                        favoritosEntity.eliminar(idUsuario, viviendaId); // Eliminar de favoritos
                        vivienda.setFavorite(false);
                        Toast.makeText(getContext(), vivienda.getTitulo() + " eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Configuración del clic en el elemento para abrir detalles
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
