package com.example.chatit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class Outgoing extends AppCompatActivity {

    String  ruid,suid,usertoken,sname;
    ImageView ivuser,ivtype;
    TextView nametv,tvtype;
    FloatingActionButton declinebtn;
    DatabaseReference videocallRef;
    DocumentReference documentReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference responseref,cancelRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    VcModel model;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing);
        ivuser = findViewById(R.id.userivVcog);
        ivtype = findViewById(R.id.uvtypeog);
        nametv = findViewById(R.id.nametvog);
        declinebtn = findViewById(R.id.declineog);
        tvtype = findViewById(R.id.tvtypecall);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null){
//            ruid = extras.getString("u");
//
//        }else {
//
//            Toast.makeText(this, "Some data missing", Toast.LENGTH_SHORT).show();
//        }

        ruid = getIntent().getStringExtra("ruid");
        sname = getIntent().getStringExtra("sname");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        suid = user.getUid();


        fetchReceiverData(ruid);
        call(suid,ruid);
        callResponse(suid,ruid);
        sendNotification(suid,ruid);

        declinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelVC(suid,ruid);
            }
        });

    }

    public void sendNotification(String suid,String ruid){

        FirebaseDatabase.getInstance().getReference().child("Tokens").child(ruid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usertoken=dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                FcmNotificationsSender notificationsSender =
                        new FcmNotificationsSender(usertoken, "Chat it", sname+" is calling you",
                                getApplicationContext(),Outgoing.this);

                notificationsSender.SendNotifications();
            }
        },1000);

    }
    private void fetchReceiverData(String ruid) {

        documentReference = db.collection("user").document(ruid);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String name = task.getResult().getString("name");
                            String about = task.getResult().getString("about");
                            String phoneno = task.getResult().getString("phone");
                            String url = task.getResult().getString("url");

                            nametv.setText(name);
                            Picasso.get().load(url).into(ivuser);

                        }else {
                            Toast.makeText(Outgoing.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Outgoing.this, e+"", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void callResponse(String suid,String ruid){

        responseref = database.getReference("vcref").child(suid).child(ruid);

        responseref.child("res").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String key = snapshot.child("key").getValue().toString();
                    String response = snapshot.child("response").getValue().toString();
                    if (response.equals("yes")){
                        joinmeeting(key);
                        Toast.makeText(Outgoing.this, "Call accepted", Toast.LENGTH_SHORT).show();

                    }else if (response.equals("no")){
                        responseref.removeValue();
                        Toast.makeText(Outgoing.this, "Call denied", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Outgoing.this,MessageAct.class);
                        startActivity(intent);
                        finish();
                    }

                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void joinmeeting(String key) {

        try {

            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(key)
                    .setWelcomePageEnabled(false)
                    .setAudioOnly(true)
                    .setVideoMuted(true)
                    .build();
            JitsiMeetActivity.launch(Outgoing.this,options);
            finish();
        }catch (Exception e){

            Toast.makeText(this,e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public  void cancelVC(String suid,String ruid){

        model = new VcModel();
        cancelRef = database.getReference("cancel");

        videocallRef = FirebaseDatabase.getInstance().getReference("vc").child(ruid);

        try {

            videocallRef.removeValue();
            model.setResponse("no");
            cancelRef.child(suid).setValue(model);
            Intent intent = new Intent(Outgoing.this,TabAct.class);
            startActivity(intent);
            Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show();
            finish();
        }catch (Exception e){

        }
    }

    public void call(String suid,String ruid){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference;
        reference = database.getReference("vc").child(ruid).child("callerid");

        reference.setValue(suid);
    }
}