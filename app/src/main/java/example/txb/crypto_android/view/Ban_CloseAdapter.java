package example.txb.crypto_android.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;

public class Ban_CloseAdapter extends RecyclerView.Adapter<Ban_CloseAdapter.ViewCustom> {
    private ArrayList<Ban_CloseCommand> closecommand;

    OpenCallback openCallback;

    public static interface OpenCallback{
        public void open(String commandId);
    }



    public Ban_CloseAdapter(Context context,OpenCallback openCallback) {
        this.context = context;
        this.openCallback = openCallback;
    }

    Context context;
    public void setList(ArrayList<Ban_CloseCommand> closecommand){
        this.closecommand= closecommand;
        this.notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewCustom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewCustom(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ban_command_closeitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCustom holder, int position) {
        holder.bindData(closecommand.get(position));
    }
    @Override
    public int getItemCount() {
        return closecommand != null ? closecommand.size() : 0;
    }
    class ViewCustom extends RecyclerView.ViewHolder {

        private TextView CoinName;
        private TextView Value;
        private TextView Leverage;
        private ImageView ImgCoin;
        private ImageView ImgArrow;
        private TextView Time;

        public ViewCustom(View itemView) {
            super(itemView);
            CoinName = (TextView) itemView.findViewById(R.id.txtCloseCoin);
            Value = (TextView) itemView.findViewById(R.id.txtCloseValue);
            Time = (TextView) itemView.findViewById(R.id.txtCloseTime);
            Leverage = (TextView) itemView.findViewById(R.id.txtCloseLeverage);
            ImgCoin = (ImageView) itemView.findViewById(R.id.imgCloseCoin);
            ImgArrow = (ImageView) itemView.findViewById(R.id.imgCloseArrow);


        }

        public void bindData(Ban_CloseCommand item) {
            CoinName.setText(item.CoinName);
            if(item.getValue()>=0){
                Value.setText("$ +" + String.format("%.2f", item.Value));
                Value.setTextColor(Color.GREEN);
            }else{
                Value.setText("$ " + String.format("%.2f", item.Value));
                Value.setTextColor(Color.RED);
            }
            Leverage.setText("$"+(long)item.moneynumber+" X "+item.Leverage);
            General.setImageUrl(context,ImgCoin,item.CoinImage);
            if(item.getBuyOrSell().equals("buy")){
                ImgArrow.setImageResource(R.drawable.ban_arrowup);
            }
            else{
                ImgArrow.setImageResource(R.drawable.ban_arrowdown);
            }
            Time.setText(General.convertTimeToDateTime(item.time));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openCallback.open(item.Id);
                }
            });
        }
    }
}
