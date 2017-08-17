package in.fine.artist.home.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import in.fine.artist.home.utils.CommonLib;

/**
 * Created by apoorvarora on 27/05/17.
 */
public class DBManager extends SQLiteOpenHelper {

    // database version
    private Context context;

    // database version
    private static final int DATABASE_VERSION = 1;

    // DB name
    public static final String DATABASE_NAME = "SUPPLY_DB";

    // table name
    public static final String RECENT_PLACES_TABLE_NAME = "RECENTPLACES";
    public static final String RECENT_SEARCHES_TABLE_NAME = "RECENTSEARCHES";

    // columns
    public static final String MASTER_ID = "masterId";
    public static final String RECENT_PLACE_ID = "place_id";
    public static final String RECENT_PLACE_ADDRESS = "address";
    public static final String RECENT_PLACE_TYPE = "source";

    public static final String TIMESTAMP = "timestamp";
    public static final String SEARCH_SOURCE_PLACE_ID = "place_id";
    public static final String SEARCH_SOURCE__PLACE_ADDRESS = "address";
    public static final String SEARCH_DESTINATION_PLACE_ID = "place_id";
    public static final String SEARCH_DESTINATION__PLACE_ADDRESS = "address";

    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(getRecentPlacesTableCreationQuery());
            db.execSQL(getRecentSearchTableCreationQuery());
        } catch (SQLiteException exception) {
            CommonLib.VLog("DB", exception.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static String getRecentPlacesTableCreationQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS " + RECENT_PLACES_TABLE_NAME + " (")
                .append(MASTER_ID + " INTEGER, ")
                .append(RECENT_PLACE_ID + " VARCHAR, ")
                .append(RECENT_PLACE_ADDRESS + " VARCHAR, ")
                .append(RECENT_PLACE_TYPE + " VARCHAR, ")
                .append(TIMESTAMP + " INTEGER);");
        return stringBuilder.toString();
    }

    public static String getRecentSearchTableCreationQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS " + RECENT_SEARCHES_TABLE_NAME + " (")
                .append(MASTER_ID + " INTEGER, ")
                .append(SEARCH_SOURCE_PLACE_ID + " VARCHAR, ")
                .append(SEARCH_SOURCE__PLACE_ADDRESS + " VARCHAR, ")
                .append(SEARCH_DESTINATION_PLACE_ID + " VARCHAR, ")
                .append(SEARCH_DESTINATION__PLACE_ADDRESS + " VARCHAR, ")
                .append(TIMESTAMP + " INTEGER);");
        return stringBuilder.toString();
    }

    public static String getPath(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            return context.getApplicationInfo().dataDir + "/databases/" + DATABASE_NAME;
        } else {
            return "/data/data/" + context.getPackageName() + "/databases/" + DATABASE_NAME;
        }
    }

    public synchronized SQLiteDatabase getDataBase() {
        getWritableDatabase();
        SQLiteDatabase db = context.openOrCreateDatabase(getPath(context), Context.MODE_PRIVATE, null);
        return db;
    }

}
