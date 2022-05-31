package com.example.chatit;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class GroupVH extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nametv,lastmtv,timetv;

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

        String message = Decode.decode(lastm);
        lastmtv.setText(message);
        nametv.setText(groupname);
        Picasso.get().load(url).into(imageView);
        timetv.setText(lastmtime);

    }
}
