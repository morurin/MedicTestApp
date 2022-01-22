package com.neko.quileiamedic.ui.patient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.neko.quileiamedic.History;
import com.neko.quileiamedic.R;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;



public class PatientHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<History> historyList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private CustomHistorypAdapter historyAdapter;
    private TextView noData;
    private PatientInfo patientViewModel;
    private String doctorDocumentId;
    private LinearLayoutManager layoutManager;
    private int count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient_history, container, false);

        db = FirebaseFirestore.getInstance();
        historyList = new ArrayList<>();
        patientViewModel = new ViewModelProvider(requireActivity()).get(PatientInfo.class);
        String patientDocumentId = patientViewModel.getDocumentID();
        count = (int) patientViewModel.getAppointmentCount();

        try {
            doctorDocumentId = patientViewModel.getDoctor().getDocumentID();

        }
        catch (Exception e){
            doctorDocumentId = "null";
        }

        progressBar = view.findViewById(R.id.progressBarHistoryP);
        noData = view.findViewById(R.id.noHistoryText);
        recyclerView = view.findViewById(R.id.recyclerHistory);

        layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);


        readDB(patientDocumentId, doctorDocumentId);



        return view;
    }




    private void readDB(String patientDocumentId, String doctorDocumentId) {

        db.collection("Patients")
                .document(patientDocumentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        HashMap<String, HashMap<String, Object>> historyDocument =
                                (HashMap<String, HashMap<String, Object>>) documentSnapshot.get("history");



                        if(historyDocument != null){
                            for(int i = 1; i <= count ; i++ ){

                                String id = patientDocumentId+i;
                                Timestamp timestamp = (Timestamp) historyDocument.get(id).get("appointmentDate");
                                Date date = timestamp.toDate();

                                String doctor = (String) historyDocument.get(id).get("doctorName");
                                String patient = (String) historyDocument.get(id).get("patientName");
                                double countN = (double) historyDocument.get(id).get("count");
                                long longNumber = (long) historyDocument.get(id).get("assisted");
                                int isAssisted = (int) longNumber;
                                String text = (String) historyDocument.get(id).get("text");
                                boolean attended = (boolean) historyDocument.get(id).get("attended");


                                History history = new History();
                                history.setCount(countN);
                                history.setDoctorName(doctor);
                                history.setPatientName(patient);
                                history.setAssisted(isAssisted);
                                history.setText(text);
                                history.setAppointmentDate(date);
                                history.setAttended(attended);

                                historyList.add(history);
                            }


                        }
                        else{
                            noData.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        }



                        historyAdapter = new CustomHistorypAdapter(
                                historyList, patientDocumentId, doctorDocumentId);

                        recyclerView.setAdapter(historyAdapter);

                        progressBar.setVisibility(View.INVISIBLE);


                    }
                });
    }
}