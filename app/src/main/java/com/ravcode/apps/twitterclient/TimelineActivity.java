package com.ravcode.apps.twitterclient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ravcode.apps.twitterclient.models.Tweet;

import org.json.JSONArray;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


public class TimelineActivity extends Activity {
    private TwitterClient twitterClient;
    private ArrayList<Tweet> tweets;
    private TweetArrayAdapter aTweets;
    private ListView lvTweets;
    private PullToRefreshLayout mPullToRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Init REST client
        twitterClient = TwitterApplication.getRestClient();

        // Fetch views, init and bind adapter
        lvTweets = (ListView)findViewById(R.id.lvTweets);
        tweets = new ArrayList<Tweet>();
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

        // Initial fetch from page zero
        populateTimeline(0);
    }

    public void populateTimeline(int page) {

        final int pageNumber = page;
        final long maxID = pageNumber > 0 ? Tweet.getLowestTweetID() : 0;

        twitterClient.getHomeTimeline(maxID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonArray) {
                mPullToRefreshLayout.setRefreshComplete();
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray);

                if (pageNumber < 0) {
                     aTweets.clear();
                }

                aTweets.addAll(newTweets);
            }

            @Override
            public void onFailure(Throwable e, String s) {
                mPullToRefreshLayout.setRefreshComplete();
                Log.d("DEBUG", e.toString());
                Log.d("DEBUG", s);
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
}
