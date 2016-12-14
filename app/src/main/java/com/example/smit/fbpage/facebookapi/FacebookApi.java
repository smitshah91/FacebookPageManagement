package com.example.smit.fbpage.facebookapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.example.smit.fbpage.model.Page;
import com.example.smit.fbpage.model.Post;
import com.example.smit.fbpage.model.Profile;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FacebookApi {

    public static Profile GetMyProfile(AccessToken token)
    {
        final Profile myProfile = new Profile();
        GraphRequest meRequest = GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            myProfile.setProfileName(object.optString("name"));
                            myProfile.setProfileId(object.optString("id"));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }
                });


        Bundle meRequestParameters = new Bundle();
        meRequestParameters.putString("fields", "id,name,email,location,picture");
        meRequest.setParameters(meRequestParameters);
        meRequest.executeAndWait();

        return myProfile;
    }

    public static List<Page> GetPageList(AccessToken token)
    {
        final List<Page> pages = new ArrayList<>();

        Bundle pageRequestParameters = new Bundle();
        pageRequestParameters.putString("fields", "id,name,category,picture,access_token");

        GraphRequest pagesRequest = new GraphRequest(token,"me/accounts",
                pageRequestParameters, HttpMethod.GET, new GraphRequest.Callback()
        {
            @Override
            public void onCompleted(GraphResponse response) {

                JSONObject object = response.getJSONObject();
                if(object != null)
                {
                    try {
                        JSONArray pageArray =object.getJSONArray("data");
                        if(pageArray != null)
                        {
                            for(int i=0; i<pageArray.length(); i++)
                            {
                                JSONObject jsonObject = pageArray.getJSONObject(i);
                                pages.add(new Page(jsonObject.optString("id"), jsonObject.optString("name"), jsonObject.optString("access_token")));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        pagesRequest.setParameters(pageRequestParameters);
        pagesRequest.executeAndWait();

        return pages;
    }

    public static boolean postTextStatus(AccessToken token, String path, JSONObject data)
    {
        final boolean[] result = {false};
        GraphRequest.newPostRequest(
                token,
                path, data,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if(response.getError() == null)
                        {
                            result[0] = true;
                        }
                    }
                }).executeAndWait();

        return result[0];
    }

    public static List<Post> getPagePosts(String graphPath, final String pageName, final String pageId)
    {
        final List<Post> postLists = new ArrayList<>();

        GraphRequest request = GraphRequest.newGraphPathRequest(
                AccessToken.getCurrentAccessToken(),
                graphPath,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject object = response.getJSONObject();
                        if(object != null)
                        {
                            try
                            {
                                JSONArray posts = object.getJSONArray("data");
                                if(posts != null)
                                {
                                    for (int i=0; i<posts.length(); i++)
                                    {
                                        JSONObject postObject = posts.getJSONObject(i);
                                        String message = null;
                                        Bitmap postImage = null;

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
                                            URL url = new URL(pictureUrl);
                                            postImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        }

                                        postLists.add(new Post(pageId, pageName, message, postImage));
                                    }

                                }
                            }
                            catch (JSONException | IOException e)
                            {
                                e.printStackTrace();
                            }
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "insights.metric(post_impressions){values},message,name,picture");
        request.setParameters(parameters);
        request.executeAndWait();

        return postLists;
    }
}
