package example.txb.crypto_android.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.model.response.QuyProfileResponseModel;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.BinhProfileViewModel;

public class Binh_ProfileActivity extends AppCompatActivity {

    private CardView cardBrief;
    private TextView txtSolenh;
    private TextView txtPercent;
    private SeekBar seekBar;
    private ImageView userImageView;
    private ImageView dialogImageView;
    private ImageButton changeImageButton;
    private int solenhValue = 0;
    private int choose = 0;
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

    //
    public int betterPercent;
    private ImageView btnCLickRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binh_activity_brief_user);

        loadingLayout = findViewById(R.id.loadingLayout);
        System.out.println(loadingLayout);
        //
        initView();

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
      /*  cardBrief.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog();
            }
        });*/

        //   updatePieChart();
        // editUserName();

      /*  btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Binh_ProfileActivity.this, Binh_SettingActivity.class);
                startActivity(intent);
            }
        });
*/
        getSupportActionBar().setTitle("Hồ sơ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profileViewModel = new BinhProfileViewModel(getApplicationContext());

        //
        setObserve();
        loadData();
        //

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // quay lại activity trc đó
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initView() {

        seekBar = findViewById(R.id.seekbar_brief_percent);
        seekBar.setEnabled(false);
        userImageView = findViewById(R.id.image_brief_user);


        btnSetting = findViewById(R.id.btn_brief_setting);

        // pie
        pieChart = findViewById(R.id.pieChart);

        //editname
        btnEditName = findViewById(R.id.btn_brief_edit_name);
        txtBriefUserName = findViewById(R.id.txt_brief_user_name);

        //
        txtBriefIncome = findViewById(R.id.txt_brief_income);
        txtBriefAvailable = findViewById(R.id.txt_brief_available);
        txtBriefInvested = findViewById(R.id.txt_brief_invested);
        txtSolenh = findViewById(R.id.txt_brief_solenh);
        txtPercent = findViewById(R.id.txt_brief_percent);

        //
        txtBriefMoneyMax = findViewById(R.id.txt_brief_money_max);
        txtBriefMoneyProfitMax = findViewById(R.id.txt_brief_profit_max);
        txtBriefMoneyLosstMax = findViewById(R.id.txt_brief_money_loss_max);
        txtBriefMoneyAverage = findViewById(R.id.txt_brief_money_average);
        //
        txtBriefRate = findViewById(R.id.txt__brief_rate);

        cardBrief = findViewById(R.id.cardView_brief_user_image);
        btnCLickRank = findViewById(R.id.btn_brief_rank);
        btnCLickRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Binh_ProfileActivity.this, Binh_RankActivity.class);
                intent.putExtra("betterPercent", betterPercent);
                startActivity(intent);
            }
        });
        findViewById(R.id.btnViewCommand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Binh_ProfileActivity.this, Quy_GuestCommandsActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
    }


    private void setProfile() {
        betterPercent = 100 * (profileDetails.totalNumber - profileDetails.topNumber) / profileDetails.totalNumber;
        //

        General.setAvatarUrl(getApplicationContext(), userImageView, profileDetails.avatar);
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
        pieDataSet.setColors(new int[]{R.color.incomeColor, R.color.availableColor, R.color.investedColor}, getApplicationContext());
        pieDataSet.setValueTextColor(Color.WHITE); // Đặt màu cho label
        pieDataSet.setValueTextSize(9f);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);

        pieChart.animateY(1000);

        // Đặt văn bản và làm đậm màu sắc
        pieChart.setCenterText("$" + String.valueOf((int) sumMoney) + "K");
        pieChart.setCenterTextColor(Color.parseColor("#FF000000"));
        pieChart.setCenterTextSize(16f);
        // Trước hết, lấy đối tượng Legend từ PieChart
        Legend legend = pieChart.getLegend();
        // Tắt hiển thị Legend (bảng chú giải)
        legend.setEnabled(false);
        // Cập nhật biểu đồ
        pieChart.invalidate();

        // hide editName , Setting
        btnEditName.setVisibility(View.GONE);
        btnSetting.setVisibility(View.GONE);
    }

    private void loadData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        profileViewModel.getInfo(userId, new BaseViewModel.OkCallback() {
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

            }
        });
    }


    public void setObserve() {
        profileViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel systemNotificationModel) {
                if (systemNotificationModel != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            General.showNotification(Binh_ProfileActivity.this, systemNotificationModel);
                        }
                    });
                }
            }
        });
        profileViewModel.isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                System.out.println(loadingLayout);
                runOnUiThread(new Runnable() {
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
        Dialog editNameDialog = new Dialog(this);
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
                    // Cập nhật TextView txt_brief_user_name
                    txtBriefUserName.setText(newName);
                }
                editNameDialog.dismiss();
            }
        });

        // Hiển thị Dialog
        editNameDialog.show();
    }


    private void updatePercentAndSeekBar() {
        try {

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
        Dialog dialog = new Dialog(this);
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
                CharSequence optionMenu[] = {"Chụp ảnh", "Chọn ảnh", "Thoát"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Binh_ProfileActivity.this);
                builder.setItems(optionMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (optionMenu[i].equals("Chụp ảnh")) {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            choose = 1;
                            getData.launch(takePicture);
                        } else if (optionMenu[i].equals("Chọn ảnh")) {
                            Intent pickPhoto = new Intent();
                            pickPhoto.setType("image/*");
                            pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
                            choose = 2;
                            getData.launch(pickPhoto);
                        } else if (optionMenu[i].equals("Thoát")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


        dialog.show();
    }

    ActivityResultLauncher<Intent> getData = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            o -> {
                if (o.getResultCode() == Activity.RESULT_OK) {
                    Intent data = o.getData();
                    Bitmap selectedImage = null;
                    if (choose == 1) {
                        selectedImage = (Bitmap) data.getExtras().get("data");
                    } else if (choose == 2) {
                        Uri selectedImageUrl = data.getData();
                        try {
                            selectedImage = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(), selectedImageUrl);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // Cập nhật ảnh cho cả userImageView và dialogImageView
                    userImageView.setImageBitmap(selectedImage);
                    dialogImageView.setImageBitmap(selectedImage);
                }
            }
    );


}
