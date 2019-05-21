package com.example.hpur.spragent.Logic;

import com.example.hpur.spragent.Logic.Models.ImageModel;
import com.example.hpur.spragent.Logic.Models.MapModel;
import com.example.hpur.spragent.Logic.Types.MessageType;

public class ChatBubble  {

    private String mTextMessage;
    private String mUserName;
    private MessageType mMessageType;
    private MapModel mMapModel;
    private ImageModel mImageModel;

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

    public ChatBubble(ImageModel mImageModel, String mUserName, MessageType mMessageType) {
        this.mUserName = mUserName;
        this.mMessageType = mMessageType;
        this.mImageModel = mImageModel;
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

    public ImageModel getmImageModel() {
        return mImageModel;
    }

    public void setmImageModel(ImageModel mImageModel) {
        this.mImageModel = mImageModel;
    }
}
