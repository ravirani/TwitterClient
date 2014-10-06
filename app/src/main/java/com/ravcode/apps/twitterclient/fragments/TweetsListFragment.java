package com.ravcode.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ravcode.apps.twitterclient.EndlessScrollListener;
import com.ravcode.apps.twitterclient.R;
import com.ravcode.apps.twitterclient.TweetArrayAdapter;
import com.ravcode.apps.twitterclient.models.Tweet;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
abstract public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> tweets;
    private com.ravcode.apps.twitterclient.TweetArrayAdapter aTweets;
    private ListView lvTweets;
    protected PullToRefreshLayout mPullToRefreshLayout;

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
        Tweet.initHighestAndLowestTweetIDs();
        aTweets = new TweetArrayAdapter(getActivity(), tweets);
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
        aTweets.insert(tweet, 0);
        // Updating the feed to update the timestamps
        aTweets.notifyDataSetChanged();
    }

    protected abstract List loadInitialData();
    protected abstract void loadMoreData(int page);
}
