package com.neko.quileiamedic.ui.patient;

import androidx.lifecycle.ViewModel;

import com.neko.quileiamedic.ui.doctor.DoctorInfo;

import java.util.Date;

public class PatientInfo extends ViewModel {

    private String documentID;
    private String name;
    private String lastName;
    private Date birthdate;
    private String id;
    private DoctorInfo doctor;
    private boolean treatment;
    private double quota;
    private Date appointmentDate;
    private int pic;
    private double appointmentCount;


    public PatientInfo(){

    }




    public PatientInfo(String documentID, String name, String lastName, Date birthdate, String id, DoctorInfo doctor,
                       boolean treatment, double quota, Date appointmentDate, int pic, double appoinmentCount) {

        this.documentID = documentID;
        this.name = name;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.id = id;
        this.doctor = doctor;
        this.treatment = treatment;
        this.quota = quota;
        this.appointmentDate = appointmentDate;
        this.pic = pic;
        this.appointmentCount = appoinmentCount;
    }

    public double getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(double appointmentCount) {
        this.appointmentCount = appointmentCount;
    }

    public String getName() {
        return name;
    }

    public String getDocumentID() {
        return documentID;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DoctorInfo getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorInfo doctor) {
        this.doctor = doctor;
    }

    public boolean isTreatment() {
        return treatment;
    }

    public void setTreatment(boolean treatment) {
        this.treatment = treatment;
    }

    public double getQuota() {
        return quota;
    }

    public void setQuota(double quota) {
        this.quota = quota;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}
