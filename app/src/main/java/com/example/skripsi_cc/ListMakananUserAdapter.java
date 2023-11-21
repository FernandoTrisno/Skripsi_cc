package com.example.skripsi_cc;

import static android.content.ContentValues.TAG;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListMakananUserAdapter extends RecyclerView.Adapter<ListMakananUserAdapter.MyViewHolder>{
    AppCompatActivity activity;
    Button btnEdit;
    Button btnDelete;
    private Context context;
    List<ListMakanUser> list;
    private Dialog dialog;
    FirebaseUser user;
    String emailuser;
    String tanggal;
    ListMakanUser listMakanUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ListMakananUserAdapter(Context context, List<ListMakanUser> list, Dialog dialog, AppCompatActivity activity, Map<String,Object> catatMakan, Button btnsimpan, RecyclerView listkalori) {
        this.activity = activity;
        this.context = context;
        this.list = list;
        this.dialog = dialog;
    }

    private void getDataUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            emailuser = user.getEmail();
        }
    }

    private void getDate(){
        Date skr = Calendar.getInstance().getTime();
        SimpleDateFormat waktu = new SimpleDateFormat("dd-MM-yyyy");
        tanggal = waktu.format(skr);
        System.out.println("Waktu Sekarang = " + tanggal);
    }

    public interface Dialog{
        void onClick(int pos);
    }

    public Dialog getDialog(){
        return dialog;
    }

    public ListMakananUserAdapter(Context context, List<ListMakanUser>list){
        this.context = context;
        this.list = list;
    }

    public void filteradapterlist(List<ListMakanUser>filteradapterlist){
        list = filteradapterlist;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_catat_makan,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.list_nama_makanan.setText(list.get(position).getNama_makanan());
        holder.jmlh_kkal.setText(list.get(position).getJumlah_kalori());
        holder.porsi.setText(list.get(position).getPorsi());
        holder.gram_catat.setText(list.get(position).getGram());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView list_nama_makanan,porsi,jmlh_kkal,gram_catat,text_gram,gram_catat_delete, text_nama_makanan,text_jumlah_kalori,satuan_porsi,text_nama_delete,text_kalori_delete;
        ItemClickListener itemClickListener;
        Button btnEdit,btnDelete, btn_edit_simpan, btn_edit_batal, btn_delete_delete, btn_delete_batal;
        EditText edit_text;
        RecyclerView listMP;
        String id_makanan;
        String data_porsi;
        String data_kalori;
        String data_gram;
        View popup_edit;
        double t_kkal;
        double t_gram;
        double t_porsi;
        double i_porsi;
        Map<String,Object> edit_data = new HashMap<>();
        View popup_delete;
        AppCompatActivity activity;
        public Button getBtn_edit_simpan() {
            return btn_edit_simpan;
        }

        public Map<String, Object> getEdit_data() {
            return edit_data;
        }
        public String getData_kalori() {
            return data_kalori;
        }

        public String getId_makanan() {
            return id_makanan;
        }

        public String getData_porsi() {
            return data_porsi;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listMP = itemView.findViewById(R.id.listMakanPagi);
            list_nama_makanan = itemView.findViewById(R.id.list_nama);
            jmlh_kkal = itemView.findViewById(R.id.jmlh_kkal);
            porsi = itemView.findViewById(R.id.porsi);
            gram_catat = itemView.findViewById(R.id.gram_catat);
            //popup edit
            //popup delete
            btnEdit = itemView.findViewById(R.id.button_edit);
            btnDelete = itemView.findViewById(R.id.button_delete);
            getDataUser();
            getDate();

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tamp_nama = list.get(getAdapterPosition()).getNama_makanan();
                    String tamp_kkal = list.get(getAdapterPosition()).getJumlah_kalori();
                    String tamp_gram = list.get(getAdapterPosition()).getGram();
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    popup_edit = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.popup_edit,null);
                    builder.setView(popup_edit);
                    text_gram = popup_edit.findViewById(R.id.gram_popedit);
                    btn_edit_simpan = popup_edit.findViewById(R.id.btn_simpan_edit);
                    btn_edit_batal = popup_edit.findViewById(R.id.btn_batal_edit);
                    text_nama_makanan = popup_edit.findViewById(R.id.text_nama_makanan);
                    text_jumlah_kalori = popup_edit.findViewById(R.id.text_jumlah_kalori);
                    edit_text = popup_edit.findViewById(R.id.editText);

                    text_nama_makanan.setText(tamp_nama);
                    text_jumlah_kalori.setText("@" + tamp_kkal +" Kkal");
                    text_gram.setText("("+tamp_gram + " gr)");


                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    btn_edit_simpan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String input_porsi = edit_text.getText().toString();
                            String tamp_porsi = list.get(getAdapterPosition()).getPorsi();
                            t_kkal = Double.parseDouble(tamp_kkal);
                            t_porsi = Double.parseDouble(tamp_porsi);
                            i_porsi = Double.parseDouble(input_porsi);
                            double edit_kkal;
                            double a_kkal;
                            double edit_gram;
                            double a_gram;
                            DecimalFormat df;

                            if (t_porsi == 1){
                                edit_kkal = t_kkal * i_porsi;
                                edit_gram = t_gram * i_porsi;
                                df = new DecimalFormat("#");
                                data_kalori = df.format(edit_kkal);
                                data_gram = df.format(edit_gram);
                            }else{
                                a_kkal = t_kkal/t_porsi; // mencari kkal @1
                                a_gram = t_gram/t_porsi;
                                edit_kkal = a_kkal * i_porsi ;
                                edit_gram = a_gram * i_porsi;
                                if (edit_kkal % 1 != 0){
                                    df = new DecimalFormat(".##");
                                    data_kalori = df.format(edit_kkal);
                                    if (edit_gram %1 !=0){
                                        data_gram = df.format(edit_gram);
                                    }else {
                                        df = new DecimalFormat("#");
                                        data_gram = df.format(edit_gram);
                                    }
                                }else {
                                    df = new DecimalFormat("#");
                                    data_kalori = df.format(edit_kkal);
                                    if (edit_gram %1 !=0){
                                        data_gram = df.format(edit_gram);
                                    }else {
                                        df = new DecimalFormat("#");
                                        data_gram = df.format(edit_gram);
                                    }
                                }

                            }
                            id_makanan = tamp_nama;
                            data_porsi = input_porsi;
                            edit_data.put("nama_makanan",id_makanan);
                            edit_data.put("jumlah_kalori",data_kalori);
                            edit_data.put("porsi",input_porsi);
                            edit_data.put("gram)",data_gram);
                            db.collection(emailuser).document(tanggal).collection("Makan Pagi").document(id_makanan).update(edit_data);
                            db.collection(emailuser).document(tanggal).collection("Total_Kalori").document("MP"+id_makanan).update("jumlah kkal",data_kalori);
                            alertDialog.dismiss();
                        }
                    });

                    btn_edit_batal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           alertDialog.cancel();
                        }
                    });
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tamp_nama = list.get(getAdapterPosition()).getNama_makanan();
                    String tamp_kkal = list.get(getAdapterPosition()).getJumlah_kalori();
                    String tamp_porsi = list.get(getAdapterPosition()).getPorsi();
                    String tamp_gram = list.get(getAdapterPosition()).getGram();
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    popup_delete = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.popup_delete,null);
                    builder.setView(popup_delete);

                    btn_delete_delete = popup_delete.findViewById(R.id.btn_delete_delete);
                    btn_delete_batal = popup_delete.findViewById(R.id.btn_batal_delete);
                    text_nama_delete = popup_delete.findViewById(R.id.text_nama_makanan);
                    text_kalori_delete = popup_delete.findViewById(R.id.text_jumlah_kalori);
                    gram_catat_delete = popup_delete.findViewById(R.id.text_gram_delete);
                    satuan_porsi = popup_delete.findViewById(R.id.satuan_porsi);

                    text_nama_delete.setText(tamp_nama);
                    text_kalori_delete.setText("@" + tamp_kkal +" Kkal");
                    satuan_porsi.setText(tamp_porsi +" porsi");
                    gram_catat_delete.setText("/ "+tamp_gram+ "gr");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    btn_delete_batal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();
                        }
                    });

                    btn_delete_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    btn_delete_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.collection(emailuser).document(tanggal).collection("Total_Kalori").document("MP"+tamp_nama).delete();
                            db.collection(emailuser).document(tanggal).collection("Total_MP").document("MP"+tamp_nama).delete();
                            db.collection(emailuser).document(tanggal).collection("Makan Pagi").document(tamp_nama).delete();
                            list.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            alertDialog.dismiss();
                        }
                    });
                }
            });

        }

    }




}
