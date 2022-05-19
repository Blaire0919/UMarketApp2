package bbc.umarket.umarketapp2.SellerSide;

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

import bbc.umarket.umarketapp2.Adapter.CompletedOrderAdapter;
import bbc.umarket.umarketapp2.Adapter.ShippingAdapter;
import bbc.umarket.umarketapp2.Adapter.ToProcessAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.R;

public class ManageOrders extends AppCompatActivity {

    ImageView back;
    String studid;

    //to process orders
    RecyclerView toProcessRecyclerView;
    ToProcessAdapter toProcessAdapter;
    ArrayList<ToProcessModel> toProcessList;

    //shipping orders
  public static  RecyclerView shippingRecyclerView;
    ShippingAdapter shippingAdapter;
    ArrayList<ToProcessModel> shippingList;

    //completed orders
    RecyclerView completedOrderRecyclerview;
    CompletedOrderAdapter completedOrderAdapter;
    ArrayList<ToProcessModel> completedList;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_manageorders);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        back = findViewById(R.id.manageorders_back);

        toProcessRecyclerView = findViewById(R.id.toProcess_RecyclerView);
        shippingRecyclerView = findViewById(R.id.shipping_RecyclerView);
        completedOrderRecyclerview = findViewById(R.id.completedOrders_RecyclerView);


        back.setOnClickListener(view -> {
            Intent intent = new Intent(ManageOrders.this, SellerCenter.class);
            startActivity(intent);
        });


        //to process orders
        toProcessRecyclerView.setHasFixedSize(true);
        toProcessRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        toProcessList = new ArrayList<>();
        toProcessAdapter = new ToProcessAdapter(this, toProcessList);
        toProcessRecyclerView.setAdapter(toProcessAdapter);

        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("process_order").child(studid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ToProcessModel toProcessModel = snapshot1.getValue(ToProcessModel.class);
                                if (toProcessModel != null) {
                                    toProcessList.add(toProcessModel);
                                }
                            }
                        }
                        toProcessAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        //shipping order
        shippingRecyclerView.setHasFixedSize(true);
        shippingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        shippingList = new ArrayList<>();
        shippingAdapter = new ShippingAdapter(this, shippingList);
        shippingRecyclerView.setAdapter(shippingAdapter);

        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("shipping_order").child(studid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ToProcessModel shippingModel = snapshot1.getValue(ToProcessModel.class);
                                if (shippingModel != null) {
                                    shippingList.add(shippingModel);
                                }
                            }
                        }
                        shippingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        //completed order
        completedOrderRecyclerview.setHasFixedSize(true);
        completedOrderRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        completedList = new ArrayList<>();
        completedOrderAdapter = new CompletedOrderAdapter(this, completedList);
        completedOrderRecyclerview.setAdapter(completedOrderAdapter);

        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("completed_order").child(studid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                ToProcessModel completedModel = snapshot1.getValue(ToProcessModel.class);
                                if (completedModel != null) {
                                    completedList.add(completedModel);
                                }
                            }
                        }
                        completedOrderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }



    @Override
    public void onStart() {
        super.onStart();

        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Online");
        } catch (Exception exception) {
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