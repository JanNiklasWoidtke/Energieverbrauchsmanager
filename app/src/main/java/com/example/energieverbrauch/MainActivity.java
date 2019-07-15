package com.example.energieverbrauch;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public ProgressBar PBcircle;
    public EditText EditTextMaxVerbrauchSoll;
    public TextView TextViewAktVerbrauch;
    public DrawerLayout drawer;

    float obergrenzeVerbrauch = 0;
    float aktuellerVerbrauch = 10;
    float verbrauchAnteilFloat = 0;
    int verbrauchAnteilInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) { //only switch to Start if app is started initially. Rotating the screen wont cause jumping back to start.
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new start_fragment()).commit();
            navigationView.setCheckedItem(R.id.nav_start);
        }
/*
        TextViewAktVerbrauch = findViewById(R.id.aktVerbrauch);
        TextViewAktVerbrauch.setText(aktuellerVerbrauch + " kWh");

        EditTextMaxVerbrauchSoll = findViewById(R.id.maxVerbrauchSoll);

        EditTextMaxVerbrauchSoll.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String maxVerbrauchSollInput = EditTextMaxVerbrauchSoll.getText().toString().trim();

                if (!TextUtils.isEmpty(maxVerbrauchSollInput)) {

                    obergrenzeVerbrauch = Float.parseFloat(maxVerbrauchSollInput);

                    verbrauchsFortschrittBerechnen();

                    verbrauchsanzeigeAktualisieren();
                }

            }
        });
*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_start:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new start_fragment()).commit();
                break;
            case R.id.nav_MyConsumption:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyConsumption_fragment()).commit();
                break;
            case R.id. nav_MyCounters:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyCounters_fragment()).commit();
                break;
            case R.id. nav_SavingTips:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SavingTips_fragment()).commit();
                break;
            case R.id. nav_Settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Settings_fragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else super.onBackPressed();
    }

    public void verbrauchsFortschrittBerechnen() {
        verbrauchAnteilFloat = aktuellerVerbrauch / obergrenzeVerbrauch * 100;// *100, da Progressbar von 0 bis 100 die Werte interpretier
        verbrauchAnteilInt = Math.round(verbrauchAnteilFloat);
    }

    public void verbrauchsanzeigeAktualisieren() {
        PBcircle = findViewById(R.id.PBcircle);
        if (verbrauchAnteilInt <= 100) PBcircle.setProgress(verbrauchAnteilInt);
        else PBcircle.setProgress(100);

    }
}