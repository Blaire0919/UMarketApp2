package bbc.umarket.umarketapp2.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import bbc.umarket.umarketapp2.Main.AddListing;
import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Main.EditProfile;
import bbc.umarket.umarketapp2.Main.ProductDetails;
import bbc.umarket.umarketapp2.Main.SellerCenter;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.Main.Settings;

public class FragProfile extends Fragment {
    Context context;
    Button btnsellercenter;
    TextView name, id, editprofile;
    LinearLayout btnsettings;

    public FragProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);
        context = view.getContext();

        SessionManager sessionManager = new SessionManager(getActivity(), SessionManager.SESSION_USERSESSION );
        HashMap<String,String> usersdetails =  sessionManager.getUserDetailSession();

        //getting data from session
        String fname = usersdetails.get(SessionManager.KEY_FNAME);
        String lname = usersdetails.get(SessionManager.KEY_LNAME);
        String studid = usersdetails.get(SessionManager.KEY_STUDID);

        //hooks
        btnsettings = view.findViewById(R.id.prof_settings);
        editprofile = view.findViewById(R.id.editprof);
        btnsellercenter = view.findViewById(R.id.btnsellercenter);
        name = view.findViewById(R.id.Acc_name);
        id = view.findViewById(R.id.Acc_id);


        //displaying value
        name.setText(fname + "\n" + lname);
        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        id.setText(studid);

        btnsettings.setOnClickListener(v -> startActivity(new Intent(getActivity(), Settings.class)));

        editprofile.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfile.class)));

        btnsellercenter.setOnClickListener(v -> startActivity(new Intent(getActivity(), SellerCenter.class)));

        return view;
    }

}