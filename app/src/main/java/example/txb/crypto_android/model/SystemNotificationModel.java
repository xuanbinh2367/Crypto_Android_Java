package example.txb.crypto_android.model;

public class SystemNotificationModel {
    public Type type;
    public String title;
    public String content;
    public OkCallback okCallback;

    public SystemNotificationModel(Type type, String title, String content, OkCallback okCallback) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.okCallback = okCallback;
    }
    public SystemNotificationModel(Type type, OkCallback okCallback) {
        this.type = type;
        this.title = "";
        this.content = "";
        this.okCallback = okCallback;
    }
    public SystemNotificationModel(Type type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.okCallback = null;
    }

    public SystemNotificationModel(Type type, String content) {
        this.type = type;
        this.title = "";
        this.content = content;
        this.okCallback = null;
    }

    public static interface OkCallback{
        public void handle();
    }
    public static enum Type{
        Info,Error,Warning
    }
}
