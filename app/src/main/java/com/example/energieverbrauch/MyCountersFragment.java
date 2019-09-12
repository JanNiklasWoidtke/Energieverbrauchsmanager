package com.example.energieverbrauch;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * In this fragment the devices with the corresponding device-information are displayed in a table.
 * A fragment to add a new device can be entered via a button.
 */

public class MyCountersFragment extends Fragment {

    public MyCountersFragmentListener listener;

    public FloatingActionButton ButtonAddCounter;
    public Button ButtonWerteAkt;
    public TextView zaehlerListe;
    public TextView headerElemente;
    public EditText aktuellerStandListe;
    public TextView anteilVerbrauch;

    public TableLayout tableLayout;

    public TableRow tableRow;

    int anzahlZaehler = 0;
    float gesamtVerbrauch = 0;

    ArrayList<String> zaehlername = new ArrayList<>();
    ArrayList<Float> standBeginn = new ArrayList<>();
    ArrayList<Float> aktuellerStand = new ArrayList<>();
    ArrayList<Float> verbrauchJedesZaehlers = new ArrayList<>();
    ArrayList<Float> anteilJedesZaehlers = new ArrayList<>();

    ArrayList<Integer> headerArray = new ArrayList<>();
    ArrayList<EditText> alleEditTextAktuellerStand = new ArrayList<>();

    Bundle dataFromMainAcitivity = new Bundle();
    Bundle dataToAddCountersFrag = new Bundle();

    Fragment AddCounterFragment = new AddCounterFragment();

    public interface MyCountersFragmentListener {
        /**
         * Enables data transfer to the "MainActivity"
         *
         * @param aktuellerStand  ArrayList containing the current standings of all devices
         * @param gesamtverbrauch Sum of the consumption
         */
        void dataFromMyCountersToMainActivity(ArrayList<Float> aktuellerStand, float gesamtverbrauch);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mycounters, container, false);

        ButtonAddCounter = v.findViewById(R.id.ButtonZählerHinzufügen);
        ButtonWerteAkt = v.findViewById(R.id.buttonWerteAkt);
        tableLayout = v.findViewById(R.id.tableLayout);

        ButtonAddCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundleDataToAddCountersFragFuellen();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, AddCounterFragment).commit();
            }
        });

        getDataFromMainActivity();

        gesamtVerbrauchBerechnen();
        anteilVerbrauchBerechnen();
        zaehlerTabelleErstellen();

        if (zaehlername.size() == 0) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddCounterFragment()).commit();
        }

        ButtonWerteAkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                werteAktualisieren();

                gesamtVerbrauchBerechnen();

                if (gesamtVerbrauch != 0) {
                    anteilVerbrauchBerechnen();
                }

                listener.dataFromMyCountersToMainActivity(aktuellerStand, gesamtVerbrauch);        //aktueller Stand wird an MainActivity übergeben
            }
        });

        return v;
    }

    public void bundleDataToAddCountersFragFuellen() {
        /**
         * This methods transfers the number of already added devices to the "AddCounterFragment" using bundles.
         */
        AddCounterFragment = new AddCounterFragment();
        dataToAddCountersFrag.putInt("zaehlernameSize", zaehlername.size());
        AddCounterFragment.setArguments(dataToAddCountersFrag);
    }

    public void gesamtVerbrauchBerechnen() {
        /**
         * This method calculates the total consumption of all devices.
         * Doing so, first the individual consumption of every device is calculated and added to an ArrayList for further usage.
         */
        verbrauchJedesZaehlers.clear();
        for (int i = 0; i < aktuellerStand.size(); i++) {
            verbrauchJedesZaehlers.add(aktuellerStand.get(i) - standBeginn.get(i));
        }

        gesamtVerbrauch = 0;

        for (int i = 0; i < verbrauchJedesZaehlers.size(); i++) {
            gesamtVerbrauch += verbrauchJedesZaehlers.get(i);
        }
    }

    public void anteilVerbrauchBerechnen() {
        /**
         * This method calculates the proportion of the consumption of every device.
         */
        anteilJedesZaehlers.clear();
        for (int i = 0; i < verbrauchJedesZaehlers.size(); i++) {
            if (gesamtVerbrauch != 0) {
                anteilJedesZaehlers.add(verbrauchJedesZaehlers.get(i) / gesamtVerbrauch);
            }
        }
    }

    public void getDataFromMainActivity() {
        /**
         * This method gets the required data from the "MainActivity" by getting the arguments of the fragment in a bundle.
         * The values stored in the bundle are accessed using the keys of the values.
         */
        dataFromMainAcitivity = getArguments();
        if (dataFromMainAcitivity != null) {
            zaehlername = dataFromMainAcitivity.getStringArrayList("zaehlername");
            standBeginn = floatArrayToArrayList(dataFromMainAcitivity.getFloatArray("standBeginn"));
            aktuellerStand = floatArrayToArrayList(dataFromMainAcitivity.getFloatArray("aktuellerStand"));
            anzahlZaehler = dataFromMainAcitivity.getInt("anzahlZaehler");
        }
    }

    public ArrayList<Float> floatArrayToArrayList(float[] FloatArray) {
        /**
         * This method transforms an C-Style float[] in an ArrayList<Float>
         */
        ArrayList<Float> arrayList = new ArrayList<>();
        for (int i = 0; i < FloatArray.length; i++) {
            arrayList.add(FloatArray[i]);
        }
        return arrayList;
    }

    public void headerArrayFuellen() {
        /**
         * This method adds the string for the row-headers to an ArrayList<String>
         */
        headerArray.add(R.string.geraetename);
        headerArray.add(R.string.aktStand);
        headerArray.add(R.string.anteilGesVerbrauch);
    }

    public void zaehlerTabelleErstellen() {
        /**
         * This method creates the table containing the information about the devices.
         * First, the header column is created.
         * Afterwards, the table containing the information is created (further description for every step)
         */
        headerArrayFuellen();

        alleEditTextAktuellerStand.clear();

        tableRow = new TableRow(getContext());
        ViewGroup.LayoutParams layoutParamsTableRow = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < 3; i++) {                               // Headerzeile mit Überschriften erstellen
            headerElemente = new TextView(getContext());
            tableRow.setLayoutParams(layoutParamsTableRow);
            headerElemente.setLayoutParams(layoutParamsTableRow);
            headerElemente.setText(headerArray.get(i));
            headerElemente.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            headerElemente.setPadding(0, 0, 48, 16);
            headerElemente.setGravity(Gravity.CENTER);
            headerElemente.setTypeface(null, Typeface.BOLD);
            tableRow.addView(headerElemente, i);

            if (i == 2) tableLayout.addView(tableRow);
        }

        for (int i = 0; i < zaehlername.size(); i++) {

            // Initialise views

            zaehlerListe = new TextView(getContext());
            tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(layoutParamsTableRow);

            // Fill first column: Device Names

            zaehlerListe.setId(i);
            zaehlerListe.setLayoutParams(layoutParamsTableRow);
            final String aktuellerZaehlername = zaehlername.get(i);
            zaehlerListe.setText(aktuellerZaehlername);
            zaehlerListe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            // Fill second column: Current Standing editable

            aktuellerStandListe = new EditText(getContext());
            aktuellerStandListe.setLayoutParams(layoutParamsTableRow);

            if (aktuellerStand.size() > 0)
                aktuellerStandListe.setText(String.valueOf(aktuellerStand.get(i))); //Sind noch keine neuen Werte eingegeben, wird der Anfanswert als Text gesetzt
            else aktuellerStandListe.setText(String.valueOf(standBeginn.get(i)));

            aktuellerStandListe.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            aktuellerStandListe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            aktuellerStandListe.setGravity(Gravity.END);

            alleEditTextAktuellerStand.add(aktuellerStandListe); //fügt EditText dem Array hinzu, um später über ID Wert abzufragen

            // Fill third column: Proportions of the consumption

            anteilVerbrauch = new TextView(getContext());
            anteilVerbrauch.setLayoutParams(layoutParamsTableRow);
            if (anteilJedesZaehlers.size() > i) {
                anteilVerbrauch.setText(String.format("%.1f", anteilJedesZaehlers.get(i) * 100) + "%");
                anteilVerbrauch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                anteilVerbrauch.setGravity(Gravity.END);
            }


            // Add views to the row, add row to the table

            tableRow.addView(zaehlerListe, 0);
            tableRow.addView(aktuellerStandListe, 1);
            tableRow.addView(anteilVerbrauch, 2);
            tableLayout.addView(tableRow);
        }
    }

    public void werteAktualisieren() {
        /**
         * This method is used to update standings.
         * The user can edit the standings in the EditTexts and by clicking "Refresh Values", this method is called.
         * The inputs are read and if permissible saved.
         * If the inputs are inadmissable, a Toast shows up and tells the user where the wrong input happened.
         */
        boolean unzulaessigeEingabe = false;
        int zeile = 0;

        for (int i = 0; i < zaehlername.size(); i++) {
            if (TextUtils.isEmpty(alleEditTextAktuellerStand.get(i).getText()) || alleEditTextAktuellerStand.get(i).getText().toString().equals(".")) {
                unzulaessigeEingabe = true;
                zeile = i;
                i = zaehlername.size();
            }
        }

        if (!unzulaessigeEingabe) {
            for (int i = 0; i < zaehlername.size(); i++) {                                                                  //für alle Zähler
                if (!TextUtils.isEmpty(alleEditTextAktuellerStand.get(i).getText()) && !alleEditTextAktuellerStand.get(i).getText().toString().equals(".")) {                                      //wenn das EditTet-Feld nicht leer ist
                    aktuellerStand.set(i, Float.parseFloat(alleEditTextAktuellerStand.get(i).getText().toString()));        //ändere den aktuellen Stand jedes geänderten Zählers auf den eingegebenen Wert
                }
            }
            Toast.makeText(getContext(), R.string.werteAktualisiert, Toast.LENGTH_SHORT).show();                                  //Toast zur visuellen Bestätigung, bis jetzt ohne Prüfung, ob tatsächlich Werte aktualisiert wurden
        } else {
            Toast.makeText(getContext(), String.format(getResources().getString(R.string.unzulaessigeEingabe), zaehlername.get(zeile)), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * The following methods are necessary to pass data between fragments and activities using the interface
     */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyCountersFragment.MyCountersFragmentListener) {
            listener = (MyCountersFragment.MyCountersFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement StartFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}