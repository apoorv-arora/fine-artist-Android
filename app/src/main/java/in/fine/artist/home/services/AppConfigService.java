package in.fine.artist.home.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import in.fine.artist.home.ZApplication;
import in.fine.artist.home.receivers.AppConfigReceiver;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.networking.UploadManager;

/**
 * Created by apoorvarora on 25/05/17.
 */
public class AppConfigService extends IntentService {

    private Context context;
    private SharedPreferences prefs;
    private ZApplication vapp;

    public AppConfigService() {
        super("AppConfigService");
        context = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        vapp = (ZApplication) getApplication();
        prefs = getSharedPreferences(CommonLib.APP_SETTINGS, 0);
        if (CommonLib.isNetworkAvailable(getApplicationContext())) {
            CommonLib.VLog("AppConfigService", "call started");
            UploadManager.getInstance().apiCallWithPriority(new HashMap<String, String>(), UploadManager.APP_CONFIG, null, null, UploadManager.REQUEST_PRIORITY_NORMAL, null);
        }
        AppConfigReceiver.completeWakefulIntent(intent);
        stopForeground(true);
    }
}
