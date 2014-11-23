package com.calhacks.pollabear;

import android.app.Activity;
import android.os.Bundle;


public class CreatePollActivity extends Activity implements SelectPollFragment.OnFragmentInteractionListener,
        TextPollFormFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SelectPollFragment())
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(String id) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

}
