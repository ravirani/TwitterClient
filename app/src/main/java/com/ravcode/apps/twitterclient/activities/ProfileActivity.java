package com.ravcode.apps.twitterclient.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.ravcode.apps.twitterclient.R;
import com.ravcode.apps.twitterclient.TwitterApplication;
import com.ravcode.apps.twitterclient.TwitterClient;
import com.ravcode.apps.twitterclient.models.User;

import org.json.JSONObject;

public class ProfileActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        loadUserProfile();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void loadUserProfile() {
        TwitterClient twitterClient = TwitterApplication.getRestClient();
        twitterClient.getLoggedInUserCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                User user = User.fromJSON(jsonObject);
                getActionBar().setTitle("@" + user.getScreenName());
                populateProfile(user);
                super.onSuccess(jsonObject);
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                super.onFailure(throwable, jsonObject);
            }
        });
    }

    public void populateProfile(User user) {

        ImageView ivProfileBackgroundImage = (ImageView)findViewById(R.id.ivProfileBackgroundImage);
        ImageView ivProfileImage = (ImageView)findViewById(R.id.ivProfileImage);
        TextView tvScreenName = (TextView)findViewById(R.id.tvScreenName);
        TextView tvUserName = (TextView)findViewById(R.id.tvUserName);
        TextView tvTagLine = (TextView)findViewById(R.id.tvTagLine);
        TextView tvTweetsCount = (TextView)findViewById(R.id.tvTweetCount);
        TextView tvFollowingCount = (TextView)findViewById(R.id.tvFollowingCount);
        TextView tvFollowersCount = (TextView)findViewById(R.id.tvFollowersCount);
        View rlHeaderLayout = (View)findViewById(R.id.rlHeader);

        // Load profile image
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(user.getProfileImageURL(), ivProfileImage);

//        // Load background image
//        if (user.getProfileBackgroundImageURL() != null) {
//            // imageLoader.loadImage(user.getProfileBackgroundImageURL(), );
//            imageLoader.displayImage(user.getProfileBackgroundImageURL(), ivProfileBackgroundImage);
//            rlHeaderLayout.setBackgroundColor(Color.TRANSPARENT);
//            ivProfileBackgroundImage.setVisibility(View.VISIBLE);
//        }
//        else if (user.getProfileBackgroundColor() != null) {
//            rlHeaderLayout.setBackgroundColor(Color.parseColor("#" + user.getProfileBackgroundColor()));
//            ivProfileBackgroundImage.setVisibility(View.GONE);
//        }
//        else {
            rlHeaderLayout.setBackgroundColor(Color.BLACK);
            ivProfileBackgroundImage.setVisibility(View.GONE);
//        }

        // Set ScreenName and UserName
        tvUserName.setText(user.getName());
        tvScreenName.setText("@" + user.getScreenName());
        if (user.getTagLine() != null) {
            tvTagLine.setText(user.getTagLine());
            tvTagLine.setVisibility(View.VISIBLE);
        }
        else {
            tvTagLine.setVisibility(View.GONE);
        }

        // Set counts
        tvTweetsCount.setText(user.getTweetsCount() + "\nTWEETS");
        tvFollowingCount.setText(user.getFollowingCount() + "\nFOLLOWING");
        tvFollowersCount.setText(user.getFollowersCount() + "\nFOLLOWERS");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
