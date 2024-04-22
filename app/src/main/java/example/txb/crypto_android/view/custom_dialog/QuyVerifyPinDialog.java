package example.txb.crypto_android.view.custom_dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.regex.Pattern;

import example.txb.crypto_android.R;

public class QuyVerifyPinDialog extends Dialog {
    EditText input;
    Button btnVerify, btnCancel;
    OkCallback okCallback;

    public void setHandleCallback(OkCallback okCallback) {
        this.okCallback = okCallback;
    }

    public QuyVerifyPinDialog(Context context) {
        super(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
    public static interface OkCallback{
        public void handle(String pin);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quy_dialog_verify_pin);
        input = findViewById(R.id.quyDialogVerifyPinInput);
        btnCancel = findViewById(R.id.quyDialogVerifyPinCancelBtn);
        btnVerify = findViewById(R.id.quyDialogVerifyPinVerifyBtn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDataValid()) {
                        okCallback.handle(input.getText().toString());
                    }
                }
            });
    }
    public static boolean checkValidPin(String pin){
        String regex = "^(\\d{4})$";
        return Pattern.matches(regex, pin);
    }
    private boolean isDataValid() {
        String pin = input.getText().toString().trim();
        if(pin.isEmpty()){
            Toast.makeText(getContext(), "Pin không được để trống",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            if(!checkValidPin(pin)){
                Toast.makeText(getContext(), "Pin không hợp lệ",Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }
}
