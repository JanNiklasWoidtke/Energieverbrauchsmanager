package com.example.energieverbrauch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * This class is used to switch between tabs in a tablayout.
 */

public class PagerAdapterStart extends FragmentStatePagerAdapter {

    int anzahlTabs;
    public Context context;

    public PagerAdapterStart(FragmentManager fragmentManager, int anzahlTabsPager, Context c) {
        super(fragmentManager);
        context = c;
        this.anzahlTabs = anzahlTabsPager;
    }

    @Override
    public Fragment getItem(int position) {
        /**
         * This method returns the chosen fragment based on the int position.
         * By swiping between fragments, position is set.
         */
        switch (position) {
            case 0:
                return new StartFragmentAlt();
            case 1:
                return new StartFragmentJahr();
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
        /**
         * This method sets the tabtitles of the tabs from resource strings.
         */
        String[] tabTitles = new String[] {context.getResources().getString(R.string.monat), context.getResources().getString(R.string.jahr)};
        return tabTitles[position];
    }
}
