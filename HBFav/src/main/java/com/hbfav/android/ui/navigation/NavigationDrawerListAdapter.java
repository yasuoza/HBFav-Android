package com.hbfav.android.ui.navigation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.model.NavigationItem;


public class NavigationDrawerListAdapter extends ArrayAdapter<NavigationItem> {
    private LayoutInflater inflater;
    private int layout;
    private NavigationItem[] items;

    public NavigationDrawerListAdapter(Context context, int textViewResourceId, NavigationItem[] items) {
        super(context, textViewResourceId, items);
        this.inflater = ((Activity) context).getLayoutInflater();
        this.layout = textViewResourceId;
        this.items = items;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(layout, parent, false);
        }

        NavigationItem item = items[position];
        ((ImageView) view.findViewById(R.id.navigation_drawer_item_icon)).setImageDrawable(item.getIcon());
        ((TextView) view.findViewById(android.R.id.text1)).setText(item.getTitle());
        return view;
    }
}
