package com.example.chatit;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MessageVH extends RecyclerView.ViewHolder {

    String currentuid;
    TextView mTvs,mtvr;

    public MessageVH(@NonNull View itemView) {
        super(itemView);
    }


    public void setmessage(Application application, String message, String search, String delete, String time,
                           String suid, String ruid){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid  = user.getUid();

        mtvr = itemView.findViewById(R.id.m_tv_r);
        mTvs = itemView.findViewById(R.id.m_tv_s);

        String decodemessage = Decode.decode(message);


        if (currentuid.equals(suid)){

            mtvr.setVisibility(View.GONE);
            mTvs.setText(decodemessage);
            mTvs.setVisibility(View.VISIBLE);

        }else if (currentuid.equals(ruid)){

            mtvr.setVisibility(View.VISIBLE);
            mTvs.setVisibility(View.GONE);
            mtvr.setText(decodemessage);
        }
    }
}

