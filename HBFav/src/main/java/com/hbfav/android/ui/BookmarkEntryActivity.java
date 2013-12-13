package com.hbfav.android.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hbfav.android.R;
import com.hbfav.android.core.HatenaApiManager;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.model.Entry;
import com.hbfav.android.model.HatenaApi;
import com.hbfav.android.model.HatenaBookmark;
import com.hbfav.android.util.HBFavUtils;

import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import java.util.ArrayList;
import java.util.Arrays;

public class BookmarkEntryActivity extends Activity {

    private Context mContext;

    private Entry mEntry;

    private boolean needShowControllPanel;
    private boolean isKeyboardShown;

    private Menu mMenu;

    private TextView mEntryTitleTextView;
    private TextView mEntryUrlTextView;
    private TextView mEntryBookmarkCountTextView;

    private EditText mCommentEditText;
    private EditText mTagsEditText;

    private Button mRecommendTagsButton;
    private Button mAllTagsButton;
    private Button mShareConfigButton;

    private ScrollView mControlScrollView;
    private FlowLayout mRecommendTagsArea;
    private FlowLayout mMyTagsArea;
    private LinearLayout mShareConfigView;

    private CheckBox mCheckBoxTwitter;
    private CheckBox mCheckBoxFacebook;
    private CheckBox mCheckBoxMixi;
    private CheckBox mCheckBoxEvernote;
    private CheckBox mCheckBoxPrivate;

    private ProgressDialog mProgressDialog;

    private ArrayList<String> mSelectedTags = new ArrayList<String>();

    private final String TAG = "com.hbfav.android.bookmark";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        needShowControllPanel = false;
        isKeyboardShown = false;

        mEntry = getIntent().getParcelableExtra("entry");
        if (mEntry == null) {
            mEntry = new Entry("", getIntent().getStringExtra("entryUrl"), 0);
        }

        fetchBookmarkStatus();
        mEntry.fetchRecommendTagsIfNeeded();

        UserInfoManager.refreshMyTagsIfNeeded();
        UserInfoManager.refreshShareServiceAvailability();

        setContentView(R.layout.activity_bookamrk_entry);
        setUpActivity();
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookmark_entry_activity_actions, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        org.scribe.model.Verb method;
        switch (item.getItemId()) {
            case R.id.action_save:
                method = Verb.POST;
                break;

            case R.id.action_update:
                method = Verb.POST;
                break;

            case R.id.action_delete:
                method = Verb.DELETE;
                break;

            default:
                return false;
        }
        final Verb requestMethod = method;
        new AsyncTask<Void, Void, Boolean>() {
            protected void onPreExecute() {
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setMessage("Please wait.");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
            }

            protected Boolean doInBackground(Void... params) {
                OAuthRequest request = new OAuthRequest(requestMethod, HatenaApi.BOOKMARK_URL);
                request.addQuerystringParameter("url", Uri.decode(mEntry.getLink()));
                request.addQuerystringParameter("comment", mCommentEditText.getText().toString());
                for (String tag : mSelectedTags) {
                    request.addQuerystringParameter("tags", tag);
                }
                request.addQuerystringParameter("post_twitter", HBFavUtils.boolToString(mCheckBoxTwitter.isChecked()));
                request.addQuerystringParameter("post_facebook", HBFavUtils.boolToString(mCheckBoxFacebook.isChecked()));
                request.addQuerystringParameter("post_mixi", HBFavUtils.boolToString(mCheckBoxMixi.isChecked()));
                request.addQuerystringParameter("post_evernote", HBFavUtils.boolToString(mCheckBoxEvernote.isChecked()));
                request.addQuerystringParameter("private", HBFavUtils.boolToString(mCheckBoxPrivate.isChecked()));
                HatenaApiManager.getService().signRequest(UserInfoManager.getAccessToken(), request);
                org.scribe.model.Response response;
                try {
                    response = request.send();
                    return response.getCode() == 200;
                } catch (OAuthConnectionException e) {
                    e.printStackTrace();
                }
                return false;
            }

            protected void onPostExecute(Boolean success) {
                mProgressDialog.dismiss();
                if (success) {
                    finish();
                } else {
                    // operation failed!
                }
            }
        }.execute();

        return true;
    }

    /**
     * Set up BookmarkEntryActivity *
     */
    private void setUpActivity() {
        final View activityRootView = findViewById(R.id.activity_view);
        if (activityRootView != null && activityRootView.getViewTreeObserver() != null) {
            activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            // if heightDiff is more than 200 pixels, its probably a keyboard...
                            int heightDiff
                                    = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                            if (heightDiff > 200 || !needShowControllPanel) {
                                resetToolbarButtonColor();
                                mControlScrollView.setVisibility(View.GONE);
                            } else {
                                mControlScrollView.setVisibility(View.VISIBLE);
                                if (mRecommendTagsArea.isShown()) {
                                    highlightToolbarButtonOnly(mRecommendTagsButton);
                                } else if (mMyTagsArea.isShown()) {
                                    highlightToolbarButtonOnly(mAllTagsButton);
                                } else {
                                    highlightToolbarButtonOnly(mShareConfigButton);
                                }
                            }
                            isKeyboardShown = (heightDiff > 200);
                        }
                    });
        }

        mEntryTitleTextView = (TextView) findViewById(R.id.activity_bookmark_entry_title);
        mEntryTitleTextView.setText(mEntry.getTitle());

        mEntryUrlTextView = (TextView) findViewById(R.id.activity_bookmark_entry_url);
        mEntryUrlTextView.setText(mEntry.getLink());

        mEntryBookmarkCountTextView = (TextView) findViewById(R.id.activity_bookmark_bookmark_count);
        mEntryBookmarkCountTextView.setText(HBFavUtils.usersToString(mEntry.getCount()));

        mCommentEditText = (EditText) findViewById(R.id.input_comment);
        mCommentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                needShowControllPanel = !hasFocus;
                resetToolbarButtonColor();
            }
        });

        mTagsEditText = (EditText) findViewById(R.id.input_tag);
        mTagsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                needShowControllPanel = hasFocus;
            }
        });
        mTagsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                mSelectedTags = new ArrayList<String>(Arrays.asList(mTagsEditText.getText().toString().split(" ")));
            }
        });

        mRecommendTagsButton = (Button) findViewById(R.id.recommend_button);
        mRecommendTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTagsEditText.requestFocus();

                dismissKeyboard();

                if (!isKeyboardShown) {
                    mControlScrollView.setVisibility(View.VISIBLE);
                }

                highlightToolbarButtonOnly(mRecommendTagsButton);
                layoutRecommendTagButtons();
            }
        });

        mAllTagsButton = (Button) findViewById(R.id.all_button);
        mAllTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTagsEditText.requestFocus();

                dismissKeyboard();

                if (!isKeyboardShown) {
                    mControlScrollView.setVisibility(View.VISIBLE);
                }

                highlightToolbarButtonOnly(mAllTagsButton);
                layoutAllTagButtons();
            }
        });

        mShareConfigButton = (Button) findViewById(R.id.share_config_button);
        mShareConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                needShowControllPanel = true;

                dismissKeyboard();

                if (!isKeyboardShown) {
                    mControlScrollView.setVisibility(View.VISIBLE);
                }

                fetchMyShareConfig();
                highlightToolbarButtonOnly(mShareConfigButton);
                setVisibleControllPanelOnly(mShareConfigView);
            }
        });

        mControlScrollView = (ScrollView) findViewById(R.id.control_area_scroll_view);
        mRecommendTagsArea = (FlowLayout) findViewById(R.id.recomment_tags_area);
        mMyTagsArea = (FlowLayout) findViewById(R.id.my_tags_area);
        mShareConfigView = (LinearLayout) findViewById(R.id.share_config_area);

        mCheckBoxTwitter = (CheckBox) findViewById(R.id.checkbox_twitter);
        mCheckBoxFacebook = (CheckBox) findViewById(R.id.checkbox_facebook);
        mCheckBoxMixi = (CheckBox) findViewById(R.id.checkbox_mixi);
        mCheckBoxEvernote = (CheckBox) findViewById(R.id.checkbox_evernote);
        mCheckBoxPrivate = (CheckBox) findViewById(R.id.checkbox_private);

        layoutRecommendTagButtons();
    }

    private void fetchBookmarkStatus() {
        new AsyncTask<Void, Void, Boolean>() {
            private HatenaBookmark hatenaBookmark;

            @Override
            protected Boolean doInBackground(Void... params) {
                OAuthRequest request = new OAuthRequest(Verb.GET, HatenaApi.BOOKMARK_URL);
                request.addQuerystringParameter("url", mEntry.getLink());
                HatenaApiManager.getService().signRequest(UserInfoManager.getAccessToken(), request);
                org.scribe.model.Response response;
                try {
                    response = request.send();
                } catch (OAuthConnectionException e) {
                    e.printStackTrace();
                    return false;
                }

                boolean success = (response.getCode() == 200);
                if (!success) {
                    return false;
                }

                Gson gson = new Gson();
                hatenaBookmark = gson.fromJson(response.getBody(), HatenaBookmark.class);
                return true;
            }

            protected void onPostExecute(Boolean alreadyBookmarked) {
                if (mMenu == null) {
                    return;
                }

                MenuItem actionSave = mMenu.findItem(R.id.action_save);
                MenuItem actionUpdate = mMenu.findItem(R.id.action_update);
                MenuItem actionDelete = mMenu.findItem(R.id.action_delete);

                if (actionSave == null || actionUpdate == null || actionDelete == null) {
                    return;
                }

                if (alreadyBookmarked) {
                    actionSave.setVisible(false);
                    actionUpdate.setVisible(true);
                    actionDelete.setVisible(true);
                } else {
                    actionSave.setVisible(true);
                    actionUpdate.setVisible(false);
                    actionDelete.setVisible(false);
                    return;
                }

                if (mCommentEditText != null) {
                    mCommentEditText.setText(hatenaBookmark.getComment());
                }

                if (mTagsEditText != null) {
                    mTagsEditText.setText(TextUtils.join(" ", hatenaBookmark.getTags()));
                }

                if (mCheckBoxPrivate != null) {
                    mCheckBoxPrivate.setChecked(hatenaBookmark.isPrivate());
                }
            }
        }.execute();
    }

    public void fetchMyShareConfig() {
        mCheckBoxTwitter.setEnabled(UserInfoManager.isOauthTwitter());
        mCheckBoxFacebook.setEnabled(UserInfoManager.isOauthFacebook());
        mCheckBoxMixi.setEnabled(UserInfoManager.isOauthMixi());
        mCheckBoxEvernote.setEnabled(UserInfoManager.isOauthEvernote());
    }


    /** UI **/

    private void layoutRecommendTagButtons() {
        setVisibleControllPanelOnly(mRecommendTagsArea);

        int areaChildCount = mRecommendTagsArea.getChildCount();
        if (areaChildCount > 0) {
            for (int i = 0; i < areaChildCount; i++) {
                Button button = (Button) mRecommendTagsArea.getChildAt(i);
                button.setPressed(mSelectedTags.indexOf(button.getText()) != -1);
            }
            return;
        }

        for (String tag : mEntry.getmRecommendTags()) {
            final Button button = new Button(mContext);
            button.setText(tag);
            button.setPressed(mSelectedTags.indexOf(tag) != -1);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return onTagButtonTouch(button, event);
                }
            });
            mRecommendTagsArea.addView(button);
        }
    }

    private void layoutAllTagButtons() {
        setVisibleControllPanelOnly(mMyTagsArea);

        int areaChildCount = mMyTagsArea.getChildCount();
        if (areaChildCount > 0) {
            for (int i = 0; i < areaChildCount; i++) {
                Button button = (Button) mMyTagsArea.getChildAt(i);
                button.setPressed(mSelectedTags.indexOf(button.getText()) != -1);
            }
            return;
        }

        for (String tag : UserInfoManager.getMyTags()) {
            final Button button = new Button(mContext);
            button.setText(tag);
            button.setPressed(mSelectedTags.indexOf(tag) != -1);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return onTagButtonTouch(button, event);
                }
            });
            mMyTagsArea.addView(button);
        }
    }

    private void dismissKeyboard() {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS
        );
    }

    private boolean onTagButtonTouch(final Button button, final MotionEvent event) {
        // show interest in events resulting from ACTION_DOWN
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        // don't handle event unless its ACTION_UP
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }

        if (mSelectedTags.indexOf(button.getText()) != -1) {
            mSelectedTags.remove(button.getText());
            button.setPressed(false);
        } else {
            mSelectedTags.add(button.getText().toString());
            button.setPressed(true);
        }
        String tagsText = TextUtils.join(" ", mSelectedTags);
        mTagsEditText.setText(tagsText);
        mTagsEditText.setSelection(tagsText.length());
        return true;
    }

    private void highlightToolbarButtonOnly(Button button) {
        for (Button btn : new Button[]{mRecommendTagsButton, mAllTagsButton, mShareConfigButton}) {
            if (btn.equals(button)) {
                btn.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                btn.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    private void resetToolbarButtonColor() {
        for (Button btn : new Button[]{mRecommendTagsButton, mAllTagsButton, mShareConfigButton}) {
            btn.setTypeface(Typeface.DEFAULT);
        }
    }

    private void setVisibleControllPanelOnly(View view) {
        for (View v : new View[]{mRecommendTagsArea, mMyTagsArea, mShareConfigView}) {
            if (v.equals(view)) {
                v.setVisibility(View.VISIBLE);
            } else {
                v.setVisibility(View.GONE);
            }
        }
    }
}
