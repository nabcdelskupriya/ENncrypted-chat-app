package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {


    ProgressBar progressBar;
    EditText ccodeEt,phoneEt;
    Button button;
    private FirebaseAuth mAUth;
    String phonenumber,ccode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.pb_l);
        ccodeEt  = findViewById(R.id.et_cc_l);
        phoneEt = findViewById(R.id.et_phone_l);
        button = findViewById(R.id.btn_login);

        mAUth = FirebaseAuth.getInstance();

        sendOtp();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ccode = ccodeEt.getText().toString().trim();
                phonenumber = phoneEt.getText().toString().trim();

                if (ccode.isEmpty()|| phonenumber.isEmpty()){
                    Toast.makeText(Login.this, "please fill all fields", Toast.LENGTH_SHORT).show();
                }else {

                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAUth)
                                    .setPhoneNumber(ccode+phonenumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(Login.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }

            }
        });
    }

    private void sendOtp(){
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
               signin(credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Login.this, "Error"+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Intent intent = new Intent(Login.this,OTPAct.class);
                intent.putExtra("id",verificationId);
                startActivity(intent);

            }
        };

    }

    private void signin(PhoneAuthCredential credential) {

        mAUth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(Login.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(Login.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        }

                });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAUth.getCurrentUser() != null){
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {

        }

    }
}