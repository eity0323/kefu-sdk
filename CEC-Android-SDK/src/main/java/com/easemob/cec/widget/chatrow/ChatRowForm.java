package com.easemob.cec.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easemob.cec.R;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.adapter.MessageAdapter;
import com.hyphenate.helpdesk.easeui.util.SmileUtils;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.helpdesk.model.FormInfo;
import com.hyphenate.helpdesk.model.MessageHelper;

public class ChatRowForm extends ChatRow {

    private TextView titleView;
    private TextView contentView;
    private String targetUrl;

    public ChatRowForm(Context context, Message message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == Message.Direct.RECEIVE ? R.layout.em_row_received_form : R.layout.hd_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        titleView = (TextView) findViewById(R.id.tv_form_title);
        contentView = (TextView) findViewById(R.id.tv_form_content);
    }

    @Override
    protected void onUpdateView() {
        if (adapter instanceof MessageAdapter) {
            ((MessageAdapter) adapter).refresh();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.body();

        Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
        // 设置内容
//        contentView.setText(span, TextView.BufferType.SPANNABLE);
        if (message.direct() == Message.Direct.RECEIVE){
            FormInfo formInfo = MessageHelper.getFormMessage(message);
            targetUrl = formInfo.getTargetUrl();
            titleView.setText(formInfo.getTitle());
            contentView.setText(formInfo.getDescription());
        }
        handleTextMessage();

    }


    protected void handleTextMessage() {
        boolean isShowProgress = UIProvider.getInstance().isShowProgress();
        if (message.direct() == Message.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    // 发送消息
                    break;
                case SUCCESS: // 发送成功
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL: // 发送失败
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    if (isShowProgress)
                        progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    protected void onBubbleClick() {

    }
}
