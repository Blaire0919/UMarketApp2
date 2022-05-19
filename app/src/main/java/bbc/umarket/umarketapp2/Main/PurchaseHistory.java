package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.PHistoryAdapter;
import bbc.umarket.umarketapp2.Adapter.ToReceiveAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.R;

public class PurchaseHistory extends AppCompatActivity {

    ImageView back;
    String studid;
    RecyclerView purchaseHistoryRecyclerView;
    ArrayList<ToReceiveModel> purchaseList;
    PHistoryAdapter purchaseHistoryAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_purchasehistory);

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

    firebaseAuth = FirebaseAuth.getInstance();
    firebaseFirestore = FirebaseFirestore.getInstance();

        back = findViewById(R.id.ph_back);
        purchaseHistoryRecyclerView = findViewById(R.id.PurchaseHistoryRecyclerview);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(PurchaseHistory.this, HomeContainer.class);
            intent.putExtra("back_Acc","Account");
            startActivity(intent);
            finish();
        });

        //purchase history recyclerview
        purchaseHistoryRecyclerView.setHasFixedSize(true);
        purchaseHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        purchaseList = new ArrayList<>();
        purchaseHistoryAdapter = new PHistoryAdapter(this, purchaseList);
        purchaseHistoryRecyclerView.setAdapter(purchaseHistoryAdapter);

        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("purchase_history").child(studid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                ToReceiveModel purchaseModel = snapshot1.getValue(ToReceiveModel.class);
                                if (purchaseModel != null){
                                    purchaseList.add(purchaseModel);
                                }
                            }
                        }purchaseHistoryAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Online");}
        catch (Exception exception) {
            Log.d("EXCEPTION", exception.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Offline");
        } catch (Exception exception) {
            Log.d("EXCEPTION", exception.getMessage());

        }
    }

}