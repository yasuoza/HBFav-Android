package com.hbfav.android.ui.about;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.ui.MainActivity;

public class AboutAppFragment extends Fragment {
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AboutAppFragment newInstance(int sectionNumber) {
        AboutAppFragment fragment = new AboutAppFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_app, container, false);
        String versionName = "";
        try {
            if (getActivity() != null && getActivity().getPackageManager() != null) {
                versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),
                        0).versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!versionName.isEmpty()) {
            ((TextView) rootView.findViewById(R.id.about_app_version_code)).setText(versionName);
        }

        ListView listView = (ListView) rootView.findViewById(R.id.about_app_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleListClicked(position);
            }
        });
        listView.setAdapter(new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new String[]{
                        getString(R.string.about_issues_demands),
                        getString(R.string.about_from_developer),
                        getString(R.string.about_credits)
                }
        ));
        return rootView;
    }


    private void handleListClicked(int number) {
        switch (number) {
            case 0:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ISSUES_URL)));
                break;
            case 2:
                startActivity(new Intent(getActivity(), CreditsActivity.class));
                break;
        }
    }
}
