package org.uvigo.esei.example.homespotter.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.uvigo.esei.example.homespotter.R;

public class PerfilActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeBottomNavigationIcon(R.id.nav_profile,R.drawable.ic_profile_selected);
        Button btnBackToHome = findViewById(R.id.btn_back_to_home);
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresa al menú principal (MainActivity)
                Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_profile;
    }
}
