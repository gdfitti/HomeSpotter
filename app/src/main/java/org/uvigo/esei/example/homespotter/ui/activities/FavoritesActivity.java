package org.uvigo.esei.example.homespotter.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.uvigo.esei.example.homespotter.R;


public class FavoritesActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstance){
        super.onCreate(savedInstance);
        changeBottomNavigationIcon(R.id.nav_favorites,R.drawable.heart);
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_favorites;
    }
}

