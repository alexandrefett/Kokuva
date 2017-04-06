package com.kokuva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.kokuva.adapter.ViewPagerAdapter;

public class PhotoActivity extends BaseActivity {
    // LogCat tag
    private static final String TAG = "--->>>";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_photos);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);

        ImageButton cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle b = new Bundle();
        b.putBoolean("flag", false);
        adapter.addFragment(new FragmentPhoto(), "Foto");
        adapter.addFragment(new FragmentGallery(), "Galeria");
        adapter.addFragment(new FragmentFacebook(), "Facebook");
        viewPager.setAdapter(adapter);
        Log.d(TAG,"Tab count: "+tabLayout.getTabCount());
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_action_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_picture);
        tabLayout.getTabAt(2).setIcon(R.drawable.com_facebook_button_icon);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}