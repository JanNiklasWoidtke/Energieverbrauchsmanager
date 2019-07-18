package com.example.energieverbrauch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyCountersFragment extends Fragment {

    public FloatingActionButton ButtonAddCounter;
    int anzahlZaehler = 0;
    ArrayList<String> zaehlername = new ArrayList<>();

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
        Button zaehlerListe  = new Button(getContext());


        if (arguments != null) {
            zaehlername = getArguments().getStringArrayList("zaehlername");
            anzahlZaehler = getArguments().getInt("anzahlZaehler");

            Toast.makeText(getContext(), String.valueOf(anzahlZaehler), Toast.LENGTH_SHORT).show();

            LinearLayout linearLayoutMyCounters = v.findViewById(R.id.linearLayoutFragment_MyCounters);
            ViewGroup.LayoutParams layoutParamsMyCounters = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, //Width of TextView
                    ViewGroup.LayoutParams.WRAP_CONTENT); //Height of TextView

            for (int i = 0; i < anzahlZaehler; i++) {
                zaehlerListe.setId(i);
                zaehlerListe.setLayoutParams(layoutParamsMyCounters);
                final String aktuellerZaehlername = zaehlername.get(i);
                zaehlerListe.setText(aktuellerZaehlername);
                zaehlerListe.setTextSize(20);
                linearLayoutMyCounters.addView(zaehlerListe);
            }
        } else Toast.makeText(getContext(), "Fehler", Toast.LENGTH_SHORT).show();



        return v;
    }
}
