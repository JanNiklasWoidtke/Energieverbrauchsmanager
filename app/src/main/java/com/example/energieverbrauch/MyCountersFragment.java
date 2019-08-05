package com.example.energieverbrauch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
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

    public FloatingActionButton ButtonAddCounter;
    public TextView zaehlerListe;
    public TextView standBeginnListe;
    public EditText aktuellerStandListe;

    int anzahlZaehler = 0;
    ArrayList<String> zaehlername = new ArrayList<>();
    ArrayList<Float> standBeginn = new ArrayList<>();
    ArrayList<Float> aktuellerStand = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mycounters, container, false);

        ButtonAddCounter = v.findViewById(R.id.ButtonZählerHinzufügen);

        ButtonAddCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddCounterFragment()).commit();
            }
        });

        Bundle arguments = getArguments();


        if (arguments != null) {
            zaehlername = getArguments().getStringArrayList("zaehlername");
            anzahlZaehler = getArguments().getInt("anzahlZaehler");

            float[] standBeginnFloatArray = getArguments().getFloatArray("standBeginn");

            floatArrayToArrayList(standBeginnFloatArray);

            final TableLayout tableLayout = v.findViewById(R.id.tableLayout);
            ViewGroup.LayoutParams layoutParamsTableLayout = new TableLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TableRow tableRow = new TableRow(getContext());
            ViewGroup.LayoutParams layoutParamsTableRow = new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            for (int i = 0; i < anzahlZaehler; i++) {
                zaehlerListe = new TextView(getContext());
                tableRow = new TableRow(getContext());
                tableRow.setLayoutParams(layoutParamsTableRow);
                zaehlerListe.setId(i);
                zaehlerListe.setLayoutParams(layoutParamsTableRow);
                final String aktuellerZaehlername = zaehlername.get(i);
                zaehlerListe.setText(aktuellerZaehlername);
                zaehlerListe.setTextSize(20);
                zaehlerListe.setPadding(0,0,48,16);

                standBeginnListe = new TextView(getContext());
                standBeginnListe.setLayoutParams(layoutParamsTableRow);
                final String aktuellerStandBeginn = Float.toString(standBeginn.get(i));
                standBeginnListe.setText(aktuellerStandBeginn);
                standBeginnListe.setTextSize(20);
                standBeginnListe.setPadding(0,0,48,16);

                aktuellerStandListe = new EditText(getContext());
                aktuellerStandListe.setLayoutParams(layoutParamsTableRow);

                aktuellerStandListe.setHint(standBeginn.get(i).toString());
                aktuellerStandListe.setInputType(InputType.TYPE_CLASS_NUMBER);
                aktuellerStandListe.setTextSize(20);

                tableRow.addView(zaehlerListe, 0);
                tableRow.addView(standBeginnListe, 1);
                tableRow.addView(aktuellerStandListe,2);
                tableLayout.addView(tableRow);
            }
        }

        return v;
    }

    public void floatArrayToArrayList(float[] standBeginnFloatArray) {
        standBeginn.clear();
        int sizeFloatArray = getArguments().getInt("arrayLaenge");
        for (int i = 0; i < sizeFloatArray; i++) {
            standBeginn.add(standBeginnFloatArray[i]);
        }
    }
}
