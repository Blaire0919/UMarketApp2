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

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Helper.RateReviewHelperClass;
import bbc.umarket.umarketapp2.R;


public class RateReviewAdapter extends RecyclerView.Adapter<RateReviewAdapter.RRViewHolder> {
    ArrayList<RateReviewHelperClass> rrlist;
    Context context;

    public RateReviewAdapter(Context context, ArrayList<RateReviewHelperClass> rrlist) {
        this.rrlist = rrlist;
        this.context = context;
    }

    @NonNull
    @Override
    public RRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_client_rateandreview, parent, false);
        return new RRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RRViewHolder holder, int position) {

        RateReviewHelperClass rateReviewHelperClass = rrlist.get(position);

        Glide.with(context)
                .load(rateReviewHelperClass.getRrImg())
                .into(holder.OrderImg);

        holder.name.setText(rateReviewHelperClass.getNameofBuyer());
        holder.studid.setText(rateReviewHelperClass.getStudID());
        holder.rate.setRating(Float.parseFloat(rateReviewHelperClass.getRate()));
        holder.ratetxt.setText(String.format("(%s)", rateReviewHelperClass.getRate()));
        holder.comment.setText(rateReviewHelperClass.getReview());
    }

    @Override
    public int getItemCount() {
        return rrlist.size();
    }

    public static class RRViewHolder extends RecyclerView.ViewHolder {
        TextView name, studid, ratetxt, comment;
        RatingBar rate;
        CardView cardView;
        ImageView OrderImg;

        public RRViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.rr_name);
            studid = itemView.findViewById(R.id.rr_studid);
            ratetxt = itemView.findViewById(R.id.rr_ratetxt);
            comment = itemView.findViewById(R.id.rr_comment);
            rate = itemView.findViewById(R.id.rr_ratingbar);
            cardView = itemView.findViewById(R.id.rrcard);
            OrderImg = itemView.findViewById(R.id.rrOrderImg);
        }
    }


}
