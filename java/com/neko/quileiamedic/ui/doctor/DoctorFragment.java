package com.neko.quileiamedic.ui.doctor;

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

import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.neko.quileiamedic.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class DoctorFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private SwipeRefreshLayout swipeRefreshLayoutD;
    private RecyclerView recyclerView;
    private ArrayList<DoctorInfo> doctorList, temporalList;
    private ProgressBar progressBar;
    private FloatingActionButton addDoctorButton;
    private FirebaseFirestore db;
    private CustomDoctorAdapter adapter;
    private DoctorInfo doctorViewModel;
    private TextView noData;
    private String documentID;


    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor, container, false);


        db = FirebaseFirestore.getInstance();
        doctorViewModel = new ViewModelProvider(requireActivity()).get(DoctorInfo.class);
        progressBar = view.findViewById(R.id.progressBarDoctor);
        doctorList = new ArrayList<>();
        noData = view.findViewById(R.id.noDataText);
        addDoctorButton = view.findViewById(R.id.addDoctorButton);
        addDoctorButton.setOnClickListener((View.OnClickListener) this);

        recyclerView = view.findViewById(R.id.doctorView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //lee los datos de la base de datos
        readDb(false);

        swipeRefreshLayoutD = view.findViewById(R.id.refreshDoctorLayout);

        //Refrescar layout con gesto hacia abajo
        swipeRefreshLayoutD.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {

                readDb(true);
                doctorList.clear();
                adapter.notifyDataSetChanged();



            }
        });

        return view;
    }



    //Método onClick
    @Override
    public void onClick(View v) {
        requireActivity().getViewModelStore().clear();
        Navigation.findNavController(v).navigate(R.id.newDoctorFragment);

    }


    //Método para leer los datos en la base de datos
    public void readDb(boolean refreshed){

        db.collection("Doctors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
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

                                DoctorInfo doctor = new DoctorInfo
                                        (document.getString("name"),
                                        document.getId(),
                                        document.getString("code"),
                                        document.getString("specialty"),
                                        document.getDouble("experience").floatValue(),
                                        document.getString("office"),
                                        document.getBoolean("home"),
                                        R.drawable.doctor,
                                        appointmentSize);

                                doctorList.add(doctor);


                            }


                            if (doctorList.isEmpty()){
                                noData.setVisibility(View.VISIBLE);
                            }



                            if(refreshed){
                                adapter.notifyDataSetChanged();
                                swipeRefreshLayoutD.setRefreshing(false);

                            }


                        }
                        else{
                            noData.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                        adapter = new CustomDoctorAdapter(doctorList);
                        setOnclick(adapter);


                    }
                });

    }

    //Método onClick y onLongClick para cada elemento del recyclerView
    public void setOnclick(CustomDoctorAdapter adapter){

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
                Navigation.findNavController(requireView()).navigate(R.id.doctorDetailsFragment);


            }
        });


    }


    //Se guardan los datos en un ViewModel para preservar la información entre las diferentes pantallas
    public void saveDataModel(View v){

        documentID = doctorList.get(recyclerView.getChildAdapterPosition(v)).getDocumentID();
        String name = doctorList.get(recyclerView.getChildAdapterPosition(v)).getName();
        String code = doctorList.get(recyclerView.getChildAdapterPosition(v)).getCode();
        String specialty = doctorList.get(recyclerView.getChildAdapterPosition(v)).getSpecialty();
        float experience = doctorList.get(recyclerView.getChildAdapterPosition(v)).getExperience();
        String office = doctorList.get(recyclerView.getChildAdapterPosition(v)).getOffice();
        boolean home = doctorList.get(recyclerView.getChildAdapterPosition(v)).isHome();
        int pic = doctorList.get(recyclerView.getChildAdapterPosition(v)).getPic();

        doctorViewModel.setName(name);
        doctorViewModel.setDocumentID(documentID);
        doctorViewModel.setCode(code);
        doctorViewModel.setSpecialty(specialty);
        doctorViewModel.setExperience(experience);
        doctorViewModel.setOffice(office);
        doctorViewModel.setHome(home);
        doctorViewModel.setPic(pic);

    }


    //Método para mostrar un popup con las opciones de borrar y editar
    private void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v, Gravity.END);
        popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        popupMenu.inflate(R.menu.recycler_menu);
        popupMenu.show();


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()){

            case R.id.delete:

                //Dialogo de confirmación al eliminar usuario
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage(getString(R.string.want_to_delete_user))
                        .setPositiveButton(getString(R.string.eliminate), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Método para comprobar que no hayan citas pendientes
                                history();
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
                Navigation.findNavController(requireView()).navigate(R.id.newDoctorFragment);

                return true;

            default:
                return false;

        }


    }


    //Método para eliminar al médico de la base de datos
    private void deleteDb(){

        db.collection("Doctors")
                .document(documentID)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(requireContext(),
                                    getString(R.string.deleted_user),
                                    Toast.LENGTH_SHORT).show();

                            doctorList.clear();
                            readDb(false);

                        }
                        else{
                            Toast.makeText(requireContext(),
                                    getString(R.string.error_deleting_user),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    //Método para comprobar el historial del médico en busca de citas pendientes
    //Si encuentra una cita pendiente este no se podrá eliminar de la base de datos
    private void history(){

        db.collection("Doctors")
                .document(documentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //Se obtiene un diccionario anidado de la base de datos
                        HashMap<String, HashMap<String, Object>> historyDocument =
                                (HashMap<String, HashMap<String, Object>>) documentSnapshot.get("history");

                        //Se obtiene un ArrayList de tipo string que contiene todos los Id's de las citas
                        //Con este se sabrá el numero de citas que tiene el usuario
                        ArrayList<String> appointmentList = (ArrayList<String>) documentSnapshot.get("appointmentCount");

                        //Se comprueba que el diccionario y la lista no sean nulos
                        if(historyDocument != null && appointmentList != null){
                            //
                            for(int i = 0; i < appointmentList.size(); i++ ){
                                //String con el número del Id de la cita
                                String id = appointmentList.get(i);

                                int onHold = 3;
                                long longNumber = (long) historyDocument.get(id).get("assisted");
                                int isAssisted = (int) longNumber;

                                if(isAssisted == onHold){
                                    Toast.makeText(requireContext(), getString(R.string.doctor_with_appointments),
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }


                        }
                        //Una vez que se haya comprobado que no hay citas en estado pendiente
                        //Se procederá a borrar al médico
                        deleteDb();

                    }
                });


    }



}