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

public class MisViviendasFragment extends Fragment {
    private ViviendaAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<Vivienda> propertyList;
    private int idUsuario;
    private ViviendaEntity viviendaEntity;
    private FotosEntity fotosEntity;
    private FavoritosEntity favoritosEntity;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static MisViviendasFragment newInstance(int idUsuario){
        MisViviendasFragment fragment = new MisViviendasFragment();
        Bundle args = new Bundle();
        args.putInt("idUsuario", idUsuario);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mis_viviendas, container, false);
        if (getArguments() != null) {
            idUsuario = getArguments().getInt("idUsuario", -1);
        }
        viviendaEntity = new ViviendaEntity(DBManager.getInstance(requireContext()).getWritableDatabase());
        fotosEntity = new FotosEntity(DBManager.getInstance(requireContext()).getWritableDatabase());
        favoritosEntity = new FavoritosEntity(DBManager.getInstance(requireContext()).getWritableDatabase());

        // Configurar ListView y adaptador
        ListView listView = view.findViewById(R.id.my_property_list);
        propertyList = new ArrayList<>();
        adapter = new ViviendaAdapter(requireContext(), propertyList, DBManager.getInstance(requireContext()).getWritableDatabase(), idUsuario);
        listView.setAdapter(adapter);

        // Cargar datos de viviendas
        cargarPropiedades();

        return view;
    }

    public void cargarPropiedades() {
        executor.execute(() -> {
            List<Vivienda> propiedades = ViviendaLoader.cargarViviendas(viviendaEntity, fotosEntity, favoritosEntity, idUsuario, true);
            mainHandler.post(() -> {
                propertyList.clear();
                propertyList.addAll(propiedades);
                adapter.notifyDataSetChanged();
            });
        });

    }
}
