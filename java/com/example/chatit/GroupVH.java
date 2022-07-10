package com.example.chatit;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GroupVH extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nametv,lastmtv,timetv;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference lastmRef;

    public GroupVH(@NonNull View itemView) {
        super(itemView);
    }

    public void setGroup(FragmentActivity activity, String admin, String adminid,
                         String url, String address, String search, String groupname, String time,
                         String delete, String lastm, String lastmtime){

        imageView = itemView.findViewById(R.id.iv_gitem);
        nametv = itemView.findViewById(R.id.nametv_gitem);
        lastmtv = itemView.findViewById(R.id.lastm_g);
        timetv = itemView.findViewById(R.id.timegrouptv);

        lastmRef = database.getReference("lastm");
        String message = Decode.decode(lastm);

        lastmRef.child(address).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String lastm = snapshot.child("lastm").getValue().toString();
                    String lastmtime = snapshot.child("lastmtime").getValue().toString();

                    lastmtv.setText(Decode.decode(lastm));
                    timetv.setText(lastmtime);

                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        lastmtv.setText(message);
        nametv.setText(groupname);
        Picasso.get().load(url).into(imageView);
        timetv.setText(lastmtime);

    }


    public void setGroupforward(Application activity, String admin, String adminid,
                                String url, String address, String search, String groupname, String time,
                                String delete, String lastm, String lastmtime){

        imageView = itemView.findViewById(R.id.iv_gitemf);
        nametv = itemView.findViewById(R.id.nametv_gitemf);
        lastmtv = itemView.findViewById(R.id.lastm_gf);
        timetv = itemView.findViewById(R.id.timegrouptvf);

        lastmRef = database.getReference("lastm");
        String message = Decode.decode(lastm);

        lastmRef.child(address).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String lastm = snapshot.child("lastm").getValue().toString();
                    String lastmtime = snapshot.child("lastmtime").getValue().toString();

                    lastmtv.setText(Decode.decode(lastm));
                    timetv.setText(lastmtime);

                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        lastmtv.setText(message);
        nametv.setText(groupname);
        Picasso.get().load(url).into(imageView);

    }
}
