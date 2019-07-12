package com.example.energieverbrauch;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public ProgressBar PBcircle;
    public EditText EditTextMaxVerbrauchSoll;
    public TextView TextViewAktVerbrauch;

    float obergrenzeVerbrauch = 0;
    float aktuellerVerbrauch = 10;
    float verbrauchAnteilFloat = 0;
    int verbrauchAnteilInt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextViewAktVerbrauch = findViewById(R.id.aktVerbrauch);
        TextViewAktVerbrauch.setText(aktuellerVerbrauch + " kWh");

        EditTextMaxVerbrauchSoll = findViewById(R.id.maxVerbrauchSoll);

        EditTextMaxVerbrauchSoll.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String maxVerbrauchSollInput = EditTextMaxVerbrauchSoll.getText().toString().trim();

                if (!TextUtils.isEmpty(maxVerbrauchSollInput)) {

                    obergrenzeVerbrauch = Float.parseFloat(maxVerbrauchSollInput);

                    verbrauchsFortschrittBerechnen();

                    verbrauchsanzeigeAktualisieren();
                }

            }
        });

    }

    public void verbrauchsFortschrittBerechnen() {
        verbrauchAnteilFloat = aktuellerVerbrauch / obergrenzeVerbrauch * 100;// *100, da Progressbar von 0 bis 100 die Werte interpretier
        verbrauchAnteilInt = Math.round(verbrauchAnteilFloat);
    }

    public void verbrauchsanzeigeAktualisieren() {
        PBcircle = findViewById(R.id.PBcircle);
        if (verbrauchAnteilInt <= 100)PBcircle.setProgress(verbrauchAnteilInt);
        else PBcircle.setProgress(100);

    }
}
