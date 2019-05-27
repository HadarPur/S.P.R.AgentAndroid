package com.example.hpur.spragent.Logic.Models;

import com.example.hpur.spragent.Logic.Types.MessageType;

public class ChatBubbleModel {

    private String mTextMessage;
    private String mUserName;
    private MessageType mMessageType;
    private MapModel mMapModel;
    private ImageModel mImageModel;

    public ChatBubbleModel(){ }

    public ChatBubbleModel(String mTextMessage, String userName, MessageType type) {
        this.mTextMessage = mTextMessage;
        this.mMessageType = type;
        this.mUserName = userName;
    }

    public ChatBubbleModel(MapModel mMapModel, String mUserName, MessageType mMessageType) {
        this.mUserName = mUserName;
        this.mMessageType = mMessageType;
        this.mMapModel = mMapModel;
        this.mTextMessage = "";
    }

    public ChatBubbleModel(ImageModel mImageModel, String mUserName, MessageType mMessageType) {
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
