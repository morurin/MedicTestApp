package com.neko.quileiamedic.ui.patient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.neko.quileiamedic.History;
import com.neko.quileiamedic.R;
import com.neko.quileiamedic.ui.doctor.CustomDoctorAdapter;
import com.neko.quileiamedic.ui.doctor.DoctorInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class NewAppointmentFragment extends Fragment implements View.OnClickListener {

    private EditText doctor, appointmentDate, appointmentTime;
    private Button cancelButton, saveButtom;
    private DoctorInfo doctorViewModel;
    private PatientInfo patientViewModel;
    private CustomDoctorAdapter adapter;
    private FirebaseFirestore db;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_appointment, container, false);

        db = FirebaseFirestore.getInstance();

        patientViewModel = new ViewModelProvider(requireActivity()).get(PatientInfo.class);
        doctor = view.findViewById(R.id.patientDoctorPicker);
        doctor.setLongClickable(false);

        appointmentDate = view.findViewById(R.id.patientDatePicker);
        appointmentTime = view.findViewById(R.id.patientTimePicker);
        cancelButton = view.findViewById(R.id.cancelAppointmentButton);
        saveButtom = view.findViewById(R.id.saveAppointmentButton);

        cancelButton.setOnClickListener(this);
        saveButtom.setOnClickListener(this);
        doctor.setOnClickListener(this);
        appointmentDate.setOnClickListener(this);
        appointmentTime.setOnClickListener(this);

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.patientDatePicker:
                datePicker();
                break;

            case R.id.patientTimePicker:
                timePicker();
                break;

            case R.id.patientDoctorPicker:
                customDialog();
                break;

            case R.id.cancelAppointmentButton:
                Navigation.findNavController(requireView()).navigate(R.id.patientDetailsFragment);
                break;

            case R.id.saveAppointmentButton:

                try {
                    saveInDb();
                } catch (ParseException e) {
                    e.printStackTrace();
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

                //Date date = calendarFormat.getTime();

                CharSequence dateSequence = DateFormat.format("EEEE, MMM d, yyyy", calendarFormat);
                appointmentDate.setText(dateSequence);

            }
        },YEAR, MONTH, DAY);
        int oneDay = 86400*1000;
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime()+oneDay);
        datePickerDialog.show();
    }


    //Método para seleccionar la hora de la cita
    private void timePicker(){

        Calendar calendar = Calendar.getInstance();

        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);
        boolean is24Format = DateFormat.is24HourFormat(requireContext());

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar calendarFormat = Calendar.getInstance();

                calendarFormat.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarFormat.set(Calendar.MINUTE, minute);

                CharSequence timeSequence = DateFormat.format("hh:mm a", calendarFormat);
                appointmentTime.setText(timeSequence);

            }
        },HOUR, MINUTE,is24Format);
        timePickerDialog.show();
    }




    //Método para crear un dialog personalizado
    private void customDialog() {

        Dialog dialog = new Dialog(requireContext());
        //Se establece que el dialog no tenga titulo
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Al pulsar fuera del dialog este se cerrará
        dialog.setCancelable(true);
        //Se selecciona el layout correspondiente para el dialog
        dialog.setContentView(R.layout.custom_dialog);
        //Se crea la un viewModel de tipo DoctorInfo
        doctorViewModel = new ViewModelProvider(requireActivity()).get(DoctorInfo.class);

        ProgressBar progressBar = dialog.findViewById(R.id.progressBarDialog);
        TextView noData = dialog.findViewById(R.id.noDataDialog);
        //Se crea un recyclerView para mostrar los médicos disponibles en una lista
        RecyclerView recyclerView = dialog.findViewById(R.id.selectDoctoRview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        ArrayList<DoctorInfo> doctorList = new ArrayList<>();

        db.collection("Doctors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {

                                int appointmentSize;
                                List<String> appointmentList = (List<String>) document.get("appointmentCount");
                                if(appointmentList != null){
                                    appointmentSize = appointmentList.size();
                                }
                                else{
                                    appointmentSize = 0;
                                }
                                DoctorInfo doctor = new DoctorInfo();
                                doctor.setDocumentID(document.getId());
                                doctor.setName(document.getString("name"));
                                doctor.setSpecialty(document.getString("specialty"));
                                doctor.setAppointmentCount(appointmentSize);
                                doctor.setPic(R.drawable.doctor);


                                doctorList.add(doctor);

                            }

                            if (doctorList.isEmpty()){
                                noData.setVisibility(View.VISIBLE);
                            }

                            adapter = new CustomDoctorAdapter(doctorList);

                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.INVISIBLE);

                            adapter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String dDocumentID = doctorList.get(recyclerView.getChildAdapterPosition(v)).getDocumentID();
                                    int appointmentCount = doctorList.get(recyclerView.getChildAdapterPosition(v)).getAppointmentCount();
                                    String name = doctorList.get(recyclerView.getChildAdapterPosition(v)).getName();
                                    String specialty = doctorList.get(recyclerView.getChildAdapterPosition(v)).getSpecialty();

                                    doctorViewModel.setAppointmentCount(appointmentCount);
                                    doctorViewModel.setDocumentID(dDocumentID);
                                    doctorViewModel.setName(name);
                                    doctorViewModel.setSpecialty(specialty);
                                    doctor.setText(name);
                                    dialog.dismiss();

                                }
                            });

                        }
                        else{
                            noData.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(665,735);
    }


    private void saveInDb() throws ParseException {

        String doctorName = doctor.getText().toString();
        String appointmentDate1 = appointmentDate.getText().toString();
        String appointmentTime1 = appointmentTime.getText().toString();
        String patientName = patientViewModel.getName() +" "+ patientViewModel.getLastName();
        String patientId = patientViewModel.getDocumentID();
        String doctorId = doctorViewModel.getDocumentID();
        int appointmentCount = (int) (patientViewModel.getAppointmentCount() + 1);

        if(doctorName.isEmpty()||appointmentDate1.isEmpty()||appointmentTime1.isEmpty()){
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

        //Conversión de las fechas de tipo string a date
        String dateTime = appointmentDate1 + " " + appointmentTime1;
        Date appointmentDateTime = new SimpleDateFormat("EEEE, MMM d, yyyy hh:mm a", Locale.getDefault()).parse(dateTime);


        //Inicializando los datos del historial del paciente para guardarlos en la base de datos
        int onHold = 3;
        String text = getString(R.string.history_text);

        //Objeto médico con los datos necesarios para guardarlos en la base de datos
        Map<String, Object> doctorInfo = new HashMap<>();
        doctorInfo.put("documentId", doctorViewModel.getDocumentID());
        doctorInfo.put("name", doctorViewModel.getName());
        doctorInfo.put("specialty", doctorViewModel.getSpecialty());


        //Objeto historial con sus respectivos parámetros
        History history = new History();
        history.setCount(appointmentCount);
        history.setDoctorName(doctorName);
        history.setPatientName(patientName);
        history.setAppointmentDate(appointmentDateTime);
        history.setAssisted(onHold);
        history.setText(text);


        String historyId = "history."+patientId+appointmentCount;
        String appointmentId = patientId+appointmentCount;


        //Actualiza los datos del paciente con el médico asignado y el historial de las citas
        db.collection("Patients")
                .document(patientId)
                .update("doctor", doctorInfo,
                        "appointmentDate", appointmentDateTime,
                        "appointmentCount", appointmentCount,
                        historyId, history )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Cita guardada con éxito",
                                Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.patientFragment);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error, intenta de nuevo",
                                Toast.LENGTH_SHORT).show();
                    }
                });


        //Actualiza los datos de los médicos con el historial de pacientes
        db.collection("Doctors")
                .document(doctorId)
                .update(historyId, history,
                        "appointmentCount", FieldValue.arrayUnion(appointmentId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("success", "Document successfully updated!");
                    }
                });




    }

}