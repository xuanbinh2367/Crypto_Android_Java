package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.stream.Collectors;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.CoinServiceModel;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.QuyProfileResponseModel;
import example.txb.crypto_android.service.CoinService;
import example.txb.crypto_android.service.ServiceConnections;
import example.txb.crypto_android.service.ServiceCreatedCallback;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.BinhProfileViewModel;


public class BanEditMainActivityHomeFragment extends Fragment {
    private ImageView btnCommand;
    private ImageView btnRating;
    private ImageView btnEditName;
    private ImageView btnSafe;
    private ImageView btnSetting;
    private ImageView imgAvatar;
    private float originalScale;
    private BinhProfileViewModel profileViewModel;
    private TextView txtName, txtMoney, txtMoneyAvailable, txtMoneyInvested, txtCommandNumber, txtRatingNumber;
    QuyProfileResponseModel.Profile profileDetails;


    public BanEditMainActivityHomeFragment() {
    }


    private String REGISTER_COIN_SERVICE_NAME = "main-activity-home-fragment";
    private CoinService coinService;
    private Boolean isBoundCoinService=false;
    private ServiceConnection serviceConnection;
    private Context context;

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
            loadData();
        }
    }

    public BanEditMainActivityHomeFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
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
        View view = inflater.inflate(R.layout.ban_activity_home, container, false);
        btnCommand = view.findViewById(R.id.btnCommand);
        btnRating = view.findViewById(R.id.btnRating);
        btnEditName = view.findViewById(R.id.btnEditName);

        btnSafe = view.findViewById(R.id.btnSafe);
        btnSetting = view.findViewById(R.id.btnSetting);
        txtName = view.findViewById(R.id.txtName);
        txtMoney = view.findViewById(R.id.txtMoney);
        txtMoneyAvailable = view.findViewById(R.id.txtMoneyAvailable);
        txtMoneyInvested = view.findViewById(R.id.txtMoneyInvested);
        txtCommandNumber = view.findViewById(R.id.txtCommandNumber);
        txtRatingNumber = view.findViewById(R.id.txtRattingNumber);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        profileViewModel = new BinhProfileViewModel(getContext());

        loadData();
        setObserve();

        setButtonClickAnimation(btnCommand, new ButtonClickAnimationAction() {
            @Override
            public void action() {
                startActivity(new Intent(getActivity(), Ban_CommandActivity.class));
            }
        });
        setButtonClickAnimation(btnRating, new ButtonClickAnimationAction() {
            @Override
            public void action() {
                startActivity(new Intent(getActivity(), Binh_RankActivity.class));
            }
        });
        setButtonClickAnimation(btnSafe, new ButtonClickAnimationAction() {
            @Override
            public void action() {
                startActivity(new Intent(getActivity(), Binh_BalanceTransactionActivity.class));
            }
        });
        setButtonClickAnimation(btnSetting, new ButtonClickAnimationAction() {
            @Override
            public void action() {
                openSettingCallback.handle();
            }
        });
        setButtonClickAnimation(btnEditName, new ButtonClickAnimationAction() {
            @Override
            public void action() {
                editInfoNavigationObject.navigation();
            }
        });
        setButtonClickAnimation(imgAvatar, new ButtonClickAnimationAction() {
            @Override
            public void action() {
                editInfoNavigationObject.navigation();
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        profileViewModel = new BinhProfileViewModel(getContext());
        setObserve();

        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(context, CoinService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setProfile() {
        int betterPercentNumber = 100 * (profileDetails.totalNumber - profileDetails.topNumber) / profileDetails.totalNumber;
        //
        General.setAvatarUrl(getContext(), imgAvatar, profileDetails.avatar);
        txtName.setText(profileDetails.name);
        txtMoney.setText("$" + String.format("%.2f",(profileDetails.moneyProfitNow+profileDetails.moneyNow+profileDetails.moneyInvested)/1000f)+" K");
        txtMoneyInvested.setText("$" + String.format("%.2f",profileDetails.moneyInvested/1000f)+" K");
        txtMoneyAvailable.setText("$" + String.format("%.2f",profileDetails.moneyNow/1000f)+" K");

        txtRatingNumber.setText(">" + betterPercentNumber + "%");
        txtCommandNumber.setText("" + profileDetails.tradingCommandNumber);
    }

    public void loadData() {
        profileViewModel.getInfo("mine", new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        profileDetails = new Gson().fromJson(data, QuyProfileResponseModel.Profile.class);
                        setProfile();
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
        });
    }


    public void setObserve() {
        profileViewModel.notification().observe(getActivity(), new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel systemNotificationModel) {
                if (systemNotificationModel != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            General.showNotification(getContext(), systemNotificationModel);
                        }
                    });
                }
            }
        });
    }

    public static interface EditInfoNavigation{
        public void navigation();
    }

    private EditInfoNavigation editInfoNavigationObject;

    public void setEditInfoNavigation(EditInfoNavigation editInfoNavigation) {
        this.editInfoNavigationObject = editInfoNavigation;
    }

    private interface ButtonClickAnimationAction {
        public void action();
    }

    private void setButtonClickAnimation(ImageView button, ButtonClickAnimationAction action) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a scale animation
                Animation anim = new ScaleAnimation(
                        1f, 0.6f, // Start and end values for the X axis scaling
                        1f, 0.6f, // Start and end values for the Y axis scaling
                        Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                        Animation.RELATIVE_TO_SELF, 0.5f // Pivot point of Y scaling
                );

                anim.setFillAfter(true); // Keeps the result of the animation
                anim.setDuration(200); // Duration of the animation in milliseconds

                // Set the animation listener to handle the animation end
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // Do something when the animation ends (e.g., start a new activity)
                        button.startAnimation(getScaleAnimation(0.6f, 1f)); // Phóng to lại
                        action.action();

                    }

                    private Animation getScaleAnimation(float fromScale, float toScale) {
                        Animation anim = new ScaleAnimation(
                                fromScale, toScale, // Start and end values for the X axis scaling
                                fromScale, toScale, // Start and end values for the Y axis scaling
                                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                                Animation.RELATIVE_TO_SELF, 0.5f // Pivot point of Y scaling
                        );
                        anim.setFillAfter(true);
                        anim.setDuration(200);
                        return anim;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                // Start the animation
                button.startAnimation(anim);
            }
        });
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtMoney.setText("$ "+String.format("%.2f",(profileDetails.moneyNow+profileDetails.moneyInvested+profileDetails.moneyProfitNow)/1000f)+" K");
            }
        });
    }

    QuyMainActivity.OpenSettingCallback openSettingCallback;

    public void setOpenSettingCallbackObject(QuyMainActivity.OpenSettingCallback openSettingCallback) {
        this.openSettingCallback = openSettingCallback;
    }
}