package in.fine.artist.home;

import android.app.ActivityManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import in.fine.artist.home.data.AppConfig;
import in.fine.artist.home.data.NameValuePair;
import in.fine.artist.home.db.DBManager;
import in.fine.artist.home.db.RecentPlaceDBWrapper;
import in.fine.artist.home.db.RecentSearchDBWrapper;
import in.fine.artist.home.receivers.AppConfigReceiver;
import in.fine.artist.home.services.AppConfigService;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.ParserJson;
import in.fine.artist.home.utils.VPrefsReader;
import in.fine.artist.home.utils.cache.LruCache;
import in.fine.artist.home.utils.location.ZLocationListener;
import in.fine.artist.home.utils.networking.UploadManager;
import in.fine.artist.home.utils.networking.UploadManagerCallback;
import io.fabric.sdk.android.Fabric;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class ZApplication extends Application implements UploadManagerCallback {

    public LruCache<String, Bitmap> cache;
    private VPrefsReader prefs;

    // location stuffs
    public ZLocationListener zll = new ZLocationListener(this);
    public LocationManager locationManager = null;
    public boolean isNetworkProviderEnabled = false;
    public boolean isGpsProviderEnabled = false;
    private CheckLocationTimeoutAsync checkLocationTimeoutThread;
    public double lat = 0;
    public double lon = 0;
    public boolean isLocationRequested = false;
    private DBManager dbManager;

    private AppConfig appConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        // init our piggi bank
        prefs = VPrefsReader.getInstance();
        prefs.setContext(getApplicationContext());

        // init our stalker
        UploadManager.getInstance().setContext(this);

        // add callbacks
        UploadManager.getInstance().addCallback(this
                , UploadManager.APP_CONFIG);

        // init our insta
        cache = new LruCache<String, Bitmap>(30);

        // init pin
        lat = Double.parseDouble(prefs.getPref(CommonLib.PLACE_LAT, "0"));
        lon = Double.parseDouble(prefs.getPref(CommonLib.PLACE_LON, "0"));

        if (prefs.getOneTimePref("version", 0) < BuildConfig.VERSION_CODE) {
            prefs.setOneTimePref("version", BuildConfig.VERSION_CODE);

            try {
                savePrefs();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        dbManager = new DBManager(this);
        RecentSearchDBWrapper.Initialize(this);
        RecentPlaceDBWrapper.Initialize(this);

        startAppConfigReceiver();
//        new ThirdPartyInitAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onTerminate() {
        UploadManager.getInstance().removeCallback(this
                , UploadManager.APP_CONFIG);
        super.onTerminate();
    }

    private class ThirdPartyInitAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                Fabric.with(getApplicationContext(), new Crashlytics());
            } catch (Exception e) {
                if (!CommonLib.VYOMLOG)
                    Crashlytics.logException(e);
                e.printStackTrace();
            }
            return null;
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private class DeleteTokenAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (Exception e) {
                if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onTokenRefresh();
        }
    }

    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        CommonLib.VLog("Token", "Refreshed token: " + refreshedToken);
        prefs.setPref(CommonLib.PROPERTY_REG_ID, refreshedToken);
        if (prefs.getPref(CommonLib.PROPERTY_USER_ID, 0) > 0)
            registerInBackground(refreshedToken);
    }

    public void registerInBackground(String regId) {
        if (regId != null && !regId.isEmpty()) {
            // Getting registration token
            String requestUrl = CommonLib.SERVER_URL + "user/notification";
            JSONObject requestJson = new JSONObject();
            try {
                requestJson.put("registrationId", regId);
            } catch (JSONException e) {
                if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                e.printStackTrace();
            }
            if (CommonLib.isNetworkAvailable(getApplicationContext())) {
//                UploadManager.getInstance().apiCallWithPriority(UploadManager.FCM_REGISTER, "", requestJson, null, UploadManager.REQUEST_PRIORITY_NORMAL);
            }
        }
    }

    @Override
    public void onLowMemory() {
        cache.clear();
        super.onLowMemory();
    }

    public void onTrimLevel(int i) {
        cache.clear();
        super.onTrimMemory(i);
    }

    public void logout() {
        prefs.clearPrefs();
        startAppConfigReceiver();

        try {
            new DeleteTokenAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (RejectedExecutionException exception) {
            if (!CommonLib.VYOMLOG)
                Crashlytics.logException(exception);

            CommonLib.VLog(this.getClass().getSimpleName(), exception.getMessage());
        }
    }

    public void savePrefs() {

        // save prefs
        String var1 = prefs.getPref(CommonLib.PROPERTY_ACCESS_TOKEN, "");
        int var3 = prefs.getPref(CommonLib.PROPERTY_USER_ID, 0);
        String var4 = prefs.getPref(CommonLib.PROPERTY_USER_NAME, "");
        String var5 = prefs.getPref(CommonLib.PROPERTY_USER_PHONE_NUMBER, "");
        String var8 = prefs.getPref(CommonLib.PROPERTY_USER_PROFILE_PIC, "");
        String var17 = prefs.getPref(CommonLib.PROPERTY_REG_ID, "");

        // clear prefs
        prefs.clearPrefs();

        // update prefs
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair(CommonLib.PROPERTY_ACCESS_TOKEN, var1));
        pairs.add(new NameValuePair(CommonLib.PROPERTY_USER_ID, var3));
        pairs.add(new NameValuePair(CommonLib.PROPERTY_USER_PHONE_NUMBER,var5));
        pairs.add(new NameValuePair(CommonLib.PROPERTY_USER_PROFILE_PIC, var8));
        pairs.add(new NameValuePair(CommonLib.PROPERTY_REG_ID, var17));

        prefs.setPrefs(pairs);
    }

    @Override
    public void uploadStarted(int requestType, Object data, Object requestData) {

    }

    @Override
    public void uploadFinished(int requestType, Object data, boolean status, String errorMessage, Object requestData) {
        if (requestType == UploadManager.APP_CONFIG) {
            if (status && data instanceof Object[]) {
                AppConfig config = (AppConfig) ((Object[])data)[0];
                setAppConfig(config);
                prefs.setPref(CommonLib.PROPERTY_APP_CONFIG, (String) ((Object[])data)[1]);
            }
        }
    }

    public void updateLocation(Place placeObject) {
        lat = placeObject.getLatLng().latitude;
        lon = placeObject.getLatLng().longitude;
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new NameValuePair(CommonLib.PLACE_LAT, String.valueOf(lat)));
        pairs.add(new NameValuePair(CommonLib.PLACE_LON, String.valueOf(lon)));
        prefs.setPrefs(pairs);
    }

    public void interruptLocationTimeout() {
        // checkLocationTimeoutThread.interrupt();
        if (checkLocationTimeoutThread != null)
            checkLocationTimeoutThread.interrupt = false;
    }

    public void startLocationCheck() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (result == ConnectionResult.SUCCESS) {
            zll.getFusedLocation(this);
        } else {
            getAndroidLocation();
        }
    }

    public boolean isLocationAvailable() {
        return (isNetworkProviderEnabled || isGpsProviderEnabled);
    }

    public void getAndroidLocation() {

        CommonLib.VLog("zll", "getAndroidLocation");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        if (providers != null) {
            for (String providerName : providers) {
                if (providerName.equals(LocationManager.GPS_PROVIDER))
                    isGpsProviderEnabled = true;
                if (providerName.equals(LocationManager.NETWORK_PROVIDER))
                    isNetworkProviderEnabled = true;
            }
        }

        if (isNetworkProviderEnabled || isGpsProviderEnabled) {

            if (isGpsProviderEnabled)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, zll);
            if (isNetworkProviderEnabled)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, zll);

            if (checkLocationTimeoutThread != null) {
                checkLocationTimeoutThread.interrupt = false;
            }

            checkLocationTimeoutThread = new CheckLocationTimeoutAsync();
            checkLocationTimeoutThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else {
            zll.locationNotEnabled();
        }
    }

    private class CheckLocationTimeoutAsync extends AsyncTask<Void, Void, Void> {
        boolean interrupt = true;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            if (interrupt) {
                zll.interruptProcess();
            }
        }
    }

    public AppConfig getAppConfig() {
        if (appConfig != null) {
        } else if (!prefs.getPref(CommonLib.PROPERTY_APP_CONFIG, "").isEmpty()) {
            try {
                JSONObject appConfigJson = new JSONObject(prefs.getPref(CommonLib.PROPERTY_APP_CONFIG, ""));
                setAppConfig(ParserJson.parse_AppConfig(appConfigJson));
            } catch (JSONException e) {
                if (!CommonLib.VYOMLOG)
                    Crashlytics.logException(e);
                e.printStackTrace();
            }
        } else {
            // read from file
            InputStream inputStream;
            File file = null;
            try {
                inputStream = getAssets().open(CommonLib.APP_CONFIG);
                file = CommonLib.createFileFromInputStream(inputStream, CommonLib.APP_CONFIG);
            } catch (IOException e) {
                if (!CommonLib.VYOMLOG)
                    Crashlytics.logException(e);
                e.printStackTrace();
            }

            if (file != null) {
                //Read text from file
                StringBuilder text = new StringBuilder();

                FileReader fileReader = null;
                try {
                    fileReader = new FileReader(file);
                    BufferedReader br = new BufferedReader(fileReader);
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                    }

                    br.close();
                    if (fileReader != null) {
                        fileReader.close();
                    }

                } catch (IOException e) {
                    if (!CommonLib.VYOMLOG)
                        Crashlytics.logException(e);
                    e.printStackTrace();
                }

                try {
                    setAppConfig(ParserJson.parse_AppConfig(new JSONObject(text.toString())));
                } catch (JSONException e) {
                    if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                    e.printStackTrace();
                }
            }
        }
        return appConfig;
    }

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void startAppConfigReceiver() {
        Intent intent = new Intent(this, AppConfigService.class);
        PendingIntent alarmUp = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
        if (alarmUp == null) {
            AppConfigReceiver pollingReceiver = new AppConfigReceiver();
            pollingReceiver.cancelAlarm(this);
            pollingReceiver.setAlarm(this, CommonLib.THREE_HOUR_TIMER);
        } else {
            try {
                if (!isMyServiceRunning(AppConfigService.class)) {
                    startService(intent);
                }
            } catch (Exception e) {
                if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
    }

    public DBManager getDbManager() {
        return dbManager;
    }

}
