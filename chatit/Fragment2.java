package com.example.chatit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Fragment2 extends Fragment implements View.OnClickListener {

    LinearLayout linearLayout;
    RecyclerView recyclerView;
    DatabaseReference groupRef,memberRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String currentuid,name,url,phone,about;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2,container,false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        linearLayout = getActivity().findViewById(R.id.llf2);

        linearLayout.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        recyclerView = getActivity().findViewById(R.id.rv_group);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        groupRef = database.getReference("groups").child(currentuid);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llf2:
                Intent intent = new Intent(getActivity(),GroupName.class);
                startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<GroupModal> options =
                new FirebaseRecyclerOptions.Builder<GroupModal>()
                        .setQuery(groupRef,GroupModal.class)
                        .build();

        FirebaseRecyclerAdapter<GroupModal,GroupVH> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<GroupModal, GroupVH>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GroupVH holder, int position, @NonNull GroupModal model) {


                        holder.setGroup(getActivity(),model.getAdmin(),model.getAdminid(),model.getUrl(),model.getAddress()
                                ,model.getSearch(),model.getGroupname(),model.getTime(),model.getDelete(),model.getLastm(),model.getLastmtime());

                        String address = getItem(position).getAddress();
                        String url = getItem(position).getUrl();
                        String groupname = getItem(position).getGroupname();
                        String adminid = getItem(position).getAdmin();

                        holder.nametv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(),GroupChat.class);
                                intent.putExtra("address",address);
                                intent.putExtra("url",url);
                                intent.putExtra("groupname",groupname);
                                intent.putExtra("adminid",adminid);
                                startActivity(intent);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public GroupVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.group_item,parent,false);

                        return new GroupVH(view);

                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
}
