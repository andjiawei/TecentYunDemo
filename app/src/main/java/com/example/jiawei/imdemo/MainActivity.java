package com.example.jiawei.imdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.tencent.TIMCallBack;
import com.tencent.TIMConnListener;
import com.tencent.TIMLogListener;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMUser;
import com.tencent.TIMUserStatusListener;

import java.util.List;

import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSUserInfo;

public class MainActivity extends AppCompatActivity {

    //--------------登录帐号需要的东西------------------------

    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private static final String TAG = "MainActivity";
    private Context context = this;
    private static final String sdkAppId = "1400010712";
    private static final String accountType = "5645";
    private static final String appIdAt3rd = accountType;
    private static final String Identifier = "jiawei";
    private  String userSig ;


    //----------------注册需要的东西---------------------------
    TLSAccountHelper accountHelper;
    TLSPwdRegListener pwdRegListener;//sdk文档是错的
    String appVer="1.0";
    private EditText et_phone;
    private EditText et_identify;
    private EditText pwd;

    //------------------密码重置-------------------------------------
//    TLSAccountHelper accountHelper;
    TLSPwdResetListener pwdResetListener;


    //----------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userSig= getString(R.string.user_key);
        initView();
        initListener();//第一步 初始化文档说的几个监听器
        initLoginUp();//初始化注册的东西
        initLoginUpListener();
    }

    private void initLoginUpListener() {

        pwdRegListener = new TLSPwdRegListener() {
            @Override
            public void OnPwdRegAskCodeSuccess(int reaskDuration, int expireDuration) {
      /* 请求下发短信成功，可以跳转到输入验证码进行校验的界面，同时可以开始倒计时, (reaskDuration 秒内不可以重发短信，如果在expireDuration 秒之后仍然没有进行短信验证，则应该回到上一步，重新开始流程)；在用户输入收到的短信验证码之后，可以调用PwdRegVerifyCode 进行验证。*/
                Log.e(TAG, "OnPwdRegAskCodeSuccess: reaskDuration"+reaskDuration+"expireDuration:"+expireDuration );
            }

            @Override
            public void OnPwdRegReaskCodeSuccess(int reaskDuration, int expireDuration) {
      /* 重新请求下发短信成功，可以跳转到输入验证码进行校验的界面，并开始倒计时，(reaskDuration 秒内不可以再次请求重发，在expireDuration 秒之后仍然没有进行短信验证，则应该回到第一步，重新开始流程)；在用户输入收到的短信验证码之后，可以调用PwdRegVerifyCode 进行验证。*/
            }

            @Override
            public void OnPwdRegVerifyCodeSuccess() {
   	/* 短信验证成功，接下来可以引导用户输入密码，然后调用PwdRegCommit 进行注册的最后一步*/
                Log.e(TAG, "OnPwdRegVerifyCodeSuccess");
                inputPwd();//拿到输入框的密码 提交
            }

            @Override
            public void OnPwdRegCommitSuccess(TLSUserInfo userInfo) {
      /* 最终注册成功，接下来可以引导用户进行密码登录了，登录流程请查看相应章节*/
                Log.e(TAG, "OnPwdRegCommitSuccessOnPwdRegCommitSuccessOnPwdRegCommitSuccess");
            }

            @Override
            public void OnPwdRegFail(TLSErrInfo tlsErrInfo) {
       /* 密码注册过程中任意一步都可以到达这里，可以根据tlsErrInfo 中ErrCode, Title, Msg 给用户弹提示语，引导相关操作*/
            }

            @Override
            public void OnPwdRegTimeout(TLSErrInfo tlsErrInfo) {
      /* 密码注册过程中任意一步都可以到达这里，顾名思义，网络超时，可能是用户网络环境不稳定，一般让用户重试即可*/
            }
        };

    }

    //用户输入密码后 完成登录的最后一步
    private void inputPwd() {

        String passward = pwd.getText().toString().trim();
        accountHelper. TLSPwdRegCommit (passward, pwdRegListener);
    }

    private void initView() {

        et_phone = (EditText) findViewById(R.id.phoneNumber_hostLogin);//手机号输入框
        et_identify = (EditText) findViewById(R.id.checkCode_hostLogin);//验证码输入框
        pwd = (EditText) findViewById(R.id.pwd);//密码输入框

    }//初始化View

    public void getYanZheng(View view){
        // 手机号码请输入正确的真实的手机号码，格式是 国家码-手机号码，比如 86-186xxx
        accountHelper.TLSPwdRegAskCode("86-"+et_phone.getText().toString().trim(), pwdRegListener);
    }

    //初始化注册的东西
    private void initLoginUp() {
        accountHelper = TLSAccountHelper.getInstance().init(getApplicationContext(),  Integer.valueOf(sdkAppId),Integer.valueOf(accountType), appVer);
    }


    public void reset(View view){
        // 手机号码请输入正确的真实的手机号码，格式是 国家码-手机号码，比如 86-186xxx
        accountHelper.TLSPwdResetAskCode(et_phone.getText().toString().trim(), pwdResetListener);
    }
    public void loginup(View view){

        accountHelper.TLSPwdRegVerifyCode(et_identify.getText().toString().trim(), pwdRegListener);
    }//注册

    public void login(View v) {
        //sdkAppId 1400010712
        // accountType 和 sdkAppId 通讯云管理平台分配
        // identifier为用户名，userSig 为用户登录凭证
        // appidAt3rd 在私有帐号情况下，填写与sdkAppId 一样

        TIMUser user = new TIMUser();
        user.setAccountType(accountType);
        user.setAppIdAt3rd(appIdAt3rd);
        user.setIdentifier(Identifier);
//        final String getUser = TLSHelper.getInstance().getUserSig(Identifier);//这里得到的是""

        //就是用户凭证 下载的user_key中的文本
        String userSig = getString(R.string.user_key);
        //发起登录请求
        TIMManager.getInstance().login(
                Integer.valueOf(sdkAppId),                   //sdkAppId，由腾讯分配
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                new TIMCallBack() {//回调接口

                    @Override
                    public void onSuccess() {//登录成功

                        Log.e("-------------", "login succlogin succlogin succ");
                    }

                    @Override
                    public void onError(int code, String desc) {//登录失败

                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        Log.d("-------------", "login failed. code: " + code + " errmsg: " + desc);
                    }
                });
    }//登录

    public void loginOut(View v) {
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
                Log.d(TAG, "-------------logout onSuccess. code: ");
            }
        });
    }//登出

    public void history(View v) {

        // accountType 和 sdkAppId 通讯云管理平台分配
        // identifier为用户名，userSig 为用户登录凭证
        // appidAt3rd 在私有帐号情况下，填写与sdkAppId 一样

        TIMUser user = new TIMUser();
        user.setAccountType(accountType);
        user.setAppIdAt3rd(appIdAt3rd);
        user.setIdentifier(Identifier);

        //发起登录请求
        TIMManager.getInstance().initStorage(
                Integer.valueOf(sdkAppId),                 //sdkAppId，由腾讯分配
                user,
                userSig,                    //用户帐号签名，由私钥加密获得，具体请参考文档
                new TIMCallBack() {//回调接口

                    @Override
                    public void onSuccess() {//登录成功
                        Log.d(TAG, "init succ");
                    }

                    @Override
                    public void onError(int code, String desc) {//登录失败

                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        Log.d(TAG, "init failed. code: " + code + " errmsg: " + desc);
                    }
                });


    }//不登录 查看历史


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
