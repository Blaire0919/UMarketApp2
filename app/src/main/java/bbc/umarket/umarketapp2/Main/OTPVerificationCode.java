package bbc.umarket.umarketapp2.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.R;

public class OTPVerificationCode extends AppCompatActivity {

    TextView txtchangenum;
    EditText getotp;
    Button verifyotp;
    String otpcode;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBarofotpverification;

    String fname, lname, userID;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_otpvcode);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide status bar

        //fetching name from session manager
        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION );
        HashMap<String,String> usersdetails =  sessionManager.getUserDetailSession();
        //getting data from session
        fname = usersdetails.get(SessionManager.KEY_FNAME);
        lname = usersdetails.get(SessionManager.KEY_LNAME);
        userID = usersdetails.get(SessionManager.KEY_STUDID);

        txtchangenum = findViewById(R.id.changenumber);
        getotp = findViewById(R.id.etgetotp);
        verifyotp = findViewById(R.id.btnverifyotp);
        progressBarofotpverification = findViewById(R.id.progressbarofotpauth);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();


        txtchangenum.setOnClickListener(view -> {
            Intent intent = new Intent(OTPVerificationCode.this, OTPAuth.class);
            startActivity(intent);
        });

        verifyotp.setOnClickListener(view -> {
            otpcode = getotp.getText().toString();
            if(otpcode.isEmpty()){ Toast.makeText(getApplicationContext(), "Enter your OTP First", Toast.LENGTH_SHORT).show();
            }else {
                progressBarofotpverification.setVisibility(View.VISIBLE);
                String codereceiver= getIntent().getStringExtra("otp");
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codereceiver, otpcode);
                useChatWithPhoneCred(credential);
            }
        });

    }

    private void useChatWithPhoneCred(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                progressBarofotpverification.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Phone Verified", Toast.LENGTH_SHORT).show();
                sendDatatoFireStore();
                Intent intent = new Intent(OTPVerificationCode.this, HomeContainer.class);
                intent.putExtra("back_Chat","Chat");
                startActivity(intent);
            }else{
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                    progressBarofotpverification.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Phone Verification Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendDatatoFireStore() {
        DocumentReference documentReference =  firebaseFirestore.collection("Users").document(Objects.requireNonNull(firebaseAuth.getUid()));
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("name", fname+ " " +lname);
        userdata.put("uid", firebaseAuth.getUid());
        userdata.put("status", "Online");
        userdata.put("userid", userID);

        documentReference.set(userdata).addOnSuccessListener(unused -> {
            Toast.makeText(getApplicationContext(), "Data on Cloud Firestore send success", Toast.LENGTH_SHORT).show();
            Log.d("FIRESTORE: ", "Data on Cloud Firestore send success");
        });

    }
}