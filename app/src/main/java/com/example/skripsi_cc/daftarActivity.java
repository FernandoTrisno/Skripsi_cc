package com.example.skripsi_cc;


import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class daftarActivity extends AppCompatActivity {
    TextInputEditText nama_signup;
    TextInputEditText email_signup;
    TextInputEditText pass_signup;
    TextInputEditText con_pass_signup;
    TextView btntxt_login;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Button btnSignup;
    Map<String,Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_screen);

        nama_signup = findViewById(R.id.nama_text_signup);
        email_signup = findViewById(R.id.email_text_signup);
        pass_signup = findViewById(R.id.password_text_signup);
        con_pass_signup = findViewById(R.id.con_pass_text_signup);
        btnSignup = findViewById(R.id.button_daftar);
        btntxt_login =findViewById(R.id.button_text_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnSignup.setOnClickListener(view->{
            createUser();
        });

        btntxt_login.setOnClickListener(view->{
            startActivity(new Intent(daftarActivity.this,loginActivity.class));
        });
    }

    private void createUser(){
        String nama = nama_signup.getText().toString();
        String email = email_signup.getText().toString();
        String pass = pass_signup.getText().toString();
        String conf_pass = con_pass_signup.getText().toString();

        if (TextUtils.isEmpty(nama)&&TextUtils.isEmpty(email)&&TextUtils.isEmpty(pass)&&TextUtils.isEmpty(conf_pass)){
            nama_signup.setError("Data ini wajib diisi !");
            nama_signup.requestFocus();
            email_signup.setError("Data ini wajib diisi !");
            email_signup.requestFocus();
            pass_signup.setError("Data ini wajib diisi !");
            pass_signup.requestFocus();
            con_pass_signup.setError("Data ini wajib diisi !");
            con_pass_signup.requestFocus();
        }else if(TextUtils.isEmpty(nama)){
            nama_signup.setError("Data ini wajib diisi !");
            nama_signup.requestFocus();
        }else if(TextUtils.isEmpty(email)){
            email_signup.setError("Data ini wajib diisi !");
            email_signup.requestFocus();
        }
        else if(TextUtils.isEmpty(pass)){
            pass_signup.setError("Data ini wajib diisi !");
            pass_signup.requestFocus();
        }else if (TextUtils.isEmpty(conf_pass)){
            con_pass_signup.setError("Data ini wajib diisi !");
            con_pass_signup.requestFocus();
        }else if (pass.length()<8){
            pass_signup.setError("Password minimal 8 karakter !");
            pass_signup.requestFocus();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        user.put("email",email);
                        user.put("users",nama);
                        user.put("boolean","0");
                        db.collection("users").document(email).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(daftarActivity.this, "Akun berhasil dibuat", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(daftarActivity.this, loginActivity.class));
                            }
                        });
                    }else{
                        Toast.makeText(daftarActivity.this,"Error : Akun gagal dibuat",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
