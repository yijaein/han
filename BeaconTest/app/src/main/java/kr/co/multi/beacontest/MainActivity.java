package kr.co.multi.beacontest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.multi.beacontest.control.SessionManager;
import kr.co.multi.beacontest.model.SQLiteHandler;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private int NUM_PAGES = 3;
    Button btn;

    /* Fragment numbering */
    public final static int FRAGMENT_PAGE1 = 0;
    public final static int FRAGMENT_PAGE2 = 1;
    public final static int FRAGMENT_PAGE3 = 2;
    TextView te;

    ViewPager mViewPager;

    LinearLayout page1Btn, page2Btn, page3Btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
                getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.maintitle);
        btn = (Button)findViewById(R.id.btn);
        te = (TextView)findViewById(R.id.maintitletext);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        mViewPager.setCurrentItem(FRAGMENT_PAGE1);
        page1Btn = (LinearLayout) findViewById(R.id.tap_home);
        page1Btn.setOnClickListener(this);
        page2Btn = (LinearLayout) findViewById(R.id.tap_mypage);
        page2Btn.setOnClickListener(this);
        page3Btn = (LinearLayout) findViewById(R.id.tap_settings);
        page3Btn.setOnClickListener(this);
        btn.setOnClickListener(this);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                page1Btn.setSelected(false);// 버튼 누를 때 상태 변환
                page2Btn.setSelected(false);
                page3Btn.setSelected(false);

                switch(position){
                    case 0:
                        te.setText("ClimbMount");
                        page1Btn.setSelected(true);
                        break;
                    case 1:
                        te.setText("MyPage");
                        page2Btn.setSelected(true);
                        break;
                    case 2:
                        te.setText("Setting");
                        page3Btn.setSelected(true);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

        page1Btn.setSelected(true);
    }


    private class pagerAdapter extends FragmentPagerAdapter {

        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return new HomeActivity();
                case 1:
                    return new MyPageActivity();
                case 2:
                    return new SettingsActivity();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return NUM_PAGES;
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.tap_home:
                mViewPager.setCurrentItem(FRAGMENT_PAGE1);
                break;
            case R.id.tap_mypage:
                mViewPager.setCurrentItem(FRAGMENT_PAGE2);
                break;
            case R.id.tap_settings:
                mViewPager.setCurrentItem(FRAGMENT_PAGE3);
                break;
            case R.id.btn:
                Intent intent = new Intent(MainActivity.this, BeaconListActivity.class);
                startActivity(intent);
        }
    }

}
