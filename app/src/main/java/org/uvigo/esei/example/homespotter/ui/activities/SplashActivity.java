package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.esei.example.homespotter.R;

/**
 * Actividad que muestra una pantalla de carga (Splash Screen) al iniciar la aplicación.
 *
 * Esta actividad se utiliza para mostrar una pantalla temporal antes de redirigir al usuario
 * a la actividad principal de la aplicación ({@link MainActivity}).
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Método llamado al crear la actividad.
     * Configura el diseño de la pantalla y establece un temporizador para redirigir
     * automáticamente a la actividad principal después de un breve retraso.
     *
     * @param savedInstanceState el estado previamente guardado de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establece el diseño de la pantalla desde un archivo XML.
        setContentView(R.layout.splash_screen);

        // Utiliza un Handler para retrasar la ejecución de un bloque de código.
        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Crea un Intent para iniciar la actividad principal.
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent); // Inicia la actividad principal.
            finish(); // Finaliza la actividad de splash para que no pueda volver atrás.
        }, 2000); // Tiempo de espera en milisegundos (2 segundos).
    }
}