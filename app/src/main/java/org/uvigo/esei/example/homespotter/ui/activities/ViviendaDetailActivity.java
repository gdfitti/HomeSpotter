package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import org.uvigo.esei.example.homespotter.database.UsuarioEntity;
import org.uvigo.esei.example.homespotter.models.Usuario;
import org.uvigo.esei.example.homespotter.ui.adapters.ImagePagerAdapter;
import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.FavoritosEntity;

import java.util.List;

public class ViviendaDetailActivity extends AppCompatActivity {

    private FavoritosEntity favoritosEntity;
    private boolean isFavorite;
    private UsuarioEntity usuarioEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vivienda_detail);
        usuarioEntity = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());
        // Obtener datos de la vivienda desde el intent
        int viviendaId = getIntent().getIntExtra("viviendaId", -1);
        String titulo = getIntent().getStringExtra("titulo");
        String direccion = getIntent().getStringExtra("direccion");
        double precio = getIntent().getDoubleExtra("precio", 0.0);
        String descripcion = getIntent().getStringExtra("descripcion");
        List<String> fotos = getIntent().getStringArrayListExtra("fotos");
        int idUsuario = getIntent().getIntExtra("userId", -1);
        int idPropietario = getIntent().getIntExtra("propietarioId", -1);
        isFavorite = getIntent().getBooleanExtra("favorito", false);

        Cursor cursorUsuario = usuarioEntity.buscarPorId(idPropietario);
        cursorUsuario.moveToFirst();
        int userId = cursorUsuario.getInt(cursorUsuario.getColumnIndexOrThrow("id_usuario"));
        String nombreUser = cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("nombre_usuario"));
        String email = cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("email"));
        String password = cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("password"));
        String foto = cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("foto_perfil"));
        String tlfno = cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("tlfno"));
        cursorUsuario.close();

        Usuario user = new Usuario(userId, nombreUser, email, password, foto, tlfno);
        // Configurar el ViewPager2
        ViewPager2 viewPager = findViewById(R.id.viewPager_images);
        ImagePagerAdapter imageAdapter = new ImagePagerAdapter(this, fotos);
        viewPager.setAdapter(imageAdapter);

        // Configurar texto y otros elementos de la UI
        TextView textTitle = findViewById(R.id.text_title);
        TextView propietarioTlfno = findViewById(R.id.owner_telephone);
        TextView textDescription = findViewById(R.id.text_description);
        TextView textDireccion = findViewById(R.id.text_address);
        TextView textPrice = findViewById(R.id.text_price);
        TextView nombrePropietario = findViewById(R.id.owner_name);
        Button backButton = findViewById(R.id.btn_back);
        ImageButton favoriteButton = findViewById(R.id.btn_favorite);
        ImageView fotoPropietario = findViewById(R.id.owner_photo);

        if(foto != null && !foto.isEmpty()){
            Glide.with(this)
                    .load(foto)
                    .placeholder(R.drawable.loading_placeholder) // Placeholder mientras carga
                    .error(R.drawable.error_placeholder) // Imagen si ocurre un error
                    .into(fotoPropietario);
        }else{
            fotoPropietario.setImageResource(R.drawable.error_placeholder);
        }

        nombrePropietario.setText(nombreUser);
        propietarioTlfno.setText(tlfno);
        textTitle.setText(titulo);
        textDireccion.setText(direccion);
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
                isFavorite = !isFavorite;
            } else {
                favoritosEntity.eliminar(idUsuario, viviendaId);
                favoriteButton.setImageResource(R.drawable.ic_favorites);
                Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                isFavorite = !isFavorite;
            }

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