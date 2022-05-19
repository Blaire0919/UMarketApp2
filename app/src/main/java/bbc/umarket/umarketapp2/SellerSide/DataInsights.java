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
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    //for Seller Product Ranking
    RecyclerView RankedRecyclerview;
    RankItemAdapter RankedAdapter;
    ArrayList<ItemRankedHelperClass> RankedItemList;

    //Database references
    DatabaseReference RRroot = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("rateandreview");


    //for charts
    BarChart barChart;
    PieChart pieChart;
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<PieEntry> pieEntries = new ArrayList<>();


    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_data_insights);

       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        back = findViewById(R.id.datainsights_back);
        totsales = findViewById(R.id.txtTotSale);
        totorders = findViewById(R.id.txtTotOrders);
        totnumCustomers = findViewById(R.id.txtTotCustomer);
        RankedRecyclerview = findViewById(R.id.ranked_Recyclerview);

      firebaseAuth = FirebaseAuth.getInstance();
       firebaseFirestore = FirebaseFirestore.getInstance();

        //static charts
        barChart = findViewById(R.id.barchart);
        pieChart = findViewById(R.id.piechart);

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


        //use for loop in charts
        for (int i=1; i<10;i++){
                //convert to float value
                float  value  = (float) (i*10.0);
                //initialize bar entry
                BarEntry barEntry  = new BarEntry(i, value);
                //initialize pie entry
                PieEntry pieEntry = new PieEntry(i, value);
                barEntries.add(barEntry);
                pieEntries.add(pieEntry);
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Income");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setDrawValues(false);
        barChart.setData(new BarData(barDataSet));
        barChart.animateY(5000);
        barChart.getDescription().setText("Net Income Chart");
        barChart.getDescription().setTextColor(R.color.NewUMarketDarkBlue);


        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Gross Sales");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setDrawValues(false);
        pieChart.setData(new PieData(pieDataSet));
        pieChart.animateXY(5000, 5000);
        pieChart.getDescription().setEnabled(false);

    }

    public static void getListProdID(List<String> value) {
        try {
            productID = value;
        } catch (Exception e) {
            Log.d("EXCEPTION", e.getMessage());
        }
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