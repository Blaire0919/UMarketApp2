package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.Main.RateAndReview;
import bbc.umarket.umarketapp2.R;

public class ToRateAdapter extends RecyclerView.Adapter<ToRateAdapter.ToRateViewHolder> {
    ArrayList<ToReceiveModel> toRateList;
    Context context;

    public ToRateAdapter(Context context, ArrayList<ToReceiveModel> toRateList) {
        this.toRateList = toRateList;
        this.context = context;
    }

    @NonNull
    @Override
    public ToRateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_torate_item, parent, false);
        return new ToRateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToRateViewHolder holder, int position) {
        ToReceiveModel toRateModel = toRateList.get(position);

        Glide.with(context)
                .load(toRateModel.getImgUrl())
                .into(holder.img);

        holder.seller.setText(toRateModel.getSellerName());
        holder.prodname.setText(toRateModel.getProdName());
        holder.price.setText(toRateModel.getProdPrice());
        holder.qty.setText(String.format("(%s)", toRateModel.getProdQty()));

        holder.button.setOnClickListener(view -> {
            Intent intent = new Intent(context, RateAndReview.class);
            intent.putExtra("pID", toRateModel.getProdID());
            intent.putExtra("imageUrl", toRateModel.getImgUrl());
            intent.putExtra("pname", toRateModel.getProdName());

            //for passing to rate params in rate and review activity
            intent.putExtra("imgurl", toRateModel.getImgUrl());
            intent.putExtra("buyerid", toRateModel.getBuyerID());
            intent.putExtra("prodid", toRateModel.getProdID());
            intent.putExtra("sellerid", toRateModel.getSellerID());
            intent.putExtra("sellername", toRateModel.getSellerName());
            intent.putExtra("prodname", toRateModel.getProdName());
            intent.putExtra("prodqty", toRateModel.getProdQty());
            intent.putExtra("prodprice", toRateModel.getProdPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return toRateList.size();
    }

    public static class ToRateViewHolder extends RecyclerView.ViewHolder {
        TextView seller, prodname, price, qty;
        ImageView img;
        MaterialCardView button;

        public ToRateViewHolder(@NonNull View itemView) {
            super(itemView);
            seller = itemView.findViewById(R.id.torate_sellername);
            img = itemView.findViewById(R.id.torate_img);
            prodname = itemView.findViewById(R.id.torate_prodname);
            price = itemView.findViewById(R.id.torate_price);
            qty = itemView.findViewById(R.id.torate_qty);
            button = itemView.findViewById(R.id.card_rateproduct);
        }
    }


}
