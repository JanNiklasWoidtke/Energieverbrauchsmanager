package com.example.energieverbrauch;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SettingsFragment.SettingsFragmentListener,
        MyCountersFragment.MyCountersFragmentListener,
        com.example.energieverbrauch.StartFragment.StartFragmentListener,
        StartFragmentJahr.StartFragmentJahrListener,
        com.example.energieverbrauch.AddCounterFragment.AddCounterFragmentListener,
        Test1Fragment.OnFragmentInteractionListener,
        Test2Fragment.OnFragmentInteractionListener,
        StartFragment.OnFragmentInteractionListener,
        StartFragmentJahr.OnFragmentInteractionListener {

    public StartFragment StartFragment;
    public StartFragmentJahr StartFragmentJahr;
    public MyConsumptionFragment MyConsumptionFragment;
    public MyCountersFragment MyCountersFragment;
    public SavingTipsFragment SavingTipsFragment;
    public SettingsFragment SettingsFragment;
    public TabContainerFragment TabContainerFragment;

    public DrawerLayout drawer;

    int progress = 0;
    int monat = 0;
    int anfangsmonat = 0;
    float maxVerbrauch = 0;
    int anzahlZaehler = 0;
    float gesamtVerbrauch = 0;
    float gesamtVerbrauchJahr = 0;
    float maxVerbrauchJahr = 0;
    int progressJahr = 0;
    float preisProEinheit = 0;

    ArrayList<String> zaehlername;
    ArrayList<Float> standBeginn;
    ArrayList<Float> aktuellerStand;
    ArrayList<Float> anteilJedesZaehlers;
    ArrayList<Float> monatlicherGesamtVerbrauch;
    ArrayList<Float> monatlicherMaximalVerbrauch;

    NavigationView navigationView;

    Bundle dataToMyCountersFrag = new Bundle();
    Bundle dataToStartFrag = new Bundle();
    Bundle dataToStartFragJahr = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        zaehlername = new ArrayList<>();
        standBeginn = new ArrayList<>();
        aktuellerStand = new ArrayList<>();
        anteilJedesZaehlers = new ArrayList<>();
        monatlicherGesamtVerbrauch = new ArrayList<>();
        monatlicherMaximalVerbrauch = new ArrayList<>();

        StartFragment = new StartFragment();
        StartFragmentJahr = new StartFragmentJahr();
        MyCountersFragment = new MyCountersFragment();
        MyConsumptionFragment = new MyConsumptionFragment();
        SavingTipsFragment = new SavingTipsFragment();
        SettingsFragment = new SettingsFragment();

        TabContainerFragment = new TabContainerFragment();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Hilfsfunktion
        float hilfsfloat1 = 1;
        float hilfsfloat2 = 9;
        monatlicherGesamtVerbrauch.add(hilfsfloat1);
        monatlicherGesamtVerbrauch.add(hilfsfloat2);

        monatlichesSpeichern();

       // monatsAbgleich();

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); //ermöglicht Blinden App zu nutzen, durch Vorlesefunktion
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) { //only switch to Start if app is started initially. Rotating the screen wont cause jumping back to start.
            bundleDataToStartFragFuellen();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TabContainerFragment).commit();
            navigationView.setCheckedItem(R.id.nav_start);
        }
    }

    public void monatsAbgleich() {

        datenLadenMonat();

        Calendar calendar = Calendar.getInstance();

        if (monat < calendar.get(Calendar.MONTH) + 1)
            if (monat == 0) {                               //verhindert speichern leerer Daten bei erstmaligem Öffnen der App
                monat = calendar.get(Calendar.MONTH) + 1;
                anfangsmonat = monat;
            } else {
                monat = calendar.get(Calendar.MONTH) + 1;
                monatlichesSpeichern();
            }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { //öffnet verschiedene Fragments, je nach Klick im NavDrawer
        switch (item.getItemId()) {
            case R.id.nav_start:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TabContainerFragment()).commit();
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
        drawer.closeDrawer(GravityCompat.START); //nachdem ein Menüpunkt geklickt wurde, schließt sich das Menü nach links(START)
        return true;
    }

    @Override
    public void dataFromStartFragmentToMainActivity(int progressSF, float MaxVerbrauchSF) { //liest Wert aus EditText_StartFragment ab
        progress = progressSF;
        maxVerbrauch = MaxVerbrauchSF;
        datenSpeichernStartFrag();
    }

    @Override
    public void dataFromStartFragmentJahrToMainActivity(int progressSFJ, float maxVerbrauchSFJ) {
        progressJahr = progressSFJ;
        maxVerbrauchJahr = maxVerbrauchSFJ;
        datenSpeichernStartFragJahr();
    }

    @Override
    public void dataFromMyCountersToMainActivity(ArrayList<Float> aktuellerStandMCF, ArrayList<Float> anteilJedesZaehlersMCF, float gesamtVerbrauchMCF) {
        aktuellerStand.clear();
        aktuellerStand = aktuellerStandMCF;

        anteilJedesZaehlers.clear();
        anteilJedesZaehlers = anteilJedesZaehlersMCF;

        gesamtVerbrauch = gesamtVerbrauchMCF;

        datenSpeichernMyCounters();
        MyCountersFragment = new MyCountersFragment();
        bundleDataToMyCountersFragFuellen();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
    }

    @Override
    public void dataFromAddCounterFragmentToMainActivity(String zaehlernameACF, float standBeginnACF, float preisProEinheitACF) {
        if (zaehlername.size() == 0) {
            preisProEinheit = preisProEinheitACF;
        }
        zaehlername.add(zaehlernameACF);
        standBeginn.add(standBeginnACF);
        aktuellerStand.add(standBeginnACF);         //wird ein neuer Zähler hinzugefügt, ist der aktuelle Stand der Anfangsstand
        anzahlZaehler++;
        datenSpeichernMyCounters();
        bundleDataToMyCountersFragFuellen();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
    }

    public void bundleDataToStartFragFuellen() {
        datenLadenStartFragment();
        dataToStartFrag.putInt("progress", progress);
        dataToStartFrag.putFloat("maxVerbrauch", maxVerbrauch);
        dataToStartFrag.putFloat("gesamtVerbrauch", gesamtVerbrauch);
        StartFragment.setArguments(dataToStartFrag);
    }


    public void bundleDataToMyCountersFragFuellen() {
        datenLadenMyCounters();
        dataToMyCountersFrag.putStringArrayList("zaehlername", zaehlername);
        dataToMyCountersFrag.putInt("anzahlZaehler", anzahlZaehler);
        dataToMyCountersFrag.putFloatArray("standBeginn", floatArrayListToFloatArray(standBeginn));
        dataToMyCountersFrag.putFloatArray("aktuellerStand", floatArrayListToFloatArray(aktuellerStand));
        dataToMyCountersFrag.putFloatArray("anteilJedesZaehlers", floatArrayListToFloatArray(anteilJedesZaehlers));
        MyCountersFragment.setArguments(dataToMyCountersFrag);
    }

    public float[] floatArrayListToFloatArray(ArrayList<Float> arrayListFloat) {
        float[] FloatArray = new float[arrayListFloat.size()];
        for (int i = 0; i < arrayListFloat.size(); i++) {
            FloatArray[i] = arrayListFloat.get(i);
        }

        dataToMyCountersFrag.putInt("arrayLaenge", arrayListFloat.size());

        return FloatArray;
    }

    @Override
    public void onBackPressed() { //sorgt dafür, dass bei klicken auf zurück bei geöffnetem Menü nicht die App, sondern das Menü geschlossen wird
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            datenLadenStartFragment();
            bundleDataToStartFragFuellen();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, StartFragmentJahr).commit();
            navigationView.setCheckedItem(R.id.nav_start);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void resetData() {
        zaehlername.clear();
        standBeginn.clear();
        aktuellerStand.clear();
        anteilJedesZaehlers.clear();
        monatlicherGesamtVerbrauch.clear();
        monatlicherMaximalVerbrauch.clear();

        maxVerbrauch = 0;
        progress = 0;
        gesamtVerbrauch = 0;
        anzahlZaehler = 0;
        gesamtVerbrauchJahr = 0;
        maxVerbrauchJahr = 0;
        progressJahr = 0;
        monat = 0;
        anfangsmonat = 0;
        preisProEinheit = 0;

        //vermutlich alles resettet

        datenSpeichernMonatlich();
        datenSpeichernStartFragJahr();
        datenSpeichernStartFrag();
        datenSpeichernMyCounters();

        datenLadenMyCounters();
        datenLadenStartFragment();
        datenLadenStartFragmentJahr();
        datenLadenMonat();
    }

    public void datenSpeichernStartFrag() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Daten von Start Fragment
        editor.putFloat("maxVerbrauch", maxVerbrauch);
        editor.putFloat("gesamtVerbrauch", gesamtVerbrauch);
        editor.putInt("progress", progress);

        editor.apply();
    }

    public void datenSpeichernStartFragJahr() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Daten von Start Fragment Jahr
        editor.putFloat("maxVerbrauchJahr", maxVerbrauchJahr);
        editor.putFloat("gesamtVerbrauchJahr", gesamtVerbrauchJahr);
        editor.putInt("progressJahr", progressJahr);

        editor.apply();
    }

    public void datenSpeichernMyCounters() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        //Daten von MyCounters Fragment
        String zaehlernameString = gson.toJson(zaehlername);
        editor.putString("zaehlername", zaehlernameString);

        String standBeginnString = gson.toJson(standBeginn);
        editor.putString("standBeginn", standBeginnString);

        String aktuellerStandString = gson.toJson(aktuellerStand);
        editor.putString("aktuellerStand", aktuellerStandString);

        String anteilJedesZaehlersString = gson.toJson(anteilJedesZaehlers);
        editor.putString("anteilJedesZaehlers", anteilJedesZaehlersString);

        editor.putFloat("preisProEinheit", preisProEinheit);

        editor.apply();
    }

    public void datenSpeichernMonatlich() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        //Monatliche Daten
        String monatlicherGesamtVerbrauchString = gson.toJson(monatlicherGesamtVerbrauch);
        editor.putString("monatlicherGesamtVerbrauch", monatlicherGesamtVerbrauchString);

        String monatlicherMaxVerbrauchString = gson.toJson(monatlicherMaximalVerbrauch);
        editor.putString("monatlicherMaxVerbrauch", monatlicherMaxVerbrauchString);

        editor.putInt("monat", monat);

        //Anwenden
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

        String aktuellerStandString = sharedPreferences.getString("aktuellerStand", null);
        Type typeArrayListFloat2 = new TypeToken<ArrayList<Float>>() {
        }.getType();
        aktuellerStand = gson.fromJson(aktuellerStandString, typeArrayListFloat2);
        if (aktuellerStand == null) aktuellerStand = new ArrayList<>();

        String anteilJedesZaehlersString = sharedPreferences.getString("anteilJedesZaehlers", null);
        Type typeArrayListFloat3 = new TypeToken<ArrayList<Float>>() {
        }.getType();
        anteilJedesZaehlers = gson.fromJson(anteilJedesZaehlersString, typeArrayListFloat3);
        if (anteilJedesZaehlers == null) anteilJedesZaehlers = new ArrayList<>();

        preisProEinheit = sharedPreferences.getFloat("preisProEinheit", 0);

        anzahlZaehler = zaehlername.size();
    }

    public void datenLadenStartFragment() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);

        maxVerbrauch = sharedPreferences.getFloat("maxVerbrauch", 0);

        gesamtVerbrauch = sharedPreferences.getFloat("gesamtVerbrauch", 0);

        progress = sharedPreferences.getInt("progress", 0);
    }

    public void datenLadenStartFragmentJahr() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);

        maxVerbrauchJahr = sharedPreferences.getFloat("maxVerbrauchJahr", 0);

        progressJahr = sharedPreferences.getInt("progressJahr", 0);
    }

    public void datenLadenMonat() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);

        monat = sharedPreferences.getInt("monat", 0);

        Gson gson = new Gson();

        String monatlicherGesamtVerbrauchString = sharedPreferences.getString("monatlicherGesamtVerbrauch", null);
        Type typeArrayListFloat = new TypeToken<ArrayList<Float>>() {
        }.getType();
        monatlicherGesamtVerbrauch = gson.fromJson(monatlicherGesamtVerbrauchString, typeArrayListFloat);
        if (monatlicherGesamtVerbrauch == null) monatlicherGesamtVerbrauch = new ArrayList<>();

        String monatlicherMaxVerbrauchString = sharedPreferences.getString("monatlicherMaxVerbrauch", null);
        Type typeArrayListFloat2 = new TypeToken<ArrayList<Float>>() {
        }.getType();
        monatlicherMaximalVerbrauch = gson.fromJson(monatlicherMaxVerbrauchString, typeArrayListFloat2);
        if (monatlicherMaximalVerbrauch == null) monatlicherMaximalVerbrauch = new ArrayList<>();
    }

    public void monatlichesSpeichern() {
        monatlicherGesamtVerbrauch.add(gesamtVerbrauch);
        gesamtVerbrauch = 0;
        monatlicherMaximalVerbrauch.add(maxVerbrauch);
        maxVerbrauch = 0;

        datenSpeichernMonatlich();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public Bundle dataToStartFragMethod() {
        datenLadenStartFragment();

        dataToStartFrag.putInt("progress", progress);
        dataToStartFrag.putFloat("maxVerbrauch", maxVerbrauch);
        dataToStartFrag.putFloat("gesamtVerbrauch", gesamtVerbrauch);

        return dataToStartFrag;
    }

    public Bundle dataToStartFragJahrMethod() {
        datenLadenMonat();
        gesamtVerbrauchJahr = 0;
        for (int i = 0; i < monatlicherGesamtVerbrauch.size(); i++) {
            gesamtVerbrauchJahr += monatlicherGesamtVerbrauch.get(i);
        }
        Toast.makeText(this, String.valueOf(gesamtVerbrauchJahr), Toast.LENGTH_SHORT).show();
        datenLadenStartFragmentJahr();

        dataToStartFragJahr.putFloat("gesamtVerbrauchJahr", gesamtVerbrauchJahr);
        dataToStartFragJahr.putFloat("maxVerbrauchJahr", maxVerbrauchJahr);
        dataToStartFragJahr.putInt("progressJahr", progressJahr);
        return dataToStartFragJahr;
    }
}