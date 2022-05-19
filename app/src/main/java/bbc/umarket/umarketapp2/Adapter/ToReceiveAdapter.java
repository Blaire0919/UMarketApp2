package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import bbc.umarket.umarketapp2.Database.SessionManager;
import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Helper.RateReviewHelperClass;
import bbc.umarket.umarketapp2.Helper.ToProcessModel;
import bbc.umarket.umarketapp2.Helper.ToReceiveModel;
import bbc.umarket.umarketapp2.Main.ProductDetails;
import bbc.umarket.umarketapp2.Main.RateAndReview;
import bbc.umarket.umarketapp2.Main.ToRate;
import bbc.umarket.umarketapp2.R;
import bbc.umarket.umarketapp2.SellerSide.ManageOrders;
import bbc.umarket.umarketapp2.SellerSide.ProcessOrder;
import bbc.umarket.umarketapp2.SellerSide.ShippingOrder;

public class ToReceiveAdapter extends RecyclerView.Adapter<ToReceiveAdapter.ToReceiveViewHolder> {
    ArrayList<ToReceiveModel> toReceiveList;
    Context context;

    String completedOrderkey, ImgUrl, buyerID, prodID, sellerID, sellerName, prodName, prodQty, prodPrice;

    //shipping database reference
    DatabaseReference Ref, ReceiveRef;

    String removeToReceiveKey;

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

        Glide.with(context)
                .load(toReceiveModel.getImgUrl())
                .into(holder.img);
        holder.seller.setText(toReceiveModel.getSellerName());
        holder.prodname.setText(toReceiveModel.getProdName());
        holder.price.setText(toReceiveModel.getProdPrice());
        holder.qty.setText(String.format("(%s)", toReceiveModel.getProdQty()));

        holder.button.setOnClickListener(view -> {
            ImgUrl = toReceiveModel.getImgUrl();
            buyerID = toReceiveModel.getBuyerID();
            prodID = toReceiveModel.getProdID();
            sellerID = toReceiveModel.getSellerID();
            sellerName = toReceiveModel.getSellerName();
            prodName = toReceiveModel.getProdName();
            prodQty = toReceiveModel.getProdQty();
            prodPrice = toReceiveModel.getProdPrice();

            Ref = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("shipping_order").child(toReceiveModel.getSellerID());

            //deleting shippped order
            Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            ToProcessModel toProcessModel = snapshot1.getValue(ToProcessModel.class);
                            if (toProcessModel != null) {
                                completedOrderkey = snapshot1.getKey();
                                Log.d("PUSH KEY", completedOrderkey);
                                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("completed_order")
                                        .child(toReceiveModel.getSellerID())
                                        .push()
                                        .setValue(toProcessModel)
                                        .addOnSuccessListener(unused -> {
                                                    Log.d("Moved to Completed order ", completedOrderkey);

                                                    FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                            .getReference("shipping_order")
                                                            .child(toReceiveModel.getSellerID())
                                                            .child(completedOrderkey)
                                                            .removeValue()
                                                            .addOnSuccessListener(unused1 ->
                                                                    Log.d("Removed from shipping order with Key ", completedOrderkey)
                                                            );
                                                }
                                        );
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            ReceiveRef = FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("to_receive").child(toReceiveModel.getBuyerID());

            ReceiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot snapshot2 : snapshot.getChildren()){
                            ToReceiveModel toReceiveModel1 = snapshot2.getValue(ToReceiveModel.class);
                            if (toReceiveModel1 !=null){
                                removeToReceiveKey = snapshot2.getKey();
                                Log.d("PUSH KEY", removeToReceiveKey);

                                //move item data from to_receive to to_rate
                                ToReceiveModel toRate = new ToReceiveModel(ImgUrl, buyerID, prodID, sellerID, sellerName, prodName, prodQty, prodPrice);
                                FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                        .getReference("to_rate")
                                        .child(toReceiveModel.getBuyerID())
                                        .push()
                                        .setValue(toRate)
                                        .addOnSuccessListener(unused -> {
                                           Log.d("OKAY","Received order is now available to rate");
                                            Intent intent = new Intent(context, RateAndReview.class);
                                            intent.putExtra("pID", toReceiveModel.getProdID());
                                            intent.putExtra("imageUrl", toReceiveModel.getImgUrl());
                                            intent.putExtra("pname", toReceiveModel.getProdName());

                                            //for passing to rate params in rate and review activity
                                            intent.putExtra("imgurl", toReceiveModel.getImgUrl());
                                            intent.putExtra("buyerid", toReceiveModel.getBuyerID());
                                            intent.putExtra("prodid", toReceiveModel.getProdID());
                                            intent.putExtra("sellerid", toReceiveModel.getSellerID());
                                            intent.putExtra("sellername", toReceiveModel.getSellerName());
                                            intent.putExtra("prodname", toReceiveModel.getProdName());
                                            intent.putExtra("prodqty", toReceiveModel.getProdQty());
                                            intent.putExtra("prodprice", toReceiveModel.getProdPrice());

                                            FirebaseDatabase.getInstance("https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                                    .getReference("to_receive")
                                                    .child(toReceiveModel.getBuyerID())
                                                    .child(removeToReceiveKey)
                                                    .removeValue()
                                                    .addOnSuccessListener(unused1 -> Log.d("Removed from to receive with Key ", completedOrderkey));

                                            context.startActivity(intent);
                                        });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        });

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
