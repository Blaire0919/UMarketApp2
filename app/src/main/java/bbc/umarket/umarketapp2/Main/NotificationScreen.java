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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import bbc.umarket.umarketapp2.Adapter.NotifAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.R;

public class NotificationScreen extends AppCompatActivity {

    ImageView back;

    RecyclerView notifRecyclerview;
    NotifAdapter notifAdapter;
    ArrayList<NotifModel> notifList;

    String studid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_notifscreen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(NotificationScreen.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);


        back = findViewById(R.id.notif_back);
        notifRecyclerview = findViewById(R.id.notification_recyclerview);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(NotificationScreen.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
        });

        notifRecyclerview.setHasFixedSize(true);
        notifRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        notifList = new ArrayList<>();
        notifAdapter = new NotifAdapter(this, notifList);
        notifRecyclerview.setAdapter(notifAdapter);

        DatabaseReference notifRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("notification").child(studid);

        notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        NotifModel notifModel = snapshot1.getValue(NotifModel.class);
                        if(notifModel!= null){
                            notifList.add(notifModel);
                        }
                    }
                }else{
                    Toast.makeText(NotificationScreen.this, "No notifications", Toast.LENGTH_LONG).show();
                }
                notifAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ERROROROR", error.getDetails());
            }
        });

    }
}