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

import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.SellerSide.ShippingOrder;


public class CompletedOrderAdapter extends RecyclerView.Adapter<CompletedOrderAdapter.CompletedOrderViewHolder> {
    ArrayList<ToProcessModel> completedList;
    Context context;

    public CompletedOrderAdapter(Context context, ArrayList<ToProcessModel> completedList) {
        this.completedList = completedList;
        this.context = context;
    }

    @NonNull
    @Override
    public CompletedOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_orders, parent, false);
        return new CompletedOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedOrderViewHolder holder, int position) {

        ToProcessModel completedOrderModel = completedList.get(position);

        holder.buyerName.setText(completedOrderModel.getBuyerName());
        holder.prodID.setText(completedOrderModel.getProdID());
        holder.prodName.setText(completedOrderModel.getProdName());
        holder.price.setText(completedOrderModel.getPrice());
        holder.qty.setText(completedOrderModel.getQty());
        holder.totAmt.setText(completedOrderModel.getTotAmt());
        holder.datetime.setText(String.format("%s %s", completedOrderModel.getCurrentdate(), completedOrderModel.getCurrenttime()));

        holder.card.setOnClickListener(view -> {
            Intent intent = new Intent(context, ShippingOrder.class);
            intent.putExtra("buyername", completedOrderModel.getBuyerName());
            intent.putExtra("pid", completedOrderModel.getProdID());
            intent.putExtra("prodname", completedOrderModel.getProdName());
            intent.putExtra("price", completedOrderModel.getPrice());
            intent.putExtra("qty", completedOrderModel.getQty());
            intent.putExtra("totamt", completedOrderModel.getTotAmt());
            intent.putExtra("buyerid", completedOrderModel.getBuyerID());

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return completedList.size();
    }

    public static class CompletedOrderViewHolder extends RecyclerView.ViewHolder {
        TextView buyerName, prodID, prodName, price, qty, totAmt, datetime;
        CardView card;

        public CompletedOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.order_card);
            buyerName = itemView.findViewById(R.id.txt_buyersname);
            prodID = itemView.findViewById(R.id.txt_prodid);
            prodName = itemView.findViewById(R.id.txt_prodname);
            price = itemView.findViewById(R.id.txt_price);
            qty = itemView.findViewById(R.id.txt_qty);
            totAmt = itemView.findViewById(R.id.txt_totamt);
            datetime = itemView.findViewById(R.id.txt_datetime);


        }
    }


}
