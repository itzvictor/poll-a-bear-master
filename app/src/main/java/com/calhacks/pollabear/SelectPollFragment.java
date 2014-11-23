package com.calhacks.pollabear;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.calhacks.pollabear.models.PollModel;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link SelectPollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectPollFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SelectPollFragment extends Fragment {

    Button select_text_btn;
    Button select_photo_btn;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static SelectPollFragment newInstance() {
        SelectPollFragment fragment = new SelectPollFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SelectPollFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PollApplication.newPoll = new PollModel(PollApplication.loggedInUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_poll, container, false);
        select_text_btn= (Button) view.findViewById(R.id.button_text);
        select_photo_btn = (Button) view.findViewById(R.id.button_photo);
        select_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PollApplication.newPoll.setType(PollModel.TEXT_POLL);
                getPollFormFragment(TextPollFormFragment.newInstance());
            }
        });
        select_photo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PollApplication.newPoll.setType(PollModel.PHOTO_POLL);
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void getPollFormFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String id) {
        if (mListener != null) {
            mListener.onFragmentInteraction(id);
        }
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

}
