package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.uvigo.esei.example.homespotter.ui.adapters.ImagePagerAdapter;
import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.FavoritosEntity;

import java.util.List;

public class ViviendaDetailActivity extends AppCompatActivity {

    private FavoritosEntity favoritosEntity;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vivienda_detail);

        // Obtener datos de la vivienda desde el intent
        int viviendaId = getIntent().getIntExtra("viviendaId", -1);
        String titulo = getIntent().getStringExtra("titulo");
        String direccion = getIntent().getStringExtra("direccion");
        double precio = getIntent().getDoubleExtra("precio", 0.0);
        String descripcion = getIntent().getStringExtra("descripcion");
        List<String> fotos = getIntent().getStringArrayListExtra("fotos");
        int idUsuario = getIntent().getIntExtra("userId", -1);
        isFavorite = getIntent().getBooleanExtra("favorito", false);

        // Configurar el ViewPager2
        ViewPager2 viewPager = findViewById(R.id.viewPager_images);
        ImagePagerAdapter imageAdapter = new ImagePagerAdapter(this, fotos);
        viewPager.setAdapter(imageAdapter);

        // Configurar texto y otros elementos de la UI
        TextView textTitle = findViewById(R.id.text_title);
        TextView textDescription = findViewById(R.id.text_description);
        TextView textPrice = findViewById(R.id.text_price);
        Button backButton = findViewById(R.id.btn_back);
        ImageButton favoriteButton = findViewById(R.id.btn_favorite);

        textTitle.setText(titulo);
        textDescription.setText(descripcion);
        textPrice.setText(String.format("%.2f€", precio));

        // Inicializar FavoritosEntity
        favoritosEntity = new FavoritosEntity(DBManager.getInstance(this).getWritableDatabase());

        // Configurar el estado inicial del botón de favorito
        favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favorites_selected : R.drawable.ic_favorites);

        // Acción para el botón de favorito
        favoriteButton.setOnClickListener(v -> {
            if(idUsuario <= 0) {
                Toast.makeText(this, "Inicia Sesión para añadir a Favoritos", Toast.LENGTH_SHORT).show();
            }else if (!isFavorite) {
                favoritosEntity.insertar(idUsuario, viviendaId);
                favoriteButton.setImageResource(R.drawable.ic_favorites_selected);
                Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
            } else {
                favoritosEntity.eliminar(idUsuario, viviendaId);
                favoriteButton.setImageResource(R.drawable.ic_favorites);
                Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
            }

            isFavorite = !isFavorite;

            // Devolver el estado actualizado al salir
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id", viviendaId);
            resultIntent.putExtra("isFavorite", isFavorite);
            setResult(RESULT_OK, resultIntent);
        });

        // Acción para el botón de volver atrás
        backButton.setOnClickListener(v -> finish());
    }
}