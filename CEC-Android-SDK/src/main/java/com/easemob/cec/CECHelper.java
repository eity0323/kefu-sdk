package com.easemob.cec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easemob.cec.receiver.CallReceiver;
import com.easemob.cec.ui.CallActivity;
import com.easemob.cec.ui.ChatActivity;
import com.easemob.cec.ui.LoginActivity;
import com.easemob.cec.utils.ListenerManager;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.Notifier;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.easeui.widget.ToastHelper;
import com.hyphenate.helpdesk.model.AgentInfo;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.hyphenate.helpdesk.util.Log;

import org.json.JSONObject;

import java.util.List;

public class CECHelper {

    private static final String TAG = "DemoHelper";

    public static CECHelper instance = new CECHelper();

    /**
     * kefuChat.MessageListener
     */
    protected ChatManager.MessageListener messageListener = null;

    /**
     * ChatClient.ConnectionListener
     */
    private ChatClient.ConnectionListener connectionListener;

    private UIProvider _uiProvider;

    public boolean isVideoCalling;
    private CallReceiver callReceiver;
    private Context appContext;

    private CECHelper() {
    }

    public synchronized static CECHelper getInstance() {
        return instance;
    }

    /**
     * init helper
     *
     * @param context application context
     */
    public void init(final Context context, ChatClient.Options options) {
        appContext = context;
        Preferences.init(context);
        // 环信客服 SDK 初始化, 初始化成功后再调用环信下面的内容
        if (ChatClient.getInstance().init(context, options)) {
            _uiProvider = UIProvider.getInstance();
            //初始化EaseUI
            _uiProvider.init(context);
            //调用easeui的api设置providers
            setEaseUIProvider(context);
            //设置全局监听
            setGlobalListeners();
        }
    }

    //登录环信客服 采用账号密码登录。
    public void login(final Context context, final String imid, String name, String pwd) {
        //需要先判断登录状态再进入会话界面，否则会出问题。
        //ChatClient.getInstance().isLoggedInBefore() 可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
        if (ChatClient.getInstance().isLoggedInBefore()) {
            toChatActivity(context, imid);
        } else {
            //随机创建一个用户并登录环信服务器
            // login huanxin server
            ChatClient.getInstance().login(name, pwd, new Callback() {
                @Override
                public void onSuccess() {
                    android.util.Log.d(TAG, "login success!");
                    toChatActivity(context, imid);
                }

                @Override
                public void onError(final int code, final String error) {
                    android.util.Log.e(TAG, "login fail,code:" + code + ",error:" + error);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "login fail,code:" + code + ",error:" + error, Toast.LENGTH_LONG).show();
                        }
                    });

                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        }
    }

    //登录环信客服 采用账号token登录。
    public void loginWithToken(final Context context, final String imid, String name, String token) {
        //需要先判断登录状态再进入会话界面，否则会出问题。
        //ChatClient.getInstance().isLoggedInBefore() 可以检测是否已经登录过环信，如果登录过则环信SDK会自动登录，不需要再次调用登录操作
        if (ChatClient.getInstance().isLoggedInBefore()) {
            toChatActivity(context, imid);
        } else {
            //随机创建一个用户并登录环信服务器
            // login huanxin server
            ChatClient.getInstance().loginWithToken(name, token, new Callback() {
                @Override
                public void onSuccess() {
                    android.util.Log.d(TAG, "login success!");
                    toChatActivity(context, imid);
                }

                @Override
                public void onError(final int code, final String error) {
                    android.util.Log.e(TAG, "login fail,code:" + code + ",error:" + error);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "login fail,code:" + code + ",error:" + error, Toast.LENGTH_LONG).show();
                        }
                    });

                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        }
    }

    // 进入聊天室页面
    private void toChatActivity(Context context, String IMID) {

        Intent intent = new IntentBuilder(context)
                .setTargetClass(ChatActivity.class)
                .setServiceIMNumber(IMID)//“IM服务号”需要与初始化时设置的appkey对应,获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“IM服务号”
                .setShowUserNick(true)
//                .setVisitorInfo(DemoMessageHelper.createVisitorInfo())
//                .setScheduleQueue(DemoMessageHelper.createQueueIdentity(queueName))
//                .setTitleName(titleName)
//				  .setScheduleAgent(DemoMessageHelper.createAgentIdentity("ceshiok1@qq.com"))
//                .setBundle(bundle)
                .build();
        context.startActivity(intent);
    }

    private void setEaseUIProvider(final Context context) {
        //设置头像和昵称 某些控件可能没有头像和昵称，需要注意
        UIProvider.getInstance().setUserProfileProvider(new UIProvider.UserProfileProvider() {
            @Override
            public void setNickAndAvatar(Context context, Message message, ImageView userAvatarView, TextView usernickView) {
                if (message.direct() == Message.Direct.RECEIVE) {
                    //设置接收方的昵称和头像
//                    UserUtil.setAgentNickAndAvatar(context, message, userAvatarView, usernickView);
                    AgentInfo agentInfo = MessageHelper.getAgentInfo(message);
                    if (usernickView != null) {
                        usernickView.setText(message.from());
                        if (agentInfo != null) {
                            if (!TextUtils.isEmpty(agentInfo.getNickname())) {
                                usernickView.setText(agentInfo.getNickname());
                            }
                        }
                    }
                    if (userAvatarView != null) {
                        if (agentInfo != null) {
                            if (!TextUtils.isEmpty(agentInfo.getAvatar())) {
                                String strUrl = agentInfo.getAvatar();
                                // 设置客服头像
                                if (!TextUtils.isEmpty(strUrl)) {
                                    if (!strUrl.startsWith("http")) {
                                        strUrl = "http:" + strUrl;
                                    }
                                    //正常的string路径
                                    Glide.with(context).load(strUrl).apply(RequestOptions.placeholderOf(com.hyphenate.helpdesk.R.drawable.hd_default_avatar).diskCacheStrategy(DiskCacheStrategy.ALL).circleCrop()).into(userAvatarView);
//                                    Glide.with(context).load(strUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(com.hyphenate.helpdesk.R.drawable.hd_default_avatar).transform(new GlideCircleTransform(context)).into(userAvatarView);
                                    return;
                                }
                            }
                        }
                        userAvatarView.setImageResource(com.hyphenate.helpdesk.R.drawable.hd_default_avatar);
                    }
                } else {
                    //此处设置当前登录用户的头像，
                    if (userAvatarView != null) {
                        userAvatarView.setImageResource(R.drawable.hd_default_avatar);
//                        Glide.with(context).load("http://oev49clxj.bkt.clouddn.com/7a8aed7bjw1f32d0cumhkj20ey0mitbx.png").diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.hd_default_avatar).into(userAvatarView);
//                        如果用圆角，可以采用此方案：http://blog.csdn.net/weidongjian/article/details/47144549
                    }
                }
            }
        });


        //设置通知栏样式
        _uiProvider.getNotifier().setNotificationInfoProvider(new Notifier.NotificationInfoProvider() {
            @Override
            public String getTitle(Message message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(Message message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(Message message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, context);
                if (message.getType() == Message.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", context.getString(R.string.noti_text_expression));
                }
                return message.from() + ": " + ticker;
            }

            @Override
            public String getLatestText(Message message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
            }

            @Override
            public Intent getLaunchIntent(Message message) {
                Intent intent;
                if (isVideoCalling) {
                    intent = new Intent(context, CallActivity.class);
                } else {
                    //设置点击通知栏跳转事件
                    Conversation conversation = ChatClient.getInstance().chatManager().getConversation(message.from());
                    String titleName = null;
                    if (conversation.officialAccount() != null) {
                        titleName = conversation.officialAccount().getName();
                    }
                    intent = new IntentBuilder(context)
                            .setTargetClass(ChatActivity.class)
                            .setServiceIMNumber(conversation.conversationId())
                            .setVisitorInfo(DemoMessageHelper.createVisitorInfo())
                            .setTitleName(titleName)
                            .setShowUserNick(true)
                            .build();

                }
                return intent;
            }
        });

        //不设置,则使用默认, 声音和震动设置
//        _uiProvider.setSettingsProvider(new UIProvider.SettingsProvider() {
//            @Override
//            public boolean isMsgNotifyAllowed(Message message) {
//                return false;
//            }
//
//            @Override
//            public boolean isMsgSoundAllowed(Message message) {
//                return false;
//            }
//
//            @Override
//            public boolean isMsgVibrateAllowed(Message message) {
//                return false;
//            }
//
//            @Override
//            public boolean isSpeakerOpened() {
//                return false;
//            }
//        });
//        ChatClient.getInstance().getChat().addMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(List<Message> msgs) {
//
//            }
//
//            @Override
//            public void onCmdMessage(List<Message> msgs) {
//
//            }
//
//            @Override
//            public void onMessageSent() {
//
//            }
//
//            @Override
//            public void onMessageStatusUpdate() {
//
//            }
//        });
    }


    private void setGlobalListeners() {
        // create the global connection listener
        /*connectionListener = new ChatClient.ConnectionListener(){

            @Override
            public void onConnected() {
                //onConnected
            }

            @Override
            public void onDisconnected(int errorcode) {
                if (errorcode == Error.USER_REMOVED){
                    //账号被移除
                }else if (errorcode == Error.USER_LOGIN_ANOTHER_DEVICE){
                    //账号在其他地方登陆
                }
            }
        };

        //注册连接监听
        ChatClient.getInstance().addConnectionListener(connectionListener);*/

        //注册消息事件监听
        registerEventListener();
        //音视频广播接收器
        IntentFilter callFilter = new IntentFilter(ChatClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if (callReceiver == null) {
            callReceiver = new CallReceiver();
        }
        // register incoming call receiver
        appContext.registerReceiver(callReceiver, callFilter);
    }

    /**
     * 全局消息事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        messageListener = new ChatManager.MessageListener() {

            @Override
            public void onMessage(List<Message> msgs) {
                for (Message message : msgs) {
                    Log.d(TAG, "onMessageReceived id : " + message.messageId());
//
                    //这里全局监听通知类消息,通知类消息是通过普通消息的扩展实现
                    if (MessageHelper.isNotificationMessage(message)) {
                        // 检测是否为留言的通知消息
                        String eventName = getEventNameByNotification(message);
                        if (!TextUtils.isEmpty(eventName)) {
                            if (eventName.equals("TicketStatusChangedEvent") || eventName.equals("CommentCreatedEvent")) {
                                // 检测为留言部分的通知类消息,刷新留言列表
                                JSONObject jsonTicket = null;
                                try {
                                    jsonTicket = message.getJSONObjectAttribute("weichat").getJSONObject("event").getJSONObject("ticket");
                                } catch (Exception ignored) {
                                }
                                ListenerManager.getInstance().sendBroadCast(eventName, jsonTicket);
                            }
                        }
                    } else if (message.isNeedToScore()) {
                        MessageHelper.createInviteCommentMsg(message, "");
                        message.setIsNeedToScore(false);
                    }
                }
            }

            @Override
            public void onCmdMessage(List<Message> msgs) {
                for (Message message : msgs) {
                    Log.d(TAG, "收到透传消息");
                    //获取消息body
                    EMCmdMessageBody cmdMessageBody = (EMCmdMessageBody) message.body();
                    String action = cmdMessageBody.action(); //获取自定义action
                    Log.d(TAG, String.format("透传消息: action:%s,message:%s", action, message.toString()));
                }
            }

            @Override
            public void onMessageStatusUpdate() {

            }

            @Override
            public void onMessageSent() {

            }
        };

        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
    }


    /**
     * 获取EventName
     *
     * @param message
     * @return
     */
    public String getEventNameByNotification(Message message) {

        try {
            JSONObject weichatJson = message.getJSONObjectAttribute("weichat");
            if (weichatJson != null && weichatJson.has("event")) {
                JSONObject eventJson = weichatJson.getJSONObject("event");
                if (eventJson != null && eventJson.has("eventName")) {
                    return eventJson.getString("eventName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void pushActivity(Activity activity) {
        _uiProvider.pushActivity(activity);
    }

    public void popActivity(Activity activity) {
        _uiProvider.popActivity(activity);
    }

    public Notifier getNotifier() {
        return _uiProvider.getNotifier();
    }

}
