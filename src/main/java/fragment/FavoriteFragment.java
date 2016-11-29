package fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import activity.BillDetailActivity;
import activity.CommitteeDetailActivity;
import activity.LegislatorDetailActivity;
import adapter.BillListAdapter;
import adapter.CommitteeListAdapter;
import adapter.LegislatorListAdapter;
import fragment.BillChildFragment.Active_Bill_Fragment;
import fragment.BillChildFragment.New_Bill_Fragment;
import other.JsonSort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment implements View.OnClickListener {
    protected View mRootView;
    private FragmentTabHost mTabHost;
    private final String legislatorTabTag = "legislatorTabTag";
    private final String committeeTabTag = "committeeTabTag";
    private final String billTabTag = "billTabTag";
    private final String KEY_LEGISLATOR_PREFERENCE = "favorite_legislators";
    private final String KEY_BILL_PREFERENCE = "favorite_bills";
    private final String KEY_COMMITTEE_PREFERENCE = "favorite_committees";
    private final String KEY_RESULTS = "results";
    private ListView mListView = null;
    private JSONObject legislatorJsonObj;
    private JSONObject billJsonObj;
    private JSONObject committeeJsonObj;
    private Map<String, Integer> mapIndex = null;

    private OnFragmentInteractionListener mListener;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void loadListView(String tabTag) {
        if(legislatorTabTag.equals(tabTag)) {
            LinearLayout indexLayout = (LinearLayout) mRootView.findViewById(R.id.slide_index_favorite);
            indexLayout.setVisibility(LinearLayout.VISIBLE);
            try {
                // layout offset
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mListView
                        .getLayoutParams();
                mlp.setMargins(0,0,50,0);

                // clear mListView
                mListView.setAdapter(new LegislatorListAdapter(getActivity(), new JSONArray()));
                LegislatorListAdapter adapter = (LegislatorListAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged();

                if(legislatorJsonObj == null) {
                    return;
                }

                // sort for favorite resArray
                JSONArray legislators = legislatorJsonObj.getJSONArray(KEY_RESULTS);

                legislators = JsonSort.sort(legislators, new Comparator() {
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

                final JSONArray legislatorResArray = legislators;

                // direct to detail
                adapter = new LegislatorListAdapter(getActivity(), legislatorResArray);
                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        JSONObject legislator = null;
                        try {
                            legislator = legislatorResArray.getJSONObject(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getActivity(), LegislatorDetailActivity.class);
                        intent.putExtra("legislator", legislator.toString());
                        startActivity(intent);
                    }
                });
                getIndexList(legislatorResArray);
                displayIndex();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if(billTabTag.equals(tabTag)) {
            LinearLayout indexLayout = (LinearLayout) mRootView.findViewById(R.id.slide_index_favorite);
            indexLayout.setVisibility(LinearLayout.GONE);
            try {
                // layout offset
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mListView
                        .getLayoutParams();
                mlp.setMargins(0,0,0,0);

                // clear mListView
                mListView.setAdapter(new BillListAdapter(getActivity(), new JSONArray()));
                BillListAdapter adapter = (BillListAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged();

                if(billJsonObj == null) {
                    return;
                }

                // sort jsona array
                JSONArray bills = billJsonObj.getJSONArray(KEY_RESULTS);

                bills = JsonSort.sort(bills, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        JSONObject ja = (JSONObject) o1;
                        JSONObject jb = (JSONObject) o2;
                        int ans = 0;
                        try {
                            ans = jb.getString("introduced_on").toLowerCase().compareTo(ja.getString("introduced_on").toLowerCase());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return ans;
                    }
                });

                final JSONArray billResArray = bills;

                mListView.setAdapter(new BillListAdapter(getActivity(), billResArray));
                adapter = (BillListAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged();

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        JSONObject bill = null;
                        try {
                            bill = billResArray.getJSONObject(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getActivity(), BillDetailActivity.class);
                        intent.putExtra("bill", bill.toString());
                        startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        else if(committeeTabTag.equals(tabTag)) {
            LinearLayout indexLayout = (LinearLayout) mRootView.findViewById(R.id.slide_index_favorite);
            indexLayout.setVisibility(LinearLayout.GONE);
            try {
                // layout offset
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mListView
                        .getLayoutParams();
                mlp.setMargins(0,0,0,0);

                // clear mListView
                mListView.setAdapter(new CommitteeListAdapter(getActivity(), new JSONArray()));
                CommitteeListAdapter adapter = (CommitteeListAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged();

                if(committeeJsonObj == null) {
                    return;
                }

                // sort json array
                JSONArray committees = committeeJsonObj.getJSONArray(KEY_RESULTS);

                committees = JsonSort.sort(committees, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        JSONObject ja = (JSONObject) o1;
                        JSONObject jb = (JSONObject) o2;
                        int ans = 0;
                        try {
                            ans = ja.getString("name").toLowerCase().compareTo(jb.getString("name").toLowerCase());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return ans;
                    }
                });

                final JSONArray committeeResArray = committees;

                mListView.setAdapter(new CommitteeListAdapter(getActivity(), committeeResArray));
                adapter = (CommitteeListAdapter) mListView.getAdapter();
                adapter.notifyDataSetChanged();

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        JSONObject committee = null;
                        try {
                            committee = committeeResArray.getJSONObject(position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(getActivity(), CommitteeDetailActivity.class);
                        intent.putExtra("committee", committee.toString());
                        startActivity(intent);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void getIndexList(JSONArray legislatorResArray) {
//        String currentTab = mTabHost.getCurrentTabTag();
        mapIndex = new LinkedHashMap<>();
        try {
            for(int i = 0; i < legislatorResArray.length(); i++) {
                JSONObject legislatorListItem = legislatorResArray.getJSONObject(i);
                String indexKeyString = legislatorListItem.getString("last_name");
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
        LinearLayout indexLayout = (LinearLayout) mRootView.findViewById(R.id.slide_index_favorite);
        indexLayout.setVisibility(LinearLayout.VISIBLE);
        indexLayout.removeAllViews();
        Context context = getActivity();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView textView = null;
        List<String> indexList = new ArrayList<>(mapIndex.keySet());
        for(String index : indexList) {
            textView = (TextView) inflater.inflate(R.layout.slide_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(FavoriteFragment.this);
            indexLayout.addView(textView);
        }
    }

    @Override
    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        mListView.setSelection(mapIndex.get(selectedIndex.getText()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_favorite, container, false);
        mRootView = rootView;
        mListView = (ListView) rootView.findViewById(R.id.listview_favorite);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("favorites", 0);
        try {
            legislatorJsonObj = new JSONObject(sharedPreferences.getString(KEY_LEGISLATOR_PREFERENCE, ""));
            billJsonObj = new JSONObject(sharedPreferences.getString(KEY_BILL_PREFERENCE, ""));
            committeeJsonObj = new JSONObject(sharedPreferences.getString(KEY_COMMITTEE_PREFERENCE, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec(legislatorTabTag).setIndicator("legislator"), Active_Bill_Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(billTabTag).setIndicator("bill"), New_Bill_Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(committeeTabTag).setIndicator("committee"), New_Bill_Fragment.class, null);

        loadListView(legislatorTabTag);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(legislatorTabTag.equals(tabId)) {
                    loadListView(legislatorTabTag);
                }
                else if(billTabTag.equals(tabId)) {
                    loadListView(billTabTag);
                }
                else if(committeeTabTag.equals(tabId)) {
                    loadListView(committeeTabTag);
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
