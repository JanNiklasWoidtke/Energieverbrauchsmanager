package com.example.energieverbrauch;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabContainerFragmentStats extends Fragment implements Soll_Ist_Vergleich_Fragment.OnFragmentInteractionListener,
        Referenzwerte_Fragment.OnFragmentInteractionListener
{


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_container_stats, container, false);

        final TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tablayoutStats);

        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPagerStats = (ViewPager)v.findViewById(R.id.viewPagerStats);
        final PagerAdapterStats pagerAdapterStats = new PagerAdapterStats(getFragmentManager(), tabLayout.getTabCount(), getContext());

        viewPagerStats.setAdapter(pagerAdapterStats);
        tabLayout.setupWithViewPager(viewPagerStats);
        viewPagerStats.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerStats.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
