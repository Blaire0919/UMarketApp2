package bbc.umarket.umarketapp2.SellerSide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Adapter.SellerItemAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.ItemHelperClass;
import bbc.umarket.umarketapp2.Main.HomeContainer;
import bbc.umarket.umarketapp2.R;

public class SellerCenter extends AppCompatActivity {
    ImageView back, sellerimg, chat;
    TextView sellername, availProdCount, pendingOrders;
    Integer prodCount=0, pOrdersCount=0;
    Toolbar toolbar;
    MaterialCardView btnaddprod, btnsaleshistory;
    String studid;
    List<String> productID = new ArrayList<>();

    //realtime database
    DatabaseReference  reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("products");
    DatabaseReference sellerroot = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("seller");
    DatabaseReference Prodroot = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("products");
    DatabaseReference process_orders = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("process_order");

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    //seller product listing
    ArrayList<ItemHelperClass> sellerProd;
    SellerItemAdapter itemAdapter;
    RecyclerView sellerRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_seller_center);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(SellerCenter.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.sc_back);
        btnaddprod = findViewById(R.id.add);
        btnsaleshistory = findViewById(R.id.saleshistory);
        sellerimg = findViewById(R.id.sellercenter_imgview);
        sellername = findViewById(R.id.sellercenter_name);
        availProdCount = findViewById(R.id.availprodcount);
        chat = findViewById(R.id.seller_chat);
        sellerRv = findViewById(R.id.RV_sellerlisting);
        pendingOrders = findViewById(R.id.tvPendingOrders);

        setSupportActionBar(toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_more_vert_24);
        toolbar.setOverflowIcon(drawable);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(SellerCenter.this, HomeContainer.class);
            intent.putExtra("back_Acc", "Account");
            startActivity(intent);
            finish();
        });

        chat.setOnClickListener(view -> {
            Intent intent = new Intent(SellerCenter.this, HomeContainer.class);
            intent.putExtra("back_Chat", "Chat");
            startActivity(intent);
            finish();
        });

        btnaddprod.setOnClickListener(view1 -> {
            Intent intent = new Intent(SellerCenter.this, AddListing.class);
            startActivity(intent);
        });

        process_orders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot orders: snapshot.getChildren()){
                        for(DataSnapshot child : orders.getChildren()){
                            String orderid = child.child("sellerID").getValue(String.class);
                            assert orderid != null;
                            if (orderid.equals(studid)){
                                pOrdersCount++;
                            }
                        }


                    }pendingOrders.setText(String.valueOf(pOrdersCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sellerroot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot seller : snapshot.getChildren()) {
                        String userid = seller.child("userID").getValue(String.class);
                        assert userid != null;

                        if (userid.equals(studid)) {
                            Glide.with(SellerCenter.this)
                                    .load(seller.child("imgSeller").getValue(String.class))
                                    .into(sellerimg);

                            sellername.setText(seller.child("shopname").getValue(String.class));

                            Prodroot.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot snaps : snapshot.getChildren()) {
                                            String sellerid = snaps.child("pSellerID").getValue(String.class);
                                            assert sellerid != null;
                                            if (sellerid.equals(studid)) {
                                                prodCount++;
                                                productID.add(snaps.child("pID").getValue(String.class));
                                            }
                                        }
                                        availProdCount.setText(String.valueOf(prodCount));
                                        DataInsights.getListProdID(productID);
                                        Log.d("PRODUCT ID", String.valueOf(productID));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        sellerRv.setHasFixedSize(true);
        sellerRv.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false ));
        sellerProd = new ArrayList<>();
        itemAdapter = new SellerItemAdapter(this, sellerProd);
        sellerRv.setAdapter(itemAdapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ItemHelperClass itemHelperClass = snap.getValue(ItemHelperClass.class);
                    if (itemHelperClass != null && Objects.equals(itemHelperClass.getpSellerID(), studid)) {
                        sellerProd.add(itemHelperClass);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });



    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sellernav_datainsights:
                Intent intent = new Intent(SellerCenter.this, DataInsights.class);
                startActivity(intent);
                break;

            case R.id.sellernav_manageorders:
                Intent MOintent = new Intent(SellerCenter.this, ManageOrders.class);
                startActivity(MOintent);
                Toast.makeText(getApplicationContext(), "Manage Orders Clicked", Toast.LENGTH_LONG).show();
                break;

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sellercenter_menu, menu);

        return super.onCreateOptionsMenu(menu);
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













