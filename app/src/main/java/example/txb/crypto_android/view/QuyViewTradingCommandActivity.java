package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.QuyTradingCommandResponseModel;
import example.txb.crypto_android.model.socket.SocketServiceEventsModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.service.SocketService;
import example.txb.crypto_android.view.custom_dialog.QuyEditBinhVerifyPinDialog;
import example.txb.crypto_android.view.custom_dialog.QuyVerifyOtpDialog;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.QuyTradingCommandViewModel;

public class QuyViewTradingCommandActivity extends AppCompatActivity {
    QuyCoinChartFragment coinChartFragment;
    QuyTradingCommandViewModel quyTradingCommandViewModel;

    FragmentContainerView quyViewTradingCommandActivityCoinChartFragmentContainer;
    CardView quyViewTradingCommandActivityOpenTradingCommandContainer;
    ImageView quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoIcon;
    TextView quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoName;
    ImageView quyViewTradingCommandActivityOpenTradingCommandContainerBuyOrSellIcon;
    TextView quyViewTradingCommandActivityOpenTradingCommandContainerProfitNow;
    TextView quyViewTradingCommandActivityOpenTradingCommandContainerCommission;
    TextView quyViewTradingCommandActivityOpenTradingCommandContainerTakeProfit;
    TextView quyViewTradingCommandActivityOpenTradingCommandContainerStopLost;
    ImageView quyViewTradingCommandActivityOpenTradingCommandContainerEditIcon;
    Button quyViewTradingCommandActivityOpenTradingCommandContainerCloseCommandBtn;
    CardView quyViewTradingCommandActivityCloseTradingCommandContainer;
    ImageView quyViewTradingCommandActivityCloseTradingCommandContainerCoinInfoIcon;
    TextView quyViewTradingCommandActivityCloseTradingCommandContainerCoinInfoName;
    ImageView quyViewTradingCommandActivityCloseTradingCommandContainerBuyOrSellIcon;
    TextView quyViewTradingCommandActivityCloseTradingCommandContainerProfit;
    TextView quyViewTradingCommandActivityCloseTradingCommandContainerSumValue;
    TextView quyViewTradingCommandActivityCloseTradingCommandContainerOpenPrice;
    TextView quyViewTradingCommandActivityCloseTradingCommandContainerOpenTime;
    TextView quyViewTradingCommandActivityCloseTradingCommandContainerClosePrice;
    TextView quyViewTradingCommandActivityCloseTradingCommandContainerCloseTime;
    TextView quyViewTradingCommandActivityCloseTradingCommandContainerCommission;
    LinearLayout quyViewTradingCommandActivityEditCommandContainer;
    View quyViewTradingCommandActivityEditCommandContainerCloseView;
    TextView quyViewTradingCommandActivityEditCommandContainerOpenPrice;
    TextView quyViewTradingCommandActivityEditCommandContainerOpenTime;
    TextView quyViewTradingCommandActivityEditCommandContainerValue;
    EditText quyViewTradingCommandActivityEditCommandContainerTakeProfit;
    Switch quyViewTradingCommandActivityEditCommandContainerEnableTPSL;
    EditText quyViewTradingCommandActivityEditCommandContainerStopLoss;
    Button quyViewTradingCommandActivityEditCommandContainerEditBtn;
    LinearLayout loadingLayout;

    private String REGISTER_COIN_SERVICE_NAME = "view-trading-command-activity";
    private CoinService coinService;
    private Boolean isBoundCoinService=false;
    private ServiceConnection coinServiceConnection = new ServiceConnections.CoinServiceConnection(new CoinServiceCreatedCallback());

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
            System.out.println("CoinServiceCreatedCallback");
        }
    }

    private String REGISTER_SOCKET_SERVICE_NAME = "view-trading-command";
    private SocketService socketService;
    private Boolean isBoundSocketService;
    private ServiceConnection socketServiceConnection = new ServiceConnections.SocketServiceConnection(new SocketServiceCreatedCallback());
    private class SocketServiceCreatedCallback implements ServiceCreatedCallback{
        @Override
        public void setService(Service service) {
            socketService = (SocketService) service;
        }
        @Override
        public void setIsBound(Boolean isBound) {
            isBoundSocketService = isBound;
        }
        @Override
        public void createdComplete() {
            System.out.println("SocketServiceCreatedCallback");
            socketService.addEventListener(SocketServiceEventsModel.EventNames.Receive.AutoCloseTradingCommand, REGISTER_SOCKET_SERVICE_NAME, new SocketServiceEventsModel.EventCallbackInterface() {
                @Override
                public void handle(String data) {
                    reloadData();
                }
            });
        }
    }

    private String tradingCommandId = null;
    QuyTradingCommandResponseModel.TradingCommandDetails tradingCommandDetails;
    CoinServiceModel.CoinNow coin;

    float profitNow=0f;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quy_view_trading_command);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        quyViewTradingCommandActivityCoinChartFragmentContainer = findViewById(R.id.quyViewTradingCommandActivityCoinChartFragmentContainer);
        quyViewTradingCommandActivityOpenTradingCommandContainer = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainer);
        quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoIcon = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoIcon);
        quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoName = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoName);
        quyViewTradingCommandActivityOpenTradingCommandContainerBuyOrSellIcon = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerBuyOrSellIcon);
        quyViewTradingCommandActivityOpenTradingCommandContainerProfitNow = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerProfitNow);
        quyViewTradingCommandActivityOpenTradingCommandContainerCommission = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerCommission);
        quyViewTradingCommandActivityOpenTradingCommandContainerTakeProfit = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerTakeProfit);
        quyViewTradingCommandActivityOpenTradingCommandContainerStopLost = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerStopLost);
        quyViewTradingCommandActivityOpenTradingCommandContainerEditIcon = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerEditIcon);
        quyViewTradingCommandActivityOpenTradingCommandContainerCloseCommandBtn = findViewById(R.id.quyViewTradingCommandActivityOpenTradingCommandContainerCloseCommandBtn);
        quyViewTradingCommandActivityCloseTradingCommandContainer = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainer);
        quyViewTradingCommandActivityCloseTradingCommandContainerCoinInfoIcon = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerCoinInfoIcon);
        quyViewTradingCommandActivityCloseTradingCommandContainerCoinInfoName = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerCoinInfoName);
        quyViewTradingCommandActivityCloseTradingCommandContainerBuyOrSellIcon = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerBuyOrSellIcon);
        quyViewTradingCommandActivityCloseTradingCommandContainerProfit = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerProfit);
        quyViewTradingCommandActivityCloseTradingCommandContainerSumValue = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerSumValue);
        quyViewTradingCommandActivityCloseTradingCommandContainerOpenPrice = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerOpenPrice);
        quyViewTradingCommandActivityCloseTradingCommandContainerOpenTime = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerOpenTime);
        quyViewTradingCommandActivityCloseTradingCommandContainerClosePrice = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerClosePrice);
        quyViewTradingCommandActivityCloseTradingCommandContainerCloseTime = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerCloseTime);
        quyViewTradingCommandActivityCloseTradingCommandContainerCommission = findViewById(R.id.quyViewTradingCommandActivityCloseTradingCommandContainerCommission);
        quyViewTradingCommandActivityEditCommandContainer = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainer);
        quyViewTradingCommandActivityEditCommandContainerCloseView = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainerCloseView);
        quyViewTradingCommandActivityEditCommandContainerOpenPrice = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainerOpenPrice);
        quyViewTradingCommandActivityEditCommandContainerOpenTime = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainerOpenTime);
        quyViewTradingCommandActivityEditCommandContainerValue = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainerValue);
        quyViewTradingCommandActivityEditCommandContainerTakeProfit = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainerTakeProfit);
        quyViewTradingCommandActivityEditCommandContainerEnableTPSL = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainerEnableTPSL);
        quyViewTradingCommandActivityEditCommandContainerStopLoss = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainerStopLoss);
        quyViewTradingCommandActivityEditCommandContainerEditBtn = findViewById(R.id.quyViewTradingCommandActivityEditCommandContainerEditBtn);
        loadingLayout = findViewById(R.id.loadingLayout);
//
//
//
        quyTradingCommandViewModel = new QuyTradingCommandViewModel(getApplicationContext());
//
//
//

        Intent intent1 = getIntent();
        tradingCommandId = intent1.getStringExtra("id");
//        tradingCommandId = "65831802eb7884668b3554d7";

//        tradingCommandId = "658792596ae2ca5adaa9616a";
//
        setObserve();
        setEvents();
//
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intentSocketService = new Intent(this, SocketService.class);
        bindService(intentSocketService, socketServiceConnection, Context.BIND_AUTO_CREATE);

        Intent intentCoinService = new Intent(this, CoinService.class);
        bindService(intentCoinService, coinServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setInitCoinChartFragment(){
        coinChartFragment = QuyCoinChartFragment.newInstance(tradingCommandDetails.coinId, tradingCommandDetails.isOpen?0L:tradingCommandDetails.closeTime, getApplicationContext());
        coinChartFragment.setIsChangeCoin(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.quyViewTradingCommandActivityCoinChartFragmentContainer, coinChartFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBoundCoinService) {
            coinService.removeEventListener(REGISTER_COIN_SERVICE_NAME);
            unbindService(coinServiceConnection);
            isBoundCoinService = false;
        }
        if (isBoundSocketService) {
            socketService.removeEventListener(REGISTER_SOCKET_SERVICE_NAME);
            unbindService(socketServiceConnection);
            isBoundSocketService = false;
        }
    }

    public void setObserve(){
        quyTradingCommandViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel systemNotificationModel) {
                if(systemNotificationModel!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            General.showNotification(QuyViewTradingCommandActivity.this,systemNotificationModel);
                        }
                    });
                }
            }
        });
        quyTradingCommandViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isLoading==true){
                            loadingLayout.setVisibility(View.VISIBLE);
                        }else{
                            loadingLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    private void setEvents(){
        quyViewTradingCommandActivityOpenTradingCommandContainerCloseCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeCommand();
            }
        });
        quyViewTradingCommandActivityOpenTradingCommandContainerEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quyViewTradingCommandActivityEditCommandContainer.getVisibility()==View.GONE){
                    quyViewTradingCommandActivityEditCommandContainer.setVisibility(View.VISIBLE);
                }else{
                    quyViewTradingCommandActivityEditCommandContainer.setVisibility(View.GONE);
                }
            }
        });
        quyViewTradingCommandActivityEditCommandContainerEnableTPSL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateEditCommandContainerTPSLInputStatus();
            }
        });
        quyViewTradingCommandActivityEditCommandContainerCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quyViewTradingCommandActivityEditCommandContainer.setVisibility(View.GONE);
            }
        });
        quyViewTradingCommandActivityEditCommandContainerEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCommand();
            }
        });

    }
    private void updateEditCommandContainerTPSLInputStatus(){
        if(quyViewTradingCommandActivityEditCommandContainerEnableTPSL.isChecked()){
            quyViewTradingCommandActivityEditCommandContainerTakeProfit.setEnabled(true);
            quyViewTradingCommandActivityEditCommandContainerStopLoss.setEnabled(true);
        }else{
            quyViewTradingCommandActivityEditCommandContainerTakeProfit.setEnabled(false);
            quyViewTradingCommandActivityEditCommandContainerStopLoss.setEnabled(false);
        }
    }

    private void loadData(){
        quyTradingCommandViewModel.details(tradingCommandId, new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                System.out.println(data);
                tradingCommandDetails = new Gson().fromJson(data, QuyTradingCommandResponseModel.TradingCommandDetails.class);
                System.out.println(tradingCommandDetails);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadCoinInfo();
                    }
                }).start();
            }
        });
    }
    private void reloadData(){
        quyTradingCommandViewModel.details(tradingCommandId, new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                tradingCommandDetails = new Gson().fromJson(data, QuyTradingCommandResponseModel.TradingCommandDetails.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(tradingCommandDetails.isOpen){
                            setOpenTradingCommandData();
                        }else{
                            setCloseTradingCommandData();
                        }
                    }
                });
            }
        });
    }

    private void setDetailsData(){
        if(tradingCommandDetails.isOpen){
            setOpenTradingCommandData();
        }else{
            setCloseTradingCommandData();
        }
        setInitCoinChartFragment();
        getSupportActionBar().setTitle(coin.name);
    }

    private void setOpenTradingCommandData(){
;       General.setImageUrl(getApplicationContext(),quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoIcon, coin.icon);
        quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoName.setText(coin.name);
        if(tradingCommandDetails.buyOrSell.equals("buy")){
            quyViewTradingCommandActivityOpenTradingCommandContainerBuyOrSellIcon.setBackgroundResource(R.drawable.binh_ic_arrow_up);
        }else{
            quyViewTradingCommandActivityOpenTradingCommandContainerBuyOrSellIcon.setBackgroundResource(R.drawable.binh_ic_arrow_down);
        }
        updateOpenProfitNow();
        quyViewTradingCommandActivityOpenTradingCommandContainerCommission.setText("Hoa hồng: $ "+(long)getCommission(tradingCommandDetails.moneyNumber,tradingCommandDetails.leverage,tradingCommandDetails.openTime));
        if(tradingCommandDetails.enableTpSl){
            quyViewTradingCommandActivityOpenTradingCommandContainerTakeProfit.setText("TP: "+(long)tradingCommandDetails.takeProfit);
            quyViewTradingCommandActivityOpenTradingCommandContainerStopLost.setText("SL: "+(long)tradingCommandDetails.stopLoss);
        }else{
            quyViewTradingCommandActivityOpenTradingCommandContainerTakeProfit.setText("");
            quyViewTradingCommandActivityOpenTradingCommandContainerStopLost.setText("");
        }
        quyViewTradingCommandActivityOpenTradingCommandContainer.setVisibility(View.VISIBLE);
        quyViewTradingCommandActivityCloseTradingCommandContainer.setVisibility(View.GONE);

        setEditContainer();
    }
    private void setEditContainer(){
        quyViewTradingCommandActivityEditCommandContainer.setVisibility(View.GONE);
        quyViewTradingCommandActivityEditCommandContainerOpenPrice.setText("$"+tradingCommandDetails.openPrice);
        quyViewTradingCommandActivityEditCommandContainerOpenTime.setText(General.convertTimeToDateTime(tradingCommandDetails.openTime));
        quyViewTradingCommandActivityEditCommandContainerValue.setText("$"+tradingCommandDetails.moneyNumber+" X "+tradingCommandDetails.leverage);
        
        quyViewTradingCommandActivityEditCommandContainerEnableTPSL.setChecked(tradingCommandDetails.enableTpSl);
        if(tradingCommandDetails.enableTpSl){
            quyViewTradingCommandActivityEditCommandContainerTakeProfit.setText(""+(long)tradingCommandDetails.takeProfit);
            quyViewTradingCommandActivityEditCommandContainerStopLoss.setText(""+(long)tradingCommandDetails.stopLoss);
        }else{
            quyViewTradingCommandActivityEditCommandContainerTakeProfit.setText("0");
            quyViewTradingCommandActivityEditCommandContainerStopLoss.setText("0");
        }
        updateEditCommandContainerTPSLInputStatus();
    }
    private void closeCommand(){
        closeCommandOpenVerifyPinAndContinue();
    }
    private void closeCommandOpenVerifyPinAndContinue(){
        QuyEditBinhVerifyPinDialog quyVerifyPinDialog = new QuyEditBinhVerifyPinDialog(this);
        quyVerifyPinDialog.setHandleCallback(new QuyEditBinhVerifyPinDialog.OkCallback() {
            @Override
            public void handle(String pin) {
                quyVerifyPinDialog.hide();
                quyTradingCommandViewModel.checkVerifyPin(pin, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeCommandOpenVerifyOtpAndContinue(pin);
                            }
                        });

                    }
                }, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyVerifyPinDialog.clearPin();
                                quyVerifyPinDialog.show();
                            }
                        });

                    }
                });
            }
        });
        quyVerifyPinDialog.show();
    }

    private void closeCommandOpenVerifyOtpAndContinue(String pin){
        QuyVerifyOtpDialog quyVerifyOtpDialog = new QuyVerifyOtpDialog(this);
        quyVerifyOtpDialog.setHandleCallback(new QuyVerifyOtpDialog.OkCallback() {
            @Override
            public void handle(String otp) {
                quyVerifyOtpDialog.hide();
                quyTradingCommandViewModel.close(otp, tradingCommandId, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reloadData();
                            }
                        });

                    }
                }, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyVerifyOtpDialog.show();
                            }
                        });
                    }
                });
            }
        });
        quyVerifyOtpDialog.setResendCallback(new QuyVerifyOtpDialog.OkCallback() {
            @Override
            public void handle(String otp) {
                quyVerifyOtpDialog.hide();
                quyTradingCommandViewModel.checkVerifyPin(pin, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyVerifyOtpDialog.clearAndFocus();
                                quyVerifyOtpDialog.show();
                            }
                        });

                    }
                }, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyVerifyOtpDialog.show();
                            }
                        });

                    }
                });
            }
        });
        quyVerifyOtpDialog.show();
    }

    private void editCommand(){
        editCommandCheckCommandAndContinue();
    }
    private void editCommandCheckCommandAndContinue(){
        boolean enableTpSl = quyViewTradingCommandActivityEditCommandContainerEnableTPSL.isChecked();
        long tp = 0L;
        long sl = 0L;
        if(quyViewTradingCommandActivityEditCommandContainerEnableTPSL.isChecked()){
            try{
                tp = Long.parseLong(quyViewTradingCommandActivityEditCommandContainerTakeProfit.getText().toString());
                sl = Long.parseLong(quyViewTradingCommandActivityEditCommandContainerStopLoss.getText().toString());
            }catch(Exception e){}
        }
        if(enableTpSl){
            if(tp<tradingCommandDetails.moneyNumber+profitNow-getCommission(tradingCommandDetails.moneyNumber,tradingCommandDetails.leverage,tradingCommandDetails.openTime)){
                General.showNotification(this,new SystemNotificationModel(SystemNotificationModel.Type.Warning,"Chốt lời không ít hơn giá trị hiện tại."));
                return;
            }
            if(sl>tradingCommandDetails.moneyNumber+profitNow-getCommission(tradingCommandDetails.moneyNumber,tradingCommandDetails.leverage,tradingCommandDetails.openTime)){
                General.showNotification(this,new SystemNotificationModel(SystemNotificationModel.Type.Warning,"Cắt lỗ không lớn hơn giá trị hiện tại."));
                return;
            }
        }
        editCommandOpenVerifyPinAndContinue();
    }

    private void editCommandOpenVerifyPinAndContinue(){
        QuyEditBinhVerifyPinDialog quyVerifyPinDialog = new QuyEditBinhVerifyPinDialog(this);
        quyVerifyPinDialog.setHandleCallback(new QuyEditBinhVerifyPinDialog.OkCallback() {
            @Override
            public void handle(String pin) {
                quyVerifyPinDialog.hide();
                quyTradingCommandViewModel.checkVerifyPin(pin, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editCommandOpenVerifyOtpAndContinue(pin);
                            }
                        });

                    }
                }, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyVerifyPinDialog.clearPin();
                                quyVerifyPinDialog.show();
                            }
                        });

                    }
                });
            }
        });
        quyVerifyPinDialog.show();
    }

    private void editCommandOpenVerifyOtpAndContinue(String pin){
        boolean enableTpSl = quyViewTradingCommandActivityEditCommandContainerEnableTPSL.isChecked();
        long tp = 0L;
        long sl = 0L;
        if(quyViewTradingCommandActivityEditCommandContainerEnableTPSL.isChecked()){
            try{
                tp = Long.parseLong(quyViewTradingCommandActivityEditCommandContainerTakeProfit.getText().toString());
                sl = Long.parseLong(quyViewTradingCommandActivityEditCommandContainerStopLoss.getText().toString());
            }catch(Exception e){}
        }

        QuyVerifyOtpDialog quyVerifyOtpDialog = new QuyVerifyOtpDialog(this);
        long finalTp = tp;
        long finalSl = sl;
        quyVerifyOtpDialog.setHandleCallback(new QuyVerifyOtpDialog.OkCallback() {
            @Override
            public void handle(String otp) {
                quyVerifyOtpDialog.hide();
                quyTradingCommandViewModel.edit(otp, tradingCommandId, enableTpSl, (float) finalTp, (float) finalSl, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reloadData();
                            }
                        });

                    }
                }, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyVerifyOtpDialog.show();
                            }
                        });
                    }
                });
            }
        });
        quyVerifyOtpDialog.setResendCallback(new QuyVerifyOtpDialog.OkCallback() {
            @Override
            public void handle(String otp) {
                quyVerifyOtpDialog.hide();
                quyTradingCommandViewModel.checkVerifyPin(pin, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyVerifyOtpDialog.clearAndFocus();
                                quyVerifyOtpDialog.show();
                            }
                        });

                    }
                }, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyVerifyOtpDialog.show();
                            }
                        });

                    }
                });
            }
        });

        quyVerifyOtpDialog.show();
    }


    private void setCloseTradingCommandData(){
        General.setImageUrl(getApplicationContext(),quyViewTradingCommandActivityCloseTradingCommandContainerCoinInfoIcon, coin.icon);
        quyViewTradingCommandActivityOpenTradingCommandContainerCoinInfoName.setText(coin.name);
        if(tradingCommandDetails.buyOrSell.equals("buy")){
            quyViewTradingCommandActivityCloseTradingCommandContainerBuyOrSellIcon.setBackgroundResource(R.drawable.binh_ic_arrow_up);
        }else{
            quyViewTradingCommandActivityCloseTradingCommandContainerBuyOrSellIcon.setBackgroundResource(R.drawable.binh_ic_arrow_down);
        }
        quyViewTradingCommandActivityCloseTradingCommandContainerCoinInfoName.setText(coin.name);

        if(tradingCommandDetails.finalProfit>0){
            quyViewTradingCommandActivityCloseTradingCommandContainerProfit.setText("$+"+String.format("%.2f", tradingCommandDetails.finalProfit));
            quyViewTradingCommandActivityCloseTradingCommandContainerProfit.setTextColor(Color.GREEN);
        }else{
            quyViewTradingCommandActivityCloseTradingCommandContainerProfit.setText("$"+String.format("%.2f", tradingCommandDetails.finalProfit));
            quyViewTradingCommandActivityCloseTradingCommandContainerProfit.setTextColor(Color.RED);
        }

        quyViewTradingCommandActivityCloseTradingCommandContainerSumValue.setText(""+(long)tradingCommandDetails.moneyNumber+" X "+tradingCommandDetails.leverage);
        quyViewTradingCommandActivityCloseTradingCommandContainerOpenPrice.setText("$"+String.format("%.2f", tradingCommandDetails.openPrice));
        quyViewTradingCommandActivityCloseTradingCommandContainerOpenTime.setText(General.convertTimeToDateTime(tradingCommandDetails.openTime));
        quyViewTradingCommandActivityCloseTradingCommandContainerClosePrice.setText("$"+String.format("%.2f", tradingCommandDetails.closePrice));
        quyViewTradingCommandActivityCloseTradingCommandContainerCloseTime.setText(General.convertTimeToDateTime(tradingCommandDetails.closeTime));
        quyViewTradingCommandActivityCloseTradingCommandContainerCommission.setText("Hoa hồng: $"+(long)tradingCommandDetails.commission);

        quyViewTradingCommandActivityCloseTradingCommandContainer.setVisibility(View.VISIBLE);
        quyViewTradingCommandActivityOpenTradingCommandContainer.setVisibility(View.GONE);

        quyViewTradingCommandActivityEditCommandContainer.setVisibility(View.GONE);
    }

    private void loadCoinInfo(){
        try {
            int i=0;
        while(true){
            System.out.println(i++);
            if(isBoundCoinService){
                coinService.getCoinById(tradingCommandDetails.coinId, new CoinService.GetOneWaitCallback() {
                    @Override
                    public void handle(CoinServiceModel.CoinNow coin) {
                        if(coin!=null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setCoinInfo(coin);
                                }
                            });
                        }
                    }
                });
                if(tradingCommandDetails.isOpen){
                    coinService.addEventListener(new ArrayList<>(Arrays.asList(tradingCommandDetails.coinId)), REGISTER_COIN_SERVICE_NAME,"coin-info", new CoinServiceModel.EventCallbackInterface() {
                        @Override
                        public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
                            updateCoinInfo(coins.get(0));
                        }
                    });
                }
                break;
            }

            Thread.sleep(500);
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void setCoinInfo(CoinServiceModel.CoinNow coin){
        this.coin = coin;
        setDetailsData();
    }
    private void updateCoinInfo(CoinServiceModel.CoinNow coin){
        if(coin.id.equals(tradingCommandDetails.coinId)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    QuyViewTradingCommandActivity.this.coin = coin;
                    updateOpenProfitNow();
                }
            });
        }
    }

    private long getCommission(float money, int leverage, long openTime){
        if(leverage==1)return 0L;
        return (long)(money*leverage*0.0005f*((System.currentTimeMillis()-openTime)/(1000*60*60*24)+1));
    }

    private void updateOpenProfitNow(){
        if(tradingCommandDetails.buyOrSell.equals("buy")){
            profitNow = (coin.priceUsd-tradingCommandDetails.openPrice)*tradingCommandDetails.coinNumber;
        }else{
            profitNow = (-coin.priceUsd+tradingCommandDetails.openPrice)*tradingCommandDetails.coinNumber;
        }
        System.out.println("profitNow");
        if(profitNow>0){
            quyViewTradingCommandActivityOpenTradingCommandContainerProfitNow.setText("$+"+String.format("%.2f", profitNow));
            quyViewTradingCommandActivityOpenTradingCommandContainerProfitNow.setTextColor(Color.GREEN);
        }else{
            quyViewTradingCommandActivityOpenTradingCommandContainerProfitNow.setText("$"+String.format("%.2f", profitNow));
            quyViewTradingCommandActivityOpenTradingCommandContainerProfitNow.setTextColor(Color.RED);
        }

    }


}