package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import android.database.Cursor;
import android.widget.ListView;

import org.uvigo.esei.example.homespotter.database.FavoritosEntity;
import org.uvigo.esei.example.homespotter.database.FotosEntity;
import org.uvigo.esei.example.homespotter.database.ViviendaEntity;
import org.uvigo.esei.example.homespotter.models.Vivienda;
import org.uvigo.esei.example.homespotter.ui.adapters.ViviendaAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * FavoritesActivity
 *
 * Actividad que muestra una lista de propiedades marcadas como favoritas por el usuario actual.
 * Permite al usuario ver y seleccionar sus propiedades favoritas para ver más detalles.
 */
public class FavoritesActivity extends BaseActivity {

    private int idUsuario; // ID del usuario actual
    private List<Vivienda> viviendasFavoritas; // Lista de viviendas favoritas del usuario
    private ViviendaEntity viviendaEntity; // Entidad para interactuar con la tabla de viviendas
    private FavoritosEntity favoritosEntity; // Entidad para interactuar con la tabla de favoritos
    private FotosEntity fotosEntity; // Entidad para interactuar con la tabla de fotos
    private ViviendaAdapter viviendaAdapter; // Adaptador para mostrar las viviendas en un ListView

    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz de usuario, inicializa las entidades de base de datos,
     * carga las viviendas favoritas y establece los eventos de clic en la lista.
     *
     * @param savedInstance Estado guardado previamente (si existe).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstance) {
        super.onCreate(savedInstance);
        changeBottomNavigationIcon(R.id.nav_favorites, R.drawable.ic_favorites_selected);

        // Inicializar vistas
        ListView favoritePropertyList = findViewById(R.id.favorite_property_list);

        // Obtener el ID del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        idUsuario = sharedPreferences.getInt("user_id", -1);

        // Inicializar entidades de base de datos
        SQLiteDatabase db = DBManager.getInstance(this).getWritableDatabase();
        viviendaEntity = new ViviendaEntity(db);
        favoritosEntity = new FavoritosEntity(db);
        fotosEntity = new FotosEntity(db);

        viviendasFavoritas = new ArrayList<>();

        // Si el usuario está autenticado, cargar las viviendas favoritas
        if (idUsuario != -1) {
            cargarFavoritosEnHilo();
        }

        // Configurar el adaptador para el ListView
        viviendaAdapter = new ViviendaAdapter(this, viviendasFavoritas, db, idUsuario);
        favoritePropertyList.setAdapter(viviendaAdapter);

        // Configurar evento de clic en los elementos de la lista
        favoritePropertyList.setOnItemClickListener((parent, view, position, id) -> {
            Vivienda viviendaSeleccionada = viviendasFavoritas.get(position);
            abrirDetalleVivienda(viviendaSeleccionada);
        });
    }

    /**
     * Carga las viviendas favoritas del usuario en un hilo secundario
     * para evitar bloqueos en la interfaz de usuario.
     */
    private void cargarFavoritosEnHilo() {
        new Thread(() -> {
            // Obtener IDs de las viviendas favoritas del usuario
            List<Integer> listaFavUser = favoritosEntity.obtenerFavoritosPorUsuario(idUsuario);

            // Cargar detalles de las viviendas
            List<Vivienda> listaCargada = cargarFavoritosPorUsuario(listaFavUser);

            // Actualizar la lista en el hilo principal
            runOnUiThread(() -> {
                viviendasFavoritas.clear();
                viviendasFavoritas.addAll(listaCargada);
                viviendaAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    /**
     * Carga los detalles de las viviendas favoritas a partir de una lista de IDs.
     *
     * @param idViviendasFavoritas Lista de IDs de viviendas favoritas.
     * @return Lista de objetos Vivienda con los detalles cargados.
     */
    protected List<Vivienda> cargarFavoritosPorUsuario(List<Integer> idViviendasFavoritas) {
        List<Vivienda> listaViviendas = new ArrayList<>();

        for (int idVivienda : idViviendasFavoritas) {
            listaViviendas.add(getVivienda(idVivienda));
        }

        return listaViviendas;
    }

    /**
     * Obtiene los detalles de una vivienda específica desde la base de datos.
     *
     * @param viviendaId ID de la vivienda.
     * @return Objeto Vivienda con los detalles cargados o null si no se encuentra.
     */
    public Vivienda getVivienda(int viviendaId) {
        Cursor cursor = viviendaEntity.buscarPorId(viviendaId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_vivienda"));
                Double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                String direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"));
                String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                String contacto = cursor.getString(cursor.getColumnIndexOrThrow("contacto"));
                int propietarioId = cursor.getInt(cursor.getColumnIndexOrThrow("propietario_id"));

                // Obtener fotos de la vivienda
                List<String> fotos = fotosEntity.obtenerListaFotos(viviendaId);

                return new Vivienda(viviendaId, titulo, tipo, precio, direccion, estado, contacto, descripcion, propietarioId, true, fotos);
            } while (cursor.moveToNext());
        }
        return null;
    }

    /**
     * Abre la actividad de detalles de la vivienda seleccionada.
     *
     * @param vivienda Vivienda seleccionada.
     */
    private void abrirDetalleVivienda(Vivienda vivienda) {
        Intent intent = new Intent(this, ViviendaDetailActivity.class);
        intent.putExtra("id_vivienda", vivienda.getId());
        startActivity(intent);
    }

    /**
     * Devuelve el diseño asociado a esta actividad.
     *
     * @return ID del recurso de diseño de la actividad.
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_favorites;
    }

    /**
     * Método llamado al reanudar la actividad.
     * Recarga las viviendas favoritas para actualizar los datos.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargarFavoritosEnHilo();
    }
}
