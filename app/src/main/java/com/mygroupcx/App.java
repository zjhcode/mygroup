package com.mygroupcx;

import android.app.Application;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DBHelper.INSTANCE.init(this);
    }
}
