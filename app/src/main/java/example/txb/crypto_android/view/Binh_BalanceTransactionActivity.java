package example.txb.crypto_android.view;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.stream.Collectors;

import example.txb.crypto_android.R;
import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.BinhTransactionHistoryResponseModel;
import example.txb.crypto_android.model.response.QuyProfileResponseModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.view.adapter.BinhTransactionHistoryAdapter;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.BinhProfileViewModel;
import example.txb.crypto_android.view_model.BinhTransactionHistoryViewModel;


public class Binh_BalanceTransactionActivity extends AppCompatActivity {

    private TextView txtBalanceExcessMoney;
    private TextView txtBalanceIncome;
    private TextView txtBalanceAvailable;
    private TextView txtBalanceSourceMoney;
    private RecyclerView recyclerView;

    private ArrayList<BinhTransactionHistoryResponseModel.Item> items;
    private BinhTransactionHistoryViewModel viewModel;

    BinhTransactionHistoryAdapter binhTransactionHistoryAdapter;

    //
    private BinhProfileViewModel profileViewModel;
    QuyProfileResponseModel.Profile profileDetails;

    private String REGISTER_COIN_SERVICE_NAME = "binh-money-history-activity";
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
            loadData2();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binh_activity_balance_transaction);

        ActionBar();

        recyclerView = findViewById(R.id.recycle_view_transaction_);
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binhTransactionHistoryAdapter = new BinhTransactionHistoryAdapter(getApplicationContext());
        recyclerView.setAdapter(binhTransactionHistoryAdapter);


        initViews();

        viewModel = new BinhTransactionHistoryViewModel(getApplicationContext());

        profileViewModel = new BinhProfileViewModel(getApplicationContext());


        setObserve();
        loadData();

        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(Binh_BalanceTransactionActivity.this, CoinService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

//        loadData2();
    }

    public void onDestroy() {
        super.onDestroy();
        if (isBoundCoinService) {
            coinService.removeEventListener(REGISTER_COIN_SERVICE_NAME);
            unbindService(serviceConnection);
            isBoundCoinService = false;
        }
    }
    private void initViews() {

        txtBalanceExcessMoney = findViewById(R.id.txt_balance_excess_money);
        txtBalanceIncome = findViewById(R.id.txt_balance_income);
        txtBalanceAvailable = findViewById(R.id.txt_balance_available);
        txtBalanceSourceMoney = findViewById(R.id.txt_balance_source_money);

        //

    }

    private void setProfile() {
        float sumMoney = profileDetails.moneyInvested + profileDetails.moneyProfitNow + profileDetails.moneyNow;
        txtBalanceExcessMoney.setText("$" + String.format("%.2f", sumMoney) + " K");
//        String.format("%.2f", sumMoney)

        txtBalanceAvailable.setText("$" + String.format("%.2f", profileDetails.moneyNow));
        txtBalanceIncome.setText("$" + String.format("%.2f", profileDetails.moneyProfitNow));

        float sourceMoney = profileDetails.moneyNow + profileDetails.moneyInvested;
        txtBalanceSourceMoney.setText("$" + String.format("%.2f", sourceMoney));

    }

    private void setObserve() {
        // Set alert error
        viewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel it) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (it != null) {
                            Toast.makeText(Binh_BalanceTransactionActivity.this, it.content, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        profileViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel it) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (it != null) {
                            Toast.makeText(Binh_BalanceTransactionActivity.this, it.content, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Set alert notification
    }

    //data lịch sử giao dịch
    private void loadData() {
        viewModel.getTransactionHistory(new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                System.out.println(data);
                BinhTransactionHistoryResponseModel.Lists parseOj = new Gson().fromJson(data, BinhTransactionHistoryResponseModel.Lists.class);
                setData(parseOj);
            }
        });
    }

    //data money user
    private void loadData2() {
       /* Intent intent = getIntent();
        userId = intent.getStringExtra("id");*/
        profileViewModel.getInfo("mine", new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                System.out.println(data);
                profileDetails = new Gson().fromJson(data, QuyProfileResponseModel.Profile.class);
                System.out.println(profileDetails);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProfile();
                    }
                });
                ArrayList<String> coinIds = new ArrayList<>(profileDetails.openCommandItems.stream().map(e->e.coinId).collect(Collectors.toList()));
                if(coinIds.size()!=0){
                    coinService.addEventListener(coinIds, REGISTER_COIN_SERVICE_NAME,"mini-profile", new CoinServiceModel.EventCallbackInterface() {
                        @Override
                        public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
                            updateMiniInfoSumMoney(coins);
                        }
                    });
                }
            }
        });
    }


    private void setData(BinhTransactionHistoryResponseModel.Lists data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binhTransactionHistoryAdapter.setList(new ArrayList<>(data.items));
            }
        });

    }


    // Xử lý khi nút back được bấm
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ActionBar() {

        // Hiển thị nút back trên action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Hiển thị nút back
            actionBar.setDisplayHomeAsUpEnabled(true);

            // Đặt màu chữ
            int colorWhite = getResources().getColor(android.R.color.black);
            actionBar.setTitle(Html.fromHtml("<font color='" + colorWhite + "'>SỐ DƯ GIAO DỊCH</font>"));

            // Đặt màu của nút back
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.binh_ic_arrow_back);
            upArrow.setColorFilter(colorWhite, PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    private void updateMiniInfoSumMoney(ArrayList<CoinServiceModel.CoinNow> coins){
        profileDetails.moneyProfitNow = 0f;
        for (int i = 0; i < profileDetails.openCommandItems.size(); i++) {
            int indCoin = -1;
            for (int j = 0; j < coins.size(); j++) {
                if(coins.get(j).id.equals(profileDetails.openCommandItems.get(i).coinId)){
                    indCoin=j;
                    break;
                }
            }
            if(indCoin!=-1){
                profileDetails.moneyProfitNow+=(coins.get(indCoin).priceUsd-profileDetails.openCommandItems.get(i).openPrice)*profileDetails.openCommandItems.get(i).coinNumber*(profileDetails.openCommandItems.get(i).buyOrSell.equals("buy")?1f:-1f);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtBalanceAvailable.setText("$" + String.format("%.2f", profileDetails.moneyNow));
                txtBalanceExcessMoney.setText("$ "+String.format("%.2f",((profileDetails.moneyNow+profileDetails.moneyInvested+profileDetails.moneyProfitNow)/1000f))+" K");
            }
        });
    }
}
