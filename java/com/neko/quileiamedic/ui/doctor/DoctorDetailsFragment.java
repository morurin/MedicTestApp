package com.neko.quileiamedic.ui.doctor;

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

public class DoctorDetailsFragment extends Fragment implements View.OnClickListener {

    private TextView name,code,specialty,expereince, office, home;
    private Button datesButton;
    String ifHome;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_doctor_details, container, false);

        DoctorInfo doctorViewModel = new ViewModelProvider(requireActivity()).get(DoctorInfo.class);
        name = view.findViewById(R.id.doctorNameView);
        code = view.findViewById(R.id.doctorCodeView);
        specialty = view.findViewById(R.id.doctorSpecialtyView);
        expereince = view.findViewById(R.id.doctorExperienceView);
        office = view.findViewById(R.id.doctorOfficeView);
        home = view.findViewById(R.id.doctorHomeView);
        datesButton = view.findViewById(R.id.doctorDateButton);

        datesButton.setOnClickListener(this);

        String dName = doctorViewModel.getName();
        String dCode = "N° tarjeta profesional "+ doctorViewModel.getCode();
        String dSpecialty = doctorViewModel.getSpecialty();
        float exp = doctorViewModel.getExperience();
        String dExperience = exp +" años de experiencia";
        String dOffice = "Consultorio N° "+ doctorViewModel.getOffice();
        boolean dHome = doctorViewModel.isHome();


        if(dHome){
            ifHome = "Disponibilidad a domicilio: Si";
        }
        else{
            ifHome = "Disponibilidad a domicilio: No";
        }

        name.setText(dName);
        code.setText(dCode);
        specialty.setText(dSpecialty);
        expereince.setText(dExperience);
        office.setText(dOffice);
        home.setText(ifHome);


        return view;
    }

    @Override
    public void onClick(View v) {
        Navigation.findNavController(requireView()).navigate(R.id.doctorHistoryFragment);

    }
}