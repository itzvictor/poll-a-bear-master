package com.calhacks.pollabear;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.calhacks.pollabear.models.PollModel;
import com.parse.ParseException;
import com.parse.ParseFile;


public class PhotoResponseActivity extends Activity {

    private static final String TAG = "PhotoResponseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_response);
        final PhotoResponseActivity _this = this;

        String id = "";
        String question = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                question = null;
            } else {
                id = extras.getString("pollId");
                question = extras.getString("question");
            }
        } else {
            id = (String) savedInstanceState.getSerializable("pollId");
            question = (String) savedInstanceState.getSerializable("question");
        }
        final String _id = id;

        TextView questionView = (TextView) findViewById(R.id.question);
        questionView.setText(question);

        final GridView grid = (GridView) findViewById(R.id.gridviewimg);

        (new AsyncTask<Void, Void, PollModel>() {
            @Override
            protected PollModel doInBackground(Void... voids) {
                return PollModel.findPoll(_id);
            }

            @Override
            protected void onPostExecute(PollModel pollModel) {
                grid.setAdapter(new LazyImageListAdapter(_this, pollModel.getPhotoOptions()));
            }
        }).execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_response, menu);
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


    public class LazyImageListAdapter extends BaseAdapter {

        private Context context;
        final int count;
        final View[] views;

        public LazyImageListAdapter(Context c, final ParseFile[] files) {
            context = c;
            count = files.length;
            views = new View[count];
            Log.d(TAG, "Got " + files.length + " files");
            for (int i = 0; i < count; i++) {
                final int idx = i;
                (new AsyncTask<Void, Void, byte[]>() {
                    @Override
                    protected byte[] doInBackground(Void... voids) {
                        byte[] ret = null;
                        try {
                            ret = files[idx].getData();
                            Log.d(TAG, "Loaded file: " + idx);
                        } catch (ParseException e) {
                            Log.e(TAG, "Error while loading photo", e);
                        }
                        return ret;
                    }

                    @Override
                    protected void onPostExecute(byte[] data) {
                        Log.d(TAG, "onPostExecute: " + idx);
                        if (views[idx] != null) {
                            views[idx].findViewById(R.id.photo_loader).setVisibility(View.GONE);
                            ImageView iv = (ImageView) views[idx].findViewById(R.id.photo_img);
                            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                            iv.setVisibility(View.VISIBLE);
                            iv.setImageBitmap(bm);
                            //            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
                        }
                    }
                }).execute();
            }
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view != null) return view;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.photo_response_item, null);
            views[i] = v;
            return v;
        }
    }
}
