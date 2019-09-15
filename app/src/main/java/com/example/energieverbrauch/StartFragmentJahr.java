package com.example.energieverbrauch;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;

/**
 * This fragment displays information about the consumption on a bigger time scale to the user.
 * Values for max 12 months are presented.
 */

public class StartFragmentJahr extends Fragment {

    public TextView textViewMaxVerbrauchSoll;
    public TextView textViewerwarteteJahresKosten;
    public TextView textViewJahr;
    public ProgressBar ProgressBar;
    public TextView TextViewAktuellerVerbrauchJahr;
    public TextView TextViewProzentAnzeige;

    float gesamtVerbrauchJahr = 0;
    int progressJahr = 0;
    float maxVerbrauchJahr = 0;
    float gesamtVerbrauchAktMonat = 0;
    float grundBetrag = 0;
    float preisProEinheit = 0;
    float erwarteteJahresKosten = 0;

    int tagImJahr = 0;
    int anzahlMonate = 0;
    int anfangsTag = 0;
    int anzahlJahre = 0;
    String jahr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_jahr, container, false);

        textViewMaxVerbrauchSoll = v.findViewById(R.id.maxVerbrauchSollJahr);
        textViewerwarteteJahresKosten = v.findViewById(R.id.textViewErwarteteJahresKosten);
        textViewJahr = v.findViewById(R.id.textViewJahr);
        ProgressBar = v.findViewById(R.id.PBcircleJahr);
        TextViewAktuellerVerbrauchJahr = v.findViewById(R.id.aktVerbrauchJahr);
        TextViewProzentAnzeige = v.findViewById(R.id.prozentAnzeigeJahr);

        getBundleDataFromMainActivity();

        aktuelleDatumsInfo();

        aktuelleWerteSetzen();

        return v;
    }

    public void aktuelleDatumsInfo() {
        /**
         * This method is used to get the required data information in this fragment.
         * An instance of the android calendar is called and used.
         */
        Calendar calendar = Calendar.getInstance();
        tagImJahr = calendar.get(Calendar.DAY_OF_YEAR);
        jahr = String.valueOf(calendar.get(Calendar.YEAR));
    }

    public void aktuelleWerteSetzen() {
        /**
         * In this method the current values are set.
         * Also the expected costs are calculated.
         */

        if (anzahlMonate >= 1) {
            textViewJahr.setText(String.format(getResources().getString(R.string.anzahlMonate), anzahlMonate + 1));
        } else {
            textViewJahr.setText(R.string.anzahlMonateEins);
        }
        TextViewAktuellerVerbrauchJahr.setText(String.valueOf(gesamtVerbrauchJahr + gesamtVerbrauchAktMonat));

        textViewMaxVerbrauchSoll.setText(String.valueOf(maxVerbrauchJahr));

        //Expected cost calculation

        if (anfangsTag < tagImJahr && anzahlJahre == 0) {
            erwarteteJahresKosten = grundBetrag + preisProEinheit * (gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) / (tagImJahr-anfangsTag) * 365; //anfangstag von tagImJahr abziehen
        }
        else if (anfangsTag == tagImJahr && anzahlJahre == 0){
            erwarteteJahresKosten = grundBetrag + preisProEinheit * (gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) * 365;
        }
        else if(anfangsTag > tagImJahr && anzahlJahre == 1) {
            erwarteteJahresKosten = grundBetrag + preisProEinheit * (gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) / (365 - anfangsTag + tagImJahr) * 365;
        }
        else {
            erwarteteJahresKosten = grundBetrag + preisProEinheit * (gesamtVerbrauchJahr + gesamtVerbrauchAktMonat);
        }

        textViewerwarteteJahresKosten.setText(String.format("%.2f", erwarteteJahresKosten));

        calculateProgress();
        updatePercentage();
    }


    public void getBundleDataFromMainActivity() {
        /**
         * This method is used to call a method from the MainActivity and get the returned bundle.
         * The bundle contains the relevant data for this fragment.
         * The maximum timeframe to watch is 12 months.
         */
        Bundle dataFromMainActivityJahr = ((MainActivity) getActivity()).dataToStartFragJahrMethod();

        gesamtVerbrauchJahr = dataFromMainActivityJahr.getFloat("gesamtVerbrauchJahr", 0);
        maxVerbrauchJahr = dataFromMainActivityJahr.getFloat("maxVerbrauchJahr", 0);
        grundBetrag = dataFromMainActivityJahr.getFloat("grundBetrag", 0);
        preisProEinheit = dataFromMainActivityJahr.getFloat("preisProEinheit", 0);
        gesamtVerbrauchAktMonat = dataFromMainActivityJahr.getFloat("gesamtVerbrauchAktMonat", 0);
        anzahlMonate = dataFromMainActivityJahr.getInt("anzahlMonate", 0);
        anfangsTag = dataFromMainActivityJahr.getInt("anfangsTag", 0);
        anzahlJahre = dataFromMainActivityJahr.getInt("anzahlJahre", 0);

        if (anzahlMonate > 12) {
            anzahlMonate = 12;
        }
    }

    public void calculateProgress() {
        /**
         * This method is ued to set/calculate the progress based on states ot other variables.
         * After calculation, the progress is set.
         */
        if (maxVerbrauchJahr != 0) {
            progressJahr = (int) ((gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) / maxVerbrauchJahr * 100);
        } else if ((gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) == 0) {
            progressJahr = 0;
        } else {
            progressJahr = 101;
        }
        ProgressBar.setProgress(progressJahr);
    }

    public void updatePercentage() {
        /**
         * This method is used to update the percentage display.
         * Bases on the value of progress, the ProgressBar is colored.
         */
        if (progressJahr < 100) {
            TextViewProzentAnzeige.setText(progressJahr + "%");
        } else {
            TextViewProzentAnzeige.setText(R.string.mehrAls100);
            ProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.ueber100ProgressColor), PorterDuff.Mode.SRC_IN);
        }
    }
}

