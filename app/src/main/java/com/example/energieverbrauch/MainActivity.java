package com.example.energieverbrauch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SettingsFragment.SettingsFragmentListener,
        MyCountersFragment.MyCountersFragmentListener,
        com.example.energieverbrauch.StartFragment.StartFragmentListener,
        com.example.energieverbrauch.AddCounterFragment.AddCounterFragmentListener,
        StartFragmentAlt.StartFragmentAltListener,
        StartFragment.OnFragmentInteractionListener,
        StartFragmentJahr.OnFragmentInteractionListener,
        StartFragmentAlt.OnFragmentInteractionListener {

    public StartFragment StartFragment;
    public StartFragmentJahr StartFragmentJahr;
    public Soll_Ist_Vergleich_Fragment MyConsumptionFragment;
    public MyCountersFragment MyCountersFragment;
    public SavingTipsFragment SavingTipsFragment;
    public SettingsFragment SettingsFragment;
    public TabContainerFragmentStart TabContainerFragment;
    public AddCounterFragment AddCounterFragment;
    public StartFragmentAlt StartFragmentAlt;

    public DrawerLayout drawer;

    int monat = 0;
    int anfangsmonat = 0;
    int anfangsMonatDiagramme = 0;
    int tagDerLetzenStandAenderung = 0;
    int aktuellerTagImJahr = 0;
    int anfangsTag = 0;

    float maxVerbrauch = 0;
    int anzahlZaehler = 0;
    float gesamtVerbrauch = 0;
    float gesamtVerbrauchJahr = 0;
    float maxVerbrauchJahr = 0;
    int progressJahr = 0;
    float preisProEinheit = 0;
    float grundBetrag = 0;
    int anzahlPersonen = 1;
    float vorherigerStand = 0;

    boolean benachrichtigungenZulaessig = true;
    boolean darkModeAktiviert = false;
    boolean neuerMonat = false;

    boolean darkModeWechsel = false;

    ArrayList<String> zaehlername;
    ArrayList<Float> standBeginn;
    ArrayList<Float> aktuellerStand;
    ArrayList<Float> monatlicherGesamtVerbrauch;
    ArrayList<Float> monatlicherMaximalVerbrauch;

    NavigationView navigationView;

    Bundle dataToMyCountersFrag = new Bundle();
    Bundle dataToStartFrag = new Bundle();
    Bundle dataToStartFragJahr = new Bundle();
    Bundle dataToSollIst = new Bundle();
    Bundle statesToSettingsFrag = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        zaehlername = new ArrayList<>();
        standBeginn = new ArrayList<>();
        aktuellerStand = new ArrayList<>();
        monatlicherGesamtVerbrauch = new ArrayList<>();
        monatlicherMaximalVerbrauch = new ArrayList<>();

        StartFragment = new StartFragment();
        StartFragmentJahr = new StartFragmentJahr();
        MyCountersFragment = new MyCountersFragment();
        MyConsumptionFragment = new Soll_Ist_Vergleich_Fragment();
        SavingTipsFragment = new SavingTipsFragment();
        SettingsFragment = new SettingsFragment();
        AddCounterFragment = new AddCounterFragment();
        TabContainerFragment = new TabContainerFragmentStart();

        StartFragmentAlt = new StartFragmentAlt();

        super.onCreate(savedInstanceState);
        setMode();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        anfangsTagAbgleich();

        monatsAbgleich();

        erstenTagSetzen();

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); //ermöglicht Blinden App zu nutzen, durch Vorlesefunktion
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (darkModeWechsel){
            bundleDataToSettingsFragFuellen();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment).commit();
            navigationView.setCheckedItem(R.id.nav_Settings);

            darkModeWechsel = false;
            datenSpeichernSettings();
        }

        else if (savedInstanceState == null) { //only switch to Start if app is started initially. Rotating the screen wont cause jumping back to start.
            bundleDataToStartFragFuellen();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, TabContainerFragment).commit();
            navigationView.setCheckedItem(R.id.nav_start);
        }
       /*
        //Push-Benachrichtigun

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "0");
*/
    }

    public void setDarkMode() {
        /**
         * This method is called, when the state of the switch "Dark Mode" is changed. The state of the boolean "darkModeAktiviert" gets toggled.
         * The activity is restarted with the updated color scheme.
         */

        darkModeAktiviert = !darkModeAktiviert;
        // datenSpeichernSettings();

        if(darkModeAktiviert){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            darkModeWechsel = true;
            datenSpeichernSettings();
            finish();
        }

        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            startActivity(new Intent(MainActivity.this, MainActivity.class));
            darkModeWechsel = true;
            datenSpeichernSettings();
            finish();
        }
    }

    public void setMode(){
        /**
         * This method is called when the activity gets started.
         * The boolean "darkModeAktiviert" is loaded from SharedPreferences and then used to determine the color scheme of the app.
         */
        datenLadenSettings();

        if(darkModeAktiviert){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void anfangsTagAbgleich() {
        /**
         * This method is called whenever the activity starts.
         * The integer "anfangsTag" indicates the day in the year, when the app was first started.
         * If the app is started for the very first time, "anfangstag" is set by using the Calendar of android.
         * The value is saved in SharedPreferences for later usage.
         */
        if (anfangsTag == 0) {
            anfangsTag = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("anfangsTag", anfangsTag);
            editor.apply();
        }
    }

    public void erstenTagSetzen() {
        /**
         * This method is called whenever the activity starts.
         * The integer "aktuellerTagImJahr" indicates the current day of the year and is set using the Calendar of android.
         */
        if (aktuellerTagImJahr == 0) {
            aktuellerTagImJahr = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        }
    }

    public void monatsAbgleich() {
        /**
         * This method is called whenever the activity starts.
         * It compares the currently saved value of the integer "monat" to the actual current month of the year.
         * calendar.get(Calendar.Month) returns the current month as an integer, while january corresponds to 0. So 1 is added to get a more usual format of the month.
         * The boolean "neuerMonat" is set to true, so a new DialogBox asking to enter a new usage objective is opened when starting the "StartFragment".
         * If the app is started for the first time, the values "monat", "anfangsmonat" and "anfangsMonatDiagramme" are set to the current month.
         * If the app is already in use, only the current month in "monat" gets updated, while the "anfangsMonatDiagramme" variable is increased by one.
         * All values are saved in SharedPreferences.
         */

        datenLadenMonat();

        Calendar calendar = Calendar.getInstance();

        if (monat < calendar.get(Calendar.MONTH) + 1) {
            neuerMonat = true;

            if (monat == 0) {                               //verhindert speichern leerer Daten bei erstmaligem Öffnen der App
                monat = calendar.get(Calendar.MONTH) + 1;
                anfangsmonat = monat;
                anfangsMonatDiagramme = anfangsmonat;
            } else {
                monat = calendar.get(Calendar.MONTH) + 1;
                anfangsMonatDiagramme++;
                monatlicherGesamtVerbrauch.add(gesamtVerbrauch);
                gesamtVerbrauch = 0;

                SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Gson gson = new Gson();

                datenLadenMyCounters();

                standBeginn = aktuellerStand;

                editor.putFloat("gesamtVerbrauch", gesamtVerbrauch);

                String standBeginnString = gson.toJson(standBeginn);
                editor.putString("standBeginn", standBeginnString);

                editor.apply();
            }

            datenSpeichernMonatlich();
        } else {
            neuerMonat = false;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /**
         * This method is the navigation center of the app.
         * Whenever am item of the NavigationDrawer is selected, the switch-case determines what fragment to open.
         * For specific fragments arguments are set by using bundles to distrubute data from the MainActivity to the respective fragments.
         * After selecting a menu-item, the drawer is closed with an animation to the left of the screen.
         */
        switch (item.getItemId()) {
            case R.id.nav_start:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TabContainerFragmentStart()).commit();
                break;
            case R.id.nav_MyConsumption:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TabContainerFragmentStats()).commit();
                break;
            case R.id.nav_MyCounters:
                bundleDataToMyCountersFragFuellen();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
                break;
            case R.id.nav_SavingTips:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SavingTipsFragment()).commit();
                break;
            case R.id.nav_Settings:
                SettingsFragment = new SettingsFragment();
                bundleDataToSettingsFragFuellen();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, SettingsFragment).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void dataFromStartFragmentToMainActivity(float maxVerbrauchSF) { //liest Wert aus EditText_StartFragment ab
        /**
         * This method is called via the interface of the StartFragment.
         * The usage objective set by the user is added to an ArrayList containing all the objectives.
         * The current usage objective is set in the variable "MaxVerbrauch".
         * The values are saved in SharedPreferences.
         */
        datenLadenMonat();
        monatlicherMaximalVerbrauch.add(maxVerbrauchSF);
        datenSpeichernMonatlich();

        datenLadenStartFragment();
        maxVerbrauch = maxVerbrauchSF;
        datenSpeichernStartFrag();
    }

    @Override
    public void dataFromMyCountersToMainActivity(ArrayList<Float> aktuellerStandMCF, float gesamtVerbrauchMCF) {
        /**
         * This method is called via the interface of the MyCountersFragment.
         * Values for "aktuellerTagImJahr" and "tagDerLetzenStandAenderung" are set and later used for calculating the average daily usage between two inputs.
         * For this "vorherigerStand" also is calculated by summing up the ArrayList containing the monthly standings of powermeters.
         * Afterwards the ArrayLists "aktuellerStand" and "anteilJedesZaehlers" and the variable "gesamtVerbrauch" are cleared and then set to the values transfered from the MyCountersFragment.
         * A new MyCountersFragment is constructed, so that the table containing the information about the devices is newly constructed with the updated values.
         * The arguments for the fragment are set by using a bundle and the "MyCountersFragment" is opened by the FragmentManager.
         */
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);

        aktuellerTagImJahr = sharedPreferences.getInt("aktuellerTagImJahr", 0);

        tagDerLetzenStandAenderung = aktuellerTagImJahr;
        aktuellerTagImJahr = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        datenSpeichernDurchschnitt();

        vorherigerStand = 0;

        for (int i = 0; i < aktuellerStand.size(); i++) {
            vorherigerStand += aktuellerStand.get(i);
        }

        aktuellerStand.clear();
        aktuellerStand = aktuellerStandMCF;

        datenLadenStartFragment();

        gesamtVerbrauch = gesamtVerbrauchMCF;

        datenSpeichernMyCounters();
        datenSpeichernStartFrag();

        MyCountersFragment = new MyCountersFragment();
        bundleDataToMyCountersFragFuellen();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
    }

    @Override
    public void dataFromAddCounterFragmentToMainActivity(String zaehlernameACF, float standBeginnACF, float preisProEinheitACF) {
        /**
         * This method is called via the interface of the "AddCounterFragment".
         * The parameters of a newly created device are transfered to the corresponding variables in the "MainActivity".
         * The price of a unit is only set, when no devices are implemented.
         * The data is saved using SharedPreferences.
         * The fragment "MyCountersFragment" is opened by the FragmentManager.
         */
        if (zaehlername.size() == 0) {
            preisProEinheit = preisProEinheitACF;
        }
        zaehlername.add(zaehlernameACF);
        standBeginn.add(standBeginnACF);
        aktuellerStand.add(standBeginnACF);
        anzahlZaehler++;
        datenSpeichernMyCounters();
        bundleDataToMyCountersFragFuellen();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, MyCountersFragment).commit();
    }

    public void bundleDataToStartFragFuellen() {
        /**
         * This method fills the bundle for the "StartFragment" with the required data.
         * The bundle is set as the arguments of the "StartFragment".
         */
        datenLadenStartFragment();
        datenLadenSettings();
        dataToStartFrag.putFloat("maxVerbrauch", maxVerbrauch);
        dataToStartFrag.putFloat("gesamtVerbrauch", gesamtVerbrauch);
        dataToStartFrag.putFloat("preisProEinheit", preisProEinheit);
        dataToStartFrag.putFloat("grundBetrag", grundBetrag);
        StartFragment.setArguments(dataToStartFrag);
    }

    public void bundleDataToMyCountersFragFuellen() {
        /**
         * This method fills the bundle for the "MyCountersFragment" with the required data.
         * The bundle is set as the arguments of the "MyCountersFragment".
         */
        datenLadenMyCounters();
        dataToMyCountersFrag.putStringArrayList("zaehlername", zaehlername);
        dataToMyCountersFrag.putInt("anzahlZaehler", anzahlZaehler);
        dataToMyCountersFrag.putFloatArray("standBeginn", floatArrayListToFloatArray(standBeginn));
        dataToMyCountersFrag.putFloatArray("aktuellerStand", floatArrayListToFloatArray(aktuellerStand));
        MyCountersFragment.setArguments(dataToMyCountersFrag);
    }

    public void bundleDataToSettingsFragFuellen() {
        /**
         * This method fills the bundle for the "SettingsFragment" with the required data.
         * The bundle is set as the arguments of the "SettingsFragment".
         */
        datenLadenSettings();
        statesToSettingsFrag.putBoolean("benachrichtigungenZulaessig", benachrichtigungenZulaessig);
        statesToSettingsFrag.putBoolean("darkModeAktiviert", darkModeAktiviert);
        statesToSettingsFrag.putFloat("preisProEinheit", preisProEinheit);
        statesToSettingsFrag.putFloat("grundBetrag", grundBetrag);
        statesToSettingsFrag.putInt("anzahlPersonen", anzahlPersonen);

        SettingsFragment.setArguments(statesToSettingsFrag);
    }

    public float[] floatArrayListToFloatArray(ArrayList<Float> arrayListFloat) {
        /**
         * This method transforms an ArrayList<Float> to a C-Style Float[].
         */
        float[] FloatArray = new float[arrayListFloat.size()];
        for (int i = 0; i < arrayListFloat.size(); i++) {
            FloatArray[i] = arrayListFloat.get(i);
        }

        return FloatArray;
    }

    public void setPersons(int persons) {
        /**
         * This method is called via the interface of the "SettingsFragment".
         * The number of persons in the household is set and saved in SharedPreferences.
         */
        anzahlPersonen = persons;
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("anzahlPersonen", anzahlPersonen);
        editor.apply();
    }

    public void setGrundBetrag(float grundBetragSF) {
        /**
         * This method is called via the interface of the "SettingsFragment".
         * The basic price "grundBetrag" is set and saved in SharedPreferences.
         */
        grundBetrag = grundBetragSF;
        datenSpeichernSettings();
    }

    public void setPreisProEinheit(float preisProEinheitSF) {
        /**
         * This method is called via the interface of the "SettingsFragment".
         * The price per unit "preisProEinheit" is set and saved in SharedPreferences.
         */
        preisProEinheit = preisProEinheitSF;
        datenSpeichernSettings();
    }

    @Override
    public void onBackPressed() {
        /**
         * This method overrides "OnBackPressed()".
         * If the NavigationDrawer is open, pressing back closes it.
         * Else, the "TabContainerFragmentStart" containing the "StartFragment" is opened.
         * The checked item in the NavigationDrawer is set to start.
         */
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            datenLadenStartFragment();
            bundleDataToStartFragFuellen();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TabContainerFragmentStart()).commit();
            navigationView.setCheckedItem(R.id.nav_start);
        }
    }

    public void resetData() {
        /**
         * This method is called via the interface of the "SettingsFragment".
         * All data is reset to the default values and saved in SharedPreferences.
         * A new "SettingsFragment" is opened and the method "setDarkMode()" is called.
         */
        zaehlername.clear();
        standBeginn.clear();
        aktuellerStand.clear();
        monatlicherGesamtVerbrauch.clear();
        monatlicherMaximalVerbrauch.clear();

        maxVerbrauch = 0;
        gesamtVerbrauch = 0;
        anzahlZaehler = 0;
        gesamtVerbrauchJahr = 0;
        maxVerbrauchJahr = 0;
        progressJahr = 0;
        vorherigerStand = 0;

        monat = 0;
        anfangsmonat = 0;
        anfangsMonatDiagramme = 0;
        anfangsTag = 0;

        preisProEinheit = 0;
        grundBetrag = 0;

        anzahlPersonen = 1;

        darkModeWechsel = false;

        darkModeAktiviert = true;
        benachrichtigungenZulaessig = true;

        neuerMonat = true;

        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("anfangsTag", anfangsTag);
        editor.putFloat("vorherigerStand", vorherigerStand);
        editor.apply();

        datenSpeichernStartFrag();
        datenSpeichernStartFragJahr();
        datenSpeichernMyCounters();
        datenSpeichernMonatlich();
        datenSpeichernSettings();

        datenLadenMyCounters();
        datenLadenStartFragment();
        datenLadenMonat();
        datenLadenSettings();

        navigationView.setCheckedItem(R.id.nav_Settings);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
        setDarkMode();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        /**
         * This method has to be implemented to use TabLayouts.
         */
    }

    public void gesamtVerbrauchJahrBerechnen() {
        /**
         * This method calculates the usage of the previous 12 months.
         * If less than 12 monthly values are available, only those are summed up.
         */

        gesamtVerbrauchJahr = 0;

        int zaehler = monatlicherGesamtVerbrauch.size() - 12;

        for (int i = 0; i < 12 && i < monatlicherGesamtVerbrauch.size(); i++) {
            if (zaehler < 0) {
                zaehler = 0;
            }
            gesamtVerbrauchJahr += monatlicherGesamtVerbrauch.get(zaehler);
            zaehler++;
        }
    }

    public void maxVerbrauchJahrBerechnen() {
        /**
         * This method calculates the sum of the usage objectives of the previous 12 months.
         * If less than 12 monthly values are available, only those are summed up.
         */

        maxVerbrauchJahr = 0;

        int zaehler = monatlicherMaximalVerbrauch.size() - 12;

        for (int i = 0; i < 12 && i < monatlicherMaximalVerbrauch.size(); i++) {
            if (zaehler < 0) {
                zaehler = 0;
            }
            maxVerbrauchJahr += monatlicherMaximalVerbrauch.get(zaehler);
            zaehler++;
        }
    }

    /**
     * The following methods are used to transfer bundles to Fragments opened by TabLayouts.
     * For a TabLayout to work, we need to call a new instance of it. Therefore set arguments are lost.
     * To yet transfer data to the fragments, methods returning the required bundles are used.
     */

    public Bundle dataToStartFragMethod() {
        datenLadenStartFragment();
        datenLadenSettings();

        dataToStartFrag.putFloat("maxVerbrauch", maxVerbrauch);
        dataToStartFrag.putFloat("gesamtVerbrauch", gesamtVerbrauch);
        dataToStartFrag.putFloat("preisProEinheit", preisProEinheit);
        dataToStartFrag.putFloat("grundBetrag", grundBetrag);

        dataToStartFrag.putBoolean("neuerMonat", neuerMonat);

        neuerMonat = false;

        return dataToStartFrag;
    }

    public Bundle dataToStartFragJahrMethod() {
        datenLadenMonat();

        gesamtVerbrauchJahrBerechnen();
        maxVerbrauchJahrBerechnen();

        datenLadenMyCounters();
        datenLadenSettings();
        datenLadenStartFragment();

        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        sharedPreferences.getInt("anfangsTag", 0);

        dataToStartFragJahr.putFloat("gesamtVerbrauchJahr", gesamtVerbrauchJahr);
        dataToStartFragJahr.putFloat("maxVerbrauchJahr", maxVerbrauchJahr);
        dataToStartFragJahr.putFloat("preisProEinheit", preisProEinheit);
        dataToStartFragJahr.putFloat("grundBetrag", grundBetrag);
        dataToStartFragJahr.putFloat("gesamtVerbrauchAktMonat", gesamtVerbrauch);
        dataToStartFragJahr.putInt("anfangsTag", anfangsTag);
        if (monatlicherGesamtVerbrauch != null) {
            dataToStartFragJahr.putInt("anzahlMonate", monatlicherGesamtVerbrauch.size());
        }
        return dataToStartFragJahr;
    }

    public Bundle dataToReferenzwerteFragMethod() {
        Bundle dataToReferenzFrag = new Bundle();

        datenLadenMonat();
//        datenLadenStartFragment(); // kann vllt. gelöscht werden

        gesamtVerbrauchJahrBerechnen();

        dataToReferenzFrag.putFloat("eigenerVerbrauch", gesamtVerbrauchJahr + gesamtVerbrauch);

        return dataToReferenzFrag;
    }

    public Bundle dataToDurchschnitt() {
        Bundle dataToDurchschnitt = new Bundle();

        datenLadenMonat();
        gesamtVerbrauchJahrBerechnen();

        datenLadenSettings();

        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);

        dataToDurchschnitt.putFloat("aktuellerStand", gesamtVerbrauchJahr + sharedPreferences.getFloat("gesamtVerbrauch", 0));
        dataToDurchschnitt.putFloat("vorherigerStand", sharedPreferences.getFloat("vorherigerStand", 0));
        dataToDurchschnitt.putInt("aktuellerTagImJahr", sharedPreferences.getInt("aktuellerTagImJahr", 0));
        dataToDurchschnitt.putInt("tagDerLetztenEingabe", sharedPreferences.getInt("tagDerLetztenEingabe", 0));
        dataToDurchschnitt.putFloat("preisProEinheit", preisProEinheit);

        return dataToDurchschnitt;
    }

    public Bundle dataToSollIst() {
        datenLadenSettings();
        datenLadenMonat();

        dataToSollIst.putInt("anfangsMonatDiagramme", anfangsMonatDiagramme);
        if (monatlicherGesamtVerbrauch != null || gesamtVerbrauch != 0) {
            dataToSollIst.putFloatArray("monatlicherGesamtVerbrauch", floatArrayListToFloatArray(monatlicherGesamtVerbrauch));
            dataToSollIst.putFloatArray("monatlicherMaxVerbrauch", floatArrayListToFloatArray(monatlicherMaximalVerbrauch));
        }
        dataToSollIst.putFloat("aktuellerVerbrauch", gesamtVerbrauch);
        dataToSollIst.putInt("anzahlPersonen", anzahlPersonen);

        return dataToSollIst;
    }

    /**
     * The following methods take care of saving and loading data using SharedPreferences.
     */

    public void datenSpeichernDurchschnitt() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("aktuellerTagImJahr", aktuellerTagImJahr);
        editor.putInt("tagDerLetztenEingabe", tagDerLetzenStandAenderung);

        editor.apply();
    }

    public void datenSpeichernStartFrag() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Daten von Start Fragment
        editor.putFloat("maxVerbrauch", maxVerbrauch);
        editor.putFloat("gesamtVerbrauch", gesamtVerbrauch);

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

        editor.putFloat("preisProEinheit", preisProEinheit);

        editor.putFloat("vorherigerStand", vorherigerStand);

        editor.apply();
    }

    public void datenSpeichernMonatlich() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String monatlicherGesamtVerbrauchString = gson.toJson(monatlicherGesamtVerbrauch);
        editor.putString("monatlicherGesamtVerbrauch", monatlicherGesamtVerbrauchString);

        String monatlicherMaxVerbrauchString = gson.toJson(monatlicherMaximalVerbrauch);
        editor.putString("monatlicherMaxVerbrauch", monatlicherMaxVerbrauchString);

        editor.putInt("monat", monat);

        editor.putInt("anfangsMonat", anfangsmonat);

        editor.putInt("anfangsMonatDiagramme", anfangsMonatDiagramme);

        editor.apply();
    }

    public void datenSpeichernSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("benachrichtigungenZulaessig", benachrichtigungenZulaessig);

        editor.putBoolean("darkModeAktiviert", darkModeAktiviert);

        editor.putFloat("grundBetrag", grundBetrag);

        editor.putFloat("preisProEinheit", preisProEinheit);

        editor.putInt("anzahlPersonen", anzahlPersonen);

        editor.putBoolean("darkModeWechsel", darkModeWechsel);

        editor.apply();
    }

    public void datenLadenSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);

        benachrichtigungenZulaessig = sharedPreferences.getBoolean("benachrichtigungenZulaessig", true);

        darkModeAktiviert = sharedPreferences.getBoolean("darkModeAktiviert", false);

        preisProEinheit = sharedPreferences.getFloat("preisProEinheit", 0);

        grundBetrag = sharedPreferences.getFloat("grundBetrag", 0);

        anzahlPersonen = sharedPreferences.getInt("anzahlPersonen", 1);

        darkModeWechsel = sharedPreferences.getBoolean("darkModeWechsel", false);
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

        preisProEinheit = sharedPreferences.getFloat("preisProEinheit", 0);

        anzahlZaehler = zaehlername.size();
    }

    public void datenLadenStartFragment() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared Preferences", MODE_PRIVATE);

        maxVerbrauch = sharedPreferences.getFloat("maxVerbrauch", 0);
        gesamtVerbrauch = sharedPreferences.getFloat("gesamtVerbrauch", 0);
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

        gesamtVerbrauch = sharedPreferences.getFloat(("gesamtVerbrauch"), 0); // falls datenLadenStartFrag unnötig

        anfangsmonat = sharedPreferences.getInt("anfangsMonat", 0);

        anfangsMonatDiagramme = sharedPreferences.getInt("anfangsMonatDiagramme", 0);
    }

}