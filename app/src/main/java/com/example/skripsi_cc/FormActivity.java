package com.example.skripsi_cc;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class FormActivity extends AppCompatActivity {
    TextInputEditText berat_badan;
    TextInputEditText tinggi_badan;
    TextInputEditText usia;
    RadioGroup radio_grup;
    RadioButton jenis_kelamin;
    Button hitung;
    TextInputLayout dropdown;
    FirebaseFirestore db;
    FirebaseUser user;
    Map<String,Object> kalori = new HashMap<>();
    Spinner aktivitas_fisik;
    String bb_user;
    String tb_user;
    String usia_user;
    String af;
    String emailuser;
    String tamp_jk;
    String kaloritubuh;
    DecimalFormat df = new DecimalFormat(".##");
    double hasil;
    String[] isi_spinner = {"Olahraga 0-1 Hari / Minggu", "Olahraga 2-3 Hari / Minggu", "Olahraga 3-5 Hari / Minggu", "Olahraga 6-7 Hari / Minggu", "Setiap Hari, Sehari Bisa 2 Kali"};



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hitung_kalori);
        getDataUser();
        berat_badan = findViewById(R.id.bb_text_form);
        tinggi_badan = findViewById(R.id.tb_text_form);
        usia = findViewById(R.id.usia_text_form);
        radio_grup = (RadioGroup) findViewById(R.id.radio_grup);
        hitung = findViewById(R.id.button_hitung);
        db = FirebaseFirestore.getInstance();
        aktivitas_fisik = (Spinner) findViewById(R.id.spinner);
        // Drop Down
        ArrayAdapter <String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, isi_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aktivitas_fisik.setAdapter(adapter);
        aktivitas_fisik.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            af = isi_spinner[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        aktivitas_fisik.getSelectedItem().toString();

        hitung.setOnClickListener(view -> {
            hitung_kalori();
            showDialog();
        });
// 655 + (9,6 x bb) + (1,8xtb) - (4,8*usia) = perempuan
    }


    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Pengumuman");
        alertDialogBuilder.setMessage("Jumlah kebutuhan kalori pada tubuh anda sebanyak " +kaloritubuh +" kkal")
                .setCancelable(false)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FormActivity.this.finish();
                startActivity(new Intent(FormActivity.this,BerandaActivity.class));
            }
        });
        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();

    }

    private void getDataUser(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            emailuser = user.getEmail();
        }
    }

    private void kaloriTubuh(){
        int bb = parseInt(bb_user);
        int tb = parseInt(tb_user);
        int u = parseInt(usia_user);
        DecimalFormat df = new DecimalFormat("#");
        if(tamp_jk.equals("Laki-laki")) {
            if (af.equals("Olahraga 0-1 Hari / Minggu")) {
                hasil = (66 + (13.7 * bb) + (5 * tb) - (6.78 * u)) * 1.2;
            } else if (af.equals("Olahraga 2-3 Hari / Minggu")) {
                hasil = (66 + (13.7 * bb) + (5 * tb) - (6.78 * u)) * 1.375;
            } else if (af.equals("Olahraga 3-5 Hari / Minggu")) {
                hasil = (66 + (13.7 * bb) + (5 * tb) - (6.78 * u)) * 1.55;
            } else if (af.equals("Olahraga 6-7 Hari / Minggu")) {
                hasil = (66 + (13.7 * bb) + (5 * tb) - (6.78 * u)) * 1.725;
            } else if (af.equals("Setiap Hari, Sehari Bisa 2 Kali")) {
                hasil = (66 + (13.7 * bb) + (5 * tb) - (6.78 * u)) * 1.9;
            }
        }else if(tamp_jk.equals("Perempuan")){
            if (af.equals("Olahraga 0-1 Hari / Minggu")) {
                hasil = (665 + (9.6 * bb) + (1.8 * tb) - (4.8 * u)) * 1.2;
            } else if (af.equals("Olahraga 2-3 Hari / Minggu")) {
                hasil = (665 + (9.6 * bb) + (1.8 * tb) - (4.8 * u)) * 1.375;
            } else if (af.equals("Olahraga 3-5 Hari / Minggu")) {
                hasil = (665 + (9.6 * bb) + (1.8 * tb) - (4.8 * u)) * 1.55;
            } else if (af.equals("Olahraga 6-7 Hari / Minggu")) {
                hasil = (665 + (9.6 * bb) + (1.8 * tb) - (4.8 * u)) * 1.725;
            } else if (af.equals("Setiap Hari, Sehari Bisa 2 Kali")) {
                hasil = (665 + (9.6 * bb) + (1.8 * tb) - (4.8 * u)) * 1.9;
            }
        }
        kaloritubuh = (df.format(hasil));
    }



    private void hitung_kalori() {
        //radio button
        bb_user = berat_badan.getText().toString();
        tb_user = tinggi_badan.getText().toString();
        usia_user = usia.getText().toString();
        String af_user = af ;
        int Id = radio_grup.getCheckedRadioButtonId();
        jenis_kelamin =(RadioButton) findViewById(Id);
        tamp_jk = jenis_kelamin.getText().toString();
        if (TextUtils.isEmpty(bb_user)) {
            berat_badan.setError("Data ini wajib diisi !");
            berat_badan.requestFocus();
        } else if (TextUtils.isEmpty(tb_user)) {
            tinggi_badan.setError("Data ini wajib diisi !");
            tinggi_badan.requestFocus();
        } else if (TextUtils.isEmpty(usia_user)) {
            usia.setError("Data ini wajib diisi !");
            usia.requestFocus();
        }else{
            kaloriTubuh();
            kalori.put("berat badan",bb_user);
            kalori.put("tinggi badan",tb_user);
            kalori.put("usia",usia_user);
            kalori.put("jenis kelamin",tamp_jk);
            kalori.put("aktivitas fisik",af_user);
            kalori.put("boolean","1");
            kalori.put("kebutuhan kalori", kaloritubuh);
            db.collection("users").document(emailuser).set(kalori, SetOptions.merge());

        }

    }

}




