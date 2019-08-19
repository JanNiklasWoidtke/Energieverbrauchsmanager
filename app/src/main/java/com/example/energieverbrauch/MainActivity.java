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


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MyCountersFragment.MyCountersFragmentListener, com.example.energieverbrauch.StartFragment.StartFragmentListener, com.example.energieverbrauch.AddCounterFragment.AddCounterFragmentListener {

    public StartFragment StartFragment;
    public MyConsumptionFragment MyConsumptionFragment;
    public MyCountersFragment MyCountersFragment;
    public SavingTipsFragment SavingTipsFragment;
    public SettingsFragment SettingsFragment;

    public DrawerLayout drawer;

    public static final String MAXVERBRAUCH = "maxVerbrauch";
    public static final String PROGRESS = "progress";

    int progress = 0;
    float MaxVerbrauch = 0;
    float aktuellerVerbrauch = 10;
    int anzahlZaehler = 0;
    boolean buttonErstelltenZaehlerHinzufuegenClicked = false;
    boolean werteAktualisiert = true;

    ArrayList<String> zaehlername;
    ArrayList<Float> standBeginn;
    ArrayList<Float> preisProEinheit;
    ArrayList<Float> aktuellerStand;

    NavigationView navigationView;

    Bundle dataToMyCountersFrag = new Bundle();
    Bundle dataToStartFrag = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        zaehlername = new ArrayList<>();
        standBeginn = new ArrayList<>();
        preisProEinheit = new ArrayList<>();

        datenLadenMyCounters();

        datenLadenStartFragment();

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

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) { //only switch to Start if app is started initially. Rotating the screen wont cause jumping back to start.
            bundleDataToStartFragFuellen();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, StartFragment).commit();
            navigationView.setCheckedItem(R.id.nav_start);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { //öffnet verschiedene Fragments, je nach Klick im NavDrawer
        switch (item.getItemId()) {
            case R.id.nav_start:
                bundleDataToStartFragFuellen();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, StartFragment).commit();
                break;
            case R.id.nav_MyConsumption:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyConsumptionFragment()).commit();
                break;
            case R.id.nav_MyCounters:
                bundleDataToMyCountersFragFuellen();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
                break;
            case R.id.nav_SavingTips:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SavingTipsFragment()).commit();
                break;
            case R.id.nav_Settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
        }
        datenSpeichern();
        drawer.closeDrawer(GravityCompat.START); //nachdem ein Menüpunkt geklickt wurde, schließt sich das Menü nach links(START)
        return true;
    }

    @Override
    public void dataFromStartFragmentToMainActivity(int progressSF, float MaxVerbrauchSF) { //liest Wert aus EditText_StartFragment ab
        progress = progressSF;
        MaxVerbrauch = MaxVerbrauchSF;
        datenSpeichern();
    }

    @Override
    public void dataFromMyCountersToMainActivity(ArrayList<Float> aktuellerStandMCF, boolean werteAktualisiertMCF) {
        aktuellerStand.clear();
        aktuellerStand = aktuellerStandMCF;
        werteAktualisiert = werteAktualisiertMCF;
        bundleDataToMyCountersFragFuellen();
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
            bundleDataToMyCountersFragFuellen();
            datenSpeichern();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
        }
    }

    public float[] floatArrayListToFloatArray(ArrayList<Float> arrayListFloat) {
        float[] FloatArray = new float[arrayListFloat.size()];
        for (int i = 0; i < arrayListFloat.size(); i++) {
            FloatArray[i] = arrayListFloat.get(i);
        }

        dataToMyCountersFrag.putInt("arrayLaenge", arrayListFloat.size());

        return FloatArray;
    }

    public void bundleDataToStartFragFuellen() {
        dataToStartFrag.putInt("progress", progress);
        dataToStartFrag.putFloat("maxVerbrauch", MaxVerbrauch);
        StartFragment.setArguments(dataToStartFrag);
    }

    public void bundleDataToMyCountersFragFuellen() {
        dataToMyCountersFrag.putStringArrayList("zaehlername", zaehlername);
        dataToMyCountersFrag.putInt("anzahlZaehler", anzahlZaehler);
        dataToMyCountersFrag.putFloatArray("standBeginn", floatArrayListToFloatArray(standBeginn));
        dataToMyCountersFrag.putFloatArray("aktuellerStand", floatArrayListToFloatArray(aktuellerStand));
        dataToMyCountersFrag.putBoolean("werteAktualisiert", werteAktualisiert);
        MyCountersFragment.setArguments(dataToMyCountersFrag);
    }


    @Override
    public void onBackPressed() { //sorgt dafür, dass bei klicken auf zurück bei geöffnetem Menü nicht die App, sondern das Menü geschlossen wird
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StartFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_start);
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

        String aktuellerStandString = gson.toJson(aktuellerStand);
        editor.putString("aktuellerStand", aktuellerStandString);

        String maxVerbrauchString = String.valueOf(MaxVerbrauch);
        editor.putString("maxVerbrauch", maxVerbrauchString);

        editor.putInt("progress", progress);

        editor.apply();
    }

    public void datenLadenMyCounters() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        Gson gson = new Gson();

        String zaehlernameString = sharedPreferences.getString("zaehlername", null);
        Type typeArrayListString = new TypeToken<ArrayList<String>>() {
        }.getType();
        zaehlername = gson.fromJson(zaehlernameString, typeArrayListString);
        if (zaehlername == null) zaehlername = new ArrayList<>();

        String standBeginnString = sharedPreferences.getString("standBeginn", null);
        Type typeArrayListFloat = new TypeToken<ArrayList<Float>>() {
        }.getType();
        standBeginn = gson.fromJson(standBeginnString, typeArrayListFloat);
        if (standBeginn == null) standBeginn = new ArrayList<>();

        String aktuellerStandString = sharedPreferences.getString("standBeginn", null);
        aktuellerStand = gson.fromJson(aktuellerStandString, typeArrayListFloat);
        if (aktuellerStand == null) aktuellerStand = new ArrayList<>();

        anzahlZaehler = zaehlername.size();
    }

    public void datenLadenStartFragment() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);

        String maxVerbrauchString = sharedPreferences.getString("maxVerbrauch", null);
        if (maxVerbrauchString != null) MaxVerbrauch = Float.parseFloat(maxVerbrauchString);

        progress = sharedPreferences.getInt("progress", 0);
    }
}