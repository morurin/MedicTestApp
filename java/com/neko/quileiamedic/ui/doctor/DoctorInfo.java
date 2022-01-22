package com.neko.quileiamedic.ui.doctor;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.SnapshotMetadata;

import java.util.Map;

public class DoctorInfo extends ViewModel {

    private String name;
    private String documentID;
    private String code;
    private String specialty;
    private float experience;
    private String office;
    private boolean home;
    private int pic;
    private int appointmentCount;


    public DoctorInfo (){

    }



    public DoctorInfo(String documentID, String name, String specialty){

        this.documentID = documentID;
        this.name = name;
        this.specialty = specialty;

    }

    public DoctorInfo(String name, String documentID, String code, String specialty, float experience,
                      String office, boolean home, int pic, int appointmentCount) {

        this.name = name;
        this.documentID = documentID;
        this.code = code;
        this.specialty = specialty;
        this.experience = experience;
        this.office = office;
        this.home = home;
        this.pic = pic;
        this.appointmentCount = appointmentCount;
    }

    public int getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(int appointmentCount) {
        this.appointmentCount = appointmentCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }
}
