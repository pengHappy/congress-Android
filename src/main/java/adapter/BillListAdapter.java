package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by coffee on 2016/11/21.
 */
public class BillListAdapter extends BaseAdapter {
    private Context context;
    private JSONArray billResArray;
    private LayoutInflater mInflater;
    private static final String KEY_BILLID = "bill_id";
    private static final String KEY_SHORTTITLE = "short_title";
    private static final String KEY_OFFICIALTITLE = "official_title";
    private static final String KEY_TITLE = "title";
    private static final String KEY_INTRODUCEDON = "introduced_on";

    public BillListAdapter(Context context, JSONArray billResArray) {
        this.context = context;
        this.billResArray = billResArray;
        this.mInflater = LayoutInflater.from(this.context);
    }

    public void clearData() {
        billResArray = new JSONArray();
    }

    @Override
    public int getCount() {
        return billResArray.length();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.list_item_bill, null);
        JSONObject billListItem = null;
        try {
            billListItem = billResArray.getJSONObject(position);
            TextView billId = (TextView) convertView.findViewById(R.id.bill_id);
            billId.setText(billListItem.getString(KEY_BILLID));
            TextView billTitle = (TextView) convertView.findViewById(R.id.bill_title);
            billTitle.setText(billListItem.getString(KEY_SHORTTITLE).equals("null") ?
                                billListItem.getString(KEY_OFFICIALTITLE) : billListItem.getString(KEY_SHORTTITLE));
            TextView billIntroduced = (TextView) convertView.findViewById(R.id.introduced_on);
            billIntroduced.setText(billListItem.getString(KEY_INTRODUCEDON));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}
