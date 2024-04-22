package example.txb.crypto_android.service;

import android.app.Service;

public interface ServiceCreatedCallback {
    public void setService(Service service);
    public void setIsBound(Boolean isBound);
    public void createdComplete();
}
