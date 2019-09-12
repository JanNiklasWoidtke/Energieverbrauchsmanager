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

/**
 * This Fragment is used to add devices.
 * The user enters the required data and by clicking a button adds the device to the device-list.
 */

public class AddCounterFragment extends Fragment {

    public AddCounterFragmentListener listener;

    EditText editTextDeviceName;
    EditText editTextInitialStanding;
    EditText editTextPricePerUnit;
    TextView textViewPricePerUnit;
    Button buttonAddNewDevice;

    Bundle dataFromMyCountersFrag = new Bundle();

    String zaehlername;
    float standBeginn = -1;
    float preisProEinheit;
    int zaehlernameSize = 0;

    public interface AddCounterFragmentListener {
        /**
         * Enables data transfer to the "MainActivity"
         * @param Zaehlername name of the added Device
         * @param standBeginn initial standing of the added Device
         * @param preisProEinheit price per unit of power
         */
        void dataFromAddCounterFragmentToMainActivity(String Zaehlername, float standBeginn, float preisProEinheit);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addcounter, container, false);

        editTextDeviceName = v.findViewById(R.id.Zählername);
        editTextInitialStanding = v.findViewById(R.id.ZählerstandBeginn);
        textViewPricePerUnit = v.findViewById(R.id.textViewPreisEinheit);
        editTextPricePerUnit = v.findViewById(R.id.PreisProEinheit);
        buttonAddNewDevice = v.findViewById(R.id.ErstelltenZählerHinzufügen);

        editTextDeviceName.addTextChangedListener(deviceNameTextWatcher);
        editTextInitialStanding.addTextChangedListener(initialStandingTextWatcher);

        getDataFromMainActivity();

        setPricePerUnitValues();

        createListenerButtonAddDevice();

        return v;
    }

    public void createListenerButtonAddDevice() {

        /**
         * This method adds an OnClickListener to the button "buttonErstelltenZaehlerHinzufuegen".
         * If all fields are filled correctly, the device is added by tranfering the entered data to the "MainActivity".
         * If the inputs are invalid, a Toast opens up and tells the user to enter all relevant data.
         * If no devices are registered yet and no price per unit is entered, a DialogBox opens up to tell the user how and where to enter/change the price.
         */

        buttonAddNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zaehlername == null || TextUtils.isEmpty(editTextInitialStanding.getText())) {
                    Toast.makeText(getContext(), R.string.fehlerhafteEingabe, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editTextPricePerUnit.getText()) && zaehlernameSize == 0) {
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
    }

    public void setPricePerUnitValues() {
        /**
         * This method sets values for the pricePerUnit-Section based on, whether a device is already registered.
         * If no devices are registered, the pricePerUnit-Section is shown with the corresponging texts and hints.
         * If a device is already registerd, the pricePerUnit-Section is hidden.
         */
        if (zaehlernameSize == 0) {
            editTextPricePerUnit.addTextChangedListener(pricePerUnitTextWatcher);
            editTextPricePerUnit.setHint(R.string.PreisEinheitNeuerZähler);

            textViewPricePerUnit.setText(R.string.EuroProKWh);
        } else {
            editTextPricePerUnit.setBackgroundColor(getResources().getColor(R.color.colorBackground)); //muss an Background Colour des Schemes angepasst werden
            editTextPricePerUnit.setEms(0); //isClickable(false) funktioniert nicht
        }
    }

    public void getDataFromMainActivity(){
        /**
         * This method gets the required data from the MainActivity.
         * The arguments of the Fragment are read and put into a bundle.
         * Out of the bundle, the data can be accessed via the keys.
         */

        dataFromMyCountersFrag = getArguments();

        if (dataFromMyCountersFrag != null) {
            zaehlernameSize = dataFromMyCountersFrag.getInt("zaehlernameSize");
        }
    }

    /**
     * The following TextWatchers are used to get the text inputs from the editTexts into variables.
     */

    public TextWatcher pricePerUnitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(editTextPricePerUnit.getText()) && !editTextPricePerUnit.getText().toString().equals(".")) {
                String preisProEinheitString = editTextPricePerUnit.getText().toString();
                preisProEinheit = Float.parseFloat(preisProEinheitString);
            }
        }
    };

    public TextWatcher initialStandingTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(editTextInitialStanding.getText()) && !editTextInitialStanding.getText().toString().equals(".")) {
                String standBeginnString = editTextInitialStanding.getText().toString();
                standBeginn = Float.parseFloat(standBeginnString);
            }
        }
    };

    public TextWatcher deviceNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            zaehlername = editTextDeviceName.getText().toString();
        }
    };

    /**
     * The following methods are necessary to pass data between fragments and activities using the interface
     */

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
