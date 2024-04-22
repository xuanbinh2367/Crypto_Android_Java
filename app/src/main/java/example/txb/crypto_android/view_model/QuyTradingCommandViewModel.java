package example.txb.crypto_android.view_model;

import android.content.Context;

import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.SystemNotificationModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class QuyTradingCommandViewModel extends BaseViewModel{
    private Context context;

    public QuyTradingCommandViewModel(Context context){
        this.context = context;
    }

    public void openCommand(String optCode, String buyOrSell, String coinId, float moneyNumber, int leverage, boolean enableTpSl, float takeProfit, float stopLoss,OkCallback okCallback,OkCallback failCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            openCommandAPI(optCode,buyOrSell,coinId,moneyNumber,leverage,enableTpSl,takeProfit,stopLoss,okCallback,failCallback);
        }).start();
    }

    private void openCommandAPI(String optCode, String buyOrSell, String coinId, float moneyNumber, int leverage, boolean enableTpSl, float takeProfit, float stopLoss, OkCallback okCallback,OkCallback failCallback){
        try{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .addFormDataPart("otpCode", optCode)
                    .addFormDataPart("buyOrSell", buyOrSell)
                    .addFormDataPart("coinId", coinId)
                    .addFormDataPart("moneyNumber", ""+moneyNumber)
                    .addFormDataPart("leverage", ""+leverage)
                    .addFormDataPart("enableTpSl", ""+enableTpSl)
                    .addFormDataPart("takeProfit", ""+takeProfit)
                    .addFormDataPart("stopLoss", ""+stopLoss)
                    .build();



            API.ResponseAPI response = API.post(context,"/tradingCommand/open",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Fail", response.error, new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        failCallback.handle("");
                    }
                }));
            }else{
//                okCallback.handle(response.data);
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info, "Success", "Mở lệnh thành công",new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        okCallback.handle(response.data);
                    }
                }));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void checkTradePinStatus(OkCallback okCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            checkTradePinStatusAPI(okCallback);
        }).start();
    }
    private void checkTradePinStatusAPI(OkCallback okCallback){
        try{
            API.RequestParams params = new API.RequestParams();

            API.ResponseAPI response = API.get(context,"/account/getTradeAuthStatus",params);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,response.error));
            }else{
                okCallback.handle(response.data);
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }
        }catch(Exception e){
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void checkVerifyPin(String pin, OkCallback okCallback, OkCallback failCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            checkVerifyPinAPI(pin, okCallback, failCallback);
        }).start();
    }
    private void checkVerifyPinAPI(String pin, OkCallback okCallback, OkCallback failCallback){
        try{
            RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pin", pin)
                .build();

            API.ResponseAPI response = API.post(context,"/tradingCommand/verifyPin",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Fail", response.error, new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        failCallback.handle("");
                    }
                }));
            }else{
//                okCallback.handle(response.data);
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info, "", "Đã gửi OTP.", new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        okCallback.handle(response.data);
                    }
                }));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void details(String id, OkCallback okCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            detailsAPI(id, okCallback);
        }).start();
    }
    private void detailsAPI(String id, OkCallback okCallback){
        try{
            API.RequestParams params = new API.RequestParams();
            params.add("id",id);

            API.ResponseAPI response = API.get(context,"/tradingCommand/details",params);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,response.error));
            }else{
                okCallback.handle(response.data);
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void close(String otpCode, String id, OkCallback okCallback, OkCallback failCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            closeAPI(otpCode, id, okCallback, failCallback);
        }).start();
    }
    private void closeAPI(String otpCode, String id, OkCallback okCallback, OkCallback failCallback){
        try{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("otpCode", otpCode)
                    .addFormDataPart("id", id)
                    .build();

            API.ResponseAPI response = API.post(context,"/tradingCommand/close",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Fail", response.error, new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        failCallback.handle("");
                    }
                }));
            }else{
//                okCallback.handle(response.data);
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info, "Success", "Đóng lệnh thành công", new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        okCallback.handle(response.data);
                    }
                }));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void edit(String otpCode, String id, boolean enableTpSl, float takeProfit, float stopLost, OkCallback okCallback, OkCallback failCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            editAPI(otpCode, id, enableTpSl, takeProfit, stopLost, okCallback, failCallback);
        }).start();
    }
    private void editAPI(String otpCode, String id, boolean enableTpSl, float takeProfit, float stopLoss, OkCallback okCallback, OkCallback failCallback){
        try{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("otpCode", otpCode)
                    .addFormDataPart("id", id)
                    .addFormDataPart("enableTpSl", ""+enableTpSl)
                    .addFormDataPart("takeProfit", ""+takeProfit)
                    .addFormDataPart("stopLoss", ""+stopLoss)
                    .build();

            API.ResponseAPI response = API.post(context,"/tradingCommand/edit",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Fail", response.error, new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        failCallback.handle("");
                    }
                }));
            }else{
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info, "Success", "Chỉnh sửa thành công", new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        okCallback.handle(response.data);
                    }
                }));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }


}
