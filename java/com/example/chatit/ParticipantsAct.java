package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ParticipantsAct extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference membersRef,groupRef,mref2;
    String groupname,address,url,adminid,currentuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        recyclerView = findViewById(R.id.rv_members);

        groupname = getIntent().getStringExtra("gname");
        address = getIntent().getStringExtra("address");
        url = getIntent().getStringExtra("url");
        adminid = getIntent().getStringExtra("admin");

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        membersRef = database.getReference("members").child(address);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MemberModal> options =
                new FirebaseRecyclerOptions.Builder<MemberModal>()
                        .setQuery(membersRef,MemberModal.class)
                        .build();

        FirebaseRecyclerAdapter<MemberModal,MemberVH> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MemberModal, MemberVH>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MemberVH holder, int position, @NonNull MemberModal model) {

                        holder.setMember(getApplication(),model.getUid(),model.getTime());

                        String uid = getItem(position).getUid();

                        if (currentuid.equals(adminid)){

                            holder.removetv.setVisibility(View.VISIBLE);
                        }else {

                            holder.removetv.setVisibility(View.GONE);
                        }

                        holder.removetv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeuser(uid);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public MemberVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.members_layout,parent,false);

                        return new MemberVH(view);

                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void removeuser(String uid) {

        groupRef = database.getReference("groups").child(uid);
        mref2 = database.getReference("members").child(address).child(uid);


        groupRef.child(address).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(ParticipantsAct.this, "removed", Toast.LENGTH_SHORT).show();
            }
        });

        mref2.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
}