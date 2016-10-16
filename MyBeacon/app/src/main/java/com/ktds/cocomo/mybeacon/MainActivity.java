package com.ktds.cocomo.mybeacon;

import android.os.Bundle;

import com.estimote.sdk.SystemRequirementsChecker;

public class MainActivity extends CustomAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 블루투스 권한 및 활성화 코드드
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
}
