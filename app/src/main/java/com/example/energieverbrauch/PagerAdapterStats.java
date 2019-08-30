package com.example.energieverbrauch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapterStats extends FragmentStatePagerAdapter {

    int anzahlTabs;
    public Context context;

    public PagerAdapterStats(FragmentManager fragmentManager, int anzahlTabsPager, Context c) {
        super(fragmentManager);
        context = c;
        this.anzahlTabs = anzahlTabsPager;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DurschnittsWerte_Fragment();
            case 1:
                return new Soll_Ist_Vergleich_Fragment();
            case 2:
                return new Referenzwerte_Fragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return anzahlTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String[] tabTitles = new String[]{context.getResources().getString(R.string.durschnittswerte), context.getResources().getString(R.string.sollIst), context.getResources().getString(R.string.referenzwerte)};
        return tabTitles[position];
    }
}
