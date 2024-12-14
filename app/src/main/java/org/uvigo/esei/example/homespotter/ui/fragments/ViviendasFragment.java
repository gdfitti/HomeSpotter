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

public class ViviendasFragment extends Fragment {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
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
        ListView listView = view.findViewById(R.id.property_list);
        propertyList = new ArrayList<>();
        adapter = new ViviendaAdapter(requireContext(), propertyList, DBManager.getInstance(requireContext()).getWritableDatabase(), idUsuario);
        listView.setAdapter(adapter);

        // Cargar datos de viviendas
        cargarPropiedades();

        return view;

    }

    public void applyFilters(ContentValues filtros, Double minPrice, Double maxPrice) {
        executor.execute(() -> {
            Cursor cursor = viviendaEntity.buscar(filtros, minPrice, maxPrice);
            List<Vivienda> filteredViviendas = new ArrayList<>();
            List<Integer> favoritosList = favoritosEntity.obtenerFavoritosPorUsuario(idUsuario);

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
                    // Agregar la vivienda a la lista filtrada
                    filteredViviendas.add(new Vivienda(id, titulo, tipo, precio, direccion, estado,contacto ,descripcion,idPropietario,favorito, fotos));
                } while (cursor.moveToNext());
                cursor.close();
            }

            mainHandler.post(() -> {
                propertyList.clear();
                propertyList.addAll(filteredViviendas);
                adapter.notifyDataSetChanged();
            });
        });
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
