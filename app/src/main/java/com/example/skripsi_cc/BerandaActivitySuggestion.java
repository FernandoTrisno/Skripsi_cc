package com.example.skripsi_cc;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BerandaActivitySuggestion extends AppCompatActivity {
    RelativeLayout makan_pagi;
    RelativeLayout makan_siang;
    RelativeLayout makan_malam;
    RelativeLayout makan_cemilan;
    RelativeLayout home;
    RelativeLayout report;
    RelativeLayout profil;
    TextView nama_user;
    TextView kebutuhan_kkal;
    TextView jumlah_kkal;
    TextView text_saran;
    FirebaseFirestore db;
    FirebaseUser user;
    String emailuser;
    String tanggal;
    List<ListMakanUser> list = new ArrayList<>();
    ListMakananUserAdapter listMakananAdapter;
    ArrayList<String>total = new ArrayList<>();
    ArrayList<Double>d_total = new ArrayList<>();
    String tamp_kkal;
    String max;
    double d_max;
    double a;
    String persen;
    int persenInt;
    double persen_proggers;
    ProgressBar progressBar;
    Drawable green;
    Drawable red;


    String nama_or;
    String waktu_olahraga;

    double tamp;
    double kelebihan;
    double kalori_or;
    double menit_or;
    double menit;

    TextView btn_selesai;
    Button btn_tidak_done;
    Button btn_ya_done;
    TextView text_done;
    View popup_done;

    Map<String,Object> kalori_keluar = new HashMap<>();



    ArrayList<String>totalMP = new ArrayList<>();
    ArrayList<String>totalMS = new ArrayList<>();
    ArrayList<String>totalMM = new ArrayList<>();
    ArrayList<String>totalMC = new ArrayList<>();


    ArrayList<Double>d_total_pagi = new ArrayList<>();
    ArrayList<Double>d_total_siang = new ArrayList<>();
    ArrayList<Double>d_total_malam = new ArrayList<>();
    ArrayList<Double>d_total_cemilan = new ArrayList<>();

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

    TextView ket_mp,ket_ms,ket_mm,ket_mc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda_suggestion);
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        green = getDrawable(R.drawable.custom_bg_proggresbar);
        red = getDrawable(R.drawable.custom_bg_proggresbar_red);
        btn_selesai = findViewById(R.id.btn_selesai);
        listMakananAdapter = new ListMakananUserAdapter(getApplicationContext(), list);
        ket_mp = findViewById(R.id.ket_mp);
        ket_ms = findViewById(R.id.ket_ms);
        ket_mm = findViewById(R.id.ket_mm);
        ket_mc = findViewById(R.id.ket_mc);
        makan_pagi = findViewById(R.id.button_makan_pagi);
        makan_siang = findViewById(R.id.button_makan_siang);
        makan_malam = findViewById(R.id.button_makan_malam);
        makan_cemilan = findViewById(R.id.button_makan_cemilan);
        nama_user = (TextView) findViewById(R.id.nama_pengguna);
        kebutuhan_kkal =(TextView) findViewById(R.id.kebutuhan_tubuh);
        jumlah_kkal = findViewById(R.id.jumlah_kalori);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        text_saran = findViewById(R.id.suggestion_Text);
        home = findViewById(R.id.home_beranda);
        report = findViewById(R.id.report_beranda);
        profil = findViewById(R.id.profil_beranda);
        nama_or = "lari";
        kalori_or = 122;
        menit_or = 10;

        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

            }
        });


        getTanggal();
        getDataUser();
        ceknama();
        totalKkal();
        kalori_mp();
        kalori_ms();
        kalori_mm();
        kalori_mc();

        makan_pagi.setOnClickListener(view->{
            startActivity(new Intent(BerandaActivitySuggestion.this, DetailMakanPagi.class));
        });
        makan_siang.setOnClickListener(view->{
            startActivity(new Intent(BerandaActivitySuggestion.this, DetailMakanSiang.class));
        });
        makan_malam.setOnClickListener(view->{
            startActivity(new Intent(BerandaActivitySuggestion.this, DetailMakanMalam.class));
        });
        makan_cemilan.setOnClickListener(view->{
            startActivity(new Intent(BerandaActivitySuggestion.this, DetailMakanCemilan.class));
        });
        report.setOnClickListener(view->{
            startActivity(new Intent(BerandaActivitySuggestion.this, ReportActivity.class));
        });
        profil.setOnClickListener(view->{
            startActivity(new Intent(BerandaActivitySuggestion.this,ProfilActivity.class));
        });

        btn_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void getDataUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            emailuser = user.getEmail();
        }
    }
    private void getTanggal(){
        Date skr = Calendar.getInstance().getTime();
        SimpleDateFormat waktu = new SimpleDateFormat("dd-MM-yyyy");
        tanggal = waktu.format(skr);
        System.out.println("Tanggal Sekarang = " + tanggal);
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


    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(BerandaActivitySuggestion.this);
        popup_done = LayoutInflater.from(BerandaActivitySuggestion.this).inflate(R.layout.popup_done,null);
        builder.setView(popup_done);

        btn_ya_done = popup_done.findViewById(R.id.btn_ya_done);
        btn_tidak_done = popup_done.findViewById(R.id.btn_tidak_done);
        text_done = popup_done.findViewById(R.id.text_done);

        text_done.setText("Apakah anda telah menyelesaikan olahraga ?");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btn_tidak_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        btn_ya_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kalori_keluar.put("jumlah_kalori","0");
                db.collection(emailuser).document(tanggal).collection("Kalori Keluar").document("lari").set(kalori_keluar, SetOptions.merge());
            startActivity(new Intent(BerandaActivitySuggestion.this,BerandaActivitySport.class));
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
        tamp = Double.parseDouble(tamp_kkal);
        jumlah_kkal.setText(tamp_kkal);

        DecimalFormat df = new DecimalFormat("#");
        persen_proggers = (a / d_max) * 100;
        persen = (df.format(persen_proggers));
        persenInt = Integer.parseInt(persen);
        setProgressBar();
        System.out.println("isi persen_proggres :" + persen_proggers);

        kelebihan =  a - d_max ;
        System.out.println("cek a : "+a);
        System.out.println("cek tamp : "+tamp);
        System.out.println("cek kelebihan : "+kelebihan);

        menit = (kelebihan / kalori_or)*menit_or;

        System.out.println("cek nilai menit or : "+menit);

        if(menit%1==0){
            DecimalFormat decimalFormat = new DecimalFormat("#");
            waktu_olahraga = (decimalFormat.format(menit));
        }else{
            DecimalFormat decimalFormat = new DecimalFormat("#");
            menit++;
            waktu_olahraga = (decimalFormat.format(menit));
        }

        text_saran.setText("Anda perlu melakukan olahraga : " + nama_or + " selama " + waktu_olahraga + " menit");
    }



    public void ceknama(){
        DocumentReference docRef = db.collection("users").document(emailuser);
        Source source = Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    nama_user.setText(documentSnapshot.getString("users"));
                    kebutuhan_kkal.setText(documentSnapshot.getString("kebutuhan kalori"));
                    max=documentSnapshot.getString("kebutuhan kalori");
                    System.out.println("Cek nilai MAX : " + max);
                    d_max = Double.parseDouble(max);
                }else{
                    Log.d(TAG, "Cache get failed " + task.getException());
                }
            }
        });
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
                        if(pagi==0){
                            ket_mp.setText("Belum makan pagi !");
                        }if(pagi>0){
                            ket_mp.setText("Detail makan pagi");
                        }
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
                        if(siang==0){
                            ket_ms.setText("Belum makan siang !");
                        }if(siang>0){
                            ket_ms.setText("Detail makan siang");
                        }

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
                        if(malam==0){
                            ket_mm.setText("Belum makan malam !");
                        }if(malam>0){
                            ket_mm.setText("Detail makan malam");
                        }
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
                        if(cemilan==0){
                            ket_mc.setText("Belum makan cemilan !");
                        }if(cemilan>0){
                            ket_mc.setText("Detail makan cemilan");
                        }
                    }
                });

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

    public void setProgressBar(){
        if(persenInt>75){
            progressBar.setProgressDrawable(red);
            progressBar.setMax(100);
            progressBar.setProgress(persenInt);
        }else{
            progressBar.setProgressDrawable(green);
            progressBar.setMax(100);
            progressBar.setProgress(persenInt);

        }
    }

}

