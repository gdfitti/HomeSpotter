package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.uvigo.esei.example.homespotter.R;

import java.util.Locale;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button btnEspanol = findViewById(R.id.btn_select_language);

        btnEspanol.setOnClickListener(v -> mostrarDialogoSeleccionIdioma());

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    private void cambiarIdioma(String codigoIdioma) {
        Locale nuevoLocale = new Locale(codigoIdioma);
        Locale.setDefault(nuevoLocale);

        // Configurar el idioma
        Configuration config = new Configuration();
        config.setLocale(nuevoLocale);

        // Reiniciar la actividad
        Intent intent = new Intent(this, this.getClass());
        finish();
        startActivity(intent);
    }

    private void guardarIdioma(String codigoIdioma) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", codigoIdioma);
        editor.apply();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String language = prefs.getString("language", "es"); // Español por defecto
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);

        super.attachBaseContext(context);
    }

    private void mostrarDialogoSeleccionIdioma() {
        // Opciones de idioma
        String[] idiomas = {"Español", "Inglés"};

        // Crear y mostrar el diálogo
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar idioma")
                .setItems(idiomas, (dialog, which) -> {
                    // Manejar selección del idioma
                    if (which == 0) { // Español
                        guardarIdioma("es"); // Guardar idioma primero
                        cambiarIdioma("es");
                    } else if (which == 1) { // Inglés
                        guardarIdioma("en"); // Guardar idioma primero
                        cambiarIdioma("en");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}