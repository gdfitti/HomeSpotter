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

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Inflar el diseño específico de la actividad en el contenedor
        getLayoutInflater().inflate(getLayoutResId(), findViewById(R.id.activity_content));

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                Intent intent = getIntent(item);

                if (intent != null) {
                    // Evita crear una nueva instancia si la actividad ya está en la parte superior
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(0, 0); // Opcional: transiciones sin animación
                }

                return true;
            });
        }
    }

    @Nullable
    private Intent getIntent(MenuItem item) {
        int itemId = item.getItemId();

        Intent intent = null;
        if (itemId == R.id.nav_profile) {
            intent = new Intent(this, PerfilActivity.class);
        } else if (itemId == R.id.nav_properties) {
            intent = new Intent(this, ViviendaActivity.class);
        } else if (itemId == R.id.nav_messages) {
            intent = new Intent(this, MensajeActivity.class);
        } else if (itemId == R.id.nav_favorites) {
            intent = new Intent(this, FavoritesActivity.class);
        }
        return intent;
    }

    protected void changeBottomNavigationIcon(int selectedItemId, int selectedIconResId){
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            Menu menu = bottomNavigationView.getMenu();

            // Iterar por todos los ítems del menú
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
                    }else if (menuItem.getItemId() == R.id.nav_messages) {
                        menuItem.setIcon(R.drawable.ic_message_default);
                    }else if (menuItem.getItemId() == R.id.nav_favorites) {
                        menuItem.setIcon(R.drawable.ic_favorites_default);
                    }
                }
            }
        }
    }

    protected abstract @LayoutRes int getLayoutResId();
}
