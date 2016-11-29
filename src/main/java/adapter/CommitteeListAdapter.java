package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import loadjson.CommitteeLoadTask;

/**
 * Created by coffee on 2016/11/26.
 */
public class CommitteeListAdapter extends BaseAdapter {
    private Context context;
    private JSONArray committeeResArray;
    private LayoutInflater mInflater;
    private static final String KEY_COMMITTEEID = "committee_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CHAMBER = "chamber";

    public CommitteeListAdapter(Context context, JSONArray billResArray) {
        this.context = context;
        this.committeeResArray = billResArray;
        this.mInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return committeeResArray.length();
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
        convertView = mInflater.inflate(R.layout.list_item_committee, null);
        JSONObject committeeListItem = null;
        try {
            committeeListItem = committeeResArray.getJSONObject(position);
            TextView committeeId = (TextView) convertView.findViewById(R.id.committee_id);
            committeeId.setText(committeeListItem.getString(KEY_COMMITTEEID));
            TextView committeeName = (TextView) convertView.findViewById(R.id.committee_name);
            committeeName.setText(committeeListItem.getString(KEY_NAME));
            TextView committeeChamber = (TextView) convertView.findViewById(R.id.committee_chamber);
            committeeChamber.setText(committeeListItem.getString(KEY_CHAMBER));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
