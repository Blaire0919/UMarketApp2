package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.RankItemAdapter;
import bbc.umarket.umarketapp2.Helper.ItemRankedHelperClass;
import bbc.umarket.umarketapp2.R;

public class DataInsights extends AppCompatActivity {

    ImageView back;
    TextView totsales, totorders, totnumCustomers;

    public static List<String> productID = new ArrayList<>();
    Integer rrcount = 0;
    //  Float totalsales = 0.00F;


    //for Seller Product Ranking
    RecyclerView RankedRecyclerview;
    RankItemAdapter RankedAdapter;
    ArrayList<ItemRankedHelperClass> RankedItemList;


    //Database references
    DatabaseReference RRroot = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("rateandreview");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_data_insights);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        back = findViewById(R.id.datainsights_back);
        totsales = findViewById(R.id.txtTotSale);
        totorders = findViewById(R.id.txtTotOrders);
        totnumCustomers = findViewById(R.id.txtTotCustomer);
        RankedRecyclerview = findViewById(R.id.ranked_Recyclerview);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(DataInsights.this, SellerCenter.class);
            startActivity(intent);
            finish();
        });

        RankedRecyclerview.setHasFixedSize(true);
        RankedRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        RankedItemList = new ArrayList<>();
        RankedAdapter = new RankItemAdapter(this, RankedItemList);
        RankedRecyclerview.setAdapter(RankedAdapter);

        DatabaseReference reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("products");
        for (String itemid : productID) {
            Query rankitems = reference.orderByChild("pID").equalTo(itemid);
            rankitems.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String url = item.child("imageUrl").getValue(String.class);
                            String name = item.child("pName").getValue(String.class);
                            String id = item.child("pID").getValue(String.class);
                            ItemRankedHelperClass itemRankedHelperClass = new ItemRankedHelperClass(url, name);
                            assert id != null;
                            if (id.equals(itemid)) {
                                RankedItemList.add(itemRankedHelperClass);
                            }
                        }
                    RankedAdapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        RRroot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String pid = snapshot1.child("prodID").getValue(String.class);
                        for (String id : productID) {
                            if (id.equals(pid)) {
                                rrcount++;
//                                Log.d("rate and review id", id);
//                                Log.d("RR ID", snapshot1.child("rrID").getValue(String.class));
                            }
                        }
                        totorders.setText(String.valueOf(rrcount));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    public static void getListProdID(List<String> value) {
        try {
            productID = value;
        } catch (Exception e) {
            Log.d("EXCEPTION", e.getMessage());
        }
    }

}