package example.txb.crypto_android.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import example.txb.crypto_android.R;
import example.txb.crypto_android.view_model.LoginViewModel;


public class Binh_OTPActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private EditText otp1;
    private EditText otp2;
    private EditText otp3;
    private EditText otp4;
    private EditText otp5;
    private EditText otp6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binh_activity_otp);
        OTP();
        setupOtpEditText(otp1, otp2, otp1);
        setupOtpEditText(otp2, otp3, otp1);
        setupOtpEditText(otp3, otp4, otp2);
        setupOtpEditText(otp4, otp5, otp3);
        setupOtpEditText(otp5, otp6, otp4);
        setupOtpEditText(otp6, otp6, otp5);

    }

    public void OTP(){
        final ImageView back = findViewById(R.id.back);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sum = getOtpSum();
                System.out.println("abc" + sum);

                finish();
            }
        });
    }

    private String getOtpSum() {
        String value1 = otp1.getText().toString();
        String value2 = otp2.getText().toString();
        String value3 = otp3.getText().toString();
        String value4 = otp4.getText().toString();
        String value5 = otp5.getText().toString();
        String value6 = otp6.getText().toString();

        String sum = value1 + value2 + value3 + value4 + value5 + value6;

        return sum;
    }

    private void setupOtpEditText(final EditText currentEditText, final EditText nextEditText, final EditText previousEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    nextEditText.requestFocus();
                } else if (editable.length() == 0) {
                    previousEditText.requestFocus();
                }
            }
        });

        currentEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // Handle delete key event
                    if (currentEditText.getText().length() == 0 && previousEditText != null) {
                        // If the current EditText is empty, move focus to the previous EditText
                        previousEditText.requestFocus();
                    } else {
                        // Clear the text if it's not empty
                        currentEditText.getText().clear();
                    }
                    return true;
                }
                return false;
            }
        });
    }
}