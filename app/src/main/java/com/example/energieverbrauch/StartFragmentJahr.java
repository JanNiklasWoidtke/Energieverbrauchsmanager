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
        Calendar calendar = Calendar.getInstance();
        tagImJahr = calendar.get(Calendar.DAY_OF_YEAR);
        jahr = String.valueOf(calendar.get(Calendar.YEAR));
    }

    public void aktuelleWerteSetzen() {
        if (anzahlMonate == 0) {
            textViewJahr.setText(R.string.nochKeineDater);
        } else if (anzahlMonate > 1) {
            textViewJahr.setText(String.format(getResources().getString(R.string.anzahlMonate), anzahlMonate));
        } else {
            textViewJahr.setText(R.string.anzahlMonateEins);
        }
        TextViewAktuellerVerbrauchJahr.setText(String.valueOf(gesamtVerbrauchJahr + gesamtVerbrauchAktMonat));

        textViewMaxVerbrauchSoll.setText(String.valueOf(maxVerbrauchJahr));

        if (anfangsTag < tagImJahr) {
            erwarteteJahresKosten = grundBetrag + preisProEinheit * (gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) / (tagImJahr-anfangsTag) * 365; //anfangstag von tagImJahr abziehen
        }
        else if (anfangsTag == tagImJahr){
            erwarteteJahresKosten = grundBetrag + preisProEinheit * (gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) * (365-anfangsTag);
        }
        else {
            erwarteteJahresKosten = grundBetrag + preisProEinheit * (gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) / tagImJahr * 365;
        }

        textViewerwarteteJahresKosten.setText(String.format("%.2f", erwarteteJahresKosten));

        calculateProgress();
        updateProgressBar();
        updatePercentage();
    }


    public void getBundleDataFromMainActivity() {
        Bundle dataFromMainActivityJahr = ((MainActivity) getActivity()).dataToStartFragJahrMethod();

        gesamtVerbrauchJahr = dataFromMainActivityJahr.getFloat("gesamtVerbrauchJahr", 0);
        maxVerbrauchJahr = dataFromMainActivityJahr.getFloat("maxVerbrauchJahr", 0);
        grundBetrag = dataFromMainActivityJahr.getFloat("grundBetrag", 0);
        preisProEinheit = dataFromMainActivityJahr.getFloat("preisProEinheit", 0);
        gesamtVerbrauchAktMonat = dataFromMainActivityJahr.getFloat("gesamtVerbrauchAktMonat", 0);
        anzahlMonate = dataFromMainActivityJahr.getInt("anzahlMonate", 0);
        anfangsTag = dataFromMainActivityJahr.getInt("anfangsTag", 0);

        if (anzahlMonate > 12) {
            anzahlMonate = 12;
        }
    }

    public void calculateProgress() {
        if (maxVerbrauchJahr != 0) {
            progressJahr = (int) ((gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) / maxVerbrauchJahr * 100);
        } else if ((gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) == 0) {
            progressJahr = 0;
        } else {
            progressJahr = 101;
        }
    }

    public void updateProgressBar() {
        ProgressBar.setProgress(progressJahr);
    }

    public void updatePercentage() {
        if (progressJahr < 100) {
            TextViewProzentAnzeige.setText(progressJahr + "%");
        } else {
            TextViewProzentAnzeige.setText("Mehr als 100%");
            ProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.ueber100ProgressColor), PorterDuff.Mode.SRC_IN);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

