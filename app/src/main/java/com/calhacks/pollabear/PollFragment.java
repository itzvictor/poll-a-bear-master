package com.calhacks.pollabear;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.calhacks.pollabear.models.PollModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class PollFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static PollFragment newInstance() {
        PollFragment fragment = new PollFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PollFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAdapter = new PollAdapter(getActivity(), PollApplication.loggedInUser.getPolls());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poll, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        Button addPollbutton = (Button) view.findViewById(R.id.addPollbutton);
        addPollbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreatePollActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            PollModel poll = PollApplication.loggedInUser.getPolls().get(position);
            int pollType = poll.getType();
            bundle.putInt("pollNumber", pollType);
            bundle.putString("question", poll.getQuestion());
            bundle.putString("pollId", poll.getParseObject().getObjectId());
            if (pollType == PollModel.TEXT_POLL) {
                bundle.putStringArray("textOptions", poll.getTextOptions());
                intent = new Intent(getActivity(), TextResponseActivity.class).putExtra("question", bundle.getString("question"))
                        .putExtra("textOptions", bundle.getStringArray("textOptions"))
                        .putExtra("pollId",bundle.getString("pollId"));
                startActivity(intent);
            } else if (pollType == PollModel.PHOTO_POLL) {
                intent = new Intent(getActivity(), PhotoResponseActivity.class).putExtra("pollId", bundle.getString("pollId"))
                        .putExtra("question", bundle.getString("question"));
                startActivity(intent);
            } else {
                intent = new Intent(getActivity(), ChartActivity.class).putExtra("pollNumber", bundle.getString("pollNumber"))
                        .putExtra("pollNumber", bundle.getInt("pollNumber")).putExtra("textOptions", bundle.getStringArray("textOptions"));
                startActivity(intent);
            }
        }
    }

    public void getPollFormFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.pager, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    private static class PollAdapter extends BaseAdapter {

        private Activity activity;
        private List<PollModel> data;
        private static LayoutInflater inflater=null;
//        public ImageLoader imageLoader;

        public PollAdapter(Activity activity, List<PollModel> data) {
            this.activity = activity;
            this.data = data;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            imageLoader=new ImageLoader(activity.getApplicationContext());
        }

        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if(convertView == null)
                vi = inflater.inflate(R.layout.poll_list_item, null);

            PollModel poll = data.get(position);

            TextView title = (TextView)vi.findViewById(R.id.title);
            title.setText(poll.getQuestion());

            return vi;
        }

    }

}
