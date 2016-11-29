package activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillDetailActivity extends AppCompatActivity {

    private String jsonStr = "";
    private JSONObject billObj;
    private JSONArray favoriteBills;
    private static final String KEY_SHAREDPREFERENCE = "favorite_bills";
    private static final String KEY_BILLID = "bill_id";
    private static final String KEY_SHORTTITLE = "short_title";
    private static final String KEY_OFFICIALTITLE = "official_title";
    private static final String KEY_INTRODUCEDON = "introduced_on";
    private static final String KEY_SPONSOR = "sponsor";
    private static final String KEY_BILLTYPE = "bill_type";
    private static final String KEY_LASTNAME = "last_name";
    private static final String KEY_FIRSTNAME = "first_name";
    private static final String KEY_CHAMBER = "chamber";
    private static final String KEY_STATUS = "active";
    private static final String KEY_URLS = "urls";
    private static final String KEY_CONGRESS = "congress";
    private static final String KEY_HISTORY = "history";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            // current legislator
            jsonStr = getIntent().getStringExtra("bill");
            billObj = new JSONObject(jsonStr);
            // shared preference
            final SharedPreferences sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            final String strForStar = sharedPreferences.getString(KEY_SHAREDPREFERENCE, "");
            boolean starEmpty = false;
            if(strForStar.length() == 0 || strForStar.equals("{\"results\":[]}") ||
                    !strForStar.toLowerCase().contains(billObj.getString(KEY_BILLID).toLowerCase())) {
                starEmpty = true;
            }

            // favorite star
            final ImageView starIcon = (ImageView) findViewById(R.id.detail_bill_star);
            starIcon.setImageResource(starEmpty == false ? R.drawable.star_filled : R.drawable.star);
            starIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // current bill id
                    String billId = "";
                    try {
                        billId = billObj.getString(KEY_BILLID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // judge whether bill Id exists
                    final String preferenceStr = sharedPreferences.getString(KEY_SHAREDPREFERENCE, "");
                    try {
                        if(preferenceStr.length() == 0) {
                            favoriteBills = new JSONArray();
                        }
                        else {
                            favoriteBills = new JSONObject(preferenceStr).getJSONArray("results");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if(!preferenceStr.toLowerCase().contains(billId.toLowerCase())) {
                        favoriteBills.put(billObj);
                        starIcon.setImageResource(R.drawable.star_filled);
                    }
                    else {
                        // delete it
//                        int step = 0;
                        JSONArray res = new JSONArray();
                        for(int i = 0; i < favoriteBills.length(); i++) {
                            try {
                                if(!favoriteBills.getJSONObject(i).getString(KEY_BILLID).equals(billId)) {
                                    res.put(favoriteBills.get(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        favoriteBills = res;
                        starIcon.setImageResource(R.drawable.star);
                    }
                    editor.putString(KEY_SHAREDPREFERENCE, "{\"results\":" + favoriteBills.toString() + "}");
                    editor.commit();
                }
            });

            // bill id
            TextView billId = (TextView) findViewById(R.id.detail_bill_id);
            billId.setText(billObj.getString(KEY_BILLID));

            // title
            TextView title = (TextView) findViewById(R.id.detail_bill_title);
            title.setText(billObj.getString(KEY_SHORTTITLE).equals("null") ?
                            billObj.getString(KEY_OFFICIALTITLE) : billObj.getString(KEY_SHORTTITLE));

            // type
            TextView type = (TextView) findViewById(R.id.detail_bill_type);
            type.setText(billObj.getString(KEY_BILLTYPE).toUpperCase());

            // sponsor
            TextView sponsor = (TextView) findViewById(R.id.detail_bill_sponsor);
            sponsor.setText(billObj.getJSONObject(KEY_SPONSOR).getString(KEY_LASTNAME) + ", " +
                    billObj.getJSONObject(KEY_SPONSOR).getString(KEY_FIRSTNAME));

            // chamber
            TextView chamber = (TextView) findViewById(R.id.detail_bill_chamber);
            chamber.setText(billObj.getString(KEY_CHAMBER));

            // status
            TextView status = (TextView) findViewById(R.id.detail_bill_status);
            String active = billObj.getJSONObject(KEY_HISTORY).getString(KEY_STATUS);
            status.setText(active.equals("true") ? "Active" : "New");

            // introduced on
            TextView introducedOn = (TextView) findViewById(R.id.detail_bill_introduced_on);
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(billObj.getString(KEY_INTRODUCEDON));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            introducedOn.setText(new SimpleDateFormat("MMM dd, yyyy").format(date));

            // congress url
            TextView congressUrl = (TextView) findViewById(R.id.detail_bill_congress_url);
            congressUrl.setText(billObj.getJSONObject(KEY_URLS).getString(KEY_CONGRESS));

            // version status
            TextView versionStatus = (TextView) findViewById(R.id.detail_bill_version_status);
            versionStatus.setText("None");

            // bill url
            TextView pdfUrl = (TextView) findViewById(R.id.detail_bill_billurl);
            pdfUrl.setText("None");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle("Bill Info");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
