package com.example.hpur.spragent.Storage;

import com.example.hpur.spragent.Logic.Models.ReportModel;
import com.example.hpur.spragent.Logic.Queries.ReportsCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Iterator;

public class FireBaseTeenagersReports {

    private DatabaseReference mRef;
    private ArrayList<ReportModel> mReportModelsArrayList;

    // c'tor
    public FireBaseTeenagersReports() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        this.mRef = data.getReference("SPRApp/Teenagers Reports");
    }

    //write user into fire base
    public void writeReportsToDataBase(String uid, ReportModel reportModel) {
        this.mRef.child(uid+"/"+reportModel.getTimestamp()).setValue(reportModel);
    }

    //write report into firebase
    public void getReportsFromDataBase(String uid, final ReportsCallback reportsCallback) {
        this.mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                mReportModelsArrayList = new ArrayList<>();
                while(itr.hasNext()) {
                    ReportModel reportModel = itr.next().getValue(ReportModel.class);
                    mReportModelsArrayList.add(reportModel);
                }

                reportsCallback.getReportsCallback(mReportModelsArrayList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
