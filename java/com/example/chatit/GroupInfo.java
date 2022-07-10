package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class GroupInfo extends AppCompatActivity {

    String groupname,address,url,adminid,adminname,currentuid;
    ImageView imageView;
    TextView participantstv,addmemberstv,seememberstv,exitgrouptv,groupnameTv;
    DocumentReference adminDoc;
    DatabaseReference memberRef,groupref;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int count ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        groupnameTv = findViewById(R.id.gname_info);
        exitgrouptv = findViewById(R.id.exitgroup);
        seememberstv = findViewById(R.id.seememberstv);
        addmemberstv = findViewById(R.id.addmemberstv);
        participantstv = findViewById(R.id.gparticipants_tv);
        imageView = findViewById(R.id.iv_groupinfo);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        groupname = getIntent().getStringExtra("gname");
        address = getIntent().getStringExtra("address");
        url = getIntent().getStringExtra("url");
        adminid = getIntent().getStringExtra("admin");

        adminDoc = db.collection("user").document(adminid);
        groupref = database.getReference("groups").child(currentuid);
        memberRef = database.getReference("members").child(address);

        if (currentuid.equals(adminid)){
            addmemberstv.setVisibility(View.VISIBLE);
            exitgrouptv.setVisibility(View.GONE);
        }else {
            addmemberstv.setVisibility(View.GONE);
        }

        adminDoc.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            adminname = task.getResult().getString("name");

                        }else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        memberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    count = (int) snapshot.getChildrenCount();
                    participantstv.setText("Total members: "+count);
                }else {

                    count = (int) snapshot.getChildrenCount();
                    participantstv.setText("Total members: "+count);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        groupnameTv.setText(groupname);
        Picasso.get().load(url).into(imageView);


        addmemberstv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(GroupInfo.this,AddUser.class);
                intent.putExtra("gname",groupname);
                intent.putExtra("address",address);
                intent.putExtra("url",url);
                intent.putExtra("admin",adminname);
                startActivity(intent);
            }
        });

        seememberstv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(GroupInfo.this,ParticipantsAct.class);
                intent.putExtra("gname",groupname);
                intent.putExtra("address",address);
                intent.putExtra("url",url);
                intent.putExtra("admin",adminid);
                startActivity(intent);


            }
        });

        exitgrouptv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                groupref.child(address).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(GroupInfo.this, "EXIT Successful", Toast.LENGTH_SHORT).show();
                    }
                });

                memberRef.child(address).child(currentuid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(GroupInfo.this,TabAct.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

    }

}