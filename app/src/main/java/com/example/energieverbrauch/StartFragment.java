package com.example.energieverbrauch;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StartFragment extends Fragment {

    public StartFragmentListener listener;

    public EditText editTextMaxVerbrauchSoll;
    public ProgressBar ProgressBar;
    public TextView TextViewAktuellerVerbrauch;
    public TextView TextViewProzentAnzeige;
    float gesamtVerbrauch = 0;
    int progress = 0;
    float maxVerbrauch = 0;

    public interface StartFragmentListener { //ermöglicht Senden an MainActivity
        void dataFromStartFragmentToMainActivity(float maxVerbrauch);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_start, container, false);

        editTextMaxVerbrauchSoll = v.findViewById(R.id.maxVerbrauchSoll);
        ProgressBar = v.findViewById(R.id.PBcircle);
        TextViewAktuellerVerbrauch = v.findViewById(R.id.aktVerbrauch);
        TextViewProzentAnzeige = v.findViewById(R.id.prozentAnzeige);

        getBundleDataFromMainActivity();

        TextViewAktuellerVerbrauch.setText(String.valueOf(gesamtVerbrauch));


        editTextMaxVerbrauchSoll.setHint(String.valueOf(maxVerbrauch));

        //editTextMaxVerbrauchSoll.setHintTextColor(getResources().getColor(android.R.color.black)); //Farbe muss an Color-Scheme angepasst werden


        calculateProgress(gesamtVerbrauch, maxVerbrauch);
        updateProgressBar(progress);
        updatePercentage(progress);

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
                    maxVerbrauch = Float.parseFloat(input.toString());
                    calculateProgress(gesamtVerbrauch, maxVerbrauch);
                    updateProgressBar(progress);
                    updatePercentage(progress);
                    listener.dataFromStartFragmentToMainActivity(maxVerbrauch);
                }
            }
        });

        return v;
    }

    public void getBundleDataFromMainActivity() {
        Bundle dataFromMainActivity = ((MainActivity) getActivity()).dataToStartFragMethod();

        progress = dataFromMainActivity.getInt("progress", 0);
        maxVerbrauch = dataFromMainActivity.getFloat("maxVerbrauch", 0);
        gesamtVerbrauch = dataFromMainActivity.getFloat("gesamtVerbrauch", 0);
    }

    public void calculateProgress(float aktuellerVerbrauch, float MaxVerbrauch) {
        if (maxVerbrauch != 0) {
            progress = (int) (aktuellerVerbrauch / MaxVerbrauch * 100);
        }
        else {
            progress = 0;
        }
    }

    public void updateProgressBar(int progress) {
        ProgressBar.setProgress(progress);
    }

    public void updatePercentage(int progress) {
        if (progress <= 100) {
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}


//Mock-Ups: https://placeit.net/c/mockups/stages/galaxy-s9-mockup-template-against-transparent-background-a19508?customG_0=pwu6ngf3d7
