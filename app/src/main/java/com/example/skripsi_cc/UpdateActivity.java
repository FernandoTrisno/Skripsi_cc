package com.example.skripsi_cc;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {
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
    String tamp_bb;
    String tamp_tb;
    String tamp_usia;
    String tamp_af;
    String kaloritubuh_baru;
    ImageView back_update;
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
    double bb_awal;
    double bb_baru;
    double bmi;
    double bmi2;
    double ideal_bmi1;
    double ideal_bmi2;
    String ideal1;
    String ideal2;
    String text_bmi;
    String goals;
    String input_bmi;
    String status;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_hitung_kalori);
        getDataUser();
        back_update = findViewById(R.id.ic_back_update);
        berat_badan = findViewById(R.id.bb_text_form_udpate);
        tinggi_badan = findViewById(R.id.tb_text_form_udpate);
        usia = findViewById(R.id.usia_text_form_udpate);
        hitung = findViewById(R.id.button_hitung_update);
        db = FirebaseFirestore.getInstance();
        aktivitas_fisik = (Spinner) findViewById(R.id.spinner_update);
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

        ceknama();
        hitung.setOnClickListener(view -> {
            hitung_kalori();
            bb_awal = parseInt(tamp_bb);
            bb_baru = parseInt(bb_user);
            if(bb_awal<bb_baru){
                text_bmi = "Yuk lebih semangat mengontrol kalori anda. ";
            }
            else if(bb_awal==bb_baru){
                text_bmi = "Yey, Anda berhasil mempertahankan berat badan anda. ";
            }
            else if(bb_awal>bb_baru){
                text_bmi = "Hore !!! Berat badan anda berhasil turun. ";
            }
            showDialog();
        });

        back_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateActivity.this,ProfilActivity.class));
            }
        });
// 655 + (9,6 x bb) + (1,8xtb) - (4,8*usia) = perempuan
    }



    public void ceknama(){
        DocumentReference docRef = db.collection("users").document(emailuser);
        Source source = Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    tamp_bb = documentSnapshot.getString("berat badan");
                    tamp_jk = documentSnapshot.getString("jenis kelamin");
                    tamp_tb = documentSnapshot.getString("tinggi badan");
                    tamp_usia = documentSnapshot.getString("usia");
                }else{
                    Log.d(TAG, "Cache get failed " + task.getException());
                }
            }
        });
        berat_badan.setHint(tamp_bb);
        tinggi_badan.setHint(tamp_tb);
        usia.setHint(tamp_usia);
        berat_badan.setText(tamp_bb);
        tinggi_badan.setText(tamp_tb);
        usia.setText(tamp_usia);
    }

    private void bmi(){
        DecimalFormat df = new DecimalFormat("#");
        double tb2 = parseInt(tb_user);
        double t_tb2 = (tb2*tb2)/10000;
        double bb2 = parseInt(bb_user);
        bmi2 = ((bb2/t_tb2));
        ideal_bmi1 = 18.5 * t_tb2;
        ideal_bmi2 = 24.9 * t_tb2;
        ideal1 = (df.format(ideal_bmi1));
        ideal2 = (df.format(ideal_bmi2));
        System.out.println(bmi2);
        if(bmi<18.5){
            status = "Berat Badan Kurang";
            goals = "Menaikan Berat Badan";
            text_bmi = "Berat badan anda KURANG";
        }else if(bmi>18.5&&bmi<24.9){
            status = "Berat Badan Normal";
            goals = "Mempertahankan Berat Badan";
            text_bmi = "Berat badan anda NORMAL";
        }else if(bmi>24.9){
            status = "Berat Badan Berlebih";
            goals = "Menurunkan Berat Badan";
            text_bmi = "Berat badan anda BERLEBIH";
        }
    }

    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Pengumuman");
        alertDialogBuilder.setMessage(text_bmi+", jumlah kebutuhan kalori pada tubuh anda sebanyak " + kaloritubuh + " kkal")
                .setCancelable(false)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateActivity.this.finish();
                        startActivity(new Intent(UpdateActivity.this, ProfilActivity.class));
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
        double d_bb = parseInt(bb_user);
        double d_tb = parseInt(tb_user);
        bmi = ((d_bb/((d_tb*d_tb)/10000)));
        DecimalFormat df2 = new DecimalFormat(".##");
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
        input_bmi = (df2.format(bmi));
    }



    private void hitung_kalori() {
        //radio button
        bb_user = berat_badan.getText().toString();
        tb_user = tinggi_badan.getText().toString();
        usia_user = usia.getText().toString();
        String af_user = af ;
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
            bmi();
            kalori.put("berat badan",bb_user);
            kalori.put("tinggi badan",tb_user);
            kalori.put("usia",usia_user);
            kalori.put("jenis kelamin",tamp_jk);
            kalori.put("aktivitas fisik",af_user);
            kalori.put("boolean","1");
            kalori.put("kebutuhan kalori", kaloritubuh);
            kalori.put("bmi",input_bmi);
            kalori.put("goals",goals);
            kalori.put("ideal1",ideal1);
            kalori.put("ideal2",ideal2);
            kalori.put("status",status);
            db.collection("users").document(emailuser).set(kalori, SetOptions.merge());

        }

    }

}




