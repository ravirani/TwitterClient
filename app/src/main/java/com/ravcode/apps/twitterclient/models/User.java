package com.ravcode.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ravi on 9/28/14.
 */
@Table(name = "users")
public class User extends Model {

    @Column(name = "name")
    private String name;

    @Column(name = "uid", unique = true)
    private long uid;

    @Column(name = "screenName")
    private String screenName;

    @Column(name = "profileImageURL")
    private String profileImageURL;

    public static User getUserByID(long uid) {
        return new Select()
                .from(User.class)
                .where("uid = ?", uid)
                .executeSingle();
    }

    public static User fromJSON(JSONObject userJSONObject) {
        User user = null;
        try {
            long userID = userJSONObject.getLong("id");
            user = User.getUserByID(userID);
            if (user == null) {
                user = new User();
            }

            user.name = userJSONObject.getString("name");
            user.uid = userJSONObject.getLong("id");
            user.screenName = userJSONObject.getString("screen_name");
            user.profileImageURL = userJSONObject.getString("profile_image_url");
            user.save();
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
