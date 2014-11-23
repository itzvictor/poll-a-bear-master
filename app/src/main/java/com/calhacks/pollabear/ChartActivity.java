package com.calhacks.pollabear;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.calhacks.pollabear.models.PollModel;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;


public class ChartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        String pollId = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                pollId = null;
            } else {
                pollId = extras.getString("pollId");
            }
        } else {
            pollId = (String) savedInstanceState.getSerializable("pollId");
        }
        PollModel poll = PollModel.findPoll(pollId);
        int[] votes = poll.getVotes(); //--> Data to graph
        //Toast.makeText(getApplicationContext(), String.format("Data: %d,%d,%d,%d",votes[0],votes[1],votes[2],votes[3]), Toast.LENGTH_LONG).show();
        GraphViewSeries.GraphViewSeriesStyle style = new GraphViewSeries.GraphViewSeriesStyle(R.color.pb_sunflower, 1);
        GraphViewSeries exampleSeries = new GraphViewSeries("This is Description.", style,  new GraphView.GraphViewData[] {
                new GraphView.GraphViewData(0.1, votes[0]),
                new GraphView.GraphViewData(0.2, votes[1]),
                new GraphView.GraphViewData(0.3, votes[2]),
                new GraphView.GraphViewData(0.4, votes[3]),
        });


        GraphView graphView = new BarGraphView(this , "");
        graphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.NONE);

        graphView.setBackgroundColor(Color.WHITE);
        graphView.setHorizontalLabels(new String[]{"A", "B", "C", "D"});
        //graphView.setVerticalLabels(new String[]{"Yes", "Option ", "Option C", "Option D"});

        graphView.addSeries(exampleSeries);
        LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
        layout.addView(graphView);
        Toast.makeText(this.getApplicationContext(), "Chart_Fragment PollID is: " , Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chart, menu);
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
