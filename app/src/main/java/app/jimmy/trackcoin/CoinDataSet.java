package app.jimmy.trackcoin;

/**
 * @author Jimmy
 *         Created on 15/1/18.
 */

public class CoinDataSet {
    private int id;
    private String coinName;
    private String coinImageUrl;
    private String coinCost;

    public CoinDataSet(int id, String coinName, String coinImageUrl, String coinCost) {
        this.id = id;
        this.coinName = coinName;
        this.coinImageUrl = coinImageUrl;
        this.coinCost = coinCost;
    }

    public int getId() {
        return id;
    }

    public String getCoinName() {
        return coinName;
    }

    public String getCoinImageUrl() {
        return coinImageUrl;
    }

    public String getCoinCost() {
        return coinCost;
    }
}
