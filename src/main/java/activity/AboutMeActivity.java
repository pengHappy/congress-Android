package activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zp007.hw9.R;

public class AboutMeActivity extends AppCompatActivity {

    private static final String URL = "http://zp007app.edh2mtbzxn.us-west-2.elasticbeanstalk.com/aws/me.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView me = (ImageView) findViewById(R.id.me_id);
        Glide.with(this).load(URL)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(me);
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
