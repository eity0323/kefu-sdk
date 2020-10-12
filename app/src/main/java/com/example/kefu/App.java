package com.example.kefu;

import android.app.Application;

import com.easemob.cec.CECHelper;
import com.hyphenate.chat.ChatClient;

/**
 * 描述：
 */
public class App extends Application {

    //appkey 获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“AppKey”
    private static final String Appkey = "1434201009092679#kefuchannelapp86375";
    //租户ID 获取地址：kefu.easemob.com，“管理员模式 > 设置 > 企业信息”页面的“租户ID”
    private static final String TenantId = "86375";
    //IM服务号 获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“IM服务号”
    public static final String IMID = "kefuchannelimid_328956";
    //留言ID  进入“管理员模式 → 留言”，可以看到这个Project ID
    public static final String ProjectId = "4098193";

    @Override
    public void onCreate() {
        super.onCreate();

        CECHelper.getInstance().init(this,
                new ChatClient.Options().setAppkey(Appkey)
                        .setTenantId(TenantId));
        /*
        ChatClient.Options()参数设置说明：
        //必填项，appkey获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“AppKey”
        options.setAppkey(Appkey);
        //必填项，tenantId获取地址：kefu.easemob.com，“管理员模式 > 设置 > 企业信息”页面的“租户ID”
        options.setTenantId(TenantId);
        //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
        options.setConsoleLog(true);
        options.showAgentInputState().showVisitorWaitCount().showMessagePredict();
        options.setUse2channel(true);
        options.setAutoLogin(false);
        options.setAppVersion("1.2.4");
        //私有部署场景下，需要设置以下IP地址和端口：
        options.setKefuServerAddress("https://kefu.easemob.com");  //KefuServer
        options.setRestServer("a1.easemob.com");  //RestServer
        options.setIMServer("msync-im1.easemob.com"); //ChatServer
        options.setIMPort(443); //ChatPort
        */
    }
}
