package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import org.uvigo.esei.example.homespotter.R;

import java.util.Locale;

/**
 * Clase principal que representa la actividad inicial de la aplicación.
 * Permite a los usuarios seleccionar el idioma de la aplicación y cambia
 * dinámicamente la configuración según la selección.
 *
 * Hereda de {@link BaseActivity}.
 */
public class MainActivity extends BaseActivity {

    /**
     * Método llamado al crear la actividad.
     * Configura el botón de selección de idioma y su comportamiento.
     *
     * @param savedInstanceState el estado previamente guardado de la actividad (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtiene el botón de selección de idioma desde el diseño.
        Button btnEspanol = findViewById(R.id.btn_select_language);

        // Asigna una acción al botón para mostrar un diálogo de selección de idioma.
        btnEspanol.setOnClickListener(v -> mostrarDialogoSeleccionIdioma());
    }

    /**
     * Devuelve el identificador del recurso de diseño asociado a esta actividad.
     *
     * @return el ID del recurso de diseño (R.layout.activity_main).
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    /**
     * Cambia el idioma de la aplicación y reinicia la actividad para aplicar los cambios.
     *
     * @param codigoIdioma el código del idioma en formato ISO (por ejemplo, "es" para español, "en" para inglés).
     */
    private void cambiarIdioma(String codigoIdioma) {
        // Crea un nuevo objeto Locale con el idioma seleccionado.
        Locale nuevoLocale = new Locale(codigoIdioma);
        Locale.setDefault(nuevoLocale); // Establece este idioma como predeterminado.

        // Configura los recursos de la aplicación con el nuevo idioma.
        Configuration config = new Configuration();
        config.setLocale(nuevoLocale);

        // Reinicia la actividad actual para aplicar el cambio.
        Intent intent = new Intent(this, this.getClass());
        finish();
        startActivity(intent);
    }

    /**
     * Guarda el idioma seleccionado en las preferencias compartidas.
     * Esto permite persistir el idioma incluso después de que la aplicación sea cerrada.
     *
     * @param codigoIdioma el código del idioma en formato ISO (por ejemplo, "es" para español, "en" para inglés).
     */
    private void guardarIdioma(String codigoIdioma) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", codigoIdioma);
        editor.apply(); // Aplica los cambios.
    }

    /**
     * Ajusta el contexto base de la actividad según el idioma guardado en las preferencias.
     * Este método se utiliza para configurar el idioma antes de que la actividad sea renderizada.
     *
     * @param newBase el contexto base original de la actividad.
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        // Recupera el idioma guardado en las preferencias compartidas.
        SharedPreferences prefs = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String language = prefs.getString("language", "es"); // Por defecto, español.
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        // Configura el contexto con el nuevo idioma.
        Configuration config = newBase.getResources().getConfiguration();
        config.setLocale(locale);
        Context context = newBase.createConfigurationContext(config);

        // Llama al método base con el contexto actualizado.
        super.attachBaseContext(context);
    }

    /**
     * Muestra un cuadro de diálogo para que el usuario seleccione el idioma de la aplicación.
     *
     * Opciones disponibles: Español e Inglés.
     * Al seleccionar un idioma, se guarda en las preferencias y se aplica inmediatamente.
     */
    private void mostrarDialogoSeleccionIdioma() {
        // Opciones de idioma disponibles.
        String[] idiomas = {"Español", "Inglés"};

        // Construye y muestra el cuadro de diálogo.
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar idioma")
                .setItems(idiomas, (dialog, which) -> {
                    // Maneja la selección del idioma.
                    if (which == 0) { // Español seleccionado.
                        guardarIdioma("es"); // Guarda la selección.
                        cambiarIdioma("es"); // Aplica el cambio.
                    } else if (which == 1) { // Inglés seleccionado.
                        guardarIdioma("en"); // Guarda la selección.
                        cambiarIdioma("en"); // Aplica el cambio.
                    }
                })
                .setNegativeButton("Cancelar", null) // Botón para cancelar.
                .show();
    }
}
