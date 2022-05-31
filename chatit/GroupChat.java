package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GroupChat extends AppCompatActivity {


    ImageView groupiv;
    TextView nametv,ecryptiontv;
    ImageButton moreoption,sendbtngc;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference groupchat;
    String groupname,address,adminid,url,currentuid,sendername;
    EditText messageEt;
    GroupChatModal messageModal;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        groupiv = findViewById(R.id.iv_gc);
        nametv = findViewById(R.id.nametvgc);
        ecryptiontv = findViewById(R.id.encryptiontvgc);
        moreoption = findViewById(R.id.morebtngc);
        recyclerView = findViewById(R.id.rv_gc);
        sendbtngc = findViewById(R.id.sendbtngc);
        messageEt = findViewById(R.id.et_gc);

        groupname = getIntent().getStringExtra("groupname");
        address = getIntent().getStringExtra("address");
        url = getIntent().getStringExtra("url");
        adminid = getIntent().getStringExtra("adminid");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        groupchat = database.getReference("group chat").child(address);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();
        documentReference = db.collection("user").document(currentuid);

        Picasso.get().load(url).into(groupiv);
        nametv.setText(groupname);

        messageModal = new GroupChatModal();

        Calendar time1 = Calendar.getInstance();
        SimpleDateFormat currenttime = new
                SimpleDateFormat("HH:mm:ss a");
        final String savetime = currenttime.format(time1.getTime());


        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ecryptiontv.setVisibility(View.GONE);
            }
        },1000);


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            sendername = task.getResult().getString("name");

                        }else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        sendbtngc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message =  messageEt.getText().toString().trim();
                String encodemessage = Encode.encode(message);

                messageModal.setMessage(encodemessage);
                messageModal.setDelete(String.valueOf(System.currentTimeMillis()));
                messageModal.setTime(savetime);
                messageModal.setSuid(currentuid);
                messageModal.setSearch(encodemessage.toLowerCase());
                messageModal.setSeen("no");
                messageModal.setSname(sendername);

                String key = groupchat.push().getKey();
                groupchat.child(key).setValue(messageModal);


                Map<String,Object> map = new HashMap<>();
                map.put("lastm",encodemessage);
                map.put("lastmtime",savetime);

                FirebaseDatabase.getInstance().getReference()
                        .child("groups").child(currentuid).child(address)
                        .updateChildren(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                messageEt.setText("");

            }
        });
    }



        @Override
        protected void onStart() {
            super.onStart();

            FirebaseRecyclerOptions<GroupChatModal> options =
                    new FirebaseRecyclerOptions.Builder<GroupChatModal>()
                            .setQuery(groupchat,GroupChatModal.class)
                            .build();

            FirebaseRecyclerAdapter<GroupChatModal,GroupChatVH> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<GroupChatModal, GroupChatVH>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull GroupChatVH holder, int position, @NonNull GroupChatModal model) {

                            holder.setgroupmessage(getApplication(),model.getSname(),model.getMessage(),model.getTime(),model.getDelete()
                                    ,model.getSeen(),model.getSearch(),model.getSuid());

                            String delete = getItem(position).getDelete();
                            String message = getItem(position).getMessage();
                            String suid = getItem(position).getSuid();

                            holder.smtv.setOnClickListener(new View.OnClickListener() {
                                @Override

                                public void onClick(View view) {


                                }
                            });


                        }

                        @NonNull
                        @Override
                        public GroupChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.gm_chat,parent,false);

                            return new GroupChatVH(view);

                        }
                    };

            recyclerView.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();

        }
    }

