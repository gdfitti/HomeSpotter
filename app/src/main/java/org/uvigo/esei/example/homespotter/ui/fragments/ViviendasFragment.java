package org.uvigo.esei.example.homespotter.ui.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.FavoritosEntity;
import org.uvigo.esei.example.homespotter.database.FotosEntity;
import org.uvigo.esei.example.homespotter.database.ViviendaEntity;
import org.uvigo.esei.example.homespotter.models.Vivienda;
import org.uvigo.esei.example.homespotter.ui.adapters.ViviendaAdapter;
import org.uvigo.esei.example.homespotter.ui.utils.ViviendaLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fragmento para mostrar una lista de todas las viviendas disponibles en la aplicación.
 * Este fragmento permite a los usuarios visualizar propiedades, aplicar filtros y cargar
 * propiedades favoritas desde la base de datos.
 *
 * Funcionalidades principales:
 * - Cargar todas las viviendas desde la base de datos.
 * - Aplicar filtros para buscar propiedades específicas.
 * - Visualizar estado de favorito en las viviendas.
 *
 * Dependencias:
 * - Adaptador: {@link ViviendaAdapter}.
 * - Entidades: {@link ViviendaEntity}, {@link FotosEntity}, {@link FavoritosEntity}.
 * - Utilidades: {@link ViviendaLoader}.
 */
public class ViviendasFragment extends Fragment {

    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Manejo de tareas en segundo plano.
    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler para actualizaciones en la UI.
    private ViviendaAdapter adapter; // Adaptador para mostrar las viviendas.
    private List<Vivienda> propertyList; // Lista de propiedades cargadas.
    private int idUsuario; // ID del usuario actual.
    private ViviendaEntity viviendaEntity; // Entidad para gestionar las viviendas en la base de datos.
    private FotosEntity fotosEntity; // Entidad para gestionar las fotos de las viviendas.
    private FavoritosEntity favoritosEntity; // Entidad para gestionar los favoritos.

    /**
     * Crea una nueva instancia del fragmento con el ID del usuario como argumento.
     *
     * @param idUsuario ID del usuario actual.
     * @return Una nueva instancia de {@link ViviendasFragment}.
     */
    public static ViviendasFragment newInstance(int idUsuario) {
        ViviendasFragment fragment = new ViviendasFragment();
        Bundle args = new Bundle();
        args.putInt("idUsuario", idUsuario);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Método llamado al crear la vista del fragmento.
     * Configura el adaptador, inicializa las entidades y carga las viviendas desde la base de datos.
     *
     * @param inflater           Objeto para inflar vistas.
     * @param container          Contenedor padre donde se alojará el fragmento.
     * @param savedInstanceState Estado previamente guardado.
     * @return Vista inflada para el fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viviendas, container, false);

        // Recuperar el ID del usuario desde los argumentos.
        if (getArguments() != null) {
            idUsuario = getArguments().getInt("idUsuario", -1);
        }

        // Inicializar entidades de base de datos.
        viviendaEntity = new ViviendaEntity(DBManager.getInstance(requireContext()).getWritableDatabase());
        fotosEntity = new FotosEntity(DBManager.getInstance(requireContext()).getWritableDatabase());
        favoritosEntity = new FavoritosEntity(DBManager.getInstance(requireContext()).getWritableDatabase());

        // Configurar el ListView y el adaptador.
        ListView listView = view.findViewById(R.id.property_list);
        propertyList = new ArrayList<>();
        adapter = new ViviendaAdapter(requireContext(), propertyList, DBManager.getInstance(requireContext()).getWritableDatabase(), idUsuario);
        listView.setAdapter(adapter);

        // Cargar las propiedades.
        cargarPropiedades();

        return view;
    }

    /**
     * Aplica filtros a la búsqueda de propiedades y actualiza la lista mostrada.
     *
     * @param filtros   Criterios de filtrado como valores clave-valor.
     * @param minPrice  Precio mínimo de la propiedad (puede ser nulo).
     * @param maxPrice  Precio máximo de la propiedad (puede ser nulo).
     */
    public void applyFilters(ContentValues filtros, Double minPrice, Double maxPrice) {
        executor.execute(() -> {
            Cursor cursor = viviendaEntity.buscar(filtros, minPrice, maxPrice);
            List<Vivienda> filteredViviendas = new ArrayList<>();
            List<Integer> favoritosList = favoritosEntity.obtenerFavoritosPorUsuario(idUsuario);

            // Procesar los resultados de la consulta.
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_vivienda"));
                    List<String> fotos = fotosEntity.obtenerListaFotos(id);
                    int idPropietario = cursor.getInt(cursor.getColumnIndexOrThrow("propietario_id"));
                    String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                    String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_vivienda"));
                    double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                    String direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"));
                    String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
                    String contacto = cursor.getString(cursor.getColumnIndexOrThrow("contacto"));
                    String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                    boolean favorito = favoritosList.contains(id);

                    // Crear objeto Vivienda y agregarlo a la lista filtrada.
                    filteredViviendas.add(new Vivienda(id, titulo, tipo, precio, direccion, estado, contacto, descripcion, idPropietario, favorito, fotos));
                } while (cursor.moveToNext());
                cursor.close();
            }

            // Actualizar la UI con los resultados filtrados.
            mainHandler.post(() -> {
                propertyList.clear();
                propertyList.addAll(filteredViviendas);
                adapter.notifyDataSetChanged();
            });
        });
    }

    /**
     * Carga todas las propiedades desde la base de datos y actualiza la lista mostrada.
     */
    public void cargarPropiedades() {
        executor.execute(() -> {
            // Cargar propiedades utilizando el ViviendaLoader.
            List<Vivienda> propiedades = ViviendaLoader.cargarViviendas(viviendaEntity, fotosEntity, favoritosEntity, idUsuario, false);

            // Actualizar la UI con las propiedades cargadas.
            mainHandler.post(() -> {
                propertyList.clear();
                propertyList.addAll(propiedades);
                adapter.notifyDataSetChanged();
            });
        });
    }
}
