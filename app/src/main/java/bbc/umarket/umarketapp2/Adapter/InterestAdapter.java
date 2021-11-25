package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.CategorizedListing;
import bbc.umarket.umarketapp2.R;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.InterestViewHolder> {

    ArrayList<InterestHelperClass> interestlist;
    Context context;


    public InterestAdapter(Context context, ArrayList<InterestHelperClass> interestlist) {
        this.interestlist = interestlist;
        this.context = context;
    }

    @Override
    public InterestAdapter.InterestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_categorycard, parent, false);
        return new InterestAdapter.InterestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestAdapter.InterestViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return interestlist.size();
    }

    public static class InterestViewHolder extends RecyclerView.ViewHolder {
        TextView catname;
        CardView cardView;

        public InterestViewHolder(@NonNull View itemView) {
            super(itemView);
            catname = itemView.findViewById(R.id.catname);
            cardView = itemView.findViewById(R.id.categoryCard);

        }
    }

}
