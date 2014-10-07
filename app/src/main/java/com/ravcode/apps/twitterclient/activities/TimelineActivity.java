package com.ravcode.apps.twitterclient;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ravcode.apps.twitterclient.activities.ProfileActivity;
import com.ravcode.apps.twitterclient.fragments.HomeTimelineFragment;
import com.ravcode.apps.twitterclient.fragments.MentionsTimelineFragment;
import com.ravcode.apps.twitterclient.listeners.FragmentTabListener;
import com.ravcode.apps.twitterclient.utils.NetworkConnectivity;

import org.json.JSONException;
import org.json.JSONObject;


public class TimelineActivity extends FragmentActivity implements com.ravcode.apps.twitterclient.ComposeTweetFragment.OnComposeTweetListener {
    public static String PREF_PROFILE_URL = "profile_url";
    public static String PREF_SCREEN_NAME = "screen_name";
    public static String PREF_USER_NAME = "user_name";
    private TwitterClient twitterClient;
    // Logged in user's credentials
    private String mProfileImageURL;
    private String mUserName;
    private String mUserScreenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Init REST client
        twitterClient = TwitterApplication.getRestClient();

        // Read preferences for user credentials
        readUserCredentialsFromPreferences();

        // Fetch credentials if they don't exist
        if (mProfileImageURL == null) {
            fetchUserCredentials();
        }

        setupTabs();
    }

    private void setupTabs() {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab homeTab = actionBar
                .newTab()
                .setText("Home")
                .setTag("HomeTimelineFragment")
                .setTabListener(
                        new FragmentTabListener<HomeTimelineFragment>(R.id.flContainer, this, "home",
                                HomeTimelineFragment.class));

        actionBar.addTab(homeTab);
        actionBar.selectTab(homeTab);

        ActionBar.Tab mentionsTab = actionBar
                .newTab()
                .setText("Mentions")
                .setTag("MentionsTimelineFragment")
                .setTabListener(
                        new FragmentTabListener<MentionsTimelineFragment>(R.id.flContainer, this, "mentions",
                                MentionsTimelineFragment.class));

        actionBar.addTab(mentionsTab);
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
        com.ravcode.apps.twitterclient.ComposeTweetFragment composeTweetFragment = com.ravcode.apps.twitterclient.ComposeTweetFragment.newInstance(mProfileImageURL, mUserScreenName, mUserName);
        composeTweetFragment.show(getSupportFragmentManager(), "fragment_compose_new_tweet");
    }

    public void OnComposeTweet(long newlyAddedTweetID) {
//        if (newlyAddedTweetID > 0) {
//            Tweet tweet = Tweet.getTweetByID(newlyAddedTweetID);
//
//            if (tweet != null) {
//                homeTimelineFragment.insertTweet(tweet, 0);
//            }
//        }
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
        if (!NetworkConnectivity.isAvailable(this)) {
            Toast.makeText(this, "Cannot complete this request. Please check your internet connection or try again later.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void viewUserProfile(MenuItem item) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
