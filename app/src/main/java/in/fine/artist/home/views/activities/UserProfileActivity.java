package in.fine.artist.home.views.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.fine.artist.home.BuildConfig;
import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.data.NameValuePair;
import in.fine.artist.home.data.User;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.ImageLoader;
import in.fine.artist.home.utils.VPrefsReader;
import in.fine.artist.home.utils.networking.UploadManager;
import in.fine.artist.home.utils.networking.UploadManagerCallback;
import in.fine.artist.home.utils.views.VCircularImageView;


/**
 * Created by apoorvarora on 13/05/17.
 */
public class UserProfileActivity extends BaseActivity implements UploadManagerCallback {
    // Generic activity stuffs
    private boolean destroyed = false;
    private Activity mActivity;
    private ZApplication vapp;
    private VPrefsReader prefs;
    private int width;
    private int height;

    private ProgressDialog zProgressDialog;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        destroyed = false;
        mActivity = this;
        vapp = (ZApplication) getApplication();
        prefs = VPrefsReader.getInstance();
        height = CommonLib.getWindowHeightWidth(mActivity)[0];
        width = CommonLib.getWindowHeightWidth(mActivity)[1];
        imageLoader = new ImageLoader(this, vapp);

        UploadManager.getInstance().addCallback(this
                , UploadManager.LOGOUT
                , UploadManager.UPDATE_PROFILE );

        setUpActionBar();
        setListeners();

        ((TextView) findViewById(R.id.tv_app_version)).setText("v"+ BuildConfig.VERSION_NAME);

        if (true || prefs.getPref(CommonLib.PROPERTY_USER_ID, 0) > 0) {
            // set user name
            String name = prefs.getPref(CommonLib.PROPERTY_USER_NAME, "");
            if (!name.isEmpty() && !name.equals("null")) {
                ((TextView) findViewById(R.id.name)).setText(name);
                findViewById(R.id.name).setEnabled(false);
            }

            // set user number
            String phone = prefs.getPref(CommonLib.PROPERTY_USER_PHONE_NUMBER, "");
            if (!phone.isEmpty() && !phone.equals("null")) {
                ((TextView) findViewById(R.id.phone)).setText(phone);
            }

            // set user image
            ImageView userImage = (VCircularImageView) findViewById(R.id.userImage);
            if (!TextUtils.isEmpty(prefs.getPref(CommonLib.PROPERTY_USER_PROFILE_PIC, ""))) {
                String url = prefs.getPref(CommonLib.PROPERTY_USER_PROFILE_PIC, "");
                imageLoader.setImageFromUrlOrDisk(url, userImage, "", width, height, true);
            } else {
                userImage.setImageResource(R.drawable.ic_user_black);
            }
        } else {
            findViewById(R.id.phone_container).setVisibility(View.GONE);
            findViewById(R.id.name_container).setVisibility(View.GONE);
            findViewById(R.id.phone_separator_container).setVisibility(View.GONE);
            findViewById(R.id.logoutLayout).setVisibility(View.GONE);
            findViewById(R.id.logout_separator).setVisibility(View.GONE);
            findViewById(R.id.address_separator).setVisibility(View.GONE);
            findViewById(R.id.middle_separator).setVisibility(View.GONE);
            findViewById(R.id.address_layout).setVisibility(View.GONE);
            ((VCircularImageView) findViewById(R.id.userImage)).setImageResource(R.drawable.ic_user_black);
        }
    }

    private void setUpActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.settings));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setListeners() {
        findViewById(R.id.logoutLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zProgressDialog = ProgressDialog.show(mActivity, null, getResources().getString(R.string.logging_out));
                UploadManager.getInstance().apiCall(new HashMap<String, String>(), UploadManager.LOGOUT, null, null, null);
            }
        });

        findViewById(R.id.termsAndConditions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( vapp.getAppConfig() != null) {
                    Intent intent = new Intent(mActivity, VWebView.class);
                    intent.putExtra(CommonLib.INTENT_TITLE, getResources().getString(R.string.terms_conditions_title));
                    intent.putExtra(CommonLib.INTENT_URL, vapp.getAppConfig().getTncUrl());
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.aboutUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( vapp.getAppConfig() != null) {
                    Intent intent = new Intent(mActivity, VWebView.class);
                    intent.putExtra(CommonLib.INTENT_TITLE, getResources().getString(R.string.about_us));
                    intent.putExtra(CommonLib.INTENT_URL, vapp.getAppConfig().getAboutUsUrl());
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.direct_call_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vapp.getAppConfig() != null) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + vapp.getAppConfig().getContactUsNumber()));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }
            }
        });

        findViewById(R.id.edit_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView editNameStr = ((TextView)findViewById(R.id.edit_phone));
                if (editNameStr.getText().toString().equals(getResources().getString(R.string.ic_circular_tick))) {
                    updateName();
                } else if (editNameStr.getText().toString().equals(getResources().getString(R.string.ic_edit))) {
                    findViewById(R.id.phone).setEnabled(true);
                    CommonLib.showSoftKeyboard(mActivity, ((EditText)findViewById(R.id.phone)));
                    ((EditText)findViewById(R.id.phone)).setSelection(((TextView)findViewById(R.id.phone)).getText().toString().length());
                    editNameStr.setTextColor(ContextCompat.getColor(mActivity,R.color.blueButton_pressed));
                    editNameStr.setText(getResources().getString(R.string.ic_circular_tick));
                }
            }
        });

        ((EditText)findViewById(R.id.phone)).setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        // Identifier of the action. This will be either the identifier you supplied,
                        // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                        if (actionId == EditorInfo.IME_ACTION_SEARCH
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || event.getAction() == KeyEvent.ACTION_DOWN
                                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            updateName();
                            return true;
                        }
                        // Return true if you have consumed the action, else false.
                        return false;
                    }
                });
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        if (zProgressDialog != null && zProgressDialog.isShowing())
            zProgressDialog.dismiss();

        UploadManager.getInstance().removeCallback(this
                , UploadManager.LOGOUT
                , UploadManager.UPDATE_PROFILE);

        super.onDestroy();
    }

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {

    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
        if (requestType == UploadManager.LOGOUT) {
            if (zProgressDialog != null && zProgressDialog.isShowing())
                zProgressDialog.dismiss();
            // clear all prefs
            vapp.logout();
            if (!destroyed) {
                // let's start again
                if (prefs.getPref(CommonLib.PROPERTY_USER_ID, 0) == 0) {
                    Intent intent = new Intent(vapp, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    mActivity.finish();
                }
            }
        } else if (requestType == UploadManager.UPDATE_PROFILE) {
            if (!destroyed) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                findViewById(R.id.edit_phone).setVisibility(View.VISIBLE);
                findViewById(R.id.phone).setEnabled(false);
                if (status && data != null && data instanceof Object[] && ((Object[])data).length > 1) {
                    if (((Object[]) data)[0] != null && ((Object[]) data)[0] instanceof User) {
                        User user = (User) ((Object[]) data)[0];
                        List<NameValuePair> prefsList = new ArrayList<>();
                        prefsList.add(new NameValuePair(CommonLib.PROPERTY_USER_PHONE_NUMBER, user.getPhoneNumber()));
                        prefs.setPrefs(prefsList);
                        ((TextView)findViewById(R.id.phone)).setText(user.getPhoneNumber());
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        CommonLib.hideKeyboard(mActivity);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateName() {
        TextView editNameStr = ((TextView)findViewById(R.id.edit_phone));
        CommonLib.hideKeyBoard(mActivity, ((EditText)findViewById(R.id.phone)));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        String text = ((TextView)findViewById(R.id.phone)).getText().toString().trim();

        if (TextUtils.isEmpty(text) || text.length() != 10) {
            Toast.makeText(this, getResources().getString(R.string.empty_number), Toast.LENGTH_SHORT).show();
            return;
        }

        editNameStr.setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        editNameStr.setText(getResources().getString(R.string.ic_edit));
        editNameStr.setTextColor(ContextCompat.getColor(mActivity,R.color.account_icon_color));

        HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("number", text);
        UploadManager.getInstance().apiCall(paramsMap, UploadManager.UPDATE_PROFILE, null, null, null);
    }
}