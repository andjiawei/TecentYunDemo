package com.example.jiawei.imdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.jiawei.imdemo.customview.ChatActivity;
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
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSPwdResetListener;
import tencent.tls.platform.TLSSmsLoginListener;
import tencent.tls.platform.TLSStrAccRegListener;
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

    //------------------密码重置-------------------------------------
//    TLSAccountHelper accountHelper;
    TLSPwdResetListener pwdResetListener;


    //------------------短信登录----------------------------------------------
    TLSLoginHelper loginHelper;
    TLSSmsLoginListener smsLoginListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userSig= getString(R.string.user_key);
        initView();
        initAccountReg();//初始化密码注册（字符串账号）
        initListener();//第一步 初始化文档说的几个监听器


        initSmsLoginListener();//短信登录的监听
    }

    //-----------------------------开发者初始化登录登出------------------------------

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

    //----------------------------不用登录查看聊天记录-------------------------------
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

    //--------------------------------手机号注册-----------------------------------

    public void loginup(View view){
        initLoginUp();//初始化注册的东西
        accountHelper.TLSPwdRegVerifyCode("86-"+et_phone.getText().toString().trim(), pwdRegListener);
    }//注册
    //初始化注册的东西
    private void initLoginUp() {
        initLoginUpListener();//注册的监听
        accountHelper = TLSAccountHelper.getInstance().init(getApplicationContext(), Integer.valueOf(sdkAppId),Integer.valueOf(accountType), appVer);
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

    }//注册的监听
    //获取验证码的
    public void getYanZheng(View view){
        // 手机号码请输入正确的真实的手机号码，格式是 国家码-手机号码，比如 86-186xxx
        accountHelper.TLSPwdRegAskCode("86-"+et_phone.getText().toString().trim(), pwdRegListener);
    }
    //用户输入密码后 完成登录的最后一步
    private void inputPwd() {

        String passward = et_pwd.getText().toString().trim();
        accountHelper. TLSPwdRegCommit (passward, pwdRegListener);
    }

    //-----------------重置密码（手机号注册）-----------------------------

    //重置密码的点击
    public void resetPwd(View view){
        initResetPwd();

        // 手机号码请输入正确的真实的手机号码，格式是 国家码-手机号码，比如 86-186xxx
        accountHelper.TLSPwdResetAskCode("86-"+et_phone.getText().toString().trim(), pwdResetListener);
    }

    private void initResetPwd() {
        initResetPwdListener();//重设密码的监听
    }

    private void initResetPwdListener() {

        pwdResetListener = new TLSPwdResetListener() {
            @Override
            public void OnPwdResetAskCodeSuccess(int reaskDuration, int expireDuration) {
      /* 请求下发短信成功，可以跳转到输入验证码进行校验的界面，同时可以开始倒计时, (reaskDuration 秒内不可以重发短信，如果在expireDuration 秒之后仍然没有进行短信验证，则应该回到上一步，重新开始流程)；在用户输入收到的短信验证码之后，可以调用PwdResetVerifyCode 进行验证。*/
                Log.e(TAG, "OnPwdResetAskCodeSuccess:下发短信成功 ");

            }

            @Override
            public void OnPwdResetReaskCodeSuccess(int reaskDuration, int expireDuration) {
      /* 重新请求下发短信成功，可以跳转到输入验证码进行校验的界面，并开始倒计时，(reaskDuration 秒内不可以再次请求重发，在expireDuration 秒之后仍然没有进行短信验证，则应该回到第一步，重新开始流程)；在用户输入收到的短信验证码之后，可以调用PwdResetVerifyCode 进行验证。*/
                Log.e(TAG, "OnPwdResetReaskCodeSuccess:重新请求下发短信成功 ");
            }

            @Override
            public void OnPwdResetVerifyCodeSuccess() {
   /* 短信验证成功，接下来可以引导用户输入密码，然后调用PwdResetCommit 完成重置密码流程*/
                Log.e(TAG, "OnPwdResetVerifyCodeSuccess: 短信验证成功");
                resetPwdCommit();
            }

            @Override
            public void OnPwdResetCommitSuccess(TLSUserInfo userInfo) {
      /* 重置密码成功，接下来可以引导用户进行新密码登录了，登录流程请查看相应章节*/
                Log.e(TAG, "OnPwdResetCommitSuccess: 重置密码成功");
            }

            @Override
            public void OnPwdResetFail(TLSErrInfo tlsErrInfo) {
   /* 重置密码过程中任意一步都可以到达这里，可以根据tlsErrInfo 中ErrCode, Title, Msg 给用户弹提示语，引导相关操作*/
                Log.e(TAG, "OnPwdResetFail: 任意一步"+"Msg"+tlsErrInfo.Msg+"ErrCode"+tlsErrInfo.ErrCode+"Title"+tlsErrInfo.Title);
            }

            @Override
            public void OnPwdResetTimeout(TLSErrInfo tlsErrInfo) {
      /* 重置密码过程中任意一步都可以到达这里，顾名思义，网络超时，可能是用户网络环境不稳定，一般让用户重试即可*/
                Log.e(TAG, "OnPwdResetTimeout:网络超时");
            }
        };

    }//重置密码的监听

    public void resetYanzheng(View view){

        accountHelper.TLSPwdResetVerifyCode(et_identify.getText().toString().trim(), pwdResetListener);
    }

    //提交重置密码
    private void resetPwdCommit() {
        accountHelper.TLSPwdResetCommit ("000000", pwdResetListener);

    }


    //--------------短信登录----4个方法--------------------
    //1 点击yzLogin按钮 发起登录请求
    public void yzLogin(View view){
        loginHelper = TLSLoginHelper.getInstance()
                .init(getApplicationContext(), Integer.valueOf(sdkAppId), Integer.valueOf(accountType), appVer);
        loginHelper.TLSSmsLoginAskCode("86-"+et_phone.getText().toString().trim(), smsLoginListener);

    }

    //2 点击 sms验证 的点击事件
    public void smsYanZheng(View view){

        loginHelper.TLSSmsLoginVerifyCode(et_identify.getText().toString().trim(), smsLoginListener);
    }

    //3 验证码登录监听
    private void initSmsLoginListener() {

        smsLoginListener = new  TLSSmsLoginListener() {
            @Override
            public void OnSmsLoginAskCodeSuccess(int reaskDuration, int expireDuration) {
   /* 请求下发短信成功，可以跳转到输入验证码进行校验的界面，同时可以开始倒计时, (reaskDuration 秒内不可以重发短信，如果在expireDuration 秒之后仍然没有进行短信验证，则应该回到上一步，重新开始流程)；在用户输入收到的短信验证码之后，可以调用SmsLoginVerifyCode进行验证。*/
                Log.e(TAG, "OnSmsLoginAskCodeSuccess:下发短信成功 ");
            }

            @Override
            public void OnSmsLoginReaskCodeSuccess(int reaskDuration, int expireDuration) {
   /* 重新请求下发短信成功，可以跳转到输入验证码进行校验的界面，并开始倒计时，(reaskDuration 秒内不可以再次请求重发，在expireDuration 秒之后仍然没有进行短信验证，则应该回到第一步，重新开始流程)；在用户输入收到的短信验证码之后，可以调用SmsLoginVerifyCode 进行验证。*/
                Log.e(TAG, "OnSmsLoginAskCodeSuccess:重新请求下发短信成功 ");
            }

            @Override
            public void OnSmsLoginVerifyCodeSuccess() {
   /* 这时候仅仅是通过了短信验证，还需要调用登录接口TLSSmsLogin 完成登录 */
                Log.e(TAG, "OnSmsLoginVerifyCodeSuccess:通过了短信验证 ");
                smsLogin();
            }

            @Override
            public void OnSmsLoginSuccess(TLSUserInfo userInfo) {
   /* 登录成功了，在这里可以获取用户票据*/
                Log.e(TAG, "OnSmsLoginVerifyCodeSuccess:登录成功了，在这里可以获取用户票据 ");
                String usersig = loginHelper.getUserSig(userInfo.identifier);
            }

            @Override
            public void OnSmsLoginFail(TLSErrInfo errInfo) {
   /* 短信登录过程中任意一步都可以到达这里，可以根据tlsErrInfo 中ErrCode, Title, Msg 给用户弹提示语，引导相关操作*/
                Log.e(TAG, "OnSmsLoginFail:任意一步都可以到达这里");
            }

            @Override
            public void OnSmsLoginTimeout(TLSErrInfo errInfo) {
          /* 短信登录过程中任意一步都可以到达这里，顾名思义，网络超时，可能是用户网络环境不稳定，一般让用户重试即可*/
                Log.e(TAG, "OnSmsLoginTimeout:网络超时");
            }
        };

    }
    //4 提交登录请求
    private void smsLogin() {
        loginHelper.TLSSmsLogin ("86-"+et_phone.getText().toString().trim(), smsLoginListener);
    }

    //----------------密码注册（字符串账号）-------------------------------
    //TLSAccountHelper accountHelper;
    TLSStrAccRegListener strAccRegListener;
    private void initAccountReg(){
        accountHelper = TLSAccountHelper.getInstance()
                .init(getApplicationContext(), Integer.valueOf(sdkAppId), Integer.valueOf(accountType), appVer);

        strAccRegListener = new TLSStrAccRegListener() {
            @Override
            public void OnStrAccRegSuccess(TLSUserInfo tlsUserInfo) {
                /* 成功注册了一个字符串帐号， 可以引导用户使用刚注册的用户名和密码登录 */
                Log.e(TAG, "OnStrAccRegSuccess:成功注册了一个字符串帐号 " );
            }

            @Override
            public void OnStrAccRegFail(TLSErrInfo tlsErrInfo) {
                /* 注册失败，请提示用户失败原因 */

                Log.e(TAG, "OnStrAccRegFail: 注册失败" );

            }

            @Override
            public void OnStrAccRegTimeout(TLSErrInfo tlsErrInfo) {
                /* 网络超时，可能是用户网络环境不稳定，一般让用户重试即可。*/
                Log.e(TAG, "OnStrAccRegTimeout: 网络超时" );
            }
        };


    }
    public void accountReg(View v){
        // 引导用户输入合法的用户名和密码\
        Log.e(TAG, "accountReg: 引导用户输入合法的用户名和密码" );
        int result = accountHelper.TLSStrAccReg("ruichuang", "11111111", strAccRegListener);
        if (result == TLSErrInfo.INPUT_INVALID) {
            Log.e(TAG, "result == TLSErrInfo.INPUT_INVALID" );
        // displayInfo("引导用户输入合法的用户名和密码");
        }
    }

    //-------------(自己写)用户名 密码登录---------------------------------

    //demo里引用的是lib中的TLSService类 但是sdk没有 应该是个代理类
//    TLSLoginHelper loginHelper;
    public void userLogin(View view){
        //初始化
        loginHelper = TLSLoginHelper.getInstance()
                .init(getApplicationContext(), Integer.valueOf(sdkAppId), Integer.valueOf(accountType), appVer);
        //提交登录请求
        loginHelper.TLSPwdLogin(et_phone.getText().toString().trim(), et_pwd.getText().toString().trim().getBytes(), new TLSPwdLoginListener() {
            @Override
            public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {
                Log.e(TAG, "OnPwdLoginSuccess: 登录成功" );
                //跳转到聊天界面
                navToChat(tlsUserInfo.identifier);
            }

            @Override
            public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {
                Log.e(TAG, "OnPwdLoginReaskImgcodeSuccess:图片验证成功？" );
            }

            @Override
            public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {
                Log.e(TAG, "OnPwdLoginNeedImgcode: 需要验证码？" );
            }

            @Override
            public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
                Log.e(TAG, "OnPwdLoginFail: 登录失败"+"Msg"+tlsErrInfo.Msg+ "title"+tlsErrInfo.Title);
            }

            @Override
            public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
                Log.e(TAG, "OnPwdLoginSuccess: 登录超时" );
            }
        });
    }
    //跳转到聊天界面
    private void navToChat(String identifier) {
        Intent intent=new Intent(this,ChatActivity.class);
        intent.putExtra("account",identifier);

        startActivity(intent);

    }
//----------------------------------------



    private EditText et_phone;
    private EditText et_identify;
    private EditText et_pwd;
    private void initView() {

        et_phone = (EditText) findViewById(R.id.phoneNumber_hostLogin);//手机号输入框
        et_identify = (EditText) findViewById(R.id.checkCode_hostLogin);//验证码输入框
        et_pwd = (EditText) findViewById(R.id.et_pwd);//密码输入框

    }//初始化View
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
