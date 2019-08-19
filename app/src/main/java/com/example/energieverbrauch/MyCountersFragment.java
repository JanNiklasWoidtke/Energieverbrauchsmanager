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
import android.text.InputType;
import android.util.TypedValue;
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

    public TableLayout tableLayout;

    public TableRow tableRow;

    int anzahlZaehler = 0;
    boolean werteAktualisiert = false;
    float[] aktuellerStandFloatArray;

    ArrayList<String> zaehlername = new ArrayList<>();
    ArrayList<Float> standBeginn = new ArrayList<>();
    ArrayList<Float> aktuellerStand = new ArrayList<>();
    ArrayList<Integer> headerArray = new ArrayList<>();
    ArrayList<EditText> alleEditTextAktuellerStand = new ArrayList<>();

    Bundle dataFromMainAcitivity = new Bundle();

    public interface MyCountersFragmentListener {
        void dataFromMyCountersToMainActivity(ArrayList<Float> aktuellerStand, boolean werteAktualisiert);
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
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddCounterFragment()).commit();
            }
        });

        dataFromMainAcitivity = getArguments();

        if (dataFromMainAcitivity != null) {

            ButtonWerteAkt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    werteAktualisiert = getArguments().getBoolean("werteAktualisiert");
                    werteAktualisieren();
                    listener.dataFromMyCountersToMainActivity(aktuellerStand, werteAktualisiert);
                    Toast.makeText(getContext(), R.string.werteAktualisiert, Toast.LENGTH_SHORT).show();
                }
            });

            zaehlerListeErstellen();
        }
        return v;
    }

    public ArrayList<Float> floatArrayToArrayList(float[] FloatArray) {
        standBeginn.clear();
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

    public void zaehlerListeErstellen() {
        headerArrayFuellen();

        zaehlername = getArguments().getStringArrayList("zaehlername");
        anzahlZaehler = getArguments().getInt("anzahlZaehler");

        float[] standBeginnFloatArray = getArguments().getFloatArray("standBeginn");

        standBeginn = floatArrayToArrayList(standBeginnFloatArray);

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
            tableRow.addView(headerElemente, i);

            if (i == 2) tableLayout.addView(tableRow);
        }


        for (int i = 0; i < anzahlZaehler; i++) {
            zaehlerListe = new TextView(getContext());
            tableRow = new TableRow(getContext());
            tableRow.setLayoutParams(layoutParamsTableRow);
            zaehlerListe.setId(i);
            zaehlerListe.setLayoutParams(layoutParamsTableRow);
            final String aktuellerZaehlername = zaehlername.get(i);
            zaehlerListe.setText(aktuellerZaehlername);
            zaehlerListe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            aktuellerStandListe = new EditText(getContext());
            aktuellerStandListe.setLayoutParams(layoutParamsTableRow);

            if (werteAktualisiert) aktuellerStandListe.setHint(aktuellerStand.get(i).toString()); //Sind noch keine neuen Werte eingegeben, wird der Anfanswert als Hint gesetzt
            else aktuellerStandListe.setHint(standBeginn.get(i).toString());

            aktuellerStandListe.setInputType(InputType.TYPE_CLASS_NUMBER);
            aktuellerStandListe.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            alleEditTextAktuellerStand.add(aktuellerStandListe); //fügt EditText dem Array hinzu, um später über ID Wert abzufragen

            tableRow.addView(zaehlerListe, 0);
            tableRow.addView(aktuellerStandListe, 1);
            tableLayout.addView(tableRow);
        }
    }

    public void werteAktualisieren() {
        aktuellerStand.clear();
        for (int i = 0; i < anzahlZaehler; i++) {
            if (!alleEditTextAktuellerStand.get(i).getText().toString().equals("")) {
                aktuellerStand.add(Float.parseFloat(alleEditTextAktuellerStand.get(i).getText().toString()));
            } else aktuellerStand.add(standBeginn.get(i));
        }

        werteAktualisiert = true;
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
