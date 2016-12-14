package com.example.smit.fbpage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smit.fbpage.facebookapi.FacebookApi;
import com.example.smit.fbpage.model.Post;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
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
                postButton.setVisibility(View.GONE);
                postLayout.setVisibility(View.VISIBLE);
                postTextView.requestFocus();
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
                try {
                    String postAsPagePath = pageId + "/feed";
                    if((postTextView.getText() != null) && (!postTextView.getText().equals("")))
                    {
                        JSONObject object = new JSONObject("{\"message\":\""+ postTextView.getText() +"\"}");
                        if(new PostStatus(pageAccessToken, postAsPagePath, object).execute().get()) {
                            startSelfActivity(pageId, pageName, pageToken);
                        }
                    }
                } catch (JSONException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }


            }
        });

        pageNameTextView.setText(pageName);
        pageDetailProfilePicture.setProfileId(pageId);

        String graphPath = pageId + "/promotable_posts";
        try {
            List<Post> postList = new PostListTask(graphPath, pageName, pageId).execute().get();

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            for (Post post:postList)
            {
                PostFragment postFragment = PostFragment.newInstance(pageId, pageName, post.getMessage(), post.getPostImage());
                ft.add(R.id.post_fragment_container, postFragment);
            }

            ft.commit();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        /*GraphRequest request = GraphRequest.newGraphPathRequest(
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
                            catch (JSONException | ExecutionException | InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "insights.metric(post_impressions){values},message,name,picture");
        request.setParameters(parameters);
        request.executeAsync();*/
    }

    public class PostStatus extends AsyncTask<Void, Void, Boolean> {
        private AccessToken token;
        private String pagePath;
        private JSONObject jsonObject;
        PostStatus(AccessToken accessToken, String path, JSONObject object)
        {
            token = accessToken;
            pagePath = path;
            jsonObject = object;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            return FacebookApi.postTextStatus(token, pagePath, jsonObject);
        }
    }

    private void startSelfActivity(String id, String name, String token) {
        Intent selfIntent = new Intent(PageDetailActivity.this, PageDetailActivity.class);
        selfIntent.putExtra("pageID", id);
        selfIntent.putExtra("pageName", name);
        selfIntent.putExtra("pageToken", token);
        startActivity(selfIntent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class PostListTask extends AsyncTask<Void, Void, List<Post>> {
        private String graphPath, pageName, pageId;

        PostListTask(String path, String name, String id)
        {
            this.graphPath= path;
            this.pageName = name;
            this.pageId = id;
        }

        @Override
        protected List<Post> doInBackground(Void... voids) {
            return FacebookApi.getPagePosts(graphPath, pageName, pageId);
        }
    }

}
