package bbc.umarket.umarketapp2.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.CategoryAdapter;
import bbc.umarket.umarketapp2.Helper.CartHelperClass;
import bbc.umarket.umarketapp2.Helper.CategoryHelperClass;
import bbc.umarket.umarketapp2.Adapter.FeatProdAdapter;
import bbc.umarket.umarketapp2.Helper.FeatProdtHelperClass;
import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Helper.ItemHelperClass;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Listener.CartItemLoadListener;
import bbc.umarket.umarketapp2.Listener.ItemLoadListener;
import bbc.umarket.umarketapp2.Listener.NotifItemLoadListener;
import bbc.umarket.umarketapp2.Main.AddToCart;
import bbc.umarket.umarketapp2.Main.NotificationScreen;
import bbc.umarket.umarketapp2.Main.Search;
import bbc.umarket.umarketapp2.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FragHome extends Fragment implements ItemLoadListener, CartItemLoadListener, NotifItemLoadListener {
    Context context;
    TextInputEditText search;
    RecyclerView category, items, features;
    CategoryAdapter categoryAdapter;
    FeatProdAdapter featProdAdapter;
    ItemAdapter itemAdapter;
    ArrayList<CategoryHelperClass> catlist;
    ArrayList<FeatProdtHelperClass> featprod;
    ArrayList<ItemHelperClass> listItem;
    String studid;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.mainLayout)
    FrameLayout mainLayout;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.badge)
    NotificationBadge badge;

    ImageView btncart;

    ItemLoadListener itemLoadListener;
    CartItemLoadListener cartItemLoadListener;

    //for database
    FirebaseDatabase rootNode;
    DatabaseReference reference, forInterest;

    //for interest
    String beautyproducts;
    String clothesaccessories;
    String electronics;
    String foodbeverages;
    String schoolsupplies;
    String sportsequipment;
    ArrayList<String> cat_interests = new ArrayList<>();

    ImageView notifbutton;
    NotificationBadge notificationBadge;
    NotifItemLoadListener notifItemLoadListener;

    public FragHome() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frag_home, container, false);
        ButterKnife.bind(this, view);
        context = view.getContext();

        itemLoadListener = this;
        cartItemLoadListener = this;
        countCartItem();

        notifItemLoadListener = this;
        countNotif();

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //for image slider on dashboard fragment
        ImageSlider imageSlider = view.findViewById(R.id.dashSlider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.slider_01, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider_03, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        //studid
        SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        //hooks
        category = view.findViewById(R.id.category_recyclerview);
        items = view.findViewById(R.id.item_RecyclerView);
        features = view.findViewById(R.id.featured_RecyclerView);
        search = view.findViewById(R.id.tilSearch);
        btncart = view.findViewById(R.id.btnCart);
        notifbutton = view.findViewById(R.id.btn_notif);
        notificationBadge = view.findViewById(R.id.notifbadge);

        reference = rootNode.getReference("products");
        forInterest = rootNode.getReference("interests");

        notifbutton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), NotificationScreen.class);
            startActivity(intent);
        });

        //for category recycler view
        category.setHasFixedSize(true);
        category.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        catlist = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(context, catlist);
        category.setAdapter(categoryAdapter);
        categoryRecycler();

        //for featured
        features.setHasFixedSize(true);
        features.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        featprod = new ArrayList<>();
        featProdAdapter = new FeatProdAdapter(context, featprod);
        features.setAdapter(featProdAdapter);

        //this will start python
        Python py = Python.getInstance();
        //get Script
        PyObject pyobj = py.getModule("foryou_rec");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//              convert the returned value from algo to array
               String[] rec = pyobj.callAttr("main", studid).toJava(String[].class);
               for (String strTemp : rec) {
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapi : snapshot.getChildren()) {
                                FeatProdtHelperClass featProdtHelperClass = snapi.getValue(FeatProdtHelperClass.class);
                                assert featProdtHelperClass != null;
                                if (featProdtHelperClass.getpName().equals(strTemp)) {
                                    if(!featProdtHelperClass.getpSellerID().equals(studid)){
                                        featprod.add(featProdtHelperClass);
                                    }

                                }
                            }
                            featProdAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });

        //for product listing
        items.setHasFixedSize(true);
        items.setLayoutManager(new GridLayoutManager(context, 3, RecyclerView.VERTICAL, false ));
        listItem = new ArrayList<>();
        itemAdapter = new ItemAdapter(context, listItem);
        items.setAdapter(itemAdapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Query check = forInterest.orderByChild("studid").equalTo(studid);
                    check.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if (snapshot2.exists()) {

                                if (Objects.equals(snapshot2.child(studid).child("beautyproducts").getValue(String.class),
                                        "Beauty Products")) {
                                    beautyproducts = snapshot2.child(studid).child("beautyproducts").getValue(String.class);
                                    cat_interests.add("Beauty Products");
                                }

                                if (Objects.equals(snapshot2.child(studid).child("clothesaccessories").getValue(String.class),
                                        "Clothes and Accessories")) {
                                    clothesaccessories = snapshot2.child(studid).child("clothesaccessories").getValue(String.class);
                                    cat_interests.add("Clothes and Accessories");
                                }

                                if (Objects.equals(snapshot2.child(studid).child("electronics").getValue(String.class),
                                        "Electronics")) {
                                    electronics = snapshot2.child(studid).child("electronics").getValue(String.class);
                                    cat_interests.add("Electronics");
                                }

                                if (Objects.equals(snapshot2.child(studid).child("foodbeverages").getValue(String.class),
                                        "Food and Beverages")) {
                                    foodbeverages = snapshot2.child(studid).child("foodbeverages").getValue(String.class);
                                    cat_interests.add("Food and Beverages");
                                }

                                if (Objects.equals(snapshot2.child(studid).child("schoolsupplies").getValue(String.class),
                                        "School Supplies")) {
                                    schoolsupplies = snapshot2.child(studid).child("schoolsupplies").getValue(String.class);
                                    cat_interests.add("School Supplies");
                                }

                                if (Objects.equals(snapshot2.child(studid).child("sportsequipment").getValue(String.class),
                                        "Sports Equipment")) {
                                    sportsequipment = snapshot2.child(studid).child("sportsequipment").getValue(String.class);
                                    cat_interests.add("Sports Equipment");
                                }

                                for(String x :cat_interests){
                                    Log.d("EWAN ", x);
                                }

                                Log.d("categories", "beautyproducts: " + beautyproducts + "\n"
                                        + "clothesaccessories: " + clothesaccessories + "\n" + "electronics: " + electronics + "\n"
                                        + "foodbeverages: " + foodbeverages + "\n" + "schoolsupplies: " + schoolsupplies + "\n"
                                        + "sportsequipment: " + sportsequipment
                                );

                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    ItemHelperClass itemHelperClass = snap.getValue(ItemHelperClass.class);
                                    for (String x : cat_interests){
                                        if (itemHelperClass != null && itemHelperClass.getpCategory().equals(x) ){
                                            if(!itemHelperClass.getpSellerID().equals(studid)){
                                                listItem.add(itemHelperClass);
                                            }

                                        }
                                    }

                                }
                                itemAdapter.notifyDataSetChanged();
                                itemLoadListener.onItemLoadSuccess(listItem);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Error: ", error.getDetails());}
                    });

                } else {
                    itemLoadListener.onItemLoadFailed("Can't find product");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });

        btncart.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddToCart.class)));
        search.setOnClickListener(v -> startActivity(new Intent(getActivity(), Search.class)));

        return view;
    }

    private void categoryRecycler() {
        catlist.add(new CategoryHelperClass("School Supplies"));
        catlist.add(new CategoryHelperClass("Electronics"));
        catlist.add(new CategoryHelperClass("Foods and Beverages"));
        catlist.add(new CategoryHelperClass("Clothes and Accessories"));
        catlist.add(new CategoryHelperClass("Beauty Products"));
        catlist.add(new CategoryHelperClass("Sports Equipment"));
    }

    @Override
    public void onItemLoadSuccess(List<ItemHelperClass> itemList) {}

    @Override
    public void onItemLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(ArrayList<CartHelperClass> cartItemList) {
        int cartSum = 0;
        for (CartHelperClass cartHelperClass : cartItemList) {
            cartSum += Integer.parseInt(cartHelperClass.getProdQty());
            badge.setNumber(cartSum);
        }
    }

    @Override
    public void onCartLoadFailed(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onNotifLoadSuccess(ArrayList<NotifModel> notifList) {
        int NotifSum = 0;
        for (NotifModel notifModel : notifList) {
            int count = 0;
            if (notifModel.getNotification()!=null){
                count++;
            }
            NotifSum += count;
            notificationBadge.setNumber(NotifSum);
        }
    }

    @Override
    public void onNotifLoadFailed(String Message) {}

    @Override
    public void onResume() {
        super.onResume();
        countCartItem();
        countNotif();
    }

    private void countNotif(){
        ArrayList<NotifModel> notifList = new ArrayList<>();

        SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("notification").child(studid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    NotifModel notifModel = dataSnapshot.getValue(NotifModel.class);
                                    notifList.add(notifModel);
                            }
                            notifItemLoadListener.onNotifLoadSuccess(notifList);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("", error.getDetails());
                    }
                });

    }

    private void countCartItem() {
        ArrayList<CartHelperClass> cartHelperClasses = new ArrayList<>();

        SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_REMEMBERME);
        HashMap<String, String> usersdetails = sessionManager.getRememberMeDetailsFromSession();

        SessionManager sessionManager2 = new SessionManager(getActivity(), SessionManager.SESSION_USERSESSION);
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


}