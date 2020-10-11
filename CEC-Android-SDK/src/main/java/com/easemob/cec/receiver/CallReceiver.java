package com.easemob.cec.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.easemob.cec.ui.CallActivity;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.util.EMLog;


/**
 * Created by liyuzhao on 11/01/2017.
 *
 * 监听呼入通话
 * 实时通话分为视频通话和音频通话，与普通电话不同，它是基于网络的。
 * 通过注册相应 action 的 BroadcastReceiver 来监听呼叫过来的通话，接到广播后开发者可以调起 APP 里的通话 Activity。
 */

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ChatClient.getInstance().isLoggedInBefore()){
            return;
        }
        //username
        String from = intent.getStringExtra("from");
        //call type
        String type = intent.getStringExtra("type");
        //call to
        String to = intent.getStringExtra("to");

        if ("video".equals(type)){// video call
            context.startActivity(new Intent(context, CallActivity.class)
            .putExtra("username", from).putExtra("isComingCall", true)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        EMLog.d("callreceiver", "app receiver");
    }
}
