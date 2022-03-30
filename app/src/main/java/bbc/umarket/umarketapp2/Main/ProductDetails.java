package bbc.umarket.umarketapp2.Main;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//Python Imports
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import bbc.umarket.umarketapp2.Adapter.CartItemAdapter;
import bbc.umarket.umarketapp2.Helper.CartHelperClass;
import bbc.umarket.umarketapp2.Helper.ClickedHistoryHelperClass;
import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Helper.ItemHelperClass;
import bbc.umarket.umarketapp2.Adapter.RateReviewAdapter;
import bbc.umarket.umarketapp2.Helper.RateReviewHelperClass;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Listener.CartItemLoadListener;
import bbc.umarket.umarketapp2.R;

public class ProductDetails extends AppCompatActivity implements CartItemLoadListener {
    ImageView back;
    Button buy, cart;
    ImageView PR_image;
    TextView PRbrand, PRcondition, PRdesc, PRhandling, PRname, PRrate, PRprice, PRstock, PRsellername;
    RatingBar PRratingbar;

    //for id
    String pID, prodID, studid;

    //for for you recommendation recyclerview
    RecyclerView items;
    ItemAdapter itemAdapter;
    ArrayList<ItemHelperClass> listItem;

    //for rate and review recyclerview
    RecyclerView ratenreview;
    RateReviewAdapter rrAdapter;
    ArrayList<RateReviewHelperClass> rrlist;

    //for rate and review database
    String imageUrl, pName, currentdatetime;

    //for clicked history database
    ClickedHistoryHelperClass clickedHistoryHelperClass;
    String chdatetime;
    long maxid = 0;

    Integer qty = 0;
    String itemID;

    //for add to cart database
    CartHelperClass cartHelperClass;
    String CuserID, CprodID, CsellerID, CsellerName, CprodName, CprodQty, CprodPrice, CimgUrl, CdateTime, CtotalPrice;
    LinearLayout mlayout;
    CartItemLoadListener cartItemLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_prod_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        cartItemLoadListener = this;

        //getting data from session
        SessionManager sessionManager = new SessionManager(ProductDetails.this);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        Intent prodintent = getIntent();
        prodID = prodintent.getExtras().getString("pID");

        //hook
        back = findViewById(R.id.proddetails_back);
        PR_image = findViewById(R.id.pd_image);
        PRbrand = findViewById(R.id.pd_brand);
        PRcondition = findViewById(R.id.pd_condition);
        PRdesc = findViewById(R.id.pd_description);
        PRhandling = findViewById(R.id.pd_handlingfee);
        PRname = findViewById(R.id.pd_name);
        PRratingbar = findViewById(R.id.pd_rate);
        PRrate = findViewById(R.id.pd_ptotalrate);
        PRprice = findViewById(R.id.pd_price);
        PRstock = findViewById(R.id.pd_stock);
        PRsellername = findViewById(R.id.pd_seller_name);
        items = findViewById(R.id.pd_recommend_recyclerview);
        ratenreview = findViewById(R.id.proddetails_recyclerview);
        buy = findViewById(R.id.btnbuy);
        cart = findViewById(R.id.btnaddtocart);
        mlayout = findViewById(R.id.mlayout);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(ProductDetails.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
        });

        DatabaseReference reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("products");
        Query checkID = reference.orderByChild("pID").equalTo(prodID);

        checkID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pID = snapshot.child(prodID).child("pID").getValue(String.class);
                    assert pID != null;
                    if (pID.equals(prodID)) {

                        Glide.with(ProductDetails.this)
                                .load(snapshot.child(prodID).child("imageUrl").getValue(String.class))
                                .into(PR_image);
                        imageUrl = snapshot.child(prodID).child("imageUrl").getValue(String.class);

                        PRbrand.setText(snapshot.child(prodID).child("pBrand").getValue(String.class));
                        PRcondition.setText(snapshot.child(prodID).child("pCondition").getValue(String.class));
                        PRdesc.setText(snapshot.child(prodID).child("pDescription").getValue(String.class));
                        PRhandling.setText(snapshot.child(prodID).child("pHandlingFee").getValue(String.class));
                        PRname.setText(snapshot.child(prodID).child("pName").getValue(String.class));
                        pName = snapshot.child(prodID).child("pName").getValue(String.class);

                        DatabaseReference ratingref = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("rateandreview");

                        ratingref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("DefaultLocale")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                float average, total = 0.0F, rating;
                                int count = 0;
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.child("prodID").getValue(String.class).equals(prodID)) {
                                            rating = Float.parseFloat(Objects.requireNonNull(dataSnapshot.child("rate").getValue(String.class)));
                                            total = total + rating;
                                            count = count + 1;
                                            average = total / count;
                                            PRratingbar.setRating(average);
                                            PRrate.setText(String.format("%.1f", ((double) average)));
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                        PRprice.setText(String.format("₱ %s", snapshot.child(prodID).child("pPrice").getValue(String.class)));
                        PRstock.setText(snapshot.child(prodID).child("pStock").getValue(String.class));
                        CprodPrice = Objects.requireNonNull(snapshot.child(prodID).child("pPrice").getValue(String.class));
                        CtotalPrice = Objects.requireNonNull(snapshot.child(prodID).child("pPrice").getValue(String.class));

                        final String SellerID = snapshot.child(prodID).child("pSellerID").getValue(String.class);

                        DatabaseReference userRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                .getReference("users");
                        Query checkUserID = userRef.orderByChild("studID").equalTo(SellerID);

                        checkUserID.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snaps) {
                                if (snaps.exists()) {
                                    assert SellerID != null;
                                    String fnamefromDB = snaps.child(SellerID).child("fname").getValue(String.class);
                                    String lnamefromDB = snaps.child(SellerID).child("lname").getValue(String.class);
                                    PRsellername.setText(String.format("%s %s", fnamefromDB, lnamefromDB));
                                    CsellerName = String.format("%s %s", fnamefromDB, lnamefromDB);
                                }

                                DatabaseReference refCH = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("clicked_history")
                                        .child(studid);
                                refCH.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            maxid = (snapshot.getChildrenCount());
                                            chdatetime = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                            clickedHistoryHelperClass = new ClickedHistoryHelperClass(studid, chdatetime, pID);
                                            refCH.child(String.valueOf(maxid + 1)).setValue(clickedHistoryHelperClass);
                                        } else {
                                            maxid = 1;
                                            chdatetime = java.text.DateFormat.getDateTimeInstance().format(new Date());
                                            clickedHistoryHelperClass = new ClickedHistoryHelperClass(studid, chdatetime, pID);
                                            refCH.child(String.valueOf(maxid)).setValue(clickedHistoryHelperClass);
                                        }
                                        currentdatetime = chdatetime;

                                        CuserID = studid;
                                        CprodID = prodID;
                                        CsellerID = SellerID;
                                        CprodName = pName;
                                        CprodQty = "1";
                                        CimgUrl = imageUrl;

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        //recommendation recyclerview
        items.setHasFixedSize(true);
        items.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        listItem = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, listItem);
        items.setAdapter(itemAdapter);

        //this will start python
        Python py = Python.getInstance();
        //get Script
        PyObject pyobj = py.getModule("rec");

        DatabaseReference ItemRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("products");
        Query lookItem = reference.orderByChild("pID").equalTo(prodID);
        lookItem.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //Set paramater to get recommendations
                String itemName = snapshot.child(prodID).child("pName").getValue(String.class);
                //return to a array
                //    String[] rec = pyobj.callAttr("main", itemName).toJava(String[].class);
                String[] rec = pyobj.callAttr("main", itemName).toJava(String[].class);
                for (String strTemp : rec) {
                    ItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                ItemHelperClass itemHelperClass = snap.getValue(ItemHelperClass.class);
                                assert itemHelperClass != null;
                                if (itemHelperClass.getpName().equals(strTemp)) {
                                    listItem.add(itemHelperClass);
                                }
                            }
                            itemAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        //create and review recyclerview
        ratenreview.setHasFixedSize(true);
        ratenreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rrlist = new ArrayList<>();
        rrAdapter = new RateReviewAdapter(this, rrlist);
        ratenreview.setAdapter(rrAdapter);
        DatabaseReference rrRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("rateandreview");
        rrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snaps1 : snapshot.getChildren()) {
                        RateReviewHelperClass rateReviewHelperClass = snaps1.getValue(RateReviewHelperClass.class);
                        if (rateReviewHelperClass != null && rateReviewHelperClass.getProdID().equals(prodID)) {
                            rrlist.add(rateReviewHelperClass);
                        }
                    }
                } else {
                    Toast.makeText(ProductDetails.this, "HINDI GUMAGANA", Toast.LENGTH_SHORT).show();
                }
                rrAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        cart.setOnClickListener(view -> {
            DatabaseReference refCart = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("cart").child(studid).child(prodID);

            refCart.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //if there is a same item in cart
                        itemID = snapshot.child("prodID").getValue(String.class);
                        assert itemID != null;
                        if (itemID.equals(prodID)) {
                            Log.d(TAG, itemID);
                            CartHelperClass cartHelperClass = snapshot.getValue(CartHelperClass.class);

                            assert cartHelperClass != null;
                            qty = Integer.parseInt(cartHelperClass.getProdQty()) + 1;
                            cartHelperClass.setProdQty(qty.toString());
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("prodQty", qty.toString());
                            Float totprice = Integer.parseInt(cartHelperClass.getProdQty()) * Float.parseFloat(cartHelperClass.getTotalPrice());
                            updateData.put("totalPrice", String.format("%.2f", totprice));

                            refCart.updateChildren(updateData)
                                    .addOnSuccessListener(unused -> cartItemLoadListener.onCartLoadFailed("Add to Cart Successful"))
                                    .addOnFailureListener(e -> cartItemLoadListener.onCartLoadFailed(e.getMessage()));
                        }

                    } else {
                        CdateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
                        clickedHistoryHelperClass = new ClickedHistoryHelperClass(studid, chdatetime, pID);
                        cartHelperClass = new CartHelperClass(CuserID, CprodID, CsellerID, CsellerName, CprodName, CprodQty, CprodPrice, CimgUrl, CdateTime, CtotalPrice);
                        refCart.setValue(cartHelperClass);
                        Snackbar.make(mlayout, "Add to Cart Successful", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        buy.setOnClickListener(view -> {
            Intent intent = new Intent(ProductDetails.this, RateAndReview.class);
            intent.putExtra("pID", prodID);
            intent.putExtra("pName", pName);
            intent.putExtra("pImg", imageUrl);
            intent.putExtra("datetime", currentdatetime);
            startActivity(intent);
            finish();
        });
    }


    @Override
    public void onCartLoadSuccess(ArrayList<CartHelperClass> cartItemList) { }

    @Override
    public void onCartLoadFailed(String message) {Snackbar.make(mlayout, message, Snackbar.LENGTH_LONG).show(); }
}