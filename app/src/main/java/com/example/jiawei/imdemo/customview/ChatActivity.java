package com.example.jiawei.imdemo.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.jiawei.imdemo.R;

public class ChatActivity extends AppCompatActivity {

    private TextView tv_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tv_account = (TextView) findViewById(R.id.account);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey("account")){
                String account = bundle.getString("account");
                tv_account.setText(account);
            }
        }






    }
}
