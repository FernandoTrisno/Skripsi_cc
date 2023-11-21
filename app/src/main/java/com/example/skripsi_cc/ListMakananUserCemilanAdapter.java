package com.example.skripsi_cc;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListMakananUserCemilanAdapter extends RecyclerView.Adapter<ListMakananUserCemilanAdapter.MyViewHolder>{
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


    public ListMakananUserCemilanAdapter(Context context, List<ListMakanUser> list, Dialog dialog, AppCompatActivity activity, Map<String,Object> catatMakan, Button btnsimpan, RecyclerView listkalori) {
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

    public ListMakananUserCemilanAdapter(Context context, List<ListMakanUser>list){
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView list_nama_makanan,porsi,jmlh_kkal,text_nama_makanan,text_jumlah_kalori,satuan_porsi,text_nama_delete,text_kalori_delete;
        ItemClickListener itemClickListener;
        Button btnEdit,btnDelete, btn_edit_simpan, btn_edit_batal, btn_delete_delete, btn_delete_batal;
        EditText edit_text;
        RecyclerView listMP;
        String id_makanan;
        String data_porsi;
        String data_kalori;
        View popup_edit;
        double t_kkal;
        double t_porsi;
        double i_porsi;
        Map<String,Object> edit_data = new HashMap<>();
        View popup_delete;

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    popup_edit = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.popup_edit,null);
                    builder.setView(popup_edit);

                    btn_edit_simpan = popup_edit.findViewById(R.id.btn_simpan_edit);
                    btn_edit_batal = popup_edit.findViewById(R.id.btn_batal_edit);
                    text_nama_makanan = popup_edit.findViewById(R.id.text_nama_makanan);
                    text_jumlah_kalori = popup_edit.findViewById(R.id.text_jumlah_kalori);
                    edit_text = popup_edit.findViewById(R.id.editText);

                    text_nama_makanan.setText(tamp_nama);
                    text_jumlah_kalori.setText("@" + tamp_kkal +" Kkal");


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
                            DecimalFormat df;

                            if (t_porsi == 1){
                                edit_kkal = t_kkal * i_porsi;
                                df = new DecimalFormat("#");
                                data_kalori = df.format(edit_kkal);
                            }else{
                                a_kkal = t_kkal/t_porsi; // mencari kkal @1
                                edit_kkal = a_kkal * i_porsi ;
                                if (edit_kkal % 1 != 0){
                                    df = new DecimalFormat(".##");
                                    data_kalori = df.format(edit_kkal);
                                }else {
                                    df = new DecimalFormat("#");
                                    data_kalori = df.format(edit_kkal);
                                }

                            }
                            id_makanan = tamp_nama;
                            data_porsi = input_porsi;
                            edit_data.put("nama_makanan",id_makanan);
                            edit_data.put("jumlah_kalori",data_kalori);
                            edit_data.put("porsi",input_porsi);
                            db.collection(emailuser).document(tanggal).collection("Total_Kalori").document("MC"+id_makanan).update("jumlah kkal",data_kalori);
                            db.collection(emailuser).document(tanggal).collection("Makan Cemilan").document(id_makanan).set(edit_data, SetOptions.merge());
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    popup_delete = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.popup_delete,null);
                    builder.setView(popup_delete);

                    btn_delete_delete = popup_delete.findViewById(R.id.btn_delete_delete);
                    btn_delete_batal = popup_delete.findViewById(R.id.btn_batal_delete);
                    text_nama_delete = popup_delete.findViewById(R.id.text_nama_makanan);
                    text_kalori_delete = popup_delete.findViewById(R.id.text_jumlah_kalori);
                    satuan_porsi = popup_delete.findViewById(R.id.satuan_porsi);

                    text_nama_delete.setText(tamp_nama);
                    text_kalori_delete.setText("@" + tamp_kkal +" Kkal");
                    satuan_porsi.setText(tamp_porsi +" porsi");

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
                            db.collection(emailuser).document(tanggal).collection("Total_Kalori").document("MC"+tamp_nama).delete();
                            db.collection(emailuser).document(tanggal).collection("Total_MC").document("MC"+tamp_nama).delete();
                            db.collection(emailuser).document(tanggal).collection("Makan Cemilan").document(tamp_nama).delete();
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
