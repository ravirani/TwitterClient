package com.ravcode.apps.twitterclient.models;

import android.text.TextUtils;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

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

    @Column(name = "profileBackgroundColor")
    private String profileBackgroundColor;

    @Column(name = "profileBackgroundImageURL")
    private String profileBackgroundImageURL;

    @Column(name = "followersCount")
    private long followersCount;

    @Column(name = "followingCount")
    private long followingCount;

    @Column(name = "tweetsCount")
    private long tweetsCount;

    @Column(name = "tagLine")
    private String tagLine;

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

            // Convert URL to use a bigger image URL
            user.profileImageURL = userJSONObject.getString("profile_image_url").replace("_normal", "_bigger");

            if (!userJSONObject.isNull("friends_count")) {
                user.followingCount = userJSONObject.getLong("friends_count");
            }

            if (!userJSONObject.isNull("followers_count")) {
                user.followersCount = userJSONObject.getLong("followers_count");
            }

            if (!userJSONObject.isNull("statuses_count")) {
                user.tweetsCount = userJSONObject.getLong("statuses_count");
            }

            if (!userJSONObject.isNull("profile_background_image_url")) {
                user.profileBackgroundImageURL = userJSONObject.getString("profile_background_image_url");
            }

            if (!userJSONObject.isNull("profile_background_color")) {
                user.profileBackgroundColor = userJSONObject.getString("profile_background_color");
            }

            if (!userJSONObject.isNull("description")) {
                user.tagLine = userJSONObject.getString("description");
            }

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

    public String getProfileBackgroundColor() {
        return profileBackgroundColor;
    }

    public String getProfileBackgroundImageURL() {
        return profileBackgroundImageURL;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public long getTweetsCount() {
        return tweetsCount;
    }

    public String getTagLine() {
        return tagLine;
    }
}
