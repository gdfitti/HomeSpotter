package org.uvigo.esei.example.homespotter.ui.fragments;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.os.Handler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

/**
 * Fragmento que muestra la lista de propiedades asociadas al usuario actual.
 * Este fragmento permite visualizar las viviendas que el usuario ha registrado en la aplicación.
 *
 * Funcionalidades principales:
 * - Cargar y mostrar las propiedades del usuario en un ListView.
 * - Sincronizar los datos de viviendas desde la base de datos.
 *
 * Dependencias:
 * - Modelos: {@link Vivienda}.
 * - Adaptador: {@link ViviendaAdapter}.
 * - Utilidades: {@link ViviendaLoader}.
 * - Entidades de base de datos: {@link ViviendaEntity}, {@link FotosEntity}, {@link FavoritosEntity}.
 */
public class MisViviendasFragment extends Fragment {

    private ViviendaAdapter adapter; // Adaptador para manejar las viviendas en el ListView.
    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Handler para ejecutar tareas en el hilo principal.
    private List<Vivienda> propertyList; // Lista de viviendas a mostrar.
    private int idUsuario; // ID del usuario actual.
    private ViviendaEntity viviendaEntity; // Entidad para gestionar las viviendas en la base de datos.
    private FotosEntity fotosEntity; // Entidad para gestionar las fotos asociadas a las viviendas.
    private FavoritosEntity favoritosEntity; // Entidad para gestionar las viviendas marcadas como favoritas.
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Executor para manejar tareas en segundo plano.

    /**
     * Método estático para crear una nueva instancia del fragmento.
     * Recibe el ID del usuario como argumento.
     *
     * @param idUsuario El ID del usuario actual.
     * @return Una nueva instancia de {@link MisViviendasFragment}.
     */
    public static MisViviendasFragment newInstance(int idUsuario) {
        MisViviendasFragment fragment = new MisViviendasFragment();
        Bundle args = new Bundle();
        args.putInt("idUsuario", idUsuario);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Método llamado al crear la vista del fragmento.
     * Configura el diseño, inicializa las entidades y configura el adaptador para el ListView.
     *
     * @param inflater           Objeto para inflar vistas en el fragmento.
     * @param container          Contenedor padre donde se mostrará el fragmento.
     * @param savedInstanceState Estado previamente guardado del fragmento.
     * @return La vista creada para el fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_viviendas, container, false);

        // Recuperar el ID del usuario desde los argumentos.
        if (getArguments() != null) {
            idUsuario = getArguments().getInt("idUsuario", -1);
        }

        // Inicializar las entidades de base de datos.
        viviendaEntity = new ViviendaEntity(DBManager.getInstance(requireContext()).getWritableDatabase());
        fotosEntity = new FotosEntity(DBManager.getInstance(requireContext()).getWritableDatabase());
        favoritosEntity = new FavoritosEntity(DBManager.getInstance(requireContext()).getWritableDatabase());

        // Configurar el ListView y el adaptador.
        ListView listView = view.findViewById(R.id.my_property_list);
        propertyList = new ArrayList<>();
        adapter = new ViviendaAdapter(requireContext(), propertyList, DBManager.getInstance(requireContext()).getWritableDatabase(), idUsuario);
        listView.setAdapter(adapter);

        // Cargar los datos de las propiedades.
        cargarPropiedades();

        return view;
    }

    /**
     * Método para cargar las propiedades del usuario desde la base de datos.
     * Este método ejecuta la carga en un hilo de fondo y actualiza la interfaz gráfica en el hilo principal.
     */
    public void cargarPropiedades() {
        executor.execute(() -> {
            // Cargar las propiedades utilizando el ViviendaLoader.
            List<Vivienda> propiedades = ViviendaLoader.cargarViviendas(viviendaEntity, fotosEntity, favoritosEntity, idUsuario, true);

            // Actualizar la lista y notificar cambios al adaptador en el hilo principal.
            mainHandler.post(() -> {
                propertyList.clear();
                propertyList.addAll(propiedades);
                adapter.notifyDataSetChanged();
            });
        });
    }
}
