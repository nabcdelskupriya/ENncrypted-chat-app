package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;

public class IncomingCall extends AppCompatActivity {

    ImageView ivic,typeiv;
    TextView tvname,typetv;
    FloatingActionButton acceptbtn,declinebtn;
    String senderuid,sendername,sendertoken,receiveruid;
    VcModel model;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,videocallRef;
    MediaPlayer mp;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        receiveruid  =  user.getUid();

        model = new VcModel();
        ivic = findViewById(R.id.ivincoming);
        tvname = findViewById(R.id.vcnameic);
        typeiv = findViewById(R.id.vcIvic);
        typetv = findViewById(R.id.tvtypecallic);
        acceptbtn = findViewById(R.id.acceptVc);
        declinebtn = findViewById(R.id.cancelVc);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            senderuid = extras.getString("suid");

        }else {

            Toast.makeText(this, "Some data missing", Toast.LENGTH_SHORT).show();
        }

        reference = database.getReference("vcref").child(senderuid).child(receiveruid); // response ref
        videocallRef = FirebaseDatabase.getInstance().getReference("vc"); //call ref

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mp = MediaPlayer.create(getApplicationContext(), notification);
            mp.start();
        }catch (Exception e){

            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

//        checkCallStatus();
//        fetchcallerdata(senderuid);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                checkCallStatus();
                fetchcallerdata(senderuid);
            }
        },1000);

        acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String response = "yes";
                sendResponse(response);
                videocallRef.removeValue();
                mp.stop();

            }
        });

        declinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                videocallRef.removeValue();
                String response = "no";
                mp.stop();
                sendResponse(response);
                Intent intent = new Intent(IncomingCall.this,TabAct.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void checkCallStatus() {
        DatabaseReference cancelRef;
        cancelRef = FirebaseDatabase.getInstance().getInstance().getReference("cancel");

        cancelRef.child(senderuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String response = snapshot.child("response").getValue().toString();
                    if (response.equals("no")){
                        Intent intent = new Intent(IncomingCall.this,TabAct.class);
                        startActivity(intent);
                        mp.stop();
                        finish();
                    }else {

                    }


                }else {
                    //  Toast.makeText(VideoCallOutgoing.this, "No response", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendResponse(String response) {
        if (response.equals("yes")){

            model.setKey(sendername+receiveruid);
            model.setResponse("yes");
            reference.child("res").setValue(model);
            joinmeeting();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reference.removeValue();
                }
            },3000);

        }else if (response.equals("no")){

            model.setKey(sendername+receiveruid);
            model.setResponse("no");
            reference.child("res").setValue(model);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reference.removeValue();
                }
            },3000);
        }

    }

    private void joinmeeting() {

        try {

            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(sendername+receiveruid)
                    .setWelcomePageEnabled(false)
                    .setAudioOnly(true)
                    .setVideoMuted(true)
                    .build();
            JitsiMeetActivity.launch(IncomingCall.this,options);
            finish();
        }catch (Exception e){

            Toast.makeText(this,e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchcallertoken(String senderuid) {
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(senderuid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sendertoken=dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void fetchcallerdata(String senderuid) {

        documentReference = db.collection("user").document(senderuid);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String name = task.getResult().getString("name");
                            String about = task.getResult().getString("about");
                            String phoneno = task.getResult().getString("phone");
                            String url = task.getResult().getString("url");

                            Picasso.get().load(url).into(ivic);
                            tvname.setText(name);

                        }else {
                            Toast.makeText(IncomingCall.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(IncomingCall.this, e+"", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videocallRef.removeValue();
        String response = "no";
        mp.stop();
        sendResponse(response);
        Intent intent = new Intent(IncomingCall.this,MainActivity.class);
        startActivity(intent);
        finish();

    }}