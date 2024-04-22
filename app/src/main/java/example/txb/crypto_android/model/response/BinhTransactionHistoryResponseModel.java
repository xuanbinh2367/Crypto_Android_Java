package example.txb.crypto_android.model.response;


import java.util.List;

public class BinhTransactionHistoryResponseModel {
    public static class Item {
        public String id;
        public String name;
        public long time;
        public float money;

        public Item(String id, String name, long time, float money) {
            this.id = id;
            this.name = name;
            this.time = time;
            this.money = money;
        }
    }

    public static class Lists {
        public List<Item> items;

        public Lists(List<Item> items) {
            this.items = items;
        }
    }
}
