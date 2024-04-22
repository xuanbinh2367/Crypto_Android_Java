package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

public class BinhMainActivityProfileFragment extends Fragment {

    private CardView cardBrief;
    private TextView txtSolenh;
    private TextView txtPercent;
    private SeekBar seekBar;
    private ImageView userImageView;
    private ImageView dialogImageView;
    private ImageButton changeImageButton;
    private int solenhValue = 0;
    public static int selectImageChooseOption = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private PieChart pieChart;
    private TextView txtBriefIncome, txtBriefInvested, txtBriefAvailable;
    private TextView txtBriefMoneyMax, txtBriefMoneyAverage, txtBriefMoneyProfitMax, txtBriefMoneyLosstMax;
    private TextView txtBriefRate;

    private ImageView btnEditName;
    private ImageView btnSetting;
    private TextView txtBriefUserName;

    private BinhProfileViewModel profileViewModel;
    private String userId;
    QuyProfileResponseModel.Profile profileDetails;
    LinearLayout loadingLayout;

    private QuyMainActivity.ReloadProfile reloadProfileObject;

    public void setReloadProfileObject(QuyMainActivity.ReloadProfile reloadProfileObject) {
        this.reloadProfileObject = reloadProfileObject;
    }

    //
    ImageView btnUserRank;
    public int betterPercent;

    private String REGISTER_COIN_SERVICE_NAME = "main-activity-profile-fragment";
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


    public BinhMainActivityProfileFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        // percent
        txtPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Trước khi thay đổi văn bản
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Trong quá trình thay đổi văn bản
//                updatePercentAndSeekBar();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Sau khi thay đổi văn bản
                updatePercentAndSeekBar();
            }
        });

        //Click image
        cardBrief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog();
            }
        });

        //   updatePieChart();
        editUserName();

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettingCallback.handle();
            }
        });

        profileViewModel = new BinhProfileViewModel(getContext());

        //
        setObserve();
//        loadData();

        //
        btnUserRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Binh_RankActivity.class);
                intent.putExtra("betterPercent", betterPercent);
                startActivity(intent);
            }
        });

        CoinServiceCreatedCallback serviceCreatedCallback = new CoinServiceCreatedCallback();
        serviceConnection = new ServiceConnections.CoinServiceConnection(serviceCreatedCallback);
        Intent intent = new Intent(context, CoinService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
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

        View view = inflater.inflate(R.layout.binh_activity_brief_user, container, false);
        loadingLayout = view.findViewById(R.id.loadingLayout);

        seekBar = view.findViewById(R.id.seekbar_brief_percent);
        seekBar.setEnabled(false);
        userImageView = view.findViewById(R.id.image_brief_user);


        btnSetting = view.findViewById(R.id.btn_brief_setting);

        // pie
        pieChart = view.findViewById(R.id.pieChart);

        //editname
        btnEditName = view.findViewById(R.id.btn_brief_edit_name);
        txtBriefUserName = view.findViewById(R.id.txt_brief_user_name);

        //
        txtBriefIncome = view.findViewById(R.id.txt_brief_income);
        txtBriefAvailable = view.findViewById(R.id.txt_brief_available);
        txtBriefInvested = view.findViewById(R.id.txt_brief_invested);
        txtSolenh = view.findViewById(R.id.txt_brief_solenh);
        txtPercent = view.findViewById(R.id.txt_brief_percent);

        //
        txtBriefMoneyMax = view.findViewById(R.id.txt_brief_money_max);
        txtBriefMoneyProfitMax = view.findViewById(R.id.txt_brief_profit_max);
        txtBriefMoneyLosstMax = view.findViewById(R.id.txt_brief_money_loss_max);
        txtBriefMoneyAverage = view.findViewById(R.id.txt_brief_money_average);
        //
        txtBriefRate = view.findViewById(R.id.txt__brief_rate);

        cardBrief = view.findViewById(R.id.cardView_brief_user_image);

        //
        btnUserRank = view.findViewById(R.id.btn_brief_rank);

        view.findViewById(R.id.btnViewCommand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Ban_CommandActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    private void setProfile() {
        betterPercent = 100 * (profileDetails.totalNumber - profileDetails.topNumber) / profileDetails.totalNumber;
        //
        General.setAvatarUrl(getContext(), userImageView, profileDetails.avatar);
        txtBriefUserName.setText(profileDetails.name);
        txtBriefIncome.setText("$" + String.format("%.2f", profileDetails.moneyProfitNow));

        txtBriefInvested.setText("$" + String.format("%.2f", profileDetails.moneyInvested));
        txtBriefAvailable.setText("$" + String.format("%.2f", profileDetails.moneyNow));

        txtBriefRate.setText(">" + 100 * (profileDetails.totalNumber - profileDetails.topNumber) / profileDetails.totalNumber + "%");

        txtBriefMoneyAverage.setText("$" + String.format("%.2f", profileDetails.tradingCommandMoneyAvg));
        txtBriefMoneyMax.setText("$" + String.format("%.2f", profileDetails.tradingCommandMoneyMaximum));
        txtBriefMoneyProfitMax.setText("$" + String.format("%.2f", profileDetails.tradingCommandProfitMaximum));
        txtBriefMoneyLosstMax.setText("$" + String.format("%.2f", profileDetails.tradingCommandLossMaximum));
        if (profileDetails.tradingCommandNumber == 0) {
            txtPercent.setText("0");
        } else {
            txtPercent.setText((100 * profileDetails.tradingCommandProfitNumber / profileDetails.tradingCommandNumber) + "%");
        }


        txtSolenh.setText("" + profileDetails.tradingCommandNumber);

        //Pie Chart
        ArrayList<PieEntry> entries = new ArrayList<>();
        //tổng tiền trong chart
        float sumMoney = profileDetails.moneyInvested + profileDetails.moneyProfitNow + profileDetails.moneyNow;
        if (profileDetails.moneyProfitNow > 0) {
            entries.add(new PieEntry(profileDetails.moneyProfitNow, ""));
        }
        if (profileDetails.moneyNow > 0) {
            entries.add(new PieEntry(profileDetails.moneyNow, ""));
        }
        if (profileDetails.moneyInvested > 0) {
            entries.add(new PieEntry(profileDetails.moneyInvested, ""));
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(new int[]{R.color.incomeColor, R.color.availableColor, R.color.investedColor}, getContext());
        pieDataSet.setValueTextColor(Color.WHITE); // Đặt màu cho label
        pieDataSet.setValueTextSize(9f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);

        pieChart.animateY(1000);

        // Đặt văn bản và làm đậm màu sắc
        pieChart.setCenterText("$" + String.format("%.2f",sumMoney/1000f) + "K");
        pieChart.setCenterTextColor(Color.parseColor("#FF000000"));
        pieChart.setCenterTextSize(16f);
        // Trước hết, lấy đối tượng Legend từ PieChart
        Legend legend = pieChart.getLegend();
        // Tắt hiển thị Legend (bảng chú giải)
        legend.setEnabled(false);
        // Cập nhật biểu đồ
        pieChart.invalidate();

    }

    public void loadData() {
     /*   Intent intent = getIntent();
        userId = intent.getStringExtra("id");*/
        profileViewModel.getInfo("mine", new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                profileDetails = new Gson().fromJson(data, QuyProfileResponseModel.Profile.class);
                getActivity().runOnUiThread(new Runnable() {
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


    public void setObserve() {
        profileViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
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
        profileViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                System.out.println(loadingLayout);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isLoading == true) {
                            loadingLayout.setVisibility(View.VISIBLE);
                        } else {
                            loadingLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }


    //edit name
    public void editUserName() {
        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị Dialog chỉnh sửa tên
                showEditNameDialog();
            }
        });
    }

    // dialog username
    private void showEditNameDialog() {
        // Tạo Dialog
        Dialog editNameDialog = new Dialog(getContext());
        editNameDialog.setContentView(R.layout.binh_dialog_edit_name);

        // Lấy tham chiếu đến các thành phần trong Dialog
        EditText editTextNewName = editNameDialog.findViewById(R.id.edit_brief_dialog_name);
        Button btnConfirm = editNameDialog.findViewById(R.id.btnConfirm);

        // Xử lý sự kiện khi nhấn nút "Đồng ý"
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editTextNewName.getText().toString();
                if (!newName.trim().isEmpty()) {
                    profileViewModel.postChangeName(newName, new BaseViewModel.OkCallback() {
                        @Override
                        public void handle(String data) {
//                            txtBriefUserName.setText(newName);
                            reloadProfileObject.reload();
                        }
                    });
                }
                editNameDialog.dismiss();
            }
        });

        // Hiển thị Dialog
        editNameDialog.show();
    }


    private void updatePercentAndSeekBar() {
        try {
           /* txtPercent.setText((profileDetails.tradingCommandProfitNumber / profileDetails.tradingCommandNumber) * 100 + "%");
            txtSolenh.setText(profileDetails.tradingCommandNumber);*/


            // Cập nhật giá tị cho SeekBar
            if (profileDetails.tradingCommandNumber == 0) {
                seekBar.setProgress(0);
            } else {
                seekBar.setProgress((100 * profileDetails.tradingCommandProfitNumber / profileDetails.tradingCommandNumber));
            }
            System.out.println(profileDetails.tradingCommandProfitNumber);
            seekBar.setMin(0);
            seekBar.setMax(100);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    private void showImageDialog() {
        // Tạo Dialog
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.binh_dialog_edit_image_user);

        // Lấy tham chiếu đến các thành phần trong Dialog
        dialogImageView = dialog.findViewById(R.id.image_item_rank_user);
        changeImageButton = dialog.findViewById(R.id.btn_brief_change_img_user);

        // Đặt ảnh từ image_brief_user vào ImageView trong Dialog
        dialogImageView.setImageDrawable(userImageView.getDrawable());

        // Xử lý sự kiện khi nhấn nút thay đổi ảnh
        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageChooseOption = 2;
                openChooseImageActivity.open(2);


            }
        });
        dialog.show();
    }

    public void setChooseAvatarImage(Bitmap bitmap) {
//        System.out.println(bitmap.getByteCount());
//        userImageView.setImageBitmap(bitmap);

        userImageView.setBackground(new BitmapDrawable(getResources(), bitmap));
        dialogImageView.setImageBitmap(bitmap);
        changeAvatar(bitmap);
    }

    private void changeAvatar(Bitmap bitmap) {
        profileViewModel.postImage(bitmap, new BaseViewModel.OkCallback() {
            @Override
            public void handle(String data) {
                reloadProfileObject.reload();
            }
        });
    }

    public static interface OpenChooseImageActivity {
        public void open(int choose);
    }

    OpenChooseImageActivity openChooseImageActivity;

    public void setOpenChooseImageActivity(OpenChooseImageActivity openChooseImageActivity) {
        this.openChooseImageActivity = openChooseImageActivity;
    }

    QuyMainActivity.OpenSettingCallback openSettingCallback;

    public void setOpenSettingCallbackObject(QuyMainActivity.OpenSettingCallback openSettingCallback) {
        this.openSettingCallback = openSettingCallback;
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
                pieChart.getData().getDataSet().getEntryForIndex(0).setY((int)profileDetails.moneyProfitNow);
                pieChart.getData().notifyDataChanged();
                pieChart.notifyDataSetChanged();
                pieChart.invalidate(); // Refresh the chart

                txtBriefIncome.setText("$ "+String.format("%.2f",((profileDetails.moneyProfitNow)/1000f))+" K");
                pieChart.setCenterText("$" + String.format("%.2f",(profileDetails.moneyInvested + profileDetails.moneyProfitNow + profileDetails.moneyNow)/1000f) + "K");
            }
        });
    }
}