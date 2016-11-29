package fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;

import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import activity.BillDetailActivity;
import adapter.BillListAdapter;
import fragment.BillChildFragment.Active_Bill_Fragment;
import fragment.BillChildFragment.New_Bill_Fragment;
import loadjson.BillLoadTask;
import model.BillListItem;
import other.JsonSort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BillFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BillFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillFragment extends Fragment implements BillLoadTask.Listener, AdapterView.OnItemClickListener {

    private FragmentTabHost mTabHost;
    private final String activeTabTag = "activeTabTag";
    private final String newTabTag = "newTabTag";
    public static final String URL = "http://zp007app.edh2mtbzxn.us-west-2.elasticbeanstalk.com/aws/index.php?category=bills&";
    private List<HashMap<String, String>> mBillMapList = null;
    private ListView mListView = null;
    private JSONArray bills = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillFragment newInstance(String param1, String param2) {
        BillFragment fragment = new BillFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onLoaded(String billResponse) {
//        JSONObject billResult;
        JSONArray billResArray = null;
        try {
             billResArray = new JSONObject(billResponse).getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        billResArray = JsonSort.sort(billResArray, new Comparator() {
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

        bills = billResArray;
        loadListView(billResArray);
    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "Error !", Toast.LENGTH_SHORT).show();
    }

    private void loadListView(JSONArray billResArray) {
        BillListAdapter adapter = new BillListAdapter(getActivity(), billResArray);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject bill = null;
                try {
                    bill = bills.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), BillDetailActivity.class);
                intent.putExtra("bill", bill.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_bill, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_bill);
        new BillLoadTask(BillFragment.this).execute(URL + "active=true");

        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec(activeTabTag).setIndicator("active bill"), Active_Bill_Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(newTabTag).setIndicator("new bill"), New_Bill_Fragment.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(activeTabTag.equals(tabId)) {
                    new BillLoadTask(BillFragment.this).execute(URL + "active=true");
                }
                else {
                    new BillLoadTask(BillFragment.this).execute(URL + "active=false");
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
