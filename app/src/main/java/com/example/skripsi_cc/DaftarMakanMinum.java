package com.example.skripsi_cc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import android.util.Log;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarOutputStream;

public class DaftarMakanMinum extends AppCompatActivity {
    SearchView cari;
    ImageButton back_daftarmakan;
    RecyclerView listkalori;
    FirebaseFirestore db;
    List<ListMakanan> list = new ArrayList<>();
    ListMakananAdapter listMakananAdapter;
    CheckBox cblist;
    Context context;
    Button btn_catatmakanan;
    FirebaseUser user;
    String emailuser;
    String tanggal;
    ArrayList <String> idnama = new ArrayList<>();
    ArrayList <String> catatkkal = new ArrayList<>();
    ArrayList <String> catatporsi = new ArrayList<>();
    RelativeLayout con_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_makan_minum);
        db = FirebaseFirestore.getInstance();
        cari = (SearchView) findViewById(R.id.button_cari);
        back_daftarmakan = findViewById(R.id.ic_back_daftarmakanan);
        cblist = (CheckBox) findViewById(R.id.checkBoxlist);
        listkalori =(RecyclerView) findViewById(R.id.firestoreList);
        btn_catatmakanan = findViewById(R.id.button_catat);
        con_layout = findViewById(R.id.con_listitem);
        listkalori.setHasFixedSize(true);
        listMakananAdapter = new ListMakananAdapter(getApplicationContext(),list);
        getDataUser();
        getTanggal();
        getData();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL);
        listkalori.setLayoutManager(layoutManager);
        listkalori.addItemDecoration(decoration);
        listkalori.setAdapter(listMakananAdapter);
        listkalori.setVisibility(View.INVISIBLE);

        cari.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        btn_catatmakanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked();
                for(int i=0 ; i<idnama.size();i++){
                    Map<String,Object> checklist = new HashMap<>();
                    Map<String,Object> total = new HashMap<>();
                    total.put("jumlah kkal",catatkkal.get(i));
                    checklist.put("nama_makanan",idnama.get(i));
                    checklist.put("jumlah_kalori",catatkkal.get(i));
                    checklist.put("porsi",catatporsi.get(i));
                    db.collection(emailuser).document(tanggal).collection("Makan Pagi").document(idnama.get(i)).set(checklist,SetOptions.merge());
                    db.collection(emailuser).document(tanggal).collection("Total_Kalori").document("MP"+idnama.get(i)).set(total,SetOptions.merge());
                    db.collection(emailuser).document(tanggal).collection("Total_MP").document("MP"+idnama.get(i)).set(total,SetOptions.merge());
                }
                startActivity(new Intent(DaftarMakanMinum.this, DetailMakanPagi.class));
            }
        });
        back_daftarmakan.setOnClickListener(view->{
            startActivity(new Intent(DaftarMakanMinum.this, DetailMakanPagi.class));
        });
    }

    private void getTanggal(){
        Date skr = Calendar.getInstance().getTime();
        SimpleDateFormat waktu = new SimpleDateFormat("dd-MM-yyyy");
        tanggal = waktu.format(skr);
        System.out.println("Tanggal Sekarang = " + tanggal);
    }

    private void getDataUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            emailuser = user.getEmail();
        }
    }

    private void checked (){
        idnama = listMakananAdapter.idmakanan;
        catatkkal = listMakananAdapter.catatkalori;
        catatporsi = listMakananAdapter.catatporsi;
    }

    private void filter (String text){
        ArrayList<ListMakanan> filterlist = new ArrayList<ListMakanan>();
        for(ListMakanan item : list){
            if (item.getNama_makanan().toLowerCase().contains(text.toLowerCase())){
                filterlist.add(item);
                listkalori.setVisibility(View.VISIBLE);
            }
            if(filterlist.isEmpty()){
                listkalori.setVisibility(View.INVISIBLE);
            }else{
                listMakananAdapter.filteradapterlist(filterlist);
            }

        }

    }


    private void getData(){
        db.collection("daftar_makanan")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                ListMakanan listMakanan = new ListMakanan(document.getString("nama_makanan"),document.getString("porsi"),document.getString("jumlah_kalori"));
                                list.add(listMakanan);
                            }
                            listMakananAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getApplicationContext(),"Data gagal diambil!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
