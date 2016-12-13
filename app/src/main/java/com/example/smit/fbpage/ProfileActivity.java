package com.example.smit.fbpage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements PageFragment.OnFragmentInteractionListener {

    TextView nameTextView;
    ImageView profilePicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle data = getIntent().getExtras();
        String name = data.getString("name", null);
        byte[] byteArray = data.getByteArray("profilePic");
        String[] pageNames = data.getStringArray("pageName");
        String[] pageIds = data.getStringArray("pageId");
        String[] pageTokens = data.getStringArray("pageToken");

        System.out.println();
        Bitmap profilePic = null;
        if(byteArray != null)
        {
            profilePic = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        }

        nameTextView = (TextView) findViewById(R.id.textName);
        profilePicView = (ImageView) findViewById(R.id.imageProfilePic);

        nameTextView.setText(name);
        profilePicView.setImageBitmap(profilePic);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        for(int i=0; i<pageIds.length; i++)
        {
            System.out.println(pageIds[i]);
            PageFragment pageFragment = PageFragment.newInstance(pageIds[i], pageNames[i], pageTokens[i]);
            ft.add(R.id.fragment_container, pageFragment);
        }

        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        System.out.println("OnFragmentInteraction");
    }
}
