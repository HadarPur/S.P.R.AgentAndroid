package com.example.hpur.spragent.Logic.Models;

import com.example.hpur.spragent.Logic.Queries.ReportsCallback;
import com.example.hpur.spragent.Storage.FireBaseTeenagersReports;

public class ReportModel {

    private String timestamp;
    private String report;

    public ReportModel() {
    }

    public ReportModel(String timestamp, String report) {
        this.timestamp = timestamp;
        this.report = report;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getReport() {
        return report;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void saveReportToFirebase(String uid) {
        FireBaseTeenagersReports report = new FireBaseTeenagersReports();
        report.writeReportsToDataBase(uid, this);
    }

    public void readReportFromFirebase(String uid, final ReportsCallback queryCallback) {
        FireBaseTeenagersReports report = new FireBaseTeenagersReports();
        report.getReportsFromDataBase(uid ,queryCallback);
    }
}
