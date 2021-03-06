package bbc.umarket.umarketapp2.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import bbc.umarket.umarketapp2.Listener.CartItemLoadListener;
import bbc.umarket.umarketapp2.Listener.ItemLoadListener;
import bbc.umarket.umarketapp2.Main.AddListing;
import bbc.umarket.umarketapp2.Main.AddToCart;
import bbc.umarket.umarketapp2.Main.EditProfile;
import bbc.umarket.umarketapp2.Main.HomeContainer;
import bbc.umarket.umarketapp2.Main.Login;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.Main.SearchedListing;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FragHome extends Fragment implements ItemLoadListener, CartItemLoadListener {
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
    DatabaseReference reference;

    public FragHome() { }

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

        reference = rootNode.getReference("products");

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
                                    featprod.add(featProdtHelperClass);
                                }
                            }
                            featProdAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {  }
        });

        //for product listing
        items.setHasFixedSize(true);
        items.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        listItem = new ArrayList<>();
        itemAdapter = new ItemAdapter(context, listItem);
        items.setAdapter(itemAdapter);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        ItemHelperClass itemHelperClass = snap.getValue(ItemHelperClass.class);
                        listItem.add(itemHelperClass);
                    }
                    itemAdapter.notifyDataSetChanged();
                    itemLoadListener.onItemLoadSuccess(listItem);
                } else {
                    itemLoadListener.onItemLoadFailed("Can't find product");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        btncart.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddToCart.class)));
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
    public void onItemLoadSuccess(List<ItemHelperClass> itemList) {
    }

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
    public void onResume() {
        super.onResume();
        countCartItem();

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