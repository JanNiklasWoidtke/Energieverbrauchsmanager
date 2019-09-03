package com.example.energieverbrauch;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class StartFragmentAlt extends Fragment {

    public StartFragmentAltListener listener;

    public TextView textViewMaxVerbrauchSoll;
    public TextView textViewMonatKosten;
    public TextView textViewMonat;
    public ProgressBar ProgressBar;
    public TextView TextViewAktuellerVerbrauch;
    public TextView TextViewProzentAnzeige;
    float gesamtVerbrauch = 0;
    int progress = 0;
    float maxVerbrauch = 0;
    float preisProEinheit = 0;
    float grundBetrag = 0;

    int tag = 0;
    int tageImMonat = 0;
    String monat;
    String jahr;

    boolean neuerMonat = false;

    public interface StartFragmentAltListener { //ermöglicht Senden an MainActivity
        void dataFromStartFragmentToMainActivity(float maxVerbrauch);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_alt, container, false);

        textViewMaxVerbrauchSoll = v.findViewById(R.id.maxVerbrauchSoll);
        textViewMonatKosten = v.findViewById(R.id.textViewErwarteteMonatKosten);
        textViewMonat = v.findViewById(R.id.textViewMonat);
        ProgressBar = v.findViewById(R.id.PBcircle);
        TextViewAktuellerVerbrauch = v.findViewById(R.id.aktVerbrauch);
        TextViewProzentAnzeige = v.findViewById(R.id.prozentAnzeige);

        aktuelleDatumsInfo();

        getBundleDataFromMainActivity();

        if (neuerMonat) {
            neuenMaxVerbrauchMonatFestlegen();
        }

        aktuelleWerteSetzen();

        return v;
    }

    public void neuenMaxVerbrauchMonatFestlegen() {
        AlertDialog.Builder alertNeuerMaxVerbrauch = new AlertDialog.Builder(getContext());
        final EditText editTextNeuerMaxVerbrauch = new EditText(getContext());

        editTextNeuerMaxVerbrauch.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextNeuerMaxVerbrauch.setSingleLine();
        editTextNeuerMaxVerbrauch.setGravity(Gravity.CENTER);
        editTextNeuerMaxVerbrauch.setHint(R.string.hintNeuesZiel);

        alertNeuerMaxVerbrauch.setTitle(R.string.neuesMonatsZielTitle);
        alertNeuerMaxVerbrauch.setMessage(R.string.neuesMonatsZielMessage);
        alertNeuerMaxVerbrauch.setView(editTextNeuerMaxVerbrauch);

        alertNeuerMaxVerbrauch.setPositiveButton(R.string.neuesMonatsZielPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editTextNeuerMaxVerbrauch.getText())) {
                    neuenMaxVerbrauchMonatFestlegen();
                } else {
                    maxVerbrauch = Float.parseFloat(editTextNeuerMaxVerbrauch.getText().toString());
                    listener.dataFromStartFragmentToMainActivity(maxVerbrauch);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new TabContainerFragmentStart()).commit();
                }
            }
        })
                .show();
    }

    public void aktuelleDatumsInfo() {
        Calendar calendar = Calendar.getInstance();
        tag = calendar.get(Calendar.DAY_OF_MONTH);
        tageImMonat = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        monat = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        jahr = String.valueOf(calendar.get(Calendar.YEAR));
    }

    public void aktuelleWerteSetzen() {
        String anzeigeMonatJahr = monat + " " + jahr;
        textViewMonat.setText(anzeigeMonatJahr);

        TextViewAktuellerVerbrauch.setText(String.valueOf(gesamtVerbrauch));

        textViewMaxVerbrauchSoll.setText(String.valueOf(maxVerbrauch));

        float erwarteteMonatlicheKosten = grundBetrag / 12 + (preisProEinheit * gesamtVerbrauch) / tag * tageImMonat;

        textViewMonatKosten.setText(String.format("%.2f", erwarteteMonatlicheKosten));

        calculateProgress();
        updateProgressBar();
        updatePercentage();
    }

    public void getBundleDataFromMainActivity() {
        Bundle dataFromMainActivity = ((MainActivity) getActivity()).dataToStartFragMethod();

        maxVerbrauch = dataFromMainActivity.getFloat("maxVerbrauch", 0);
        gesamtVerbrauch = dataFromMainActivity.getFloat("gesamtVerbrauch", 0);
        preisProEinheit = dataFromMainActivity.getFloat("preisProEinheit", 0);
        grundBetrag = dataFromMainActivity.getFloat("grundBetrag", 0);
        neuerMonat = dataFromMainActivity.getBoolean("neuerMonat", false);
    }

    public void calculateProgress() {
        if (maxVerbrauch != 0) {
            progress = (int) (gesamtVerbrauch / maxVerbrauch * 100);
        } else if (gesamtVerbrauch == 0) {
            progress = 0;
        } else {
            progress = 101;
        }
    }

    public void updateProgressBar() {
        ProgressBar.setProgress(progress);
    }

    public void updatePercentage() {
        if (progress < 100) {
            TextViewProzentAnzeige.setText(progress + "%");
            ProgressBar.getProgressDrawable().clearColorFilter();
        } else {
            TextViewProzentAnzeige.setText("Mehr als 100%");
            ProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.ueber100ProgressColor), PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StartFragmentAltListener) {
            listener = (StartFragmentAltListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement StartFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}


//Mock-Ups: https://placeit.net/c/mockups/stages/galaxy-s9-mockup-template-against-transparent-background-a19508?customG_0=pwu6ngf3d7
