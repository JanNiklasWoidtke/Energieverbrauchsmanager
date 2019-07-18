package com.example.energieverbrauch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddCounterFragment extends Fragment {

    public AddCounterFragmentListener listener;

    EditText EditTextZaehlername;
    EditText EditTextZaehlerstandBeginn;
    EditText EditTextPreisProEinheit;
    Button ButtonErstelltenZaehlerHinzufuegen;

    String zaehlername;
    float standBeginn;
    float preisProEinheit;
    boolean buttonErstelltenZaehlerHinzufuegenClicked = false;

    public interface AddCounterFragmentListener { //ermöglicht Senden an MainActivity
        void dataFromAddCounterFragmentToMainActivity(String Zaehlername, float standBeginn, float preisProEinheit, boolean buttonErstelltenZaehlerHinzufuegenClicked);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addcounter, container, false);

        buttonErstelltenZaehlerHinzufuegenClicked = false;

        EditTextZaehlername = v.findViewById(R.id.Zählername);
        EditTextZaehlerstandBeginn = v.findViewById(R.id.ZählerstandBeginn);
        EditTextPreisProEinheit = v.findViewById(R.id.PreisProEinheit);
        ButtonErstelltenZaehlerHinzufuegen = v.findViewById(R.id.ErstelltenZählerHinzufügen);

        EditTextZaehlername.addTextChangedListener(zaehlernameTextWatcher);
        EditTextZaehlerstandBeginn.addTextChangedListener(zaehlerstandBeginnTextWatcher);
        EditTextPreisProEinheit.addTextChangedListener(preisProEinheitTextWatcher);

        ButtonErstelltenZaehlerHinzufuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonErstelltenZaehlerHinzufuegenClicked = true;
                listener.dataFromAddCounterFragmentToMainActivity(zaehlername, standBeginn, preisProEinheit, buttonErstelltenZaehlerHinzufuegenClicked);
               // getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyCountersFragment()).commit();
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
        String standBeginnString = EditTextZaehlerstandBeginn.getText().toString();
        standBeginn = Float.parseFloat(standBeginnString);
    }

    public void getPreisProEinheit () {
        String preisProEinheitString = EditTextPreisProEinheit.getText().toString();
        preisProEinheit = Float.parseFloat(preisProEinheitString);
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
