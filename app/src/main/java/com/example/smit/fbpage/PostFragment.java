package com.example.smit.fbpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;


public class PostFragment extends Fragment {

    private static final String PAGE_ID = "pageID";
    private static final String PAGE_NAME = "pageName";
    private static final String MESSAGE = "postMessage";
    private static final String PICTURE = "postPicture";

    private String pageID, pageName, postMessage;
    private Bitmap postImageBitmap;

    ProfilePictureView pageProfilePicture;
    TextView pageNameView;
    TextView postMessageView;
    ImageView postImage;
    TextView postViews;

    private OnFragmentInteractionListener mListener;

    public PostFragment() {}

    public static PostFragment newInstance(String id, String name, String message, Bitmap pic) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_ID, id);
        args.putString(PAGE_NAME, name);
        args.putString(MESSAGE, message);
        args.putParcelable(PICTURE, pic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageID = getArguments().getString(PAGE_ID);
            pageName = getArguments().getString(PAGE_NAME);
            postMessage = getArguments().getString(MESSAGE);
            postImageBitmap = getArguments().getParcelable(PICTURE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);

        pageProfilePicture = (ProfilePictureView) view.findViewById(R.id.postProfilePic);
        pageNameView = (TextView) view.findViewById(R.id.postPageName);
        postMessageView = (TextView) view.findViewById(R.id.postMessage);
        postImage = (ImageView) view.findViewById(R.id.postImage);
        postViews = (TextView) view.findViewById(R.id.postViews);

        pageProfilePicture.setProfileId(pageID);
        pageNameView.setText(pageName);
        postMessageView.setText(postMessage);
        if(postImageBitmap != null)
        {
            postImage.setVisibility(View.VISIBLE);
            postImage.setImageBitmap(postImageBitmap);
        }
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
