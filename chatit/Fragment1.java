package com.example.chatit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class Fragment1  extends Fragment {

    RecyclerView recyclerView;
    FirebaseDatabase database ;
    DatabaseReference messageRef,chatlist;
    String rphone,currentuid;
    FirebaseFirestore db;
    DocumentReference documentReference;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1,container,false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = getActivity().findViewById(R.id.rv_f1);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        database = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user =  mAuth.getCurrentUser();
        currentuid = user.getUid();

        chatlist = database.getReference("chat list").child(currentuid);
        documentReference = db.collection("user").document(currentuid);


        checkAccount();

    }

    private void checkAccount() {


        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                          //  Toast.makeText(getActivity(), "profile exist", Toast.LENGTH_SHORT).show();
                        }else {

                            Intent intent = new Intent(getActivity(),Profile.class);
                            startActivity(intent);


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

        checkAccount();

        FirebaseRecyclerOptions<ListModal> options =
                new FirebaseRecyclerOptions.Builder<ListModal>()
                        .setQuery(chatlist,ListModal.class)
                        .build();

        FirebaseRecyclerAdapter<ListModal,ListVH> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ListModal, ListVH>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListVH holder, int position, @NonNull ListModal model) {

                        holder.setList(getActivity(),model.getMcount(),model.getTime(),model.getLastm(),model.getName()
                                ,model.getUrl(),model.getSeen(),model.getUid());

                        String postkey = getRef(position).getKey();
                        String ruid = getItem(position).getUid();

                        holder.nametv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(getActivity(),MessageAct.class);
                                intent.putExtra("ruid",ruid);
                                startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.list_item,parent,false);

                        return new ListVH(view);

                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }
}
