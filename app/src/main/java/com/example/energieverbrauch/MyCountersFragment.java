package com.example.energieverbrauch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

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

    ArrayList<String> zaehlername = new ArrayList<>();                      //Liste enthält alle Zählernamen
    ArrayList<Float> standBeginn = new ArrayList<>();                       //Liste enthält die Zählerstande beim ersten Eintragen
    ArrayList<Float> aktuellerStand = new ArrayList<>();                    //Liste enthält die aktuell eingegebenen Zählerstände
    ArrayList<Float> verbrauchJedesZaehlers = new ArrayList<>();            //Liste enthält die aktuell verbrauchte Menge jedes Zählers
    ArrayList<Float> anteilJedesZaehlers = new ArrayList<>();               //Liste enthält die aktuellen Anteil jedes Zaehlers am Gesamtverbrauch

    ArrayList<Integer> headerArray = new ArrayList<>();                     //Liste enthält Überschriften der Tabelle
    ArrayList<EditText> alleEditTextAktuellerStand = new ArrayList<>();     //Liste enthält alle EditText-Felder in denen der aktuelle Stand des jeweiligen Zählers eingegeben werden kann

    Bundle dataFromMainAcitivity = new Bundle();                            //Bundle enthält die für das Fragment notwendigen Daten, die aus der MainActivity verteilt werden

    public interface MyCountersFragmentListener { //ermöglicht Datenübertragung in die MainActivity aus dem Fragment
        void dataFromMyCountersToMainActivity(ArrayList<Float> aktuellerStand,
                                              ArrayList<Float> anteilJedesZaehlers,
                                              float gesamtverbrauch); //übergibt die aktuellen Zählerstände und Anteile an die MainActivity
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mycounters, container, false);

        ButtonAddCounter = v.findViewById(R.id.ButtonZählerHinzufügen);     //Button zum Hinzufügen eines neuen Zählers, führt in anderes Fragment
        ButtonWerteAkt = v.findViewById(R.id.buttonWerteAkt);               //Button zum aktualisieren der Zählerstände
        tableLayout = v.findViewById(R.id.tableLayout);                     //Tabellen-Layout zum Darstellen der Zählerinformationen

        ButtonAddCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddCounterFragment()).commit(); //wird der Button gedrückt, wird das Fragment zum erstellen eines Zählers aufgerufen
            }
        });

        dataFromMainAcitivity = getArguments(); //Argumente aus dem Bundle werden für das Fragment gesetzt, damit Daten entnommen werden können

        if (dataFromMainAcitivity != null) { //wenn Argumente übergeben wurden...
            bundleAuslesen();
            gesamtVerbrauchBerechnen();
            anteilVerbrauchBerechnen();
            zaehlerTabelleErstellen();
        }

        ButtonWerteAkt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                werteAktualisieren();

                gesamtVerbrauchBerechnen();

                if (gesamtVerbrauch != 0) {
                    anteilVerbrauchBerechnen();
                }

                listener.dataFromMyCountersToMainActivity(aktuellerStand, anteilJedesZaehlers, gesamtVerbrauch);        //aktueller Stand wird an MainActivity übergeben
            }
        });

        return v;
    }

    public void gesamtVerbrauchBerechnen() {
        verbrauchJedesZaehlers.clear();
        for (int i = 0; i < aktuellerStand.size(); i++) { //füllen der Liste mit dem Verbrauch jeden Zählers
            verbrauchJedesZaehlers.add(aktuellerStand.get(i) - standBeginn.get(i));              //Verbrauch = aktueller Stand - Anfangsstand
        }

        gesamtVerbrauch = 0;                                                                        //Rücksetzen des Gesamtverbrauchs auf 0 vor Neuberechnung

        for (int i = 0; i < verbrauchJedesZaehlers.size(); i++) {                                   //Berechnung des Gesamtverbrauchs
            gesamtVerbrauch += verbrauchJedesZaehlers.get(i);                                       //Gesamtverbrauch = Summe der Verbrauchsstände jedes Zählers
        }

        Toast.makeText(getContext(), String.valueOf(gesamtVerbrauch), Toast.LENGTH_SHORT).show();
    }

    public void anteilVerbrauchBerechnen() {
        anteilJedesZaehlers.clear();
        for (int i = 0; i < verbrauchJedesZaehlers.size(); i++) {
            anteilJedesZaehlers.add(verbrauchJedesZaehlers.get(i) / gesamtVerbrauch);
        }
    }

    public void bundleAuslesen() {                                                                          //holt die Zählernamen, Anfangsstände etc. aus den von der MainActivity gelieferten Daten
        zaehlername = dataFromMainAcitivity.getStringArrayList("zaehlername");
        standBeginn = floatArrayToArrayList(dataFromMainAcitivity.getFloatArray("standBeginn"));
        aktuellerStand = floatArrayToArrayList(dataFromMainAcitivity.getFloatArray("aktuellerStand"));
        anteilJedesZaehlers = floatArrayToArrayList(dataFromMainAcitivity.getFloatArray("anteilJedesZaehlers"));
        anzahlZaehler = dataFromMainAcitivity.getInt("anzahlZaehler");
    }

    public ArrayList<Float> floatArrayToArrayList(float[] FloatArray) {                             //wandelt Float-Array in ArrayList-Float um
        //standBeginn.clear();     //warum?                                                           //wird benötigt, da ArrayList-Float nicht über Bundle an Fragments übergeben werden kann
        ArrayList<Float> arrayList = new ArrayList<>();
        for (int i = 0; i < FloatArray.length; i++) {
            arrayList.add(FloatArray[i]);
        }
        return arrayList;
    }

    public void headerArrayFuellen() {
        headerArray.add(R.string.geraetename);
        headerArray.add(R.string.aktStand);
        headerArray.add(R.string.anteilGesVerbrauch);
    }

    public void zaehlerTabelleErstellen() {

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
            tableRow.addView(headerElemente, i);

            if (i == 2) tableLayout.addView(tableRow);
        }

        for (int i = 0; i < zaehlername.size(); i++) {

            // Views initialisieren

            zaehlerListe = new TextView(getContext());
            tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(layoutParamsTableRow);

            // 1. Spalte befüllen: Zählernamen

            zaehlerListe.setId(i);
            zaehlerListe.setLayoutParams(layoutParamsTableRow);
            final String aktuellerZaehlername = zaehlername.get(i);
            zaehlerListe.setText(aktuellerZaehlername);
            zaehlerListe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            // 2. Spalte befüllen: Aktueller Stand bearbeitbar

            aktuellerStandListe = new EditText(getContext());
            aktuellerStandListe.setLayoutParams(layoutParamsTableRow);

            if (aktuellerStand.size() > 0)
                aktuellerStandListe.setText(String.valueOf(aktuellerStand.get(i))); //Sind noch keine neuen Werte eingegeben, wird der Anfanswert als Text gesetzt
            else aktuellerStandListe.setText(String.valueOf(standBeginn.get(i)));

            aktuellerStandListe.setInputType(InputType.TYPE_CLASS_NUMBER);
            aktuellerStandListe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            aktuellerStandListe.setGravity(Gravity.END);

            alleEditTextAktuellerStand.add(aktuellerStandListe); //fügt EditText dem Array hinzu, um später über ID Wert abzufragen

            // 3. Spalte befüllen: Anteil am Verbrauch

            anteilVerbrauch = new TextView(getContext());
            anteilVerbrauch.setLayoutParams(layoutParamsTableRow);
            if (anteilJedesZaehlers.size() > i) {
                anteilVerbrauch.setText(String.format("%.1f", anteilJedesZaehlers.get(i)*100) + "%");
                anteilVerbrauch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                anteilVerbrauch.setGravity(Gravity.END);
            }


            // Views in Spalten einfügen, Zeile zur Tabelle hinzufügen

            tableRow.addView(zaehlerListe, 0);
            tableRow.addView(aktuellerStandListe, 1);
            tableRow.addView(anteilVerbrauch, 2);
            tableLayout.addView(tableRow);
        }
    }

    public void werteAktualisieren() {
        for (int i = 0; i < zaehlername.size(); i++) {                                                                  //für alle Zähler
            if (!TextUtils.isEmpty(alleEditTextAktuellerStand.get(i).getText())) {                                      //wenn das EditTet-Feld nicht leer ist
                aktuellerStand.set(i, Float.parseFloat(alleEditTextAktuellerStand.get(i).getText().toString()));        //ändere den aktuellen Stand jedes geänderten Zählers auf den eingegebenen Wert
            }
        }

        Toast.makeText(getContext(), R.string.werteAktualisiert, Toast.LENGTH_SHORT).show();                                  //Toast zur visuellen Bestätigung, bis jetzt ohne Prüfung, ob tatsächlich Werte aktualisiert wurden

    }

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

/*
Funktion werteAktualisieren
aktuellerStand.clear();
        for (int i = 0; i < anzahlZaehler; i++) {
            if (!alleEditTextAktuellerStand.get(i).getText().toString().equals("")) {
                aktuellerStand.add(Float.parseFloat(alleEditTextAktuellerStand.get(i).getText().toString()));
            } else aktuellerStand.add(standBeginn.get(i));
        }

        ENDE




            if (aktuellerStand.size() > 0) Toast.makeText(getContext(), aktuellerStand.get(0).toString(), Toast.LENGTH_SHORT).show(); //Testfunktion
            else Toast.makeText(getContext(), "aktueller Stand ist leer", Toast.LENGTH_SHORT).show(); //Testfunktion

            ButtonWerteAkt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    werteAktualisiert = getArguments().getBoolean("werteAktualisiert");                 //Boolesche Variable, ob Werte aktualisiert wurden, wird dem Bundle entnommen
                    werteAktualisieren();                                                                    //Funktion zum aktualisieren der aktuellen Zählerstände
                    listener.dataFromMyCountersToMainActivity(aktuellerStand, werteAktualisiert);            //Interface wird aufgerufen, um Daten an MainActivity zu übergeben
                    Toast.makeText(getContext(), R.string.werteAktualisiert, Toast.LENGTH_SHORT).show();     //Toast zur Bestätigung des Nutzers, dass Daten aktualsiert wurden (muss Prüfung bekommen)
                }
            });

            zaehlerListeErstellen();    //Funktion zum Erstellen der Tabelle mit Zählerinformationen
 */