package fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import activity.LegislatorDetailActivity;
import adapter.LegislatorListAdapter;
import fragment.BillChildFragment.Active_Bill_Fragment;
import fragment.BillChildFragment.New_Bill_Fragment;
import loadjson.LegislatorLoadTask;
import other.JsonSort;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LegislatorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LegislatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LegislatorFragment extends Fragment implements LegislatorLoadTask.Listener, AdapterView.OnItemClickListener, View.OnClickListener {
    private FragmentTabHost mTabHost;
    private final String byStatesTabTag = "byStatesTabTag";
    private final String houseTabTag = "hosueTabTag";
    private final String senateTabTag = "senateTabTag";
    public static final String URL = "http://zp007app.edh2mtbzxn.us-west-2.elasticbeanstalk.com/aws/index.php?category=legislators";
    private List<HashMap<String, String>> mBillMapList = null;
    private ListView mListView = null;
    private Map<String, Integer> mapIndex = null;
    private JSONArray legislators = null;

    private OnFragmentInteractionListener mListener;

    public LegislatorFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LegislatorFragment newInstance(String param1, String param2) {
        LegislatorFragment fragment = new LegislatorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLoaded(String legislatorResponse) {
//        JSONObject legislatorResult;
        JSONArray legislatorResArray = null;
        try {
            legislatorResArray = new JSONObject(legislatorResponse).getJSONArray("results");
//            legislators = new JSONObject(legislatorResponse).getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String currentTab = mTabHost.getCurrentTabTag();
        if(currentTab.equals(byStatesTabTag)) {
            legislatorResArray = JsonSort.sort(legislatorResArray, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    JSONObject ja = (JSONObject) o1;
                    JSONObject jb = (JSONObject) o2;
                    int ans = 0;
                    try {
                        ans = ja.getString("state_name").toLowerCase().compareTo(jb.getString("state_name").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return ans;
                }
            });
        }
        else {
            legislatorResArray = JsonSort.sort(legislatorResArray, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    JSONObject ja = (JSONObject) o1;
                    JSONObject jb = (JSONObject) o2;
                    int ans = 0;
                    try {
                        ans = ja.getString("last_name").toLowerCase().compareTo(jb.getString("last_name").toLowerCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return ans;
                }
            });
        }

        legislators = legislatorResArray;

        loadListView(legislatorResArray);
        getIndexList(legislatorResArray);
        displayIndex();

    }

    private void getIndexList(JSONArray legislatorResArray) {
        String currentTab = mTabHost.getCurrentTabTag();
        mapIndex = new LinkedHashMap<>();
        try {
            for(int i = 0; i < legislatorResArray.length(); i++) {
                JSONObject legislatorListItem = legislatorResArray.getJSONObject(i);
                String indexKeyString = currentTab.equals(byStatesTabTag) ?
                        legislatorListItem.getString("state_name") : legislatorListItem.getString("last_name");
                String index = indexKeyString.substring(0, 1);
                if(mapIndex.get(index) == null) {
                    mapIndex.put(index, i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayIndex() {
        LinearLayout indexLayout = (LinearLayout) getView().findViewById(R.id.slide_index);
        indexLayout.removeAllViews();
        Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView textView = null;
        List<String> indexList = new ArrayList<>(mapIndex.keySet());
        for(String index : indexList) {
            textView = (TextView) inflater.inflate(R.layout.slide_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(LegislatorFragment.this);
            indexLayout.addView(textView);
        }
    }

    @Override
    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        mListView.setSelection(mapIndex.get(selectedIndex.getText()));
    }


    private void loadListView(JSONArray legislatorResArray) {
        LegislatorListAdapter adapter = new LegislatorListAdapter(getActivity(), legislatorResArray);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject legislator = null;
                try {
                    legislator = legislators.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), LegislatorDetailActivity.class);
                intent.putExtra("legislator", legislator.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "Error !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_legislator, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_legislator);
        new LegislatorLoadTask(LegislatorFragment.this).execute(URL);

        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec(byStatesTabTag).setIndicator("by states"), Active_Bill_Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(houseTabTag).setIndicator("house"), New_Bill_Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(senateTabTag).setIndicator("senate"), New_Bill_Fragment.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(byStatesTabTag.equals(tabId)) {
                    new LegislatorLoadTask(LegislatorFragment.this).execute(URL);
                }
                else if(houseTabTag.equals(tabId)) {
                    new LegislatorLoadTask(LegislatorFragment.this).execute(URL + "&chamber=house");
                }
                else if(senateTabTag.equals(tabId)) {
                    new LegislatorLoadTask(LegislatorFragment.this).execute(URL + "&chamber=senate");
                }
            }
        });
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
