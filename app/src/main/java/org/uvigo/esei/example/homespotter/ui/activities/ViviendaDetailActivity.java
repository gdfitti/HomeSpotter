package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import org.uvigo.esei.example.homespotter.database.MensajesEntity;
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
 * Permite marcar o desmarcar la vivienda como favorita, enviar mensajes al propietario y navegar
 * entre las imágenes de la vivienda.
 *
 * Funcionalidades principales:
 * - Mostrar detalles de una vivienda.
 * - Visualizar imágenes asociadas en un `ViewPager2`.
 * - Mostrar datos del propietario, incluida su foto.
 * - Marcar o desmarcar la vivienda como favorita.
 * - Enviar mensajes al propietario.
 * - Volver a la actividad anterior.
 *
 * Dependencias:
 * - Modelo: {@link Usuario}.
 * - Adaptador: {@link ImagePagerAdapter}.
 * - Entidades: {@link FavoritosEntity}, {@link UsuarioEntity}, {@link MensajesEntity}.
 * - Biblioteca: Glide (para cargar imágenes).
 */
public class ViviendaDetailActivity extends AppCompatActivity {

    private FavoritosEntity favoritosEntity;
    private boolean isFavorite;
    private UsuarioEntity usuarioEntity;
    private MensajesEntity mensajesEntity;

    /**
     * Método llamado al crear la actividad.
     * Inicializa las entidades necesarias, recupera los datos de la vivienda desde el `Intent`,
     * y configura la interfaz gráfica con los detalles de la vivienda.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vivienda_detail);

        // Inicializar entidades
        usuarioEntity = new UsuarioEntity(DBManager.getInstance(this).getWritableDatabase());
        favoritosEntity = new FavoritosEntity(DBManager.getInstance(this).getWritableDatabase());

        // Recuperar datos del Intent
        int viviendaId = getIntent().getIntExtra("viviendaId", -1);
        List<String> fotos = getIntent().getStringArrayListExtra("fotos");
        int idUsuario = getIntent().getIntExtra("userId", -1);
        int idPropietario = getIntent().getIntExtra("propietarioId", -1);
        isFavorite = getIntent().getBooleanExtra("favorito", false);

        // Obtener detalles del propietario
        Usuario user = usuarioEntity.obtenerUsuarioPorId(idPropietario);
        loadData(user);

        // Configurar el ViewPager para las imágenes
        ViewPager2 viewPager = findViewById(R.id.viewPager_images);
        ImagePagerAdapter imageAdapter = new ImagePagerAdapter(this, fotos);
        viewPager.setAdapter(imageAdapter);

        // Configurar el botón de favorito
        setFavoriteButton(idUsuario, idPropietario, viviendaId);

        // Configuración del botón de enviar mensaje
        EditText editTextMessage = findViewById(R.id.vivienda_edit_text);
        Button btnSendMessage = findViewById(R.id.vivienda_send_message);
        btnSendMessage.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
            } else {
                enviarMensaje(message, idPropietario);
            }
        });

        // Configuración del botón de retroceso
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Carga los datos de la vivienda y los muestra en los elementos de la interfaz gráfica.
     *
     * @param user El propietario de la vivienda.
     */
    private void loadData(Usuario user) {
        String titulo = getIntent().getStringExtra("titulo");
        String direccion = getIntent().getStringExtra("direccion");
        double precio = getIntent().getDoubleExtra("precio", 0.0);
        String descripcion = getIntent().getStringExtra("descripcion");

        TextView textTitle = findViewById(R.id.text_title);
        TextView propietarioTlfno = findViewById(R.id.owner_telephone);
        TextView textDescription = findViewById(R.id.text_description);
        TextView textDireccion = findViewById(R.id.text_address);
        TextView textPrice = findViewById(R.id.text_price);
        TextView nombrePropietario = findViewById(R.id.owner_name);

        // Configurar los textos con los datos de la vivienda
        textTitle.setText(titulo);
        propietarioTlfno.setText(user.getTlfno());
        nombrePropietario.setText(user.getNombre());
        textDireccion.setText(this.getString(R.string.address) + ": " + direccion);
        textDescription.setText(this.getString(R.string.description) + ": " + descripcion);

        // Formatear y mostrar el precio
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("es", "ES"));
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(0);
        textPrice.setText(this.getString(R.string.price) + ": " + numberFormat.format(precio) + "€");

        cargarImagen(user.getFoto());
    }

    /**
     * Configura el botón de favorito para permitir al usuario marcar o desmarcar la vivienda como favorita.
     *
     * @param idUsuario   ID del usuario actual.
     * @param idPropietario ID del propietario de la vivienda.
     * @param viviendaId  ID de la vivienda actual.
     */
    private void setFavoriteButton(int idUsuario, int idPropietario, int viviendaId) {
        ImageButton favoriteButton = findViewById(R.id.btn_favorite);
        favoriteButton.setImageResource(isFavorite ? R.drawable.ic_favorites_selected : R.drawable.ic_favorites_default);

        // Ocultar el botón si el usuario es el propietario
        if (idUsuario == idPropietario) {
            favoriteButton.setVisibility(View.GONE);
        }

        favoriteButton.setOnClickListener(v -> {
            if (idUsuario <= 0) {
                Toast.makeText(this, "Inicia sesión para añadir a favoritos", Toast.LENGTH_SHORT).show();
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

            // Actualizar el estado de favorito al volver a la actividad anterior
            Intent resultIntent = new Intent();
            resultIntent.putExtra("viviendaId", viviendaId);
            resultIntent.putExtra("isFavoriteChanged", true);
            setResult(RESULT_OK, resultIntent);
        });
    }

    /**
     * Carga la foto del propietario utilizando la biblioteca Glide.
     *
     * @param foto URL de la foto del propietario.
     */
    private void cargarImagen(String foto) {
        ImageView fotoPropietario = findViewById(R.id.owner_photo);

        if (foto != null && !foto.isEmpty()) {
            Glide.with(this)
                    .load(foto)
                    .placeholder(R.drawable.loading_placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(fotoPropietario);
        } else {
            fotoPropietario.setImageResource(R.drawable.error_placeholder);
        }
    }

    /**
     * Envía un mensaje al propietario de la vivienda.
     *
     * @param message       Contenido del mensaje.
     * @param propietarioID ID del propietario.
     */
    private void enviarMensaje(String message, int propietarioID) {
        mensajesEntity = new MensajesEntity(DBManager.getInstance(this).getWritableDatabase());

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int usuarioId = sharedPreferences.getInt("user_id", -1);

        mensajesEntity.insertar(usuarioId, propietarioID, message);

        // Limpiar el campo de texto después de enviar
        EditText editTextMessage = findViewById(R.id.vivienda_edit_text);
        editTextMessage.setText("");
    }
}
