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

    int tagImJahr = 0;
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
        textViewJahr.setText(jahr);

        TextViewAktuellerVerbrauchJahr.setText(String.valueOf(gesamtVerbrauchJahr + gesamtVerbrauchAktMonat));

        textViewMaxVerbrauchSoll.setText(String.valueOf(maxVerbrauchJahr));

        float erwarteteJahresKosten = grundBetrag + preisProEinheit * (gesamtVerbrauchJahr + gesamtVerbrauchAktMonat) / tagImJahr * 365;

        textViewerwarteteJahresKosten.setText(String.format("%.2f", erwarteteJahresKosten));

        calculateProgress();
        updateProgressBar(progressJahr);
        updatePercentage(progressJahr);
    }


    public void getBundleDataFromMainActivity() {
        Bundle dataFromMainActivityJahr = ((MainActivity) getActivity()).dataToStartFragJahrMethod();

        gesamtVerbrauchJahr = dataFromMainActivityJahr.getFloat("gesamtVerbrauchJahr", 0);
        maxVerbrauchJahr = dataFromMainActivityJahr.getFloat("maxVerbrauchJahr", 0);
        grundBetrag = dataFromMainActivityJahr.getFloat("grundBetrag", 0);
        preisProEinheit = dataFromMainActivityJahr.getFloat("preisProEinheit", 0);
        gesamtVerbrauchAktMonat = dataFromMainActivityJahr.getFloat("gesamtVerbrauchAktMonat", 0);
    }

    public void calculateProgress() {
        if (maxVerbrauchJahr != 0) {
            progressJahr = (int) ((gesamtVerbrauchJahr + gesamtVerbrauchAktMonat)/ maxVerbrauchJahr * 100);
        } else {
            progressJahr = 101;
        }
    }

    public void updateProgressBar(int progress) {
        ProgressBar.setProgress(progress);
    }

    public void updatePercentage(int progress) {
        if (progress <= 100) {
            TextViewProzentAnzeige.setText(progress + "%");
        } else {
            TextViewProzentAnzeige.setText("Mehr als 100%");
            ProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.ueber100ProgressColor), PorterDuff.Mode.SRC_IN);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

