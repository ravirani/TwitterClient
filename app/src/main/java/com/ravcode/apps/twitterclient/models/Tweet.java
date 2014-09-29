package com.ravcode.apps.twitterclient.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ravi on 9/28/14.
 */
public class Tweet {
    private String body;
    private long uid;
    private String createdAt;
    private User user;

    private static long maxTweetID = 0;

    public static Tweet fromJSON(JSONObject tweetJSONObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = tweetJSONObject.getString("text");
            tweet.uid = tweetJSONObject.getLong("id");
            tweet.createdAt = tweetJSONObject.getString("created_at");
            tweet.user = User.fromJSON(tweetJSONObject.getJSONObject("user"));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return tweet;
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

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        long lowestTweetID = 0;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJSON = null;
            try {
                tweetJSON = jsonArray.getJSONObject(i);
            }
            catch (JSONException e) {
                e.printStackTrace();
                continue;
            }


            Tweet tweet = Tweet.fromJSON(tweetJSON);
            if (tweet != null) {
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
        maxTweetID = lowestTweetID - 1;
        return tweets;
    }

    @Override
    public String toString() {
        return body;
    }

    public static long getMaxTweetID() {
        return maxTweetID;
    }
}
