package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bbc.umarket.umarketapp2.Helper.NotifModel;
import bbc.umarket.umarketapp2.Helper.RateReviewHelperClass;
import bbc.umarket.umarketapp2.R;


public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifViewHolder> {
    ArrayList<NotifModel> notiflist;
    Context context;

    public NotifAdapter(Context context, ArrayList<NotifModel> notiflist) {
        this.notiflist = notiflist;
        this.context = context;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_notif, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {

        NotifModel notifModel = notiflist.get(position);

        holder.time.setText(notifModel.getCurrenttime());

        holder.notif.setText(notifModel.getNotification());

    }

    @Override
    public int getItemCount() {
        return notiflist.size();
    }

    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView time, notif;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.notif_time);
            notif = itemView.findViewById(R.id.notif_sentence);

        }
    }


}
