package com.example.skripsi_cc;
import android.content.ClipData;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListMakananAdapter extends RecyclerView.Adapter<ListMakananAdapter.MyViewHolder>{
    AppCompatActivity activity;
    ArrayList<String> idmakanan = new ArrayList<>();
    ArrayList<String> catatkalori = new ArrayList<>();
    ArrayList<String> catatporsi = new ArrayList<>();
    ArrayList<String> catatgram = new ArrayList<>();
    Button btnsimpan;
    private Context context;
    List<ListMakanan> list;
    private Dialog dialog;
    FirebaseUser user;
    String emailuser;
    String tanggal;
    TextView text_nama_makanan,text_jumlah_kalori,gram_popedit,satuan_porsi,text_nama_delete,text_kalori_delete, text_gram;
    Button btnEdit,btnDelete, btn_edit_simpan, btn_edit_batal, btn_delete_delete, btn_delete_batal;
    EditText edit_text;
    RecyclerView listMP;
    RelativeLayout con_layout;
    String id_makanan;
    String data_porsi;
    String data_kalori;
    String data_gram;
    View popup_edit;
    double t_kkal;
    double t_porsi;
    double i_porsi;
    double t_gram;
    Map<String,Object> edit_data = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ListMakananAdapter(Context context, List<ListMakanan> list, Dialog dialog, AppCompatActivity activity, Button btnsimpan,RecyclerView listkalori) {
        this.activity = activity;
        this.btnsimpan = btnsimpan;
        this.context = context;
        this.list = list;
        this.dialog = dialog;
    }

    public ArrayList<String> getCatatkalori() {
        return catatkalori;
    }

    public void setCatatkalori(ArrayList<String> catatkalori) {
        this.catatkalori = catatkalori;
    }

    public ArrayList<String> getCatatporsi() {
        return catatporsi;
    }

    public void setCatatporsi(ArrayList<String> catatporsi) {
        this.catatporsi = catatporsi;
    }

    public ArrayList<String> getIdmakanan() {
        return idmakanan;
    }

    public void setIdmakanan(ArrayList<String> idmakanan) {
        this.idmakanan = idmakanan;
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

    public ListMakananAdapter(Context context,List<ListMakanan>list){
        this.context = context;
        this.list = list;
    }

    public void filteradapterlist(List<ListMakanan>filteradapterlist){
        list = filteradapterlist;
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_makanan,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.list_nama_makanan.setText(list.get(position).getNama_makanan());
        holder.jmlh_kkal.setText(list.get(position).getJumlah_kalori());
        holder.porsi.setText(list.get(position).getPorsi());
        holder.gram.setText(list.get(position).getGram());
        holder.checkBoxlist.setOnCheckedChangeListener(null);
        holder.checkBoxlist.setChecked(list.get(position).isSelected());
        holder.checkBoxlist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String tamp = list.get(holder.getAdapterPosition()).getNama_makanan();
                    String tamp_kkal = list.get(holder.getAdapterPosition()).getJumlah_kalori();
                    String tamp_porsi = list.get(holder.getAdapterPosition()).getPorsi();
                    String tamp_gram = list.get(holder.getAdapterPosition()).getGram();
                    idmakanan.add(tamp);
                    catatkalori.add(tamp_kkal);
                    catatporsi.add(tamp_porsi);
                    catatgram.add(tamp_gram);
                    holder.checkBoxlist.setChecked(true);
                }else{
                    String tamp = list.get(holder.getAdapterPosition()).getNama_makanan();
                    String tamp_kkal = list.get(holder.getAdapterPosition()).getJumlah_kalori();
                    String tamp_porsi = list.get(holder.getAdapterPosition()).getPorsi();
                    idmakanan.remove(holder.getAdapterPosition());
                    catatkalori.remove(holder.getAdapterPosition());
                    catatporsi.remove(holder.getAdapterPosition());
                    catatgram.remove(holder.getAdapterPosition());
                    holder.checkBoxlist.setChecked(false);
                }
            }
        });

        holder.con_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tamp_nama = list.get(holder.getAdapterPosition()).getNama_makanan();
                String tamp_kkal = list.get(holder.getAdapterPosition()).getJumlah_kalori();
                String tamp_gram = list.get(holder.getAdapterPosition()).getGram();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                popup_edit = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.popup_edit,null);
                builder.setView(popup_edit);

                btn_edit_simpan = popup_edit.findViewById(R.id.btn_simpan_edit);
                btn_edit_batal = popup_edit.findViewById(R.id.btn_batal_edit);
                text_nama_makanan = popup_edit.findViewById(R.id.text_nama_makanan);
                text_jumlah_kalori = popup_edit.findViewById(R.id.text_jumlah_kalori);
                gram_popedit = popup_edit.findViewById(R.id.gram_popedit);
                edit_text = popup_edit.findViewById(R.id.editText);

                text_nama_makanan.setText(tamp_nama);
                text_jumlah_kalori.setText("@" + tamp_kkal +" Kkal");
                gram_popedit.setText("("+tamp_gram+" gr)");


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btn_edit_simpan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String input_porsi = edit_text.getText().toString();
                        String tamp_porsi = list.get(holder.getAdapterPosition()).getPorsi();
                        t_kkal = Double.parseDouble(tamp_kkal);
                        t_gram = Double.parseDouble(tamp_gram);
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
                        edit_data.put("gra,",data_gram);
                        db.collection("daftar_makanan").document(id_makanan).update(edit_data);
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView list_nama_makanan,porsi,jmlh_kkal,gram;
        CheckBox checkBoxlist;
        ItemClickListener itemClickListener;
        Button btnsimpan;
        RecyclerView listkalori;
        RelativeLayout con_layout;
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


        public void setItemClickListener(ItemClickListener ic){
            this.itemClickListener = ic;
        }


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            listkalori = itemView.findViewById(R.id.firestoreList);
            list_nama_makanan = itemView.findViewById(R.id.list_nama);
            jmlh_kkal = itemView.findViewById(R.id.jmlh_kkal);
            porsi = itemView.findViewById(R.id.porsi);
            gram = itemView.findViewById(R.id.gram);
            checkBoxlist = itemView.findViewById(R.id.checkBoxlist);
            con_layout = itemView.findViewById(R.id.con_listitem);
            btnsimpan = itemView.findViewById(R.id.button_catat);
            getDataUser();
            getDate();
            checkBoxlist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        Toast.makeText(context, "Checklist" + list_nama_makanan.getText().toString(),Toast.LENGTH_SHORT).show();
                    }if(!isChecked){
                        Toast.makeText(context, "Unchecklist" + list_nama_makanan.getText().toString(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }


}
