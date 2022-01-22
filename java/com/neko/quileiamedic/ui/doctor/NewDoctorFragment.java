package com.neko.quileiamedic.ui.doctor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neko.quileiamedic.R;
import java.util.HashMap;
import java.util.Map;


public class NewDoctorFragment extends Fragment implements View.OnClickListener{

    private EditText name, code, specialty, experience, office;
    private RadioButton buttonY, buttonN;
    private Button saveButton, cancelButton;
    private FirebaseFirestore db;
    private DoctorInfo doctorViewModel;
    private String documentID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_doctor, container, false);

        db = FirebaseFirestore.getInstance();
        doctorViewModel = new ViewModelProvider(requireActivity()).get(DoctorInfo.class);
        name = view.findViewById(R.id.doctorNameText);
        code = view.findViewById(R.id.doctorCodeText);
        specialty = view.findViewById(R.id.doctorSpecialtyText);
        experience = view.findViewById(R.id.doctorExperienceText);
        experience.setLongClickable(false);
        office = view.findViewById(R.id.doctorOfficeText);
        buttonY = view.findViewById(R.id.radioButtonY);
        buttonN = view.findViewById(R.id.radioButtonN);
        saveButton = view.findViewById(R.id.saveDoctorButton);
        cancelButton = view.findViewById(R.id.cancelDoctorButton);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.doctorFragment);

            }
        });

        //Si el documento no es nulo
        documentID = doctorViewModel.getDocumentID();
        if(documentID != null){
            String dName = doctorViewModel.getName();
            String dCode = doctorViewModel.getCode();
            String dSpecialty = doctorViewModel.getSpecialty();
            String dExperience = Float.toString(doctorViewModel.getExperience());
            String dOffice = doctorViewModel.getOffice();
            boolean dButtonY = doctorViewModel.isHome();

            if(dButtonY){
                buttonY.setChecked(true);
            }
            else{
                buttonN.setChecked(true);
            }
            experience.setText(dExperience);
            name.setText(dName);
            code.setText(dCode);
            specialty.setText(dSpecialty);
            office.setText(dOffice);
        }

        return view;
    }


    @Override
    public void onClick(View v) {


        String name1 = name.getText().toString();
        String code1 = code.getText().toString();
        String specialty1 = specialty.getText().toString();
        String exp = experience.getText().toString();
        String office1 = office.getText().toString();
        boolean home = buttonY.isChecked();

        if(name1.isEmpty() || code1.isEmpty() || specialty1.isEmpty() || exp.isEmpty() || office1.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Todos los campos son obligatorios")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return;

        }

        float experience1;
        //Si sucede un error de formato se le pedirá al usuario que ingrese uno correcto
        try {
            experience1 = Float.parseFloat(exp);
        }
        catch (Exception e){
            experience.setError(getString(R.string.unvalidText));
            return;
        }

        if(documentID != null){
            updateInDb(name1, code1, specialty1, experience1, office1, home);
        }
        else{
            saveInDb(name1, code1, specialty1, experience1, office1, home);
        }





    }

    //Método para guardar los datos en la base de datos
    private void saveInDb(String name1, String code1, String specialty1, float experience1, String office1, boolean home){

        Map<String, Object> doctor = new HashMap<>();
        doctor.put("name", name1);
        doctor.put("code", code1);
        doctor.put("specialty", specialty1);
        doctor.put("experience", experience1);
        doctor.put("office", office1);
        doctor.put("home", home);

        db.collection("Doctors")
                .document()
                .set(doctor)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Toast.makeText(getContext(), "Datos guardados con éxito",
                                Toast.LENGTH_SHORT).show();

                        Navigation.findNavController(requireView()).navigate(R.id.doctorFragment);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getContext(), "Error, intenta de nuevo",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }



    private void updateInDb(String name1, String code1, String specialty1, float experience1, String office1, boolean home){

        db.collection("Doctors")
                .document(documentID)
                .update("name", name1,
                        "code", code1,
                        "specialty", specialty1,
                        "experience", experience1,
                        "office", office1,
                        "home", home)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireContext(),"Datos actualizados con éxito",
                                Toast.LENGTH_SHORT).show();

                        Navigation.findNavController(requireView()).navigate(R.id.doctorFragment);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(),"Error al actualizar datos",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}