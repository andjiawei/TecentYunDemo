package com.example.jiawei.imdemo.customview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiawei.imdemo.R;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFileElem;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;

import java.io.File;
import java.util.List;

import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSLoginHelper;
import utils.FileUtils;

public class ChatActivity extends AppCompatActivity {

    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE =100 ;
    private TextView tv_account;
    private String account;
    private TextView message;
    private Context context = this;

    private final String tag = "ChatActivity";

    private TLSLoginHelper loginHelper;
    private TLSAccountHelper accountHelper;
    private EditText et_username;
    private TIMConversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initBundle();
        initData();
//        sendMessage();
        receiveMessage();
    }

    //点击button打开图片
    public void sendImage(View v) {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    public void send(View view) {
        sendMessage();
    }

    private void receiveMessage() {

        //2 新消息通知
        //设置消息监听器，收到新消息时，通过此监听器回调
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                Toast.makeText(context, "接收到send消息" + list.get(0).getSender(), Toast.LENGTH_SHORT).show();

                for (TIMMessage msg : list) {
                    for (int i = 0; i < msg.getElementCount(); ++i) {
                        dealMessage(msg, i);
                    }
                }

                return false;
            }//消息监听器

//            @Override
//            public boolean onNewMessage(List msgs) {//收到新消息
//                //消息的内容解析请参考 4.5 消息解析
//                return true; //返回true将终止回调链，不再调用下一个新消息监听器
//            }
        });
    }

    private void dealMessage(TIMMessage msg, int i) {
        TIMElem elem = msg.getElement(i);
        //获取当前元素的类型
        TIMElemType elemType = elem.getType();

        if (elemType == TIMElemType.Text) {
            //处理文本消息
            TIMTextElem element = (TIMTextElem) msg.getElement(i);
            String text = element.getText();
            Toast.makeText(ChatActivity.this, "text" + text, Toast.LENGTH_SHORT).show();
        } else if (elemType == TIMElemType.Image) {
            //demo中在此类封装ImageMessage
            //处理图片消息
            TIMImageElem element = (TIMImageElem) msg.getElement(i);
            for(TIMImage image : element.getImageList()) {

                //获取图片类型, 大小, 宽高
                Log.e(tag, "image type: " + image.getType() +
                        " image size " + image.getSize() +
                        " image height " + image.getHeight() +
                        " image width " + image.getWidth());

                image.getImage(new TIMValueCallBack<byte[]>() {
                    @Override
                    public void onError(int code, String desc) {//获取图片失败
                        //错误码code和错误描述desc，可用于定位请求失败原因
                        //错误码code含义请参见错误码表
                        Log.e(tag, "getImage failed. code: " + code + " errmsg: " + desc);
                    }

                    @Override
                    public void onSuccess(byte[] data) {//成功，参数为图片数据
                        //doSomething
                        Log.e(tag, "getImage success. data size: " + data.length);
                    }
                });
            }

        }else if(elemType == TIMElemType.File){
            //文档居然不给示例了！！！
            TIMFileElem element = (TIMFileElem) msg.getElement(i);
            element.getFile(new TIMValueCallBack<byte[]>() {
                @Override
                public void onError(int i, String s) {
                    Log.e(tag, "getFile failed. i: " + i + " s: " + s);
                }

                @Override
                public void onSuccess(byte[] bytes) {
                    Log.e(tag, "getFile success. data size: " + bytes.length);
                }
            });
        }
    }

    private void sendMessage() {

        //获取单聊会话
//        String peer = "zhangjiawei";  //获取与用户 "sample_user_1" 的会话

        initConversation();

        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText("a new msg to sssssssssssss");

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            Log.d(tag, "addElement failed");
            return;
        }

        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.d(tag, "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功

                Toast.makeText(ChatActivity.this, "SendMsg ok", Toast.LENGTH_SHORT).show();
                Log.e(tag, "SendMsg ok");
            }
        });

    }

    private void initConversation() {
        String peer = et_username.getText().toString().trim();

        //会话类型：单聊
//会话对方用户帐号//对方id
        conversation = TIMManager.getInstance().getConversation(
                TIMConversationType.C2C,    //会话类型：单聊
                peer);
    }

    private void initData() {
        tv_account.setText(account);
    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("account")) {
                account = bundle.getString("account");

            }
        }
    }

    private void initView() {
        tv_account = (TextView) findViewById(R.id.account);
        message = (TextView) findViewById(R.id.message);
        et_username = (EditText) findViewById(R.id.et_username);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK) {
                sendImage(FileUtils.getImageFilePath(this, data.getData()));
            }
        }else if(requestCode == FILE_CODE){
           Log.e(tag, "onActivityResult: "+data.getData() );
           sendFile(FileUtils.getFilePath(this, data.getData()));
       }else if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
           sendImage(fileUri.getPath());
       }
    }

    private void sendFile(String path) {

        if (path == null) return;
        File file = new File(path);
        if (file.exists()){
            if (file.length() > 1024 * 1024 * 10){
                Toast.makeText(this, "文件太大",Toast.LENGTH_SHORT).show();
            }else{
                sendFileConversation(path);
            }
        }else{
            Toast.makeText(this, "文件不存在",Toast.LENGTH_SHORT).show();
        }

    }

    private void sendFileConversation(String path) {
        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加文件内容
        TIMFileElem elem = new TIMFileElem();
        elem.setPath(path); //设置文件路径
        elem.setFileName(path.substring(path.lastIndexOf("/")+1)); //设置消息展示用的文件名称

        //将elem添加到消息
        if(msg.addElement(elem) != 0) {
            Log.d(tag, "addElement failed");
            return;
        }
        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                Log.d(tag, "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e(tag, "SendMsg ok");
            }
        });
    }

    private void sendImage(String path) {
        if (path == null) return;
        File file = new File(path);
        if (file.exists() && file.length() > 0){
            if (file.length() > 1024 * 1024 * 10){
                Toast.makeText(this, "文件太大",Toast.LENGTH_SHORT).show();
            }else{
                sendImageMeg(path);
            }
        }else{
            Toast.makeText(this, "文件不存在",Toast.LENGTH_SHORT).show();
        }


    }

    private void sendImageMeg(String path) {
        initConversation();
        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加图片
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(path);

        //将elem添加到消息
        if(msg.addElement(elem) != 0) {
            Log.d(tag, "addElement failed");
            return;
        }

        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                Log.d(tag, "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                Log.e(tag, "SendMsg ok");
            }
        });

    }

    public void sendFile(View v){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT < 19) {
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, FILE_CODE);
    }
    private Uri fileUri;
    public void photo(View v){
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtils.getTempFile(FileUtils.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

}
