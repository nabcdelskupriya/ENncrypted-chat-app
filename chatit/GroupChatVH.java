package com.example.chatit;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GroupChatVH  extends RecyclerView.ViewHolder {

    TextView sunametv,runametv,smtv,rmtv;
    String currentuid;

    public GroupChatVH(@NonNull View itemView) {
        super(itemView);
    }
    public void setgroupmessage(Application application, String sname, String message, String time, String delete,
                                String seen, String search, String suid){

        sunametv = itemView.findViewById(R.id.uname_m_items);
        runametv = itemView.findViewById(R.id.uname_m_itemr);
        smtv = itemView.findViewById(R.id.gm_items);
        rmtv = itemView.findViewById(R.id.gm_itemr);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentuid = user.getUid();

        String encodemessage = Decode.decode(message);

        if (suid.equals(currentuid)){

            rmtv.setVisibility(View.GONE);
            runametv.setVisibility(View.GONE);
            smtv.setText(encodemessage);
            //   sunametv.setText(sname);
            sunametv.setVisibility(View.GONE);
        }else {
            sunametv.setVisibility(View.GONE);
            smtv.setVisibility(View.GONE);
            rmtv.setText(encodemessage);
            runametv.setText(sname);
        }


    }

}
