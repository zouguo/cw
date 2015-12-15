package com.clinkworld.pay.whell;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.clinkworld.pay.R;


public class CityAdapter extends AbstractWheelTextAdapter {
    // City names
    String cities[] = new String[]{"New York", "Washington", "Chicago", "Atlanta", "Orlando"};


    /**
     * Constructor
     */
    public CityAdapter(Context context, String[] data, int currentIndex) {
        super(context, R.layout.whell_select_item, NO_RESOURCE, currentIndex);
        cities = data;
        setItemTextResource(R.id.whell_item);
    }

    public void setData(String[] data) {
        cities = data;
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        return view;
    }

    @Override
    public int getItemsCount() {
        return cities.length;
    }

    @Override
    public CharSequence getItemText(int index) {
        return cities[index];
    }
}