package com.ravcode.apps.twitterclient;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ravcode.apps.twitterclient.models.Tweet;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "XVjjOrWswlldKEcyAkDpcgC5X";       // Change this
	public static final String REST_CONSUMER_SECRET = "SYVJ95c4nVRoEKJKOMbFfaNGGYUr31NzCX7fGiSj6Xw6ww7hJy"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getTimeline(Tweet.TweetType tweetType, long maxID, long sinceID, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        switch (tweetType) {
            case HOME_TIMELINE:
                getHomeTimeline(maxID, sinceID, asyncHttpResponseHandler);
                break;

            case USER_TIMELINE:
                getUserTimeline(maxID, sinceID, asyncHttpResponseHandler);
                break;

            case MENTIONS_TIMELINE:
                getMentionsTimeline(maxID, sinceID, asyncHttpResponseHandler);
                break;
        }
    }


    public void getHomeTimeline(long maxID, long sinceID, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiURL = getApiUrl("statuses/home_timeline.json");
        getTweets(apiURL, maxID, sinceID, asyncHttpResponseHandler);
    }

    public void getMentionsTimeline(long maxID, long sinceID, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiURL = getApiUrl("statuses/mentions_timeline.json");
        getTweets(apiURL, maxID, sinceID, asyncHttpResponseHandler);
    }

    public void getUserTimeline(long maxID, long sinceID, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiURL = getApiUrl("statuses/user_timeline.json");
        getTweets(apiURL, maxID, sinceID, asyncHttpResponseHandler);
    }

    private void getTweets(String apiURL, long maxID, long sinceID, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams requestParams = new RequestParams();
        requestParams.put("exclude_replies", "true");
        requestParams.put("count", "10");
        if (maxID > 0) {
            // Right way to achieve pagination - https://dev.twitter.com/rest/public/timelines
            requestParams.put("max_id", "" + maxID);
        }
        else if (sinceID > 0) {
            requestParams.put("since_id", "" + sinceID);
        }

        client.get(apiURL, requestParams, asyncHttpResponseHandler);
    }

    public void getLoggedInUserCredentials(AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiURL = getApiUrl("account/verify_credentials.json");
        RequestParams requestParams = new RequestParams();
        requestParams.put("include_entities", "false");
        requestParams.put("skip_status", "true");
        client.get(apiURL, requestParams, asyncHttpResponseHandler);
    }

    public void postTweet(String tweetText, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        String apiURL = getApiUrl("statuses/update.json");
        RequestParams requestParams = new RequestParams();
        requestParams.put("status", tweetText);
        client.post(apiURL, requestParams, asyncHttpResponseHandler);
    }
}