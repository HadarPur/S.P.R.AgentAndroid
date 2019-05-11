package com.example.hpur.spragent.Logic;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.hpur.spragent.R;
import java.util.List;

public class ChatBubbleAdapter extends RecyclerView.Adapter<ChatBubbleHolder> {

    private static final int USER_MESSAGE = 1;
    private static final int OTHER_MESSAGE = 2;
    private static final String TAG = "ChatBubbleAdapter:";

    private Context mContext;
    private List<ChatBubble> mChatBubbles;
    private int mItemResource;

    public ChatBubbleAdapter(Context context, int resource, List<ChatBubble> chatBubbles) {
        this.mContext = context;
        this.mChatBubbles = chatBubbles;
        this.mItemResource = resource;
    }

    @NonNull
    @Override
    public ChatBubbleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewType value comes from callback of getItemViewType method
        switch(viewType){
            case USER_MESSAGE :
                Log.d(TAG,"right chat bubble layout");
                mItemResource = R.layout.right_chat_bubble;
                break;
            case OTHER_MESSAGE:
                Log.d(TAG,"left chat bubble layout");
                mItemResource = R.layout.left_chat_bubble;
                break;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(mItemResource, parent, false);

        return new ChatBubbleHolder(this.mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBubbleHolder chatBubbleHolder, int position) {
        Log.d(TAG,"onBindViewHolder");
        ChatBubble chatBubble = this.mChatBubbles.get(position);
        chatBubbleHolder.bindChatBubble(chatBubble);
    }

    @Override
    public int getItemCount() {
        return this.mChatBubbles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.mChatBubbles.get(position).getmMyMessage() ? USER_MESSAGE : OTHER_MESSAGE;
    }


}
