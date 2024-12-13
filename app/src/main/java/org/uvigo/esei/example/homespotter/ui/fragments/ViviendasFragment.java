package org.uvigo.esei.example.homespotter.ui.fragments;

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

public class ViviendasFragment extends Fragment {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private ListView listView;
    private ViviendaAdapter adapter;
    private List<Vivienda> propertyList;
    private int idUsuario;
    private ViviendaEntity viviendaEntity;
    private FotosEntity fotosEntity;
    private FavoritosEntity favoritosEntity;

    public static ViviendasFragment newInstance(int idUsuario) {
        ViviendasFragment fragment = new ViviendasFragment();
        Bundle args = new Bundle();
        args.putInt("idUsuario", idUsuario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viviendas, container, false);
        if (getArguments() != null) {
            idUsuario = getArguments().getInt("idUsuario", -1);
        }
        // Inicializar base de datos
        viviendaEntity = new ViviendaEntity(DBManager.getInstance(requireContext()).getWritableDatabase());
        fotosEntity = new FotosEntity(DBManager.getInstance(requireContext()).getWritableDatabase());
        favoritosEntity = new FavoritosEntity(DBManager.getInstance(requireContext()).getWritableDatabase());

        // Configurar ListView y adaptador
        listView = view.findViewById(R.id.property_list);
        propertyList = new ArrayList<>();
        adapter = new ViviendaAdapter(requireContext(), propertyList, DBManager.getInstance(requireContext()).getWritableDatabase(), idUsuario);
        listView.setAdapter(adapter);

        // Cargar datos de viviendas
        cargarPropiedades();

        return view;

    }

    /**
     * Método `cargarPropiedades`
     * Obtiene todas las viviendas desde la base de datos, incluidas sus fotos y estado de favorito,
     * y las carga en la lista.
     */
    public void cargarPropiedades() {
        executor.execute(() -> {
            List<Vivienda> propiedades = ViviendaLoader.cargarViviendas(viviendaEntity, fotosEntity, favoritosEntity, idUsuario, false);
            mainHandler.post(() -> {
                propertyList.clear();
                propertyList.addAll(propiedades);
                adapter.notifyDataSetChanged();
            });
        });
    }

}
