package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.uvigo.esei.example.homespotter.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Configuración del menú de navegación inferior utilizando setOnItemSelectedListener
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                    return true;
                } else if (itemId == R.id.nav_properties) {
                    startActivity(new Intent(MainActivity.this, ViviendaActivity.class));
                    return true;
                } else if (itemId == R.id.nav_messages) {
                    startActivity(new Intent(MainActivity.this, MensajesActivity.class));
                    return true;
                } else if (itemId == R.id.nav_favourites) {
                    startActivity(new Intent(MainActivity.this, MensajesActivity.class));
                    return true;
                }

                return false;
            }
        });
    }
}