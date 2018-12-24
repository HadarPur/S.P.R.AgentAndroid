package com.example.hpur.spragent.Storage;

import com.example.hpur.spragent.Queries.CheckUserCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Iterator;

public class FireBaseAuthenticationAgents {

    private final int RESET=0, SIGN=1;
    private DatabaseReference mRef;

    // c'tor
    public FireBaseAuthenticationAgents() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        this.mRef = data.getReference("Agents");
    }

    //write user into fire base
    public void writeUserToDataBase(String uid, String email) {
        this.mRef.child(uid).setValue(email);
    }

    // check if user exist
    public void checkUser(final int type,final String email, final CheckUserCallback queryCallback) {
        this.mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExist = false;
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                while(itr.hasNext()) {
                    if (itr.next().getValue().equals(email)) {
                        userExist = true;
                        break;
                    }
                }

                switch (type) {
                    case SIGN:
                        queryCallback.checkUserCallback(userExist);
                        break;
                    case RESET:
                        queryCallback.checkUserExistResetCallBack(userExist);
                        break;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
