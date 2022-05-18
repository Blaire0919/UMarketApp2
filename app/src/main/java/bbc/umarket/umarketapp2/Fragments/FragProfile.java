package bbc.umarket.umarketapp2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.HashMap;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.Listener.ToReceiveLoadListener;
import bbc.umarket.umarketapp2.Main.EditProfile;
import bbc.umarket.umarketapp2.Main.ToReceive;
import bbc.umarket.umarketapp2.SellerSide.SellerCenter;
import bbc.umarket.umarketapp2.SellerSide.SellerRegistration;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.Main.Settings;

public class FragProfile extends Fragment implements ToReceiveLoadListener {
    Context context;
    Button btnsellercenter;
    TextView name, id, editprofile;
    LinearLayout btnsettings;
    String studid, fname, lname, userid;

    DatabaseReference sellerRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("seller");

    Query checkseller;

    ToReceiveLoadListener toReceiveLoadListener;
    NotificationBadge toreceivebadge;

    MaterialCardView toreceive;

    public FragProfile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);
        context = view.getContext();

        toReceiveLoadListener = this;
        countToReceive();

        SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        //getting data from session
        fname = usersdetails.get(SessionManager.KEY_FNAME);
        lname = usersdetails.get(SessionManager.KEY_LNAME);
        studid = usersdetails.get(SessionManager.KEY_STUDID);
        checkseller = sellerRef.orderByChild("userID").equalTo(studid);

        //hooks
        btnsettings = view.findViewById(R.id.prof_settings);
        editprofile = view.findViewById(R.id.editprof);
        btnsellercenter = view.findViewById(R.id.btnsellercenter);
        name = view.findViewById(R.id.Acc_name);
        id = view.findViewById(R.id.Acc_id);
        toreceivebadge = view.findViewById(R.id.toReceivebadge);
        toreceive = view.findViewById(R.id.cardToReceive);

        toreceive.setOnClickListener( view1 -> {
            Intent intent = new Intent(getActivity(), ToReceive.class);
            startActivity(intent);
        });


        //displaying value
        name.setText(String.format("%s\n%s", fname, lname));
        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        id.setText(studid);

        btnsettings.setOnClickListener(v -> startActivity(new Intent(getActivity(), Settings.class)));
        editprofile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfile.class)));

        btnsellercenter.setOnClickListener(v -> {
            checkseller.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Intent intent = new Intent(getActivity(), SellerCenter.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), SellerRegistration.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("ERROR", error.getDetails());
                }
            });
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        countToReceive();
    }


    private void countToReceive() {
        ArrayList<ToReceiveModel> toReceiveList = new ArrayList<>();

        SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_USERSESSION);
        HashMap<String, String> usersdetails = sessionManager.getUserDetailSession();
        studid = usersdetails.get(SessionManager.KEY_STUDID);

        FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("to_receive").child(studid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ToReceiveModel toReceiveModel = dataSnapshot.getValue(ToReceiveModel.class);
                                toReceiveList.add(toReceiveModel);
                            }
                            toReceiveLoadListener.onToReceiveLoadSuccess(toReceiveList);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("", error.getDetails());
                    }
                });
    }

    @Override
    public void onToReceiveLoadSuccess(ArrayList<ToReceiveModel> toReceiveList) {
        int TRSum = 0;
        for (ToReceiveModel toReceiveModel : toReceiveList) {
            TRSum += Integer.parseInt(toReceiveModel.getProdQty());
            toreceivebadge.setNumber(TRSum);
        }
    }

    @Override
    public void onToReceiveLoadFailed(String Message) {

    }
}