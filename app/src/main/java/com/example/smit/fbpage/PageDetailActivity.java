package com.example.smit.fbpage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.widget.ShareButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class PageDetailActivity extends AppCompatActivity implements PostFragment.OnFragmentInteractionListener{

    ProfilePictureView pageDetailProfilePicture;
    TextView pageNameTextView;
    Button postButton, cancelButton, shareButton;
    RelativeLayout postLayout;
    EditText postTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_detail);

        Bundle data = getIntent().getExtras();
        final String pageId = data.getString("pageID");
        final String pageName = data.getString("pageName");
        final String pageToken = data.getString("pageToken");

        AccessToken currentToken = AccessToken.getCurrentAccessToken();
        final AccessToken pageAccessToken = new AccessToken(pageToken, currentToken.getApplicationId(), currentToken.getUserId(),
                currentToken.getPermissions(), currentToken.getDeclinedPermissions(), currentToken.getSource(),
                currentToken.getExpires(), currentToken.getLastRefresh());

        pageDetailProfilePicture = (ProfilePictureView) findViewById(R.id.pageProfilePicture);
        pageNameTextView = (TextView) findViewById(R.id.pageDetailName);
        postTextView = (EditText) findViewById(R.id.publishPostTextView);
        postLayout = (RelativeLayout) findViewById(R.id.publishPostRelativeLayout);

        postButton = (Button) findViewById(R.id.postButton);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Onclick popup window");

                postButton.setVisibility(View.GONE);
                postLayout.setVisibility(View.VISIBLE);

            }
        });

        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postButton.setVisibility(View.VISIBLE);
                postLayout.setVisibility(View.GONE);
            }
        });
        shareButton = (Button) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject params = new JSONObject();
                try {
                    params.put("message", postTextView.getText());
                    String postAsPagePath = pageId + "/feed";
                    if(params.has("message") && params.get("message") != "")
                    {
                        GraphRequest request = GraphRequest.newPostRequest(
                                pageAccessToken,
                                postAsPagePath,
                                new JSONObject("{\"message\":\""+ postTextView.getText() +"\"}"),
                                new GraphRequest.Callback() {
                                    @Override
                                    public void onCompleted(GraphResponse response) {
                                        System.out.println("After Post" + response.toString());
                                        Intent selfIntent = new Intent(PageDetailActivity.this, PageDetailActivity.class);
                                        selfIntent.putExtra("pageID", pageId);
                                        selfIntent.putExtra("pageName", pageName);
                                        selfIntent.putExtra("pageToken", pageToken);
                                        startActivity(selfIntent);
                                    }
                                });
                        request.executeAsync();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        pageNameTextView.setText(pageName);
        pageDetailProfilePicture.setProfileId(pageId);

        String graphPath = pageId + "/promotable_posts";
        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                graphPath,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        if(object != null)
                        {
                            System.out.println(object);
                            try
                            {
                                JSONArray posts = object.getJSONArray("data");
                                if(posts != null)
                                {
                                    FragmentManager fm = getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();

                                    for (int i=0; i<posts.length(); i++)
                                    {
                                        JSONObject postObject = posts.getJSONObject(i);
                                        String message = null;
                                        byte[] postImageByteArray = null;

                                        if(postObject.has("message"))
                                        {
                                            message = postObject.getString("message");
                                        }
                                        else if(postObject.has("name"))
                                        {
                                            if(postObject.getString("name").contains("cover photo"))
                                            {
                                                message = pageName + " has changed its cover photo";
                                            }
                                            else if(postObject.getString("name").contains(pageName))
                                            {
                                                message = pageName + " has changed profile picture";
                                            }
                                        }

                                        if(postObject.has("picture"))
                                        {
                                            String pictureUrl = postObject.getString("picture");
                                            postImageByteArray = new PostPicTask(pictureUrl).execute().get();
                                        }

                                        PostFragment postFragment = PostFragment.newInstance(pageId, pageName, message, postImageByteArray);
                                        ft.add(R.id.post_fragment_container, postFragment);
                                    }

                                    ft.commit();
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "insights.metric(post_impressions){values},message,name,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class PostPicTask extends AsyncTask<Void, Void, byte[]> {
        private String urlString;

        PostPicTask(String url)
        {
            this.urlString = url;
        }

        @Override
        protected byte[] doInBackground(Void... voids) {
            URL url;
            Bitmap profilePic = null;
            try {
                url = new URL(urlString);
                profilePic = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                profilePic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                return stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

}
