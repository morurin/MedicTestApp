package com.neko.quileiamedic.ui.patient;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neko.quileiamedic.R;

import java.util.ArrayList;

public class CustomPatientAdapter extends RecyclerView.Adapter<CustomPatientAdapter.CustomViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private View.OnClickListener listener;
    private View.OnLongClickListener longListener;
    private final ArrayList<PatientInfo> patientList;

    public CustomPatientAdapter(ArrayList<PatientInfo> patientList) {

        this.patientList = patientList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new CustomPatientAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {


        String name = patientList.get(position).getName() + " " + patientList.get(position).getLastName();
        String id = "Dni: " +patientList.get(position).getId();
        holder.pName.setText(name);
        holder.pId.setText(id);
        holder.pPic.setImageResource(patientList.get(position).getPic());

    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongClickListener(View.OnLongClickListener longListener) {
        this.longListener =  longListener;
    }

    @Override
    public void onClick(View v) {

        if(listener != null){
            listener.onClick(v);
        }
    }

    @Override
    public boolean onLongClick(View v) {

        if(longListener != null) {
            longListener.onLongClick(v);
        }
        return false;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        private final TextView pName;
        private final TextView pId;
        private final ImageView pPic;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            pName = itemView.findViewById(R.id.nameView);
            pId = itemView.findViewById(R.id.details);
            pPic = itemView.findViewById(R.id.userPicView);
        }
    }
}

