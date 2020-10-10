package com.example.kefu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.easemob.cec.CECHelper;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.Callback;

import static com.example.kefu.App.IMID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatClient.getInstance().register("xiao", "123456", new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Test=","注册成功");
                    }

                    @Override
                    public void onError(int code, String error) {
                        Log.d("Test=","注册失败"+code+error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
                CECHelper.getInstance().login(MainActivity.this,IMID,"xiao","123456");
            }
        });
    }
}