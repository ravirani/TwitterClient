package com.ravcode.apps.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ravcode.apps.twitterclient.models.Tweet;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MentionsTimelineFragment extends TweetsListFragment {


    public MentionsTimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTweetType = Tweet.TweetType.MENTIONS_TIMELINE;
        super.onCreate(savedInstanceState);
    }
}
