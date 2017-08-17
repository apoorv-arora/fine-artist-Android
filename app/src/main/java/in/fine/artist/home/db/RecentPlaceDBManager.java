package in.fine.artist.home.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.fine.artist.home.data.PlaceExtended;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.VPrefsReader;

/**
 * Created by apoorvarora on 27/05/17.
 */
public class RecentPlaceDBManager {

    public static int addRecentPlace(DBManager dbManager, Context mContext, PlaceExtended placeExtended) {
        int result = -1;
        try {
            SQLiteDatabase db = dbManager.getDataBase();
            Cursor cursor = null;
            VPrefsReader prefs = VPrefsReader.getInstance();

            ContentValues values = new ContentValues();
            values.put(DBManager.TIMESTAMP, System.currentTimeMillis() / 1000);

            cursor = db.query(DBManager.RECENT_PLACES_TABLE_NAME, null, DBManager.RECENT_PLACE_ID + " =? AND " + DBManager.RECENT_PLACE_TYPE + " =? AND " + DBManager.MASTER_ID + "=?", new String[]{placeExtended.getPlaceId(), placeExtended.getPlaceType()+"", Integer.toString(prefs.getPref(CommonLib.PROPERTY_USER_ID, 0))}, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                result = db.update(DBManager.RECENT_PLACES_TABLE_NAME, values, DBManager.RECENT_PLACE_ID + " =? AND " + DBManager.RECENT_PLACE_TYPE + " =? AND " + DBManager.MASTER_ID + "=?",
                        new String[]{placeExtended.getPlaceId(), placeExtended.getPlaceType()+"", Integer.toString(prefs.getPref(CommonLib.PROPERTY_USER_ID, 0))});
            } else {
                values.put(DBManager.MASTER_ID, prefs.getPref(CommonLib.PROPERTY_USER_ID, 0));
                values.put(DBManager.RECENT_PLACE_ID, placeExtended.getPlaceId());
                values.put(DBManager.RECENT_PLACE_ADDRESS, placeExtended.getAddress());
                values.put(DBManager.RECENT_PLACE_TYPE, placeExtended.getPlaceType());
                result = (int) db.insertOrThrow(DBManager.RECENT_PLACES_TABLE_NAME, null, values);
            }

            if (cursor != null) {
                cursor.close();
            }
            db.close();
            dbManager.close();
        } catch (Exception e) {
            try {
                dbManager.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            result = -1;
        }
        return result;
    }

    public static ArrayList<PlaceExtended> getRecentPlaces(DBManager dbManager, Context mContext) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<PlaceExtended> queries = new ArrayList<>();

        try {
            db = dbManager.getDataBase();
            VPrefsReader prefs = VPrefsReader.getInstance();

            cursor = db.query(DBManager.RECENT_PLACES_TABLE_NAME, null,
                    DBManager.MASTER_ID + "=?", new String[]{Integer.toString(prefs.getPref(CommonLib.PROPERTY_USER_ID, 0))}, null, null, DBManager.TIMESTAMP + " DESC", "7"); // no limit initially

            if (cursor != null)
                cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                PlaceExtended recentPlace = new PlaceExtended();

                recentPlace.setPlaceId(cursor.getString(cursor.getColumnIndex(DBManager.RECENT_PLACE_ID)));
                recentPlace.setPlaceType(cursor.getInt(cursor.getColumnIndex(DBManager.RECENT_PLACE_TYPE)));
                recentPlace.setAddress(cursor.getString(cursor.getColumnIndex(DBManager.RECENT_PLACE_ADDRESS)));

                queries.add(recentPlace);
            }

            cursor.close();
            db.close();
            dbManager.close();
        } catch (Exception E) {
            try {
                dbManager.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return queries;
    }
}
