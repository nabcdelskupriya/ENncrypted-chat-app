package com.example.chatit;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class UserVH  extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nametc,abouttv;

    public UserVH(@NonNull View itemView) {
        super(itemView);
    }

    public void setUser(Application application,String name,String phone,String  about,String url,String uid){

        imageView = itemView.findViewById(R.id.iv_userItem);
        nametc = itemView.findViewById(R.id.nametv_useritem);
        abouttv = itemView.findViewById(R.id.abouttv_useritem);


        Picasso.get().load(url).into(imageView);
        nametc.setText(name);

        if (about.equals("")){
            abouttv.setVisibility(View.GONE);
        }else {
            abouttv.setText(about);
        }

    }
}
