package com.example.energieverbrauch;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;


public class Soll_Ist_Vergleich_Fragment extends Fragment {

    int anfangsMonatDiagramme = 0;
    int anzahlPersonen = 1;

    ArrayList<Float> monatlicherGesamtVerbrauch = new ArrayList<>();
    ArrayList<Float> monatlicherMaxVerbrauch = new ArrayList<>();

    CombinedChart monatlicherSollIstVergleich;

    BarData barData = new BarData();

    LineData lineData = new LineData();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_soll_ist, container, false);

        monatlicherSollIstVergleich = v.findViewById(R.id.combinedChartSollIst);

        bundleDataToMainActivityAuslesen();

        hilfsDatenErstellen();

        barChartDataErstellen();

        lineChartDataErstellen();

        combinedChartErstellen();

        return v;
    }

    public void lineChartDataErstellen() {

        float referenzVerbrauch = getResources().getIntArray(R.array.referenzwerte)[anzahlPersonen];

        ArrayList<Entry> entriesReferenzWerte = new ArrayList<>();

        int[] percentages = getResources().getIntArray(R.array.percentages);

        for (int i = 0; i < 12; i++) {
            if (i + anfangsMonatDiagramme - 1 < 12) {
                entriesReferenzWerte.add(new Entry(i, referenzVerbrauch * percentages[i + anfangsMonatDiagramme - 1] / 10000));
            }
            else {
                entriesReferenzWerte.add(new Entry(i, referenzVerbrauch * percentages[i + anfangsMonatDiagramme - 1 - 12] / 10000));
            }
        }

        LineDataSet lineDataSet = new LineDataSet(entriesReferenzWerte, null);
        lineDataSet.setColor(R.color.colorPrimaryDark);
        lineDataSet.setCircleColor(R.color.colorPrimaryDark);

        lineData.addDataSet(lineDataSet);
    }

    public void combinedChartErstellen() {
        //x-Achse formatieren

        final XAxis xAxis = monatlicherSollIstVergleich.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String[] monate = getResources().getStringArray(R.array.monate);
                if (value == 0) return monate[(int) value];
                else if (value + anfangsMonatDiagramme <= monatlicherMaxVerbrauch.size()) {
                    return monate[(int) (value + anfangsMonatDiagramme)];
                } else {
                    return monate[(int) (value + anfangsMonatDiagramme - 12)];
                }
            }
        });

        //y-Achse formmatieren

        final YAxis yAxis = monatlicherSollIstVergleich.getAxisLeft();
        yAxis.setDrawGridLines(false);

        //Chart formatieren

        monatlicherSollIstVergleich.getAxisRight().setDrawLabels(false);
        monatlicherSollIstVergleich.getLegend().setEnabled(false);
        monatlicherSollIstVergleich.getDescription().setEnabled(false);
        monatlicherSollIstVergleich.animateY(2000);
        monatlicherSollIstVergleich.invalidate();

        monatlicherSollIstVergleich.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR,  CombinedChart.DrawOrder.LINE
        });

        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        monatlicherSollIstVergleich.setData(combinedData);
        monatlicherSollIstVergleich.invalidate();
    }

    public void barChartDataErstellen() {

        //Gesamtverbrauch Einträge erstellen und formatieren

        ArrayList<BarEntry> entriesBarGesamtVerbrauchUnter = new ArrayList<>();
        ArrayList<BarEntry> entriesBarGesamtVerbrauchUeber = new ArrayList<>();


        for (int i = 1; i < monatlicherGesamtVerbrauch.size() + 1; i++) {
            if (monatlicherGesamtVerbrauch.get(i - 1) < monatlicherMaxVerbrauch.get(i - 1)) {
                entriesBarGesamtVerbrauchUnter.add(new BarEntry(i - 0.2f, monatlicherGesamtVerbrauch.get(i - 1)));
            } else {
                entriesBarGesamtVerbrauchUeber.add(new BarEntry(i - 0.2f, monatlicherGesamtVerbrauch.get(i - 1)));
            }
        }
        BarDataSet barDataSetGesamtVerbrauchUnter = new BarDataSet(entriesBarGesamtVerbrauchUnter, null);
        BarDataSet barDataSetGesamtVerbrauchUeber = new BarDataSet(entriesBarGesamtVerbrauchUeber, null);

        barDataSetGesamtVerbrauchUnter.setColor(Color.GREEN);
        barDataSetGesamtVerbrauchUnter.setDrawValues(false);

        barDataSetGesamtVerbrauchUeber.setColor(Color.RED);
        barDataSetGesamtVerbrauchUeber.setDrawValues(false);

        //MaxVerbrauch Einträge erstellen und formatieren

        ArrayList<BarEntry> entriesBarMaxVerbrauch = new ArrayList<>();

        for (int i = 1; i < monatlicherGesamtVerbrauch.size() + 1; i++) {
            entriesBarMaxVerbrauch.add(new BarEntry(i + 0.2f, monatlicherMaxVerbrauch.get(i - 1)));
        }

        BarDataSet barDataSetMaxVerbrauch = new BarDataSet(entriesBarMaxVerbrauch, null);
        barDataSetMaxVerbrauch.setColor(Color.BLACK);
        barDataSetMaxVerbrauch.setDrawValues(false);

        //Data-Sets der Bardata hinzufügen und formatieren

        barData.addDataSet(barDataSetGesamtVerbrauchUnter);
        barData.addDataSet(barDataSetGesamtVerbrauchUeber);
        barData.addDataSet(barDataSetMaxVerbrauch);

        barData.setBarWidth(0.4f);
        //monatlicherSollIstVergleich.setData(barDataMonatlicherSollIstVergleich);
    }

    public ArrayList<Float> floatArrayToArrayList(float[] FloatArray) {                             //wandelt Float-Array in ArrayList-Float um
        // wird benötigt, da ArrayList-Float nicht über Bundle an Fragments übergeben werden kann
        ArrayList<Float> arrayList = new ArrayList<>();
        for (int i = 0; i < FloatArray.length; i++) {
            arrayList.add(FloatArray[i]);
        }
        return arrayList;
    }

    public ArrayList<String> stringArraytoStringArrayList(String[] StringArray) {                             //wandelt Float-Array in ArrayList-Float um
        // wird benötigt, da ArrayList-Float nicht über Bundle an Fragments übergeben werden kann
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < StringArray.length; i++) {
            arrayList.add(StringArray[i]);
        }
        return arrayList;
    }

    public void bundleDataToMainActivityAuslesen() {
        Bundle dataFromMainActivity = ((MainActivity) getActivity()).dataToSollIst();
        anfangsMonatDiagramme = dataFromMainActivity.getInt("anfangsMonatDiagramme") - 1;
        monatlicherGesamtVerbrauch = floatArrayToArrayList(dataFromMainActivity.getFloatArray("monatlicherGesamtVerbrauch"));
        monatlicherMaxVerbrauch = floatArrayToArrayList(dataFromMainActivity.getFloatArray("monatlicherMaxVerbrauch"));
        anzahlPersonen = dataFromMainActivity.getInt("anzahlPersonen", 1);
    }


    public void hilfsDatenErstellen() {
        float hilfsfloat = 160;
        monatlicherGesamtVerbrauch.clear(); //nur zum Testen
        monatlicherMaxVerbrauch.clear(); //nur zum Testen

        for (int i = 0; i < 12; i++) {
            monatlicherGesamtVerbrauch.add(hilfsfloat);
            hilfsfloat += 2;
        }

        for (int i = 0; i < 12; i++) {
            monatlicherMaxVerbrauch.add(170f);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
