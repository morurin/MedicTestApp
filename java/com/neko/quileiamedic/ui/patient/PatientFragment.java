package com.neko.quileiamedic.ui.patient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.neko.quileiamedic.R;
import com.neko.quileiamedic.ui.doctor.DoctorInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class PatientFragment extends Fragment implements PopupMenu.OnMenuItemClickListener, View.OnClickListener{

    private SwipeRefreshLayout swipeRefreshLayoutP;
    private RecyclerView recyclerView;
    private ArrayList<PatientInfo> patientList;
    private ProgressBar progressBar;
    private FloatingActionButton addPatientButton;
    private FirebaseFirestore db;
    private CustomPatientAdapter adapter;
    private PatientInfo patientViewModel;
    private TextView noData;
    private String documentID;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_patient, container, false);

        db = FirebaseFirestore.getInstance();
        patientViewModel = new ViewModelProvider(requireActivity()).get(PatientInfo.class);
        progressBar = view.findViewById(R.id.progressBarHistoryP);
        patientList = new ArrayList<>();
        noData = view.findViewById(R.id.noHistoryText);
        addPatientButton = view.findViewById(R.id.addPatientButton);
        addPatientButton.setOnClickListener((View.OnClickListener) this);

        recyclerView = view.findViewById(R.id.patientView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        readDb(false);

        swipeRefreshLayoutP = view.findViewById(R.id.refreshPatientLayout);

        //Refrescar layout con gesto hacia abajo
        swipeRefreshLayoutP.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                readDb(true);
                patientList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void readDb(boolean refreshed){

        db.collection("Patients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())) {


                                Map<String, String> map = (Map<String, String>) document.get("doctor");

                                DoctorInfo doctorInfo = new DoctorInfo();
                                if(map != null){
                                    doctorInfo.setDocumentID(map.get("documentId"));
                                    doctorInfo.setName(map.get("name"));
                                    doctorInfo.setSpecialty(map.get("specialty"));
                                }

                                PatientInfo patient = new PatientInfo
                                        (document.getId(),
                                        document.getString("name"),
                                        document.getString("lastname"),
                                        document.getDate("birthdate"),
                                        document.getString("id"),
                                        doctorInfo,
                                        document.getBoolean("treatment"),
                                        document.getDouble("quota"),
                                        document.getDate("appointmentDate"),
                                        R.drawable.patient,
                                        document.getDouble("appointmentCount"));


                                patientList.add(patient);

                            }

                            if (patientList.isEmpty()){
                                noData.setVisibility(View.VISIBLE);
                            }

                            if(refreshed){
                                adapter.notifyDataSetChanged();
                                swipeRefreshLayoutP.setRefreshing(false);
                            }

                        }
                        else{
                            noData.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        adapter = new CustomPatientAdapter(patientList);
                        setOnclick(adapter);

                    }
                });

    }



    @Override
    public void onClick(View v) {
        requireActivity().getViewModelStore().clear();
        Navigation.findNavController(v).navigate(R.id.newPatientFragment);

    }

    private void setOnclick(CustomPatientAdapter adapter){

        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);

        adapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveDataModel(v);
                showPopup(v);

                return false;
            }
        });

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveDataModel(v);
                Navigation.findNavController(requireView()).navigate(R.id.patientDetailsFragment);


            }
        });


    }

    private void saveDataModel(View v){

        documentID = patientList.get(recyclerView.getChildAdapterPosition(v)).getDocumentID();
        double appointmentCount = patientList.get(recyclerView.getChildAdapterPosition(v)).getAppointmentCount();
        String name = patientList.get(recyclerView.getChildAdapterPosition(v)).getName();
        String lastName = patientList.get(recyclerView.getChildAdapterPosition(v)).getLastName();
        Date birthdate = patientList.get(recyclerView.getChildAdapterPosition(v)).getBirthdate();
        String id = patientList.get(recyclerView.getChildAdapterPosition(v)).getId();
        DoctorInfo doctor1 = patientList.get(recyclerView.getChildAdapterPosition(v)).getDoctor();
        boolean treatment = patientList.get(recyclerView.getChildAdapterPosition(v)).isTreatment();
        double quota = patientList.get(recyclerView.getChildAdapterPosition(v)).getQuota();
        Date appointmentDate = patientList.get(recyclerView.getChildAdapterPosition(v)).getAppointmentDate();
        int pic = patientList.get(recyclerView.getChildAdapterPosition(v)).getPic();




        patientViewModel.setDocumentID(documentID);
        patientViewModel.setAppointmentCount(appointmentCount);
        patientViewModel.setName(name);
        patientViewModel.setLastName(lastName);
        patientViewModel.setBirthdate(birthdate);
        patientViewModel.setId(id);
        patientViewModel.setDoctor(doctor1);
        patientViewModel.setTreatment(treatment);
        patientViewModel.setQuota(quota);
        patientViewModel.setAppointmentDate(appointmentDate);
        patientViewModel.setPic(pic);

    }

    private void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v, Gravity.END);
        popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popupMenu.inflate(R.menu.recycler_menu);
        popupMenu.show();


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage(getString(R.string.want_to_delete_user))
                        .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                db.collection("Patients")
                                        .document(documentID)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(requireContext(),
                                                            getString(R.string.deleted_user),
                                                            Toast.LENGTH_SHORT).show();

                                                    patientList.clear();
                                                    readDb(false);

                                                } else {
                                                    Toast.makeText(requireContext(),
                                                            getString(R.string.error_deleting_user),
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();


                return true;

            case R.id.edit:
                Navigation.findNavController(requireView()).navigate(R.id.newPatientFragment);

                return true;

            default:
                return false;

        }
    }



}