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

        if (arguments != null) {
            zaehlername = getArguments().getStringArrayList("zaehlername");
            anzahlZaehler = getArguments().getInt("anzahlZaehler");

            LinearLayout ListungZaehler = new LinearLayout(getActivity());
            ListungZaehler.setOrientation(LinearLayout.VERTICAL);

            for (int i = 0; i < anzahlZaehler; i++) {
                TextView listeZaehler = new TextView(getContext());
                String listeZaehlerText = zaehlername.get(i);
                listeZaehler.setText(listeZaehlerText);
            }
        }else Toast.makeText(getContext(), "Fehler", Toast.LENGTH_SHORT).show();



        return v;
    }
}
