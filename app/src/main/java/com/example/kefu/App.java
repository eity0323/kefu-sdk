package com.example.kefu;

import android.app.Application;

import com.easemob.cec.CECHelper;
import com.hyphenate.chat.ChatClient;

/**
 * 描述：
 */
public class App extends Application {

    private static final String Appkey = "1434201009092679#kefuchannelapp86375";
    private static final String TenantId = "86375";
    public static final String IMID = "kefuchannelimid_328956";

    @Override
    public void onCreate() {
        super.onCreate();

        ChatClient.Options options = new ChatClient.Options();
        //必填项，appkey获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“AppKey”
        options.setAppkey(Appkey);
        //必填项，tenantId获取地址：kefu.easemob.com，“管理员模式 > 设置 > 企业信息”页面的“租户ID”
        options.setTenantId(TenantId);
        options.showAgentInputState().showVisitorWaitCount().showMessagePredict();
        //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
        options.setConsoleLog(true);
        //options.setKefuRestServer("https://sandbox.kefu.easemob.com");
//	    options.setUse2channel(true);
//        options.setAutoLogin(false);
//        options.setAppVersion("1.2.4");

        CECHelper.getInstance().init(this,options);
    }
}
