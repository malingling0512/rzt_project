package com.mll.mll_project;

import android.app.Application;
import android.util.Log;

import com.wave.rztcore.ComponentsInitor;


public class MyApp extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        //在Application首先调用这面这句进行库初始化
        ComponentsInitor.init(this, "Initor.txt");

        Log.i("TAG", "MyApplication");

    }






}
