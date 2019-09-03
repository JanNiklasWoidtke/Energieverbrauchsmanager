package com.example.energieverbrauch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsFragment extends Fragment {

    public SettingsFragmentListener listener;
    Button buttonResetData;
    Switch switchDarkMode;
    EditText editTextPreisProEinheit;
    EditText editTextGrundBetrag;
    EditText editTextPersonen;

    public interface SettingsFragmentListener {
        void resetData();

        void setPreisProEinheit(float preisProEinheit);

        void setGrundBetrag(float grundBetrag);

        void setDarkMode();

        void setPersons(int persons);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        float grundBetrag = 0;
        float preisProEinheit = 0;
        int anzahlPersonen = 1;
        boolean darkModeAktiviert = false;

        buttonResetData = v.findViewById(R.id.buttonResetAllData);
        switchDarkMode = v.findViewById(R.id.switchDarkMode);
        editTextGrundBetrag = v.findViewById(R.id.editTextGrundBetrag);
        editTextPreisProEinheit = v.findViewById(R.id.editTextPreis);
        editTextPersonen = v.findViewById(R.id.editTextPersonen);

        Bundle statesFromMainActivity = getArguments();

        if (statesFromMainActivity != null) {
            preisProEinheit = statesFromMainActivity.getFloat("preisProEinheit", 0);
            grundBetrag = statesFromMainActivity.getFloat("grundBetrag", 0);
            darkModeAktiviert = statesFromMainActivity.getBoolean("darkModeAktiviert", false);
            anzahlPersonen = statesFromMainActivity.getInt("anzahlPersonen", 1);
        }

        switchDarkMode.setChecked(darkModeAktiviert);
        editTextPreisProEinheit.setText(String.valueOf(preisProEinheit));
        editTextGrundBetrag.setText(String.valueOf(grundBetrag));
        editTextPersonen.setText(String.valueOf(anzahlPersonen));

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listener.setDarkMode();
            }
        });

        editTextGrundBetrag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(editTextGrundBetrag.getText()) && !editTextGrundBetrag.getText().toString().equals(".")) {
                    listener.setGrundBetrag(Float.parseFloat(editTextGrundBetrag.getText().toString()));
                }
            }
        });

        editTextPersonen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty((editTextPersonen.getText())) && !editTextPersonen.getText().toString().equals(".")) {
                    listener.setPersons(Integer.parseInt(editTextPersonen.getText().toString()));
                }
            }
        });

        editTextPreisProEinheit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(editTextPreisProEinheit.getText()) && !editTextPreisProEinheit.getText().toString().equals(".")) {
                    listener.setPreisProEinheit(Float.parseFloat(editTextPreisProEinheit.getText().toString()));
                }
            }
        });

        buttonResetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.resetData)
                        .setMessage(R.string.resetMessage)
                        .setPositiveButton(R.string.ja, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.resetData();
                            }
                        })
                        .setNegativeButton(R.string.nein, null)
                        .show();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsFragment.SettingsFragmentListener) {
            listener = (SettingsFragment.SettingsFragmentListener) context;
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

//Bei reset sind switches falsch getogglet