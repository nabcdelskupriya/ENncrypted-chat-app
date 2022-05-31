package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPAct extends AppCompatActivity {

    EditText editText;
    Button button;
    ProgressBar progressBar;
    String verificationid;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        progressBar = findViewById(R.id.otp_pb);
        button = findViewById(R.id.btn_otp);
        editText = findViewById(R.id.et_phone_otp);
        mAuth = FirebaseAuth.getInstance();

        verificationid = getIntent().getStringExtra("id");

        progressBar.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String code = editText.getText().toString().trim();

                if (code.isEmpty()){
                    Toast.makeText(OTPAct.this, "please enter code", Toast.LENGTH_SHORT).show();
                }else {

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
                    signin(credential);
                }

            }
        });

    }

    private void signin(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          progressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(OTPAct.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {

                            Toast.makeText(OTPAct.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}