package com.ahmadrafiuddin.lockmate.model;

/**
 * Created by T430 on 16/5/2017.
 */

public class AccessLog {
    private String studentName;
    private String matricNo;
    private String scan_dateTime;

    public AccessLog(String studentName, String matricNo, String scan_dateTime) {
        this.studentName = studentName;
        this.matricNo = matricNo;
        this.scan_dateTime = scan_dateTime;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getMatricNo() {
        return matricNo;
    }

    public void setMatricNo(String matricNo) {
        this.matricNo = matricNo;
    }

    public String getScan_dateTime() {
        return scan_dateTime;
    }

    public void setScan_dateTime(String scan_dateTime) {
        this.scan_dateTime = scan_dateTime;
    }
}
