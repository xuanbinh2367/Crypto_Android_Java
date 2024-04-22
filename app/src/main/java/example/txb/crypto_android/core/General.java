package example.txb.crypto_android.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import example.txb.crypto_android.R;
import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.SystemNotificationModel;

public class General {
    public static void showNotification(Context context, SystemNotificationModel model){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(model.type.equals(SystemNotificationModel.Type.Info)){
            builder.setIcon(R.drawable.quy_info);
            builder.setTitle("Thông báo");
        }else if(model.type.equals(SystemNotificationModel.Type.Warning)){
            builder.setIcon(R.drawable.quy_warning);
            builder.setTitle("Cảnh báo");
        }else{
            builder.setIcon(R.drawable.quy_error);
            builder.setTitle("Error!");
        }

        builder.setMessage(model.content);
        builder.setCancelable(false);
        builder.setOnDismissListener(null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(model.okCallback!=null){
                    model.okCallback.handle();
                }
            }
        });

        builder.show();
    }
    public static void setImageUrl(Context context, ImageView image, String url){
        Picasso.with(context).load(url).placeholder(R.drawable.quy_cryptocurrency).error(R.drawable.quy_cryptocurrency).into(image);
    }
    public static void setAvatarUrl(Context context, ImageView image, String name){
        String url = API.SERVER_URL_AND_PORT+"/resource/account/avatar/"+name;
        Picasso.with(context).load(url).error(R.drawable.binh_ic_user).into(image);
    }
    public static String convertTimeToDateTime(long milliSeconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
