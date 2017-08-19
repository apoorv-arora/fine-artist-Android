package in.fine.artist.home.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.fine.artist.home.data.AppConfig;
import in.fine.artist.home.data.BlogUpdate;
import in.fine.artist.home.data.User;
import in.fine.artist.home.data.course.CourseBrief;
import in.fine.artist.home.data.course.CourseCategory;
import in.fine.artist.home.utils.networking.UploadManager;

/**
 * Created by apoorvarora on 03/10/16.
 */
public class ParserJson {

    public static final Object[] parseData(int requestType, String responseJson) throws JSONException {
        final Object[] response = new Object[]{null, false, ""};

        if (responseJson == null || responseJson.isEmpty())
            return response;

        JSONObject responseObject = null;

        if (!TextUtils.isEmpty(responseJson)) {
            responseObject = new JSONObject(responseJson);
        }

        if (responseObject == null)
            return response;

        int codeResponse;

        if (responseObject.has("code") && responseObject.get("code") instanceof Integer) {
            codeResponse = responseObject.getInt("code");

            response[1] = (codeResponse == 0);
        }

        if (responseObject.has("errorMessage") && responseObject.get("errorMessage") != null)
            response[2] = String.valueOf(responseObject.get("errorMessage"));

        switch (requestType) {
            case UploadManager.LOGIN:
            case UploadManager.UPDATE_PROFILE:
                response[0] = parse_LoginResponse(responseObject);
                break;
            case UploadManager.COURSES_USER:
            case UploadManager.COURSES_SEARCH:
            case UploadManager.COURSES_CATEGORY_ID:
            case UploadManager.COURSES_RECOMMENDED:
                response[0] = parse_RecommendedCourses(responseObject);
                break;
            case UploadManager.UPDATES:
                response[0] = parse_BlogUpdates(responseObject);
                break;
            case UploadManager.APP_CONFIG:
                Object[] appConfigResponse = new Object[2];
                appConfigResponse[0] = parse_AppConfig(responseObject);
                appConfigResponse[1] = responseObject.toString();
                response[0] = appConfigResponse;
                break;
            case UploadManager.PLACE_AUTOCOMPLETE_START:
            case UploadManager.PLACE_AUTOCOMPLETE_DROP:
                response[0] = responseJson;
                break;
            case UploadManager.COURSES_CATEGORIES:
                response[0] = parse_CourseCategories(responseObject);
                break;
        }
        return response;
    }

    public static Object parse_LoginResponse(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        Object[] response = new Object[2];
        if (jsonResponse.has("user") && jsonResponse.get("user") instanceof JSONObject) {
            response[0] = parse_User(jsonResponse.getJSONObject("user"));
        }

        if (jsonResponse.has("accessToken")) {
            response[1] = String.valueOf(jsonResponse.get("accessToken"));
        }

        return response;
    }

    public static List<CourseCategory> parse_CourseCategories(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        List<CourseCategory> courseBriefList = new ArrayList<>();

        if (jsonResponse.has("courseCategories") && jsonResponse.get("courseCategories") instanceof JSONArray) {
            JSONArray courseArray = jsonResponse.getJSONArray("courseCategories");
            for (int i=0; i<courseArray.length(); i++) {
                if ( courseArray.get(i) instanceof JSONObject)
                    courseBriefList.add(parse_CourseCategory(courseArray.getJSONObject(i)));
            }
        }

        return courseBriefList;
    }

    public static List<CourseBrief> parse_RecommendedCourses(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        List<CourseBrief> courseBriefList = new ArrayList<>();

        if (jsonResponse.has("recommendedCourses") && jsonResponse.get("recommendedCourses") instanceof JSONArray) {
            JSONArray courseArray = jsonResponse.getJSONArray("recommendedCourses");
            for (int i=0; i<courseArray.length(); i++) {
                if ( courseArray.get(i) instanceof JSONObject)
                    courseBriefList.add(parse_CourseBrief(courseArray.getJSONObject(i)));
            }
        }

        return courseBriefList;
    }

    public static List<BlogUpdate> parse_BlogUpdates(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        List<BlogUpdate> blogUpdates = new ArrayList<>();

        if (jsonResponse.has("blogUpdates") && jsonResponse.get("blogUpdates") instanceof JSONArray) {
            JSONArray courseArray = jsonResponse.getJSONArray("blogUpdates");
            for (int i=0; i<courseArray.length(); i++) {
                if ( courseArray.get(i) instanceof JSONObject)
                    blogUpdates.add(parse_BlogUpdate(courseArray.getJSONObject(i)));
            }
        }

        return blogUpdates;
    }

    public static User parse_User(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        User supplier = new User();

        if (jsonResponse.has("userId") && jsonResponse.get("userId") instanceof Integer)
            supplier.setUserId(jsonResponse.getInt("userId"));

        if (jsonResponse.has("name"))
            supplier.setName(String.valueOf(jsonResponse.get("name")));

        if (jsonResponse.has("number"))
            supplier.setPhoneNumber(String.valueOf(jsonResponse.get("number")));

        if (jsonResponse.has("imageUrl"))
            supplier.setImageUrl(String.valueOf(jsonResponse.get("imageUrl")));

        return supplier;
    }

    public static CourseCategory parse_CourseCategory(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        CourseCategory courseBrief = new CourseCategory();

        if (jsonResponse.has("courseCategoryId") && jsonResponse.get("courseCategoryId") instanceof Integer)
            courseBrief.setCategoryId(jsonResponse.getInt("courseCategoryId"));

        if (jsonResponse.has("categoryTitle"))
            courseBrief.setCategoryTitle(String.valueOf(jsonResponse.get("categoryTitle")));

        if (jsonResponse.has("categoryIconUrl"))
            courseBrief.setCategoryIconUrl(String.valueOf(jsonResponse.get("categoryIconUrl")));

        return courseBrief;
    }

    public static CourseBrief parse_CourseBrief(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        CourseBrief courseBrief = new CourseBrief();

        if (jsonResponse.has("shortDescription"))
            courseBrief.setShortDescription(String.valueOf(jsonResponse.get("shortDescription")));

        if (jsonResponse.has("title"))
            courseBrief.setTitle(String.valueOf(jsonResponse.get("title")));

        if (jsonResponse.has("coverImageUrl"))
            courseBrief.setCoverImageUrl(String.valueOf(jsonResponse.get("coverImageUrl")));

        return courseBrief;
    }

    public static AppConfig parse_AppConfig(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        AppConfig appConfig = new AppConfig();
        if (jsonResponse.has("appConfigList") && jsonResponse.get("appConfigList") instanceof JSONArray) {
            JSONArray appConfigJsonArr = jsonResponse.getJSONArray("appConfigList");
            for (int i=0; i<appConfigJsonArr.length(); i++) {
                if (appConfigJsonArr.get(i) instanceof JSONObject) {
                    JSONObject appConfigJson = appConfigJsonArr.getJSONObject(i);
                    if (appConfigJson.has("appKey")) {
                        String key = String.valueOf(appConfigJson.get("appKey"));
                        String value = "";
                        if (appConfigJson.has("appValue"))
                            value = String.valueOf(appConfigJson.get("appValue"));

                        if (key.equalsIgnoreCase("terms")) {
                            appConfig.setTncUrl(value);
                        } else if (key.equalsIgnoreCase("aboutUs")) {
                            appConfig.setAboutUsUrl(value);
                        } else if (key.equalsIgnoreCase("contact")) {
                            appConfig.setContactUsNumber(value);
                        }

                    }
                }
            }
        }
        return appConfig;
    }

    public static BlogUpdate parse_BlogUpdate(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse == null)
            return null;

        BlogUpdate blogUpdate = new BlogUpdate();

        if (jsonResponse.has("blogId") && jsonResponse.get("blogId") instanceof Integer)
            blogUpdate.setBlogId(jsonResponse.getInt("blogId"));

        if (jsonResponse.has("title"))
            blogUpdate.setTitle(String.valueOf(jsonResponse.get("title")));

        if (jsonResponse.has("shortDescription"))
            blogUpdate.setShortDescription(String.valueOf(jsonResponse.get("shortDescription")));

        if (jsonResponse.has("longDescription"))
            blogUpdate.setLongDescription(String.valueOf(jsonResponse.get("longDescription")));

        if (jsonResponse.has("refUrl"))
            blogUpdate.setRefUrl(String.valueOf(jsonResponse.get("refUrl")));

        if (jsonResponse.has("refImageUrl"))
            blogUpdate.setRefImageUrl(String.valueOf(jsonResponse.get("refImageUrl")));

        if (jsonResponse.has("created") && jsonResponse.get("created") instanceof Integer)
            blogUpdate.setCreated(jsonResponse.getInt("created"));

        if (jsonResponse.has("modified") && jsonResponse.get("modified") instanceof Integer)
            blogUpdate.setModified(jsonResponse.getInt("modified"));

        return blogUpdate;
    }

}

