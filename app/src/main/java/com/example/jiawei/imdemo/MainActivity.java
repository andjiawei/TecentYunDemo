package com.example.jiawei.imdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tencent.TIMCallBack;
import com.tencent.TIMConnListener;
import com.tencent.TIMLogListener;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMUser;
import com.tencent.TIMUserStatusListener;

import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private static final String TAG = "MainActivity";
    private Context context=this;
    private static final String sdkAppId="1400010712";
    private static final String accountType="5645";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListener();//第一步 初始化文档说的几个监听器


    }

    public void login(View v){
        //sdkAppId 1400010712
        // accountType 和 sdkAppId 通讯云管理平台分配
        // identifier为用户名，userSig 为用户登录凭证
        // appidAt3rd 在私有帐号情况下，填写与sdkAppId 一样

        TIMUser user = new TIMUser();
        user.setAccountType(accountType);
        user.setAppIdAt3rd(sdkAppId);
        user.setIdentifier("jiawei");
//        final String userSig = TLSHelper.getInstance().getUserSig("jiawei");
        String userSig="eJx1jkFPgzAYhu-8ioYrxtBJC1viYZhqFoZEdM55aTpa5GOCWIpjGv*7GS6Ri*-1eZLn-bIQQvbD8v5cZNlbVxtuDo2y0QzZhHrEPvvjTQOSC8MvtBw49lzXxa6PJyNL9Q1oxUVulP61iE8D97iRBVLVBnI4OSWIvYIRb*WOD73-Qy28DDBmq6tFmDaO977dFgXTkx5LJwrTSLIsWUO4etbdE9uVadh9BHExBzY3*k4dblmy3Ih24YmocsqbpM4J6QFf*68Yb-z11H-8rNL4cpQ0UKnTIUqmlHo0sK1v6wf9JlgM";

        //发起登录请求
        TIMManager.getInstance().login(
                Integer.valueOf(sdkAppId),                   //sdkAppId，由腾讯分配
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                new TIMCallBack() {//回调接口

                    @Override
                    public void onSuccess() {//登录成功
                        Log.d("-------------", "login succ");
                    }

                    @Override
                    public void onError(int code, String desc) {//登录失败

                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        Log.d("-------------", "login failed. code: " + code + " errmsg: " + desc);
                    }
                });
    }

    public void loginOut(View v){
        //登出
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {

                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.d(TAG, "-------------logout failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess() {
                //登出成功
                Log.d(TAG, "-------------logout onSuccess. code: " );
            }
        });
    }


    private void initListener() {

        //2 新消息通知
        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                return false;
            }//消息监听器

//            @Override
//            public boolean onNewMessage(List msgs) {//收到新消息
//                //消息的内容解析请参考 4.5 消息解析
//                return true; //返回true将终止回调链，不再调用下一个新消息监听器
//            }
        });

        //3 网络事件通知
        //设置网络连接监听器，连接建立／断开时回调
        TIMManager.getInstance().setConnectionListener(new TIMConnListener() {//连接监听器
            @Override
            public void onConnected() {//连接建立
                Log.e(TAG, "connected");
            }

            @Override
            public void onDisconnected(int code, String desc) {//连接断开
                //接口返回了错误码code和错误描述desc，可用于定位连接断开原因
                //错误码code含义请参见错误码表
                Log.e(TAG, "disconnected");
            }

            @Override
            public void onWifiNeedAuth(String s) {

            }
        });

        //4 日志事件
        TIMManager.getInstance().setLogListener(new TIMLogListener() {
            @Override
            public void log(int level, String tag, String msg) {
                //可以通过此回调将sdk的log输出到自己的日志系统中
            }
        });

        //5 用户状态变更
        TIMManager.getInstance().setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                //被踢下线
            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要换票后重新登录
            }
        });
        //中间几部不用 最后直接初始化sdk
        TIMManager.getInstance().init(getApplicationContext());//.so文件找不到 jni调用进坑了
    }//初始化一堆东西
    private void init() {
        /*SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        InitBusiness.start(getApplicationContext(),loglvl);
        TlsBusiness.init(getApplicationContext());
        String id =  TLSService.getInstance().getLastUserIdentifier();
        UserInfo.getInstance().setId(id);
        UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));*/
    }//不知道干嘛 先放着
}
