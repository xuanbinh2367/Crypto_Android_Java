package example.txb.crypto_android.view;

public class Ban_OpenCommand {
    public String Id="";
    public String CoinId=" ";
    public String CoinName=" ";
    public Float Value=0f;
    public String Leverage=" ";
    public String CoinImage=" ";
    public String BuyOrSell=" ";
    public float openPrice;
    public float coinNumber;
    public float moneynumber;
    public long time;

    public Ban_OpenCommand(String id, String coinId, String coinName, Float value, String leverage, String coinImage, String buyOrSell, float openPrice, float coinNumber, float moneynumber, long time) {
        Id = id;
        CoinId = coinId;
        CoinName = coinName;
        Value = value;
        Leverage = leverage;
        CoinImage = coinImage;
        BuyOrSell = buyOrSell;
        this.openPrice = openPrice;
        this.coinNumber = coinNumber;
        this.moneynumber = moneynumber;
        this.time = time;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getCoinNumber() {
        return coinNumber;
    }

    public void setCoinNumber(float coinNumber) {
        this.coinNumber = coinNumber;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCoinId() {
        return CoinId;
    }

    public void setCoinId(String coinId) {
        CoinId = coinId;
    }

    public String getCoinName() {
        return CoinName;
    }

    public void setCoinName(String coinName) {
        CoinName = coinName;
    }

    public Float getValue() {
        return Value;
    }

    public void setValue(Float value) {
        Value = value;
    }

    public String getLeverage() {
        return Leverage;
    }

    public void setLeverage(String leverage) {
        Leverage = leverage;
    }

    public String getCoinImage() {
        return CoinImage;
    }

    public void setCoinImage(String coinImage) {
        CoinImage = coinImage;
    }

    public String getBuyOrSell() {
        return BuyOrSell;
    }

    public void setBuyOrSell(String buyOrSell) {
        BuyOrSell = buyOrSell;
    }
}
