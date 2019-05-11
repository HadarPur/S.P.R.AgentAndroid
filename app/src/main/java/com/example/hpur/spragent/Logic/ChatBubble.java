package com.example.hpur.spragent.Logic;

public class ChatBubble  {

    private String mTextMessage;
    // private String mPhotoUrl;
    private String mUserName;
    private boolean mMyMessage;

    //
    public ChatBubble(){ }
    //todo:: Change the photo/text content and name logic
    public ChatBubble(String mTextMessage, String userName, boolean myMessage) {
        this.mTextMessage = mTextMessage;
        // this.mPhotoUrl = null;
        this.mMyMessage = myMessage;
        this.mUserName = userName;
    }

    public String getmTextMessage() {
        return mTextMessage;
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

    public boolean getmMyMessage() {
        return mMyMessage;
    }

    public void setmMyMessage(boolean mMyMessage) {
        this.mMyMessage = mMyMessage;
    }
}
