package com.example.chatit;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MemberVH extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nametv,abouttv,removetv;
    DocumentReference dr;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public MemberVH(@NonNull View itemView) {
        super(itemView);
    }

    public void setMember(Application application,String uid, String time){

        imageView = itemView.findViewById(R.id.iv_mitem);
        nametv = itemView.findViewById(R.id.nametv_mitem);
        abouttv = itemView.findViewById(R.id.about_mitem);
        removetv = itemView.findViewById(R.id.remove_tvmitem);

        dr = db.collection("user").document(uid);
        dr.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String name = task.getResult().getString("name");
                            String about = task.getResult().getString("about");
                            String phoneno = task.getResult().getString("phone");
                            String url = task.getResult().getString("url");

                            nametv.setText(name);
                            abouttv.setText(about);
                            Picasso.get().load(url).into(imageView);

                        }else {

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
