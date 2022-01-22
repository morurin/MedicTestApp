package com.neko.quileiamedic.ui.patient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neko.quileiamedic.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NewPatientFragment extends Fragment implements View.OnClickListener{

    private EditText name, lastname, birthdate, id, quota;
    private RadioButton buttonN, buttonY;
    private Button saveButton, cancelButton;
    private PatientInfo patientViewModel;
    private FirebaseFirestore db;
    private String documentId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_patient, container, false);

        db = FirebaseFirestore.getInstance();
        patientViewModel = new ViewModelProvider(requireActivity()).get(PatientInfo.class);
        name = view.findViewById(R.id.patientNameText);
        lastname = view.findViewById(R.id.patientLastnameText);
        birthdate = view.findViewById(R.id.patientBirthPicker);
        id = view.findViewById(R.id.patientIdText);

        quota = view.findViewById(R.id.patientQuoataText);
        quota.setLongClickable(false);
        buttonN = view.findViewById(R.id.pRadioButtonN);
        buttonY = view.findViewById(R.id.pRadioButtonY);
        saveButton = view.findViewById(R.id.savePatientButton);
        cancelButton = view.findViewById(R.id.cancelPatientButton);

        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        birthdate.setOnClickListener(this);


        //Se obtiene el id del documento en la base de
        //datos y si este es nulo se crea un nuevo usuario
        //De lo contrario se actualiza el ya existente
        documentId = patientViewModel.getDocumentID();
        if(documentId != null){
            String pName = patientViewModel.getName();
            String pLastname = patientViewModel.getLastName();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date birthdateF = patientViewModel.getBirthdate();
            String pBirthdate = simpleDateFormat.format(birthdateF);

            String pId = patientViewModel.getId();
            String pQuota =  Double.toString(patientViewModel.getQuota());

            boolean pButtonN = patientViewModel.isTreatment();

            if(pButtonN){
                buttonY.setChecked(true);
            }
            else{
                buttonN.setChecked(true);

            }
            name.setText(pName);
            lastname.setText(pLastname);
            birthdate.setText(pBirthdate);
            id.setText(pId);
            quota.setText(pQuota);



        }

        return view;
    }






    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.patientBirthPicker:
                datePicker();
                break;


            case R.id.cancelPatientButton:
                Navigation.findNavController(requireView()).navigate(R.id.patientFragment);
                break;

            case R.id.savePatientButton:

                if(documentId != null){
                    try {
                        updateInDb();
                    } catch (ParseException e) {
                        e.printStackTrace(); }

                }
                else{
                    try {
                        saveInDb();
                    } catch (ParseException e) {
                        e.printStackTrace(); }
                }


                break;

            default:
                break;
        }

    }




    //Método para seleccionar fecha
    private void datePicker(){

        Calendar calendar = Calendar.getInstance();

        int DAY = calendar.get(Calendar.DATE);
        int MONTH = calendar.get(Calendar.MONTH);
        int YEAR = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendarFormat = Calendar.getInstance();

                calendarFormat.set(Calendar.YEAR,year);
                calendarFormat.set(Calendar.MONTH,month);
                calendarFormat.set(Calendar.DATE,dayOfMonth);

                Date date = calendarFormat.getTime();

                CharSequence birthSequence = DateFormat.format("MM/dd/yyyy", calendarFormat);
                birthdate.setText(birthSequence);
                patientViewModel.setBirthdate(date);


            }
        },YEAR, MONTH, DAY);
        int oneDay = 86400*1000;
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime()-oneDay);
        datePickerDialog.show();
    }



    //Método para guardar los datos obtenidos del usuario en la base de datos
    private void saveInDb() throws ParseException {

        //Una vez el usuario ingrese los datos, estos se almacenas en variables
        String name1 = name.getText().toString();
        String lastname1 = lastname.getText().toString();
        String birthdate1 = birthdate.getText().toString();
        String id1 = id.getText().toString();
        String quota1 = quota.getText().toString();
        boolean buttonY1 = buttonY.isChecked();

        //Se comprueba que todos los campos no estén vacíos
        if(name1.isEmpty()||lastname1.isEmpty()||birthdate1.isEmpty()||id1.isEmpty()||quota1.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage(getString(R.string.all_data_required))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return;

        }
        double quotaNumber;
        //Si sucede un error de formato se le pedirá al usuario que ingrese uno correcto
        try {
            //Conversión de tipo string a double
            quotaNumber = Double.parseDouble(quota1);
        }
        catch(Exception e){
            quota.setError(getString(R.string.unvalidText));
            return;
        }

        //Conversión de las fechas de tipo string a date
        Date birthdateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.getDefault()).parse(birthdate1);



        //Diccionario con todos los datos del paciente
        Map<String, Object> patient = new HashMap<>();
        patient.put("name", name1);
        patient.put("lastname", lastname1);
        patient.put("birthdate", birthdateFormat);
        patient.put("id", id1);
        patient.put("quota",quotaNumber );
        patient.put("treatment", buttonY1);
        patient.put("appointmentCount", 0);

        //Almacenamiento de los datos del paciente en la base de datos
        db.collection("Patients")
                .document()
                .set(patient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), getString(R.string.new_data),
                                Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.patientFragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), getString(R.string.try_again),
                                Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void updateInDb() throws ParseException {

        //Una vez el usuario ingrese los datos, estos se almacenas en variables
        String name1 = name.getText().toString();
        String lastname1 = lastname.getText().toString();
        String birthdate1 = birthdate.getText().toString();
        String id1 = id.getText().toString();
        String quota1 = quota.getText().toString();
        boolean buttonY1 = buttonY.isChecked();

        //Se comprueba que todos los campos no estén vacíos
        if(name1.isEmpty()||lastname1.isEmpty()||birthdate1.isEmpty()||id1.isEmpty()||quota1.isEmpty()){

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage(getString(R.string.all_data_required))
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return;

        }

        double quotaNumber;
        //Si sucede un error de formato se le pedirá al usuario que ingrese uno correcto
        try {
            //Conversión de tipo string a double
            quotaNumber = Double.parseDouble(quota1);
        }
        catch(Exception e){
            quota.setError(getString(R.string.unvalidText));
            return;
        }
        //Conversión de las fechas de tipo string a date
        Date birthdateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.getDefault()).parse(birthdate1);



        //Almacenamiento de los datos del paciente en la base de datos
        db.collection("Patients")
                .document(documentId)
                .update("name", name1,
                    "lastname", lastname1,
                        "birthdate", birthdateFormat,
                        "id", id1,
                        "quota", quotaNumber,
                        "treatment", buttonY1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), getString(R.string.data_update),
                                Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.patientFragment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), getString(R.string.try_again),
                                Toast.LENGTH_SHORT).show();
                    }
                });


    }
}