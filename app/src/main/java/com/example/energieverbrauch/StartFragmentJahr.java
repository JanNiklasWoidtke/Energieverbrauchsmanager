package com.example.energieverbrauch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StartFragmentJahr extends Fragment {

    public StartFragmentJahrListener listener;

    public EditText editTextMaxVerbrauchSoll;
    public ProgressBar ProgressBar;
    public TextView TextViewAktuellerVerbrauchJahr;
    public TextView TextViewProzentAnzeige;

    float gesamtVerbrauchJahr = 0;
    int progressJahr = 0;
    float maxVerbrauchJahr = 0;

    public interface StartFragmentJahrListener { //ermöglicht Senden an MainActivity
        void dataFromStartFragmentJahrToMainActivity(int progressJahr, float maxVerbrauchJahr);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start_jahr, container, false);

        editTextMaxVerbrauchSoll = v.findViewById(R.id.maxVerbrauchSollJahr);
        ProgressBar = v.findViewById(R.id.PBcircleJahr);
        TextViewAktuellerVerbrauchJahr = v.findViewById(R.id.aktVerbrauchJahr);
        TextViewProzentAnzeige = v.findViewById(R.id.prozentAnzeigeJahr);

        getBundleDataFromMainActivity();

        TextViewAktuellerVerbrauchJahr.setText(String.valueOf(gesamtVerbrauchJahr));


        editTextMaxVerbrauchSoll.setHint(String.valueOf(maxVerbrauchJahr));

        // editTextMaxVerbrauchSoll.setHintTextColor(getResources().getColor(android.R.color.black)); //Farbe muss an Color-Scheme angepasst werden


        calculateProgress();
        updateProgressBar(progressJahr);
        updatePercentage(progressJahr);

        editTextMaxVerbrauchSoll.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CharSequence input = editTextMaxVerbrauchSoll.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    maxVerbrauchJahr = Float.parseFloat(input.toString());
                    calculateProgress();
                    updateProgressBar(progressJahr);
                    updatePercentage(progressJahr);
                    listener.dataFromStartFragmentJahrToMainActivity(progressJahr, maxVerbrauchJahr);
                }
            }
        });

        return v;
    }


    public void getBundleDataFromMainActivity() {
        /*Bundle dataFromMainActivity = getArguments();

        if (dataFromMainActivity != null) {
            progressJahr = dataFromMainActivity.getInt("progressJahr", 0);
            maxVerbrauchJahr = dataFromMainActivity.getFloat("maxVerbrauchJahr", 0);
            gesamtVerbrauch = dataFromMainActivity.getFloat("gesamtVerbrauch", 0);
        }

        else {*/
        Bundle dataFromMainActivityJahr = ((MainActivity) getActivity()).dataToStartFragJahrMethod();

        gesamtVerbrauchJahr = dataFromMainActivityJahr.getFloat("gesamtVerbrauchJahr", 0);
        maxVerbrauchJahr = dataFromMainActivityJahr.getFloat("maxVerbrauchJahr", 0);
        progressJahr = dataFromMainActivityJahr.getInt("progressJahr;", 0);

    }
    //}

    public void calculateProgress() {
        if (maxVerbrauchJahr != 0) {
            progressJahr = (int) (gesamtVerbrauchJahr / maxVerbrauchJahr * 100);
        }
        else {
            progressJahr = 0;
        }
    }

    public void updateProgressBar(int progress) {
        ProgressBar.setProgress(progress);
    }

    public void updatePercentage(int progress) {
        if (progress <= 100) TextViewProzentAnzeige.setText(progress + "%");
        else TextViewProzentAnzeige.setText("Mehr als 100%");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StartFragmentJahrListener) {
            listener = (StartFragmentJahrListener) context;
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

