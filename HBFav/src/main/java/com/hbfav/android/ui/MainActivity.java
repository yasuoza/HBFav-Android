package com.hbfav.android.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.hbfav.android.BuildConfig;
import com.hbfav.android.Constants;
import com.hbfav.android.R;
import com.hbfav.android.ui.about.AboutAppFragment;
import com.hbfav.android.ui.navigation.NavigationDrawerFragment;
import com.hbfav.android.ui.setting.SettingFragment;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Cache MainActivity context
     */
    private static Context contextOfApplication;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mSelectedSectionNumber;

    private ActionBar.OnNavigationListener mOnNavigationListener;

    private static RequestQueue mRequestQueue;


    public static Context getContextOfApplication() {
        return contextOfApplication;
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!BuildConfig.DEBUG && hasCrashlyticsApiKey(this)) {
            Crashlytics.start(this);
        }

        contextOfApplication = getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SettingFragment.newInstance(position))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, TimelineListFragment.newInstance(position))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, EntryListFragment.newInstance(position))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HotentryListFragment.newInstance(position))
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AboutAppFragment.newInstance(position))
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int number) {
        mTitle = Constants.MENUS[number].getTitle();
        mSelectedSectionNumber = number;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }
        switch (mSelectedSectionNumber) {
            case 2:
            case 3:
                break;
            default:
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setTitle(mTitle);
                break;
        }
    }

    public void restoreActionBar(ActionBar.OnNavigationListener onNavigationListener) {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SubTitledSpinnerAdapter adapter = new SubTitledSpinnerAdapter(
                actionBar.getThemedContext(),
                R.layout.subtitled_spinner_item,
                android.R.id.text1,
                mTitle,
                getResources().getStringArray(R.array.entry_category_list)
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mOnNavigationListener = onNavigationListener;
        actionBar.setListNavigationCallbacks(adapter, mOnNavigationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * @return true if the Crashlytics API key is declared in AndroidManifest.xml metadata,
     * otherwise return false.
     */
    static boolean hasCrashlyticsApiKey(Context context) {
        boolean hasValidKey = false;
        try {
            Context appContext = context.getApplicationContext();
            ApplicationInfo ai = appContext.getPackageManager().getApplicationInfo(
                    appContext.getPackageName(),
                    PackageManager.GET_META_DATA
            );
            Bundle bundle = ai.metaData;
            if (bundle != null) {
                String apiKey = bundle.getString("com.crashlytics.ApiKey");
                hasValidKey = apiKey != null && !apiKey.equals("0000000000000000000000000000000000000000");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("com.hbfav.android", "Unexpected NameNotFound.", e);
        }
        return hasValidKey;
    }
}
