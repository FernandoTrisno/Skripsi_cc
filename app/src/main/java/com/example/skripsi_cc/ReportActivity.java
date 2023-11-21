package com.example.skripsi_cc;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ReportActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser user;
    String emailuser;
    String tanggal;
    String namauser;
    ArrayList<String>total = new ArrayList<>();
    ArrayList<String>totalMP = new ArrayList<>();
    ArrayList<String>totalMS = new ArrayList<>();
    ArrayList<String>totalMM = new ArrayList<>();
    ArrayList<String>totalMC = new ArrayList<>();
    ArrayList<Double>d_total = new ArrayList<>();
    ArrayList<Double>d_total_pagi = new ArrayList<>();
    ArrayList<Double>d_total_siang = new ArrayList<>();
    ArrayList<Double>d_total_malam = new ArrayList<>();
    ArrayList<Double>d_total_cemilan = new ArrayList<>();

    //Total kkl
    double a;
    String tamp_kkal;
    float total_kkal_user;
    //Total MP
    double apagi;
    float pagi;
    float set_pagi;
    String tamp_kkal_pagi;
    //Total MS
    double asiang;
    float siang;
    float set_siang;
    String tamp_kkal_siang;
    //Total MM
    double amalam;
    float malam;
    float set_malam;
    String tamp_kkal_malam;
    //Total MC
    double acemilan;
    float cemilan;
    float set_cemilan;
    String tamp_kkal_cemnilan;

    Date tanggalprev;
    PieChart pieChart;
    List<String>isi_spinner = new ArrayList<>();
    ArrayAdapter <String> arrayAdapter;
    String isi;

    String max;
    double d_max;
    int colorRed = Color.rgb(224,0,0);



    TextView text_tanggal, kkal_user, max_kkal_user,text_kkal_user,dropdown,t_persen;
    TextView presentase_makan_pagi, presentase_makan_siang, presentase_makan_malam, presentase_makan_cemilan;
    TextView kkal_makan_pagi, kkal_makan_siang, kkal_makan_malam, kkal_makan_cemilan;
    ImageButton prev, next;

    LinearLayout colom_makan_pagi, colom_makan_siang, colom_makan_malam, colom_makan_cemilan;
    RelativeLayout nav_report, nav_home, nav_profil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        text_tanggal = findViewById(R.id.text_tanggal);
        kkal_user = findViewById(R.id.total_kkal_user_report);
        max_kkal_user = findViewById(R.id.batas_kkal_user);
        text_kkal_user = findViewById(R.id.jumlah_kkal_user);
        t_persen = findViewById(R.id.t_persen);
        dropdown = findViewById(R.id.view_drop_down);
        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        presentase_makan_pagi = findViewById(R.id.presentase_makan_pagi);
        presentase_makan_siang = findViewById(R.id.presentase_makan_siang);
        presentase_makan_malam = findViewById(R.id.presentase_makan_malam);
        presentase_makan_cemilan = findViewById(R.id.presentase_makan_cemilan);
        kkal_makan_pagi = findViewById(R.id.kkal_makan_pagi);
        kkal_makan_siang = findViewById(R.id.kkal_makan_siang);
        kkal_makan_malam = findViewById(R.id.kkal_makan_malam);
        kkal_makan_cemilan = findViewById(R.id.kkal_makan_cemilan);
        colom_makan_pagi = findViewById(R.id.colom_makan_pagi);
        colom_makan_siang = findViewById(R.id.colom_makan_siang);
        colom_makan_malam = findViewById(R.id.colom_makan_malam);
        colom_makan_cemilan = findViewById(R.id.colom_makan_cemilan);
        pieChart = findViewById(R.id.piechart);
        db = FirebaseFirestore.getInstance();
        nav_home = findViewById(R.id.home_report);
        nav_report = findViewById(R.id.report_report);
        nav_profil = findViewById(R.id.profil_report);


        getDataUser();
        getTanggal();
        getKaloriUser();
        totalKkal();


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevTanggal();

                pieChart.startAnimation();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTanggal();
                pieChart.startAnimation();
            }
        });

        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
        nav_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this,ProfilActivity.class));
            }
        });

        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this,BerandaActivity.class));
            }
        });


    }

    private void prevTanggal(){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = format.parse(tanggal);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE,-1);
            tanggal = format.format(c.getTime());
            System.out.println(tanggal);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tanggal ="";
        }
    }

    private void nextTanggal(){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = format.parse(tanggal);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE,+1);
            tanggal = format.format(c.getTime());
            System.out.println(tanggal);
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            tanggal ="";
        }
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


    public void getKaloriUser(){
        DocumentReference docRef = db.collection("users").document(emailuser);
        Source source = Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    max = documentSnapshot.getString("kebutuhan kalori");
                    System.out.println("Cek nilai MAX : "+max);
                    d_max = Double.parseDouble(max);
                }else{
                    Log.d(TAG, "Cache get failed " + task.getException());
                }
            }
        });
    }


    public void showMenu(){
        PopupMenu popupMenu = new PopupMenu(this,dropdown);
        popupMenu.getMenuInflater().inflate(R.menu.content_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dropdown_menu1:
                        break;
                    case R.id.dropdown_menu2:
                        startActivity(new Intent(ReportActivity.this,ReportActivityBarChart.class));
                        break;
                    case R.id.dropdown_menu3:
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void kalori_mp(){
        db.collection(emailuser).document(tanggal).collection("Total_MP")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                totalMP.add(document.getString("jumlah kkal"));
                                System.out.println(totalMP);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Data gagal diambil!!",Toast.LENGTH_SHORT).show();
                        }
                        DtoS_pagi();
                        set_pagi = (pagi/total_kkal_user)*100;
                        DecimalFormat df = new DecimalFormat(".##");
                        String persen = df.format(set_pagi);
                        kkal_makan_pagi.setText(pagi + " Kkal");
                        presentase_makan_pagi.setText(persen + "%");
                        pieChart.addPieSlice(
                                new PieModel(
                                        "Makan Pagi",
                                        set_pagi,
                                        Color.parseColor("#bcd9ea")));
                    }
                });
    }

    public void kalori_ms(){
        db.collection(emailuser).document(tanggal).collection("Total_MS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                totalMS.add(document.getString("jumlah kkal"));
                                System.out.println(totalMS);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Data gagal diambil!!",Toast.LENGTH_SHORT).show();
                        }
                        DtoS_total_siang();
                        set_siang = (siang/total_kkal_user)*100;
                        DecimalFormat df = new DecimalFormat(".##");
                        String persen = df.format(set_siang);
                        kkal_makan_siang.setText(siang + " Kkal");
                        presentase_makan_siang.setText(persen + "%");
                        pieChart.addPieSlice(
                                new PieModel(
                                        "Makan Siang",
                                        set_siang,
                                        Color.parseColor("#5ba4cf")));

                    }
                });
    }

    public void kalori_mm(){
        db.collection(emailuser).document(tanggal).collection("Total_MM")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                totalMM.add(document.getString("jumlah kkal"));
                                System.out.println(totalMM);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Data gagal diambil!!",Toast.LENGTH_SHORT).show();
                        }
                        DtoS_total_malam();
                        set_malam = (malam/total_kkal_user)*100;
                        DecimalFormat df = new DecimalFormat(".##");
                        String persen = df.format(set_malam);
                        kkal_makan_malam.setText(malam + " Kkal");
                        presentase_makan_malam.setText(persen + "%");
                        pieChart.addPieSlice(
                                new PieModel(
                                        "Makan Malam",
                                        set_malam,
                                        Color.parseColor("#094c72")));

                    }
                });
    }

    public void kalori_mc(){
        db.collection(emailuser).document(tanggal).collection("Total_MC")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                totalMC.add(document.getString("jumlah kkal"));
                                System.out.println(totalMC);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Data gagal diambil!!",Toast.LENGTH_SHORT).show();
                        }
                        DtoS_total_cemilan();
                        set_cemilan = (cemilan/total_kkal_user)*100;
                        DecimalFormat df = new DecimalFormat(".##");
                        String persen = df.format(set_cemilan);
                        kkal_makan_cemilan.setText(cemilan + " Kkal");
                        presentase_makan_cemilan.setText(persen + "%");
                        pieChart.addPieSlice(
                                new PieModel(
                                        "Makan Cemilan",
                                        set_cemilan,
                                        Color.parseColor("#ffb800")));
                    }
                });
    }

    public void totalKkal() {
        db.collection(emailuser).document(tanggal).collection("Total_Kalori")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                total.add(document.getString("jumlah kkal"));
                                System.out.println(total);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Data gagal diambil!!", Toast.LENGTH_SHORT).show();
                        }
                        DtoS_total();
                        kalori_mp();
                        kalori_ms();
                        kalori_mm();
                        kalori_mc();
                        kkal_user.setText(tamp_kkal);
                        pieChart.startAnimation();

                    }
                });
    }

    public void DtoS_total (){
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
        max_kkal_user.setText("Batas Max: "+max+" Kkal");
        total_kkal_user = Float.parseFloat(tamp_kkal);
        Float tamp_a = Float.parseFloat(max);
        Float tamp = (total_kkal_user/tamp_a)*100 ;

        if(tamp>100){
            float over = tamp-100;
            DecimalFormat df = new DecimalFormat(".##");
            String persen = df.format(over);
            text_kkal_user.setText(persen);
            text_kkal_user.setTextColor(colorRed);
            t_persen.setText("% Melebihi batas");

        }else{
            DecimalFormat df = new DecimalFormat(".##");
            String persen = df.format(tamp);
            text_kkal_user.setText(persen);
            t_persen.setText("% dari Batas Max");
        }

    }

    public void DtoS_pagi(){
        for (int i = 0;i<totalMP.size();i++){
            d_total_pagi.add(Double.parseDouble(totalMP.get(i)));
            System.out.println(d_total_pagi);
        }
        for(int i = 0;i < d_total_pagi.size();i++){
            apagi = apagi + d_total_pagi.get(i);
        }
        if(apagi%1==0){
            DecimalFormat df = new DecimalFormat("#");
            tamp_kkal_pagi = (df.format(apagi));
        }else{
            DecimalFormat df = new DecimalFormat(".##");
            tamp_kkal_pagi = (df.format(apagi));
        }
        System.out.println("Cek isi string :" + tamp_kkal_pagi);
        pagi = Float.parseFloat(tamp_kkal_pagi);
        System.out.println("Cek isi PAGI :" + pagi);


    }

    public void DtoS_total_siang (){
        for (int i = 0;i<totalMS.size();i++){
            d_total_siang.add(Double.parseDouble(totalMS.get(i)));
            System.out.println(d_total_siang);
        }
        for(int i = 0;i < d_total_siang.size();i++){
            asiang = asiang + d_total_siang.get(i);
        }
        if(asiang%1==0){
            DecimalFormat df = new DecimalFormat("#");
            tamp_kkal_siang = (df.format(asiang));
        }else{
            DecimalFormat df = new DecimalFormat(".##");
            tamp_kkal_siang = (df.format(asiang));
        }
        System.out.println("Cek isi string :" + tamp_kkal_siang);
        siang = Float.parseFloat(tamp_kkal_siang);

    }

    public void DtoS_total_malam (){
        for (int i = 0;i<totalMM.size();i++){
            d_total_malam.add(Double.parseDouble(totalMM.get(i)));
            System.out.println(d_total_malam);
        }
        for(int i = 0;i < d_total_malam.size();i++){
            amalam = amalam + d_total_malam.get(i);
        }
        if(amalam%1==0){
            DecimalFormat df = new DecimalFormat("#");
            tamp_kkal_malam = (df.format(amalam));
        }else{
            DecimalFormat df = new DecimalFormat(".##");
            tamp_kkal_malam = (df.format(amalam));
        }
        System.out.println("Cek isi string :" + tamp_kkal_malam);
        malam = Float.parseFloat(tamp_kkal_malam);

    }

    public void DtoS_total_cemilan (){
        for (int i = 0;i<totalMC.size();i++){
            d_total_cemilan.add(Double.parseDouble(totalMC.get(i)));
            System.out.println(d_total_cemilan);
        }
        for(int i = 0;i < d_total_cemilan.size();i++){
            acemilan = acemilan + d_total_cemilan.get(i);
        }
        if(acemilan%1==0){
            DecimalFormat df = new DecimalFormat("#");
            tamp_kkal_cemnilan = (df.format(acemilan));
        }else{
            DecimalFormat df = new DecimalFormat(".##");
            tamp_kkal_cemnilan = (df.format(acemilan));
        }
        System.out.println("Cek isi string :" + tamp_kkal_cemnilan);
        cemilan = Float.parseFloat(tamp_kkal_cemnilan);

    }

}
