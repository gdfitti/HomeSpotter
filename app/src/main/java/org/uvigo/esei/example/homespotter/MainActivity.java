package org.uvigo.esei.example.homespotter;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                } else if (itemId == R.id.nav_properties) {
                    startActivity(new Intent(MainActivity.this, PropertyListActivity.class));
                    return true;
                } else if (itemId == R.id.nav_messages) {
                    startActivity(new Intent(MainActivity.this, FiltersActivity.class));
                    return true;
                } else if (itemId == R.id.nav_favourites) {
                    startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
                    return true;
                }

                return false;
            }
        });
    }
}