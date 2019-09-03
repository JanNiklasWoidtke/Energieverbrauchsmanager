package com.example.energieverbrauch;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DurschnittsWerte_Fragment extends Fragment {

    TextView durchschnittsVerbrauch;
    TextView durchschnittsKosten;
    TextView durchschnittCO2;

    float vorherigerStand = 0;
    float aktuellerStand = 0;
    int aktuellerTagImJahr = 0;
    int tagDerLetztenEingabe = 0;

    float preisProEinheit = 0;

    float durchschnittlicherVerbrauchProTag = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_durchschnittswerte, container, false);

        durchschnittsVerbrauch = v.findViewById(R.id.textViewDurchschnittsVerbrauch);
        durchschnittsKosten = v.findViewById(R.id.textViewDurchschnittsKosten);
        durchschnittCO2 = v.findViewById(R.id.co2Verbrauch);

        Bundle dataFromMainActivity = ((MainActivity) getActivity()).dataToDurchschnitt();

        aktuellerStand = dataFromMainActivity.getFloat("aktuellerStand", 0);
        vorherigerStand = dataFromMainActivity.getFloat("vorherigerStand", 0);
        aktuellerTagImJahr = dataFromMainActivity.getInt("aktuellerTagImJahr", 0);
        tagDerLetztenEingabe = dataFromMainActivity.getInt("tagDerLetztenEingabe", 0);
        preisProEinheit = dataFromMainActivity.getFloat("preisProEinheit", 0);

        if (tagDerLetztenEingabe != aktuellerTagImJahr) {
            if (aktuellerTagImJahr < tagDerLetztenEingabe) {
                durchschnittlicherVerbrauchProTag = (aktuellerStand - vorherigerStand) / (365 - tagDerLetztenEingabe + aktuellerTagImJahr);
            } else {
                durchschnittlicherVerbrauchProTag = (aktuellerStand - vorherigerStand) / (aktuellerTagImJahr - tagDerLetztenEingabe);
            }
        } else {
            durchschnittlicherVerbrauchProTag = aktuellerStand - vorherigerStand;
        }
        durchschnittsVerbrauch.setText(String.valueOf(durchschnittlicherVerbrauchProTag));

        durchschnittsKosten.setText(String.valueOf(durchschnittlicherVerbrauchProTag * preisProEinheit));

        durchschnittCO2.setText(String.valueOf(durchschnittlicherVerbrauchProTag * 537)); //537 g/kWh als Quellwert vom UBA

        return v;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
