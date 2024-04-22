package example.txb.crypto_android.view_model;

import android.content.Context;


import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.SystemNotificationModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class LoginViewModel extends BaseViewModel{
    private Context context;

    public LoginViewModel(Context context){
        this.context = context;
    }

    public void login(String sdt, String password, SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            loginAPI(sdt,password,okCallback,failCallback);
        }).start();
    }
    private void loginAPI(String sdt, String password, SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failCallback){
        try{
            System.out.println(sdt);
            System.out.println(password);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .addFormDataPart("numberPhone", sdt)
                    .addFormDataPart("password", password)
                    .build();

            API.ResponseAPI response = API.post(context,"/account/login",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"",response.error,failCallback));
//                failCallback.handle();
            }else{
                API.setAuth(context,response.data);
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"","Đăng nhập thành công",okCallback));
//                okCallback.handle();
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void register(String sdt, String password, String name, SystemNotificationModel.OkCallback okCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            registerAPI(sdt,password, name,okCallback);
        }).start();
    }
    private void registerAPI(String sdt, String password, String name ,SystemNotificationModel.OkCallback okCallback){
        try{
            System.out.println(sdt);
            System.out.println(password);
            System.out.println(name);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .addFormDataPart("numberPhone", sdt)
                    .addFormDataPart("password", password)
                    .addFormDataPart("name", name)
                    .build();

            API.ResponseAPI response = API.post(context,"/account/registerStep1",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,response.error));
            }else{
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"","Bạn hãy xác nhận mã OTP",okCallback));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void register2(String sdt, String sum, SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failOkCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            registerAPI2(sdt, sum ,okCallback, failOkCallback);
        }).start();
    }
    private void registerAPI2(String sdt, String sum ,SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failOkCallback){
        try{
            System.out.println(sdt);
            System.out.println(sum);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .addFormDataPart("numberPhone", sdt)
                    .addFormDataPart("code", sum)
                    .build();

            API.ResponseAPI response = API.post(context,"/account/registerStep2",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
//                failOkCallback.handle();
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"",response.error,failOkCallback));
            }else{
                okCallback.handle();
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info, "","Đăng ký tài khoản thành công",okCallback));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void resetPassword(String sdt, String newPassword, SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            resetPasswordAPI(sdt, newPassword,okCallback, failCallback);
        }).start();
    }
    private void resetPasswordAPI(String sdt, String newPassword ,SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failCalllback){
        try{
            System.out.println(sdt);
            System.out.println(newPassword);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .addFormDataPart("numberPhone", sdt)
                    .addFormDataPart("password", newPassword)
                    .build();

                API.ResponseAPI response = API.post(context,"/account/resetPasswordStep1",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"",response.error,failCalllback));
            }else{
//                okCallback.handle();
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"","Bạn hãy xác nhận mã OTP",okCallback));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void resetPassword2(String sdt, String code, SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            resetPasswordAPI2(sdt, code,okCallback, failCallback);
        }).start();
    }
    private void resetPasswordAPI2(String sdt, String code ,SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failCallback){
        try{
            System.out.println(sdt);
            System.out.println(code);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .addFormDataPart("numberPhone", sdt)
                    .addFormDataPart("code", code)
                    .build();

            API.ResponseAPI response = API.post(context,"/account/resetPasswordStep2",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"",response.error,failCallback));
            }else{
//                okCallback.handle();
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"", "Thay đổi password thành công.",okCallback));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

    public void resend(String sdt, SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failCallback){
        if(isLoading().getValue())return;
        _isLoading.postValue(true);
        new Thread(()->{
            resendAPI(sdt, okCallback, failCallback);
        }).start();
    }
    private void resendAPI(String sdt ,SystemNotificationModel.OkCallback okCallback, SystemNotificationModel.OkCallback failCallback){
        try{
            System.out.println(sdt);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .addFormDataPart("numberPhone", sdt)
                    .build();

            API.ResponseAPI response = API.post(context,"/account/registerStep1Resend",requestBody);
            if(response.status== API.ResponseAPI.Status.Fail){
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"",response.error,failCallback));
            }else{
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"","Đã gửi lại OTP",okCallback));
            }
        }catch(Exception e){
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error,"Lỗi hệ thống."));
        }finally {
            _isLoading.postValue(false);
        }
    }

}
