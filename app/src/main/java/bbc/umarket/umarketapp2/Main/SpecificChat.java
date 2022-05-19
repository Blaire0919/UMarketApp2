package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.MessagesAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.MessagesModel;
import bbc.umarket.umarketapp2.R;

public class SpecificChat extends AppCompatActivity {
    ImageButton back;
    EditText et_getmessage;
    ImageView send;
    RecyclerView rv_thread;
    TextView nameofspecificuser;

    String enteredmessages;
    String mreceivername, mreceiveruid, msenderuid;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");
    String senderroom, receiverroom;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    String userID;

    MessagesAdapter messagesAdapter;
    ArrayList<MessagesModel> messagesModelArrayList = new ArrayList<>();


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_specific_chat);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();

        userID = usersdetails.get(SessionManager.KEY_STUDID);

        back = findViewById(R.id.backbtnSpecificChat);
        et_getmessage = findViewById(R.id.et_getmessage);
        send = findViewById(R.id.sendmessagebutton);
        nameofspecificuser = findViewById(R.id.NameofSpecificUser);
        rv_thread = findViewById(R.id.RVChatMessages);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        msenderuid = firebaseAuth.getUid();
        mreceiveruid = getIntent().getStringExtra("receiveruid");
        mreceivername = getIntent().getStringExtra("name");
        senderroom = msenderuid + mreceiveruid;
        receiverroom = mreceiveruid + msenderuid;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rv_thread.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(SpecificChat.this, messagesModelArrayList);
        rv_thread.setAdapter(messagesAdapter);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(SpecificChat.this, HomeContainer.class);
            intent.putExtra("back_Chat", "Chat");
            startActivity(intent);
            finish();
        });

        nameofspecificuser.setText(mreceivername);
        databaseReference = firebaseDatabase.getReference("chats").child(senderroom).child("messages");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesModelArrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessagesModel messagesModel = snapshot1.getValue(MessagesModel.class);
                    messagesModelArrayList.add(messagesModel);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        send.setOnClickListener(view -> {
            enteredmessages = et_getmessage.getText().toString();

            if (enteredmessages.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Enter message first", Toast.LENGTH_SHORT).show();
            } else {
                Date date = new Date();
                currenttime = simpleDateFormat.format(calendar.getTime());
                MessagesModel messages = new MessagesModel(enteredmessages, firebaseAuth.getUid(), currenttime, date.getTime());
                firebaseDatabase.getReference("chats")// pangalan ng db
                        .child(senderroom)
                        .child("messages")
                        .push()
                        .setValue(messages)
                        .addOnSuccessListener(unused ->
                                firebaseDatabase.getReference("chats")
                                        .child(receiverroom)
                                        .child("messages")
                                        .push()
                                        .setValue(messages)
                                        .addOnSuccessListener(unused1 -> {
                                        }));

                et_getmessage.setText(null);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Online");
        } catch (Exception exception) {
            Log.d("EXCEPTION", exception.getMessage());

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStop() {
        super.onStop();
        if (messagesAdapter != null) {
            messagesAdapter.notifyDataSetChanged();
        }
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Offline");
        } catch (Exception exception) {
            Log.d("EXCEPTION", exception.getMessage());

        }

    }

}