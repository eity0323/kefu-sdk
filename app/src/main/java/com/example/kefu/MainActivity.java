package com.example.kefu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.easemob.cec.CECHelper;

import static com.example.kefu.App.IMID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CECHelper.getInstance().login(MainActivity.this,IMID,"1p978p410n21295","123456");
            }
        });
    }
}