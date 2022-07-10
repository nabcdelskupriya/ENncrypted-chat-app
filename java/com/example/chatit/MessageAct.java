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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class MessageAct extends AppCompatActivity {


    EditText messageEt;
    TextView nametv,lasteentv;
    ImageButton moreBtn,sendbtn,callbtn;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference sRef,rRef,schatlist,rchatlist,lastseenref,groupRef,groupchat;
    String rname,rurl,rabout,rphone,suid,ruid,sname,sabout,sphone,surl,address,usertoken,savetime;
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
        lasteentv = findViewById(R.id.lastseenmtv);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        callbtn = findViewById(R.id.callbtn);


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
        lastseenref = database.getReference("online");

        Calendar time1 = Calendar.getInstance();
        SimpleDateFormat currenttime = new
                SimpleDateFormat("HH:mm:ss a");
        savetime = currenttime.format(time1.getTime());

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

                sendNotification(suid,ruid);

            }
        });

        callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MessageAct.this,Outgoing.class);
                intent.putExtra("ruid",ruid);
                intent.putExtra("sname",sname);
                startActivity(intent);

            }
        });

        getdata();

    }

    public void sendNotification(String suid,String ruid){

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(ruid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usertoken=dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                FcmNotificationsSender notificationsSender =
                        new FcmNotificationsSender(usertoken, "Chat it", sname+  " Sent message",
                                getApplicationContext(),MessageAct.this);

                notificationsSender.SendNotifications();
            }
        },1000);

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

                        String delete = getItem(position).getDelete();
                        String message = getItem(position).getMessage();
                        String suid = getItem(position).getSuid();


                        holder.mTvs.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                messageSheet(delete,message,suid);

                            }
                        });

                        holder.mtvr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                messageSheet(delete,message,suid);

                            }
                        });


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

    private void getdata() {

        lastseenref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String userstatus = snapshot.child(ruid).getValue().toString();
                    lasteentv.setText(userstatus);

                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void messageSheet(String delete, String message, String suid2) {

        final Dialog dialog = new Dialog(MessageAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_bs);


        TextView deletetv = dialog.findViewById(R.id.del_gc_m);
        TextView forwardtv = dialog.findViewById(R.id.forward_gc_m);
        TextView copytv = dialog.findViewById(R.id.copy_gc_m);
        TextView replyp = dialog.findViewById(R.id.replyp_gc_m);
        replyp.setVisibility(View.GONE);

        if (suid2.equals(suid)){
            deletetv.setVisibility(View.VISIBLE);
        }else {
            deletetv.setVisibility(View.GONE);

        }

        deletetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query query = sRef.orderByChild("delete").equalTo(delete);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();
                            Toast.makeText(MessageAct.this, "Unsent", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query2 = rRef.orderByChild("delete").equalTo(delete);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();
                            Toast.makeText(MessageAct.this, "Unsent", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        copytv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboardManager = (ClipboardManager) MessageAct.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String",message);
                clipboardManager.setPrimaryClip(clip);
                clip.getDescription();

                Toast.makeText(MessageAct.this, "copied", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        forwardtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    forwardMessage(message);

                }catch (Exception e){

                    Toast.makeText(MessageAct.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomAnim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void forwardMessage(String message) {

        final Dialog dialog = new Dialog(MessageAct.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forward_bs);

        RecyclerView recyclerView = dialog.findViewById(R.id.rv_forward);
        Spinner spinner = dialog.findViewById(R.id.spinnerf);
        String[] items = {"Chats","Groups"};

        LinearLayoutManager manager = new LinearLayoutManager(MessageAct.this);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,items);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        // textView.setText("Chats");
                        showchats();
                        break;
                    case 1:
                        //  textView.setText("Groups");
                        showgroups();
                        break;
                }
            }

            private void showchats() {

                schatlist = database.getReference("chat list").child(suid);


                FirebaseRecyclerOptions<ListModal> options =
                        new FirebaseRecyclerOptions.Builder<ListModal>()
                                .setQuery(schatlist,ListModal.class)
                                .build();

                FirebaseRecyclerAdapter<ListModal,ListVH> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<ListModal, ListVH>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull ListVH holder, int position, @NonNull ListModal model) {

                                holder.setListforward(getApplication(),model.getMcount(),model.getTime(),model.getLastm(),model.getName()
                                        ,model.getUrl(),model.getSeen(),model.getUid());


                                String ruid = getItem(position).getUid();

                                holder.seentv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public  void onClick(View view) {

                                        sRef = database.getReference("Message").child(suid).child(ruid);
                                        rRef = database.getReference("Message").child(ruid).child(suid);
                                        rchatlist = database.getReference("chat list").child(ruid);
                                        holder.seentv.setText("....");

                                        //   String encodemessage = Encode.encode(message);

                                        // set the code to the edit text

                                        modal.setMessage(message);
                                        modal.setSearch(message.toLowerCase());
                                        modal.setRuid(ruid);
                                        modal.setSuid(suid);
                                        modal.setTime(savetime);
                                        modal.setDelete(String.valueOf(System.currentTimeMillis()));

                                        String key = sRef.push().getKey();

                                        sRef.child(key).setValue(modal);
                                        messageEt.setText("");

                                        rmodal.setMessage(message);
                                        rmodal.setSearch(message.toLowerCase());
                                        rmodal.setRuid(ruid);
                                        rmodal.setSuid(suid);
                                        rmodal.setTime(savetime);
                                        rmodal.setDelete(String.valueOf(System.currentTimeMillis()));

                                        String key2 = rRef.push().getKey();

                                        rRef.child(key2).setValue(rmodal);
                                        messageEt.setText("");

                                        // chat list ref


                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                listModel.setLastm(message);
                                                listModel.setName(rname);
                                                listModel.setMcount("0");
                                                listModel.setSeen("unseen");
                                                listModel.setUid(ruid);
                                                listModel.setUrl(rurl);
                                                listModel.setTime(savetime);


                                                schatlist.child(ruid).setValue(listModel);


                                                // adding data in receiver

                                                rlistmodel.setLastm(message);
                                                rlistmodel.setName(sname);
                                                rlistmodel.setMcount("0");
                                                rlistmodel.setSeen("New");
                                                rlistmodel.setUid(suid);
                                                rlistmodel.setUrl(surl);
                                                rlistmodel.setTime(savetime);


                                                rchatlist.child(suid).setValue(rlistmodel);
                                                holder.seentv.setText("Sent");
                                            }
                                        },1000);

                                    }
                                });

                            }

                            @NonNull
                            @Override
                            public ListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.liste_itemf,parent,false);

                                return new ListVH(view);
                            }
                        };

                recyclerView.setAdapter(firebaseRecyclerAdapter);
                firebaseRecyclerAdapter.startListening();
            }

            private void showgroups() {
                groupRef = database.getReference("groups").child(suid);
                GroupChatModal messageModal = new GroupChatModal();

                FirebaseRecyclerOptions<GroupModal> options =
                        new FirebaseRecyclerOptions.Builder<GroupModal>()
                                .setQuery(groupRef,GroupModal.class)
                                .build();

                FirebaseRecyclerAdapter<GroupModal,GroupVH> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<GroupModal, GroupVH>(options) {
                            @Override
                            protected void onBindViewHolder(@NonNull GroupVH holder, int position, @NonNull GroupModal model) {


                                holder.setGroupforward(getApplication(),model.getAdmin(),model.getAdminid(),model.getUrl(),model.getAddress()
                                        ,model.getSearch(),model.getGroupname(),model.getTime(),model.getDelete(),model.getLastm(),model.getLastmtime());

                                String address = getItem(position).getAddress();
                                String url = getItem(position).getUrl();
                                String groupname = getItem(position).getGroupname();
                                String adminid = getItem(position).getAdmin();
                                groupchat = database.getReference("group chat").child(address);


                                holder.timetv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        holder.timetv.setText("....");
                                        messageModal.setMessage(message);
                                        messageModal.setDelete(String.valueOf(System.currentTimeMillis()));
                                        messageModal.setTime(savetime);
                                        messageModal.setSuid(suid);
                                        messageModal.setSearch(message.toLowerCase());
                                        messageModal.setSeen("no");
                                        messageModal.setSname(sname);

                                        String key = groupchat.push().getKey();
                                        groupchat.child(key).setValue(messageModal);


                                        Map<String,Object> map = new HashMap<>();
                                        map.put("lastm",message);
                                        map.put("lastmtime",savetime);

                                        FirebaseDatabase.getInstance().getReference()
                                                .child("groups").child(suid).child(address)
                                                .updateChildren(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });



                                    }
                                });

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        holder.timetv.setText("Sent");
                                    }
                                },2000);


                            }

                            @NonNull
                            @Override
                            public GroupVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.group_item_f,parent,false);

                                return new GroupVH(view);

                            }
                        };

                recyclerView.setAdapter(firebaseRecyclerAdapter);
                firebaseRecyclerAdapter.startListening();

            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.BottomAnim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}