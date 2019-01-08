package dev.salgino.gasapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.androidnetworking.AndroidNetworking;

import dev.salgino.gasapp.banner.BannerService;
import ss.com.bannerslider.Slider;

/**
 * Created by ELTE on 11/2/2017.
 */

public class MyApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
        AndroidNetworking.initialize(getApplicationContext());
        Slider.init(new BannerService(getApplicationContext()));

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static Context getContext(){
        return MyApp.context;
    }

}
