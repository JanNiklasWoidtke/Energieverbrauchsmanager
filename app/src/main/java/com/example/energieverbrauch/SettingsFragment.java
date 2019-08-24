package com.example.energieverbrauch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsFragment extends Fragment {

    public SettingsFragmentListener listener;
    Button buttonResetData;

    public interface SettingsFragmentListener {
        void resetData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        buttonResetData = v.findViewById(R.id.buttonResetAllData);

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
