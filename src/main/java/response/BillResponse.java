package response;

import java.util.ArrayList;
import java.util.List;

import model.BillListItem;

/**
 * Created by coffee on 2016/11/20.
 */
public class BillResponse {

    private List<BillListItem> billListItems = new ArrayList<>();

    public List<BillListItem> getBillListItems() {
        return billListItems;
    }
}
