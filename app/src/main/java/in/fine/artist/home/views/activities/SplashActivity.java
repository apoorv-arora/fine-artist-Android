package in.fine.artist.home.views.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.data.NameValuePair;
import in.fine.artist.home.data.User;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.VPrefsReader;
import in.fine.artist.home.utils.facebook.FacebookConnect;
import in.fine.artist.home.utils.facebook.FacebookConnectCallback;
import in.fine.artist.home.utils.networking.UploadManager;
import in.fine.artist.home.utils.networking.UploadManagerCallback;


/**
 * Created by apoorvarora on 21/04/17.
 */
public class SplashActivity extends BaseActivity implements UploadManagerCallback, FacebookConnectCallback {

    // Generic activity stuffs
    private boolean destroyed = false;
    private Activity mActivity;
    private ZApplication vapp;
    private Activity mContext;

    // Play services stuffs
    private final int SPLASH_MAXIMUM_DISPLAY_LENGTH = 3000;
    private int requestCode = 101;
    private VPrefsReader prefs;

    private Timer timer;
    private int description;
    private String APPLICATION_ID;

    private ProgressDialog z_ProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        UploadManager.getInstance().addCallback(this,
                UploadManager.LOGIN);

        mContext = this;
        vapp = (ZApplication) getApplication();
        mActivity = SplashActivity.this;
        prefs = VPrefsReader.getInstance();
        APPLICATION_ID = getResources().getString(R.string.fb_app_id);

        setListeners();
        animateSplash();

        vapp.zll.forced = true;
        startLocationCheck();
        fetchAppConfig();
    }

    private Animation animation1, animation2;

    private void animateSplash() {
        try {
            animation1 = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up_center);
            animation1.setDuration(700);
            animation1.restrictDuration(700);
            animation1.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // should run second animation or not
                    triggerFlow();
                }
            });
            animation1.scaleCurrentDuration(1);
            findViewById(R.id.logo).setAnimation(animation1);
        } catch (Exception e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    private void setListeners() {
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        });

        findViewById(R.id.google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        findViewById(R.id.facebook_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                z_ProgressDialog = ProgressDialog.show(SplashActivity.this, null,
                        getResources().getString(R.string.signingup_wait), true, false);
                z_ProgressDialog.setCancelable(false);
                String regId = prefs.getPref(CommonLib.PROPERTY_REG_ID, "");
                FacebookConnect facebookConnect = new FacebookConnect(SplashActivity.this, 1, APPLICATION_ID, true,
                        regId, "");
                facebookConnect.execute();
            }
        });
    }

    private void showLoginScreen() {
        setTimer();

        animation2 = AnimationUtils.loadAnimation(mActivity, R.anim.slide_in_bottom);
        animation2.setDuration(400);
        animation2.restrictDuration(700);
        animation2.scaleCurrentDuration(1);
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.login_container).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.login_container).setAnimation(animation2);
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        UploadManager.getInstance().removeCallback(this,
                UploadManager.LOGIN);
        super.onDestroy();
    }

    public synchronized void triggerFlow() {
        if (prefs.getPref(CommonLib.PROPERTY_ACCESS_TOKEN, "") != null
                && prefs.getPref(CommonLib.PROPERTY_ACCESS_TOKEN, "").length() > 0) {
            navigateToHome();
        } else {
            if (!checkPlayServices())
                return;
            showLoginScreen();
        }
    }

    private void navigateToHome() {
        // play_service_check is true only for first time
        // if it's true, we have to check whether phone supports google play services or not
        // if not, we do not allow to proceed
        if (checkPlayServices()) {
            Intent intent = new Intent(mActivity, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mActivity.finish();
        }
    }

    private boolean checkPlayServices() {
        if (prefs.getOneTimePref(CommonLib.PROPERTY_PLAY_SERVICES_CHECK, true)) {
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            requestCode = googleAPI.isGooglePlayServicesAvailable(mActivity);
            if (requestCode == ConnectionResult.SUCCESS) {
                prefs.setOneTimePref(CommonLib.PROPERTY_PLAY_SERVICES_CHECK, false);
                return true;
            } else {
                googleAPI.showErrorDialogFragment(mActivity, requestCode, requestCode, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(mActivity, getResources().getString(R.string.update_play_services),Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                return false;
            }
        } else
            return true;
    }

    @Override
    protected void onActivityResult(int reqCode,int resCode,Intent data){
        if (reqCode == requestCode) {
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            int resultCode = googleAPI.isGooglePlayServicesAvailable(mActivity);
            if (resultCode == ConnectionResult.SUCCESS) {
                triggerFlow();
            } else {
                Toast.makeText(mActivity, getResources().getString(R.string.update_play_services),Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            try {
                super.onActivityResult(reqCode, resCode, data);
                Session.getActiveSession().onActivityResult(this, reqCode, resCode, data);

            } catch (Exception w) {

                w.printStackTrace();

                try {
                    Session fbSession = Session.getActiveSession();
                    if (fbSession != null) {
                        fbSession.closeAndClearTokenInformation();
                    }
                    Session.setActiveSession(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {

    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
        if (requestType == UploadManager.LOGIN) {
            if (!destroyed) {
                if (z_ProgressDialog != null && z_ProgressDialog.isShowing())
                    z_ProgressDialog.dismiss();

                if (status && data != null && data instanceof Object[] && ((Object[])data).length > 1) {
                    String token = String.valueOf(((Object[]) data)[1]);

                    List<NameValuePair> prefsList = new ArrayList<>();
                    prefsList.add(new NameValuePair(CommonLib.PROPERTY_ACCESS_TOKEN, token));
                    if (((Object[]) data)[0] != null && ((Object[]) data)[0] instanceof User) {
                        User user = (User) ((Object[]) data)[0];
                        prefsList.add(new NameValuePair(CommonLib.PROPERTY_USER_ID, user.getUserId()));
                        prefsList.add(new NameValuePair(CommonLib.PROPERTY_USER_NAME, user.getName()));
                        prefsList.add(new NameValuePair(CommonLib.PROPERTY_USER_PHONE_NUMBER, user.getPhoneNumber()));
                        prefsList.add(new NameValuePair(CommonLib.PROPERTY_USER_PROFILE_PIC, user.getImageUrl()));
                    }
                    prefs.setPrefs(prefsList);

                    triggerFlow();
                }
            }
        }
    }

    public void startLocationCheck() {
        if (Build.VERSION.SDK_INT < 23) {
            vapp.startLocationCheck();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // add for location callbacks
            vapp.startLocationCheck();
        }
    }

    private void setTimer() {
        findViewById(R.id.text_container).setVisibility(View.VISIBLE);
        description = 1;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!destroyed) {
                    SplashActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!destroyed) {
                                if (description == 3) {
                                    timer.cancel();
                                } else if (description == 2) {
                                    description = 3;
                                    ((TextView) findViewById(R.id.description)).setText(getResources().getString(R.string.introductory_3));
                                } else if (description == 1) {
                                    description = 2;
                                    ((TextView) findViewById(R.id.description)).setText(getResources().getString(R.string.introductory_2));
                                }
                            }
                        }
                    });
                } else {
                    timer.cancel();
                }
            }
        }, 3000, 3000);
    }

    @Override
    public void response(Bundle bundle) {
        int status = bundle.getInt("status");
        String error_exception = "";
        String error_responseCode = "";
        String error_stackTrace = "";
        boolean regIdSent = false;

        if (bundle.containsKey("error_responseCode"))
            error_responseCode = bundle.getString("error_responseCode");

        if (bundle.containsKey("error_exception"))
            error_exception = bundle.getString("error_exception");

        if (bundle.containsKey("error_stackTrace"))
            error_stackTrace = bundle.getString("error_stackTrace");

        if (status == 0) {
            if (!error_exception.equals("") || !error_responseCode.equals("") || !error_stackTrace.equals(""))
                ;

            if (bundle.getString("errorMessage") != null) {
                String errorMessage = bundle.getString("errorMessage");
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, R.string.err_occurred, Toast.LENGTH_SHORT).show();
            }
            if (z_ProgressDialog != null && z_ProgressDialog.isShowing())
                z_ProgressDialog.dismiss();

        } else {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("email", bundle.getString("email"));
            requestMap.put("facebookId", bundle.getString("email"));
            requestMap.put("facebookData", bundle.getString("facebookData"));
            requestMap.put("token", bundle.getString("token"));
            requestMap.put("facebookPermission", bundle.getString("facebookPermission"));
            requestMap.put("userName", bundle.getString("userName"));
            requestMap.put("profile_pic", bundle.getString("profile_pic"));
            requestMap.put("logintype", CommonLib.LOGIN_TYPE_FACEBOOK+"");

            UploadManager.getInstance().apiCall(requestMap, UploadManager.LOGIN, null, null, null);
        }
    }

    private void fetchAppConfig() {
        UploadManager.getInstance().apiCallWithPriority(new HashMap<String, String>(), UploadManager.APP_CONFIG, null, null, UploadManager.REQUEST_PRIORITY_NORMAL, null);
    }

}

