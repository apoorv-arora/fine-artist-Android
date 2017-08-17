package in.fine.artist.home.db;

import android.content.Context;

import java.util.List;

import in.fine.artist.home.ZApplication;
import in.fine.artist.home.data.PlaceExtended;

/**
 * Created by apoorvarora on 27/05/17.
 */
public class RecentPlaceDBWrapper {

    private static DBManager dbManager;
    private static Context mContext;

    public static void Initialize(Context context){
        if (context instanceof ZApplication) {
            ZApplication vapp = (ZApplication) context;
            dbManager = vapp.getDbManager();
            mContext = context;
        }
    }

    public static List<PlaceExtended> getRecentPlaces(Context mContext){
        return RecentPlaceDBManager.getRecentPlaces(dbManager,mContext);
    }

    public static int addPlace(PlaceExtended place) {
        return RecentPlaceDBManager.addRecentPlace(dbManager, mContext, place);
    }

}
