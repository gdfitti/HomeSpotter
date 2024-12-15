package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.uvigo.esei.example.homespotter.R;

/**
 * BaseActivity
 *
 * Clase base abstracta para todas las actividades que comparten un diseño común con una barra
 * de navegación inferior (BottomNavigationView).
 *
 * Proporciona funcionalidad para inflar diseños específicos en un contenedor,
 * configurar la navegación inferior y cambiar dinámicamente los íconos del menú.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Método llamado al crear la actividad.
     *
     * @param savedInstanceState Estado guardado previamente (si existe).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Inflar el diseño específico de la actividad en el contenedor del diseño base
        getLayoutInflater().inflate(getLayoutResId(), findViewById(R.id.activity_content));

        // Configurar la barra de navegación inferior
        setupBottomNavigation();
    }

    /**
     * Configura la barra de navegación inferior (BottomNavigationView),
     * estableciendo los listeners para manejar la navegación entre actividades.
     */
    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                Intent intent = getIntent(item);

                if (intent != null) {
                    // Evitar crear una nueva instancia si la actividad ya está en la parte superior
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                    // Opcional: aplicar transiciones sin animación
                    overridePendingTransition(0, 0);
                }

                return true;
            });
        }
    }

    /**
     * Determina la intención (Intent) correspondiente al elemento seleccionado
     * en la barra de navegación inferior.
     *
     * @param item Elemento del menú seleccionado.
     * @return La intención para la actividad correspondiente, o null si no existe.
     */
    @Nullable
    private Intent getIntent(MenuItem item) {
        int itemId = item.getItemId();

        Intent intent = null;
        if (itemId == R.id.nav_profile) {
            intent = new Intent(this, PerfilActivity.class);
        } else if (itemId == R.id.nav_properties) {
            intent = new Intent(this, ViviendaActivity.class);
        } else if (itemId == R.id.nav_messages) {
            intent = new Intent(this, ChatListActivity.class);
        } else if (itemId == R.id.nav_favorites) {
            intent = new Intent(this, FavoritesActivity.class);
        }
        return intent;
    }

    /**
     * Cambia dinámicamente el ícono de un elemento seleccionado en la barra de navegación inferior.
     *
     * @param selectedItemId ID del elemento seleccionado.
     * @param selectedIconResId Recurso del ícono para el elemento seleccionado.
     */
    protected void changeBottomNavigationIcon(int selectedItemId, int selectedIconResId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            Menu menu = bottomNavigationView.getMenu();

            // Iterar por todos los elementos del menú
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);

                if (menuItem.getItemId() == selectedItemId) {
                    // Cambiar el ícono del ítem seleccionado
                    menuItem.setIcon(selectedIconResId);
                } else {
                    // Restaurar el ícono predeterminado para los demás ítems
                    if (menuItem.getItemId() == R.id.nav_profile) {
                        menuItem.setIcon(R.drawable.ic_profile_default);
                    } else if (menuItem.getItemId() == R.id.nav_properties) {
                        menuItem.setIcon(R.drawable.ic_home_default);
                    } else if (menuItem.getItemId() == R.id.nav_messages) {
                        menuItem.setIcon(R.drawable.ic_message_default);
                    } else if (menuItem.getItemId() == R.id.nav_favorites) {
                        menuItem.setIcon(R.drawable.ic_favorites_default);
                    }
                }
            }
        }
    }

    /**
     * Método abstracto que debe ser implementado por las clases hijas para
     * proporcionar el ID del diseño específico de cada actividad.
     *
     * @return ID del recurso de diseño de la actividad.
     */
    protected abstract @LayoutRes int getLayoutResId();
}