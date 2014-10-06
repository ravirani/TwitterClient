package com.ravcode.apps.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

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
        MENTIONS_TIMELINE(2);

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

    @Column(name = "uid", index = true, unique = true)
    private long uid;

    @Column (name = "tweetType", index = true)
    private int tweetType;

    @Column(name = "createdAt")
    private String createdAt;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    private static long lowestTweetID = 0;
    private static long highestTweetID = 0;

    public static Tweet fromJSON(JSONObject tweetJSONObject, TweetType tweetType) {
        Tweet tweet = new Tweet();
        try {
            long tweetID = tweetJSONObject.getLong("id");
            if (Tweet.getTweetByID(tweetID) != null) {
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

    public static void initHighestAndLowestTweetIDs() {
//        highestTweetID = SQLiteUtils.execSql("SELECT MAX(uid) FROM tweets");
//        lowestTweetID = (new Select("MIN(uid)").from(Tweet.class).executeSingle()).getId();
//        Log.d("DEBUG", highestTweetID + " " + lowestTweetID);
    }


    public static List<Tweet> fetchAllTweets(TweetType tweetType) {
        return new Select()
                .from(Tweet.class)
                .where("tweetType = ?", tweetType.getValue())
                .orderBy("uid DESC")
                .execute();
    }

    public static Tweet getTweetByID(long uid) {
        return new Select()
                .from(Tweet.class)
                .where("uid = ?", uid)
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

        long lowestTweetID = 0;
        long highestTweetID = 0;
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
                highestTweetID = Math.max(highestTweetID, tweet.getUid());
                if (lowestTweetID == 0) {
                    lowestTweetID = tweet.getUid();
                }

                if (lowestTweetID > tweet.getUid()) {
                    lowestTweetID = tweet.getUid();
                }

                tweets.add(tweet);
            }
        }

        // Reduced by one to remove duplicates
        // https://dev.twitter.com/rest/public/timelines
        Tweet.lowestTweetID = lowestTweetID - 1;
        Tweet.highestTweetID = highestTweetID;
        return tweets;
    }

    @Override
    public String toString() {
        return body;
    }

    public static long getLowestTweetID() {
        return lowestTweetID;
    }

    public static long getHighestTweetID() {
        return highestTweetID;
    }
}
