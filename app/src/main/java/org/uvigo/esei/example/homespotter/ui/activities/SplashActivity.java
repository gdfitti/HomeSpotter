package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.esei.example.homespotter.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Esperar 2 segundos y lanzar la actividad principal
        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 2000); // Duración en milisegundos
    }
}
