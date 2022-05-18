package bbc.umarket.umarketapp2.Main;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextThemeWrapper;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import bbc.umarket.umarketapp2.Helper.InterestHelperClass;
import bbc.umarket.umarketapp2.Helper.UserHelperClass;
import bbc.umarket.umarketapp2.R;

public class Register extends AppCompatActivity {
    ImageView back, checkVerified;
    TextView backlogin;
    public TextInputLayout fname, lname, studid, contact, email, cpass;
    Button register;
    MaterialCardView sendCode;
    CheckBox chkSS, chkE, chkFB, chkCA, chkBP, chkSE, chkOthers;
    LinearLayout forOthers;
    ProgressBar progress_bar;
    AlertDialog dialog;

    //for usermodel
    String sfname, slname, sstudid, scontacts, sgender, sbday, semail, spass;
    //for interest
    String ss, e, fb, ca, bp, se;

    //Database
    FirebaseDatabase rootNode;
    DatabaseReference reference, catref;

    //for validation
    String studentid;

    //Authentication
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public int Answer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        dialog = builder.setTitle("")
                .setMessage("REQUIRED: Please use your UMak email address upon registering.")
                .setNeutralButton("OK", null)
                .create();
        dialog.show();

        //Hook
        back = findViewById(R.id.btnback);
        backlogin = findViewById(R.id.backlogin);
        register = findViewById(R.id.btnRegister);
        checkVerified = findViewById(R.id.ImageView_emailVerified);
        sendCode = findViewById(R.id.card_sendemailcode);
        forOthers = findViewById(R.id.LinearForOthers);
        progress_bar = findViewById(R.id.regprogressbar);

        fname = findViewById(R.id.Reg_fname);
        lname = findViewById(R.id.Reg_lname);
        studid = findViewById(R.id.Reg_studid);
        contact = findViewById(R.id.Reg_contactnum);
        email = findViewById(R.id.Reg_email);
        cpass = findViewById(R.id.Reg_CreatePass);

        chkSS = findViewById(R.id.chkSchoolSupplies);
        chkE = findViewById(R.id.chkElectronics);
        chkFB = findViewById(R.id.chkFoodsBeverage);
        chkCA = findViewById(R.id.chkClothesandAccessories);
        chkBP = findViewById(R.id.chkBeautyProduct);
        chkSE = findViewById(R.id.chkSportsEquipment);
        chkOthers = findViewById(R.id.chkOthers);


        chkOthers.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                forOthers.setVisibility(View.VISIBLE);
            } else {
                forOthers.setVisibility(View.INVISIBLE);
            }
        });

        //for validation methods
        studentid = Objects.requireNonNull(studid.getEditText()).getText().toString();

        //go back
        back.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });

        //go to login
        backlogin.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });

        //verify email
        sendCode.setOnClickListener(view -> {
            progress_bar.setVisibility(View.VISIBLE);
            String verify_email = Objects.requireNonNull(email.getEditText()).getText().toString();
            String verify_pass = Objects.requireNonNull(cpass.getEditText()).getText().toString();

            if (validateEmail() && validatePass()) {
                firebaseAuth.createUserWithEmailAndPassword(verify_email, verify_pass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progress_bar.setVisibility(View.INVISIBLE);
                        sendCode.setEnabled(false);
                        sendCode.setCardBackgroundColor(Color.GRAY);
                        checkVerified.setColorFilter(ContextCompat.getColor(this, R.color.shamrock), android.graphics.PorterDuff.Mode.SRC_IN);
                        Answer = 1;
                    }
                });
            }
        });

        //register
        register.setOnClickListener(v -> {
            progress_bar.setVisibility(View.VISIBLE);
            if (!validateFName() | !validateLName() | !validatestudID() | !validateContact() | !validateEmail() | !validatePass()) {
                progress_bar.setVisibility(View.INVISIBLE);
                return;
            }

            if (Answer == 1) {

                if (!validateFName() | !validateLName() | !validatestudID() | !validateContact() | !validateEmail() | !validatePass()) {
                    return;
                }

                if (!chkSS.isChecked() && !chkE.isChecked() && !chkFB.isChecked() && !chkCA.isChecked() && !chkBP.isChecked() && !chkSE.isChecked()) {
                    Toast.makeText(getBaseContext(), "Please check atleast one interest", Toast.LENGTH_SHORT).show();
                } else {

                    //Get all the values
                    sfname = Objects.requireNonNull(fname.getEditText()).getText().toString();
                    slname = Objects.requireNonNull(lname.getEditText()).getText().toString();
                    sstudid = Objects.requireNonNull(studid.getEditText()).getText().toString().toLowerCase();
                    scontacts = Objects.requireNonNull(contact.getEditText()).getText().toString();
                    sgender = "";
                    sbday = "";
                    semail = Objects.requireNonNull(email.getEditText()).getText().toString();
                    spass = Objects.requireNonNull(cpass.getEditText()).getText().toString();


                    if (chkSS.isChecked()) {
                        ss = "School Equipment";
                    } else {
                        ss = "";
                    }

                    if (chkE.isChecked()) {
                        e = "Electronics";
                    } else {
                        e = "";
                    }

                    if (chkFB.isChecked()) {
                        fb = "Food and Beverages";
                    } else {
                        fb = "";
                    }

                    if (chkCA.isChecked()) {
                        ca = "Clothes and Accessories";
                    } else {
                        ca = "";
                    }
                    if (chkBP.isChecked()) {
                        bp = "Beauty Products";
                    } else {
                        bp = "";
                    }
                    if (chkSE.isChecked()) {
                        se = "Sports Equipment";
                    } else {
                        se = "";
                    }

                    rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");
                    reference = rootNode.getReference("users");
                    catref = rootNode.getReference("interests");

                    //user database
                    UserHelperClass userHelperClass = new UserHelperClass(sfname, slname, sstudid, scontacts, sgender, sbday, semail, spass);
                    reference.child(sstudid).setValue(userHelperClass);
                    //interest database
                    InterestHelperClass interestHelperClass = new InterestHelperClass(sstudid, ss, e, fb, ca, bp, se);
                    catref.child(sstudid).setValue(interestHelperClass);

                    DocumentReference documentReference =  firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));

                    Map<String, Object> userdata = new HashMap<>();
                    userdata.put("name", String.format("%s %s", sfname, slname));
                    userdata.put("uid", firebaseAuth.getUid());
                    userdata.put("status", "Offline");
                    userdata.put("userid", sstudid);

                    documentReference.set(userdata).addOnSuccessListener(unused -> {
                        Toast.makeText(getApplicationContext(), "Data on Cloud Firestore send success", Toast.LENGTH_SHORT).show();
                        Log.d("FIRESTORE: ", "Data on Cloud Firestore send success"); });


                    progress_bar.setVisibility(View.INVISIBLE);

                    AlertDialog dialog = builder.setTitle("")
                            .setMessage("Registered Successfully!")
                            .create();

                    dialog.show();

                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    private Boolean validateFName() {
        String val = Objects.requireNonNull(fname.getEditText()).getText().toString();

        if (val.isEmpty()) {
            fname.setError("Field cannot be empty");
            return false;
        } else {
            fname.setError(null);
            fname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateLName() {
        String val = Objects.requireNonNull(lname.getEditText()).getText().toString();

        if (val.isEmpty()) {
            lname.setError("Field cannot be empty");
            return false;
        } else {
            lname.setError(null);
            lname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatestudID() {
        String val = Objects.requireNonNull(studid.getEditText()).getText().toString();
        String noWhiteSpace = "^\\A\\w{8,10}\\z$";

        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");

        Query checkID = reference.orderByChild("studID").equalTo(val);

        checkID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    studid.setError("This Student ID is already exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (val.isEmpty()) {
            studid.setError("Field cannot be empty");
            return false;
        } else if (val.length() > 10) {
            studid.setError("Student ID too long");
            return false;
        } else if (val.length() < 9) {
            studid.setError("Invalid. Use your UMak Student ID");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            studid.setError("Space not allowed");
            return false;
        } else {
            studid.setError(null);
            studid.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateContact() {
        String val = Objects.requireNonNull(contact.getEditText()).getText().toString();
        if (val.isEmpty()) {
            contact.setError("Field cannot be empty");
            return false;
        } else {
            contact.setError(null);
            contact.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = Objects.requireNonNull(email.getEditText()).getText().toString();
        String emailPattern = "^\\S+@umak\\.edu\\.ph$";

        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");

        Query checkEmail = reference.orderByChild("email").equalTo(val);

        checkEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    email.setError("This email is already registered");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            email.setError("Invalid. Use your UMak gmail");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePass() {
        String val = Objects.requireNonNull(cpass.getEditText()).getText().toString();
        String pass = "^" + "(?=.*[a-zA-Z0-9])" + ".{5,}" + "$";

        if (val.isEmpty()) {
            cpass.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(pass)) {
            cpass.setError("Password is weak");
            return false;
        } else {
            cpass.setError(null);
            cpass.setErrorEnabled(false);
            return true;
        }
    }



}