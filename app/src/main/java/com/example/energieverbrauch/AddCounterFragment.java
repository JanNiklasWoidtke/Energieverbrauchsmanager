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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCounterFragment extends Fragment {

    public AddCounterFragmentListener listener;

    EditText EditTextZaehlername;
    EditText EditTextStandBeginn;
    EditText EditTextPreisProEinheit;
    Button ButtonErstelltenZaehlerHinzufuegen;

    String zaehlername;
    float standBeginn = -1;
    float preisProEinheit;
    boolean buttonErstelltenZaehlerHinzufuegenClicked = false;

    public interface AddCounterFragmentListener { //ermöglicht Senden an MainActivity
        void dataFromAddCounterFragmentToMainActivity(String Zaehlername, float standBeginn, float preisProEinheit);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addcounter, container, false);

        buttonErstelltenZaehlerHinzufuegenClicked = false;

        EditTextZaehlername = v.findViewById(R.id.Zählername);
        EditTextStandBeginn = v.findViewById(R.id.ZählerstandBeginn);
        EditTextPreisProEinheit = v.findViewById(R.id.PreisProEinheit);
        ButtonErstelltenZaehlerHinzufuegen = v.findViewById(R.id.ErstelltenZählerHinzufügen);

        EditTextZaehlername.addTextChangedListener(zaehlernameTextWatcher);
        EditTextStandBeginn.addTextChangedListener(zaehlerstandBeginnTextWatcher);
        EditTextPreisProEinheit.addTextChangedListener(preisProEinheitTextWatcher);

        ButtonErstelltenZaehlerHinzufuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zaehlername == null || TextUtils.isEmpty(EditTextStandBeginn.getText()) || TextUtils.isEmpty(EditTextPreisProEinheit.getText())) {
                    Toast.makeText(getContext(), R.string.fehlerhafteEingabe, Toast.LENGTH_SHORT).show();
                } else
                    listener.dataFromAddCounterFragmentToMainActivity(zaehlername, standBeginn, preisProEinheit);
            }
        });

        return v;
    }

    public TextWatcher preisProEinheitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getPreisProEinheit();
        }
    };

    public TextWatcher zaehlerstandBeginnTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getStandBeginn();
        }
    };

    public TextWatcher zaehlernameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getZaehlername();
        }
    };

    public void getZaehlername() {
        zaehlername = EditTextZaehlername.getText().toString();
    }

    public void getStandBeginn() {
        if (!TextUtils.isEmpty(EditTextStandBeginn.getText())) {
            String standBeginnString = EditTextStandBeginn.getText().toString();
            standBeginn = Float.parseFloat(standBeginnString);
        }
    }

    public void getPreisProEinheit() {
        if (!TextUtils.isEmpty(EditTextPreisProEinheit.getText())) {
            String preisProEinheitString = EditTextPreisProEinheit.getText().toString();
            preisProEinheit = Float.parseFloat(preisProEinheitString);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StartFragment.StartFragmentListener) {
            listener = (AddCounterFragment.AddCounterFragmentListener) context;
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
