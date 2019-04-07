package com.ti_a.pengendali;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter {

    public DeviceListAdapter(Context context, ArrayList lists) {
        super(context, 0,lists);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
