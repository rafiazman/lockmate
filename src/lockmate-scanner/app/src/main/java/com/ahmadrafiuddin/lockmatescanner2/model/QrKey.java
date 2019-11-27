package com.ahmadrafiuddin.lockmatescanner2.model;

/**
 * Created by X230 on 27/3/2017.
 */

public class QrKey {
    private String id;
    private String studentName;
    private String matricNo;
    private String password;

    public QrKey() {
    }

    public QrKey(String studentName, String matricNo, String password) {
        this.studentName = studentName;
        this.matricNo = matricNo;
        this.password = password;
    }

    public QrKey(String id, String studentName, String matricNo, String password) {
        this.id = id;
        this.studentName = studentName;
        this.matricNo = matricNo;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
