package com.example.hpur.spragent.Logic;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hpur.spragent.Logic.Queries.OnChatCardClickedCallback;
import com.example.hpur.spragent.R;

public class TeenagerHolder extends RecyclerView.ViewHolder {

    private TeenagerNameModel mTeenagerNameModel;
    private TextView mTxtMessage;
    private View mItemView;

    public TeenagerHolder(@NonNull View itemView) {
        super(itemView);
        this.mItemView = itemView;
        this.mTxtMessage = itemView.findViewById(R.id.teenager_chat_txt_msg);

    }

    public void bindTeenagerNameModel(TeenagerNameModel teenagerNameModel, final OnChatCardClickedCallback callback) {
        this.mTeenagerNameModel = teenagerNameModel;
        this.mTxtMessage.setText(this.mTeenagerNameModel.getId());
        this.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onCardClicked(mTeenagerNameModel.getId());

            }
        });
    }
}
