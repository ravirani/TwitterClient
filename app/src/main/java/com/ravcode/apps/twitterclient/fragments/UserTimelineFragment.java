package com.ravcode.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ravcode.apps.twitterclient.TwitterApplication;
import com.ravcode.apps.twitterclient.TwitterClient;
import com.ravcode.apps.twitterclient.models.Tweet;
import com.ravcode.apps.twitterclient.utils.NetworkConnectivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserTimelineFragment extends TweetsListFragment {

    TwitterClient twitterClient;
    public UserTimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init REST client
        twitterClient = TwitterApplication.getRestClient();

        if (getAdapter().getCount() == 0) {
            // Fetch initial data
            populateTimeline(0);
        }
    }

    public void populateTimeline(int page) {
        if (!checkNetworkConnection()) {
            return;
        }

        final long maxID = page > 0 ? Tweet.getLowestTweetID() : 0;
        final long sinceID = page < 0 ? Tweet.getHighestTweetID() : 0;

        twitterClient.getUserTimeline(maxID, sinceID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                mPullToRefreshLayout.setRefreshComplete();
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray, Tweet.TweetType.USER_TIMELINE);

                Log.d("DEBUG", jsonArray.toString());
                if (sinceID > 0) {
                    Log.d("DEBUG", "SINCEID COUNT - " + newTweets.size());
                    for (Tweet tweet: newTweets) {
                        getAdapter().insert(tweet, 0);
                    }
                }
                else {
                    Log.d("DEBUG", "MAXID COUNT - " + newTweets.size());
                    getAdapter().addAll(newTweets);
                }

                getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                mPullToRefreshLayout.setRefreshComplete();
                Toast.makeText(getActivity(), "Failed fetching tweets = " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void handleFailureMessage(Throwable throwable, String s) {
                mPullToRefreshLayout.setRefreshComplete();
                Toast.makeText(getActivity(), "Failed fetching tweets = " + s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Boolean checkNetworkConnection() {
        if (!NetworkConnectivity.isAvailable(getActivity())) {
            mPullToRefreshLayout.setRefreshComplete();
            Toast.makeText(getActivity(), "Cannot complete this request. Please check your internet connection or try again later.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    protected void loadMoreData(int page) {
        populateTimeline(page);
    }

    protected List loadInitialData() {
        return Tweet.fetchAllTweets(Tweet.TweetType.USER_TIMELINE);
    }
}
