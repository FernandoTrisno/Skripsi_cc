package com.example.skripsi_cc;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

public class loginActivity extends AppCompatActivity {
    TextInputEditText email_login;
    TextInputEditText pass_login;
    TextView btntxt_daftar;
    Button btnLogin;
    String email;
    String pass;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String bool;
    String cekbool = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        email_login = findViewById(R.id.email_text_login);
        pass_login = findViewById(R.id.password_text_login);
        btntxt_daftar = findViewById(R.id.button_text_daftar);
        btnLogin = findViewById(R.id.button_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnLogin.setOnClickListener(view ->
                loginUser()
        );

        btntxt_daftar.setOnClickListener(view->
                startActivity(new Intent(loginActivity.this, daftarActivity.class))
        );
    }

    private void loginUser() {
        email = email_login.getText().toString();
        pass = pass_login.getText().toString();
        ceklogin();
        if (TextUtils.isEmpty(email)) {
            email_login.setError("Data ini wajib diisi !");
            email_login.requestFocus();
        } else if (TextUtils.isEmpty(pass)) {
            pass_login.setError("Data ini wajib diisi !");
            pass_login.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()  {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if(bool.equals(cekbool)){
                            Toast.makeText(loginActivity.this, "Akun sukses login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(loginActivity.this,FormActivity.class));
                        }else{
                            Toast.makeText(loginActivity.this, "Akun sukses login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(loginActivity.this,BerandaActivity.class));
                        }

                    }else{
                        Toast.makeText(loginActivity.this,"Login Gagal : ",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void ceklogin(){
        DocumentReference docRef = db.collection("users").document(email);
        Source source = Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    bool = documentSnapshot.getString("boolean");
                    Log.d(TAG, "Cache document data: " + bool);
                }else{
                    Log.d(TAG, "Cache get failed " + task.getException());
                }
            }
        });
    }

}
