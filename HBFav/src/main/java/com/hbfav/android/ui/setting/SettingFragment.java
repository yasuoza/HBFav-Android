package com.hbfav.android.ui.setting;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.ui.MainActivity;

public class SettingFragment extends ListFragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingFragment newInstance(int sectionNumber) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_view, container, false);

        String[] menus = new String[]{
                getString(R.string.setting_account),
        };

        setListAdapter(new ArrayAdapter<String>(
                getActivity(),
                R.layout.setting_list_item,
                android.R.id.text1,
                menus
        ));
        return rootView;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(getActivity(), UserRegistrationActivity.class));
                break;
            default:
                break;
        }
    }
}
