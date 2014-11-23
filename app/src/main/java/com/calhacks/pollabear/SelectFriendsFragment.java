package com.calhacks.pollabear;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.calhacks.pollabear.models.UserModel;
import com.facebook.widget.ProfilePictureView;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class SelectFriendsFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private FriendAdapter mAdapter;

    public static SelectFriendsFragment newInstance() {
        SelectFriendsFragment fragment = new SelectFriendsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SelectFriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_select_friends, container, false);
        final SelectFriendsFragment that = this;

        Button nextButton = (Button)view.findViewById(R.id.button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    if (mAdapter.getSelected(i)) {
                        UserModel user = (UserModel)mAdapter.getItem(i);
                        PollApplication.newPoll.addVoter(user.getParseUser().getObjectId());
                    }
                }
                (new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        PollApplication.newPoll.save();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void v) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                }).execute();
            }
        });

        (new AsyncTask<Void, Void, List<UserModel>>(){
            @Override
            protected List<UserModel> doInBackground(Void... voids) {
                return PollApplication.loggedInUser.getFriends();
            }

            @Override
            protected void onPostExecute(List<UserModel> friends) {
                super.onPostExecute(friends);
                mAdapter = new FriendAdapter(getActivity(),friends);
                // Set the adapter
                mListView = (AbsListView) view.findViewById(android.R.id.list);
                ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
                // Set OnItemClickListener so we can be notified on item clicks
                mListView.setOnItemClickListener(that);
            }
        }).execute();

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
        mAdapter.setSelected(position, !mAdapter.getSelected(position));
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

    private static class FriendAdapter extends BaseAdapter {

        private Activity activity;
        private List<UserModel> data;
        private boolean[] selected;
        private View[] views;
        private static LayoutInflater inflater=null;
        public ImageView imageView;

        public FriendAdapter(Activity activity, List<UserModel> data) {
            this.activity = activity;
            this.data = data;
            selected = new boolean[data.size()];
            views = new View[data.size()];
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageView=new ImageView(activity.getApplicationContext());
        }

        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return data.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public boolean getSelected(int position) {
            return selected[position];
        }

        public void setSelected(int position, boolean selected) {
            this.selected[position] = selected;
            if (views[position] != null) {
//                TextView title = (TextView)views[position].findViewById(R.id.textView);
                int color = views[position].getResources().getColor(selected ?
                        R.color.item_selected_color :
                        android.R.color.transparent);
                views[position].setBackgroundColor(color);
            }
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            if(convertView == null)
                vi = inflater.inflate(R.layout.friends_checkbox_list, null);

            UserModel user = data.get(position);
            ProfilePictureView fbProfilePicture = (ProfilePictureView) vi.findViewById(R.id.fbProfilePicture);
            fbProfilePicture.setProfileId(user.getFbId());
            fbProfilePicture.setDrawingCacheEnabled(true);
            fbProfilePicture.buildDrawingCache();

            TextView title = (TextView) vi.findViewById(R.id.textView);
            title.setText(user.getFullName());

            views[position] = vi;
            return vi;
        }

    }

}
