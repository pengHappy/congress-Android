package activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommitteeDetailActivity extends AppCompatActivity {

    private String jsonStr = "";
    private JSONObject committeeObj;
    private JSONArray favoriteCommittees;
    private static final String KEY_SHAREDPREFERENCE = "favorite_committees";
    private static final String KEY_COMMITTEEID = "committee_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CHAMBER = "chamber";
    private static final String KEY_PARENT_COMMITTEEID = "parent_committee_id";
    private static final String KEY_CONTACT = "phone";
    private static final String KEY_OFFICE = "office";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_committee_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            // current legislator
            jsonStr = getIntent().getStringExtra("committee");
            committeeObj = new JSONObject(jsonStr);
            // shared preference
            final SharedPreferences sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            final String strForStar = sharedPreferences.getString(KEY_SHAREDPREFERENCE, "");
            boolean starEmpty = false;
            if(strForStar.length() == 0 || strForStar.equals("{\"results\":[]}") ||
                    !strForStar.toLowerCase().contains(committeeObj.getString(KEY_COMMITTEEID).toLowerCase())) {
                starEmpty = true;
            }

            // favorite star
            final ImageView starIcon = (ImageView) findViewById(R.id.detail_committee_star);
            starIcon.setImageResource(starEmpty == false ? R.drawable.star_filled : R.drawable.star);
            starIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // current bill id
                    String committeeId = "";
                    try {
                        committeeId = committeeObj.getString(KEY_COMMITTEEID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // judge whether bill Id exists
                    final String preferenceStr = sharedPreferences.getString(KEY_SHAREDPREFERENCE, "");
                    try {
                        if(preferenceStr.length() == 0) {
                            favoriteCommittees = new JSONArray();
                        }
                        else {
                            favoriteCommittees = new JSONObject(preferenceStr).getJSONArray("results");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if(!preferenceStr.toLowerCase().contains(committeeId.toLowerCase())) {
                        favoriteCommittees.put(committeeObj);
                        starIcon.setImageResource(R.drawable.star_filled);
                    }
                    else {
                        // delete it
//                        int step = 0;
                        JSONArray res = new JSONArray();
                        for(int i = 0; i < favoriteCommittees.length(); i++) {
                            try {
                                if(!favoriteCommittees.getJSONObject(i).getString(KEY_COMMITTEEID).equals(committeeId)) {
                                    res.put(favoriteCommittees.get(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        favoriteCommittees = res;
                        starIcon.setImageResource(R.drawable.star);
                    }
                    editor.putString(KEY_SHAREDPREFERENCE, "{\"results\":" + favoriteCommittees.toString() + "}");
                    editor.commit();
                }
            });

            // committee id
            TextView committeeId = (TextView) findViewById(R.id.detail_committee_id);
            committeeId.setText(committeeObj.getString(KEY_COMMITTEEID));

            // name
            TextView name = (TextView) findViewById(R.id.detail_committee_name);
            name.setText(committeeObj.getString(KEY_NAME));

            // chamber
            final ImageView chamberImg = (ImageView) findViewById(R.id.detail_committee_chamber_image);
            String chamberStr = committeeObj.getString(KEY_CHAMBER);
            if(chamberStr.equals("house")) {
                Glide.with(this).load("http://cs-server.usc.edu:45678/hw/hw8/images/h.png")
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(chamberImg);
            }
            else {
                Glide.with(this).load("http://zp007app.edh2mtbzxn.us-west-2.elasticbeanstalk.com/aws/chamber_s.png")
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(chamberImg);
            }
            TextView chamber = (TextView) findViewById(R.id.detail_committee_chamber);
            chamber.setText(chamberStr);

            // parent committee
            TextView parent = (TextView) findViewById(R.id.detail_committee_parent_committee);
            parent.setText(committeeObj.has(KEY_PARENT_COMMITTEEID) == false ?
                            "N.A." : committeeObj.getString(KEY_PARENT_COMMITTEEID));

            // contact
            TextView contact = (TextView) findViewById(R.id.detail_committee_contact);
            contact.setText(committeeObj.has(KEY_CONTACT) == false ?
                            "N.A." : committeeObj.getString(KEY_CONTACT));

            // office
            TextView office = (TextView) findViewById(R.id.detail_committee_office);
            office.setText(committeeObj.has(KEY_OFFICE) == false ?
                            "N.A." : committeeObj.getString(KEY_OFFICE));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        getSupportActionBar().setTitle("Committee Info");
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
