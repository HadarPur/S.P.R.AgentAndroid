package com.example.hpur.spragent.Storage;

import android.content.Context;

import com.example.hpur.spragent.Logic.Models.AgentModel;
import com.example.hpur.spragent.Logic.Queries.CheckUserCallback;
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
        this.mRef = data.getReference("SPRApp").child("Agents");
    }

    //write user into fire base
    public void writeUserToDataBase(String uid, AgentModel agent) {
        this.mRef.child(uid).setValue(agent);
    }

    // check if user exist
    public void checkUser(final Context ctx, final int type, final String email, final CheckUserCallback queryCallback) {
        this.mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExist = false;
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                while(itr.hasNext()) {
                    AgentModel agent = itr.next().getValue(AgentModel.class);

                    if (agent.getEmail().equals(email)) {
                        agent.saveLocalObj(ctx);
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
