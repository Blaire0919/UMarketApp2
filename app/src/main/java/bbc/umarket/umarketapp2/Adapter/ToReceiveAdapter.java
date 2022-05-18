package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Helper.RateReviewHelperClass;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.R;


public class ToReceiveAdapter extends RecyclerView.Adapter<ToReceiveAdapter.ToReceiveViewHolder> {
    ArrayList<ToReceiveModel> toReceiveList;
    Context context;

    public ToReceiveAdapter(Context context, ArrayList<ToReceiveModel> toReceiveList) {
        this.toReceiveList = toReceiveList;
        this.context = context;
    }

    @NonNull
    @Override
    public ToReceiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_toreceive_item, parent, false);
        return new ToReceiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToReceiveViewHolder holder, int position) {

        ToReceiveModel toReceiveModel = toReceiveList.get(position);

    }

    @Override
    public int getItemCount() {
        return toReceiveList.size();
    }

    public static class ToReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView seller, prodname, price, qty;
        ImageView img;
        MaterialCardView button;

        public ToReceiveViewHolder(@NonNull View itemView) {
            super(itemView);

            seller = itemView.findViewById(R.id.toreceive_sellername);
            img = itemView.findViewById(R.id.toreceive_img);
            prodname = itemView.findViewById(R.id.toreceive_prodname);
            price = itemView.findViewById(R.id.toreceive_price);
            qty = itemView.findViewById(R.id.toreceive_qty);
            button = itemView.findViewById(R.id.card_orderreceived);

        }
    }


}