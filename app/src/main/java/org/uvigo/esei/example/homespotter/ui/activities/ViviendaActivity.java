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
 * Actividad principal que permite visualizar, filtrar y gestionar las viviendas
 * disponibles en la aplicación HomeSpotter. Proporciona opciones para ver todas las
 * propiedades o solo las asociadas al usuario actual, así como agregar nuevas propiedades.
 */
public class ViviendaActivity extends BaseActivity {
    private int idUsuario;

    /**
     * Método llamado al crear la actividad.
     * Inicializa el menú inferior de navegación, la barra de herramientas y carga
     * el fragmento predeterminado (ViviendasFragment) para mostrar todas las propiedades.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recuperar el ID del usuario actual desde SharedPreferences.
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        idUsuario = sharedPreferences.getInt("user_id", -1);

        // Configurar el ícono de navegación inferior para la sección de propiedades.
        changeBottomNavigationIcon(R.id.nav_properties, R.drawable.ic_home_selected);

        // Configurar la barra de herramientas.
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.property_toolbar);
        setSupportActionBar(toolbar);

        // Configurar el menú de navegación inferior.
        BottomNavigationView bottomNavigationView = findViewById(R.id.appbar_navigation);

        // Cargar el fragmento de "Viviendas" por defecto al iniciar la actividad.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ViviendasFragment.newInstance(idUsuario))
                    .commit();
        }

        // Configurar los listeners para manejar los eventos de navegación en el menú inferior.
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.menu_all_properties) {
                selectedFragment = ViviendasFragment.newInstance(idUsuario);
            } else if (item.getItemId() == R.id.menu_my_properties) {
                selectedFragment = MisViviendasFragment.newInstance(idUsuario);
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }

    /**
     * Método que devuelve el diseño asociado a esta actividad.
     *
     * @return ID del recurso del diseño (R.layout.activity_viviendas).
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_viviendas;
    }

    /**
     * Crea el menú superior de opciones.
     *
     * @param menu El menú donde se inflarán las opciones.
     * @return true si el menú se infló correctamente.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    /**
     * Maneja las acciones seleccionadas en el menú superior.
     *
     * @param item El elemento del menú seleccionado.
     * @return true si la acción fue manejada correctamente.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.apply_filter) {
            showFilterDialog(); // Mostrar el cuadro de diálogo para filtrar propiedades.
            return true;
        } else if (item.getItemId() == R.id.add_propertie) {
            Intent intent = new Intent(ViviendaActivity.this, ViviendaAddActivity.class);
            intent.putExtra("userId", idUsuario); // Pasar el ID del usuario actual.
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Muestra un cuadro de diálogo para filtrar las propiedades.
     * Permite al usuario ingresar criterios como precio, título, dirección, tipo y estado.
     */
    private void showFilterDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_filters, null);

        // Inicializar los campos de texto para ingresar filtros.
        EditText minPriceEditText = dialogView.findViewById(R.id.EditText_min_price);
        EditText maxPriceEditText = dialogView.findViewById(R.id.EditText_max_price);
        EditText titleEditText = dialogView.findViewById(R.id.EditText_property_title);
        EditText addressEditText = dialogView.findViewById(R.id.EditText_property_address);
        EditText typeEditText = dialogView.findViewById(R.id.EditText_property_type);
        EditText stateEditText = dialogView.findViewById(R.id.EditText_property_state);

        // Configurar el diálogo.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.filters))
                .setView(dialogView)
                .setPositiveButton(this.getString(R.string.apply), (dialogInterface, i) -> {
                    ContentValues filtros = new ContentValues();
                    boolean buscar = false;

                    // Procesar los campos de texto y preparar los filtros.
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

                    // Procesar los precios.
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
                        return;
                    }

                    // Aplicar los filtros al fragmento actual si es válido.
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (buscar && fragment instanceof ViviendasFragment) {
                        Toast.makeText(ViviendaActivity.this, "Filtro aplicado", Toast.LENGTH_SHORT).show();
                        ((ViviendasFragment) fragment).applyFilters(filtros, minPrice, maxPrice);
                    } else {
                        Toast.makeText(ViviendaActivity.this, "Los campos están vacíos", Toast.LENGTH_SHORT).show();
                        ((ViviendasFragment) fragment).cargarPropiedades();
                    }
                })
                .setNegativeButton(this.getString(R.string.cancel), null)
                .create()
                .show();
    }

    /**
     * Maneja el resultado de actividades lanzadas desde esta actividad.
     *
     * @param requestCode Código de solicitud enviado.
     * @param resultCode Código de resultado devuelto.
     * @param data Intent con datos adicionales.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            boolean isFavoriteChanged = data.getBooleanExtra("isFavoriteChanged", false);

            if (isFavoriteChanged) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof ViviendasFragment) {
                    ((ViviendasFragment) currentFragment).cargarPropiedades();
                }
            }
        }
    }

    /**
     * Método llamado al reanudar la actividad.
     * Actualiza los datos según el usuario autenticado y recarga el fragmento actual si es necesario.
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int nuevoIdUsuario = sharedPreferences.getInt("user_id", -1);

        if (idUsuario != nuevoIdUsuario) {
            idUsuario = nuevoIdUsuario;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ViviendasFragment.newInstance(idUsuario))
                    .commit();
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof ViviendasFragment) {
                ((ViviendasFragment) currentFragment).cargarPropiedades();
            } else if (currentFragment instanceof MisViviendasFragment) {
                ((MisViviendasFragment) currentFragment).cargarPropiedades();
            }
        }
    }
}
