package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.SellerSide.ProcessOrder;
import bbc.umarket.umarketapp2.SellerSide.ShippingOrder;


public class ShippingAdapter extends RecyclerView.Adapter<ShippingAdapter.ShippingViewHolder> {
    ArrayList<ToProcessModel> shippingList;
    Context context;
    public static int setStatus;

    public ShippingAdapter(Context context, ArrayList<ToProcessModel> shippingList) {
        this.shippingList = shippingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShippingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_orders, parent, false);
        return new ShippingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShippingViewHolder holder, int position) {

        ToProcessModel shippingModel = shippingList.get(position);

        if(setStatus==1 ){
            holder.status.setVisibility(View.VISIBLE);
        }


        holder.buyerName.setText(shippingModel.getBuyerName());
        holder.prodID.setText(shippingModel.getProdID());
        holder.prodName.setText(shippingModel.getProdName());
        holder.price.setText(shippingModel.getPrice());
        holder.qty.setText(shippingModel.getQty());
        holder.totAmt.setText(shippingModel.getTotAmt());
        holder.datetime.setText(String.format("%s %s", shippingModel.getCurrentdate(), shippingModel.getCurrenttime()));

        holder.card.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShippingOrder.class);
            intent.putExtra("buyername", shippingModel.getBuyerName());
            intent.putExtra("pid", shippingModel.getProdID());
            intent.putExtra("prodname", shippingModel.getProdName());
            intent.putExtra("price", shippingModel.getPrice());
            intent.putExtra("qty", shippingModel.getQty());
            intent.putExtra("totamt", shippingModel.getTotAmt());
            intent.putExtra("buyerid", shippingModel.getBuyerID());
            intent.putExtra("sellerid", shippingModel.getSellerID());
            context.startActivity(intent);
        });

    }

    public static void StatusShipped(int value) {
        if (value==1){
            setStatus =1;
        }else{
            setStatus=2;
        }

    }

    @Override
    public int getItemCount() {
        return shippingList.size();
    }

    public static class ShippingViewHolder extends RecyclerView.ViewHolder {
        TextView buyerName, prodID, prodName, price, qty, totAmt, datetime;
        CardView card;
        LinearLayout status;


        public ShippingViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.order_card);
            buyerName = itemView.findViewById(R.id.txt_buyersname);
            prodID = itemView.findViewById(R.id.txt_prodid);
            prodName = itemView.findViewById(R.id.txt_prodname);
            price = itemView.findViewById(R.id.txt_price);
            qty = itemView.findViewById(R.id.txt_qty);
            totAmt = itemView.findViewById(R.id.txt_totamt);
            datetime = itemView.findViewById(R.id.txt_datetime);
            status = itemView.findViewById(R.id.linear_statusShipped);

        }
    }


}
