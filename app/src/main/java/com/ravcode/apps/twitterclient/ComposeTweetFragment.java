package com.ravcode.apps.twitterclient;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ravcode.apps.twitterclient.models.Tweet;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComposeTweetFragment.OnComposeTweetListener} interface
 * to handle interaction events.
 * Use the {@link ComposeTweetFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ComposeTweetFragment extends DialogFragment {
    private static final String ARG_SCREEN_NAME = "screen_name";
    private static final String ARG_USER_NAME = "user_name";
    private static final String ARG_USER_PROFILE_IMAGE_URL = "user_profile_url";

    private String mScreenName;
    private String mUserName;
    private String mUserProfileURL;

    private TextView tvScreenName;
    private TextView tvUserName;
    private TextView tvTweetCount;
    private EditText etTweetText;

    private OnComposeTweetListener mListener;

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            int remainingLength = 140 - s.length();
            tvTweetCount.setText(String.valueOf(remainingLength));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userID ID of the user composing the tweet.
     * @return A new instance of fragment ComposeTweetFragment.
     */
    public static ComposeTweetFragment newInstance(String userProfileImageURL, String screenName, String userName) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_PROFILE_IMAGE_URL, userProfileImageURL);
        args.putString(ARG_SCREEN_NAME, screenName);
        args.putString(ARG_USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }
    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserProfileURL = getArguments().getString(ARG_USER_PROFILE_IMAGE_URL);
            mScreenName = getArguments().getString(ARG_SCREEN_NAME);
            mUserName = getArguments().getString(ARG_USER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View composeView = inflater.inflate(R.layout.fragment_compose_tweet, container, false);

        // Load views
        tvScreenName = (TextView)composeView.findViewById(R.id.tvScreenName);
        tvUserName = (TextView)composeView.findViewById(R.id.tvUserName);
        tvTweetCount = (TextView)composeView.findViewById(R.id.tvTweetCount);
        etTweetText = (EditText)composeView.findViewById(R.id.etTweetText);
        etTweetText.addTextChangedListener(mTextEditorWatcher);

        // Add content
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mUserProfileURL, (ImageView)composeView.findViewById(R.id.ivProfileImage));
        tvScreenName.setText(mScreenName);
        tvUserName.setText(mUserName);

        // Add onClickHandler
        Button bTweetButton = (Button) composeView.findViewById(R.id.btTweet);
        bTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTweetPressed(composeView);
            }
        });

        return composeView;
    }

    @Override
    public void onResume() {
        etTweetText.requestFocus();
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void onTweetPressed(View view) {

        String tweetText = etTweetText.getText().toString();
        if (TextUtils.isEmpty(tweetText)) {
            Toast.makeText(getActivity(), "Please enter some text to post to Twitter.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tweetText.length() > 140) {
            Toast.makeText(getActivity(), "Tweets cannot be more than 140 characters.", Toast.LENGTH_SHORT).show();
            return;
        }

        TwitterClient twitterClient = TwitterApplication.getRestClient();
        twitterClient.postTweet(tweetText, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                Tweet tweet = Tweet.fromJSON(jsonObject);
                if (tweet != null && mListener != null) {
                    mListener.OnComposeTweet(tweet.getUid());
                }

                // Dismiss if tweet was posted
                dismiss();
            }

            @Override
            public void onFailure(Throwable e, String s) {
                Toast.makeText(getActivity(), "Error posting tweet = " + s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected void handleFailureMessage(Throwable throwable, String s) {
                Toast.makeText(getActivity(), "Error posting tweet = " + s, Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnComposeTweetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnComposeTweetListener {
        public void OnComposeTweet(long newlyAddedTweetID);
    }
}
