package bbc.umarket.umarketapp2.Main;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.ContextThemeWrapper;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import bbc.umarket.umarketapp2.Helper.InterestHelperClass;
import bbc.umarket.umarketapp2.Helper.UserHelperClass;
import bbc.umarket.umarketapp2.R;


public class Register extends AppCompatActivity {
    ImageView back;
    TextView backlogin;
    TextInputLayout fname, lname, studid, contact, email, cpass;
    Button register;
    CheckBox chkSS, chkE, chkFB, chkCA, chkBP, chkSE;
    FirebaseDatabase rootNode;
    DatabaseReference reference, catref;

    //for validation
    String check_id, check_email, studentid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        AlertDialog dialog = builder.setTitle("")
                .setMessage("REQUIRED: Please use your UMak email address upon signing up.")
                .setNeutralButton("OK", null)
                .create();
        dialog.show();

        //Hook
        back = findViewById(R.id.btnback);
        backlogin = findViewById(R.id.backlogin);
        register = findViewById(R.id.btnRegister);

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

        back.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });

        backlogin.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });

        register.setOnClickListener(v -> RegisterUser());

        studentid = Objects.requireNonNull(studid.getEditText()).getText().toString();
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

        DatabaseReference reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        Query checkID = reference.orderByChild("studID").equalTo(val);

        if (val.isEmpty()) {
            studid.setError("Field cannot be empty");
            return false;
        } else if (val.length() > 10) {
            studid.setError("Student ID too long");
            return false;
        } else if (val.length() < 9) {
            studid.setError("Invalid. Use your Student ID");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            studid.setError("Space not allowed");
            return false;
        }else if(Objects.equals(val, check_id)){
            studid.setError("This Student ID is already registered");
            return false;
        }
        else {
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

        DatabaseReference reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users");
        Query checkEmail = reference.orderByChild("email").equalTo(val);

        checkEmail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    check_email = snapshot.child(studentid).child("email").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            email.setError("Invalid. Use your UMak gmail");
            return false;
        }else if (Objects.equals(val, check_email)) {
            email.setError("This email is already registered");
            return false;
        }
        else {
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

    private void RegisterUser() {
        String ss, e, fb, ca, bp, se;

        rootNode = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/");
        reference = rootNode.getReference("users");
        catref = rootNode.getReference("interests");

        if (!validateFName() | !validateLName() | !validatestudID() | !validateContact() | !validateEmail() | !validatePass()) {
            return;
        }

        if (!chkSS.isChecked() && !chkE.isChecked() && !chkFB.isChecked() && !chkCA.isChecked() && !chkBP.isChecked() && !chkSE.isChecked()) {
            Toast.makeText(getBaseContext(), "Please check atleast one interest", Toast.LENGTH_SHORT).show();
        } else {

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

            //Get all the values
            String sfname = Objects.requireNonNull(fname.getEditText()).getText().toString();
            String slname = Objects.requireNonNull(lname.getEditText()).getText().toString();
            String sstudid = Objects.requireNonNull(studid.getEditText()).getText().toString().toLowerCase();
            String scontacts = Objects.requireNonNull(contact.getEditText()).getText().toString();
            String sgender = "";
            String sbday = "";
            String semail = Objects.requireNonNull(email.getEditText()).getText().toString();
            String spass = Objects.requireNonNull(cpass.getEditText()).getText().toString();
            String is_seller = "";

            UserHelperClass helperClass = new UserHelperClass(sfname, slname, sstudid, scontacts, sgender, sbday, semail, spass, is_seller);
            reference.child(sstudid).setValue(helperClass);

            InterestHelperClass helperClass1 = new InterestHelperClass(sstudid, ss, e, fb, ca, bp, se);
            catref.child(sstudid).setValue(helperClass1);

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            AlertDialog dialog = builder.setTitle("")
                    .setMessage("Registered Successfully!")
                    .create();

            dialog.show();


            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        }

    }


}