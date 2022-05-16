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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.Model;
import bbc.umarket.umarketapp2.Helper.SellerHelperClass;
import bbc.umarket.umarketapp2.R;

public class SellerRegistration extends AppCompatActivity {

    TextInputLayout sname, semail, scontactnum;
    Button register, upload;
    ImageView back, sellerimg;
    String studid;
    String sellerKey;
    String sellername, selleremail, sellercontact;


    //for seller profile image upload and creating uri
    DatabaseReference sellerroot = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("seller");
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://umarketapp2-58178.appspot.com");
    StorageReference storageref, imageref;
    private Uri imageUri;
    SellerHelperClass sellerHelperClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sellerreg);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        storageref = storage.getReference();

        sname = findViewById(R.id.sellerreg_name);
        semail = findViewById(R.id.sellerreg_email);
        scontactnum = findViewById(R.id.sellerreg_contact);
        register = findViewById(R.id.sellerreg_button);
        back = findViewById(R.id.sellerreg_back);
        sellerimg = findViewById(R.id.sellerreg_imgview);
        upload = findViewById(R.id.sellerreg_upload);


        back.setOnClickListener(view -> {
            Intent intent = new Intent(SellerRegistration.this, HomeContainer.class);
            intent.putExtra("back_Acc", "Account");
            startActivity(intent);
            finish();
        });

        upload.setOnClickListener(view -> {
            Intent photopicker = new Intent(Intent.ACTION_PICK);
            photopicker.setType("image/*");
            startActivityForResult(photopicker, 2);
        });


        register.setOnClickListener(view -> {
            if (imageUri != null) {
                Add_OnSellerDB();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                builder.setTitle("");
                builder.setMessage("Please select an image");

                // add a button
                builder.setNeutralButton("OK", null);

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

    }

    private Boolean validateSName() {
        String val = Objects.requireNonNull(sname.getEditText()).getText().toString();

        if (val.isEmpty()) {
            sname.setError("Field cannot be empty");
            return false;
        } else {
            sname.setError(null);
            sname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateSEmail() {
        String val = Objects.requireNonNull(semail.getEditText()).getText().toString();

        if (val.isEmpty()) {
            semail.setError("Field cannot be empty");
            return false;
        } else {
            semail.setError(null);
            semail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateSContact() {
        String val = Objects.requireNonNull(scontactnum.getEditText()).getText().toString();
        if (val.isEmpty()) {
            scontactnum.setError("Field cannot be empty");
            return false;
        } else {
            scontactnum.setError(null);
            scontactnum.setErrorEnabled(false);
            return true;
        }
    }



    private void Add_OnSellerDB() {
        if (!validateSName() | !validateSEmail() | !validateSContact() ) {
            return;
        }

        sellername = Objects.requireNonNull(sname.getEditText()).getText().toString();
        selleremail = Objects.requireNonNull(semail.getEditText()).getText().toString();
        sellercontact = Objects.requireNonNull(scontactnum.getEditText()).getText().toString();
        UploadImage();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            sellerimg.setImageURI(imageUri);
        }
    }

    private void UploadImage() {
        sellerKey = sellerroot.push().getKey();
        imageref = storageref.child("seller_imgprof/" + sellerKey + "." + getFileExtension(imageUri));

        imageref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {

                    imageref.getDownloadUrl()
                            .addOnSuccessListener(uri -> {

                                sellerHelperClass = new SellerHelperClass(sellerKey, uri.toString(), studid, sellername, selleremail, sellercontact);
                                sellerroot.child(sellerKey).setValue(sellerHelperClass);

                                Intent intent = new Intent(SellerRegistration.this, SellerCenter.class);
                                startActivity(intent);
                                finish();
                            });
                    Toast.makeText(SellerRegistration.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(SellerRegistration.this, "Upload Failed", Toast.LENGTH_SHORT).show());
    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));

    }

}