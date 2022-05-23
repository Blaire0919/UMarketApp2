package bbc.umarket.umarketapp2.SellerSide;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import bbc.umarket.umarketapp2.Helper.ItemModel;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Main.HomeContainer;
import bbc.umarket.umarketapp2.R;

public class AddListing extends AppCompatActivity {
    ImageView back, selectedimg;
    MaterialCardView save;
    MaterialCardView mcvaddphoto, forimg;
    TextInputLayout pname, pdesc, pbrand, pcat, psubcat, pprice, pstock, pcondition;
    AutoCompleteTextView autocat, autosubcat, autocondition;
    List<String> ListCat, ListSubCat, ListCondition;
    ArrayAdapter<String> arrayAdapter_Cat, arrayAdapter_SubCat, arrayAdapter_Condition;
    String studid;

    //for database
    DatabaseReference root = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("products");
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://umarketapp2-58178.appspot.com");
    StorageReference storageref, imageref;
    private Uri imageUri;
    String pBrand, pCat, pSubCat, pCondition, pDescription, pName, pPrice, pStock, pSellerID, pSold, pOverAllrate, pScore, pID;
    ItemModel itemModel;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_add_listing);

        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        storageref = storage.getReference();

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);
        pSellerID = usersdetails.get(SessionManager.KEY_STUDID);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //hooks
        back = findViewById(R.id.settings_back);
        mcvaddphoto = findViewById(R.id.addphoto);
        selectedimg = findViewById(R.id.selectedphoto);
        forimg = findViewById(R.id.forimgview);
        save = findViewById(R.id.save);

        pname = findViewById(R.id.InputPName);
        pdesc = findViewById(R.id.InputPDesc);
        pbrand = findViewById(R.id.InputPBrand);
        pprice = findViewById(R.id.InputPrice);
        pstock = findViewById(R.id.InputPStock);
        pcat = findViewById(R.id.InputPCategory);
        psubcat = findViewById(R.id.InputPSubCategory);
        pcondition = findViewById(R.id.InputPCondition);

        autocat = findViewById(R.id.acCat);
        autosubcat = findViewById(R.id.acSubCat);
        autocondition = findViewById(R.id.acCondition);

        ListCat = new ArrayList<>();
        ListSubCat = new ArrayList<>();
        ListCondition = new ArrayList<>();

        arrayAdapter_Cat = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, ListCat);
        arrayAdapter_SubCat = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, ListSubCat);
        arrayAdapter_Condition = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, ListCondition);

        ListCat.add("Foods and Beverages");
        ListCat.add("School Supplies");
        ListCat.add("Electronics");
        ListCat.add("Clothes and Accessories");
        ListCat.add("Beauty Products");
        ListCat.add("Sports Equipment");
        autocat.setAdapter(arrayAdapter_Cat);
        autocat.setThreshold(1);


        autocat.setOnItemClickListener((parent, view, position, id) -> {
            autosubcat.clearListSelection();
            arrayAdapter_SubCat.clear();
            autocondition.clearListSelection();
            arrayAdapter_Condition.clear();
            //   psubcat.getEditText().setText("");

            switch (arrayAdapter_Cat.getItem(position)) {
                case "Foods and Beverages":
                    ListSubCat.add("Instant Foods");
                    ListSubCat.add("Candies");
                    ListSubCat.add("Delicacies");
                    ListSubCat.add("Canned Goods");
                    ListSubCat.add("Frozen and Fresh");
                    ListSubCat.add("Beverages");
                    ListSubCat.add("Snacks");
                    autosubcat.setAdapter(arrayAdapter_SubCat);
                    autosubcat.setThreshold(1);

                    ListCondition.add("Fresh");
                    ListCondition.add("Processed");
                    autocondition.setAdapter(arrayAdapter_Condition);
                    autocondition.setThreshold(1);
                    break;

                case "School Supplies":

                    ListSubCat.add("Art Supplies");
                    ListSubCat.add("Writing Materials");
                    ListSubCat.add("Calculator");
                    ListSubCat.add("Sticky Notes");
                    ListSubCat.add("Organizers");
                    ListSubCat.add("Adhesive and Clips");
                    autosubcat.setAdapter(arrayAdapter_SubCat);
                    autosubcat.setThreshold(1);

                    ListCondition.add("Brand New");
                    ListCondition.add("Used");
                    autocondition.setAdapter(arrayAdapter_Condition);
                    autocondition.setThreshold(1);
                    break;

                case "Electronics":
                    ListSubCat.add("Chargers");
                    ListSubCat.add("Devices");
                    ListSubCat.add("Earphones");
                    ListSubCat.add("Adhesive Wall Hooks");
                    ListSubCat.add("Tools");
                    ListSubCat.add("Cables");
                    ListSubCat.add("Keyboards");
                    ListSubCat.add("USB Hubs");
                    autosubcat.setAdapter(arrayAdapter_SubCat);
                    autosubcat.setThreshold(1);

                    ListCondition.add("Brand New");
                    ListCondition.add("Used");
                    autocondition.setAdapter(arrayAdapter_Condition);
                    autocondition.setThreshold(1);
                    break;

                case "Clothes and Accessories":
                    ListSubCat.add("Sweatshirts");
                    ListSubCat.add("Hats");
                    ListSubCat.add("Rings");
                    ListSubCat.add("Necklace");
                    ListSubCat.add("Shoes");
                    ListSubCat.add("Socks");
                    ListSubCat.add("Sandals");
                    ListSubCat.add("Pajamas");
                    ListSubCat.add("Tops");
                    ListSubCat.add("Shorts");
                    ListSubCat.add("Underwear");
                    ListSubCat.add("Coat");
                    ListSubCat.add("Dress");
                    ListSubCat.add("Accessories");
                    ListSubCat.add("Pants");
                    autosubcat.setAdapter(arrayAdapter_SubCat);
                    autosubcat.setThreshold(1);

                    ListCondition.add("Brand New");
                    ListCondition.add("Used");
                    autocondition.setAdapter(arrayAdapter_Condition);
                    autocondition.setThreshold(1);
                    break;

                case "Beauty Products":

                    ListSubCat.add("Skincare");
                    ListSubCat.add("Cologne/Perfume");
                    ListSubCat.add("Cosmetics");
                    ListSubCat.add("Cleanser and Scrubs");
                    ListSubCat.add("Bath and Body");
                    ListSubCat.add("Beauty Supplements");
                    autosubcat.setAdapter(arrayAdapter_SubCat);
                    autosubcat.setThreshold(1);

                    ListCondition.add("Brand New");
                    ListCondition.add("Used");
                    autocondition.setAdapter(arrayAdapter_Condition);
                    autocondition.setThreshold(1);
                    break;

                case "Sports Equipment":
                    ListSubCat.add("Table Tennis");
                    ListSubCat.add("Support");
                    ListSubCat.add("Exercise and Fitness");
                    ListSubCat.add("Tennis");
                    ListSubCat.add("Protection");
                    ListSubCat.add("FootBall");
                    ListSubCat.add("BasketBall");
                    ListSubCat.add("VolleyBall");
                    ListSubCat.add("Soccer");
                    ListSubCat.add("Swimsuit");
                    ListSubCat.add("Badminton");
                    ListSubCat.add("Jumping Rope");
                    ListSubCat.add("Swimming");
                    autosubcat.setAdapter(arrayAdapter_SubCat);
                    autosubcat.setThreshold(1);

                    ListCondition.add("Brand New");
                    ListCondition.add("Used");
                    autocondition.setAdapter(arrayAdapter_Condition);
                    autocondition.setThreshold(1);
                    break;
                default:
            }

        });

        mcvaddphoto.setOnClickListener(view -> {
            Intent photopicker = new Intent(Intent.ACTION_PICK);
            photopicker.setType("image/*");
            startActivityForResult(photopicker, 2);
        });

        back.setOnClickListener(view -> {
            Intent intent = new Intent(AddListing.this, SellerCenter.class);
            startActivity(intent);
            finish();
        });

        save.setOnClickListener(v -> {
            if (imageUri != null) {
                Add_ProdOnDB();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddListing.this, R.style.CustomAlertDialog));
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

    private Boolean validatePName() {
        String val = Objects.requireNonNull(pname.getEditText()).getText().toString();

        if (val.isEmpty()) {
            pname.setError("Field cannot be empty");
            return false;
        } else {
            pname.setError(null);
            pname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePDesc() {
        String val = Objects.requireNonNull(pdesc.getEditText()).getText().toString();

        if (val.isEmpty()) {
            pdesc.setError("Field cannot be empty");
            return false;
        } else {
            pdesc.setError(null);
            pdesc.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePBrand() {
        String val = Objects.requireNonNull(pbrand.getEditText()).getText().toString();

        if (val.isEmpty()) {
            pbrand.setError("Field cannot be empty");
            return false;
        } else {
            pbrand.setError(null);
            pbrand.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePPrice() {
        String val = Objects.requireNonNull(pprice.getEditText()).getText().toString();

        if (val.isEmpty()) {
            pprice.setError("Field cannot be empty");
            return false;
        } else {
            pprice.setError(null);
            pprice.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePStock() {
        String val = Objects.requireNonNull(pstock.getEditText()).getText().toString();

        if (val.isEmpty()) {
            pstock.setError("Field cannot be empty");
            return false;
        } else {
            pstock.setError(null);
            pstock.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePCat() {
        String val = Objects.requireNonNull(pcat.getEditText()).getText().toString();

        if (val.isEmpty()) {
            pcat.setError("Field cannot be empty");
            return false;
        } else {
            pcat.setError(null);
            pcat.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePSubCat() {
        String val = Objects.requireNonNull(psubcat.getEditText()).getText().toString();

        if (val.isEmpty()) {
            psubcat.setError("Field cannot be empty");
            return false;
        } else {
            psubcat.setError(null);
            psubcat.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePCondtion() {
        String val = Objects.requireNonNull(pcondition.getEditText()).getText().toString();

        if (val.isEmpty()) {
            pcondition.setError("Field cannot be empty");
            return false;
        } else {
            pcondition.setError(null);
            pcondition.setErrorEnabled(false);
            return true;
        }
    }


    private void Add_ProdOnDB() {
        if (!validatePBrand() | !validatePCat() | !validatePSubCat() | !validatePCondtion() |
                !validatePDesc() | !validatePName() | !validatePPrice() |
                !validatePStock()) {
            return;
        }
        pBrand = Objects.requireNonNull(pbrand.getEditText()).getText().toString();
        pCat = Objects.requireNonNull(pcat.getEditText()).getText().toString();
        pSubCat = Objects.requireNonNull(psubcat.getEditText()).getText().toString();
        pCondition = Objects.requireNonNull(pcondition.getEditText()).getText().toString();
        pDescription = Objects.requireNonNull(pdesc.getEditText()).getText().toString();
        pName = Objects.requireNonNull(pname.getEditText()).getText().toString();
        pPrice = Objects.requireNonNull(pprice.getEditText()).getText().toString();
        pStock = Objects.requireNonNull(pstock.getEditText()).getText().toString();
        pSellerID = studid;
        pOverAllrate = "0.00";
        pSold = "0";
        pScore = "0.0";
        pID = root.push().getKey();
        UploadImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            forimg.setVisibility(View.VISIBLE);
            selectedimg.setImageURI(imageUri);
            selectedimg.setVisibility(View.VISIBLE);
        }
    }

    private void UploadImage() {
        imageref = storageref.child("product_image/" + pID + "." + getFileExtension(imageUri));

        imageref.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageref.getDownloadUrl().addOnSuccessListener(uri -> {
                itemModel = new ItemModel(uri.toString(), pID, pBrand, pCat, pSubCat, pCondition, pDescription, pName, pPrice, pStock, pSellerID, pOverAllrate, pSold, pScore);
                root.child(pID).setValue(itemModel);
                Intent intent = new Intent(AddListing.this, SellerCenter.class);
                startActivity(intent);
                finish();
            });
            Toast.makeText(AddListing.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(AddListing.this, "Upload Failed", Toast.LENGTH_SHORT).show());
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