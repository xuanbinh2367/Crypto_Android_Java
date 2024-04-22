package example.txb.crypto_android.view;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.BanTradingCommandModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.view_model.BanCommandViewModel;
import example.txb.crypto_android.view_model.BaseViewModel;

public class Quy_GuestCommandsActivity extends AppCompatActivity {
    BanCommandViewModel banCommandViewModel;
    private ArrayList<Ban_CloseCommand> closeCommands = new ArrayList<>();
    BanTradingCommandModel.CloseTradingCommandList commandResponses;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    Ban_CloseAdapter closeAdapter;
//    String userId;

    private String REGISTER_COIN_SERVICE_NAME = "quy-guest-trading-commands-activity";
    private CoinService coinService;
    private Boolean isBoundCoinService=false;
    private ServiceConnection serviceConnection;

    private class CoinServiceCreatedCallback implements ServiceCreatedCallback {
        @Override
        public void setService(Service service) {
            coinService = (CoinService) service;
        }
        @Override
        public void setIsBound(Boolean isBound) {
            isBoundCoinService = isBound;
        }
        @Override
        public void createdComplete() {
            loadCoins();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ban_fragment_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sổ lệnh");

        banCommandViewModel = new BanCommandViewModel(getApplicationContext());
        setObserve();

        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(Quy_GuestCommandsActivity.this, CoinService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        recyclerView = findViewById(R.id.listCloseItem);
        progressBar = findViewById(R.id.LoadCloseList);
        closeAdapter = new Ban_CloseAdapter(getApplicationContext(),openObject);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(closeAdapter);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Ban_CloseAdapter.OpenCallback openObject = new Ban_CloseAdapter.OpenCallback() {
        @Override
        public void open(String commandId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Quy_GuestCommandsActivity.this, QuyViewTradingCommandActivity.class);
                    intent.putExtra("id", commandId);
                    startActivity(intent);
                }
            });

        }
    };

    @Override
    public void onStart() {
        super.onStart();


    }

    public void onDestroy() {
        super.onDestroy();
        if (isBoundCoinService) {
            coinService.removeEventListener(REGISTER_COIN_SERVICE_NAME);
            unbindService(serviceConnection);
            isBoundCoinService = false;
        }
    }

    public void setObserve(){
        banCommandViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel systemNotificationModel) {
                if(systemNotificationModel!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            General.showNotification(Quy_GuestCommandsActivity.this,systemNotificationModel);
                        }
                    });
                }
            }
        });
        banCommandViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isLoading==true){
                            progressBar.setVisibility(View.VISIBLE);
                        }else{
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
    private void loadCoins(){
        progressBar.setVisibility(View.VISIBLE);
        coinService.getAllCoins(new CoinService.GetAllWaitCallback() {
            @Override
            public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
                loadCommands(coins);
            }
        });
    }
    private void loadCommands(ArrayList<CoinServiceModel.CoinNow> coins){
        String userId = getIntent().getStringExtra("userId");

        banCommandViewModel.getList(userId, false, new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                commandResponses = new Gson().fromJson(data, BanTradingCommandModel.CloseTradingCommandList.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCommands(coins);
                    }
                });
            }
        });

    }

    private void setCommands(ArrayList<CoinServiceModel.CoinNow> coins){
        convertResponseModelToBan_CloseCommand(coins);
        closeAdapter.setList(closeCommands);
    }


    private CoinServiceModel.CoinNow getCoinById(ArrayList<CoinServiceModel.CoinNow> coins, String coinId){
        for (int i = 0; i < coins.size(); i++) {
            if(coins.get(i).id.equals(coinId))return coins.get(i);
        }
        return null;
    }

    private void convertResponseModelToBan_CloseCommand(ArrayList<CoinServiceModel.CoinNow> coins){
        for (int i = 0; i < commandResponses.items.size(); i++) {
            BanTradingCommandModel.CloseTradingCommandItem item = commandResponses.items.get(i);
            CoinServiceModel.CoinNow coin = getCoinById(coins, item.coinId);
            closeCommands.add(new Ban_CloseCommand(item.id,coin.id,coin.name,item.finalProfit,""+item.leverage, coin.icon,item.buyOrSell, item.openPrice, 1f, item.moneyNumber,item.closeTime));
        };
    }
}