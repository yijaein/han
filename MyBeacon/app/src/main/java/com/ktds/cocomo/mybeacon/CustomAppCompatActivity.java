package com.ktds.cocomo.mybeacon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.util.UUID;

/**
 * Created by COCOMO on 2016-06-24.
 */
public class CustomAppCompatActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beaconManager = new BeaconManager(this);

        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-Aff9-25556B57FE6D"),  44056,41473);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }

    @Override
    protected void onPause() {

        beaconManager.stopRanging(region);

        super.onPause();
    }
}
