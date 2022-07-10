package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ImageAct extends AppCompatActivity {


    Button pickbtn,savebtn,deltbn;
    ImageView ivupload;
    ProgressBar progressBar;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private Uri imageUri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUid;
    DocumentReference documentReference;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        pickbtn =  findViewById(R.id.pick_btn);
        deltbn =  findViewById(R.id.delete_btn);
        savebtn = findViewById(R.id.save_btn);
        ivupload = findViewById(R.id.ivimageupload);
        progressBar = findViewById(R.id.pv_ivupload);

        storageReference = firebaseStorage.getInstance().getReference("profile images");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        currentUid = user.getUid();

        deltbn.setVisibility(View.GONE);

        geturl();

        pickbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intenth = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intenth.setType("image/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intenth,1);
            }
        });


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadProfile();
            }
        });

        deltbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseStorage.getInstance().getReferenceFromUrl(url).
                        delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ImageAct.this, "deleted", Toast.LENGTH_SHORT).show();
                    }
                });


                final DocumentReference sfDocRef = db.collection("user").document(currentUid);

                db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        transaction.update(sfDocRef,"url","");

                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Map<String,Object> map = new HashMap<>();
                        map.put("url","");

                    }
                });

            }
        });

    }

    private void geturl() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        documentReference = db.collection("user").document(currentUserId);

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            url = task.getResult().getString("url");
                            if (url.equals("")){
                                deltbn.setVisibility(View.GONE);

                            }else {
                                deltbn.setVisibility(View.VISIBLE);
                                Picasso.get().load(url).into(ivupload);
                            }

                        }else {
                            Toast.makeText(ImageAct.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ImageAct.this, e+"", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 || resultCode == RESULT_OK ||
                data != null || data.getData() != null){
            imageUri = data.getData();

            Picasso.get().load(imageUri).into(ivupload);
        }

    }

    private void uploadProfile() {

        progressBar.setVisibility(View.VISIBLE);
        final  StorageReference reference = storageReference.child(System.currentTimeMillis() + ".jpg" );

        UploadTask uploadTask = reference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw  task.getException();
                }
                return  reference.getDownloadUrl();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUri = task.getResult();


                            final DocumentReference sfDocRef = db.collection("user").document(currentUid);

                            db.runTransaction(new Transaction.Function<Void>() {
                                @Override
                                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                    DocumentSnapshot snapshot = transaction.get(sfDocRef);

                                    transaction.update(sfDocRef,"url",downloadUri.toString());

                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressBar.setVisibility(View.INVISIBLE);

                                    Map<String,Object> map = new HashMap<>();
                                    map.put("url",downloadUri.toString());



                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(ImageAct.this, "failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });



    }
}