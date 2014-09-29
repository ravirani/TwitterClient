package com.ravcode.apps.twitterclient.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ravi on 9/28/14.
 */
public class User {
    private String name;
    private long uid;
    private String screenName;
    private String profileImageURL;

    public static User fromJSON(JSONObject userJSONObject) {
        User user = new User();
        try {
            user.name = userJSONObject.getString("name");
            user.uid = userJSONObject.getLong("id");
            user.screenName = userJSONObject.getString("screen_name");
            user.profileImageURL = userJSONObject.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }
}
