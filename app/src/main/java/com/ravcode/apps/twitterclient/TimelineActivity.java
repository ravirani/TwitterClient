package com.ravcode.apps.twitterclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ravcode.apps.twitterclient.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class TimelineActivity extends FragmentActivity implements ComposeTweetFragment.OnComposeTweetListener {
    private TwitterClient twitterClient;
    private ArrayList<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;
    private PullToRefreshLayout mPullToRefreshLayout;

    // Logged in user's credentials
    private String mProfileImageURL;
    private String mUserName;
    private String mUserScreenName;

    public static String PREF_PROFILE_URL = "profile_url";
    public static String PREF_SCREEN_NAME = "screen_name";
    public static String PREF_USER_NAME = "user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Init REST client
        twitterClient = TwitterApplication.getRestClient();

        // Fetch views, init and bind adapter
        lvTweets = (ListView)findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>(Tweet.fetchAllTweets());
        aTweets = new TweetArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);

        // Setup onScrollListener for loading older tweets on scroll
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("DEBUG", "PAGE = " + page);
                populateTimeline(page);
            }
        });

        // Setup pull-to-refresh
        mPullToRefreshLayout = (PullToRefreshLayout)findViewById(R.id.ptrLayout);
        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        populateTimeline(-1);
                    }
                })
                .setup(mPullToRefreshLayout);

        // Read preferences for user credentials
        readUserCredentialsFromPreferences();

        // Fetch credentials if they don't exist
        if (mProfileImageURL == null) {
            fetchUserCredentials();
        }

        // Initial fetch from page zero
        populateTimeline(0);
    }

    public void populateTimeline(int page) {
        if (!checkNetworkConnection()) {
            return;
        }

        final long maxID = page > 0 ? Tweet.getLowestTweetID() : 0;
        final long sinceID = page < 0 ? Tweet.getHighestTweetID() : 0;

        twitterClient.getHomeTimeline(maxID, sinceID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                mPullToRefreshLayout.setRefreshComplete();
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray);

                if (sinceID > 0) {
                    for (Tweet tweet: newTweets) {
                       aTweets.insert(tweet, 0);
                    }
                }
                else {
                    aTweets.addAll(newTweets);
                }

                aTweets.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                mPullToRefreshLayout.setRefreshComplete();
                Toast.makeText(TimelineActivity.this, "Failed fetching tweets = " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void handleFailureMessage(Throwable throwable, String s) {
                mPullToRefreshLayout.setRefreshComplete();
                Toast.makeText(TimelineActivity.this, "Failed fetching tweets = " + s, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fetchUserCredentials() {
        if (!checkNetworkConnection()) {
            return;
        }

        twitterClient.getLoggedInUserCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                try {
                    mProfileImageURL = jsonObject.getString("profile_image_url");
                    mUserName = jsonObject.getString("name");
                    mUserScreenName = jsonObject.getString("screen_name");
                    saveUserCredentialsToPreferences();
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

            }

            @Override
            public void onFailure(Throwable e, String s) {
                Toast.makeText(TimelineActivity.this, "Error fetching user credentials = " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void handleFailureMessage(Throwable throwable, String s) {
                Toast.makeText(TimelineActivity.this, "Error fetching user credentials = " + s, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onCreateNewTweet(MenuItem item) {
        ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance(mProfileImageURL, mUserScreenName, mUserName);
        composeTweetFragment.show(getSupportFragmentManager(), "fragment_compose_new_tweet");
    }

    public void OnComposeTweet(long newlyAddedTweetID) {
        if (newlyAddedTweetID > 0) {
            Tweet tweet = Tweet.getTweetByID(newlyAddedTweetID);

            if (tweet != null) {
                aTweets.insert(tweet, 0);
                // Updating the feed to update the timestamps
                aTweets.notifyDataSetChanged();
            }
        }
    }

    private void saveUserCredentialsToPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PREF_PROFILE_URL, mProfileImageURL);
        editor.putString(PREF_SCREEN_NAME, mUserScreenName);
        editor.putString(PREF_USER_NAME, mUserName);
        editor.commit();
    }

    private void readUserCredentialsFromPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mProfileImageURL = sharedPref.getString(PREF_PROFILE_URL, null);
        mUserScreenName = sharedPref.getString(PREF_SCREEN_NAME, null);
        mUserName = sharedPref.getString(PREF_USER_NAME, null);
    }

    private Boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            Toast.makeText(this, "Cannot complete this request. Please check your internet connection or try again later.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
