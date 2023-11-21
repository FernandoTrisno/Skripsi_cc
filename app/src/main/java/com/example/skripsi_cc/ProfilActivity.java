package com.example.skripsi_cc;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.firebase.firestore.Source;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfilActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser user;
    String emailuser;
    String tanggal;
    String namauser;
    TextView nama_profil;
    TextView bb;
    TextView ideal;
    String ideal1;
    String ideal2;
    String status;
    TextView goals;
    TextView tv_status;
    Button update;
    Button logout;
    RelativeLayout nav_home;
    RelativeLayout nav_report;
    RelativeLayout nav_profil;
    Drawable status_red;
    Drawable status_green;
    Drawable status_yellow;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        status_red = getDrawable(R.drawable.custom_status_red_pastel);
        status_green = getDrawable(R.drawable.custom_status_green_pastel);
        status_yellow = getDrawable(R.drawable.custom_status_yellow_pastel);
        nama_profil = findViewById(R.id.nama_profil);
        ideal = findViewById(R.id.ideal_user);
        goals = findViewById(R.id.goals_user);
        bb = findViewById(R.id.bb_profil);
        tv_status = findViewById(R.id.status);
        db = FirebaseFirestore.getInstance();
        update = findViewById(R.id.button_update);
        logout = findViewById(R.id.button_keluar);
        nav_home = findViewById(R.id.home_profil);
        nav_report = findViewById(R.id.report_profil);
        nav_profil = findViewById(R.id.profil_profil);

        getTanggal();
        getDataUser();
        cekprofil();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfilActivity.this,loginActivity.class));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilActivity.this,UpdateActivity.class));
            }
        });

        nav_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilActivity.this,ReportActivity.class));
            }
        });

        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilActivity.this,BerandaActivity.class));
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

    public void cekprofil() {
        DocumentReference docRef = db.collection("users").document(emailuser);
        Source source = Source.CACHE;
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    nama_profil.setText(documentSnapshot.getString("users"));
                    bb.setText(documentSnapshot.getString("berat badan")+" Kg");
                    goals.setText(documentSnapshot.getString("goals"));
                    ideal1 = documentSnapshot.getString("ideal1");
                    ideal2 = documentSnapshot.getString("ideal2");
                    ideal.setText(ideal1+" Kg - "+ideal2+" Kg");
                    status = documentSnapshot.getString("status");
                    if(status.equals("Berat Badan Berlebih")){
                        tv_status.setBackground(status_red);
                        tv_status.setText(" "+status+" ");
                    }else if(status.equals("Berat Badan Normal")) {
                        tv_status.setBackground(status_green);
                        tv_status.setText(" "+status+" ");
                    }else if(status.equals("Berat Badan Kurang")){
                        tv_status.setBackground(status_yellow);
                        tv_status.setText(" "+status+" ");
                    }
                } else {
                    Log.d(TAG, "Cache get failed " + task.getException());
                }
            }
        });
    }



}


