package bbc.umarket.umarketapp2.Helper;

public class ItemRankedHelperClass {
    String rankItemImg, rankItemName;

    public ItemRankedHelperClass() {}

    public ItemRankedHelperClass(String rankItemImg, String rankItemName) {
        this.rankItemImg = rankItemImg;
        this.rankItemName = rankItemName;
    }

    public String getRankItemImg() { return rankItemImg; }

    public void setRankItemImg(String rankItemImg) { this.rankItemImg = rankItemImg; }

    public String getRankItemName() {return rankItemName;}

    public void setRankItemName(String rankItemName) { this.rankItemName = rankItemName;}
}
