package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.ItemAdapter;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.ItemHelperClass;
import bbc.umarket.umarketapp2.R;

public class CategorizedListing extends AppCompatActivity {
    ImageView back;
    TextView cattext;
    ItemAdapter itemAdapter;
    ArrayList<ItemHelperClass> listItem;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    RecyclerView items;

    String studid;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_categorized_listing);

        Intent catintent = getIntent();
        String catname = catintent.getExtras().getString("pcat");

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rootNode.getReference("products");

        //hooks
        back = findViewById(R.id.editprof_back);
        cattext = findViewById(R.id.cattext);
        items = findViewById(R.id.rvcategorized);

        cattext.setText(catname);

        items.setHasFixedSize(true);
        items.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        listItem = new ArrayList<>();
        itemAdapter = new ItemAdapter(this, listItem);
        items.setAdapter(itemAdapter);

        reference = rootNode.getReference("products");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    ItemHelperClass itemHelperClass = snap.getValue(ItemHelperClass.class);
                    if (itemHelperClass != null && itemHelperClass.getpCategory().equals(catname) && itemHelperClass.getpSellerID() != studid) {
                        listItem.add(itemHelperClass);
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });


        back.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeContainer.class);
            intent.putExtra("back_Home", "Dashboard");
            startActivity(intent);
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