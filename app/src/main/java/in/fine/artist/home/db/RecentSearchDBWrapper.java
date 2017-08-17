package in.fine.artist.home.db;

import android.content.Context;

import java.util.ArrayList;

import in.fine.artist.home.ZApplication;
import in.fine.artist.home.data.SearchQuery;

/**
 * Created by apoorvarora on 27/05/17.
 */
public class RecentSearchDBWrapper {
    private static DBManager dbManager;
    private static Context mContext;

    public static void Initialize(Context context){
        if (context instanceof ZApplication) {
            ZApplication vapp = (ZApplication) context;
            dbManager = vapp.getDbManager();
            mContext = context;
        }
    }

    public static int addSearchQuery(SearchQuery searchQuery) {
        return RecentSearchDBManager.addSearchQuery(dbManager, mContext, searchQuery);
    }

    public static ArrayList<SearchQuery> getSearchQueries() {
        return RecentSearchDBManager.getSearchQueries(dbManager, mContext);
    }
}
