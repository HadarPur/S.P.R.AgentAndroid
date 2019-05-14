package com.example.hpur.spragent.Logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.hpur.spragent.Logic.Queries.OnMapClickedCallback;
import com.example.hpur.spragent.R;
import java.util.List;

//In charge to providing between the items and the underlying data
//The adapter links the data in the data set with the items of the view
public class ChatBubbleAdapter extends RecyclerView.Adapter<ChatBubbleHolder> {

    private static final int USER_MESSAGE = 0;
    private static final int OTHER_MESSAGE = 1;
    private static final int USER_MAP = 2;
    private static final int OTHER_MAP = 3;
    private static final String TAG = "ChatBubbleAdapter:";

    private Context mContext;
    private List<ChatBubble> mChatBubbles;
    private int mItemResource;
    private FragmentManager mFragment;
    private OnMapClickedCallback mOnMapClickedCallback;

    public ChatBubbleAdapter(Context context, int resource, List<ChatBubble> chatBubbles, OnMapClickedCallback onMapClickedCallback) {
        this.mContext = context;
        this.mChatBubbles = chatBubbles;
        this.mItemResource = resource;
        this.mOnMapClickedCallback = onMapClickedCallback;
    }

    @NonNull
    @Override
    public ChatBubbleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewType value comes from callback of getItemViewType method
        switch(viewType){
            case USER_MESSAGE :
                mItemResource = R.layout.right_chat_bubble;
                break;
            case OTHER_MESSAGE:
                mItemResource = R.layout.left_chat_bubble;
                break;
            case USER_MAP:
                mItemResource = R.layout.right_map_bubble;
                break;
            case OTHER_MAP:
                mItemResource = R.layout.left_map_bubble;
                break;
            default:
                Log.d(TAG, "view type is not expected.. error at getItemViewType() ");
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(mItemResource, parent, false);

        return new ChatBubbleHolder(getSupportedFragment() ,this.mContext, view, viewType);
    }

    //onBindViewHolder purpose:
    //      Instead of creating a new view for each new row,an old view
    //      is recycled and reused by binding new data to it
    @Override
    public void onBindViewHolder(@NonNull ChatBubbleHolder chatBubbleHolder, int position) {
        Log.d(TAG,"onBindViewHolder");
        ChatBubble chatBubble = this.mChatBubbles.get(position);
        chatBubbleHolder.bindChatBubble(chatBubble, mOnMapClickedCallback);
    }

    @Override
    public int getItemCount() {
        return this.mChatBubbles.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.mChatBubbles.get(position).getmMessageType().ordinal();
    }

    public void setSupportFragmentManager(FragmentManager fragment) {
        this.mFragment = fragment;
    }

    public FragmentManager getSupportedFragment() {
        return this.mFragment;
    }


}
