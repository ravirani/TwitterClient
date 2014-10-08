package com.ravcode.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ravcode.apps.twitterclient.models.Tweet;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserTimelineFragment extends TweetsListFragment {

    public UserTimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTweetType = Tweet.TweetType.USER_TIMELINE;
        super.onCreate(savedInstanceState);
    }
}
