package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.SellerSide.ShippingOrder;


public class PHistoryAdapter extends RecyclerView.Adapter<PHistoryAdapter.PHistoryViewHolder> {
    ArrayList<ToReceiveModel> purchaseList;
    Context context;

    public PHistoryAdapter(Context context, ArrayList<ToReceiveModel> purchaseList) {
        this.purchaseList = purchaseList;
        this.context = context;
    }

    @NonNull
    @Override
    public PHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_phistory_item, parent, false);
        return new PHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PHistoryViewHolder holder, int position) {

        ToReceiveModel purchaseModel = purchaseList.get(position);

        Glide.with(context)
                .load(purchaseModel.getImgUrl())
                .into(holder.img);
        holder.seller.setText(purchaseModel.getSellerName());
        holder.prodname.setText(purchaseModel.getProdName());
        holder.price.setText(purchaseModel.getProdPrice());
        holder.qty.setText(String.format("(%s)", purchaseModel.getProdQty()));

    }

    @Override
    public int getItemCount() {   return purchaseList.size();
    }

    public static class PHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView seller, prodname, price, qty;
        ImageView img;

        public PHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            seller = itemView.findViewById(R.id.phistory_sellername);
            img = itemView.findViewById(R.id.phistory_img);
            prodname = itemView.findViewById(R.id.phistory_prodname);
            price = itemView.findViewById(R.id.phistory_price);
            qty = itemView.findViewById(R.id.phistory_qty);

        }
    }
}
