package example.txb.crypto_android.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import example.txb.crypto_android.R;
import example.txb.crypto_android.core.General;
import example.txb.crypto_android.model.SystemNotificationModel;
import example.txb.crypto_android.view.custom_dialog.QuyVerifyOtpDialog;
import example.txb.crypto_android.view_model.LoginViewModel;

public class Binh_ResetPasswordActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    EditText phonenumber;
    EditText newpassword;

    private void setRender() {
        // Set alert error
        loginViewModel.notification().observe(this, new Observer<SystemNotificationModel>() {
            @Override
            public void onChanged(SystemNotificationModel it) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (it != null) {
                            General.showNotification(Binh_ResetPasswordActivity.this, it);
                        }
                    }
                });
            }
        });

        // Set alert notification
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binh_activity_reset_password);

        phonenumber = findViewById(R.id.phonenumber);
        newpassword = findViewById(R.id.newpassword);

        loginViewModel = new LoginViewModel(getApplicationContext());
        ResetPassword();
        setRender();
    }
    public void ResetPassword() {
        final TextView signin = findViewById(R.id.signin);
        final TextView signup = findViewById(R.id.signup);
        phonenumber = findViewById(R.id.phonenumber);
        newpassword = findViewById(R.id.newpassword);
        final EditText confirmnewpassword = findViewById(R.id.confirmnewpassword);
        final Button btn = findViewById(R.id.btn);


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        signup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Thuc_ResetPasswordActivity.this, Thuc_SignUpActivity.class);
//                startActivity(intent);
//            }
//        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String setPhonenumber = phonenumber.getText().toString();
                String setNewPassword = newpassword.getText().toString();
                String setConfirmNewPassword = confirmnewpassword.getText().toString();

                if (setNewPassword.isEmpty() || setPhonenumber.isEmpty() || setConfirmNewPassword.isEmpty()) {
                    Toast.makeText(Binh_ResetPasswordActivity.this, "Bạn đang bỏ trống", Toast.LENGTH_SHORT).show();
                } else {
                    if (!setConfirmNewPassword.equals(setNewPassword)) {
                        Toast.makeText(Binh_ResetPasswordActivity.this, "Xác nhận mật khẩu sai", Toast.LENGTH_LONG).show();
                    }else resetPassword1(setPhonenumber, setNewPassword);
                }

            }
        });
    }

    public void resetPassword1(String phonenumber, String newpassword){
        loginViewModel.resetPassword(phonenumber, newpassword, new SystemNotificationModel.OkCallback() {
            @Override
            public void handle() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openOTP();
                    }
                });

            }
        }, new SystemNotificationModel.OkCallback() {
            @Override
            public void handle() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            }
        });
    }
    public void openOTP(){
        QuyVerifyOtpDialog quyVerifyOtpDialog = new QuyVerifyOtpDialog(this);
        quyVerifyOtpDialog.setHandleCallback(new QuyVerifyOtpDialog.OkCallback() {
            @Override
            public void handle(String otp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        quyVerifyOtpDialog.hide();
                        String setNumberphone = phonenumber.getText().toString();
                        loginViewModel.resetPassword2(setNumberphone, otp, new SystemNotificationModel.OkCallback() {

                            @Override
                            public void handle() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                        }, new SystemNotificationModel.OkCallback() {
                            @Override
                            public void handle() {
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

            }
        });
        quyVerifyOtpDialog.setResendCallback(new QuyVerifyOtpDialog.OkCallback() {
            @Override
            public void handle(String otp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        quyVerifyOtpDialog.hide();
                        String setNumberphone = phonenumber.getText().toString();
                        String setPassword = newpassword.getText().toString();
                        loginViewModel.resetPassword(setNumberphone, setPassword , new SystemNotificationModel.OkCallback() {
                            @Override
                            public void handle() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        quyVerifyOtpDialog.show();
                                    }
                                });

                            }
                        }, new SystemNotificationModel.OkCallback() {
                            @Override
                            public void handle() {
                              runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      quyVerifyOtpDialog.show();
                                  }
                              });


                            }
                        });
                        ;
                    }
                });

            }
        });
        quyVerifyOtpDialog.show();
    }
}