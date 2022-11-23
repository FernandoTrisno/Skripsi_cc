package com.example.skripsi_cc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ProfilActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser user;
    String emailuser;
    String tanggal;
    String namauser;
    Button update;
    Button logout;
    RelativeLayout nav_home;
    RelativeLayout nav_report;
    RelativeLayout nav_profil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        db = FirebaseFirestore.getInstance();

        update = findViewById(R.id.button_update);
        logout = findViewById(R.id.button_keluar);
        nav_home = findViewById(R.id.home_profil);
        nav_report = findViewById(R.id.report_profil);
        nav_profil = findViewById(R.id.profil_profil);

        getTanggal();
        getDataUser();

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



}


