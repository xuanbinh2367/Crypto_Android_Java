package example.txb.crypto_android.view.custom_dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.cardview.widget.CardView;

import example.txb.crypto_android.R;


public class QuyEditBinhVerifyPinDialog extends Dialog {
    OkCallback okCallback;

    View[] pinCodeViews;

    public void setHandleCallback(OkCallback okCallback) {
        this.okCallback = okCallback;
    }

    public QuyEditBinhVerifyPinDialog(Context context) {
        super(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
    public static interface OkCallback{
        public void handle(String pin);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quy_edit_binh_dialog_pin_code);


        // mã pin
        pinCodeViews = new View[]{
            findViewById(R.id.quy_edit_binh_pin_code_1),
            findViewById(R.id.quy_edit_binh_pin_code_2),
            findViewById(R.id.quy_edit_binh_pin_code_3),
            findViewById(R.id.quy_edit_binh_pin_code_4)
        };

        CardView[] btns = {
                findViewById(R.id.quy_edit_binh_btn0),
                findViewById(R.id.quy_edit_binh_btn1),
                findViewById(R.id.quy_edit_binh_btn2),
                findViewById(R.id.quy_edit_binh_btn3),
                findViewById(R.id.quy_edit_binh_btn4),
                findViewById(R.id.quy_edit_binh_btn5),
                findViewById(R.id.quy_edit_binh_btn6),
                findViewById(R.id.quy_edit_binh_btn7),
                findViewById(R.id.quy_edit_binh_btn8),
                findViewById(R.id.quy_edit_binh_btn9),
        };

        for (int i = 0; i < btns.length; i++) {
            int finalI = i;
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handlePinButtonClick(finalI);
                }
            });
        }

        findViewById(R.id.quy_edit_binh_btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        findViewById(R.id.quy_edit_binh_btn_clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPin();
            }
        });
    }
    private void handlePinButtonClick(int pin) {
        for (View pinCodeView : pinCodeViews) {
            if (pinCodeView.getTag() == null) {
                pinCodeView.setBackgroundResource(R.drawable.binh_view_circle_pin_code2);
                pinCodeView.setTag(pin);
                checkAndShowToast();
                break;
            }
        }

    }

    // Kiểm tra
    private void checkAndShowToast() {
        StringBuilder enteredPin = new StringBuilder();
        for (View pinCodeView : pinCodeViews) {
            if (pinCodeView.getTag() != null) {
                enteredPin.append(pinCodeView.getTag());
            }
        }
        if (enteredPin.length() == 4) {
            okCallback.handle(enteredPin.toString());
        }
    }

    // Clear all pin
    public void clearPin() {
        for (View pinCodeView : pinCodeViews) {
            pinCodeView.setBackgroundResource(R.drawable.binh_view_circle_pin_code);
            pinCodeView.setTag(null);
        }
    }
}
