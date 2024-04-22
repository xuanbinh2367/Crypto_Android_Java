package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.QuyProfileResponseModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.view.custom_dialog.QuyEditBinhVerifyPinDialog;
import example.txb.crypto_android.view.custom_dialog.QuyVerifyOtpDialog;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.QuyProfileViewModel;
import example.txb.crypto_android.view_model.QuyTradingCommandViewModel;

public class QuyMainActivityTradingFragment extends Fragment {
    FragmentContainerView coinChartFragmentView;
    QuyCoinChartFragment coinChartFragment;
    TextView miniInfoSumMoney, miniInfoReadyMoney, miniInfoTradingCommandNumber;
    Context context;
    private QuyMainActivity.InterestedCoinsChange interestedCoinsChange;
    private QuyMainActivity.ReloadProfile reloadProfileObject;

    public void setReloadProfileObject(QuyMainActivity.ReloadProfile reloadProfileObject) {
        this.reloadProfileObject = reloadProfileObject;
    }

    TextView quyMainActivityTradingFragmentCoinInfoName;
    ImageView quyMainActivityTradingFragmentCoinInfoIcon;
    TextView quyMainActivityTradingFragmentCoinInfoPrice;
    TextView quyMainActivityTradingFragmentCoinInfoChange24h;
    Button quyMainActivityTradingFragmentCoinInfoSellBtn;
    Button quyMainActivityTradingFragmentCoinInfoBuyBtn;
    LinearLayout quyMainActivityTradingFragmentCreateCommandContainer;
    View quyMainActivityTradingFragmentCreateCommandContainerCloseView;
    TextView quyMainActivityTradingFragmentCreateCommandContainerSwitchSellLabel;
    Switch quyMainActivityTradingFragmentCreateCommandContainerSwitchBuySell;
    TextView quyMainActivityTradingFragmentCreateCommandContainerSwitchBuyLabel;
    EditText quyMainActivityTradingFragmentCreateCommandContainerInputMoney;
    SeekBar quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney;
    Spinner quyMainActivityTradingFragmentCreateCommandContainerLeverage;
    EditText quyMainActivityTradingFragmentCreateCommandContainerTakeProfit;
    Switch quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL;
    EditText quyMainActivityTradingFragmentCreateCommandContainerStopLoss;
    TextView quyMainActivityTradingFragmentCreateCommandContainerSumMoney;
    TextView quyMainActivityTradingFragmentCreateCommandContainerCommission;
    View quyMainActivityTradingFragmentCreateCommandContainerOpenCommandButton;
    ImageView quyMainActivityTradingFragmentInterestedIcon;
    LinearLayout loadingLayout;

    ImageView quyMainActivityTradingFragmentMiniInfoSumIcon;
    LinearLayout quyMainActivityTradingFragmentMiniInfoSumLayout;
    ImageView quyMainActivityTradingFragmentMiniInfoReadyMoneyIcon;
    LinearLayout quyMainActivityTradingFragmentMiniInfoReadyMoneyLayout;
    ImageView quyMainActivityTradingFragmentMiniInfoTradingCommandNumberIcon;
    LinearLayout quyMainActivityTradingFragmentMiniInfoTradingCommandNumberLayout;


    QuyProfileResponseModel.MiniProfile miniProfile;

    QuyProfileViewModel quyProfileViewModel;
    QuyTradingCommandViewModel quyTradingCommandViewModel;

    ActivityResultLauncher<Intent> changeCoinLauncher;

    private String REGISTER_COIN_SERVICE_NAME = "main-activity-trading-fragment";
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
            loadCoinInfo();
        }
    }

    private String coinId = "bitcoin";
    QuyProfileResponseModel.InterestedCoins interestedCoins;

    public QuyMainActivityTradingFragment(Context context,ActivityResultLauncher<Intent> changeCoinLauncher, QuyMainActivity.InterestedCoinsChange interestedCoinsChange) {
        this.context = context;
        this.interestedCoinsChange = interestedCoinsChange;
        this.changeCoinLauncher = changeCoinLauncher;
        quyProfileViewModel = new QuyProfileViewModel(context);
        quyTradingCommandViewModel = new QuyTradingCommandViewModel(context);

        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(context, CoinService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public void changeCoinChartView(String coinId){
        this.coinId = coinId;
        loadCoinInfo();
        coinChartFragment.changeCoinView(coinId);
        setInterestedCoins();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBoundCoinService) {
            coinService.removeEventListener(REGISTER_COIN_SERVICE_NAME);
            context.unbindService(serviceConnection);
            isBoundCoinService = false;
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quy_main_activity_trading, container, false);
        coinChartFragmentView =view.findViewById(R.id.quyMainActivityTradingFragmentCoinChartFragmentContainer);
        miniInfoSumMoney = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoSum);
        miniInfoReadyMoney = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoReadyMoney);
        miniInfoTradingCommandNumber = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoTradingCommandNumber);

        quyMainActivityTradingFragmentCoinInfoName = view.findViewById(R.id.quyMainActivityTradingFragmentCoinInfoName);
        quyMainActivityTradingFragmentCoinInfoIcon = view.findViewById(R.id.quyMainActivityTradingFragmentCoinInfoIcon);
        quyMainActivityTradingFragmentCoinInfoPrice = view.findViewById(R.id.quyMainActivityTradingFragmentCoinInfoPrice);
        quyMainActivityTradingFragmentCoinInfoChange24h = view.findViewById(R.id.quyMainActivityTradingFragmentCoinInfoChange24h);
        quyMainActivityTradingFragmentCoinInfoSellBtn = view.findViewById(R.id.quyMainActivityTradingFragmentCoinInfoSellBtn);
        quyMainActivityTradingFragmentCoinInfoBuyBtn = view.findViewById(R.id.quyMainActivityTradingFragmentCoinInfoBuyBtn);
        quyMainActivityTradingFragmentCreateCommandContainer = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainer);
        quyMainActivityTradingFragmentCreateCommandContainerCloseView = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerCloseView);
        quyMainActivityTradingFragmentCreateCommandContainerSwitchSellLabel = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerSwitchSellLabel);
        quyMainActivityTradingFragmentCreateCommandContainerSwitchBuySell = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerSwitchBuySell);
        quyMainActivityTradingFragmentCreateCommandContainerSwitchBuyLabel = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerSwitchBuyLabel);
        quyMainActivityTradingFragmentCreateCommandContainerInputMoney = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerInputMoney);
        quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney);
        quyMainActivityTradingFragmentCreateCommandContainerLeverage = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerLeverage);
        quyMainActivityTradingFragmentCreateCommandContainerTakeProfit = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerTakeProfit);
        quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL);
        quyMainActivityTradingFragmentCreateCommandContainerStopLoss = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerStopLoss);
        quyMainActivityTradingFragmentCreateCommandContainerSumMoney = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerSumMoney);
        quyMainActivityTradingFragmentCreateCommandContainerCommission = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerCommission);
        quyMainActivityTradingFragmentCreateCommandContainerOpenCommandButton = view.findViewById(R.id.quyMainActivityTradingFragmentCreateCommandContainerOpenCommandButton);

        quyMainActivityTradingFragmentInterestedIcon = view.findViewById(R.id.quyMainActivityTradingFragmentInterestedIcon);

        quyMainActivityTradingFragmentMiniInfoSumIcon = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoSumIcon);
        quyMainActivityTradingFragmentMiniInfoSumLayout = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoSumLayout);
        quyMainActivityTradingFragmentMiniInfoReadyMoneyIcon = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoReadyMoneyIcon);
        quyMainActivityTradingFragmentMiniInfoReadyMoneyLayout = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoReadyMoneyLayout);
        quyMainActivityTradingFragmentMiniInfoTradingCommandNumberIcon = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoTradingCommandNumberIcon);
        quyMainActivityTradingFragmentMiniInfoTradingCommandNumberLayout = view.findViewById(R.id.quyMainActivityTradingFragmentMiniInfoTradingCommandNumberLayout);

        loadingLayout = view.findViewById(R.id.loadingLayout);

        coinChartFragment = QuyCoinChartFragment.newInstance(this.coinId, 0L, context);
        coinChartFragment.setChangeCoinLauncher(changeCoinLauncher);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Replace the current fragment with the new one
        transaction.replace(R.id.quyMainActivityTradingFragmentCoinChartFragmentContainer, coinChartFragment);
        // Add the transaction to the back stack (optional)
//        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commitAllowingStateLoss();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setInitView();
        setEvents();
        setObserve();
        loadMiniProfile();
    }

    private void setInitView(){
        String leverageLabel[] = {"1","5","8","10","13","20"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, leverageLabel);
        quyMainActivityTradingFragmentCreateCommandContainerLeverage.setAdapter(adapter);
        quyMainActivityTradingFragmentCreateCommandContainerLeverage.setSelection(0);
        quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney.setMin(0);
        quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney.setMax(100);
    }

    public void setObserve(){
        quyProfileViewModel.notification().observe(getActivity(), new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel systemNotificationModel) {
                if(systemNotificationModel!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            General.showNotification(getContext(),systemNotificationModel);
                        }
                    });
                }
            }
        });
        quyProfileViewModel.isLoading().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                getActivity().runOnUiThread(new Runnable() {
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
        quyTradingCommandViewModel.notification().observe(getActivity(), new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel systemNotificationModel) {
                if(systemNotificationModel!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            General.showNotification(getContext(),systemNotificationModel);
                        }
                    });
                }
            }
        });
        quyTradingCommandViewModel.isLoading().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                getActivity().runOnUiThread(new Runnable() {
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
        quyMainActivityTradingFragmentCreateCommandContainerCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quyMainActivityTradingFragmentCreateCommandContainer.setVisibility(View.GONE);
            }
        });
        quyMainActivityTradingFragmentCoinInfoSellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateCommandContainer(false);
            }
        });
        quyMainActivityTradingFragmentCoinInfoBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateCommandContainer(true);
            }
        });
        quyMainActivityTradingFragmentCreateCommandContainerSwitchBuySell.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    quyMainActivityTradingFragmentCreateCommandContainerSwitchSellLabel.setTextColor(Color.GRAY);
                    quyMainActivityTradingFragmentCreateCommandContainerSwitchBuyLabel.setTextColor(Color.parseColor("#3bd391"));
                }else{
                    quyMainActivityTradingFragmentCreateCommandContainerSwitchBuyLabel.setTextColor(Color.GRAY);
                    quyMainActivityTradingFragmentCreateCommandContainerSwitchSellLabel.setTextColor(Color.parseColor("#FF3333"));
                }
            }
        });
        quyMainActivityTradingFragmentCreateCommandContainerOpenCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCommandAndContinue();
            }
        });
        quyMainActivityTradingFragmentCreateCommandContainerLeverage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTempSumMoney();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setTPSLInputStatus();
            }
        });
        quyMainActivityTradingFragmentInterestedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleInterestedCoin();
            }
        });
        quyMainActivityTradingFragmentCoinInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, QuyCoinListActivity.class);
                changeCoinLauncher.launch(intent);
            }
        });
        quyMainActivityTradingFragmentMiniInfoSumIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumMoneyOpen();
            }
        });
        quyMainActivityTradingFragmentMiniInfoSumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumMoneyOpen();
            }
        });
        quyMainActivityTradingFragmentMiniInfoReadyMoneyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumMoneyOpen();
            }
        });
        quyMainActivityTradingFragmentMiniInfoReadyMoneyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumMoneyOpen();
            }
        });
        quyMainActivityTradingFragmentMiniInfoTradingCommandNumberIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandNumberOpen();
            }
        });
        quyMainActivityTradingFragmentMiniInfoTradingCommandNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commandNumberOpen();
            }
        });

    }

    TextWatcher quyMainActivityTradingFragmentCreateCommandContainerInputMoneyTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
            long money=0;
            try{
                money = Long.parseLong(editable.toString());
            }catch (Exception e){}
            long maximumMoney = (long) (miniProfile.moneyNow*0.99f);
            if(money>maximumMoney){
                quyMainActivityTradingFragmentCreateCommandContainerInputMoney.setText(""+maximumMoney);
            }
            if(quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney.getProgress()!=(int)(100*money/maximumMoney)){
                quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney.setProgress((int)(100*money/maximumMoney));
            }
            setTempSumMoney();
        }
    };
    private void setConstrainEvents(){
        quyMainActivityTradingFragmentCreateCommandContainerInputMoney.removeTextChangedListener(quyMainActivityTradingFragmentCreateCommandContainerInputMoneyTextWatcher);
        quyMainActivityTradingFragmentCreateCommandContainerInputMoney.addTextChangedListener(quyMainActivityTradingFragmentCreateCommandContainerInputMoneyTextWatcher);
        quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                long money=0L;
                try{
                    money = Long.parseLong(quyMainActivityTradingFragmentCreateCommandContainerInputMoney.getText().toString());
                }catch (Exception e){}
                if(money!=(long)(seekBar.getProgress()/100f*0.99f*miniProfile.moneyNow)){
                    quyMainActivityTradingFragmentCreateCommandContainerInputMoney.setText((long)(seekBar.getProgress()/100f*0.99f*miniProfile.moneyNow)+"");
                }
                setTempSumMoney();
            }
        });
    }
    private void resetInputValueGeneral(){
        System.out.println(" void resetInputValueGeneral(){");
        quyMainActivityTradingFragmentCreateCommandContainerLeverage.setSelection(0);
        quyMainActivityTradingFragmentCreateCommandContainerInputMoney.setText("0");
        quyMainActivityTradingFragmentCreateCommandContainerSeekBarMoney.setProgress(0);
        quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL.setChecked(false);
        quyMainActivityTradingFragmentCreateCommandContainerTakeProfit.setText("0");
        quyMainActivityTradingFragmentCreateCommandContainerStopLoss.setText("0");
        setTPSLInputStatus();
        setTempSumMoney();
    }
    private void openCreateCommandContainer(boolean isBuy){
        System.out.println("teCommandContainer(boolean isBuy){");
        quyMainActivityTradingFragmentCreateCommandContainerSwitchBuySell.setChecked(isBuy);
        resetInputValueGeneral();
        quyMainActivityTradingFragmentCreateCommandContainer.setVisibility(View.VISIBLE);
    }

    private void setTPSLInputStatus(){
        if(quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL.isChecked()){
            quyMainActivityTradingFragmentCreateCommandContainerTakeProfit.setEnabled(true);
            quyMainActivityTradingFragmentCreateCommandContainerStopLoss.setEnabled(true);
        }else{
            quyMainActivityTradingFragmentCreateCommandContainerTakeProfit.setEnabled(false);
            quyMainActivityTradingFragmentCreateCommandContainerStopLoss.setEnabled(false);
        }
    }
    private void setTempSumMoney(){
        long money = 0L;
        int leverage = 1;
        long commission = 0L;
        try{
            leverage = Integer.parseInt(quyMainActivityTradingFragmentCreateCommandContainerLeverage.getSelectedItem().toString());
            money = Long.parseLong(quyMainActivityTradingFragmentCreateCommandContainerInputMoney.getText().toString());
            if(leverage!=1){
                commission = getCommission(money,leverage);//0.05%
            }
        }catch(Exception e){}
        quyMainActivityTradingFragmentCreateCommandContainerSumMoney.setText("Khối lượng: "+money*leverage);
        quyMainActivityTradingFragmentCreateCommandContainerCommission.setText("Hoa hồng: "+commission+"$");
    }

    private class LoadMiniProfileOk implements BaseViewModel.OkCallback{
        @Override
        public void handle(String data) {
            try{
                miniProfile = new Gson().fromJson(data,QuyProfileResponseModel.MiniProfile.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setMiniInfo();
                    }
                });

                ArrayList<String> coinIds = new ArrayList<>(miniProfile.openCommandItems.stream().map(e->e.coinId).collect(Collectors.toList()));
                if(coinIds.size()!=0){
                    coinService.addEventListener(coinIds, REGISTER_COIN_SERVICE_NAME,"mini-profile", new CoinServiceModel.EventCallbackInterface() {
                        @Override
                        public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
                            updateMiniInfoSumMoney(coins);
                        }
                    });
                }
                setConstrainEvents();
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }
    public void loadMiniProfile(){
        quyProfileViewModel.getMiniInfo(new LoadMiniProfileOk());
    }
    private void setMiniInfo(){
        miniInfoSumMoney.setText("$ "+String.format("%.2f",((miniProfile.moneyNow+miniProfile.moneyInvested+miniProfile.moneyProfitNow)/1000f))+" K");
        miniInfoReadyMoney.setText("$ "+String.format("%.2f",((miniProfile.moneyNow)/1000f))+" K");
        miniInfoTradingCommandNumber.setText(miniProfile.openTradingCommandNumber+"");
        loadInterestedCoinStatus();
    }
    private void updateMiniInfoSumMoney(ArrayList<CoinServiceModel.CoinNow> coins){
        miniProfile.moneyProfitNow = 0f;
        for (int i = 0; i < miniProfile.openCommandItems.size(); i++) {
            int indCoin = -1;
            for (int j = 0; j < coins.size(); j++) {
                if(coins.get(j).id.equals(miniProfile.openCommandItems.get(i).coinId)){
                    indCoin=j;
                    break;
                }
            }
            if(indCoin!=-1){
                miniProfile.moneyProfitNow+=(coins.get(indCoin).priceUsd-miniProfile.openCommandItems.get(i).openPrice)*miniProfile.openCommandItems.get(i).coinNumber*(miniProfile.openCommandItems.get(i).buyOrSell.equals("buy")?1f:-1f);
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                miniInfoSumMoney.setText("$ "+String.format("%.2f",((miniProfile.moneyNow+miniProfile.moneyInvested+miniProfile.moneyProfitNow)/1000f))+" K");
            }
        });
    }

    private void loadInterestedCoinStatus(){
        quyProfileViewModel.getInterestedCoins(new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                interestedCoins = new Gson().fromJson(data,QuyProfileResponseModel.InterestedCoins.class);
                setInterestedCoins();
            }
        });
    }

    public void setInterestedCoins() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(interestedCoins!=null){
                    if(interestedCoins.items.contains(QuyMainActivityTradingFragment.this.coinId)){
                        quyMainActivityTradingFragmentInterestedIcon.setBackgroundResource(R.drawable.quy_heart_fill);
                    }else{
                        quyMainActivityTradingFragmentInterestedIcon.setBackgroundResource(R.drawable.quy_heart);
                    }
                }
            }
        });
    }
    public void toggleInterestedCoin(){
        quyProfileViewModel.toggleInterestedCoin(this.coinId, new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                if(interestedCoins.items.contains(QuyMainActivityTradingFragment.this.coinId)){
                    interestedCoins.items.remove(QuyMainActivityTradingFragment.this.coinId);
                }else{
                    interestedCoins.items.add(QuyMainActivityTradingFragment.this.coinId);
                }
                setInterestedCoins();
                interestedCoinsChange.setStatus(true);
            }
        });
    }

    private void loadCoinInfo(){
        coinService.getCoinById(this.coinId, new CoinService.GetOneWaitCallback() {
            @Override
            public void handle(CoinServiceModel.CoinNow coin) {
                if(coin!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCoinInfo(coin);
                        }
                    });
                }
            }
        });
        coinService.addEventListener(new ArrayList<>(Arrays.asList(this.coinId)), REGISTER_COIN_SERVICE_NAME,"coin-info", new CoinServiceModel.EventCallbackInterface() {
            @Override
            public void handle(ArrayList<CoinServiceModel.CoinNow> coins) {
//                for (int i = 0; i < coins.size(); i++) {
//                    updateCoinInfo(coins.get(i));
//                }
                updateCoinInfo(coins.get(0));
            }
        });

    }
    private void setCoinInfo(CoinServiceModel.CoinNow coin){
        quyMainActivityTradingFragmentCoinInfoName.setText(coin.name);
        General.setImageUrl(context,quyMainActivityTradingFragmentCoinInfoIcon,coin.icon);
        quyMainActivityTradingFragmentCoinInfoPrice.setText("$ "+String.format("%.2f", coin.priceUsd));
        if(coin.changePercent24Hr>=0){
            quyMainActivityTradingFragmentCoinInfoChange24h.setTextColor(Color.GREEN);
            quyMainActivityTradingFragmentCoinInfoChange24h.setText("+"+String.format("%.2f", coin.changePercent24Hr)+"%");
        }else{
            quyMainActivityTradingFragmentCoinInfoChange24h.setTextColor(Color.RED);
            quyMainActivityTradingFragmentCoinInfoChange24h.setText(String.format("%.2f", coin.changePercent24Hr)+"%");
        }
    }
    private void updateCoinInfo(CoinServiceModel.CoinNow coin){
        if(coin.id.equals(this.coinId)){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    quyMainActivityTradingFragmentCoinInfoPrice.setText("$ "+String.format("%.2f", coin.priceUsd));
                    if(coin.changePercent24Hr>=0){
                        quyMainActivityTradingFragmentCoinInfoChange24h.setTextColor(Color.GREEN);
                        quyMainActivityTradingFragmentCoinInfoChange24h.setText("+"+String.format("%.2f", coin.changePercent24Hr)+"%");
                    }else{
                        quyMainActivityTradingFragmentCoinInfoChange24h.setTextColor(Color.RED);
                        quyMainActivityTradingFragmentCoinInfoChange24h.setText(""+String.format("%.2f", coin.changePercent24Hr)+"%");
                    }
                }
            });
        }
    }

    private long getCommission(long money, int leverage){
        if(leverage==1)return 0L;
        return (long)(money*leverage*0.0005f);
    }
    private void checkCommandAndContinue(){
        long money = 0L;
        int leverage = 1;
        long commission = 0;
        try{
            leverage = Integer.parseInt(quyMainActivityTradingFragmentCreateCommandContainerLeverage.getSelectedItem().toString());
            money = Long.parseLong(quyMainActivityTradingFragmentCreateCommandContainerInputMoney.getText().toString());
            if(leverage!=1){
                commission = getCommission(money,leverage);//0.01%
            }
        }catch(Exception e){}

        if(money==0L){
            General.showNotification(getContext(),new SystemNotificationModel(SystemNotificationModel.Type.Warning,"Chưa nhập số tiền."));
            return;
        }else if(money<10L){
            General.showNotification(getContext(),new SystemNotificationModel(SystemNotificationModel.Type.Warning,"Số tiền không ít hơn 10$."));
            return;
        }

        if(quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL.isChecked()){
            long tp = 0L;
            long sl = 0L;
            try{
                tp = Long.parseLong(quyMainActivityTradingFragmentCreateCommandContainerTakeProfit.getText().toString());
                sl = Long.parseLong(quyMainActivityTradingFragmentCreateCommandContainerStopLoss.getText().toString());
            }catch(Exception e){}
            if(tp<money-commission){
                General.showNotification(getContext(),new SystemNotificationModel(SystemNotificationModel.Type.Warning,"Chốt lời không ít hơn giá trị hiện tại."));
                return;
            }
            if(sl>money-commission){
                General.showNotification(getContext(),new SystemNotificationModel(SystemNotificationModel.Type.Warning,"Cắt lỗ không lớn hơn giá trị hiện tại."));
                return;
            }
        }
        checkTracePinStatusAndContinue();
    }
    private void checkTracePinStatusAndContinue(){
        quyTradingCommandViewModel.checkTradePinStatus(new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(data.equals("true")){
                            openVerifyPinAndContinue();
//                            openVerifyOtpAndContinue();
                        }else{
                            General.showNotification(getContext(),new SystemNotificationModel(SystemNotificationModel.Type.Warning,"Bạn chưa thiết lập mã pin giao dịch. Hãy vào hồ sơ > cài đặt để thiết lập."));
                        }
                    }
                });

            }
        });
    }

    private void openVerifyPinAndContinue(){

        QuyEditBinhVerifyPinDialog verifyPinDialog = new QuyEditBinhVerifyPinDialog(getContext());
        verifyPinDialog.setHandleCallback(new QuyEditBinhVerifyPinDialog.OkCallback() {
            @Override
            public void handle(String pin) {
                verifyPinDialog.hide();
                quyTradingCommandViewModel.checkVerifyPin(pin, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                openVerifyOtpAndContinue(pin);
                            }
                        });

                    }
                }, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                verifyPinDialog.clearPin();
                                verifyPinDialog.show();
                            }
                        });

                    }
                });
            }
        });
        verifyPinDialog.show();
    }

    private void openVerifyOtpAndContinue(String pin){
        String buyOrSell = quyMainActivityTradingFragmentCreateCommandContainerSwitchBuySell.isChecked()?"buy":"sell";
        long money = 0L;
        int leverage = 1;
        try{
            leverage = Integer.parseInt(quyMainActivityTradingFragmentCreateCommandContainerLeverage.getSelectedItem().toString());
            money = Long.parseLong(quyMainActivityTradingFragmentCreateCommandContainerInputMoney.getText().toString());
        }catch(Exception e){}

        boolean enableTpSl = quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL.isChecked();
        long tp = 0L;
        long sl = 0L;
        if(quyMainActivityTradingFragmentCreateCommandContainerEnableTPSL.isChecked()){
            try{
                tp = Long.parseLong(quyMainActivityTradingFragmentCreateCommandContainerTakeProfit.getText().toString());
                sl = Long.parseLong(quyMainActivityTradingFragmentCreateCommandContainerStopLoss.getText().toString());
            }catch(Exception e){}
        }

        QuyVerifyOtpDialog quyVerifyOtpDialog = new QuyVerifyOtpDialog(getContext());
        long finalMoney = money;
        int finalLeverage = leverage;
        long finalTp = tp;
        long finalSl = sl;
        quyVerifyOtpDialog.setHandleCallback(new QuyVerifyOtpDialog.OkCallback() {
            @Override
            public void handle(String otp) {
                quyVerifyOtpDialog.hide();
                quyTradingCommandViewModel.openCommand(otp, buyOrSell, coinId, (float) finalMoney, finalLeverage, enableTpSl, (float) finalTp, (float) finalSl, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                quyMainActivityTradingFragmentCreateCommandContainer.setVisibility(View.GONE);
                                loadMiniProfile();
                                reloadProfileObject.reload();
                            }
                        });

                    }
                }, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        getActivity().runOnUiThread(new Runnable() {
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
                        getActivity().runOnUiThread(new Runnable() {
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
                        getActivity().runOnUiThread(new Runnable() {
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

    public void sumMoneyOpen(){
        Intent intent = new Intent(getActivity(), Binh_BalanceTransactionActivity.class);
        startActivity(intent);
    }
//    public void moneyNowOpen(){
//        Intent intent = new Intent(getActivity(), Binh_ProfileActivity.class);
//        startActivity(intent);
//    }
    public void commandNumberOpen(){
        Intent intent = new Intent(getActivity(), Ban_CommandActivity.class);
        startActivity(intent);
    }
}