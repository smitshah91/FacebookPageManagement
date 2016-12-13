package com.example.smit.fbpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragment extends Fragment {

    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "name";
    private static final String ARG_PARAM3 = "token";

    private String pageId, pageName, pageToken;
    ProfilePictureView profilePicture;
    TextView textView;
    Button fragmentButton;


    private OnFragmentInteractionListener mListener;

    public PageFragment() {

    }

    public static PageFragment newInstance(String id, String name, String token) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, name);
        args.putString(ARG_PARAM3, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            pageId = getArguments().getString(ARG_PARAM1);
            pageName = getArguments().getString(ARG_PARAM2);
            pageToken = getArguments().getString(ARG_PARAM3);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
