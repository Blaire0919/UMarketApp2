package bbc.umarket.umarketapp2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import bbc.umarket.umarketapp2.Helper.MessagesModel;
import bbc.umarket.umarketapp2.R;

public class MessagesAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<MessagesModel> messagesModelArrayList;

    int ITEM_SEND=1;
    int ITEM_RECEIVE=2;

    public MessagesAdapter(Context context, ArrayList<MessagesModel> messagesModelArrayList) {
        this.context = context;
        this.messagesModelArrayList = messagesModelArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      if(viewType==ITEM_SEND){
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.senderchatlayout, parent, false);
          return new SenderViewHolder(view);
      }else{
          View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiverchatlayout, parent, false);
          return new ReceiverViewHolder(view);
      }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessagesModel messagesModel = messagesModelArrayList.get(position);

        if (holder.getClass() == SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.message.setText(messagesModel.getMessages());
            viewHolder.timeofmessage.setText(messagesModel.getCurrenttime());
        }
        else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.message.setText(messagesModel.getMessages());
            viewHolder.timeofmessage.setText(messagesModel.getCurrenttime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessagesModel messagesModel = messagesModelArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messagesModel.getSenderId())){
            return ITEM_SEND;
        }else{
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesModelArrayList.size();
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView message, timeofmessage;
        public SenderViewHolder(@NonNull View itemView) { super(itemView);
            message = itemView.findViewById(R.id.sendermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
        }
    }

   public static class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView message, timeofmessage;
        public ReceiverViewHolder(@NonNull View itemView) {super(itemView);
            message = itemView.findViewById(R.id.sendermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
        }
    }

}
