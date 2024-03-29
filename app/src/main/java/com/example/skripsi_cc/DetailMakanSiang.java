package com.example.skripsi_cc;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

public class DetailMakanSiang extends AppCompatActivity {
    ImageButton back_siang;
    Button catat_makan_siang;
    RecyclerView listMP;
    List<ListMakanUser> list = new ArrayList<>();
    ListMakananUserSiangAdapter listMakananAdapter;
    FirebaseFirestore db;
    FirebaseUser user;
    String emailuser;
    String tanggal;
    String namauser;
    ArrayList<String>total = new ArrayList<>();
    ArrayList<Double>d_total = new ArrayList<>();
    String tamp_kkal;
    String max;
    double a;
    double max_kkal;
    TextView text_detailms;
    View popup_peringatan;
    TextView text_peringatan;
    Button btn_peringatan;

    Map<String,Object> kalori_keluar = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_siang);
        back_siang = findViewById(R.id.ic_back_siang);
        catat_makan_siang = findViewById(R.id.button_catatsiang);
        text_detailms = findViewById(R.id.text_detailms);
        text_detailms.setVisibility(View.INVISIBLE);
        listMP = (RecyclerView) findViewById(R.id.listMakanSiang);
        db = FirebaseFirestore.getInstance();
        listMP.setHasFixedSize(true);
        listMakananAdapter = new ListMakananUserSiangAdapter(getApplicationContext(), list);
        getDataUser();
        getTanggal();
        getData();
        cek_max_kkal();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        listMP.setLayoutManager(layoutManager);
        listMP.addItemDecoration(decoration);
        listMP.setAdapter(listMakananAdapter);

        if(max_kkal-a < 0){
            showDialog();
        }

        back_siang.setOnClickListener(view->{
            if(max_kkal-a >= 0){
                kalori_keluar.put("jumlah_kalori",max_kkal-a);
                db.collection(emailuser).document(tanggal).collection("Kalori Keluar").document("lari").set(kalori_keluar, SetOptions.merge());
                startActivity(new Intent(DetailMakanSiang.this, BerandaActivity.class));
            }else{
                kalori_keluar.put("jumlah_kalori","0");
                db.collection(emailuser).document(tanggal).collection("Kalori Keluar").document("lari").set(kalori_keluar, SetOptions.merge());
                startActivity(new Intent(DetailMakanSiang.this, BerandaActivitySuggestion.class));
            }
        });
        catat_makan_siang.setOnClickListener(view->{
            startActivity(new Intent(DetailMakanSiang.this, DaftarMakanMinumSiang.class));
        });
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailMakanSiang.this);
        popup_peringatan = LayoutInflater.from(DetailMakanSiang.this).inflate(R.layout.popup_edit,null);
        builder.setView(popup_peringatan);

        text_peringatan = popup_peringatan.findViewById(R.id.text_peringatan);
        btn_peringatan = popup_peringatan.findViewById(R.id.btn_oke_peringatan);

        text_peringatan.setText("Jumlah kalori berlebihan, anda harus melakukan olahraga !!");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btn_peringatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    private void getTanggal() {
        Date skr = Calendar.getInstance().getTime();
        SimpleDateFormat waktu = new SimpleDateFormat("dd-MM-yyyy");
        tanggal = waktu.format(skr);
        System.out.println("Tanggal Sekarang = " + tanggal);
    }

    private void getDataUser() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailuser = user.getEmail();
            namauser = emailuser;
        }
    }

    private void getData() {
        db.collection(emailuser)
                .document(tanggal)
                .collection("Makan Siang")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                ListMakanUser listMakananUser = new ListMakanUser(document.getString("nama_makanan"),document.getString("porsi"),document.getString("jumlah_kalori"),document.getString("gram"));
                                list.add(listMakananUser);
                            }
                            listMakananAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(),"Data gagal diambil!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cek_max_kkal(){
        DocumentReference docRef = db.collection("users").document(emailuser);
        Source source = Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    max=documentSnapshot.getString("kebutuhan kalori");
                    System.out.println("Cek nilai MAX : "+max);
                    max_kkal= Double.parseDouble(max);
                }else{
                    Log.d(TAG, "Cache get failed " + task.getException());
                }
            }
        });
        totalKkal();
    }


    public void totalKkal(){
        db.collection(emailuser).document(tanggal).collection("Total_Kalori")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                total.add(document.getString("jumlah kkal"));
                                System.out.println(total);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Data gagal diambil!!",Toast.LENGTH_SHORT).show();
                        }
                        DtoS();

                    }
                });

    }

    public void DtoS (){
        for (int i = 0;i<total.size();i++){
            d_total.add(Double.parseDouble(total.get(i)));
            System.out.println(d_total);
        }
        for(int i = 0;i < d_total.size();i++){
            a = a + d_total.get(i);
        }
        if(a%1==0){
            DecimalFormat df = new DecimalFormat("#");
            tamp_kkal = (df.format(a));
        }else{
            DecimalFormat df = new DecimalFormat(".##");
            tamp_kkal = (df.format(a));
        }
        System.out.println("Cek isi string :" + tamp_kkal);
        if(a>0){
            text_detailms.setVisibility(View.VISIBLE);
        }
    }

}
