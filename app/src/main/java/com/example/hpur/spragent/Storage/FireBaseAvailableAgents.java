package com.example.hpur.spragent.Storage;

import com.example.hpur.spragent.Logic.Types.AvailabilityType;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseAvailableAgents {
    private DatabaseReference mRef;

    // c'tor
    public FireBaseAvailableAgents() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        this.mRef = data.getReference("SPRApp/Available Agents");
    }

    //write availability into fire base
    public void setAvailability(String uid, AvailabilityType available) {
        switch (available) {
            case OFF:
                this.mRef.child(uid).removeValue();
                break;
            case ON:
                this.mRef.child(uid).setValue(available);
                break;
        }
    }
}
