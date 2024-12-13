package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Clase ViviendaDetailActivity
 *
 * Actividad que muestra los detalles de una vivienda específica en la aplicación HomeSpotter.
 * Incluye información como título, descripción, precio, dirección, fotos, y datos del propietario.
 * Permite marcar o desmarcar la vivienda como favorita.
 *
 * Funcionalidades principales:
 * - Mostrar detalles de una vivienda.
 * - Visualizar imágenes asociadas en un `ViewPager2`.
 * - Mostrar datos del propietario, incluida su foto.
 * - Marcar o desmarcar la vivienda como favorita.
 * - Volver a la actividad anterior.
 *
 * Dependencias:
 * - Modelo: `Usuario`.
 * - Adaptador: `ImagePagerAdapter`.
 * - Entidades: `FavoritosEntity`, `UsuarioEntity`.
 * - Biblioteca: Glide (para cargar imágenes).
 */
public class ViviendaDetailActivity extends AppCompatActivity {

    private FavoritosEntity favoritosEntity;
    private boolean isFavorite;
    private UsuarioEntity usuarioEntity;

    /**
     * Método `onCreate`
     * Configura la actividad cuando se crea. Inicializa las entidades, recupera los datos de la vivienda
     * desde el `Intent` y configura la interfaz gráfica con los detalles de la vivienda.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vivienda_detail);

        // Inicializar las entidades
        usuarioEntity = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());
        favoritosEntity = new FavoritosEntity(DBManager.getInstance(this).getWritableDatabase());

        // Obtener datos de la vivienda desde el Intent
        int viviendaId = getIntent().getIntExtra("viviendaId", -1);
        String titulo = getIntent().getStringExtra("titulo");
        String direccion = getIntent().getStringExtra("direccion");
        double precio = getIntent().getDoubleExtra("precio", 0.0);
        String descripcion = getIntent().getStringExtra("descripcion");
        List<String> fotos = getIntent().getStringArrayListExtra("fotos");
        int idUsuario = getIntent().getIntExtra("userId", -1);
        int idPropietario = getIntent().getIntExtra("propietarioId", -1);
        isFavorite = getIntent().getBooleanExtra("favorito", false);

        // Obtener detalles del propietario desde la base de datos
        Cursor cursorUsuario = usuarioEntity.buscarPorId(idPropietario);
        cursorUsuario.moveToFirst();
        Usuario user = new Usuario(
                cursorUsuario.getInt(cursorUsuario.getColumnIndexOrThrow("id_usuario")),
                cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("nombre_usuario")),
                cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("email")),
                cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("password")),
                cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("foto_perfil")),
                cursorUsuario.getString(cursorUsuario.getColumnIndexOrThrow("tlfno"))
        );
        cursorUsuario.close();

        // Configurar el `ViewPager2` para las fotos
        ViewPager2 viewPager = findViewById(R.id.viewPager_images);
        ImagePagerAdapter imageAdapter = new ImagePagerAdapter(this, fotos);
        viewPager.setAdapter(imageAdapter);

        // Configurar elementos de la interfaz gráfica
        TextView textTitle = findViewById(R.id.text_title);
        TextView propietarioTlfno = findViewById(R.id.owner_telephone);
        TextView textDescription = findViewById(R.id.text_description);
        TextView textDireccion = findViewById(R.id.text_address);
        TextView textPrice = findViewById(R.id.text_price);
        TextView nombrePropietario = findViewById(R.id.owner_name);
        ImageButton favoriteButton = findViewById(R.id.btn_favorite);
        Button backButton = findViewById(R.id.btn_back);
        ImageView fotoPropietario = findViewById(R.id.owner_photo);

        // Cargar la foto del propietario usando Glide
        if (user.getFoto() != null && !user.getFoto().isEmpty()) {
            Glide.with(this)
                    .load(user.getFoto())
                    .placeholder(R.drawable.loading_placeholder) // Imagen de carga
                    .error(R.drawable.error_placeholder)         // Imagen en caso de error
                    .into(fotoPropietario);
        } else {
            fotoPropietario.setImageResource(R.drawable.error_placeholder);
        }

        // Configurar los textos
        textTitle.setText(titulo);
        propietarioTlfno.setText(user.getTlfno());
        nombrePropietario.setText(user.getNombre());
        String address = this.getString(R.string.address);
        textDireccion.setText(address +": " + direccion);
        String desc = this.getString(R.string.description);
        textDescription.setText(desc+": "+descripcion);

        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("es", "ES"));
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(0);
        String price = this.getString(R.string.price);

        textPrice.setText(price + ": "+ numberFormat.format(precio) + "€");

        // Configurar el botón de favorito
        favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favorites_selected : R.drawable.ic_favorites_default);

        if(idUsuario == idPropietario){
            favoriteButton.setVisibility(View.GONE);
        }
        favoriteButton.setOnClickListener(v -> {
            if (idUsuario <= 0) {
                Toast.makeText(this, "Inicia Sesión para añadir a Favoritos", Toast.LENGTH_SHORT).show();
            } else if (!isFavorite) {
                favoritosEntity.insertar(idUsuario, viviendaId);
                favoriteButton.setImageResource(R.drawable.ic_favorites_selected);
                Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                isFavorite = true;
            } else {
                favoritosEntity.eliminar(idUsuario, viviendaId);
                favoriteButton.setImageResource(R.drawable.ic_favorites_default);
                Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                isFavorite = false;
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("viviendaId", viviendaId);
            resultIntent.putExtra("isFavoriteChanged", true);
            setResult(RESULT_OK, resultIntent);
        });

        // Configurar botón de volver atrás
        backButton.setOnClickListener(v -> finish());
    }
}