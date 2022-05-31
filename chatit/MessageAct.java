package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MessageAct extends AppCompatActivity {


    EditText messageEt;
    TextView nametv;
    ImageButton moreBtn,sendbtn;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference sRef,rRef,schatlist,rchatlist;
    String rname,rurl,rabout,rphone,suid,ruid,sname,sabout,sphone,surl,address;
    ImageView imageView;
    MessageModal modal,rmodal;
    ListModal listModel,rlistmodel;
    DocumentReference documentReference,documentReferenceSender;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageView = findViewById(R.id.iv_message);
        messageEt = findViewById(R.id.et_message);
        nametv = findViewById(R.id.nametvmessage);
        moreBtn = findViewById(R.id.morebtn);
        sendbtn = findViewById(R.id.sendbtn);
        recyclerView = findViewById(R.id.rv_message);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);


        listModel = new ListModal();
        rlistmodel = new ListModal();
        modal = new MessageModal();
        rmodal = new MessageModal();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        suid = user.getUid();

        ruid = getIntent().getStringExtra("ruid");
        address = getIntent().getStringExtra("a");

        documentReference = db.collection("user").document(ruid);
        documentReferenceSender = db.collection("user").document(suid);

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            rname = task.getResult().getString("name");
                            rabout = task.getResult().getString("about");
                            rphone = task.getResult().getString("phone");
                            rurl = task.getResult().getString("url");

                            nametv.setText(rname);
                            Picasso.get().load(rurl).into(imageView);


                        }else {
                            Toast.makeText(MessageAct.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MessageAct.this, e+"", Toast.LENGTH_SHORT).show();
                    }
                });


        documentReferenceSender.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            sname = task.getResult().getString("name");
                            sabout = task.getResult().getString("about");
                            sphone = task.getResult().getString("phone");
                            surl = task.getResult().getString("url");

                        }else {
                            Toast.makeText(MessageAct.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MessageAct.this, e+"", Toast.LENGTH_SHORT).show();
                    }
                });

        sRef = database.getReference("Message").child(suid).child(ruid);
        rRef = database.getReference("Message").child(ruid).child(suid);
        schatlist = database.getReference("chat list").child(suid);
        rchatlist = database.getReference("chat list").child(ruid);

        Calendar time1 = Calendar.getInstance();
        SimpleDateFormat currenttime = new
                SimpleDateFormat("HH:mm:ss a");
        final String savetime = currenttime.format(time1.getTime());

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = messageEt.getText().toString().trim();

                if (message.isEmpty()){
                    Toast.makeText(MessageAct.this, "cannot send empty message", Toast.LENGTH_SHORT).show();
                }else {

                    String encodemessage = Encode.encode(message);

                    // set the code to the edit text

                    modal.setMessage(encodemessage);
                    modal.setSearch(encodemessage.toLowerCase());
                    modal.setRuid(ruid);
                    modal.setSuid(suid);
                    modal.setTime(savetime);
                    modal.setDelete(String.valueOf(System.currentTimeMillis()));

                    String key = sRef.push().getKey();

                    sRef.child(key).setValue(modal);
                    messageEt.setText("");

                    rmodal.setMessage(encodemessage);
                    rmodal.setSearch(encodemessage.toLowerCase());
                    rmodal.setRuid(ruid);
                    rmodal.setSuid(suid);
                    rmodal.setTime(savetime);
                    rmodal.setDelete(String.valueOf(System.currentTimeMillis()));

                    String key2 = rRef.push().getKey();

                    rRef.child(key2).setValue(rmodal);
                    messageEt.setText("");

                    // chat list ref

                    listModel.setLastm(encodemessage);
                    listModel.setName(rname);
                    listModel.setMcount("0");
                    listModel.setSeen("unseen");
                    listModel.setUid(ruid);
                    listModel.setUrl(rurl);
                    listModel.setTime(savetime);
                  //  listModel.setAddress(sphone+rphone);

                    schatlist.child(ruid).setValue(listModel);


                    // adding data in receiver

                    rlistmodel.setLastm(encodemessage);
                    rlistmodel.setName(sname);
                    rlistmodel.setMcount("0");
                    rlistmodel.setSeen("New");
                    rlistmodel.setUid(suid);
                    rlistmodel.setUrl(surl);
                    rlistmodel.setTime(savetime);
                  //  listModel.setAddress(sphone+rphone);

                    rchatlist.child(suid).setValue(rlistmodel);

                }

            }
        });

    }

    private void getdata() {


    }

    @Override
    protected void onStart() {
        super.onStart();


        updateseen();

        FirebaseRecyclerOptions<MessageModal> options =
                new FirebaseRecyclerOptions.Builder<MessageModal>()
                        .setQuery(sRef,MessageModal.class)
                        .build();

        FirebaseRecyclerAdapter<MessageModal,MessageVH> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MessageModal, MessageVH>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MessageVH holder, int position, @NonNull MessageModal model) {

                        holder.setmessage(getApplication(),model.getMessage(),model.getSearch(),model.getDelete(),model.getTime()
                                ,model.getSuid(),model.getRuid());

                    }

                    @NonNull
                    @Override
                    public MessageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.message_item,parent,false);

                        return new MessageVH(view);

                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public void updateseen(){


        Map<String,Object> map = new HashMap<>();
        map.put("seen","seen");

        FirebaseDatabase.getInstance().getReference()
                .child("chat list").child(ruid)
                .child(suid)
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                    }
                });




    }
}