package com.hbfav;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EntryListFragment extends ListFragment {
    private TextView headerTextView;
    private String[] entries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        entries = new String[] {
                "hello world 00",
                "hello world 01",
                "hello world 02"
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entry_list_view_main, container, false);

        setListAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                entries
        ));

        headerTextView = (TextView) rootView.findViewById(R.id.list_header_text);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        headerTextView.setText(entries[position]);
    }
}
