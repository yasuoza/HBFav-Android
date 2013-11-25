package com.hbfav.android.ui;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hbfav.R;

public class SubTitledSpinnerAdapter extends ArrayAdapter<String> {
    private CharSequence mActionBarTitle = "";

    public SubTitledSpinnerAdapter(Context context, int resource, int textViewResourceId, CharSequence actionBarTitle,
        String[] objects) {
        super(context, resource, textViewResourceId, objects);
        this.mActionBarTitle = actionBarTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        ((TextView) view.findViewById(R.id.actionbar_title_text)).setText(mActionBarTitle);
        return view;

    }
}
