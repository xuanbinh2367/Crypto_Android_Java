package example.txb.crypto_android.model.response;

import java.util.ArrayList;

public class QuyProfileResponseModel {
    public static class OpenCommandItem{
        public String id;
        public String buyOrSell;
        public String coinId;
        public float openPrice;
        public float coinNumber;

        public OpenCommandItem(String id,String buyOrSell, String coinId, float openPrice, float coinNumber) {
            this.id = id;
            this.buyOrSell = buyOrSell;
            this.coinId = coinId;
            this.openPrice = openPrice;
            this.coinNumber = coinNumber;
        }
    }
    public static class Profile{
        public String id;

        public String name;
        public String avatar;
        public float moneyNow=0;
        public float moneyInvested=0;
        public float moneyProfitNow=0;
        public int tradingCommandNumber=0;
        public int tradingCommandProfitNumber=0;
        public int topNumber=0;
        public int totalNumber=0;
        public float tradingCommandMoneyMaximum=0;
        public float tradingCommandMoneyAvg=0;
        public float tradingCommandProfitMaximum=0;
        public float tradingCommandLossMaximum=0;
        public ArrayList<OpenCommandItem> openCommandItems = new ArrayList<>();

        public Profile() {
        }
    }
    public static class MiniProfile{

        public float moneyNow=0;
        public float moneyInvested=0;
        public float moneyProfitNow=0;
        public int openTradingCommandNumber=0;
        public ArrayList<OpenCommandItem> openCommandItems = new ArrayList<>();
        public ArrayList<String> interestedCoins = new ArrayList<>();

        public MiniProfile() {
        }

        public MiniProfile(float moneyNow, float moneyInvested, float moneyProfitNow, int openTradingCommandNumber, ArrayList<OpenCommandItem> openCommandItems, ArrayList<String> interestedCoins) {
            this.moneyNow = moneyNow;
            this.moneyInvested = moneyInvested;
            this.moneyProfitNow = moneyProfitNow;
            this.openTradingCommandNumber = openTradingCommandNumber;
            this.openCommandItems = openCommandItems;
            this.interestedCoins = interestedCoins;
        }
    }

    public static class TopUser{
        public String id;
        public String name;
        public String avatar;
        public float moneyNow=0;
        public int tradingCommandNumber=0;
        public int tradingCommandProfitNumber=0;
        public int topNumber=0;

        public TopUser() {
        }

        public TopUser(String id, String name, String avatar, float moneyNow, int tradingCommandNumber, int tradingCommandProfitNumber, int topNumber) {
            this.id = id;
            this.name = name;
            this.avatar = avatar;
            this.moneyNow = moneyNow;
            this.tradingCommandNumber = tradingCommandNumber;
            this.tradingCommandProfitNumber = tradingCommandProfitNumber;
            this.topNumber = topNumber;
        }
    }
    public static class TopUsers{
        public ArrayList<TopUser> items = new ArrayList<>();

    }
    public static class InterestedCoins{
        public ArrayList<String> items;

        public InterestedCoins(ArrayList<String> items) {
            this.items = items;
        }
        public InterestedCoins() {
            this.items = new ArrayList<>();
        }
    }
}
