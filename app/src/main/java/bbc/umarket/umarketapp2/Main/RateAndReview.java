package bbc.umarket.umarketapp2.Main;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Helper.RateReviewHelperClass;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.SellerSide.ProcessOrder;

public class RateAndReview extends AppCompatActivity {
    ImageView p_img, back, selectedphoto;
    TextView p_name;
    TextInputLayout review;
    RatingBar rate;
    MaterialCardView addphoto, card;
    Button submit;

    long maxid = 0;
    public String studid, fname, lname, buyerName, rrID, pId, p_date, pUrl, ratenum, rrreview, prodname;

    DatabaseReference reference;
    DatabaseReference refRR = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("rateandreview");

    public AlertDialog.Builder builder;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://umarketapp2-58178.appspot.com");
    StorageReference storageref, imageref;
    private Uri imageUri;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    //for purchase history database
  String removeToRateKey, ImgUrl, buyerID, prodID, sellerID, sellerName, prodName, prodQty, prodPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_ratenreview);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        builder = new AlertDialog.Builder(new ContextThemeWrapper(RateAndReview.this, R.style.CustomAlertDialog));

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);
        fname = usersdetails.get(SessionManager.KEY_FNAME);
        lname = usersdetails.get(SessionManager.KEY_LNAME);
        buyerName = fname + " " + lname;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent rrintent = getIntent();
        pId = rrintent.getExtras().getString("pID");
        pUrl = rrintent.getExtras().getString("imageUrl");
        prodname = rrintent.getExtras().getString("pname");
        p_date = java.text.DateFormat.getDateTimeInstance().format(new Date());

        //getting the values from intent
        Intent phistoryintent = getIntent();
        ImgUrl = phistoryintent.getExtras().getString("imgurl");
        buyerID = phistoryintent.getExtras().getString("buyerid");
        prodID = phistoryintent.getExtras().getString("prodid");
        sellerID = phistoryintent.getExtras().getString("sellerid");
        sellerName = phistoryintent.getExtras().getString("sellername");
        prodName = phistoryintent.getExtras().getString("prodname");
        prodQty = phistoryintent.getExtras().getString("prodqty");
        prodPrice = phistoryintent.getExtras().getString("prodprice");
        storageref = storage.getReference();

        //hooks
        p_img = findViewById(R.id.p_image);
        back = findViewById(R.id.rr_back);
        p_name = findViewById(R.id.p_name);
        submit = findViewById(R.id.rr_submit);
        review = findViewById(R.id.rr_TILreview);
        rate = findViewById(R.id.rrinputratebar);
        card = findViewById(R.id.rrforimgview);
        addphoto = findViewById(R.id.rr_addphoto);
        selectedphoto = findViewById(R.id.rr_selectedphoto);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(RateAndReview.this, HomeContainer.class);
            intent.putExtra("back_Home", "Home");
            startActivity(intent);
            finish();
        });

        reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("products");
        Query checkID = reference.orderByChild("pID").equalTo(pId);
        checkID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Glide.with(RateAndReview.this).load(pUrl).into(p_img);
                    p_name.setText(prodname);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        addphoto.setOnClickListener(view -> {
            Intent photopicker = new Intent(Intent.ACTION_PICK);
            photopicker.setType("image/*");
            startActivityForResult(photopicker, 2);
        });

        submit.setOnClickListener(view -> {
            if (imageUri != null) {
                AddOnRateReviewDB();
            } else {
                AlertDialog dialog = builder.setTitle("").setMessage("Please select an Image.").setNeutralButton("OK", null).create();
                dialog.show();
            }
        });
    }

    private void AddOnRateReviewDB() {
        ratenum = String.valueOf(rate.getRating());
        rrreview = Objects.requireNonNull(review.getEditText()).getText().toString();

        refRR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    maxid = (snapshot.getChildrenCount());
                    rrID = maxid + 1 + "_key";
                    imageref = storageref.child("rate_review/" + rrID + "." + getFileExtension(imageUri));
                    imageref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        imageref.getDownloadUrl().addOnSuccessListener(uri -> {
                            RateReviewHelperClass rateReviewHelperClass = new RateReviewHelperClass(uri.toString(), rrID, pId, studid, buyerName, ratenum, rrreview, p_date);
                            refRR.child(rrID).setValue(rateReviewHelperClass).addOnSuccessListener(unused -> {
                                Log.d(TAG, "Inserted on rate and review db");

                                ToReceiveModel pHistory = new ToReceiveModel(ImgUrl, buyerID, prodID, sellerID, sellerName, prodName, prodQty, prodPrice);
                                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("purchase_history")
                                        .child(buyerID)
                                        .push()
                                        .setValue(pHistory).addOnSuccessListener(awit -> {
                                            Toast.makeText(RateAndReview.this, "Moved to purchase history", Toast.LENGTH_SHORT).show();

                                            //removing item in to rate db
                                             FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                    .getReference("to_rate").child(buyerID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                    if (snapshot1.exists()) {
                                                        for (DataSnapshot dataSnapshot : snapshot1.getChildren()) {
                                                            ToReceiveModel phlist = dataSnapshot.getValue(ToReceiveModel.class);
                                                            if (phlist != null) {
                                                           //     removeToRateKey = snapshot1.getKey();

                                                                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                        .getReference("to_rate")
                                                                        .child(buyerID)
                                                                        //.child(removeToRateKey)
                                                                        .removeValue()
                                                                        .addOnSuccessListener(unused1 -> {

                                                                            Toast.makeText(RateAndReview.this, "Removed from to receive with Key " + removeToRateKey, Toast.LENGTH_LONG).show();
                                                                            Intent intent = new Intent(RateAndReview.this, PurchaseHistory.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        });
                                                            }
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {}
                                            });

                                        });
                            });
                        });
                        Toast.makeText(RateAndReview.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Toast.makeText(RateAndReview.this, "Upload Failed", Toast.LENGTH_SHORT).show());

                } else {
                    maxid = 1;
                    rrID = (maxid + "_key");
                    imageref = storageref.child("rate_review/" + rrID + "." + getFileExtension(imageUri));
                    imageref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        imageref.getDownloadUrl().addOnSuccessListener(uri -> {
                            RateReviewHelperClass rateReviewHelperClass2 = new RateReviewHelperClass(uri.toString(), rrID, pId, studid, buyerName, ratenum, rrreview, p_date);
                            refRR.child(rrID).setValue(rateReviewHelperClass2).addOnSuccessListener(unused -> {
                                Log.d(TAG, "Inserted on rate and review db");

                                ToReceiveModel pHistory = new ToReceiveModel(ImgUrl, buyerID, prodID, sellerID, sellerName, prodName, prodQty, prodPrice);
                                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("purchase_history")
                                        .child(buyerID)
                                        .push()
                                        .setValue(pHistory).addOnSuccessListener(awit -> {
                                            Toast.makeText(RateAndReview.this, "Moved to purchase history", Toast.LENGTH_SHORT).show();

                                            //removing item in to rate db
                                            DatabaseReference buyerRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                    .getReference("to_rate").child(buyerID);

                                            buyerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                    if (snapshot1.exists()) {
                                                        for (DataSnapshot dataSnapshot : snapshot1.getChildren()) {
                                                            ToReceiveModel phlist = dataSnapshot.getValue(ToReceiveModel.class);
                                                            if (phlist != null && phlist.getProdName().equals(prodName)) {
                                                                removeToRateKey = snapshot1.getKey();
                                                                Log.d("TO_RATE KEY", removeToRateKey);
                                                                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                                        .getReference("to_rate")
                                                                        .child(buyerID)
                                                                        .child(removeToRateKey)
                                                                        .removeValue()
                                                                        .addOnSuccessListener(unused1 -> {
                                                                            Toast.makeText(RateAndReview.this, "Removed from to receive with Key " + removeToRateKey, Toast.LENGTH_LONG).show();
                                                                            Intent intent = new Intent(RateAndReview.this, PurchaseHistory.class);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        });
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });


                                        });
                            });

                        });
                        Toast.makeText(RateAndReview.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Toast.makeText(RateAndReview.this, "Upload Failed", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            selectedphoto.setImageURI(imageUri);
            selectedphoto.setVisibility(View.VISIBLE);
        }
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
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