package com.suma.coffeeshopmvpassignment.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

//singleton CoffeShopApp class
public class CoffeeShopApp extends Application {

    private static CoffeeShopApp app;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);

    }

    public static CoffeeShopApp getApp() {
        return app;
    }

    public static Context getAppContext() {
        return context;
    }

}
