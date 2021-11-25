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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.CategoryAdapter;
import bbc.umarket.umarketapp2.Adapter.CategoryHelperClass;
import bbc.umarket.umarketapp2.Adapter.FeatProdAdapter;
import bbc.umarket.umarketapp2.Adapter.FeatProdtHelperClass;
import bbc.umarket.umarketapp2.Adapter.InterestAdapter;
import bbc.umarket.umarketapp2.Adapter.InterestHelperClass;
import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Adapter.ItemHelperClass;
import bbc.umarket.umarketapp2.CategorizedListing;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.SearchedListing;


public class FragHome extends Fragment {
    Context context;
    TextInputLayout search;
    RecyclerView category, items, features;
    CategoryAdapter categoryAdapter;
    FeatProdAdapter featProdAdapter;
    ItemAdapter itemAdapter;
    ArrayList<CategoryHelperClass> catlist;
    ArrayList<FeatProdtHelperClass> featprod;
    ArrayList<ItemHelperClass> listItem;
    ImageView searchbutton;
    String searchprod, studid;


    //for database
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    public FragHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
        context = view.getContext();

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");

        //for image slider on dashboard fragment
        ImageSlider imageSlider = view.findViewById(R.id.dashSlider);
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.slider_01, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slider_03, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        //studid
        SessionManager sessionManager = new SessionManager(getActivity() );
        HashMap<String,String> usersdetails =  sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        //hooks
        category = view.findViewById(R.id.category_recyclerview);
        items = view.findViewById(R.id.item_RecyclerView);
        features = view.findViewById(R.id.featured_RecyclerView);
        search = view.findViewById(R.id.tilSearch);
        searchbutton = view.findViewById(R.id.btnsearch);

        reference = rootNode.getReference("products");

        searchprod = Objects.requireNonNull(search.getEditText()).getText().toString();
        searchbutton.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, SearchedListing.class);
            intent.putExtra("searched", searchprod);
            context.startActivity(intent);
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
                for (String strTemp : rec){
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
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
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

                for (DataSnapshot snap : snapshot.getChildren()) {
                    ItemHelperClass itemHelperClass = snap.getValue(ItemHelperClass.class);
                    listItem.add(itemHelperClass);
                }
                itemAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

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

}