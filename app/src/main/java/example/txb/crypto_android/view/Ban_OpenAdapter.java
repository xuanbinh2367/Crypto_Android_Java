package example.txb.crypto_android.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;

public class Ban_OpenAdapter extends RecyclerView.Adapter<Ban_OpenAdapter.ViewCustom> {
    Button editButton;
    Button closeButton;
    private ArrayList<Ban_OpenCommand> opencommand;

    OpenCallback openCallback;

    public static interface OpenCallback{
        public void open(String commandId);
    }

    public void setOpenCallback(OpenCallback openCallback) {
        this.openCallback = openCallback;
    }



    public Ban_OpenAdapter(Context context,OpenCallback openCallback) {
        this.context = context;
        this.openCallback = openCallback;
    }

    Context context;
    public void setList(ArrayList<Ban_OpenCommand> opencommand){
        this.opencommand= opencommand;
        this.notifyDataSetChanged();
    }
    public void updateCommand(String commandId, float profitNow){
        for (int i = 0; i < opencommand.size(); i++) {
            if(opencommand.get(i).getId().equals(commandId)){
                opencommand.get(i).setValue(profitNow);
                this.notifyItemChanged(i);
                break;
            }
        }
    }
    @NonNull
    @Override
    public ViewCustom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewCustom(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ban_command_openitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCustom holder, int position) {
        holder.bindData(opencommand.get(position));
    }
    @Override
    public int getItemCount() {
        return opencommand != null ? opencommand.size() : 0;
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
            Time = (TextView) itemView.findViewById(R.id.txtOpenTime);
            Leverage = (TextView) itemView.findViewById(R.id.txtCloseLeverage);
            ImgCoin = (ImageView) itemView.findViewById(R.id.imgCloseCoin);
            ImgArrow = (ImageView) itemView.findViewById(R.id.imgCloseArrow);
            editButton = (Button) itemView.findViewById(R.id.btnEdit);
            closeButton = (Button) itemView.findViewById(R.id.btnClose);

        }

        public void bindData(Ban_OpenCommand item) {
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

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCallback.open(item.Id);
                }
            });
            closeButton.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openCallback.open(item.Id);
                }
            }));
            Time.setText(General.convertTimeToDateTime(item.time));
        }
    }
}
