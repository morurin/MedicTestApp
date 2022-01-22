package com.neko.quileiamedic;

import java.util.Date;

public class History {



    private String patientName;
    private String doctorName;
    private String text;
    private Date appointmentDate;
    private int assisted;
    private double count;
    private boolean expanded;
    private boolean attended;


    public History(){

    }

    public History(String patientName, String doctorName, String text, Date appointmentDate, int assisted) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.text = text;
        this.appointmentDate = appointmentDate;
        this.assisted = assisted;
        this.expanded = false;
        this.count = 0;
        this.attended = false;
    }


    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public int getAssisted() {
        return assisted;
    }

    public void setAssisted(int assisted) {
        this.assisted = assisted;
    }
}
