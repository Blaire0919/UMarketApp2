package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.SellerHelperClass;
import bbc.umarket.umarketapp2.R;

public class SellerCenter extends AppCompatActivity {
    ImageView back, sellerimg;
    TextView sellername, availProdCount;
    Integer prodCount = 0;
    Toolbar toolbar;
    MaterialCardView btnaddprod, btnsaleshistory;
    String studid;
    List<String> productID = new ArrayList<>();

    DatabaseReference sellerroot = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("seller");

    DatabaseReference Prodroot = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("products");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_seller_center);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(SellerCenter.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.sc_back);
        btnaddprod = findViewById(R.id.add);
        btnsaleshistory = findViewById(R.id.saleshistory);
        sellerimg = findViewById(R.id.sellercenter_imgview);
        sellername = findViewById(R.id.sellercenter_name);
        availProdCount = findViewById(R.id.availprodcount);

        setSupportActionBar(toolbar);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_more_vert_24);
        toolbar.setOverflowIcon(drawable);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(SellerCenter.this, HomeContainer.class);
            intent.putExtra("back_Acc", "Account");
            startActivity(intent);
            finish();
        });
        btnaddprod.setOnClickListener(view1 -> {
            Intent intent = new Intent(SellerCenter.this, AddListing.class);
            startActivity(intent);
        });

        sellerroot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot seller : snapshot.getChildren()) {
                        String userid = seller.child("userID").getValue(String.class);
                        assert userid != null;
                        if (userid.equals(studid)) {
                            Glide.with(SellerCenter.this)
                                    .load( seller.child("imgSeller").getValue(String.class))
                                    .into(sellerimg);

                            sellername.setText(seller.child("shopname").getValue(String.class));

                            Prodroot.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot snaps : snapshot.getChildren()){
                                            String sellerid = snaps.child("pSellerID").getValue(String.class);
                                            assert sellerid != null;
                                            if(sellerid.equals(studid)){
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
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.sellernav_datainsights:
                Intent intent = new Intent(SellerCenter.this, DataInsights.class);
                startActivity(intent);
                break;

            case R.id.sellernav_manageorders:
//                Intent intent = new Intent(SellerCenter.this, DataInsights.class);
//                startActivity(intent);
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
}













