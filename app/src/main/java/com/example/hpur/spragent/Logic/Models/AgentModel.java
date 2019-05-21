package com.example.hpur.spragent.Logic.Models;

import android.content.Context;

import com.example.hpur.spragent.Logic.Types.GenderType;
import com.example.hpur.spragent.Logic.Types.SectorType;
import com.example.hpur.spragent.Logic.Types.SexType;
import com.example.hpur.spragent.Storage.SharedPreferencesStorage;
import com.google.gson.Gson;

public class AgentModel {
    private String email;
    private String firstName;
    private String lastName;
    private SexType sexType;
    private String living;
    private SectorType sectorType;
    private String birthday;
    private GenderType genderType;

    public AgentModel() {
    }

    public AgentModel(String email, String firstName, String lastName, SexType sexType, String living, SectorType sectorType, String birthday, GenderType genderType) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sexType = sexType;
        this.living = living;
        this.sectorType = sectorType;
        this.birthday = birthday;
        this.genderType = genderType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public SexType getSexType() {
        return sexType;
    }

    public void setSexType(SexType sexType) {
        this.sexType = sexType;
    }

    public String getLiving() {
        return living;
    }

    public void setLiving(String living) {
        this.living = living;
    }

    public SectorType getSectorType() {
        return sectorType;
    }

    public void setSectorType(SectorType sectorType) {
        this.sectorType = sectorType;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public GenderType getGenderType() {
        return genderType;
    }

    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }

    public void saveLocalObj(Context ctx) {
        Gson gson = new Gson();
        String json = gson.toJson(this);

        SharedPreferencesStorage storage = new SharedPreferencesStorage(ctx);
        storage.saveData(json,"UserModel");
    }

    public AgentModel readLocalObj(Context ctx) {
        Gson gson = new Gson();
        SharedPreferencesStorage storage = new SharedPreferencesStorage(ctx);

        String json = storage.readData("UserModel");
        AgentModel obj = gson.fromJson(json, AgentModel.class);

        return obj;
    }
}
