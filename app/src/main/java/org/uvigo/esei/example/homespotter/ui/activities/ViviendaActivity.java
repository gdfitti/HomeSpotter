package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.uvigo.esei.example.homespotter.R;
import org.uvigo.esei.example.homespotter.ui.fragments.MisViviendasFragment;
import org.uvigo.esei.example.homespotter.ui.fragments.ViviendasFragment;


/**
 * Clase ViviendaActivity
 *
 * Actividad principal que muestra una lista de viviendas disponibles en la aplicación HomeSpotter.
 * Permite interactuar con las viviendas y muestra sus detalles, fotos y estado de favorito.
 *
 * Funcionalidades principales:
 * - Mostrar una lista de viviendas obtenidas de la base de datos.
 * - Indicar si una vivienda está marcada como favorita por el usuario.
 * - Cargar fotos asociadas a cada vivienda.
 * - Botón para agregar una nueva propiedad (pendiente de implementación).
 *
 * Dependencias:
 * - Modelos: `Vivienda`.
 * - Adaptadores: `ViviendaAdapter`.
 * - Entidades: `ViviendaEntity`, `FotosEntity`, `FavoritosEntity`.
 */
public class ViviendaActivity extends BaseActivity {
    private int idUsuario = 1;

    /**
     * Método `onCreate`
     * Configura la actividad al ser creada. Inicializa la base de datos, los adaptadores
     * y los elementos de la interfaz gráfica.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBottomNavigationIcon(R.id.nav_properties,R.drawable.ic_home_selected);
        ActivityResultLauncher<Intent> addViviendaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Encuentra el fragmento actual visible
                        ViviendasFragment fragment = (ViviendasFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                        if (fragment != null) {
                            fragment.cargarPropiedades(); // Llama al método para recargar la lista
                        }
                    }
                }
        );

        // Configurar botones flotantes
        findViewById(R.id.button_back).setOnClickListener(v -> finish());
        findViewById(R.id.button_add_property).setOnClickListener(v -> {
            Intent intent = new Intent(ViviendaActivity.this, ViviendaAddActivity.class);
            intent.putExtra("userId", idUsuario); // Pasa el ID del usuario
            addViviendaLauncher.launch(intent);
        });

        // Configurar el BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.appbar_navigation);

        // Cargar la sección de "Viviendas" por defecto
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ViviendasFragment.newInstance(idUsuario))
                    .commit();
        }

        // Configurar listener para el BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.menu_all_properties) {
                selectedFragment = ViviendasFragment.newInstance(idUsuario); // Fragmento para todas las viviendas

            }else if(item.getItemId() == R.id.menu_my_properties){
                selectedFragment = MisViviendasFragment.newInstance(idUsuario); // Fragmento para "Mis Viviendas"
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_viviendas;
    }


    /**
     * Método `onActivityResult`
     * Maneja los resultados de actividades lanzadas desde esta actividad. En este caso,
     * recarga las propiedades si el resultado es exitoso.
     *
     * @param requestCode Código de solicitud.
     * @param resultCode Código de resultado.
     * @param data Intent con datos adicionales.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            boolean isFavoriteChanged = data.getBooleanExtra("isFavoriteChanged", false);

            if (isFavoriteChanged) {
                // Obtener el fragmento activo
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (currentFragment instanceof ViviendasFragment) {
                    // Notificar al fragmento que recargue los datos
                    ((ViviendasFragment) currentFragment).cargarPropiedades();
                }
            }
        }
    }



}
