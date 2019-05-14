package com.example.hpur.spragent.Logic;

import com.example.hpur.spragent.Logic.MapModel;
import com.example.hpur.spragent.Logic.MessageType;

public class ChatBubble  {

    private String mTextMessage;
    private String mUserName;
    private MessageType mMessageType;
    private MapModel mMapModel;

    public ChatBubble(){ }

    public ChatBubble(String mTextMessage, String userName, MessageType type) {
        this.mTextMessage = mTextMessage;
        this.mMessageType = type;
        this.mUserName = userName;
    }

    public ChatBubble(MapModel mMapModel, String mUserName, MessageType mMessageType) {
        this.mUserName = mUserName;
        this.mMessageType = mMessageType;
        this.mMapModel = mMapModel;
        this.mTextMessage = "";
    }

    public String getmTextMessage() {
        return mTextMessage;
    }

    public MapModel getmMapModel() {
        return mMapModel;
    }

    public void setmMapModel(MapModel mMapModel) {
        this.mMapModel = mMapModel;
    }

    public MessageType getmMessageType() {
        return mMessageType;
    }

    public void setmMessageType(MessageType mMessageType) {
        this.mMessageType = mMessageType;
    }

    public void setmTextMessage(String mTextMessage) {
        this.mTextMessage = mTextMessage;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

}
