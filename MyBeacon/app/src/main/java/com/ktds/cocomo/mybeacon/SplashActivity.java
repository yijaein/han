package com.ktds.cocomo.mybeacon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final String executeType = getIntent().getStringExtra("executeType");

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = null;

                // 비콘에의해 실행될 때와 아닐때 이동될 Activity를 다르게 줄 수 있습니다.
                if(executeType != null && executeType.equals("beacon")) {
                    intent = new Intent(SplashActivity.this, CouponActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
