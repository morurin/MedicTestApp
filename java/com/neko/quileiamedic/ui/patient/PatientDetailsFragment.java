package com.neko.quileiamedic.ui.patient;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.neko.quileiamedic.R;
import com.neko.quileiamedic.ui.doctor.DoctorInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PatientDetailsFragment extends Fragment implements View.OnClickListener{

    private PatientInfo patientViewModel;
    private TextView nameLastname, birthdate, id, doctor,lastDate, quota, intreatment;
    private String isIntreatment, pLastDate, pDoctor;
    private Button datesButton, newDateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_patient_details, container, false);

        patientViewModel = new ViewModelProvider(requireActivity()).get(PatientInfo.class);
        datesButton = view.findViewById(R.id.patientDateButton);
        newDateButton = view.findViewById(R.id.patientNewDateButton);
        nameLastname = view.findViewById(R.id.patientNameView);
        birthdate = view.findViewById(R.id.patientBirthView);
        id = view.findViewById(R.id.patientIdView);
        doctor = view.findViewById(R.id.patientDoctorView);
        lastDate = view.findViewById(R.id.patientDateView);
        quota = view.findViewById(R.id.patientQuotaView);
        intreatment = view.findViewById(R.id.patientTreatmentView);

        datesButton.setOnClickListener(this);
        newDateButton.setOnClickListener(this);


        String pNamelastname = patientViewModel.getName() +" "+ patientViewModel.getLastName();
        DoctorInfo doctorInfo = patientViewModel.getDoctor();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Date birthdateFormat = patientViewModel.getBirthdate();
        String pBirthdate = getString(R.string.birthdate) +" "+ simpleDateFormat.format(birthdateFormat);


        String pId = getString(R.string.patientId) +" "+ patientViewModel.getId();

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEEE, MMM d, yyyy hh:mm a", Locale.getDefault());
        Date lastDateFormat = patientViewModel.getAppointmentDate();


        if (lastDateFormat == null || doctorInfo == null){

            pLastDate = getString(R.string.last_date) +" "+ getString(R.string.no_appointment);
            pDoctor = getString(R.string.patient_doctor) +" "+ getString(R.string.no_doctor);

        }
        else{
            pLastDate = getString(R.string.last_date) +" "+ simpleDateFormat1.format(lastDateFormat);
            pDoctor = getString(R.string.patient_doctor) +" "+ patientViewModel.getDoctor().getName();
        }


        String pQuota = getString(R.string.patient_quota) +" "+ patientViewModel.getQuota();
        boolean pIntreatment = patientViewModel.isTreatment();

        if(pIntreatment){
            isIntreatment = getString(R.string.treatmentY);
        }
        else{
            isIntreatment = getString(R.string.treatmentN);
        }

        doctor.setText(pDoctor);
        nameLastname.setText(pNamelastname);
        birthdate.setText(pBirthdate);
        id.setText(pId);
        lastDate.setText(pLastDate);
        quota.setText(pQuota);
        intreatment.setText(isIntreatment);

        return view;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.patientDateButton:
                Navigation.findNavController(requireView()).navigate(R.id.patientHistoryFragment);
                break;

            case R.id.patientNewDateButton:
                Navigation.findNavController(requireView()).navigate(R.id.newAppointmentFragment);

                break;

            default:
                break;
        }
    }
}