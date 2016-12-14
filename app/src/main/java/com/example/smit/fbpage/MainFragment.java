package com.example.smit.fbpage;

import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smit.fbpage.facebookapi.FacebookApi;
import com.example.smit.fbpage.model.Page;
import com.example.smit.fbpage.model.Profile;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainFragment extends Fragment {

    private CallbackManager callbackManager;
    private String name, id;
    private String[] pageId, pageName, pageAccessToken;

    private FacebookCallback<LoginResult> callBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {
            getLoggedIn(loginResult.getAccessToken());
            startProfileActivity();
        }

        @Override
        public void onCancel() {
            System.out.println("Login Cancelled");
        }

        @Override
        public void onError(FacebookException error) {
            System.out.println("Login Error");
        }
    };

    private void getLoggedIn(AccessToken token) {
        Profile profile;
        List<Page> pageList;
        try {
            profile = new MyProfile(token).execute().get();
            pageList = new MyPages(token).execute().get();

            name = profile.getProfileName();
            id = profile.getProfileId();

            pageId = getPageIdArray(pageList);
            pageName = getPageNameArray(pageList);
            pageAccessToken = getPageTokenArray(pageList);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginButton fbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, callBack);

        if(isLoggedIn())
        {
            getLoggedIn(AccessToken.getCurrentAccessToken());
            startProfileActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class MyProfile extends AsyncTask<Void, Void, Profile> {
        private AccessToken token;
        MyProfile(AccessToken accessToken)
        {
            token = accessToken;
        }
        @Override
        protected Profile doInBackground(Void... voids) {
            return FacebookApi.GetMyProfile(token);
        }
    }

    public class MyPages extends AsyncTask<Void, Void, List<Page>> {
        private AccessToken token;
        MyPages(AccessToken accessToken)
        {
            token = accessToken;
        }
        @Override
        protected List<Page> doInBackground(Void... voids) {
            return FacebookApi.GetPageList(token);
        }
    }

    private void startProfileActivity() {
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        profileIntent.putExtra("name", name);
        profileIntent.putExtra("profileId", id);
        profileIntent.putExtra("pageId", pageId);
        profileIntent.putExtra("pageName", pageName);
        profileIntent.putExtra("pageToken", pageAccessToken);

        startActivity(profileIntent);
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private String[] getPageIdArray(List<Page> pageList)
    {
        String[] pageIdArray = new String[pageList.size()];
        for (int i=0; i<pageList.size(); i++)
        {
               pageIdArray[i] = pageList.get(i).getId();
        }
        return pageIdArray;
    }

    private String[] getPageNameArray(List<Page> pageList)
    {
        String[] pageNameArray = new String[pageList.size()];
        for (int i=0; i<pageList.size(); i++)
        {
            pageNameArray[i] = pageList.get(i).getName();
        }
        return pageNameArray;
    }

    private String[] getPageTokenArray(List<Page> pageList)
    {
        String[] pageTokenArray = new String[pageList.size()];
        for (int i=0; i<pageList.size(); i++)
        {
            pageTokenArray[i] = pageList.get(i).getAccessToken();
        }
        return pageTokenArray;
    }
}
