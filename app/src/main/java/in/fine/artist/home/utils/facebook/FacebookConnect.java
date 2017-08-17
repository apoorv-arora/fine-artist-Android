package in.fine.artist.home.utils.facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import in.fine.artist.home.R;
import in.fine.artist.home.utils.CommonLib;

/**
 * Created by apoorvarora on 14/05/17.
 */
public class FacebookConnect {

    // permissions sought from facebook
    private final String PERMISSION_EMAIL = "email";
    private final String PERMISSION_USER_FRIENDS = "user_friends";
    private final String PERMISSION_PUBLIC_PROFILE = "public_profile";
    private final String PERMISSION_ABOUT_ME = "user_about_me";
    private final String PERMISSION_POST = "publish_actions";

    // fields sought from the facebook 'user' object
    private final String FIELD_NAME = "name";
    private final String FIELD_ID = "id";
    private final String FIELD_EMAIL = "email";
    private final String FIELD_BIRTHDAY = "birthday";
    private final String FIELD_GENDER = "gender";
    //private final String FIELD_LOCATION = "location";
    private final String FIELDS = "fields";
    private final String FIELD_PICTURE = "picture.type(large)";

    private Exception failException = null;

    /**
     * action
     *  1 => login
     *  2 => connect
     *  3 => post
     *  4 => connect and post
     *  5 => friends list
     */
    private int action = 1;
    private String APPLICATION_ID = "";
    private String accessToken = "";
    private int returnAction = 1;
    private boolean regIdSent = false;

    FacebookConnectCallback callback;
    private boolean switchToThreeWhenSessionOpened = false;

    // gcm
    String regId, invitationId;

    public FacebookConnect(FacebookConnectCallback callback, int action, String APPLICATION_ID, boolean gcm, String regId, String invitationId) {
        this.action = action;
        this.callback = callback;
        this.APPLICATION_ID = APPLICATION_ID;
        returnAction = action;
        this.regId = regId;
        this.invitationId = invitationId;
    }

    public FacebookConnect(FacebookConnectCallback callback, int action, String APPLICATION_ID, String accessToken) {
        this.action = action;
        this.callback = callback;
        this.APPLICATION_ID = APPLICATION_ID;
        this.accessToken = accessToken;
        returnAction = action;
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {

            CommonLib.VLog("FC session", session.toString());
            CommonLib.VLog("FC state", state.name());

            if (exception != null) {

                failException = exception;
                CommonLib.VLog("FC exception", exception.toString() + ".");
                exception.printStackTrace();

                Bundle bundle = new Bundle();
                bundle.putInt("action", returnAction);
                bundle.putInt("status", 0);

                if (failException != null) {
                    try {
                        bundle.putString("error_exception", failException.getClass().toString());
                        bundle.putString("error_stackTrace", Log.getStackTraceString(failException) + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callback.response(bundle);
            }

            if (session.isOpened()) {
                CommonLib.VLog("FC", "session isOpened");
                fbRequestMe(session);
                session.removeCallback(this);

            } else {
                if (state == SessionState.CLOSED_LOGIN_FAILED || state == SessionState.CLOSED) {
                    CommonLib.VLog("FC", "session !isOpened");
                    Bundle bundle = new Bundle();
                    bundle.putInt("action", returnAction);
                    bundle.putInt("status", 0);
                    callback.response(bundle);
                    session.removeCallback(this);
                }
            }

        }
    }

    private Session.StatusCallback mStatusCallback = new SessionStatusCallback();

    public void execute() {

        CommonLib.VLog("FC", "Session.getActiveSession()");

        // get the current active session
        Session session = Session.getActiveSession();

        // create session if null
        if (session == null) {
            CommonLib.VLog("FC", "new Session");
            session = new Session((Activity) callback);
            Session.setActiveSession(session);
        }
        CommonLib.VLog("FC session state", session.getState() + ".");

        if (!session.isOpened()) {
            String permsString = (action != 3) ? PERMISSION_EMAIL : PERMISSION_POST;

            if (action != 3) {

                if (!session.isOpened() && !session.isClosed()) {
                    CommonLib.VLog("FC", "open for read, site alpha");
                    session.openForRead(new Session.OpenRequest((Activity) callback)
                            .setPermissions(Arrays.asList(permsString, PERMISSION_USER_FRIENDS, PERMISSION_PUBLIC_PROFILE, PERMISSION_ABOUT_ME))
                            .setCallback(mStatusCallback));

                } else {
                    CommonLib.VLog("FC", "open for read, site bravo");
                    Session.openActiveSession((Activity) callback, true, mStatusCallback);
                }

            } else {

                if (!session.isOpened() && !session.isClosed()) {
                    CommonLib.VLog("FC", "open for publish, site alpha");
                    session.openForPublish(new Session.OpenRequest((Activity) callback)
                            .setPermissions(Arrays.asList(permsString))
                            .setCallback(mStatusCallback));

                } else {
                    CommonLib.VLog("FC", "open for publish, site bravo");
                    Session.openActiveSession((Activity) callback, true, mStatusCallback);
                }
            }

        } else {

            CommonLib.VLog("FC", "session opened");
            if (action == 3) {

                try {
                    if (!switchToThreeWhenSessionOpened)
                        session.requestNewPublishPermissions(new NewPermissionsRequest((Activity) callback, Arrays.asList(new String[] { PERMISSION_POST})));

                    session.addCallback(new Session.StatusCallback() {

                        @Override
                        public void call(Session session, SessionState state, Exception exception) {

                            if (exception != null) {
                                CommonLib.VLog("FC exception", exception.toString() + ".");
                                exception.printStackTrace();
                                Bundle bundle = new Bundle();
                                bundle.putInt("action", returnAction);
                                bundle.putInt("status", 0);
                                callback.response(bundle);

                            }

                            if (session.isOpened()) {
                                CommonLib.VLog("FC", "session isOpened");
                                fbRequestMe(session);
                                session.removeCallback(this);

                            } else {
                                if (state == SessionState.CLOSED_LOGIN_FAILED || state == SessionState.CLOSED) {
                                    CommonLib.VLog("FC", "session !isOpened");
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("action", returnAction);
                                    bundle.putInt("status", 0);
                                    callback.response(bundle);
                                    session.removeCallback(this);
                                }

                            }
                        }
                    });

                } catch (Exception e) {
                    CommonLib.VLog("FC", "Exception Raised");
                    failException = e;
                    e.printStackTrace();
                }
            } else {
                fbRequestMe(session);
            }
        }
    }

    public void fbRequestMe(final Session session) {

        CommonLib.VLog("FC", "fbRequestME");

        String REQUEST_FIELDS = TextUtils.join(",", new String[] {
                FIELD_ID, FIELD_EMAIL, FIELD_NAME, FIELD_BIRTHDAY, FIELD_GENDER, FIELD_PICTURE});//, FIELD_LOCATION });

        Bundle parameters = new Bundle();
        parameters.putString(FIELDS, REQUEST_FIELDS);

        final Request request = new Request();
        request.setSession(session);
        request.setParameters(parameters);
        request.setGraphPath("me");
        request.setCallback(new Request.Callback() {

            @Override
            public void onCompleted(Response response) {

                CommonLib.VLog("FC response.getRawResponse();", response.getRawResponse());
                final GraphUser user = response.getGraphObjectAs(GraphUser.class);

                if (user != null) {
                    CommonLib.VLog("FC", "user !null");
                    String email = "";
                    try {
                        JSONObject json = user.getInnerJSONObject();
                        Map<String, Object> map = user.asMap();
                        json = new JSONObject(map);

                        final JSONObject finalJson = json;

                        if(json.has("email"))
                            email = json.getString("email");
                        final JSONArray permissionsJson = new JSONArray(session.getPermissions());
                        if (action == 1) {
                            //check for the email here...
//							if email is null show dialog

                            if(email == null || email.equalsIgnoreCase("")) {
                                LayoutInflater inflater = LayoutInflater.from((Context) callback);
                                final View customView = inflater.inflate(R.layout.dialog_email_input, null);
                                final AlertDialog dialog = new AlertDialog.Builder((Context) callback, AlertDialog.THEME_HOLO_LIGHT)
                                        .setCancelable(false)
                                        .setView(customView)
                                        .create();
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                                customView.findViewById(R.id.email_input).requestFocus();
                                ((InputMethodManager) ((Activity)callback).getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(((EditText)customView.findViewById(R.id.email_input)), InputMethodManager.SHOW_FORCED);
                                try {
                                    android.os.Handler handler = new android.os.Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if( (Build.VERSION.SDK_INT >=17 && !((Activity) callback).isDestroyed()) && customView != null && customView.findViewById(R.id.email_input) != null)
                                                ((InputMethodManager) ((Activity)callback).getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(customView.findViewById(R.id.email_input), InputMethodManager.SHOW_FORCED);
                                        }
                                    }, 400);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                customView.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String inputMail = ((TextView)customView.findViewById(R.id.email_input)).getText().toString().trim();
                                        if(inputMail == null || inputMail.equalsIgnoreCase("")) {
                                            Toast.makeText((Context) callback, "Invalid email", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        boolean result = true;
                                        try {
                                            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
                                            Pattern p = Pattern.compile(ePattern);
                                            java.util.regex.Matcher m = p.matcher(inputMail);
                                            result = m.matches();
                                        } catch (Exception ex) {
                                            result = false;
                                        }
                                        if(!result) {
                                            Toast.makeText((Context) callback, "Invalid email", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        CommonLib.hideKeyBoard((Activity)callback, customView.findViewById(R.id.email_input));

                                        callback.response(getBundledDataFromParams(user.getId().toString(), finalJson, inputMail.trim().toString(), session.getAccessToken(), permissionsJson.toString()));

                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                callback.response(getBundledDataFromParams(user.getId().toString(), json, email, session.getAccessToken(), permissionsJson.toString()));
                            }
                        } else {
                            CommonLib.VLog("FC", "onCompleted");
                            action = 3;

                            // Session request in execute code
                            switchToThreeWhenSessionOpened = true;
                            session.requestNewPublishPermissions(new NewPermissionsRequest((Activity) callback, Arrays.asList(new String[] { PERMISSION_POST })));
                            execute();
                        }

                    } catch (Exception e) {
                        failException = e;
                        e.printStackTrace();
                    }

                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("action", returnAction);
                    bundle.putInt("status", 0);

                    callback.response(bundle);
                    // callback = null;
                    CommonLib.VLog("FC", "user null");
                }

                request.setCallback(null);
            }
        });
        CommonLib.VLog("FC", "batchAsyncFired");
        Request.executeBatchAsync(request);
    }

    private Bundle getBundledDataFromParams(String facebookId, JSONObject fbJson, String email, String token, String permissions) {
        Bundle bundle = new Bundle();

        bundle.putString("facebookId", facebookId);

        String profile_pic = null;
        try {
            if (fbJson.has("picture")) {
                JSONObject profilePicJson;
                profilePicJson = fbJson.getJSONObject("picture");
                if (profilePicJson.has("data")) {
                    profilePicJson = profilePicJson.getJSONObject("data");
                    if (profilePicJson.has("url"))
                        profile_pic = String.valueOf(profilePicJson.get("url"));
                }
            }
            bundle.putString("profile_pic", profile_pic);
            if (fbJson.has("id"))
                bundle.putString("id", String.valueOf(fbJson.get("id")));
            if (fbJson.has("email"))
                bundle.putString("email", String.valueOf(fbJson.get("email")));
            if (fbJson.has("name"))
                bundle.putString("userName", String.valueOf(fbJson.get("name")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        bundle.putString("facebookData", fbJson.toString());
        bundle.putInt("status", 1);
        bundle.putString("token", token);
        bundle.putString("facebookPermission", permissions);

        return bundle;
    }

}