package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.database.DBManager;
import org.uvigo.esei.example.homespotter.database.FavoritosEntity;
import org.uvigo.esei.example.homespotter.database.FotosEntity;
import org.uvigo.esei.example.homespotter.database.ViviendaEntity;
import org.uvigo.esei.example.homespotter.models.Vivienda;
import org.uvigo.esei.example.homespotter.ui.adapters.ViviendaAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViviendaActivity extends AppCompatActivity {

    private ViviendaEntity viviendaEntity;
    private FotosEntity fotosEntity;
    private FavoritosEntity favoritosEntity;
    private List<Vivienda> propertyList;
    private ViviendaAdapter adapter;
    private int idUsuario = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viviendas);

        // Configurar la base de datos
        viviendaEntity = new ViviendaEntity(DBManager.getInstance(this).getWritableDatabase());
        fotosEntity = new FotosEntity(DBManager.getInstance(this).getWritableDatabase());
        favoritosEntity = new FavoritosEntity(DBManager.getInstance(this).getWritableDatabase());

        // Configurar el ListView
        ListView listView = findViewById(R.id.property_list);
        propertyList = new ArrayList<>();
        adapter = new ViviendaAdapter(this, propertyList, DBManager.getInstance(this).getWritableDatabase(),idUsuario);
        listView.setAdapter(adapter);

        // Cargar datos desde la base de datos
        cargarPropiedades();

        // Configurar botones flotantes
        findViewById(R.id.button_back).setOnClickListener(v -> finish());
        findViewById(R.id.button_add_property).setOnClickListener(v -> {
            // Acción para añadir nueva propiedad

        });
    }

    private void cargarPropiedades() {
        propertyList.clear();
        List<Integer> favoritos = favoritosEntity.obtenerFavoritosPorUsuario(idUsuario);
        Cursor cursor = viviendaEntity.buscar(null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id_vivienda"));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo_vivienda"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                String direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"));
                String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
                String contacto = cursor.getString(cursor.getColumnIndexOrThrow("contacto"));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"));
                int idPropietario = cursor.getInt(cursor.getColumnIndexOrThrow("propietario_id"));
                boolean favorito = favoritos.contains(id);
                // Crear una nueva lista de fotos para cada vivienda
                List<String> fotos = new ArrayList<>();
                Cursor fotosCursor = fotosEntity.obtenerFotosPorVivienda(id);
                if (fotosCursor != null && fotosCursor.moveToFirst()) {
                    do {
                        fotos.add(fotosCursor.getString(fotosCursor.getColumnIndexOrThrow("url_foto")));
                    } while (fotosCursor.moveToNext());
                    fotosCursor.close();
                }

                // Crear un objeto Vivienda y agregarlo a la lista
                propertyList.add(new Vivienda(id, titulo, tipo, precio, direccion, estado, contacto, descripcion, idPropietario, favorito, fotos));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Notificar cambios al adaptador
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Recargar los datos desde la base de datos

            cargarPropiedades();

            // Notificar cambios al adaptador
            adapter.notifyDataSetChanged();
        }
    }

}
