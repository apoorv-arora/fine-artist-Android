package in.fine.artist.home.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import in.fine.artist.home.data.SearchQuery;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.VPrefsReader;

/**
 * Created by apoorvarora on 27/05/17.
 */
public class RecentSearchDBManager {

    public static int addSearchQuery(DBManager dbManager, Context mContext, SearchQuery searchQuery) {
        int result = -1;
        try {
            SQLiteDatabase db = dbManager.getDataBase();
            Cursor cursor = null;
            VPrefsReader prefs = VPrefsReader.getInstance();

            ContentValues values = new ContentValues();
            values.put(DBManager.TIMESTAMP, System.currentTimeMillis() / 1000);

            cursor = db.query(DBManager.RECENT_SEARCHES_TABLE_NAME, null, DBManager.SEARCH_SOURCE_PLACE_ID + " =? AND " + DBManager.SEARCH_DESTINATION_PLACE_ID + " =? AND " + DBManager.MASTER_ID + "=?", new String[]{searchQuery.getSourcePlaceId(), searchQuery.getDestinationPlaceId(), Integer.toString(prefs.getPref(CommonLib.PROPERTY_USER_ID, 0))}, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                result = db.update(DBManager.RECENT_SEARCHES_TABLE_NAME, values, DBManager.SEARCH_SOURCE_PLACE_ID + " =? AND " + DBManager.SEARCH_DESTINATION_PLACE_ID + " =? AND " + DBManager.MASTER_ID + "=?",
                        new String[]{searchQuery.getSourcePlaceId(), searchQuery.getDestinationPlaceId(), Integer.toString(prefs.getPref(CommonLib.PROPERTY_USER_ID, 0))});
            } else {
                values.put(DBManager.MASTER_ID, prefs.getPref(CommonLib.PROPERTY_USER_ID, 0));
                values.put(DBManager.SEARCH_SOURCE_PLACE_ID, searchQuery.getSourcePlaceId());
                values.put(DBManager.SEARCH_SOURCE__PLACE_ADDRESS, searchQuery.getSourceString());
                values.put(DBManager.SEARCH_DESTINATION_PLACE_ID, searchQuery.getDestinationPlaceId());
                values.put(DBManager.SEARCH_DESTINATION__PLACE_ADDRESS, searchQuery.getDestinationString());
                result = (int) db.insertOrThrow(DBManager.RECENT_SEARCHES_TABLE_NAME, null, values);
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

    public static ArrayList<SearchQuery> getSearchQueries(DBManager dbManager, Context mContext) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<SearchQuery> queries = new ArrayList<>();

        try {
            db = dbManager.getDataBase();
            VPrefsReader prefs = VPrefsReader.getInstance();

            cursor = db.query(DBManager.RECENT_SEARCHES_TABLE_NAME, null,
                    DBManager.MASTER_ID + "=?", new String[]{Integer.toString(prefs.getPref(CommonLib.PROPERTY_USER_ID, 0))}, null, null, DBManager.TIMESTAMP + " DESC", "7"); // no limit initially

            if (cursor != null)
                cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                SearchQuery searchQuery = new SearchQuery();

                searchQuery.setSourcePlaceId(cursor.getString(cursor.getColumnIndex(DBManager.SEARCH_SOURCE_PLACE_ID)));
                searchQuery.setSourceString(cursor.getString(cursor.getColumnIndex(DBManager.SEARCH_SOURCE__PLACE_ADDRESS)));
                searchQuery.setDestinationPlaceId(cursor.getString(cursor.getColumnIndex(DBManager.SEARCH_DESTINATION_PLACE_ID)));
                searchQuery.setDestinationString(cursor.getString(cursor.getColumnIndex(DBManager.SEARCH_DESTINATION__PLACE_ADDRESS)));

                queries.add(searchQuery);
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
