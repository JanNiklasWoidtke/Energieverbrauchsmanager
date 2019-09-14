package com.example.energieverbrauch;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This fragment displays reference values for power consumption of housholds with different amounts of people in it.
 * The consumption of the user is graphically comparable.
 */

public class Referenzwerte_Fragment extends Fragment {

    BarChart barChartReferenz;
    ArrayList<Integer> referenzwerte = new ArrayList<>();
    Bundle dataFromMainActivity = new Bundle();
    int eigenerVerbrauch = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_referenz, container, false);

        barChartReferenz = v.findViewById(R.id.barChartReferenz);

        dataFromMainActivity = ((MainActivity) getActivity()).dataToReferenzwerteFragMethod();

        referenzwerteFuellen();

        barChartFuellen();


        return v;
    }

    public void referenzwerteFuellen() {
        /**
         * This method gets the reference values from resources and fills the ArrayList containing the values.
         * The consumption of the user is also added in.
         * To later display the data by increasing y-values, the ArrayList is sorted from high to low.
         */
        for (int i = 0; i < getResources().getIntArray(R.array.referenzwerte).length; i++) {
            referenzwerte.add(getResources().getIntArray(R.array.referenzwerte)[i]);
        }

        eigenerVerbrauch = (int) dataFromMainActivity.getFloat("eigenerVerbrauch", 0);

        referenzwerte.add(eigenerVerbrauch);

        Collections.sort(referenzwerte);
    }

    public void barChartFuellen() {
        /**
         * This method fills up the BarChart with data.
         * Also, the BarChart gets formatted.
         */
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        int stelleEigenerVerbrauch = 0;
        boolean eigenenVerbrauchHinzufuegen = true;

        for (int i = 1; i <= referenzwerte.size(); i++) {
            if (referenzwerte.get(i - 1) != eigenerVerbrauch) {
                barEntries.add(new BarEntry(i, referenzwerte.get(i - 1)));
            } else {
                if (eigenenVerbrauchHinzufuegen) {
                    stelleEigenerVerbrauch = i;
                    eigenenVerbrauchHinzufuegen = false;
                } else {
                    barEntries.add(new BarEntry(i, referenzwerte.get(i - 1)));
                }
            }
        }

        ArrayList<BarEntry> barEntryEigenerVerbrauch = new ArrayList<>();

        barEntryEigenerVerbrauch.add(new BarEntry(stelleEigenerVerbrauch, eigenerVerbrauch));

        BarDataSet referenzDataSet = new BarDataSet(barEntries, "");
        BarDataSet eigenerVerbrauchDataSet = new BarDataSet(barEntryEigenerVerbrauch, "");

        referenzDataSet.setColor(Color.GRAY);
        referenzDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));
        referenzDataSet.setDrawValues(true);

        eigenerVerbrauchDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        eigenerVerbrauchDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));
        eigenerVerbrauchDataSet.setDrawValues(true);

        BarData barData = new BarData(referenzDataSet, eigenerVerbrauchDataSet);

        barChartReferenz.setData(barData);

        final int stelleEignerVerbrauchFinal = stelleEigenerVerbrauch;

        // Format x-Axis

        final XAxis xAxis = barChartReferenz.getXAxis();
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0.5f);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value == 0) {
                    return "";
                } else if ((int) value < stelleEignerVerbrauchFinal) {
                    return String.format(getResources().getString(R.string.personen), (int) value);
                } else if ((int) value == stelleEignerVerbrauchFinal) {
                    return getResources().getString(R.string.meinVerbrauch);
                } else {
                    return String.format(getResources().getString(R.string.personen), (int) value - 1);
                }
            }
        });

        // Format y-Axis

        final YAxis yAxis = barChartReferenz.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(Collections.max(referenzwerte) * 1.1f); //Achsenmaximum auf 110% des Maximalwertes setzen
        yAxis.setDrawGridLines(false);
        yAxis.setEnabled(false);

        barChartReferenz.getAxisRight().setDrawLabels(false);
        barChartReferenz.getAxisRight().setDrawGridLines(false);
        barChartReferenz.getAxisRight().setDrawAxisLine(false);
        barChartReferenz.getLegend().setEnabled(false);
        barChartReferenz.getDescription().setEnabled(false);
        barChartReferenz.animateY(2000);
        barChartReferenz.invalidate();
    }

    public interface OnFragmentInteractionListener {
    }
}
