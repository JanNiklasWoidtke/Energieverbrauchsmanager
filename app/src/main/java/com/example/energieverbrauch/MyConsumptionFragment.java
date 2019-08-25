package com.example.energieverbrauch;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;


public class MyConsumptionFragment extends Fragment {

    int anfangsMonatDiagramme = 0;

    ArrayList<Float> monatlicherGesamtVerbrauch = new ArrayList<>();
    ArrayList<Float> monatlicherMaxVerbrauch = new ArrayList<>();

    BarChart monatlicherSollIstVergleich;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_myconsumption, container, false);

        monatlicherSollIstVergleich = (BarChart) v.findViewById(R.id.barChart);

        bundleDataToMainActivityAuslesen();

        hilfsDatenErstellen();

        barChartSollIstErstellen();

        return v;
    }

    public void barChartSollIstErstellen(){

        ArrayList<BarEntry> entriesIstMonatlicherSollIstVergleich = new ArrayList<>();

        for (int i = 1; i < monatlicherGesamtVerbrauch.size() + 1; i++) {
            entriesIstMonatlicherSollIstVergleich.add(new BarEntry(2 * i - 0.375f, monatlicherGesamtVerbrauch.get(i - 1)));
        }

        BarDataSet barDataSetIstMonatlicherSollIstVergleich = new BarDataSet(entriesIstMonatlicherSollIstVergleich, null);

        barDataSetIstMonatlicherSollIstVergleich.setColor(Color.GREEN);
        barDataSetIstMonatlicherSollIstVergleich.setDrawValues(false);

        ArrayList<BarEntry> entriesSollMonatlicherSollIstVergleich = new ArrayList<>();

        for (int i = 1; i < monatlicherGesamtVerbrauch.size() + 1; i++) {
            entriesSollMonatlicherSollIstVergleich.add(new BarEntry(2 * i + 0.375f, monatlicherMaxVerbrauch.get(i - 1)));
        }

        BarDataSet barDataSetSollMonatlicherSollIstVergleich = new BarDataSet(entriesSollMonatlicherSollIstVergleich, null);
        barDataSetSollMonatlicherSollIstVergleich.setColor(Color.RED);
        barDataSetSollMonatlicherSollIstVergleich.setDrawValues(false);

        BarData barDataMonatlicherSollIstVergleich = new BarData(barDataSetIstMonatlicherSollIstVergleich, barDataSetSollMonatlicherSollIstVergleich);

        barDataMonatlicherSollIstVergleich.setBarWidth(0.75f);
        monatlicherSollIstVergleich.setData(barDataMonatlicherSollIstVergleich);

        monatlicherSollIstVergleich.getAxisRight().setDrawLabels(false);
        monatlicherSollIstVergleich.getLegend().setEnabled(false);
        XAxis xAxis = monatlicherSollIstVergleich.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        monatlicherSollIstVergleich.getDescription().setEnabled(false);
        monatlicherSollIstVergleich.invalidate();

    }

    public ArrayList<Float> floatArrayToArrayList(float[] FloatArray) {                             //wandelt Float-Array in ArrayList-Float um
        // wird benötigt, da ArrayList-Float nicht über Bundle an Fragments übergeben werden kann
        ArrayList<Float> arrayList = new ArrayList<>();
        for (int i = 0; i < FloatArray.length; i++) {
            arrayList.add(FloatArray[i]);
        }
        return arrayList;
    }

    public void bundleDataToMainActivityAuslesen() {
        Bundle dataFromMainActivity = new Bundle();

        dataFromMainActivity = getArguments();

        if (dataFromMainActivity != null) {
            anfangsMonatDiagramme = dataFromMainActivity.getInt("anfangsMonatDiagramme");
            monatlicherGesamtVerbrauch = floatArrayToArrayList(dataFromMainActivity.getFloatArray("monatlicherGesamtVerbrauch"));
            monatlicherMaxVerbrauch = floatArrayToArrayList(dataFromMainActivity.getFloatArray("monatlicherMaxVerbrauch"));
        }
    }

    public void hilfsDatenErstellen() {
        float hilfsfloat = 1;
        monatlicherGesamtVerbrauch.clear(); //nur zum Testen
        monatlicherMaxVerbrauch.clear(); //nur zum Testen

        for (int i = 0; i < 12; i++) {
            monatlicherGesamtVerbrauch.add(hilfsfloat);
            hilfsfloat++;
        }

        for (int i = 0; i < 12; i++) {
            monatlicherMaxVerbrauch.add(10.5f);
        }
    }
}

/*
BarChart chart1 = (BarChart)v.findViewById(R.id.barChart);

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        barEntries.add(new BarEntry(1,1));
        barEntries.add(new BarEntry(5,2));

        ArrayList<BarEntry> barEntries1 = new ArrayList<>();

        barEntries1.add(new BarEntry(3,3));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Test");
        BarDataSet barDataSet1 = new BarDataSet(barEntries1, "Test1");
        barDataSet1.setColor(Color.RED);

        BarData barData = new BarData(barDataSet, barDataSet1);
        chart1.setData(barData);
        chart1.getAxisRight().setDrawLabels(false);
        chart1.getLegend().setEnabled(false);
        XAxis xAxis = chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        chart1.invalidate();

        https://stackoverflow.com/questions/45320457/how-to-set-string-value-of-xaxis-in-mpandroidchart

        Combined Chart: https://stackoverflow.com/questions/31056095/mpandroidchart-combined-chart
 */
