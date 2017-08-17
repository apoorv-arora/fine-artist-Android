package in.fine.artist.home.services;

import com.google.firebase.iid.FirebaseInstanceIdService;

import in.fine.artist.home.ZApplication;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class FBTokenService extends FirebaseInstanceIdService {

    private ZApplication vapp;

    @Override
    public void onCreate() {
        super.onCreate();
        vapp = (ZApplication) getApplication();
    }

    @Override
    public void onTokenRefresh() {
        vapp.onTokenRefresh();
    }
}