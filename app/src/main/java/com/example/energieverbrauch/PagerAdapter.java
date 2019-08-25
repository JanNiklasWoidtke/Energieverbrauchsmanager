package com.example.energieverbrauch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int anzahlTabs;

    private String[] tabTitles = new String[]{"Monat", "Jahr"};

    public PagerAdapter(FragmentManager fragmentManager, int anzahlTabsPager) {
        super(fragmentManager);
        this.anzahlTabs = anzahlTabsPager;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                StartFragment startFragment = new StartFragment();
                return new StartFragment();
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

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
