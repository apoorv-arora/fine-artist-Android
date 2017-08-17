package in.fine.artist.home.services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import in.fine.artist.home.ZApplication;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.NotificationManager;

/**
 * Created by apoorvarora on 21/04/17.
 */
public class FBNotificationService  extends FirebaseMessagingService {

    private ZApplication vapp;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        CommonLib.VLog("Notification", "Received notification ");
        NotificationManager.getInstance(this).notifyRemoteMessage(remoteMessage);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        vapp = (ZApplication) getApplication();
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        CommonLib.VLog("Notification Message", "Message sent: " + s);
    }

}