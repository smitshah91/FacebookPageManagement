package com.example.smit.fbpage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

public class PageFragment extends Fragment {

    private static final String PAGE_ID = "id";
    private static final String PAGE_NAME = "name";
    private static final String PAGE_TOKEN = "token";

    private String pageId, pageName, pageToken;
    ProfilePictureView profilePicture;
    TextView textView;
    Button fragmentButton;

    private OnFragmentInteractionListener mListener;

    public PageFragment() {}

    public static PageFragment newInstance(String id, String name, String token) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_ID, id);
        args.putString(PAGE_NAME, name);
        args.putString(PAGE_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageId = getArguments().getString(PAGE_ID);
            pageName = getArguments().getString(PAGE_NAME);
            pageToken = getArguments().getString(PAGE_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page, container, false);

        profilePicture = (ProfilePictureView) view.findViewById(R.id.pagePicture);
        textView = (TextView) view.findViewById(R.id.pageText);
        fragmentButton = (Button) view.findViewById(R.id.fragmentButton);
        fragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPageDetailActivity();
            }
        });

        textView.setText(pageName);
        profilePicture.setProfileId(pageId);

        return view;
    }

    private void startPageDetailActivity() {
        Intent pageDetailIntent = new Intent(getActivity(), PageDetailActivity.class);
        pageDetailIntent.putExtra("pageID", pageId);
        pageDetailIntent.putExtra("pageName", pageName);
        pageDetailIntent.putExtra("pageToken", pageToken);
        startActivity(pageDetailIntent);
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
