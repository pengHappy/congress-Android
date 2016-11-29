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
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.zp007.hw9.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import activity.CommitteeDetailActivity;
import adapter.CommitteeListAdapter;
import fragment.BillChildFragment.Active_Bill_Fragment;
import fragment.BillChildFragment.New_Bill_Fragment;
import loadjson.CommitteeLoadTask;
import other.JsonSort;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommitteeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CommitteeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommitteeFragment extends Fragment implements CommitteeLoadTask.Listener, AdapterView.OnItemClickListener{

    private FragmentTabHost mTabHost;
    private final String houseTabTag = "houseTabTag";
    private final String senateTabTag = "senateTabTag";
    private final String jointTabTag = "jointTabTag";
    public static final String URL = "http://zp007app.edh2mtbzxn.us-west-2.elasticbeanstalk.com/aws/index.php?category=committees&";
    private List<HashMap<String, String>> mCommitteeMapList = null;
    private ListView mListView = null;
    private View mRootView = null;
    private JSONArray committees = null;

    private OnFragmentInteractionListener mListener;

    public CommitteeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommitteeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommitteeFragment newInstance(String param1, String param2) {
        CommitteeFragment fragment = new CommitteeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onLoaded(String committeeResponse) {
//        JSONObject committeeResult;
        JSONArray committeeResArray = null;
        try {
            committeeResArray = new JSONObject(committeeResponse).getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        committeeResArray = JsonSort.sort(committeeResArray, new Comparator() {
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

        committees = committeeResArray;
        loadListView(committeeResArray);
    }

    private void loadListView(JSONArray committeeResArray) {
        CommitteeListAdapter adapter = new CommitteeListAdapter(getActivity(), committeeResArray);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject committee = null;
                try {
                    committee = committees.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getActivity(), CommitteeDetailActivity.class);
                intent.putExtra("committee", committee.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "Error !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_committee, container, false);
        mRootView = rootView;
        mListView = (ListView) rootView.findViewById(R.id.listview_committee);

        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec(houseTabTag).setIndicator("house"), Active_Bill_Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(senateTabTag).setIndicator("senate"), New_Bill_Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec(jointTabTag).setIndicator("joint"), New_Bill_Fragment.class, null);

        new CommitteeLoadTask(CommitteeFragment.this).execute(URL + "chamber=house");

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(houseTabTag.equals(tabId)) {
                    new CommitteeLoadTask(CommitteeFragment.this).execute(URL + "chamber=house");
                }
                else if(senateTabTag.equals(tabId)) {
                    new CommitteeLoadTask(CommitteeFragment.this).execute(URL + "chamber=senate");
                }
                else if(jointTabTag.equals(tabId)){
                    new CommitteeLoadTask(CommitteeFragment.this).execute(URL + "chamber=joint");
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
