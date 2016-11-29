package model;

/**
 * Created by coffee on 2016/11/20.
 */
public class BillListItem {

    private String billId;
    private String shortTitle;
    private String introducedOn;

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getIntroducedOn() {
        return introducedOn;
    }

    public void setIntroducedOn(String introducedOn) {
        this.introducedOn = introducedOn;
    }
}
