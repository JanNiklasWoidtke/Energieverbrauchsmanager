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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddCounterFragment extends Fragment {

    public AddCounterFragmentListener listener;

    EditText EditTextZaehlername;
    EditText EditTextStandBeginn;
    EditText EditTextPreisProEinheit;
    TextView textViewPreisEinheit;
    Button ButtonErstelltenZaehlerHinzufuegen;

    String zaehlername;
    float standBeginn = -1;
    float preisProEinheit;
    int zaehlernameSize = 0;

    public interface AddCounterFragmentListener { //ermöglicht Senden an MainActivity
        void dataFromAddCounterFragmentToMainActivity(String Zaehlername, float standBeginn, float preisProEinheit);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addcounter, container, false);

        Bundle dataFromMyCountersFrag = new Bundle();

        dataFromMyCountersFrag = getArguments();

        if (dataFromMyCountersFrag != null) {

            zaehlernameSize = dataFromMyCountersFrag.getInt("zaehlernameSize");
        }
        EditTextZaehlername = v.findViewById(R.id.Zählername);
        EditTextStandBeginn = v.findViewById(R.id.ZählerstandBeginn);
        textViewPreisEinheit = v.findViewById(R.id.textViewPreisEinheit);
        EditTextPreisProEinheit = v.findViewById(R.id.PreisProEinheit);
        ButtonErstelltenZaehlerHinzufuegen = v.findViewById(R.id.ErstelltenZählerHinzufügen);

        EditTextZaehlername.addTextChangedListener(zaehlernameTextWatcher);
        EditTextStandBeginn.addTextChangedListener(zaehlerstandBeginnTextWatcher);

        if (zaehlernameSize == 0) {
            EditTextPreisProEinheit.addTextChangedListener(preisProEinheitTextWatcher);
            EditTextPreisProEinheit.setHint(R.string.PreisEinheitNeuerZähler);

            textViewPreisEinheit.setText(R.string.EuroProKWh);
        } else {
            EditTextPreisProEinheit.setBackgroundColor(getResources().getColor(R.color.colorWhiteAsStandardBackground)); //muss an Background Colour des Schemes angepasst werden
            EditTextPreisProEinheit.setEms(0); //isClickable(false) funktioniert nicht
        }

        ButtonErstelltenZaehlerHinzufuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zaehlername == null || TextUtils.isEmpty(EditTextStandBeginn.getText())) {
                    Toast.makeText(getContext(), R.string.fehlerhafteEingabe, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(EditTextPreisProEinheit.getText()) && zaehlernameSize == 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.titlePreisNichtHinzugefuegt)
                            .setMessage(R.string.textPreisNichtHinzugefuegt)
                            .setPositiveButton(R.string.jetztAendern, null)
                            .setNegativeButton(R.string.spaeterAendern, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.dataFromAddCounterFragmentToMainActivity(zaehlername, standBeginn, 0);
                                }
                            })
                            .show();
                } else {
                    listener.dataFromAddCounterFragmentToMainActivity(zaehlername, standBeginn, preisProEinheit);
                }
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
        if (!TextUtils.isEmpty(EditTextStandBeginn.getText()) && !EditTextStandBeginn.getText().toString().equals(".")) {
            String standBeginnString = EditTextStandBeginn.getText().toString();
            standBeginn = Float.parseFloat(standBeginnString);
        }
    }

    public void getPreisProEinheit() {
        if (!TextUtils.isEmpty(EditTextPreisProEinheit.getText()) && !EditTextPreisProEinheit.getText().toString().equals(".")) {
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
