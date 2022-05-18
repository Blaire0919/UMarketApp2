package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Helper.Model;
import bbc.umarket.umarketapp2.Helper.RateReviewHelperClass;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.SellerSide.AddListing;

public class RateAndReview extends AppCompatActivity {
    ImageView p_img, back, selectedphoto;
    TextView p_name, submit;
    TextInputLayout review;
    RatingBar rate;
    MaterialCardView addphoto, card;

    long maxid = 0;

    String studid, fname, lname, buyerName, rrID, pId, p_date, pUrl, ratenum, rrreview, prodname;

    DatabaseReference reference;
    DatabaseReference refRR = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("rateandreview");

    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RateAndReview.this, R.style.AlertDialogCustom));

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://umarketapp2-58178.appspot.com");
    StorageReference storageref, imageref;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ratenreview);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);
        fname = usersdetails.get(SessionManager.KEY_FNAME);
        lname = usersdetails.get(SessionManager.KEY_LNAME);
        buyerName = fname + " " + lname;

        Intent rrintent = getIntent();
        pId = rrintent.getExtras().getString("pID");
        pUrl = rrintent.getExtras().getString("imageUrl");
        prodname = rrintent.getExtras().getString("pname");
        p_date = java.text.DateFormat.getDateTimeInstance().format(new Date());

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
                    Glide.with(RateAndReview.this)
                            .load(pUrl)
                            .into(p_img);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                builder.setTitle("");
                builder.setMessage("Please select an Image.");
                // add a button
                builder.setNeutralButton("OK", null);
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
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
                            refRR.child(rrID).setValue(rateReviewHelperClass);

                            AlertDialog dialog = builder.setTitle("")
                                    .setMessage("Successfully reviewed the product!")
                                    .create();
                            dialog.show();

                            Intent intent = new Intent(RateAndReview.this, HomeContainer.class);
                            intent.putExtra("back_Home", "Home");
                            startActivity(intent);
                            finish();
                        });
                        Toast.makeText(RateAndReview.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Toast.makeText(RateAndReview.this, "Upload Failed", Toast.LENGTH_SHORT).show());

                } else {
                    maxid = 1;
                    rrID = (maxid + "_key");
                    imageref = storageref.child("rate_review/" + rrID + "." + getFileExtension(imageUri));
                    RateReviewHelperClass rateReviewHelperClass = new RateReviewHelperClass(imageUri.toString(), rrID, pId, studid, buyerName, ratenum, rrreview, p_date);
                    refRR.child(rrID).setValue(rateReviewHelperClass);

                    imageref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                        imageref.getDownloadUrl().addOnSuccessListener(uri -> {
                            RateReviewHelperClass rateReviewHelperClass2 = new RateReviewHelperClass(uri.toString(), rrID, pId, studid, buyerName, ratenum, rrreview, p_date);
                            refRR.child(rrID).setValue(rateReviewHelperClass2);

                            AlertDialog dialog = builder.setTitle("")
                                    .setMessage("Successfully reviewed the product!")
                                    .create();
                            dialog.show();

                            Intent intent = new Intent(RateAndReview.this, HomeContainer.class);
                            intent.putExtra("back_Home", "Home");
                            startActivity(intent);
                            finish();
                        });
                        Toast.makeText(RateAndReview.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Toast.makeText(RateAndReview.this, "Upload Failed", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            card.setVisibility(View.VISIBLE);
            selectedphoto.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

}