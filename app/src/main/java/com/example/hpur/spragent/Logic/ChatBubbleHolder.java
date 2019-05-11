package com.example.hpur.spragent.Logic;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.hpur.spragent.R;

public class ChatBubbleHolder extends RecyclerView.ViewHolder {

    private final TextView mTxtMessage;
    private Context mContext;
    private ChatBubble mChatBubble;

    public ChatBubbleHolder(Context context,@NonNull View itemView) {
        super(itemView);
        this.mContext = context;

        this.mTxtMessage = itemView.findViewById(R.id.txt_msg);
    }

    public void bindChatBubble(ChatBubble chatBubble){
        this.mChatBubble = chatBubble;
        this.mTxtMessage.setText(mChatBubble.getmTextMessage());
    }
}
