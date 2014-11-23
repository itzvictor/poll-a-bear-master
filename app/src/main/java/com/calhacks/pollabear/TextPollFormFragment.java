package com.calhacks.pollabear;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TextPollFormFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TextPollFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class TextPollFormFragment extends Fragment {

    EditText question;
    EditText option1;
    EditText option2;
    EditText option3;
    EditText option4;
    LinearLayout linearLayout;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TextPollForm.
     */
    // TODO: Rename and change types and number of parameters
    public static TextPollFormFragment newInstance() {
        TextPollFormFragment fragment = new TextPollFormFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public TextPollFormFragment() {
        // Required empty public constructor
    }

    public void hideEditText(EditText a, EditText b, EditText c, EditText d){
        for(int i = 3; i > 0 ; i --) {
            if (i == 3) {
                if(d.getText().toString().matches("") && c.getText().toString().matches("") && c.isShown()){
                    d.setVisibility(View.GONE);
                }
            } else if (i == 2) {
                if(c.getText().toString().matches("") && b.getText().toString().matches("") && b.isShown()){
                    c.setVisibility(View.GONE);
                }
            } else if (i == 1) {
                if(b.getText().toString().matches("") && a.getText().toString().matches("") && a.isShown()){
                    b.setVisibility(View.GONE);
                }
            }
        }
    }

    public void showEditText(EditText a, EditText b, EditText c, EditText d){
        for(int i = 0; i < 3 ; i ++) {
            if (i == 2) {
                if(!c.getText().toString().matches("") && d.getText().toString().matches("")){
                    d.setVisibility(View.VISIBLE);
                }
            } else if (i == 1) {
                if(!b.getText().toString().matches("") && c.getText().toString().matches("")){
                    c.setVisibility(View.VISIBLE);
                }
            } else if (i == 0) {
                if(!a.getText().toString().matches("") && b.getText().toString().matches("")){
                    b.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_text_poll_form, container, false);
        question = (EditText) view.findViewById(R.id.question);
        option1 = (EditText) view.findViewById(R.id.option1);
        option2 = (EditText) view.findViewById(R.id.option2);
        option3 = (EditText) view.findViewById(R.id.option3);
        option4 = (EditText) view.findViewById(R.id.option4);
        option3.setVisibility(View.GONE);
        option4.setVisibility(View.GONE);

        final EditText[] options = new EditText[] { option1, option2, option3, option4 };
        for (EditText option : options) {
            option.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    hideEditText(option1, option2, option3, option4);
                }

                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    showEditText(option1, option2, option3, option4);
                }
            });
        }

        Button next_button = (Button) view.findViewById(R.id.button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText[] checklist = {question, option1, option2};
                if(!checkEmpty(checklist)){return;}
                PollApplication.newPoll.setQuestion(question.getText().toString());
                PollApplication.newPoll.clearOptions();
                for (EditText option : options) {
                    String text = option.getText().toString();
                    if (!text.isEmpty()) {
                        PollApplication.newPoll.addOption(text);
                    }
                }
                Intent intent = new Intent(getActivity(), SelectFriendsActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public boolean checkEmpty(EditText[] views)
    {
        for (EditText view : views){
            if(view.getText().toString().isEmpty())
            {
                view.setError("This is empty!");
                return false;
            }
        }
        return true;
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
