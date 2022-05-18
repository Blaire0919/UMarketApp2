package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import bbc.umarket.umarketapp2.Adapter.ToReceiveAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.R;

public class ToReceive extends AppCompatActivity {

    ImageView back;
    String studid;
    RecyclerView toreceiveRecyclerView;
    ArrayList<ToReceiveModel> toReceiveList;
    ToReceiveAdapter toReceiveAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_toreceive);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar


        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);


        back = findViewById(R.id.toreceive_back);
        toreceiveRecyclerView = findViewById(R.id.toreceive_recyclerview);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(ToReceive.this, HomeContainer.class);
            intent.putExtra("back_Acc","Account");
            startActivity(intent);
            finish();
        });


        //to receive recyclerview
        toreceiveRecyclerView.setHasFixedSize(true);
        toreceiveRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        toReceiveList = new ArrayList<>();
        toReceiveAdapter = new ToReceiveAdapter(this, toReceiveList);
        toreceiveRecyclerView.setAdapter(toReceiveAdapter);

        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("to_receive").child(studid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                               ToReceiveModel toReceiveModel = snapshot1.getValue(ToReceiveModel.class);
                                if (toReceiveModel != null){
                                    toReceiveList.add(toReceiveModel);
                                }
                            }
                        }toReceiveAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

    }
}