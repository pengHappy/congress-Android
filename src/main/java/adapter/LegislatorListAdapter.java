package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by coffee on 2016/11/21.
 */
public class LegislatorListAdapter extends BaseAdapter {

    private Context context;
    private JSONArray legislatorResArray;
    private LayoutInflater mInflater;
    private static final String KEY_BIOGUIDEID = "bioguide_id";
    private static final String KEY_LASTNAME = "last_name";
    private static final String KEY_FIRSTNAME = "first_name";
    private static final String KEY_PARTY = "party";
    private static final String KEY_STATE = "state_name";
    private static final String KEY_DISTRICT = "district";
    private static final String imageURLPrefix = "http://theunitedstates.io/images/congress/original/";

    public LegislatorListAdapter(Context context, JSONArray legislatorResArray) {
        this.context = context;
        this.legislatorResArray = legislatorResArray;
        this.mInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return legislatorResArray.length();
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
        convertView = mInflater.inflate(R.layout.list_item_legislator, null);
        JSONObject legislatorListItem = null;
        try {
            legislatorListItem = legislatorResArray.getJSONObject(position);

            String imgURL = imageURLPrefix + legislatorListItem.getString(KEY_BIOGUIDEID) + ".jpg";
            Log.d("imgURL:   ", imgURL);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.legislator_image);
            Glide.with(context).load(imgURL).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

            TextView name = (TextView) convertView.findViewById(R.id.legislator_name);
            name.setText(legislatorListItem.getString(KEY_LASTNAME) + legislatorListItem.getString(KEY_FIRSTNAME));

            TextView party = (TextView) convertView.findViewById(R.id.legislator_party);
            party.setText("(" + legislatorListItem.getString(KEY_PARTY) + ")");

            TextView state = (TextView) convertView.findViewById(R.id.legislator_state);
            state.setText(legislatorListItem.getString(KEY_STATE) + " - ");

            TextView district = (TextView) convertView.findViewById(R.id.legislator_district);
            district.setText("District " + (legislatorListItem.getString(KEY_DISTRICT).equals("null") ? "N.A." : legislatorListItem.getString(KEY_DISTRICT)));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}
