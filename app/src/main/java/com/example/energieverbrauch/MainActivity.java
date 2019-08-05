package com.example.energieverbrauch;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, com.example.energieverbrauch.StartFragment.StartFragmentListener, com.example.energieverbrauch.AddCounterFragment.AddCounterFragmentListener {

    public StartFragment StartFragment;
    public MyConsumptionFragment MyConsumptionFragment;
    public MyCountersFragment MyCountersFragment;
    public SavingTipsFragment SavingTipsFragment;
    public SettingsFragment SettingsFragment;

    public DrawerLayout drawer;

    int progress = 0;
    float MaxVerbrauch = 0;
    float aktuellerVerbrauch = 10;
    int anzahlZaehler = 0;
    boolean buttonErstelltenZaehlerHinzufuegenClicked = false;

    ArrayList<String> zaehlername;
    ArrayList<Float> standBeginn;
    ArrayList<Float> preisProEinheit;

    Bundle dataToMyCountersFrag = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        zaehlername = new ArrayList<>();
        standBeginn = new ArrayList<>();
        preisProEinheit = new ArrayList<>();

        datenLaden();

        StartFragment = new StartFragment();
        MyCountersFragment = new MyCountersFragment();
        MyConsumptionFragment = new MyConsumptionFragment();
        SavingTipsFragment = new SavingTipsFragment();
        SettingsFragment = new SettingsFragment();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); //ermöglicht Blinden App zu nutzen, durch Vorlesefunktion
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) { //only switch to Start if app is started initially. Rotating the screen wont cause jumping back to start.
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StartFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_start);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { //öffnet verschiedene Fragments, je nach Klick im NavDrawer
        datenSpeichern();
        switch (item.getItemId()) {
            case R.id.nav_start:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StartFragment()).commit();
                break;
            case R.id.nav_MyConsumption:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyConsumptionFragment()).commit();
                break;
            case R.id.nav_MyCounters:
                dataToMyCountersFrag.putStringArrayList("zaehlername", zaehlername);
                dataToMyCountersFrag.putFloatArray("standBeginn", floatArrayListToFloatArray(standBeginn));
                dataToMyCountersFrag.putInt("anzahlZaehler", anzahlZaehler);
                MyCountersFragment.setArguments(dataToMyCountersFrag);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
                break;
            case R.id.nav_SavingTips:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SavingTipsFragment()).commit();
                break;
            case R.id.nav_Settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START); //nachdem ein Menüpunkt geklickt wurde, schließt sich das Menü nach links(START)
        return true;
    }

    @Override
    public void dataFromStartFragmentToMainActivity(int progressSF, float MaxVerbrauchSF) { //liest Wert aus EditText_StartFragment ab
        progress = progressSF;
        MaxVerbrauch = MaxVerbrauchSF;
    }

    @Override
    public void dataFromAddCounterFragmentToMainActivity(String zaehlernameACF, float standBeginnACF, float preisProEinheitACF, boolean buttonErstelltenZaehlerHinzufuegenClickedACF) {
        zaehlername.add(zaehlernameACF);
        standBeginn.add(standBeginnACF);
        preisProEinheit.add(preisProEinheitACF);
        buttonErstelltenZaehlerHinzufuegenClicked = buttonErstelltenZaehlerHinzufuegenClickedACF;
        if (buttonErstelltenZaehlerHinzufuegenClicked) {
            buttonErstelltenZaehlerHinzufuegenClicked = false;
            anzahlZaehler++;
            dataToMyCountersFrag.putStringArrayList("zaehlername", zaehlername);
            dataToMyCountersFrag.putInt("anzahlZaehler", anzahlZaehler);
            dataToMyCountersFrag.putFloatArray("standBeginn", floatArrayListToFloatArray(standBeginn));
            MyCountersFragment.setArguments(dataToMyCountersFrag);
            datenSpeichern();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
        }
    }

    public float[] floatArrayListToFloatArray(ArrayList<Float> standBeginn) {
        int sizeFloatArrayList = standBeginn.size();
        float[] standBeginnFloatArray = new float[sizeFloatArrayList];
        for (int i = 0; i < sizeFloatArrayList; i++) {
            standBeginnFloatArray[i] = standBeginn.get(i);
        }

        dataToMyCountersFrag.putInt("arrayLaenge", sizeFloatArrayList);

        return standBeginnFloatArray;
    }

    public float updateHint() {
        return MaxVerbrauch;
    }

    public int sendProgressData() {
        return progress;
    }

    public float sendAktuelleVerbrauchsData() {
        return aktuellerVerbrauch;
    }


    @Override
    public void onBackPressed() { //sorgt dafür, dass bei klicken auf zurück bei geöffnetem Menü nicht die App, sondern das Menü geschlossen wird
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StartFragment()).commit();
        }
    }

    @Override
    protected void onDestroy() {
        datenSpeichern();
        super.onDestroy();
    }

    public void datenSpeichern() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String zaehlernameString = gson.toJson(zaehlername);
        editor.putString("zaehlername", zaehlernameString);

        String standBeginnString = gson.toJson(standBeginn);
        editor.putString("standBeginn", standBeginnString);

        String preisProEinheitString = gson.toJson(preisProEinheit);
        editor.putString("preisProEinheit", preisProEinheitString);

        String anzahlZaehlerString = gson.toJson(anzahlZaehler);
        editor.putString("anzahlZaehler", anzahlZaehlerString);

        String maxVerbrauchString = gson.toJson(MaxVerbrauch);
        editor.putString("maxVerbrauch", maxVerbrauchString);

        String progressString = gson.toJson(progress);
        editor.putString("progress", progressString);

        editor.apply();
    }

    public void datenLaden() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        Gson gson = new Gson();

        String zaehlernameString = sharedPreferences.getString("zaehlername", null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        zaehlername = gson.fromJson(zaehlernameString, type);
        if (zaehlername == null) zaehlername = new ArrayList<>();

        String standBeginnString = sharedPreferences.getString("standBeginn", null);
        Type type1 = new TypeToken<ArrayList<Float>>() {
        }.getType();
        standBeginn = gson.fromJson(standBeginnString, type1);
        if (standBeginn == null) standBeginn = new ArrayList<>();

        anzahlZaehler = zaehlername.size();
    }
}