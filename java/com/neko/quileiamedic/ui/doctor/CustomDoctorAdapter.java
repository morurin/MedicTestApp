package com.neko.quileiamedic.ui.doctor;

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

public class CustomDoctorAdapter extends RecyclerView.Adapter<CustomDoctorAdapter.CustomViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    private View.OnClickListener listener;
    private View.OnLongClickListener longListener;
    private final ArrayList<DoctorInfo> doctorList;

    public CustomDoctorAdapter(ArrayList<DoctorInfo> doctorList) {

        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomDoctorAdapter.CustomViewHolder holder, int position) {

        holder.dName.setText(doctorList.get(position).getName());
        holder.dSpecialty.setText(doctorList.get(position).getSpecialty());
        holder.dPic.setImageResource(doctorList.get(position).getPic());

    }

    @Override
    public int getItemCount() {
        return doctorList.size();
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
        private final TextView dName;
        private final TextView dSpecialty;
        private final ImageView dPic;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            dName = itemView.findViewById(R.id.nameView);
            dSpecialty = itemView.findViewById(R.id.details);
            dPic = itemView.findViewById(R.id.userPicView);





        }
    }
}
