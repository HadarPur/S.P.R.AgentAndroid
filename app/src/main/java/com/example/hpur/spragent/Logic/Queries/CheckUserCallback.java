package com.example.hpur.spragent.Logic.Queries;

public interface CheckUserCallback {
    void checkUserCallback(boolean result);
    void checkUserExistResetCallBack(boolean result);
    void checkAdminPasswordCallback(boolean result);
}
