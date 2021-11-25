package bbc.umarket.umarketapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Adapter.RateReviewHelperClass;
import bbc.umarket.umarketapp2.Database.SessionManager;

public class RateAndReview extends AppCompatActivity {
    ImageView p_img, back;
    TextView p_name, submit;
    TextInputLayout review;
    RatingBar rate;
    long maxid = 0;

    String studid,fname, lname, buyerName, rrID, pId, p_date, pUrl, ratenum, rrreview;

    DatabaseReference reference, refRR;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ratenreview);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);
        fname = usersdetails.get(SessionManager.KEY_FNAME);
        lname = usersdetails.get(SessionManager.KEY_LNAME);
        buyerName = fname + " "+ lname;


        //for database
        Bundle bundle = getIntent().getExtras();
        pId = bundle.getString("pID");
        p_date = bundle.getString("datetime");
        pUrl = bundle.getString("pImg");

        //hooks
        p_img = findViewById(R.id.p_image);
        back = findViewById(R.id.rr_back);
        p_name = findViewById(R.id.p_name);
        submit = findViewById(R.id.rr_submit);
        review = findViewById(R.id.rr_TILreview);
        rate = findViewById(R.id.rrinputratebar);

        reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("products");
        Query checkID = reference.orderByChild("pID").equalTo(pId);
        checkID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Glide.with(RateAndReview.this)
                            .load(snapshot.child(pId).child("imageUrl").getValue(String.class))
                            .into(p_img);
                    p_name.setText(bundle.getString("pName"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        refRR = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("rateandreview");

        submit.setOnClickListener(view -> {

            ratenum = String.valueOf(rate.getRating());
            rrreview = Objects.requireNonNull(review.getEditText()).getText().toString();

            refRR.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        maxid = (snapshot.getChildrenCount());
                        rrID = maxid + 1 + "_key";
                        RateReviewHelperClass rateReviewHelperClass = new RateReviewHelperClass(rrID, pId, studid, buyerName, ratenum, rrreview, p_date);
                        refRR.child(rrID).setValue(rateReviewHelperClass);
                    }else {
                        maxid = 1;
                        rrID = (maxid+"_key");
                        RateReviewHelperClass rateReviewHelperClass = new RateReviewHelperClass(rrID, pId, studid, buyerName, ratenum, rrreview, p_date);
                        refRR.child(rrID).setValue(rateReviewHelperClass);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {  }
            });

            Intent intent = new Intent(RateAndReview.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
        });

        back.setOnClickListener(view -> {
            Intent intent = new Intent(RateAndReview.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
        });

    }
}