package com.neko.quileiamedic.ui.patient;



import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kyanogen.signatureview.SignatureView;
import com.neko.quileiamedic.History;
import com.neko.quileiamedic.R;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomHistorypAdapter extends RecyclerView.Adapter<CustomHistorypAdapter.CustomViewHolder>
        implements View.OnClickListener {

    private View.OnClickListener listener;
    private final ArrayList<History> historyList;
    private final String patientId, doctorId;
    private String num;
    private final int assisted = 1;
    private final int unassisted = 2;
    private final int onHold = 3;



    public CustomHistorypAdapter(ArrayList<History> historyList, String patientId, String doctorId) {

        this.historyList = historyList;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    @NonNull
    @Override
    public CustomHistorypAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        view.setOnClickListener(this);


        return new CustomHistorypAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHistorypAdapter.CustomViewHolder holder, int position) {

        //Se definen los diferentes par??metros a mostrar dependiendo de la informaci??n almacenada
        //en la base de datos, como los diferentes iconos, botones, texto
        String appointment;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy hh:mm a", Locale.getDefault());
        Date appointmentDate = historyList.get(position).getAppointmentDate();
        Date currentTime = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(appointmentDate);
        calendar.add(Calendar.MINUTE, 45);
        appointmentDate = calendar.getTime();

        String text = historyList.get(position).getText();
        int isAssisted = historyList.get(position).getAssisted();
        int count = (int) historyList.get(position).getCount();
        String n = Integer.toString(count);
        boolean isExpanded = historyList.get(position).isExpanded();
        boolean attended = historyList.get(position).isAttended();
        String name = "M??dico: " + historyList.get(position).getDoctorName();



        appointment = simpleDateFormat.format(appointmentDate);
        holder.assistedButton.setVisibility(isExpanded ? View.VISIBLE: View.GONE);
        holder.doctorName.setText(name);
        holder.appointmentDate.setText(appointment);
        holder.text.setText(text);
        holder.number.setText(n);


        //1 = Asistido a la cita
        //2 = No asistido a la cita
        //3 = En espera de confirmaci??n
        if (isAssisted == assisted){
            holder.assistedIcon.setVisibility(View.VISIBLE);
            if (!attended){
                holder.signatureButton.setVisibility(View.VISIBLE);
            }
            else{
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
                View view = holder.itemView;
                historyList.get(position).setAssisted(unassisted);

                updateInDb(position, context, view);
                holder.unassistedIcon.setVisibility(View.VISIBLE);
            }
        }



    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {

        if(listener != null){
            listener.onClick(v);
        }
    }



    public  class CustomViewHolder extends RecyclerView.ViewHolder {


        private final TextView doctorName;
        private final TextView appointmentDate;
        private final TextView text;
        private final ImageView assistedIcon, unassistedIcon, onHoldIcon, signatureIcon;
        private final Button assistedButton, signatureButton;
        private final TextView number;



        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);


            doctorName = itemView.findViewById(R.id.nameView);
            appointmentDate = itemView.findViewById(R.id.appointmentDateView);
            text = itemView.findViewById(R.id.justText);
            assistedIcon = itemView.findViewById(R.id.assistedIcon);
            unassistedIcon = itemView.findViewById(R.id.unassistedIcon);
            onHoldIcon = itemView.findViewById(R.id.onHoldIcon);
            signatureIcon = itemView.findViewById(R.id.signatureIcon);
            ConstraintLayout topLayout = itemView.findViewById(R.id.top_layout);
            assistedButton = itemView.findViewById(R.id.saveAssistedButton);
            signatureButton = itemView.findViewById(R.id.signatureButton);
            number = itemView.findViewById(R.id.n);
            Context context = itemView.getContext();

            assistedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    historyList.get(getAdapterPosition()).setAssisted(assisted);
                    updateInDb(getAdapterPosition(), context, itemView);

                    assistedIcon.setVisibility(View.VISIBLE);
                    assistedButton.setVisibility(View.GONE);
                    onHoldIcon.setVisibility(View.INVISIBLE);
                    signatureButton.setVisibility(View.VISIBLE);

                    Toast.makeText(context,"Asistencia confirmada",
                            Toast.LENGTH_SHORT).show();

                    }
            });

            //Al pulsar el bot??n se abrir?? un canvas donde el paciente podr?? firmar
            signatureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    signatureDialog(itemView, signatureButton, signatureIcon, getAdapterPosition());
                }
            });

            //Al pulsar el icono de "gesture" se mostrar?? la firma almacenada en la base de datos
            signatureIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showSignatureDialog(itemView, context, getAdapterPosition());
                }
            });

            //Se mostrar?? el bot??n de asistencia del paciente al pulsar sobre el layout
            topLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int isAssisted = historyList.get(getAdapterPosition()).getAssisted();

                    if(isAssisted == assisted || isAssisted == unassisted){
                        return;
                    }
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {

                            num = number.getText().toString();
                            History history = historyList.get(getAdapterPosition());
                            history.setExpanded(!history.isExpanded());
                            notifyItemChanged(getAdapterPosition());


                        }
                    }

            });

        }
    }



    //M??todo para actualizar los datos en la base de datos
    private void updateInDb(int position, Context context, View view) {

        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        //Recoge si el paciente ha asistido a la cita
        int isAssisted = historyList.get(position).getAssisted();
        //Recoge si el paciente ha sido atendido por el m??dico
        boolean attended = historyList.get(position).isAttended();

        String updateText = "";
        //La asistencia por defecto est?? en "en espera"
        int assistance = onHold;

        TextView number = view.findViewById(R.id.n);
        TextView text = view.findViewById(R.id.justText);

        //N??mero ??nico de la cita
        num = number.getText().toString();
        String assistedText = context.getString(R.string.assisted_text);
        String unassistedText = context.getString(R.string.unassisted_text);
        String attendedText = context.getString(R.string.attended_text);

        //Ruta para actualizar la informaci??n del paciente
        String historyId = "history."+patientId+num;


        //Si el paciente asisti?? y/o fue atendido en su cita, se actualizar?? el texto en la base de datos
        if(isAssisted == assisted){
            updateText = assistedText;
            assistance = assisted;

            //Si el paciente fue atendido se a??adir?? un texto extra
            if (attended){
                updateText = assistedText +"\n"+attendedText;
            }

        }
        else if (isAssisted == unassisted){
            updateText = unassistedText;
            assistance = unassisted;
        }

        if(position != RecyclerView.NO_POSITION){

            //Diferentes rutas de los documentos en la base de datos
            String assistedDb = historyId+".assisted";
            String textDb = historyId+".text";
            String attendedDb = historyId+".attended";

            db.collection("Patients")
                    .document(patientId)
                    .update(assistedDb, assistance,
                            textDb, updateText,
                            attendedDb,attended)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

            //Se actualiza conjuntamente el historial del m??dico
            db.collection("Doctors")
                    .document(doctorId)
                    .update(assistedDb, assistance,
                            textDb, updateText,
                            attendedDb, attended)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

            //Se establece el nuevo texto en el textview
            text.setText(updateText);

        }
    }


    //M??todo para mostrar el lienzo donde firmar?? el paciente
    public void signatureDialog(View view, Button signatureButton, ImageView signatureIcon, int position){

        SignatureView signatureView;
        Button saveButton, clearButton;


        Dialog dialog = new Dialog(view.getContext());
        //Se establece que el dialog no tenga titulo
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Al pulsar fuera del dialog este se cerrar??
        dialog.setCancelable(true);
        //Se selecciona el layout correspondiente para el dialog
        dialog.setContentView(R.layout.signature_layout);

        signatureView = dialog.findViewById(R.id.signatureView);
        saveButton = dialog.findViewById(R.id.saveSignature);
        clearButton = dialog.findViewById(R.id.cancelSignature);


        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(675,795);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signatureButton.setVisibility(View.GONE);
                signatureIcon.setVisibility(View.VISIBLE);
                //Se establece que el paciente ha firmado
                historyList.get(position).setAttended(true);
                Bitmap bitmap = signatureView.getSignatureBitmap();

                //Se env??a la informaci??n al m??todo para subir la imagen
                uploadImage(bitmap, view.getContext(), position);
                //Se env??a la informaci??n a la base de datos de que el paciente a firmado
                updateInDb(position, view.getContext(), view);
                dialog.dismiss();
            }
        });
    }



    //M??todo para mostrar la firma en pantalla cargada desde de firebase storage
    private void showSignatureDialog(View view, Context context, int position) {

        ImageView signatureImage;
        ProgressBar progressBar;
        StorageReference storageRef;
        //Tama??o de la imagen
        final long ONE_MEGABYTE = 400 * 400;
        //N??mero ??nico de la cita
        int num = (int) historyList.get(position).getCount();
        //Ruta de la imagen en Firebase storage
        String path = patientId+"/"+patientId+num;
        Dialog dialog = new Dialog(view.getContext());
        //Se establece que el dialog no tenga titulo
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Al pulsar fuera del dialog este se cerrar??
        dialog.setCancelable(true);
        //Se selecciona el layout correspondiente para el dialog
        dialog.setContentView(R.layout.show_signature);

        //ID de la imagen donde se mostrar?? la firma
        signatureImage = dialog.findViewById(R.id.signatureImage);
        progressBar = dialog.findViewById(R.id.progressBarImage);

        storageRef = FirebaseStorage.getInstance().getReference(path);

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                //Conversi??n de los bytes obtenidos a bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length, options);

                if(bitmap != null) {
                    signatureImage.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    Toast.makeText(view.getContext(),view.getContext().getString(R.string.error_loading_image),
                            Toast.LENGTH_SHORT).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(context,context.getString(R.string.error_loading_image),
                        Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(675,675);


    }




    //M??todo para guardar una imagen en Firebase storage
    public void uploadImage(Bitmap bitmap, Context context, int position){

        StorageReference storageRef;
        //N??mero ??nico de la cita
        int num = (int) historyList.get(position).getCount();
        //Ruta para almacenar la imagen
        String path = patientId +"/"+patientId+num;
        storageRef = FirebaseStorage.getInstance().getReference(path);

        //Conversi??n de bitmap a byte[]
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bytes);
        byte[] data = bytes.toByteArray();

        //Subida de los datos a Firebase storage
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Guardado con ??xito", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error "+ e, Toast.LENGTH_SHORT).show();
            }
        });
    }


}

