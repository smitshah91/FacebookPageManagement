package com.example.smit.fbpage;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;


public class MainFragment extends Fragment {

    private CallbackManager callbackManager;
    private LoginButton fbLoginButton;
    private String name;
    private String[] pageId, pageName, pageAccessToken;
    byte[] profilePicArray;

    private FacebookCallback<LoginResult> callBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {

            Bundle meRequestParameters = new Bundle();
            meRequestParameters.putString("fields", "id,name,email,location,picture");

            Bundle pageRequestParameters = new Bundle();
            pageRequestParameters.putString("fields", "id,name,category,picture,access_token");

            GraphRequest meRequest = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            System.out.println(loginResult.getAccessToken());
                            System.out.println(AccessToken.getCurrentAccessToken());
                            try {
                                name = object.optString("name");

                                if (object.has("picture")) {
                                    if(object.getJSONObject("picture").has("data"))
                                    {
                                        String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                        new NetworkTask(profilePicUrl).execute();
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                    });

            GraphRequest pagesRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),"me/accounts",
                    pageRequestParameters, HttpMethod.GET, new GraphRequest.Callback()
            {
                @Override
                public void onCompleted(GraphResponse response) {
                    System.out.println(response.toString());
                    JSONObject object = response.getJSONObject();
                    if(object != null)
                    {
                        try {
                            JSONArray pageArray =object.getJSONArray("data");
                            if(pageArray != null)
                            {

                                pageId = new String[pageArray.length()];
                                pageName = new String[pageArray.length()];
                                pageAccessToken = new String[pageArray.length()];

                                for(int i=0; i<pageArray.length(); i++)
                                {
                                    JSONObject jsonObject = pageArray.getJSONObject(i);
                                    pageId[i] = jsonObject.getString("id");
                                    pageName[i] = jsonObject.getString("name");
                                    pageAccessToken[i] = jsonObject.getString("access_token");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            meRequest.setParameters(meRequestParameters);
            meRequest.executeAsync();

            pagesRequest.setParameters(pageRequestParameters);
            pagesRequest.executeAsync();


            //getUserDataAndPages(loginResult.getAccessToken());
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        /*if(isLoggedIn())
        {
            getUserDataAndPages(AccessToken.getCurrentAccessToken());
            startProfileActivity();
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(callbackManager, callBack);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class NetworkTask extends AsyncTask<Void, Void, Bitmap> {
        private String urlString;

        NetworkTask(String url)
        {
            this.urlString = url;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            URL url;
            Bitmap profilePic = null;
            try {
                url = new URL(urlString);
                profilePic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return profilePic;
        }

        @Override
        protected void onPostExecute(Bitmap image)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            profilePicArray = stream.toByteArray();

            startProfileActivity();
        }
    }

    private void startProfileActivity() {
        Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
        profileIntent.putExtra("name", name);
        profileIntent.putExtra("profilePic", profilePicArray);
        profileIntent.putExtra("pageId", pageId);
        profileIntent.putExtra("pageName", pageName);
        profileIntent.putExtra("pageToken", pageAccessToken);

        startActivity(profileIntent);
    }

    private void getUserDataAndPages(AccessToken accessToken){
        Bundle meRequestParameters = new Bundle();
        meRequestParameters.putString("fields", "id,name,email,location,picture");

        GraphRequest meRequest = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            name = object.optString("name");

                            if (object.has("picture")) {
                                if(object.getJSONObject("picture").has("data"))
                                {
                                    String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                    new NetworkTask(profilePicUrl).execute();
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });

        meRequest.setParameters(meRequestParameters);
        meRequest.executeAsync();

        Bundle pageRequestParameters = new Bundle();
        pageRequestParameters.putString("fields", String.valueOf(R.string.parameters_pages));

        GraphRequest pagesRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),"me/accounts",
                pageRequestParameters, HttpMethod.GET, new GraphRequest.Callback()
        {
            @Override
            public void onCompleted(GraphResponse response) {
                System.out.println(response.toString());
            }
        });

        pagesRequest.setParameters(pageRequestParameters);
        pagesRequest.executeAsync();

        GraphRequestBatch batch = new GraphRequestBatch();
        batch.add(meRequest);
        batch.add(pagesRequest);
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch graphRequests) {
                startProfileActivity();
            }
        });
        batch.executeAndWait();
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
