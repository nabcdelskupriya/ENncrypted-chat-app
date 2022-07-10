package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddUser extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference chatlist,groupref,memberRef;
    String currentuid;
    TextView selectedtv;
    String groupname,adminname,address,groupurl,savetime,savedate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        recyclerView = findViewById(R.id.rv_au);
        selectedtv = findViewById(R.id.selectedtv);

        LinearLayoutManager manager = new LinearLayoutManager(AddUser.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        groupname = getIntent().getStringExtra("gname");
        address = getIntent().getStringExtra("address");
        groupurl = getIntent().getStringExtra("url");
        adminname = getIntent().getStringExtra("admin");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        memberRef = database.getReference("members");

        chatlist = database.getReference("chat list").child(currentuid);
        groupref = database.getReference("groups");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                selectedtv.setVisibility(View.GONE);
            }
        },3000);

        Calendar time1 = Calendar.getInstance();
        SimpleDateFormat currenttime = new
                SimpleDateFormat("HH:mm:ss a");
        savetime = currenttime.format(time1.getTime());


        Calendar date = Calendar.getInstance();
        SimpleDateFormat currentdate = new
                SimpleDateFormat("dd-MMMM-yyyy");
        savedate = currentdate.format(date.getTime());



    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<ListModal> options =
                new FirebaseRecyclerOptions.Builder<ListModal>()
                        .setQuery(chatlist,ListModal.class)
                        .build();

        FirebaseRecyclerAdapter<ListModal,ListVH> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ListModal, ListVH>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListVH holder, int position, @NonNull ListModal model) {

                        holder.setListau(getApplication(),model.getMcount(),model.getTime(),model.getLastm(),model.getName()
                                ,model.getUrl(),model.getSeen(),model.getUid());


                        String postkey = getRef(position).getKey();
                        String muid = getItem(position).getUid();


                        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                                if (b){

                                    addmembers(muid);
                                }else {
                                    removeuser(muid);

                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.adduser_item,parent,false);

                        return new ListVH(view);

                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void removeuser(String muid) {

        groupref.child(muid).child(address).removeValue();

        memberRef.child(address).child(muid).removeValue();


    }

    private void addmembers(String muid) {

        GroupModal modal = new GroupModal();
        String message = Encode.encode("Send first message");

        modal.setAddress(address);
        modal.setAdmin(adminname);
        modal.setAdminid(currentuid);
        modal.setDelete(String.valueOf(System.currentTimeMillis()));
        modal.setSearch(groupname.toLowerCase());
        modal.setUrl(groupurl);
        modal.setTime(savetime);
        modal.setGroupname(groupname);
        modal.setLastm(message);
        modal.setLastmtime("");
        groupref.child(muid).child(address).setValue(modal);

        MemberModal memberModal = new MemberModal();

        memberModal.setUid(muid);
        memberModal.setTime("Joined On "+savedate+savetime);

        memberRef.child(address).child(muid).setValue(memberModal);

    }
}