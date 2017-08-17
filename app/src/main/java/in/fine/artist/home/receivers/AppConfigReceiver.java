package in.fine.artist.home.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import in.fine.artist.home.ZApplication;
import in.fine.artist.home.services.AppConfigService;
import in.fine.artist.home.utils.CommonLib;

/**
 * Created by apoorvarora on 25/05/17.
 */
public class AppConfigReceiver extends WakefulBroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        ZApplication vapp = (ZApplication) context.getApplicationContext();
        CommonLib.VLog("appConfig receiver", System.currentTimeMillis()+"");
        if(!vapp.isMyServiceRunning(AppConfigService.class)) {
            Intent service = new Intent(context, AppConfigService.class);
            startWakefulService(context, service);
        }
    }

    public void setAlarm(Context context, long syncTime) {
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AppConfigReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                syncTime, syncTime, alarmIntent);
    }

    public void cancelAlarm(Context context) {
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}