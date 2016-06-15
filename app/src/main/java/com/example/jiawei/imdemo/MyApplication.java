package com.example.jiawei.imdemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by zjw on 2016/6/14.
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
//        Foreground.init(this);//腾讯demo有这个类 暂时没用
        context = getApplicationContext();
       /* if(MsfSdkUtils.isMainProcess(this)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
                }
            });
        }*/
    }

    public static Context getContext() {
        return context;
    }

}
