package com.example.hpur.spragent.Logic.Models;
import com.example.hpur.spragent.Logic.Queries.CheckUserCallback;
import com.example.hpur.spragent.Storage.FireBaseAuthenticationAdmin;

public class AdminModel {

    public AdminModel() {
    }

    public void readAdminPassFromFirebase(String pass, final CheckUserCallback queryCallback) {
        FireBaseAuthenticationAdmin admin = new FireBaseAuthenticationAdmin();
        admin.checkPassword(pass, queryCallback);
    }
}
