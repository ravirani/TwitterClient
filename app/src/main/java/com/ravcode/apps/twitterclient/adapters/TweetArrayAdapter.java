package com.ravcode.apps.twitterclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ravcode.apps.twitterclient.models.Tweet;

import java.util.List;

/**
 * Created by ravi on 9/28/14.
 */
public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

    private static class ViewHolder {
        ImageView ivUserProfileImage;
        TextView tvUserName;
        TextView tvScreenName;
        TextView tvTimestamp;
        TextView tvTweetBody;
    };

    public TweetArrayAdapter(Context context, List<Tweet> objects) {
        super(context, R.layout.tweet_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Tweet tweet = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.tweet_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.ivUserProfileImage = (ImageView)convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView)convertView.findViewById(R.id.tvUserName);
            viewHolder.tvScreenName = (TextView)convertView.findViewById(R.id.tvScreenName);
            viewHolder.tvTimestamp = (TextView)convertView.findViewById(R.id.tvTimestamp);
            viewHolder.tvTweetBody = (TextView)convertView.findViewById(R.id.tvBody);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.ivUserProfileImage.setImageResource(android.R.color.transparent);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(tweet.getUser().getProfileImageURL(), viewHolder.ivUserProfileImage);
        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvTimestamp.setText(com.ravcode.apps.twitterclient.RelativeDate.getRelativeDate(tweet.getCreatedAt()));
        viewHolder.tvTweetBody.setText(tweet.getBody());

        return convertView;
    }
}
