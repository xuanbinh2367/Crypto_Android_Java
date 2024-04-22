package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.view.custom_dialog.QuyEditBinhVerifyPinDialog;
import example.txb.crypto_android.view_model.BaseViewModel;
import example.txb.crypto_android.view_model.BinhProfileViewModel;

public class Binh_SettingActivity extends AppCompatActivity {

    private Button btnLogout;
    private Button btnLogoutAll;
    private Button btnChangePin;
    private ProgressBar progressBar;
    private BinhProfileViewModel profileViewModel;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binh_activity_setting);

        //
        profileViewModel = new BinhProfileViewModel(getApplicationContext());

        btnLogout = findViewById(R.id.btn_setting_logout);
        btnLogoutAll = findViewById(R.id.btn_setting_logout_all);
        btnChangePin = findViewById(R.id.btn_setting_change_pin);
        progressBar = findViewById(R.id.progressBar3);

        //
        btnChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGetPassword();
            }
        });

        setObserve();
        //đăng xuất
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileViewModel.postLogout(false, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent2 = new Intent();
                                intent2.putExtra("isLogout",true);
                                setResult(Activity.RESULT_OK,intent2);
                                finish();
                            }
                        });
                    }
                });
            }
        });
        btnLogoutAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileViewModel.postLogout(true, new BaseViewModel.OkCallback() {
                    @Override
                    public void handle(String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent2 = new Intent();
                                intent2.putExtra("isLogout",true);
                                setResult(Activity.RESULT_OK,intent2);
                                finish();
                            }
                        });
                    }
                });
            }
        });

        getSupportActionBar().setTitle("Cài đặt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void setObserve() {
        profileViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel systemNotificationModel) {
                if (systemNotificationModel != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            General.showNotification(Binh_SettingActivity.this, systemNotificationModel);
                        }
                    });
                }
            }
        });
        profileViewModel.isLoading().observe(this, new Observer<Boolean>() {
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
    //dialog mã pin
    private void showGetPinDiaLog(String password) {
        // new dialog
        QuyEditBinhVerifyPinDialog dialog = new QuyEditBinhVerifyPinDialog(this);
        dialog.setHandleCallback(new QuyEditBinhVerifyPinDialog.OkCallback() {
            @Override
            public void handle(String pin) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                        profileViewModel.editPin(pin, password, new BaseViewModel.OkCallback() {
                            @Override
                            public void handle(String data) {

                            }
                        });
                    }
                });

            }
        });
        dialog.show();
    }
    private void showGetPassword(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Nhập mật khẩu");
        EditText input = new EditText(this);
        input.setHint("Mật khẩu");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            //@Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                showGetPinDiaLog(password);
            }
        });
        alert.setCancelable(true);
        alert.show();

    }


    // Xử lý sự kiện khi nút quay lại được bấm (actionBar)
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
}
