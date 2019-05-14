package com.example.hpur.spragent.Storage;

import com.example.hpur.spragent.Logic.Queries.CheckUserCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Iterator;

public class FireBaseAuthenticationAdmin {
    private DatabaseReference mRef;

    // c'tor
    public FireBaseAuthenticationAdmin() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        this.mRef = data.getReference("SPRApp").child("Admin");
    }

    public void checkPassword(final String pass, final CheckUserCallback queryCallback) {
        this.mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean passwordMatch = false;
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                while (itr.hasNext()) {
                    if (itr.next().getValue().equals(pass)) {
                        passwordMatch = true;
                        break;
                    }
                }

                queryCallback.checkAdminPasswordCallback(passwordMatch);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
