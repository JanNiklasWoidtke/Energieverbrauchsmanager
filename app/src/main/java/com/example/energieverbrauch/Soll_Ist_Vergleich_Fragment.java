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
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * This fragment displays a combined Bar and LineChart to visualize the consumption and objectives of the user, as well as reference values.
 */

public class Soll_Ist_Vergleich_Fragment extends Fragment {

    int anfangsMonatDiagramme = 0;
    int anzahlPersonen = 1;
    float aktuellerVerbrauch = 0;
    boolean datenVerfuegbar = false;

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

        if (datenVerfuegbar) {

            monatlicherGesamtVerbrauch.add(aktuellerVerbrauch);

            barChartDataErstellen();

            lineChartDataErstellen();

            combinedChartErstellen();
        } else {
            Toast.makeText(getContext(), R.string.nochKeineDater, Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    public void barChartDataErstellen() {
        /**
         * This method is used to create and format the barData for the combinedChart
         */

        //Create total consumption entries

        ArrayList<BarEntry> entriesBarGesamtVerbrauchUnter = new ArrayList<>();
        ArrayList<BarEntry> entriesBarGesamtVerbrauchUeber = new ArrayList<>();

        int stelle = 12;

        for (int i = monatlicherGesamtVerbrauch.size() - 1; i >= 0; i--) {
            if (monatlicherGesamtVerbrauch.get(i) < monatlicherMaxVerbrauch.get(i)) {
                entriesBarGesamtVerbrauchUnter.add(new BarEntry(stelle - 0.2f, monatlicherGesamtVerbrauch.get(i)));

            } else {
                entriesBarGesamtVerbrauchUeber.add(new BarEntry(stelle - 0.2f, monatlicherGesamtVerbrauch.get(i)));
            }
            stelle--;
        }

        Collections.sort(entriesBarGesamtVerbrauchUeber, new EntryXComparator());
        Collections.sort(entriesBarGesamtVerbrauchUnter, new EntryXComparator());

        BarDataSet barDataSetGesamtVerbrauchUnter = new BarDataSet(entriesBarGesamtVerbrauchUnter, null);
        BarDataSet barDataSetGesamtVerbrauchUeber = new BarDataSet(entriesBarGesamtVerbrauchUeber, null);

        barDataSetGesamtVerbrauchUnter.setColor(ContextCompat.getColor(getContext(), R.color.unter100ProgressColor));
        barDataSetGesamtVerbrauchUnter.setDrawValues(false);

        barDataSetGesamtVerbrauchUeber.setColor(ContextCompat.getColor(getContext(), R.color.ueber100ProgressColor));
        barDataSetGesamtVerbrauchUeber.setDrawValues(false);

        //Create maxConsumption entries

        ArrayList<BarEntry> entriesBarMaxVerbrauch = new ArrayList<>();

        stelle = 12;
        for (int i = monatlicherGesamtVerbrauch.size() - 1; i >= 0; i--) {
            entriesBarMaxVerbrauch.add(new BarEntry(stelle + 0.2f, monatlicherMaxVerbrauch.get(i)));
            stelle--;
        }

        Collections.sort(entriesBarMaxVerbrauch, new EntryXComparator());

        BarDataSet barDataSetMaxVerbrauch = new BarDataSet(entriesBarMaxVerbrauch, null);
        barDataSetMaxVerbrauch.setColor(Color.GRAY);
        barDataSetMaxVerbrauch.setDrawValues(false);

        //Add data sets to barData

        barData.addDataSet(barDataSetGesamtVerbrauchUnter);
        barData.addDataSet(barDataSetGesamtVerbrauchUeber);
        barData.addDataSet(barDataSetMaxVerbrauch);

        barData.setBarWidth(0.4f);
    }

    public void lineChartDataErstellen() {
        /**
         * This method is used to create and format the lineData for the combinedChart.
         */

        if (anzahlPersonen > 3) {
            anzahlPersonen = 3;
        }

        if (anzahlPersonen < 0) {
            anzahlPersonen = 0;
        }

        float referenzVerbrauch = getResources().getIntArray(R.array.referenzwerte)[anzahlPersonen];

        ArrayList<Entry> entriesReferenzWerte = new ArrayList<>();

        int[] percentages = getResources().getIntArray(R.array.percentages);

        for (int i = 12; i > 0; i--) {
            if (anfangsMonatDiagramme + i - 12 >= 0) {
                entriesReferenzWerte.add(new Entry(i, referenzVerbrauch * percentages[anfangsMonatDiagramme + i - 12] / 10000));
            } else {
                entriesReferenzWerte.add(new Entry(i, referenzVerbrauch * percentages[anfangsMonatDiagramme + i] / 10000));
            }
        }

        Collections.sort(entriesReferenzWerte, new EntryXComparator());

        LineDataSet lineDataSet = new LineDataSet(entriesReferenzWerte, null);
        lineDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));

        lineData.addDataSet(lineDataSet);
    }

    public void combinedChartErstellen() {
        /**
         * This method is used to create and format the combinedChart out of the barChart and the lineChart
         */

        //format x-Axis

        final XAxis xAxis = monatlicherSollIstVergleich.getXAxis();
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(13);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        anfangsMonatDiagramme++;
        final String language = Locale.getDefault().getLanguage();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String[] monate;
                if (language.equals("de")) {
                    monate = getResources().getStringArray(R.array.monate);
                } else {
                    monate = getResources().getStringArray(R.array.months);
                }

                if (value == 0) {
                    return monate[(int) value];
                } else if (value == 13) {
                    return "";
                } else if (value + anfangsMonatDiagramme < 12) {
                    return monate[(int) (value + anfangsMonatDiagramme)];
                } else if (value + anfangsMonatDiagramme - 12 == 0) {
                    return monate[12];
                } else {
                    return monate[(int) (value + anfangsMonatDiagramme - 12)];
                }
            }
        });

        //format left y-Axis

        final YAxis yAxis = monatlicherSollIstVergleich.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0);
        yAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));

        //format the combinedChart

        monatlicherSollIstVergleich.getAxisRight().setDrawLabels(false);
        monatlicherSollIstVergleich.getAxisRight().setEnabled(false);
        monatlicherSollIstVergleich.getDescription().setEnabled(false);

        monatlicherSollIstVergleich.animateY(2000);
        monatlicherSollIstVergleich.invalidate();

        monatlicherSollIstVergleich.setDrawOrder(new CombinedChart.DrawOrder[]

                {
                        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
                });


        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        monatlicherSollIstVergleich.setData(combinedData);
        monatlicherSollIstVergleich.invalidate();

        addLegend();
    }

    public ArrayList<Float> floatArrayToArrayList(float[] FloatArray) {
        /**
         * This method transforms an C-Style float[] in an ArrayList<Float>
         */
        ArrayList<Float> arrayList = new ArrayList<>();
        for (int i = 0; i < FloatArray.length; i++) {
            arrayList.add(FloatArray[i]);
        }
        return arrayList;
    }

    public void bundleDataToMainActivityAuslesen() {
        /**
         * This method calls a method of the "MainActivity" to get the requrred data in to a fragment in a TabLayout
         */
        Bundle dataFromMainActivity = ((MainActivity) getActivity()).dataToSollIst();

        if (dataFromMainActivity.getFloatArray("monatlicherGesamtVerbrauch") != null) {
            datenVerfuegbar = true;
            anfangsMonatDiagramme = dataFromMainActivity.getInt("anfangsMonatDiagramme") - 1;
            monatlicherGesamtVerbrauch = floatArrayToArrayList(dataFromMainActivity.getFloatArray("monatlicherGesamtVerbrauch"));
            monatlicherMaxVerbrauch = floatArrayToArrayList(dataFromMainActivity.getFloatArray("monatlicherMaxVerbrauch"));
            anzahlPersonen = dataFromMainActivity.getInt("anzahlPersonen", 1) - 1;
            aktuellerVerbrauch = dataFromMainActivity.getFloat("aktuellerVerbrauch", 0);
        } else {
            datenVerfuegbar = false;
        }
    }

    public void addLegend() {
        /**
         * This method is used to create and format the legend of the combinedChart
         */
        Legend legend = monatlicherSollIstVergleich.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace(20f);

        LegendEntry[] legendEntries = new LegendEntry[4];
        String[] legendLabels = {getResources().getString(R.string.legendeReferenz), getResources().getString(R.string.legendeUnter100), getResources().getString(R.string.legendeUeber100), getResources().getString(R.string.legendeZiel)};
        int[] colorArray = {ContextCompat.getColor(getContext(), R.color.colorTextOnBackground), ContextCompat.getColor(getContext(), R.color.unter100ProgressColor), ContextCompat.getColor(getContext(), R.color.ueber100ProgressColor), Color.GRAY};

        for (int i = 0; i < legendEntries.length; i++){
            LegendEntry entry = new LegendEntry();
            entry.formColor = colorArray[i];
            entry.label = legendLabels[i];
            legendEntries[i] = entry;
        }
        legend.setCustom(legendEntries);
        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextOnBackground));
    }
}