package bbc.umarket.umarketapp2.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.R;

public class EditProfile extends AppCompatActivity {
    ImageView btnback, btnbday;
    TextView uploadimg;
    MaterialCardView save, cancel;
    EditText efname, elname, estudid, econtacts, ebday, eemail, epass;
    TextInputLayout tlcontacts, tlgender, tlbday, tlpass;
    int mDate, mMonth, mYear;
    AutoCompleteTextView autoCompletegender;
    DatabaseReference reference;

    //global variables for holding current data
    public String fname, lname, studid, contacts, gender, bday, email, pass, seller;

    //global variables for updating database and session manager
    public String upfname, uplname, upstudID, upcontacts, upgender, upbday, upemail, uppass, upseller;


    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_UMarketApp2);
        setContentView(R.layout.act_editprofile);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        reference = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("users");

       firebaseAuth = FirebaseAuth.getInstance();
  firebaseFirestore = FirebaseFirestore.getInstance();

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();

        //getting data from sessionmanager
        fname = usersdetails.get(SessionManager.KEY_FNAME);
        lname = usersdetails.get(SessionManager.KEY_LNAME);
        studid = usersdetails.get(SessionManager.KEY_STUDID);
        contacts = usersdetails.get(SessionManager.KEY_CONTACTS);
        gender = usersdetails.get(SessionManager.KEY_GENDER);
        bday = usersdetails.get(SessionManager.KEY_BDAY);
        email = usersdetails.get(SessionManager.KEY_EMAIL);
        pass = usersdetails.get(SessionManager.KEY_PASS);
        seller = usersdetails.get(SessionManager.KEY_IS_SELLER);

        //hooks buttons
        btnback = findViewById(R.id.editprof_back);
        save = findViewById(R.id.mcvsave);
        cancel = findViewById(R.id.mcvcancel);
        uploadimg = findViewById(R.id.uploadprofileimg);
        btnbday = findViewById(R.id.imgvbday);

        //hooks inputlayout
        tlcontacts = findViewById(R.id.textinputcontact);
        tlgender = findViewById(R.id.textinputgender);
        tlbday = findViewById(R.id.textinputbday);
        tlpass = findViewById(R.id.textinputpass);

        //hooks edittext
        efname = findViewById(R.id.epfname);
        elname = findViewById(R.id.eplname);
        estudid = findViewById(R.id.epstudid);
        econtacts = findViewById(R.id.epcontact);
        ebday = findViewById(R.id.epbday);
        autoCompletegender = findViewById(R.id.epgender);
        eemail = findViewById(R.id.epemail);
        epass = findViewById(R.id.epeditpass);

        ArrayList<String> arrayList_Gender = new ArrayList<>();
        arrayList_Gender.add("Female");
        arrayList_Gender.add("Male");
        arrayList_Gender.add("Others");
        ArrayAdapter<String> arrayAdapter_gender = new ArrayAdapter<>(this, R.layout.dropdown_item, arrayList_Gender);
        autoCompletegender.setAdapter(arrayAdapter_gender);
        autoCompletegender.setThreshold(1);

        btnbday.setOnClickListener(v -> {
            final Calendar Cal = Calendar.getInstance();
            mDate = Cal.get(Calendar.DATE);
            mMonth = Cal.get(Calendar.MONTH);
            mYear = Cal.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfile.this,
                    android.R.style.Theme_Holo_Dialog_MinWidth, (view, year, month, day) -> ebday.setText(new StringBuilder()
                    .append(day)
                    .append("-")
                    .append((month)+1)
                    .append("-")
                    .append(year)
                    .toString()), mYear, mMonth, mDate);
           datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

            datePickerDialog.show();
        });

        //displaying value
        efname.setText(fname);
        elname.setText(lname);
        estudid.setText(studid);
        econtacts.setText(contacts);
        autoCompletegender.setText(gender);
        ebday.setText(bday);
        eemail.setText(email);
        epass.setText(pass);

        btnback.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, HomeContainer.class);
            intent.putExtra("back_Acc", "Account");
            startActivity(intent);
            finish();
        });

        cancel.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, HomeContainer.class);
            intent.putExtra("back_Acc", "Account");
            startActivity(intent);
            finish();
        } );

        save.setOnClickListener(view -> updateUser());
    }

    private Boolean validateGender() {
        String val = Objects.requireNonNull(tlgender.getEditText()).getText().toString();

        if (val.isEmpty()) {
            tlgender.setError("Please choose your gender");
            return false;
        } else {
            tlgender.setError(null);
            tlgender.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateBday() {
        String val = Objects.requireNonNull(tlbday.getEditText()).getText().toString();

        if (val.isEmpty()) {
            tlbday.setError("Field cannot be empty");
            return false;
        } else {
            tlbday.setError(null);
            tlbday.setErrorEnabled(false);
            return true;
        }
    }

    private void updateUser() {
        if (!validateGender() | !validateBday()) {  return; }

        upfname = fname;
        uplname = lname;
        upstudID = studid;
        upemail = email;
        upseller = seller;

        if (!contacts.equals(Objects.requireNonNull(tlcontacts.getEditText()).getText().toString())) {
            reference.child(studid).child("contacts").setValue(Objects.requireNonNull(tlcontacts.getEditText()).getText().toString());
            upcontacts = Objects.requireNonNull(tlcontacts.getEditText()).getText().toString();
        }else{
            upcontacts = contacts;
        }

        if (!gender.equals(Objects.requireNonNull(tlgender.getEditText()).getText().toString())) {
            reference.child(studid).child("gender").setValue(Objects.requireNonNull(tlgender.getEditText()).getText().toString());
            upgender = Objects.requireNonNull(tlgender.getEditText()).getText().toString();
        }else {
            upgender = gender;
        }

        if (!bday.equals(Objects.requireNonNull(tlbday.getEditText()).getText().toString())) {
            reference.child(studid).child("bday").setValue(Objects.requireNonNull(tlbday.getEditText()).getText().toString());
            upbday = Objects.requireNonNull(tlbday.getEditText()).getText().toString();
        }else{upbday = bday;}

        if (!pass.equals(Objects.requireNonNull(tlpass.getEditText()).getText().toString())) {
            reference.child(studid).child("password").setValue(Objects.requireNonNull(tlpass.getEditText()).getText().toString());
            uppass = Objects.requireNonNull(tlpass.getEditText()).getText().toString();
        } else{uppass = pass;}

        //passing data to session manager
            SessionManager sessionManager = new SessionManager(EditProfile.this, SessionManager.SESSION_USERSESSION);
            sessionManager.createLoginSession(upfname, uplname, upstudID, upcontacts, upgender, upbday, upemail, uppass, upseller);

            Intent intent = new Intent(EditProfile.this, HomeContainer.class);
            intent.putExtra("back_Acc", "Account");
            startActivity(intent);
            finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
            documentReference.update("status", "Online");}
        catch (Exception exception) {
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