package com.example.energieverbrauch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This fragment displays statistics between two standing updates.
 * Average daily consumption, CO2-Emission and cost are presented.
 */

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

        getDataFromMainActivity();

        setValues();

        return v;
    }

    public void getDataFromMainActivity() {
        /**
         * This method calls a function from the MainActivity and gets the returned Bundle.
         * The values of the variables of this fragment are pulled from the acquired Bundle.
         */
        Bundle dataFromMainActivity = ((MainActivity) getActivity()).dataToDurchschnitt();

        aktuellerStand = dataFromMainActivity.getFloat("aktuellerStand", 0);
        vorherigerStand = dataFromMainActivity.getFloat("vorherigerStand", 0);
        aktuellerTagImJahr = dataFromMainActivity.getInt("aktuellerTagImJahr", 0);
        tagDerLetztenEingabe = dataFromMainActivity.getInt("tagDerLetztenEingabe", 0);
        preisProEinheit = dataFromMainActivity.getFloat("preisProEinheit", 0);
    }

    public void setValues() {
        /**
         * This methods calculates and sets the values of the statistics displayed.
         */
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

        /**
         * 537 g CO2 / kWh is a referencevalue from the UmweltBundesAmt Germany.
         */
        durchschnittCO2.setText(String.valueOf(durchschnittlicherVerbrauchProTag * 537));
    }

}
