package com.ravcode.apps.twitterclient.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ravcode.apps.twitterclient.activities.ProfileActivity;
import com.ravcode.apps.twitterclient.models.Tweet;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class UserTimelineFragment extends TweetsListFragment {

    private long mUserID;
    public UserTimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mTweetType = Tweet.TweetType.USER_TIMELINE;
        Tweet.deleteTweetsOfType(mTweetType);
        mUserID = ((ProfileActivity)getActivity()).getUserID();
        Log.d("DEBUG", "User ID = " + Long.toString(mUserID));
        super.onCreate(savedInstanceState);
    }

    protected void getData(long maxID, long sinceID, JsonHttpResponseHandler jsonHttpResponseHandler) {
        if (mUserID > 0) {
            twitterClient.getUserTimeline(mUserID, maxID, sinceID, jsonHttpResponseHandler);
        }
        else {
            super.getData(maxID, sinceID, jsonHttpResponseHandler);
        }
    }
}
