package com.example.kefu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.cec.CECHelper;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.Callback;

import static com.example.kefu.App.IMID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText name = findViewById(R.id.name);
        EditText pwd = findViewById(R.id.pwd);
        /*注册模式分两种，开放注册和授权注册。只有开放注册时，才可以客户端注册。
            1，开放注册是为了测试使用，正式环境中不推荐使用该方式注册环信账号；
            2，授权注册的流程应该是您服务器通过环信提供的 REST API 注册，之后保存到您的服务器或返回给客户端。
            建议使用授权模式，即在服务端注册，而不要放到APP中，可以在登录自己APP时从返回的结果中获取环信账号再登录环信服务器。
       这里为了测试方便在APP内注册客服用户名和密码，正式环境一般在服务器端注册好后返回给客户端*/
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText().toString().trim()) || TextUtils.isEmpty(pwd.getText().toString().trim())) {
                    Toast.makeText(MainActivity.this, "账号或密码为空", Toast.LENGTH_LONG).show();
                }
                //注册客服
                ChatClient.getInstance().register(name.getText().toString().trim(), pwd.getText().toString().trim(), new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Test=", "注册成功");
                    }

                    @Override
                    public void onError(int code, String error) {
                        Log.d("Test=", "注册失败" + code + error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });

            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录并进入到客服聊天页面
                CECHelper.getInstance().login(MainActivity.this, IMID, name.getText().toString().trim(), pwd.getText().toString().trim());
            }
        });


    }
}