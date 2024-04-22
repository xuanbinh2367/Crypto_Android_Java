package example.txb.crypto_android.view_model;

import android.content.Context;
import android.graphics.Bitmap;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import example.txb.crypto_android.api.API;
import example.txb.crypto_android.model.SystemNotificationModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class BinhProfileViewModel extends BaseViewModel {

    private Context context;

    public BinhProfileViewModel(Context context) {
        this.context = context;
    }

    public void getInfo(String userId, OkCallback okCallback) {
        if (isLoading().getValue()) return;
        _isLoading.postValue(true);
        new Thread(() -> {
            getInfoAPI(userId, okCallback);
        }).start();
    }

    private void getInfoAPI(String userId, OkCallback okCallback) {
        try {
            API.RequestParams params = new API.RequestParams();
            params.add("userId", userId);

            API.ResponseAPI response = API.get(context, "/profile/details", params);
            if (response.status == API.ResponseAPI.Status.Fail) {
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, response.error));
            } else {
                okCallback.handle(response.data);
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }


        } catch (Exception e) {
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Lỗi hệ thống."));
        } finally {
            _isLoading.postValue(false);
        }
    }


    //change name
    public void postChangeName(String newName, OkCallback okCallback) {
        if (isLoading().getValue()) return;
        _isLoading.postValue(true);
        new Thread(() -> {
            postChangNameAPI(newName, okCallback);
        }).start();
    }

    private void postChangNameAPI(String newName, OkCallback okCallback) {
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("newName", newName)
                    .build();

            API.ResponseAPI response = API.post(context, "/account/changeName", requestBody);
            if (response.status == API.ResponseAPI.Status.Fail) {
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, response.error));
            } else {
//                okCallback.handle(response.data);
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info, "", "Đổi tên thành công", new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        okCallback.handle("");
                    }
                }));
            }


        } catch (Exception e) {
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Lỗi hệ thống."));
        } finally {
            _isLoading.postValue(false);
        }
    }

    //change image
    public void postImage(Bitmap bitmap, OkCallback okCallback) {
        if (isLoading().getValue()) return;
        _isLoading.postValue(true);
        new Thread(() -> {
            postImageAPI(bitmap, okCallback);
        }).start();
    }

    private void postImageAPI(Bitmap bitmap, OkCallback okCallback) {
        try {
////            File file = new File(imagePath);
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            File f = new File(context.getCacheDir(), "avatar.png");
            f.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("avatar", "avatar.png", RequestBody.create(MediaType.parse("image/*"), f))
                    .build();

            API.ResponseAPI response = API.post(context, "/account/editAvatar", requestBody);
            if (response.status == API.ResponseAPI.Status.Fail) {
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, response.error));
            } else {
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info, "", "Đổi avatar thành công", new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        okCallback.handle("");
                    }
                }));
            }
        } catch (Exception e) {
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Lỗi hệ thống."));
        } finally {
            _isLoading.postValue(false);
        }
    }


    // logout


    public void postLogout(boolean isLogoutAll, OkCallback okCallback) {
        if (isLoading().getValue()) return;
        _isLoading.postValue(true);
        new Thread(() -> {
            postLogoutAPI(isLogoutAll, okCallback);
        }).start();
    }

    private void postLogoutAPI(boolean isLogoutAll, OkCallback okCallback) {
        try {
//            API.RequestParams params = new API.RequestParams();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("acb","abc")
                    .build();

            API.ResponseAPI response = API.post(context, "/account/"+(isLogoutAll?"logoutAll":"logout"), requestBody);
            if (response.status == API.ResponseAPI.Status.Fail) {
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, response.error));
            } else {
                okCallback.handle(response.data);
//                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info,"ok"));
            }


        } catch (Exception e) {
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Lỗi hệ thống."));
        } finally {
            _isLoading.postValue(false);
        }
    }

    public void editPin(String pin, String password, OkCallback okCallback) {
        if (isLoading().getValue()) return;
        _isLoading.postValue(true);
        new Thread(() -> {
            editPinAPI(pin,password,okCallback);
        }).start();
    }

    private void editPinAPI(String pin, String password, OkCallback okCallback) {
        try {
//            API.RequestParams params = new API.RequestParams();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("tradePin",pin)
                    .addFormDataPart("password",password)
                    .build();

            API.ResponseAPI response = API.post(context, "/account/editTradeAuth", requestBody);
            if (response.status == API.ResponseAPI.Status.Fail) {
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, response.error));
            } else {
//                okCallback.handle(response.data);
                _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Info, "", "Đổi mã oin thành công.", new SystemNotificationModel.OkCallback() {
                    @Override
                    public void handle() {
                        okCallback.handle("");
                    }
                }));
            }


        } catch (Exception e) {
            System.out.println(e);
            _notification.postValue(new SystemNotificationModel(SystemNotificationModel.Type.Error, "Lỗi hệ thống."));
        } finally {
            _isLoading.postValue(false);
        }
    }


}
