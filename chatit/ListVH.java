package com.example.chatit;

import android.app.Application;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ListVH extends RecyclerView.ViewHolder {

    ImageView listiv,ivau;
    TextView nametv,timetv,seentv,lastmtv;
    TextView nameTvau,abouttvAu;
    CheckBox checkBox;


    public ListVH(@NonNull View itemView) {
        super(itemView);
    }

    public void setList(FragmentActivity application, String mcount, String time,
                        String lastm,
                        String name,
                        String url,
                        String seen,
                        String uid){

        String decodemessage = Decode.decode(lastm);

        listiv = itemView.findViewById(R.id.iv_list);
        nametv = itemView.findViewById(R.id.nametv_list);
        timetv = itemView.findViewById(R.id.time_list);
        seentv = itemView.findViewById(R.id.seentv);
        lastmtv = itemView.findViewById(R.id.lastm_list);


        Picasso.get().load(url).into(listiv);
        nametv.setText(name);
        timetv.setText(time);
        seentv.setText(seen);
        lastmtv.setText(decodemessage);

    }

    public void setListau(Application application, String mcount, String time,
                          String lastm,
                          String name,
                          String url,
                          String seen,
                          String uid){

        ivau = itemView.findViewById(R.id.iv_au);
        nameTvau = itemView.findViewById(R.id.nametv_au);
        abouttvAu = itemView.findViewById(R.id.abouttv_au);
        checkBox = itemView.findViewById(R.id.cb_au);

        DocumentReference dr ;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

                            if (url.equals("")){
                                nameTvau.setText(name);
                                abouttvAu.setText(about);
                            }else {
                                nameTvau.setText(name);
                                abouttvAu.setText(about);
                                Picasso.get().load(url).into(ivau);
                            }


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
