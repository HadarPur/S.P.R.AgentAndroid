package com.example.hpur.spragent.Logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hpur.spragent.Logic.Models.ChatBubbleModel;
import com.example.hpur.spragent.Logic.Queries.OnMessageModelClickedCallback;
import com.example.hpur.spragent.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

//In charge of handling item's content/user interaction (row)
public class ChatBubbleHolder extends RecyclerView.ViewHolder {

    private static final int USER_MESSAGE = 0;
    private static final int OTHER_MESSAGE = 1;
    private static final int USER_MAP = 2;
    private static final int OTHER_MAP = 3;
    private static final int USER_IMAGE = 4;
    private static final int OTHER_IMAGE = 5;

    private TextView mTxtMessage;
    private ImageView mMapMessage;
    private ImageView mImageMessage;

    private Context mContext;
    private ChatBubbleModel mChatBubble;
    private SpinKitView mProgressBar = null;


    public ChatBubbleHolder(Context context, @NonNull View itemView, int type) {
        super(itemView);
        this.mContext = context;

        switch (type) {
            case USER_MESSAGE: case OTHER_MESSAGE:
                this.mTxtMessage = itemView.findViewById(R.id.txt_msg);
                break;
            case USER_MAP: case OTHER_MAP:
                this.mMapMessage = itemView.findViewById(R.id.map_bubble);
                this.mProgressBar = itemView.findViewById(R.id.spin_kit);
                break;
            case USER_IMAGE: case OTHER_IMAGE:
                this.mImageMessage = itemView.findViewById(R.id.image_bubble);
                this.mProgressBar = itemView.findViewById(R.id.spin_kit);
                break;
        }
    }
    public void bindChatBubble(ChatBubbleModel chatBubble, final OnMessageModelClickedCallback callback){
        this.mChatBubble = chatBubble;
        if (mChatBubble.getmMapModel() != null) {
            setMapMessage(callback);
        }
        else if (mChatBubble.getmImageModel() != null) {
            setImageMessage(callback);
        }
        else
            this.mTxtMessage.setText(mChatBubble.getmTextMessage());
    }

    private void setMapMessage(final OnMessageModelClickedCallback callback) {
        mProgressBar.setVisibility(View.VISIBLE);
        final String latLong = mChatBubble.getmMapModel().getLongitude() +","+mChatBubble.getmMapModel().getLatitude();
        final String url = "https://static-maps.yandex.ru/1.x/?lang=en_US&ll="+latLong+"&size=400,200&z=7&l=map&pt="+latLong+",pm2rdl";
        Picasso.get().load(url).fit().into(mMapMessage, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("bindChatBubble", "onSuccess");
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                    mMapMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.onMapBubbleClicked(mChatBubble.getmMapModel().getLatitude(), mChatBubble.getmMapModel().getLongitude());
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                Log.e("bindChatBubble", "onError: "+ e);
                e.printStackTrace();
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });


    }

    private void setImageMessage(final OnMessageModelClickedCallback callback) {
        mProgressBar.setVisibility(View.VISIBLE);
        final String url = mChatBubble.getmImageModel().getUrl_file();
        Picasso.get().load(url).into(mImageMessage, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("bindChatBubble", "onSuccess");
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                    mImageMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.onImageBubbleClicked(url);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("bindChatBubble", "onError: "+ e);
                e.printStackTrace();
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
