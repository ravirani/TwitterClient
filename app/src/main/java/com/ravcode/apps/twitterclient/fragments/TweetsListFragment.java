package com.ravcode.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ravcode.apps.twitterclient.EndlessScrollListener;
import com.ravcode.apps.twitterclient.R;
import com.ravcode.apps.twitterclient.TweetArrayAdapter;
import com.ravcode.apps.twitterclient.TwitterApplication;
import com.ravcode.apps.twitterclient.TwitterClient;
import com.ravcode.apps.twitterclient.models.Tweet;
import com.ravcode.apps.twitterclient.utils.NetworkConnectivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A fragment representing a list of Items.
 */
abstract public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private com.ravcode.apps.twitterclient.TweetArrayAdapter aTweets;
    private ListView lvTweets;
    protected PullToRefreshLayout mPullToRefreshLayout;
    protected Tweet.TweetType mTweetType;
    private TwitterClient twitterClient;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TweetsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init the adapter
        tweets = new ArrayList<Tweet>(loadInitialData());
        aTweets = new TweetArrayAdapter(getActivity(), tweets);

        // Init REST client
        twitterClient = TwitterApplication.getRestClient();

        if (getAdapter().getCount() == 0) {
            // Fetch initial data
            populateTimeline(0);
        }
        else {
            // If it exists, simulate pull to refresh
            populateTimeline(-1);
        }
    }

    public void populateTimeline(int page) {
        if (!checkNetworkConnection()) {
            return;
        }

        final long maxID = page > 0 ? Tweet.getLowestTweetID(mTweetType) : 0;
        final long sinceID = page < 0 ? Tweet.getHighestTweetID(mTweetType) : 0;

        twitterClient.getTimeline(mTweetType, maxID, sinceID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                mPullToRefreshLayout.setRefreshComplete();
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray, mTweetType);

                Log.d("DEBUG", jsonArray.toString());
                if (sinceID > 0) {
                    Log.d("DEBUG", "SINCEID COUNT - " + newTweets.size());
                    for (Tweet tweet : newTweets) {
                        getAdapter().insert(tweet, 0);
                    }
                } else {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        // Get the list view, bind the adapter, and set on scroll listener
        lvTweets = (ListView)rootView.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);

        // Setup onScrollListener for loading older tweets on scroll
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d("DEBUG", "PAGE = " + page);
                loadMoreData(page);
            }
        });

        // Setup pull-to-refresh
        mPullToRefreshLayout = (PullToRefreshLayout)rootView.findViewById(R.id.ptrLayout);
        ActionBarPullToRefresh.from(getActivity())
                .allChildrenArePullable()
                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        loadMoreData(-1);
                    }
                })
                .setup(mPullToRefreshLayout);

        return rootView;
    }

    protected TweetArrayAdapter getAdapter() {
        return aTweets;
    }

    public void insertTweet(Tweet tweet, int position) {
        aTweets.insert(tweet, position);
        // Updating the feed to update the timestamps
        aTweets.notifyDataSetChanged();
    }

    protected void loadMoreData(int page) {
        populateTimeline(page);
    }

    protected List loadInitialData() {
        return Tweet.fetchAllTweets(mTweetType);
    }
}
