package com.neko.quileiamedic.ui.doctor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neko.quileiamedic.History;
import com.neko.quileiamedic.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomHistorydAdapter extends RecyclerView.Adapter<CustomHistorydAdapter.CustomViewHolder>
implements View.OnClickListener{


    private View.OnClickListener listener;
    private final ArrayList<History> historyList;

    public CustomHistorydAdapter(ArrayList<History> historyList) {
        this.historyList = historyList;
    }


    @NonNull
    @Override
    public CustomHistorydAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        view.setOnClickListener(this);
        return new CustomViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CustomHistorydAdapter.CustomViewHolder holder, int position) {

        String patientName1 = "Paciente: " +historyList.get(position).getPatientName();
        int isAssisted = historyList.get(position).getAssisted();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy hh:mm a", Locale.getDefault());
        Date appointmentDate = historyList.get(position).getAppointmentDate();
        Date currentTime = new Date();
        boolean attended = historyList.get(position).isAttended();
        String text1 = historyList.get(position).getText();


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointmentDate);
        calendar.add(Calendar.MINUTE, 45);
        appointmentDate = calendar.getTime();


        String appointment = simpleDateFormat.format(appointmentDate);
        holder.patientName.setText(patientName1);
        holder.appointmentDate.setText(appointment);
        holder.text.setText(text1);

        //1 = Asistido a la cita
        //2 = No asistido a la cita
        //3 = En espera de confirmación
        int assisted = 1;
        int unassisted = 2;
        if (isAssisted == assisted){
            holder.assistedIcon.setVisibility(View.VISIBLE);

            if(attended){
                holder.signatureIcon.setVisibility(View.VISIBLE);
            }
        }
        else if(isAssisted == unassisted){
            holder.unassistedIcon.setVisibility(View.VISIBLE);
        }
        else {
            holder.onHoldIcon.setVisibility(View.VISIBLE);

            if(currentTime.after(appointmentDate)){

                Context context = holder.itemView.getContext();
                String unassistedText = context.getString(R.string.unassisted_text);

                historyList.get(position).setAssisted(unassisted);
                holder.unassistedIcon.setVisibility(View.VISIBLE);
                holder.text.setText(unassistedText);
            }
        }



    }



    @Override
    public int getItemCount() {
        return historyList.size();
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onClick(v);
        }
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }


    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        private final TextView patientName;
        private final TextView appointmentDate;
        private final TextView text;
        private final ImageView assistedIcon, unassistedIcon, onHoldIcon, signatureIcon;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.nameView);
            appointmentDate = itemView.findViewById(R.id.appointmentDateView);
            text = itemView.findViewById(R.id.justText);
            assistedIcon = itemView.findViewById(R.id.assistedIcon);
            unassistedIcon = itemView.findViewById(R.id.unassistedIcon);
            onHoldIcon = itemView.findViewById(R.id.onHoldIcon);
            signatureIcon = itemView.findViewById(R.id.signatureIcon);


        }
    }


}
