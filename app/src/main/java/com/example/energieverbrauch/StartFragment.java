package com.example.energieverbrauch;

import android.content.Context;
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

public class StartFragment extends Fragment {

    public StartFragmentListener listener;

    public EditText EditTextMaxVerbrauchSoll;
    public ProgressBar ProgressBar;
    public TextView TextViewAktuellerVerbrauch;
    public TextView TextViewProzentAnzeige;
    float aktuellerVerbrauch = 10;
    int progress = 0;
    float MaxVerbrauch = 0;

    public interface StartFragmentListener { //ermöglicht Senden an MainActivity
        void dataFromStartFragmentToMainActivity(int progress, float MaxVerbrauch);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);

        MainActivity MainActivity = (MainActivity) getActivity();
        int hint = (int) MainActivity.updateHint();

        progress = MainActivity.sendProgressData();

        EditTextMaxVerbrauchSoll = v.findViewById(R.id.maxVerbrauchSoll);
        ProgressBar = v.findViewById(R.id.PBcircle);
        TextViewAktuellerVerbrauch = v.findViewById(R.id.aktVerbrauch);
        TextViewProzentAnzeige = v.findViewById(R.id.prozentAnzeige);

        TextViewAktuellerVerbrauch.setText(Float.toString(aktuellerVerbrauch));

        EditTextMaxVerbrauchSoll.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                CharSequence input = EditTextMaxVerbrauchSoll.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    MaxVerbrauch = Float.parseFloat(input.toString());
                    calculateProgress(aktuellerVerbrauch, MaxVerbrauch);
                    updateProgressBar(progress);
                    updatePercentage(progress);
                    listener.dataFromStartFragmentToMainActivity(progress, MaxVerbrauch);
                }
            }
        });

        EditTextMaxVerbrauchSoll.setCursorVisible(false);

        updateProgressBar(progress);
        updatePercentage(progress);

        EditTextMaxVerbrauchSoll.setHint(Integer.toString(hint));
        
        return v;
    }

    public void calculateProgress(float aktuellerVerbrauch, float MaxVerbrauch) {
        float verbrauchterAnteil = aktuellerVerbrauch / MaxVerbrauch * 100;
        progress = (int) verbrauchterAnteil;
    }

    public void updateProgressBar(int progress) {
        ProgressBar.setProgress(progress);
    }

    public void updatePercentage(int progress) {
        /*if (progress<=100)*/TextViewProzentAnzeige.setText(progress + "%");
        //else TextViewProzentAnzeige.setText("Mehr als 100%");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StartFragmentListener) {
            listener = (StartFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement StartFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}