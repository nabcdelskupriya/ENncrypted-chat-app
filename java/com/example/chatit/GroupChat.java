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

public class GroupChat extends AppCompatActivity {


    ImageView groupiv;
    TextView nametv,ecryptiontv;
    ImageButton moreoption,sendbtngc;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference groupchat,schatlist,sRef,rRef,rchatlist,groupRef,lastmREf;
    String groupname,address,adminid,url,currentuid,sendername,sname,surl,rname,rurl,sphone,rphone,savetime;
    EditText messageEt;
    GroupChatModal messageModal;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference,docrefR,docrefS;
    ListModal listModel,rlistmodel;
    MessageModal modal,rmodal;
    LastmModal lastmModal ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        listModel = new ListModal();
        rlistmodel = new ListModal();

        lastmModal = new LastmModal();

        modal = new MessageModal();
        rmodal = new MessageModal();
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
        lastmREf = database.getReference("lastm");


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();
        documentReference = db.collection("user").document(currentuid);

        Picasso.get().load(url).into(groupiv);
        nametv.setText(groupname);

        messageModal = new GroupChatModal();

        Calendar time1 = Calendar.getInstance();
        SimpleDateFormat currenttime = new
                SimpleDateFormat("HH:mm:ss a");
         savetime = currenttime.format(time1.getTime());


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


                lastmModal.setLastm(encodemessage);
                lastmModal.setLastmtime(savetime);

                lastmREf.child(address).setValue(lastmModal);

                messageEt.setText("");

            }
        });

        moreoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(GroupChat.this,GroupInfo.class);
                intent.putExtra("gname",groupname);
                intent.putExtra("address",address);
                intent.putExtra("url",url);
                intent.putExtra("admin",adminid);
                startActivity(intent);

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
                                    messageSheet(delete,message,suid);

                                }
                            });

                            holder.rmtv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    messageSheet(delete,message,suid);

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


    private void messageSheet(String delete, String message, String suid) {

        final Dialog dialog = new Dialog(GroupChat.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.group_bs);


        TextView deletetv = dialog.findViewById(R.id.del_gc_m);
        TextView forwardtv = dialog.findViewById(R.id.forward_gc_m);
        TextView copytv = dialog.findViewById(R.id.copy_gc_m);
        TextView replyp = dialog.findViewById(R.id.replyp_gc_m);

        if (suid.equals(currentuid)){
            replyp.setVisibility(View.GONE);
            deletetv.setVisibility(View.VISIBLE);
        }else {
            deletetv.setVisibility(View.GONE);
        }

        deletetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query query = groupchat.orderByChild("delete").equalTo(delete);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();
                            Toast.makeText(GroupChat.this, "Unsent", Toast.LENGTH_SHORT).show();
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

                ClipboardManager clipboardManager = (ClipboardManager) GroupChat.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String",message);
                clipboardManager.setPrimaryClip(clip);
                clip.getDescription();

                Toast.makeText(GroupChat.this, "copied", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        replyp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(GroupChat.this,MessageAct.class);
                intent.putExtra("ruid",suid);
                startActivity(intent);
                finish();

            }
        });

        forwardtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    forwardMessage(message);

                }catch (Exception e){

                    Toast.makeText(GroupChat.this, ""+e, Toast.LENGTH_SHORT).show();
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

        final Dialog dialog = new Dialog(GroupChat.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forward_bs);

        RecyclerView recyclerView = dialog.findViewById(R.id.rv_forward);
        Spinner spinner = dialog.findViewById(R.id.spinnerf);
        //  TextView textView = dialog.findViewById(R.id.spinnerTv);

        String[] items = {"Chats","Groups"};

        LinearLayoutManager manager = new LinearLayoutManager(GroupChat.this);

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

                schatlist = database.getReference("chat list").child(currentuid);


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

                                        sRef = database.getReference("Message").child(currentuid).child(ruid);
                                        rRef = database.getReference("Message").child(ruid).child(currentuid);
                                        rchatlist = database.getReference("chat list").child(ruid);

                                        docrefR = db.collection("user").document(ruid);
                                        docrefS = db.collection("user").document(currentuid);

                                        docrefR.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                        if (task.getResult().exists()){
                                                            rname = task.getResult().getString("name");
                                                            //  rabout = task.getResult().getString("about");
                                                            rphone = task.getResult().getString("phone");
                                                            rurl = task.getResult().getString("url");


                                                        }else {
                                                            //  Toast.makeText(MessageAct.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        //  Toast.makeText(MessageAct.this, e+"", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        docrefS.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                        if (task.getResult().exists()){
                                                            sname = task.getResult().getString("name");
                                                            //   sabout = task.getResult().getString("about");
                                                            sphone = task.getResult().getString("phone");
                                                            surl = task.getResult().getString("url");

                                                        }else {
                                                            // Toast.makeText(MessageAct.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        //   Toast.makeText(MessageAct.this, e+"", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                        holder.seentv.setText("....");

                                        //   String encodemessage = Encode.encode(message);

                                        // set the code to the edit text

                                        modal.setMessage(message);
                                        modal.setSearch(message.toLowerCase());
                                        modal.setRuid(ruid);
                                        modal.setSuid(currentuid);
                                        modal.setTime(savetime);
                                        modal.setDelete(String.valueOf(System.currentTimeMillis()));

                                        String key = sRef.push().getKey();

                                        sRef.child(key).setValue(modal);
                                        messageEt.setText("");

                                        rmodal.setMessage(message);
                                        rmodal.setSearch(message.toLowerCase());
                                        rmodal.setRuid(ruid);
                                        rmodal.setSuid(currentuid);
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
                                                rlistmodel.setUid(currentuid);
                                                rlistmodel.setUrl(surl);
                                                rlistmodel.setTime(savetime);


                                                rchatlist.child(currentuid).setValue(rlistmodel);
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
                groupRef = database.getReference("groups").child(currentuid);

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

                                holder.timetv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        //  String encodemessage = Encode.encode(message);

                                        holder.timetv.setText("....");
                                        messageModal.setMessage(message);
                                        messageModal.setDelete(String.valueOf(System.currentTimeMillis()));
                                        messageModal.setTime(savetime);
                                        messageModal.setSuid(currentuid);
                                        messageModal.setSearch(message.toLowerCase());
                                        messageModal.setSeen("no");
                                        messageModal.setSname(sendername);

                                        String key = groupchat.push().getKey();
                                        groupchat.child(key).setValue(messageModal);


                                        Map<String,Object> map = new HashMap<>();
                                        map.put("lastm",message);
                                        map.put("lastmtime",savetime);

                                        FirebaseDatabase.getInstance().getReference()
                                                .child("groups").child(currentuid).child(address)
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