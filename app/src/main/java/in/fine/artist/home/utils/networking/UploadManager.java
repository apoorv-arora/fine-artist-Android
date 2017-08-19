package in.fine.artist.home.utils.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.fine.artist.home.BuildConfig;
import in.fine.artist.home.R;
import in.fine.artist.home.ZApplication;
import in.fine.artist.home.utils.CommonLib;
import in.fine.artist.home.utils.ParserJson;

/**
 * Created by apoorvarora on 06/10/16.
 */
public class UploadManager {

    private Context context;
    private SharedPreferences prefs, oneTimePrefs;
    private HashMap<Integer, ArrayList<UploadManagerCallback>> callbacks = new HashMap<Integer, ArrayList<UploadManagerCallback>>();
    private static volatile UploadManager sInstance;
    private ZApplication zapp;
    private static final int THREAD_POOL_LENGTH = 10;

    // Request tags
    public static final int LOGIN = 101;
    public static final int LOGOUT = 102;
    public static final int APP_CONFIG = 103;
    public static final int COURSES_RECOMMENDED = 104;
    public static final int COURSES_USER = 105;
    public static final int UPDATES = 106;
    public static final int COURSES_CATEGORIES = 107;
    public static final int COURSES_CATEGORY_ID = 108;
    public static final int COURSES_SEARCH = 109;

    public static final int UPDATE_ADDRESS = 108;
    public static final int UPDATE_PROFILE = 111;
    public static final int SEARCH_PRICES = 112;


    public static final int PLACE_AUTOCOMPLETE_START = 10001;
    public static final int PLACE_AUTOCOMPLETE_DROP = 10002;
    public static final int AUTOCOMPLETE_REQUEST = 1003;
    private static String URL_AUTOCOMPLETE_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?type=(regions)&components=country:in";

    // Event tags
    public static final int SMS_RECEIVED = 501;

    // Request urls
    private static final String URL_LOGIN = CommonLib.SERVER_URL + "auth/consumer/login";
    private static final String URL_LOGOUT = CommonLib.SERVER_URL + "auth/consumer/logout";
    private static final String URL_UPDATE_PROFILE = CommonLib.SERVER_URL + "consumer/profile/update";
    private static final String URL_APP_CONFIG = CommonLib.SERVER_URL + "consumer/util/config";
    private static final String URL_SEARCH_PRICES = CommonLib.SERVER_URL + "consumer/prices/search";
    private static final String URL_COURSES_RECOMMENDED = CommonLib.SERVER_URL + "course/recommended/all";
    private static final String URL_COURSES_USER = CommonLib.SERVER_URL + "consumer/courses/mine";
    private static final String URL_UPDATES = CommonLib.SERVER_URL + "blog/updates/all";
    private static final String URL_COURSES_CATEGORIES = CommonLib.SERVER_URL + "course/category/all";
    private static final String URL_COURSES_CATEGORY_ID = CommonLib.SERVER_URL + "course/category/courses";
    private static final String URL_COURSES_SEARCH = CommonLib.SERVER_URL + "course/category/search";

    // priorities, cause you must know to say NO; and NO means NO
    public static final Request.Priority REQUEST_PRIORITY_LOW = Request.Priority.LOW;
    public static final Request.Priority REQUEST_PRIORITY_NORMAL = Request.Priority.NORMAL;
    public static final Request.Priority REQUEST_PRIORITY_HIGH = Request.Priority.HIGH;
    public static final Request.Priority REQUEST_PRIORITY_IMMEDIATE = Request.Priority.IMMEDIATE;

    // request type
    public static final int REQUEST_TYPE_GET = Request.Method.GET;
    public static final int REQUEST_TYPE_PUT = Request.Method.PUT;
    public static final int REQUEST_TYPE_POST = Request.Method.POST;
    public static final int REQUEST_TYPE_DELETE = Request.Method.DELETE;

    // Volley stuffs
    private RequestQueue mRequestQueue;
    private static Map<Integer,RequestInfo> requestMap = new HashMap<>();

    public static class RequestInfo {
        String url;
        int networkRequestType;

        public RequestInfo(String url, int networkRequestType){
            this.url = url;
            this.networkRequestType = networkRequestType;
        }
    }

    static {
        requestMap.put(UploadManager.LOGIN, new RequestInfo(URL_LOGIN, REQUEST_TYPE_POST));
        requestMap.put(UploadManager.LOGOUT, new RequestInfo(URL_LOGOUT, REQUEST_TYPE_POST));
        requestMap.put(UploadManager.PLACE_AUTOCOMPLETE_START, new RequestInfo(URL_AUTOCOMPLETE_BASE_URL, REQUEST_TYPE_GET));
        requestMap.put(UploadManager.PLACE_AUTOCOMPLETE_DROP, new RequestInfo(URL_AUTOCOMPLETE_BASE_URL, REQUEST_TYPE_GET));
        requestMap.put(UploadManager.UPDATE_PROFILE, new RequestInfo(URL_UPDATE_PROFILE, REQUEST_TYPE_POST));
        requestMap.put(UploadManager.APP_CONFIG, new RequestInfo(URL_APP_CONFIG, REQUEST_TYPE_POST));
        requestMap.put(UploadManager.SEARCH_PRICES, new RequestInfo(URL_SEARCH_PRICES, REQUEST_TYPE_POST));

        requestMap.put(UploadManager.COURSES_RECOMMENDED, new RequestInfo(URL_COURSES_RECOMMENDED, REQUEST_TYPE_POST));
        requestMap.put(UploadManager.COURSES_USER, new RequestInfo(URL_COURSES_USER, REQUEST_TYPE_POST));
        requestMap.put(UploadManager.UPDATES, new RequestInfo(URL_UPDATES, REQUEST_TYPE_POST));

        requestMap.put(UploadManager.COURSES_CATEGORIES, new RequestInfo(URL_COURSES_CATEGORIES, REQUEST_TYPE_POST));
        requestMap.put(UploadManager.COURSES_CATEGORY_ID, new RequestInfo(URL_COURSES_CATEGORY_ID, REQUEST_TYPE_POST));
        requestMap.put(UploadManager.COURSES_SEARCH, new RequestInfo(URL_COURSES_SEARCH, REQUEST_TYPE_POST));
    }

    public static UploadManager getInstance() {
        if (sInstance == null) {
            synchronized (UploadManager.class) {
                if (sInstance == null) {
                    sInstance = new UploadManager();
                }
            }
        }
        return sInstance;
    }

    public void setContext(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(CommonLib.APP_SETTINGS, 0);
        oneTimePrefs = context.getSharedPreferences(CommonLib.ONE_LAUNCH_SETTINGS, 0);
        if (context instanceof ZApplication) {
            zapp = (ZApplication) context;
        }
        mRequestQueue = Volley.newRequestQueue(zapp);
    }

    public void addCallback(UploadManagerCallback callback, Integer... requestTypes) {
        for (Integer requestType : requestTypes) {
            if (!callbacks.containsKey(requestType)) {
                ArrayList<UploadManagerCallback> tempList = new ArrayList<>();
                tempList.add(callback);
                callbacks.put(requestType, tempList);
            }

            if (!callbacks.get(requestType).contains(callback)) {
                callbacks.get(requestType).add(callback);
            }
        }

        // this is here because its called from a lot of places.
        if ((double) Debug.getNativeHeapAllocatedSize() / Runtime.getRuntime().maxMemory() > .70) {
            if (zapp != null) {

                if (zapp.cache != null)
                    zapp.cache.clear();
            }
        }
    }

    public void removeCallback(UploadManagerCallback callback, Integer... requestTypes) {
        for (Integer requestType : requestTypes) {
            if (callbacks.containsKey(requestType) && callbacks.get(requestType).contains(callback)) {
                callbacks.get(requestType).remove(callback);
            }
        }
    }

    public void apiCallWithPriority(Map<String, String> paramsMap, int requestType, String requestParams, Object internalRequestObject, Request.Priority priority, Object... urlFormatters) {
        handleApiCall(paramsMap, requestType, requestParams, internalRequestObject, priority, urlFormatters);
    }

    public void apiCall(Map<String, String> paramsMap, int requestType, String requestParams, Object internalRequestObject, Object... urlFormatters) {
        handleApiCall(paramsMap, requestType, requestParams, internalRequestObject, Request.Priority.HIGH, urlFormatters);
    }

    private void handleApiCall(Map<String, String> paramsMap, int requestType, String requestParams, Object internalRequestObject, Request.Priority priority, Object... urlFormatters) {
        RequestInfo requestInfo = requestMap.get(requestType);
        String requestUrl = requestInfo.url;

        if (urlFormatters != null && urlFormatters.length > 0) {
            requestUrl = String.format(requestUrl, urlFormatters);
        }

        if (!TextUtils.isEmpty(requestParams)){
            requestUrl = requestUrl.concat(requestParams);
        }

        int type = requestInfo.networkRequestType;

        if (callbacks.get(requestType) != null) {
            for (UploadManagerCallback callback : callbacks.get(requestType)) {
                callback.uploadStarted(requestType, requestUrl, internalRequestObject);
            }
        }

        Request<?> volleyRequestObject = null;
        if (paramsMap instanceof Map && paramsMap != null)
            volleyRequestObject = getStringObjectRequest(paramsMap, type, requestUrl, requestType, priority, internalRequestObject);
        else if (paramsMap == null)
            volleyRequestObject = getStringObjectRequest(new HashMap<String, String>(), type, requestUrl, requestType, priority, internalRequestObject);

        mRequestQueue.add(volleyRequestObject);
    }

    public void cancelAllRequests(Object tag){
        mRequestQueue.cancelAll(tag);
    }

    private StringRequest getStringObjectRequest(final Map<String, String> paramsMap, int type, final String url, final int requestType, final Request.Priority priority, final Object reqObject) {
        CommonLib.VLog("Volley", "URL : " + url);
        CommonLib.VLog("Volley", "request object : " + paramsMap.toString());
        final long time = System.currentTimeMillis();

        StringRequest stringRequest = new StringRequest(type, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                handleResponse(response, requestType, url, time, reqObject);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error, requestType, url, time, reqObject);
            }
        }) {
            @Override
            public Request.Priority getPriority() {
                return priority;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = UploadManager.getInstance().getHeaders();
                // remove the following header printing after debugging is done
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " <<<<<</>>>>>> " + entry.getValue());
                }
                return UploadManager.getInstance().getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (!paramsMap.containsKey("accessToken")) {
                    paramsMap.put("accessToken", prefs.getString(CommonLib.PROPERTY_ACCESS_TOKEN, ""));
                }

                return paramsMap;
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(CommonLib.CONNECTION_TIMEOUT),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        stringRequest.setTag(type);
        return stringRequest;
    }

    private VolleyMultipartRequest getMultiPartRequest(int type, final String url, final byte[] jsonObject, final int requestType, final Request.Priority priority, final Object reqObject) {
        CommonLib.VLog("Volley", "URL : " + url);
        final long time = System.currentTimeMillis();
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                handleResponse(new String(response.data), requestType, url, time, reqObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error, requestType, url, time, reqObject);
            }
        }) {
            @Override
            public Request.Priority getPriority() {
                return priority;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return UploadManager.getInstance().getHeaders();
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart("file_cover.jpg", jsonObject, "image/jpeg"));
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(CommonLib.MULTIPART_TIMEOUT),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return multipartRequest;
    }

    public void sendEvent(int eventType, Object data, boolean status, Object reqObject) {
        if (callbacks.containsKey(eventType)) {
            for (UploadManagerCallback callback : callbacks.get(eventType)) {
                callback.uploadFinished(eventType, data, status, "", reqObject);
            }
        }
    }

    public void getPlacesFromApi(int reqType , String input, int tag, String apiKey) {
        String inputFormatted = CommonLib.encodeURL(input);

        RequestInfo requestInfo = requestMap.get(reqType);
        String requestUrl = requestInfo.url;

        requestUrl = requestUrl + "&input="+inputFormatted+"&key=" + apiKey;

        int type = requestInfo.networkRequestType;

        if (callbacks.get(reqType) != null) {
            for (UploadManagerCallback callback : callbacks.get(reqType)) {
                callback.uploadStarted(reqType, requestUrl, null);
            }
        }

        Request<?> volleyRequestObject = getStringObjectRequest(new HashMap<String, String>(), type, requestUrl, reqType, Request.Priority.HIGH, tag);

        mRequestQueue.add(volleyRequestObject);
    }

    public Map<String, String> getHeaders() {
        Map<String, String>  params = new HashMap<String, String>();
        params.put(CommonLib.HEADER_KEY_TOKEN, prefs.getString(CommonLib.PROPERTY_ACCESS_TOKEN, ""));
        params.put(CommonLib.HEADER_KEY_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
        params.put(CommonLib.HEADER_KEY_ACCEPT_FORMAT, "application/json");
        params.put(CommonLib.HEADER_KEY_SOURCE, "APP");
        params.put(CommonLib.HEADER_KEY_ENCODING, "application/gzip");
        params.put(CommonLib.HEADER_KEY_AP, "fos");
        params.put("pn", prefs.getString(CommonLib.PROPERTY_USER_PHONE_NUMBER, ""));
        return params;
    }

    public Map<String, String> getFeatureListHeaders() {
        Map<String, String>  params = new HashMap<String, String>();
        params.put(CommonLib.HEADER_KEY_TOKEN, prefs.getString(CommonLib.PROPERTY_ACCESS_TOKEN, ""));
        params.put(CommonLib.HEADER_KEY_USERID, String.valueOf(prefs.getInt(CommonLib.PROPERTY_USER_ID, 0)));
        params.put(CommonLib.HEADER_KEY_VERSION, String.valueOf(BuildConfig.VERSION_CODE));
        params.put(CommonLib.HEADER_KEY_SOURCE, "APP");
        return params;
    }

    private void handleResponse (String response, int requestType, String url, long time, Object reqObject) {
        CommonLib.VLog("Volley", "Response : " + response.toString());

        Object result[] = new Object[]{"", false, ""};

        try {
            result = ParserJson.parseData(requestType, response.toString());
        } catch (Exception e) {
            if (!CommonLib.VYOMLOG) Crashlytics.logException(e);
            e.printStackTrace();
        }

        if (result != null && result.length == 3 && !(Boolean) result[1]) {
            if (CommonLib.isNetworkAvailable(context)) {
                if (!(result[2].equals("")))
                    Toast.makeText(context, (String) result[2], Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show();
            }
        }



        if (callbacks.get(requestType) != null) {
            for (UploadManagerCallback callback : callbacks.get(requestType)) {
                callback.uploadFinished(requestType, result[0], (boolean) result[1], String.valueOf(result[2]), reqObject);
            }
        }

//        makeMixPanelCall(url, System.currentTimeMillis() - time);
    }

    private void handleError(VolleyError error, int requestType, String url, long time, Object reqObject) {
        CommonLib.VLog("Volley", "Error : " + error.toString());
        if (error.networkResponse != null) {
            int responseCode = error.networkResponse.statusCode;
//            if (responseCode == CommonLib.HTTP_DUDE_INVALID_TOKEN) {
//                zapp.logout();
//                Intent intent = new Intent(context, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//                return;
//            }
        }
        if (!CommonLib.isNetworkAvailable(context)) {
            Toast.makeText(context, context.getResources().getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show();
        }

        if (callbacks.get(requestType) != null) {
            for (UploadManagerCallback callback : callbacks.get(requestType)) {
                callback.uploadFinished(requestType, null, false, "", reqObject);
            }
        }
    }
}