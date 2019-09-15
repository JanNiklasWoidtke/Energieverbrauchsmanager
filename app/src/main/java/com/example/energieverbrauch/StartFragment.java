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
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * This fragment is the start and home screen of the application.
 * The consumption of the current month is displayed in a circular progress bar.
 * Also, a forecast of the expected costs is also given.
 */

public class StartFragment extends Fragment {

    public StartFragmentListener listener;

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

    public interface StartFragmentListener {
        /**
         * Enables data transfer to the "MainActivity"
         * @param maxVerbrauch maximum consumption objective set by user
         */
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

        progress = 0;


        aktuelleDatumsInfo();

        getBundleDataFromMainActivity();

        if (neuerMonat) {
            neuenMaxVerbrauchMonatFestlegen();
        }

        aktuelleWerteSetzen();

        return v;
    }

    public void neuenMaxVerbrauchMonatFestlegen() {
        /**
         * This method is used to display a DialogBox if a new month started.
         * Here, the user has to put in a new consumption objective.
         * The box is not dismissable.
         * If a valid value is entered, the data is transfered to the MainActivity using the interface.
         */

        AlertDialog.Builder alertNeuerMaxVerbrauch = new AlertDialog.Builder(getContext());
        final EditText editTextNeuerMaxVerbrauch = new EditText(getContext());

        editTextNeuerMaxVerbrauch.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextNeuerMaxVerbrauch.setSingleLine();
        editTextNeuerMaxVerbrauch.setGravity(Gravity.CENTER);
        editTextNeuerMaxVerbrauch.setHint(R.string.hintNeuesZiel);

        alertNeuerMaxVerbrauch.setTitle(R.string.neuesMonatsZielTitle);
        alertNeuerMaxVerbrauch.setMessage(R.string.neuesMonatsZielMessage);
        alertNeuerMaxVerbrauch.setView(editTextNeuerMaxVerbrauch);
        alertNeuerMaxVerbrauch.setCancelable(false);

        alertNeuerMaxVerbrauch.setPositiveButton(R.string.neuesMonatsZielPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editTextNeuerMaxVerbrauch.getText())) {
                    neuenMaxVerbrauchMonatFestlegen();
                    Toast.makeText(getContext(), R.string.fehlerKeinZiel, Toast.LENGTH_SHORT).show();
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
        /**
         * This method is used to get the required data information in this fragment.
         * An instance of the android calendar is called and used.
         */
        Calendar calendar = Calendar.getInstance();
        tag = calendar.get(Calendar.DAY_OF_MONTH);
        tageImMonat = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        monat = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        jahr = String.valueOf(calendar.get(Calendar.YEAR));
    }

    public void aktuelleWerteSetzen() {
        /**
         * In this method the current values are set.
         * Also the expected costs are calculated.
         */
        String anzeigeMonatJahr = monat + " " + jahr;
        textViewMonat.setText(anzeigeMonatJahr);

        TextViewAktuellerVerbrauch.setText(String.valueOf(gesamtVerbrauch));

        textViewMaxVerbrauchSoll.setText(String.valueOf(maxVerbrauch));

        float erwarteteMonatlicheKosten = grundBetrag / 12 + (preisProEinheit * gesamtVerbrauch) / tag * tageImMonat;

        textViewMonatKosten.setText(String.format("%.2f", erwarteteMonatlicheKosten));

        calculateProgress();
        updatePercentage();
    }

    public void getBundleDataFromMainActivity() {
        /**
         * This method is used to call a method from the MainActivity and get the returned bundle.
         * The bundle contains the relevant data for this fragment.
         */
        Bundle dataFromMainActivity = ((MainActivity) getActivity()).dataToStartFragMethod();

        maxVerbrauch = dataFromMainActivity.getFloat("maxVerbrauch", 0);
        gesamtVerbrauch = dataFromMainActivity.getFloat("gesamtVerbrauch", 0);
        preisProEinheit = dataFromMainActivity.getFloat("preisProEinheit", 0);
        grundBetrag = dataFromMainActivity.getFloat("grundBetrag", 0);
        neuerMonat = dataFromMainActivity.getBoolean("neuerMonat", false);
    }

    public void calculateProgress() {
        /**
         * This method is ued to set/calculate the progress based on states ot other variables.
         * After calculation, the progress is set.
         */
        if (maxVerbrauch != 0) {
            progress = (int) (gesamtVerbrauch / maxVerbrauch * 100);
        } else if (gesamtVerbrauch == 0) {
            progress = 0;
        } else {
            progress = 101;
        }
        ProgressBar.setProgress(progress);
    }

    public void updatePercentage() {
        /**
         * This method is used to update the percentage display.
         * Bases on the value of progress, the ProgressBar is colored.
         */
        if (progress < 100) {
            TextViewProzentAnzeige.setText(progress + "%");
            ProgressBar.getProgressDrawable().clearColorFilter();
        } else  {
            TextViewProzentAnzeige.setText(R.string.mehrAls100);
            ProgressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.ueber100ProgressColor), PorterDuff.Mode.SRC_IN);
        }
    }
}