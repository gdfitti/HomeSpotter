package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
    private int idUsuario;


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
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        idUsuario = sharedPreferences.getInt("user_id", -1);
        changeBottomNavigationIcon(R.id.nav_properties,R.drawable.ic_home_selected);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.property_toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        ActivityResultLauncher<Intent> addViviendaLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == RESULT_OK) {
//                        // Encuentra el fragmento actual visible
//                        ViviendasFragment fragment = (ViviendasFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//                        if (fragment != null) {
//                            fragment.cargarPropiedades(); // Llama al método para recargar la lista
//                        }
//                    }
//                }
//        );
        if (item.getItemId() == R.id.apply_filter) {
            showFilterDialog();
            return true;
        }else if(item.getItemId() == R.id.add_propertie){
            Intent intent = new Intent(ViviendaActivity.this, ViviendaAddActivity.class);
            intent.putExtra("userId", idUsuario); // Pasa el ID del usuario
//            addViviendaLauncher.launch(intent);
            return true;
        }

        return false;
    }

    private void showFilterDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_filters, null);
        EditText minPriceEditText = dialogView.findViewById(R.id.EditText_min_price);
        EditText maxPriceEditText = dialogView.findViewById(R.id.EditText_max_price);
        EditText titleEditText = dialogView.findViewById(R.id.EditText_property_title);
        EditText addressEditText = dialogView.findViewById(R.id.EditText_property_address);
        EditText typeEditText = dialogView.findViewById(R.id.EditText_property_type);
        EditText stateEditText = dialogView.findViewById(R.id.EditText_property_state);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.filters))
                .setView(dialogView);
        builder.setPositiveButton(this.getString(R.string.apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean buscar = false;
                ContentValues filtros = new ContentValues();

                // Procesar los campos de texto
                if (!titleEditText.getText().toString().trim().isEmpty()) {
                    filtros.put("titulo", titleEditText.getText().toString().trim());
                    buscar = true;
                }
                if (!addressEditText.getText().toString().trim().isEmpty()) {
                    filtros.put("direccion", addressEditText.getText().toString().trim());
                    buscar = true;
                }
                if (!typeEditText.getText().toString().trim().isEmpty()) {
                    filtros.put("tipo_vivienda", typeEditText.getText().toString().trim());
                    buscar = true;
                }
                if (!stateEditText.getText().toString().trim().isEmpty()) {
                    filtros.put("estado", stateEditText.getText().toString().trim());
                    buscar = true;
                }

                // Procesar precios
                Double minPrice = null, maxPrice = null;
                try {
                    if (!minPriceEditText.getText().toString().trim().isEmpty()) {
                        minPrice = Double.parseDouble(minPriceEditText.getText().toString().trim());
                        buscar = true;
                    }
                    if (!maxPriceEditText.getText().toString().trim().isEmpty()) {
                        maxPrice = Double.parseDouble(maxPriceEditText.getText().toString().trim());
                        buscar = true;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(ViviendaActivity.this, "Introduce un número válido en los campos de precio.", Toast.LENGTH_SHORT).show();
                    return; // Sal del método si hay un error
                }

                // Aplicar los filtros solo si hay datos válidos
                if (buscar) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragment instanceof ViviendasFragment) {
                        ((ViviendasFragment) fragment).applyFilters(filtros, minPrice, maxPrice);
                    }
                } else {
                    Toast.makeText(ViviendaActivity.this, "Por favor, rellena al menos un campo para aplicar filtros.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(this.getString(R.string.sure_cancel), null);
        builder.create().show();
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
