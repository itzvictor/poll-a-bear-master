package com.calhacks.pollabear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.calhacks.pollabear.R;
import com.calhacks.pollabear.models.PollModel;

import java.util.*;

public class TextResponseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String question = "";
        String pollId = "";
        final String[] options;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                question = null;
                options = null;
                pollId = null;
            } else {
                question = extras.getString("question");
                options = extras.getStringArray("textOptions");
                pollId = extras.getString("pollId");
            }
        } else {
            question = (String) savedInstanceState.getSerializable("question");
            options = (String[]) savedInstanceState.getSerializable("textOptions");
            pollId = (String) savedInstanceState.getSerializable("pollId");
        }
        setContentView(R.layout.activity_text_response);
        Button nextButton = (Button) findViewById(R.id.button);
        TextView textView = (TextView) findViewById(R.id.question);
        textView.setText(question);
        final ArrayList<RadioButton> radioList = new ArrayList<RadioButton>();
        radioList.add((RadioButton) findViewById(R.id.option1));
        radioList.add((RadioButton) findViewById(R.id.option2));
        radioList.add((RadioButton) findViewById(R.id.option3));
        radioList.add((RadioButton) findViewById(R.id.option4));
        for (int i = 0; i < options.length; i++) {
            radioList.get(i).setVisibility(View.VISIBLE);
            radioList.get(i).setText(options[i]);
            final String t = options[i];
            radioList.get(i).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }
            });
        }
        final String id = pollId;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected_value= -1;
                for(int r = 0; r<radioList.size(); r++){
                    if(radioList.get(r).isChecked()){
                        selected_value = r; // <--Selected value
                    }
                }
                PollModel poll = PollModel.findPoll(id);
                poll.insertVote(selected_value);
               // Toast.makeText(getApplicationContext(),selected_value, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ChartActivity.class).putExtra("pollId", id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.text_response, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
