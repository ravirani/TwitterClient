package com.ravcode.apps.twitterclient.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravi on 9/28/14.
 */

@Table(name = "tweets")
public class Tweet extends Model {

    public enum TweetType {
        HOME_TIMELINE(1),
        MENTIONS_TIMELINE(2),
        USER_TIMELINE(3);

        TweetType(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    };

    @Column(name = "body")
    private String body;

    @Column(name = "uid", index = true)
    private long uid;

    @Column (name = "tweetType", index = true)
    private int tweetType;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    public static Tweet fromJSON(JSONObject tweetJSONObject, TweetType tweetType) {
        Tweet tweet = new Tweet();
        try {
            long tweetID = tweetJSONObject.getLong("id");
            if (Tweet.getTweetByID(tweetID, tweetType) != null) {
                // Do not return existing tweets
                return null;
            }

            tweet.body = tweetJSONObject.getString("text");
            tweet.uid = tweetID;
            tweet.tweetType = tweetType.getValue();
            tweet.createdAt = tweetJSONObject.getString("created_at");
            tweet.user = User.fromJSON(tweetJSONObject.getJSONObject("user"));
            tweet.save();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return tweet;
    }

    public static List<Tweet> fetchAllTweets(TweetType tweetType) {
        return new Select()
                .from(Tweet.class)
                .where("tweetType = ?", tweetType.getValue())
                .orderBy("uid DESC")
                .execute();
    }

    public static Tweet getTweetByID(long uid, TweetType tweetType) {
        return new Select()
                .from(Tweet.class)
                .where("uid = ? AND tweetType = ?", uid, tweetType.getValue())
                .executeSingle();
    }

    public static Tweet deleteTweetsOfType(TweetType tweetType) {
        return new Delete()
                .from(Tweet.class)
                .where("tweetType = ?", tweetType.getValue())
                .executeSingle();
    }

    public int getTweetType() {
        return tweetType;
    }

    public String getBody() {
        return body;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray, TweetType tweetType) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJSON = null;
            try {
                tweetJSON = jsonArray.getJSONObject(i);
            }
            catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJSON(tweetJSON, tweetType);
            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    @Override
    public String toString() {
        return body;
    }

    public static long getLowestTweetID(TweetType mTweetType) {
        long lowestTweetID = 0;
        Cursor cursor = Cache.openDatabase().rawQuery("SELECT MIN(uid) FROM tweets WHERE tweetType = " + mTweetType.getValue(), new String[]{});
        if (cursor.moveToFirst()) {
            lowestTweetID = cursor.getLong(0);
        }
        cursor.close();

        return Math.min(lowestTweetID, lowestTweetID - 1);
    }

    public static long getHighestTweetID(TweetType mTweetType) {
        long highestTweetID = 0;
        Cursor cursor = Cache.openDatabase().rawQuery("SELECT MAX(uid) FROM tweets WHERE tweetType = " + mTweetType.getValue(), new String[]{});
        if (cursor.moveToFirst()) {
            highestTweetID = cursor.getLong(0);
        }
        cursor.close();

        return highestTweetID;
    }
}
