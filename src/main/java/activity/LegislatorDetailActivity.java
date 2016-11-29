package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class LegislatorDetailActivity extends AppCompatActivity {

    private String jsonStr = "";
    private JSONObject legislatorObj;
    private JSONArray favoriteLegislators;
    private static final String KEY_SHAREDPREFERENCE = "favorite_legislators";
    private static final String KEY_BIOGUIDEID = "bioguide_id";
    private static final String KEY_LASTNAME = "last_name";
    private static final String KEY_FIRSTNAME = "first_name";
    private static final String KEY_PARTY = "party";
    private static final String KEY_DISTRICT = "district";
    private static final String KEY_EMAIL = "oc_email";
    private static final String KEY_FACEBOOKID = "facebook_id";
    private static final String KEY_TWITTERID = "twitter_id";
    private static final String KEY_TERMSTART = "term_start";
    private static final String KEY_TERMEND = "term_end";
    private static final String KEY_WEBSITE = "website";
    private static final String KEY_CHAMBER = "chamber";
    private static final String KEY_CONTACT = "phone";
    private static final String KEY_OFFICE = "office";
    private static final String KEY_STATE = "state_name";
    private static final String KEY_FAX = "fax";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String profileImgPrefix = "http://theunitedstates.io/images/congress/original/";
    private static final String facebookURLPrefix = "https://www.facebook.com/";
    private static final String twitterURLPrefix = "https://www.twitter.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legislator_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            // current legislator
            jsonStr = getIntent().getStringExtra("legislator");
            legislatorObj = new JSONObject(jsonStr);
            // shared preference
            final SharedPreferences sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            final String strForStar = sharedPreferences.getString(KEY_SHAREDPREFERENCE, "");
            boolean starEmpty = false;
            if(strForStar.length() == 0 || strForStar.equals("{\"results\":[]}") ||
                    !strForStar.toLowerCase().contains(legislatorObj.getString(KEY_BIOGUIDEID).toLowerCase())) {
                starEmpty = true;
            }

            // favorite star
            final ImageView starIcon = (ImageView) findViewById(R.id.detail_legislator_star);
            starIcon.setImageResource(starEmpty == false ? R.drawable.star_filled : R.drawable.star);
            starIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // current legislator bioguide id
                    String bioguideId = "";
                    try {
                        bioguideId = legislatorObj.getString(KEY_BIOGUIDEID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // judge whether bioguideId exists
                    final String preferenceStr = sharedPreferences.getString(KEY_SHAREDPREFERENCE, "");
                    try {
                        if(preferenceStr.length() == 0) {
                            favoriteLegislators = new JSONArray();
                        }
                        else {
                            favoriteLegislators = new JSONObject(preferenceStr).getJSONArray("results");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if(!preferenceStr.toLowerCase().contains(bioguideId.toLowerCase())) {
                        favoriteLegislators.put(legislatorObj);
                        starIcon.setImageResource(R.drawable.star_filled);
                    }
                    else {
                        // delete it
//                        int step = 0;
                        JSONArray res = new JSONArray();
                        for(int i = 0; i < favoriteLegislators.length(); i++) {
                            try {
                                if(!favoriteLegislators.getJSONObject(i).getString(KEY_BIOGUIDEID).equals(bioguideId)) {
                                    res.put(favoriteLegislators.get(i));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        favoriteLegislators = res;
                        starIcon.setImageResource(R.drawable.star);
                    }
                    editor.putString(KEY_SHAREDPREFERENCE, "{\"results\":" + favoriteLegislators.toString() + "}");
                    editor.commit();
                }
            });

            // facebook icon
            if(!legislatorObj.getString(KEY_FACEBOOKID).equals("null")) {
                ImageView facebookIcon = (ImageView) findViewById(R.id.detail_legislator_fb);
                facebookIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        try {
                            intent.setData(Uri.parse(facebookURLPrefix + legislatorObj.getString(KEY_FACEBOOKID)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
            }

            // twitter icon
            if(!legislatorObj.getString(KEY_TWITTERID).equals("null")) {
                ImageView twitterIcon = (ImageView) findViewById(R.id.detail_legislator_twitter);
                twitterIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        try {
                            intent.setData(Uri.parse(twitterURLPrefix + legislatorObj.getString(KEY_TWITTERID)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
            }

            // website
            if(!legislatorObj.getString(KEY_WEBSITE).equals("null")) {
                ImageView websiteIcon = (ImageView) findViewById(R.id.detail_legislator_web);
                websiteIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        try {
                            intent.setData(Uri.parse(legislatorObj.getString(KEY_WEBSITE)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
            }

            // profile image
            ImageView profileImg = (ImageView) findViewById(R.id.detail_legislator_profile_image);
            Glide.with(this).load(profileImgPrefix + legislatorObj.getString(KEY_BIOGUIDEID) + ".jpg")
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileImg);

            // party image and text
            ImageView partyImage = (ImageView) findViewById(R.id.detail_legislator_party_image);
            String party = legislatorObj.getString(KEY_PARTY);
            partyImage.setImageResource(party.equals("R") ? R.drawable.party_r : R.drawable.party_d);
            TextView partyText = (TextView) findViewById(R.id.detail_legislator_party);
            partyText.setText(party.equals("R") ? "Republican" : "Democratic");

            // legislator name
            TextView name = (TextView) findViewById(R.id.detail_legislator_name);
            name.setText(legislatorObj.getString(KEY_LASTNAME) + ", " + legislatorObj.getString(KEY_FIRSTNAME));

            // email
            TextView email = (TextView) findViewById(R.id.detail_legislator_email);
            email.setText(legislatorObj.getString(KEY_EMAIL));

            // chamber
            TextView chamber = (TextView) findViewById(R.id.detail_legislator_chamber);
            chamber.setText(legislatorObj.getString(KEY_CHAMBER));

            // contact
            TextView contact = (TextView) findViewById(R.id.detail_legislator_contact);
            contact.setText(legislatorObj.getString(KEY_CONTACT));

            // start term
            TextView startTerm = (TextView) findViewById(R.id.detail_legislator_start_term);
            Date date_s = null;
            try {
                date_s = new SimpleDateFormat("yyyy-MM-dd").parse(legislatorObj.getString(KEY_TERMSTART));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startTerm.setText(new SimpleDateFormat("MMM dd, yyyy").format(date_s));

            // end term
            TextView endTerm = (TextView) findViewById((R.id.detail_legislator_end_term));
            Date date_e = null;
            try {
                date_e = new SimpleDateFormat("yyyy-MM-dd").parse(legislatorObj.getString(KEY_TERMEND));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            endTerm.setText(new SimpleDateFormat("MMM dd, yyyy").format(date_e));


            // progress bar
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
            String d1 = legislatorObj.getString(KEY_TERMSTART);
            String d2 = legislatorObj.getString(KEY_TERMEND);
            Date dateStart = null, dateEnd = null;
            try {
                dateStart = new SimpleDateFormat("yyyy-MM-dd").parse(d1);
                dateEnd = new SimpleDateFormat("yyyy-MM-dd").parse(d2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date dateCurrent = new Date();
            long dif1 = dateCurrent.getTime() - dateStart.getTime();
            long dif2 = dateEnd.getTime() - dateStart.getTime();
            double portion = ((double) dif1 / dif2) * 100;
            TextView progressTextview = (TextView) findViewById(R.id.progressbar_text);
            progressTextview.setText(String.valueOf((int)portion) + "%");
            progressBar.setProgress((int)portion);

            // office
            TextView office = (TextView) findViewById(R.id.detail_legislator_office);
            office.setText(legislatorObj.getString(KEY_OFFICE));

            // state
            TextView state = (TextView) findViewById(R.id.detail_legislator_state);
            state.setText(legislatorObj.getString(KEY_STATE));

            // fax
            TextView fax = (TextView) findViewById(R.id.detail_legislator_fax);
            fax.setText(legislatorObj.getString(KEY_FAX));

            // birthday
            TextView birthday = (TextView) findViewById(R.id.detail_legislator_birthday);
            Date date_b = null;
            try {
                date_b = new SimpleDateFormat("yyyy-MM-dd").parse(legislatorObj.getString(KEY_BIRTHDAY));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            birthday.setText(new SimpleDateFormat("MMM dd, yyyy").format(date_b));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSupportActionBar().setTitle("Legislator Info");
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
