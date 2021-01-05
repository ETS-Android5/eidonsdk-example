package com.cyberneid.eidonsdk.example;

import android.app.Application;

import com.cyberneid.eidon.sdk.EIDONSDK;

public class MainApplication extends Application {

    public static final String clientid = "ee943f5efd3c6eaeb53180308d1c174e1d87e8a6502717a8ce388e9179b6930a";
    public static final String secret = "f2f15b22b980c1b3e2a6e0f492139cd410181bfb12e90797563e0768a491fb78";

    @Override
    public void onCreate() {
        super.onCreate();

        EIDONSDK.init(this, clientid, secret);
    }
}
