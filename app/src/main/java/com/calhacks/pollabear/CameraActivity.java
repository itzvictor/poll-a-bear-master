package com.calhacks.pollabear;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CameraActivity extends Activity {

    private static final String TAG = "CameraActivity";

    final int CAMERA_CAPTURE = 1;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private GridView grid;
    private EditText text;
    private List<String> listOfImagesPath;

    public static final String GridViewDemo_ImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GridViewDemo/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final CameraActivity _this = this;
        text = (EditText) findViewById(R.id.question);
        grid = (GridView) findViewById(R.id.gridviewimg);
        setItemClickListenerOnGrid(grid, this);

        Button sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PollApplication.newPoll.clearOptions();
                for (int i = 0; i < listOfImagesPath.size(); i++) {
                    File file = new File(listOfImagesPath.get(i));
                    if (file.exists()) {
                        // TODO bad, reading a second time
                        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
                        byte[] bbuf = new byte[1024];
                        FileInputStream in = null;
                        try {
                            in = new FileInputStream(file);
                            int len;
                            while ((len = in.read(bbuf)) > 0) {
                                buffer.put(bbuf, 0, len);
                            }
                            Log.d(TAG, "Adding new file " + file.getName());
                            // TODO fix filename (file.getName() fails)
                            ParseFile parseFile = new ParseFile(i + ".jpg", buffer.array());
                            try {
                                parseFile.save();   // TODO make asynchronous
                            } catch (ParseException e) {
                                Log.e(TAG, "Parse exception when uploading photo", e);
                            }
                            PollApplication.newPoll.setQuestion(text.getText().toString());
                            PollApplication.newPoll.addOption(parseFile);
                        } catch (FileNotFoundException e) {
                            Log.wtf(TAG, "Couldn't find file, but existed a moment ago!", e);
                        } catch (IOException e) {
                            Log.e(TAG, "Error reading file", e);
                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    Log.wtf(TAG, "Error closing stream!", e);
                                }
                            }
                        }
                    }
                }
                Intent intent = new Intent(_this, SelectFriendsActivity.class);
                startActivity(intent);
            }
        });

        listOfImagesPath = null;
        listOfImagesPath = RetrieveCapturedImagePath();

        removeAllPics();

        if (listOfImagesPath != null) {
            grid.setAdapter(new ImageListAdapter(this, listOfImagesPath));
        }
    }

    /**
     * Remove all selected pictures from disk
     */
    private void removeAllPics() {
        listOfImagesPath = null;
        listOfImagesPath = RetrieveCapturedImagePath();

        if (listOfImagesPath.isEmpty()) {
            return;
        }
        File file = null;
        for (String path : listOfImagesPath) {
            file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private void setItemClickListenerOnGrid(final GridView grid, final Context ctx) {
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (((ImageListAdapter) grid.getAdapter()).getImageUriCount() > position) {
                    // image has been loaded into this grid item
                    Intent viewImageIntent = new Intent(Intent.ACTION_VIEW);
                    viewImageIntent.setDataAndType(Uri.parse("file:///" + grid.getItemAtPosition(position)), "image/*");
                    startActivity(viewImageIntent);
                } else {
                    try {
                        //use standard intent to capture an image
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //we will handle the returned data in onActivityResult
                        startActivityForResult(captureIntent, CAMERA_CAPTURE);
                    } catch (ActivityNotFoundException anfe) {
                        //display an error message
                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                        Toast toast = Toast.makeText(ctx, errorMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                if (((ImageListAdapter) grid.getAdapter()).getImageUriCount() > position) {
                    // image has been loaded into this grid item

                    // Confirmation dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ctx)
                        .setTitle("Delete image")
                        .setMessage("Do you really want to delete this image?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((ImageListAdapter) grid.getAdapter()).removeItemAndFile(position);
                                listOfImagesPath = null;
                                listOfImagesPath = RetrieveCapturedImagePath();
                                if (listOfImagesPath != null) {
                                    grid.setAdapter(new ImageListAdapter(ctx, listOfImagesPath));
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);
                    dialog.show();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //user is returning from capturing an image using the camera
            if (requestCode == CAMERA_CAPTURE) {
                Bundle extras        = data.getExtras();
                Bitmap thePic        = extras.getParcelable("data");
                String imgcurTime    = dateFormat.format(new Date());
                File imageDirectory  = new File(GridViewDemo_ImagePath);

                imageDirectory.mkdirs();
                String _path = GridViewDemo_ImagePath + imgcurTime + ".jpg";

                try {
                    FileOutputStream out = new FileOutputStream(_path);
                    thePic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                listOfImagesPath = null;
                listOfImagesPath = RetrieveCapturedImagePath();
                if (listOfImagesPath != null) {
                    grid.setAdapter(new ImageListAdapter(this, listOfImagesPath));
                }
            }
        }
    }

    private List<String> RetrieveCapturedImagePath() {
        List<String> tFileList = new ArrayList<String>();
        File f = new File(GridViewDemo_ImagePath);
        if (f.exists()) {
            File[] files = f.listFiles();
            Arrays.sort(files);

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory())
                    continue;
                tFileList.add(file.getPath());
            }
        }
        return tFileList;
    }

    public class ImageListAdapter extends BaseAdapter {
        private Context context;
        private List<String> imgPic;

        public ImageListAdapter(Context c, List<String> thePic) {
            context = c;
            imgPic = thePic;
        }

        public int getCount() {
            return 4;
        }

        public int getImageUriCount() {
            return imgPic.size();
        }

        @Override
        public Object getItem(int i) {
            return imgPic.get(i);
        }

        public long getItemId(int position) {
            return position;
        }

        public void removeItemAndFile(int i) {
            File file = new File(imgPic.get(i));
            if (file.exists()) {
                file.delete();
            }
            imgPic.remove(i);
        }

        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView;
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inDither = false;             //Disable Dithering mode
            bfOptions.inPurgeable = true;              //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            bfOptions.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
            bfOptions.inTempStorage = new byte[32 * 1024];

            if (convertView == null) {
                itemView = inflater.inflate(R.layout.camera_action_grid_item, null);

                if (imgPic.isEmpty() || imgPic.size() <= position || imgPic.get(position) == null) {
                    return itemView;
                }

                FileInputStream fs = null;
                Bitmap bm;
                try {
                    fs = new FileInputStream(new File(imgPic.get(position).toString()));

                    if (fs != null) {
                        bm = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
                        ImageView imageView = (ImageView) itemView;
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(bm);
                        imageView.setId(position);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fs != null) {
                        try {
                            fs.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                itemView = convertView;
            }
            return itemView;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
