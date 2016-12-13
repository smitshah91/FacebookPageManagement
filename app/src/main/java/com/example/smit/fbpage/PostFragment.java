package com.example.smit.fbpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PAGE_ID = "pageID";
    private static final String PAGE_NAME = "pageName";
    private static final String MESSAGE = "postMessage";
    private static final String PICTURE = "postPicture";

    // TODO: Rename and change types of parameters
    private String pageID, pageName, postMessage;
    private byte[] postImageByteArray;

    ProfilePictureView pageProfilePicture;
    TextView pageNameView;
    TextView postMessageView;
    ImageView postImage;
    TextView postViews;

    private OnFragmentInteractionListener mListener;

    public PostFragment() {

    }

    public static PostFragment newInstance(String id, String name, String message, byte[] pic) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_ID, id);
        args.putString(PAGE_NAME, name);
        args.putString(MESSAGE, message);
        args.putByteArray(PICTURE, pic);
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
            postImageByteArray = getArguments().getByteArray(PICTURE);
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
        if(postImageByteArray != null)
        {
            postImage.setVisibility(View.VISIBLE);
            Bitmap postImageBitmap = BitmapFactory.decodeByteArray(postImageByteArray,0,postImageByteArray.length);
            postImage.setImageBitmap(postImageBitmap);
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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
