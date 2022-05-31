package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {


    ImageView profileiv;
    TextView nametv,phonetv,abouttv;
    Button button,logoutbtn;
    FirebaseFirestore db;
    DocumentReference documentReference;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        db = FirebaseFirestore.getInstance();
        profileiv  = findViewById(R.id.iv_profile);
        nametv = findViewById(R.id.unametv);
        phonetv = findViewById(R.id.phonetv);
        abouttv = findViewById(R.id.abouttv);
        button = findViewById(R.id.btn_profile);
        logoutbtn = findViewById(R.id.btn_signout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        documentReference = db.collection("user").document(currentUserId);

        profileiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Profile.this,ImageAct.class);
                startActivity(i);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String btnstatus = button.getText().toString();

                if (btnstatus.equals("Add")){
                    shownameSheet();
                }else if (btnstatus.equals("Edit")){
                    editsheet();

                }

            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();;
                Intent i = new Intent(Profile.this, Login.class);
                startActivity(i);
                finish();

            }
        });

        getData();

    }

    private void getData() {

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            button.setText("Edit");
                            String name = task.getResult().getString("name");
                            String about = task.getResult().getString("about");
                            String phoneno = task.getResult().getString("phone");
                            String url = task.getResult().getString("url");

                            if (url.equals("")){
                                nametv.setText(name);
                                abouttv.setText(about);
                                phonetv.setText(phoneno);
                            }else {
                                nametv.setText(name);
                                abouttv.setText(about);
                                phonetv.setText(phoneno);
                                Picasso.get().load(url).into(profileiv);
                            }

                        }else {
                            Toast.makeText(Profile.this, "np profile", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private void editsheet() {

        final Dialog dialog = new Dialog(Profile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_bs);

        EditText nameEt = dialog.findViewById(R.id.nameEt);
        EditText aboutEt = dialog.findViewById(R.id.aboutEt);
        TextView canceltv = dialog.findViewById(R.id.canceltv);
        TextView savetv = dialog.findViewById(R.id.savetv);



        savetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameEt.getText().toString().trim();
                String about = aboutEt.getText().toString().trim();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String phonenumber = user.getPhoneNumber();

                final DocumentReference sfDocRef = db.collection("user").document(currentUserId);

                db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        transaction.update(sfDocRef,"name",name);
                        transaction.update(sfDocRef,"about",about);

                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();

                    }
                });


            }
        });

        canceltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomAnim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void shownameSheet() {

        final Dialog dialog = new Dialog(Profile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_bs);

        EditText nameEt = dialog.findViewById(R.id.nameEt);
        EditText aboutEt = dialog.findViewById(R.id.aboutEt);
        TextView canceltv = dialog.findViewById(R.id.canceltv);
        TextView savetv = dialog.findViewById(R.id.savetv);


        savetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameEt.getText().toString().trim();
                String about = aboutEt.getText().toString().trim();


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String phoneNumber = user.getPhoneNumber();


                Map<String ,String > profile = new HashMap<>();
                profile.put("name",name);
                profile.put("about",about);
                profile.put("phone",phoneNumber);
                profile.put("url","");
                profile.put("uid",currentUserId);



                documentReference.set(profile)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                dialog.dismiss();
                                Toast.makeText(Profile.this, "Saved", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "failed", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });




            }
        });


        canceltv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomAnim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

}