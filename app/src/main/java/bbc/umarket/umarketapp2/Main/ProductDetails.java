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
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nex3z.notificationbadge.NotificationBadge;

import bbc.umarket.umarketapp2.Adapter.CartItemAdapter;
import bbc.umarket.umarketapp2.Fragments.FragChat;
import bbc.umarket.umarketapp2.Helper.CartHelperClass;
import bbc.umarket.umarketapp2.Helper.CheckOutHelperClass;
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
    ImageView PR_image, ChatSeller;
    TextView PRbrand, PRcondition, PRdesc, PRname, PRrate, PRprice, PRstock, PRsellername;
    RatingBar PRratingbar;
    ImageView gotocart;
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

    //checkout
    CheckOutHelperClass checkOutHelperClass;
    public static String coImgUrl, coProdId, coSellerName, coProdName, coQty, coPrice, coSubTotal, coSellerID;

    //for add to cart database
    CartHelperClass cartHelperClass;
    String CuserID, CprodID, CsellerID, CsellerName, CprodName, CprodQty, CprodPrice, CimgUrl, CdateTime, CtotalPrice;
    LinearLayout mlayout;
    CartItemLoadListener cartItemLoadListener;

    DocumentReference documentReference;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    
    NotificationBadge cartCount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_prod_details);
        //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        cartItemLoadListener = this;
        countCartItem();

        //getting data from session
        SessionManager sessionManager = new SessionManager(ProductDetails.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent prodintent = getIntent();
        prodID = prodintent.getExtras().getString("pID");

        //hook
        back = findViewById(R.id.proddetails_back);
        PR_image = findViewById(R.id.pd_image);
        PRbrand = findViewById(R.id.pd_brand);
        PRcondition = findViewById(R.id.pd_condition);
        PRdesc = findViewById(R.id.pd_description);
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
        gotocart = findViewById(R.id.proddetails_cart);
        cartCount = findViewById(R.id.prodDetails_cartbadge);
        ChatSeller = findViewById(R.id.ImageViewChatSeller);

        gotocart.setOnClickListener(view -> {
            Intent intent = new Intent(ProductDetails.this, AddToCart.class);
            startActivity(intent);
            finish();
        });

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
                        PRname.setText(snapshot.child(prodID).child("pName").getValue(String.class));
                        pName = snapshot.child(prodID).child("pName").getValue(String.class);

                        //for checkout single item parameter
                        coImgUrl = imageUrl;
                        coProdId = prodID;
                        coProdName = pName;

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

                        coQty = "1";
                        coPrice = snapshot.child(prodID).child("pPrice").getValue(String.class);
                        coSubTotal = coPrice;

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

                                    coSellerName = String.format("%s %s", fnamefromDB, lnamefromDB);
                                    coSellerID = SellerID;
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
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
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
                                    if (!itemHelperClass.getpSellerID().equals(studid)) {
                                        listItem.add(itemHelperClass);
                                    }

                                }
                            }
                            itemAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });

        //create and review recyclerview
        ratenreview.setHasFixedSize(true);
        ratenreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rrlist = new ArrayList<>();
        rrAdapter = new RateReviewAdapter(this, rrlist);
        ratenreview.setAdapter(rrAdapter);
        DatabaseReference rrRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("rateandreview");

        //Rate and review
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
                    Log.d(TAG, "AYAW GUMANA");
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


                        Toast.makeText(ProductDetails.this, "Add to Cart Successful", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        buy.setOnClickListener(view -> {
            checkOutHelperClass = new CheckOutHelperClass(coImgUrl, coProdId, coSellerName, coProdName, coQty, coPrice, coSubTotal, coSellerID);
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("checkout")
                    .child(studid)
                    .child("items")
                    .child(prodID)
                    .setValue(checkOutHelperClass)
                    .addOnSuccessListener(unused -> {
                        Log.d(TAG, "Checkout Success!");
                        Intent intent = new Intent(ProductDetails.this, Checkout.class);
                        intent.putExtra("pID", prodID);
                        startActivity(intent);
                        finish();
                    });
        });

        ChatSeller.setOnClickListener(view -> {
            FragChat.GoToSeller(coSellerID);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        countCartItem();
    }


    private void countCartItem() {
        ArrayList<CartHelperClass> cartHelperClasses = new ArrayList<>();

        SessionManager sessionManager = new SessionManager(ProductDetails.this, SessionManager.SESSION_REMEMBERME);
        HashMap<String, String> usersdetails = sessionManager.getRememberMeDetailsFromSession();

        SessionManager sessionManager2 = new SessionManager(ProductDetails.this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails2 = sessionManager2.getUserDetailSession();

        if (usersdetails.get(SessionManager.KEY_SESSIONSTUDID) != null) {
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("cart").child(Objects.requireNonNull(usersdetails.get(SessionManager.KEY_SESSIONSTUDID)))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot cartsnapshot : snapshot.getChildren()) {
                                    CartHelperClass cartHelperClass = cartsnapshot.getValue(CartHelperClass.class);
                                    cartHelperClasses.add(cartHelperClass);
                                }
                                cartItemLoadListener.onCartLoadSuccess(cartHelperClasses);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("", error.getDetails());
                        }
                    });
        } else {
            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("cart").child(Objects.requireNonNull(usersdetails2.get(SessionManager.KEY_STUDID)))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot cartsnapshot : snapshot.getChildren()) {
                                    CartHelperClass cartHelperClass = cartsnapshot.getValue(CartHelperClass.class);
                                    cartHelperClasses.add(cartHelperClass);
                                }
                                cartItemLoadListener.onCartLoadSuccess(cartHelperClasses);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("", error.getDetails());
                        }
                    });
        }


    }

    @Override
    public void onCartLoadSuccess(ArrayList<CartHelperClass> cartItemList) {
        int cartSum = 0;
        for (CartHelperClass cartHelperClass : cartItemList) {
            cartSum += Integer.parseInt(cartHelperClass.getProdQty());
            cartCount.setNumber(cartSum);
        }
    }

    @Override
    public void onCartLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (documentReference != null) {
            try {
                documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
                documentReference.update("status", "Online");
            } catch (Exception exception) {
                Log.d("EXCEPTION", exception.getMessage());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (documentReference != null) {
            try {
                documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
                documentReference.update("status", "Offline");
            } catch (Exception exception) {
                Log.d("EXCEPTION", exception.getMessage());
            }
        }
    }
}