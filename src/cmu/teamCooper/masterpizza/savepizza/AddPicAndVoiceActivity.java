package cmu.teamCooper.masterpizza.savepizza;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cmu.teamCooper.masterpizza.R;
import cmu.teamCooper.masterpizza.createpizza.MiniGameActivity;

public class AddPicAndVoiceActivity extends Activity {
	
	public static final String DEBUG_TAG = "add Pic";
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	private File file;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private String mCurrentPhotoPath;
	private String mCurrentRecordPath;
	private ImageView mImageView;
	private TextView textView;
	private long rowID;
	private String name;
	private byte[] image;
	private Button record;
	private Button play;
	private boolean mStartRecording;
	private boolean mStartPlaying;
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
	        setContentView(R.layout.activity_add_pic_and_voice);
	        
	        
	        mImageView = (ImageView)findViewById(R.id.pizzaPhoto);
	        textView = (TextView)findViewById(R.id.pizzaName);
	        record = (Button)findViewById(R.id.record);
	        play = (Button)findViewById(R.id.play);
	        
	        // get the selected pizza's row ID, name, image
		    Bundle extras = getIntent().getExtras();
		    rowID = extras.getLong(ListSavedPizzaActivity.ROW_ID); 
		    name = extras.getString(ViewPizzaActivity.pizzaName);
		    image = extras.getByteArray(ViewPizzaActivity.pizzaImage);
		    
		    textView.setText(name);
	    
	    //get pizza's photo path
	    DatabaseConnector db = new DatabaseConnector(this);
	    db.open();
	    Cursor cursor = db.getPhotoPath(rowID);
	    cursor.moveToFirst();
	    
	    int pathIndex = cursor.getColumnIndex("photoPath");
	
	    mCurrentPhotoPath = cursor.getString(pathIndex);
	    
	    //if the pizza has a previous photo, show it
	    if(mCurrentPhotoPath != null) {
		    File imageFile = new File(mCurrentPhotoPath);
		    if(imageFile.exists()) {
		    	setPic();
		    }
	    }
	    
	    db.close();
	    
	    mCurrentRecordPath = getRecordFile().getAbsolutePath();
	    mStartRecording = true;
	    mStartPlaying = true;
        } catch(Exception e) {
        	Toast.makeText(getBaseContext(), "Memory problem", Toast.LENGTH_LONG).show();
        	
        	finish();
        }
	    
    }
    
 
    
    public void onTake(View view) {
    	 // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        if(mCurrentPhotoPath != null) {
	        File preFile = new File(mCurrentPhotoPath);
	        if(preFile.exists()) {
	        	boolean deleted = preFile.delete();
	        	if(deleted) {
	        		System.out.println("mei de");
	        	}
	        }
        }
        
        if(mCurrentPhotoPath != null) {
        	file = new File(mCurrentPhotoPath);
        } else {
        	file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        }// create a file to save the image
        
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // set the image file name
        mCurrentPhotoPath = file.getAbsolutePath();
        
        System.out.println("lalala" + mCurrentPhotoPath);
        
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    
    
    /* This part is for recording */
    public void onRecord(View view) {
        
    	if(record.getText().equals("Record")) {
    		startRecording();
    	} else {
    		stopRecording();
    	}
    }
    
    private void startRecording() {
    	
    	if (mStartRecording) {
            record.setText("Stop");
        } else {
            record.setText("Record");
        }
        mStartRecording = !mStartRecording;
    
    	
    	mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mCurrentRecordPath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }
    
    private void stopRecording() {
    	
    	if (mStartRecording) {
            record.setText("Stop");
        } else {
            record.setText("Record");
        }
        mStartRecording = !mStartRecording;
        
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    
    /* respond to play button */
    public void onPlay(View view) {
    	if (play.getText().equals("Play")) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }
    
    private void startPlaying() {
    	
    	if (mStartPlaying) {
            play.setText("Stop");
        } else {
            play.setText("Play");
        }
        mStartPlaying = !mStartPlaying;
        
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mCurrentRecordPath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
    	
    	if (mStartPlaying) {
            play.setText("Stop");
        } else {
            play.setText("Play");
        }
        mStartPlaying = !mStartPlaying;
        
        mPlayer.release();
        mPlayer = null;
    }
     
    
    
    /** Create a File for saving a record */
    private static File getRecordFile() {
    	
    	if(isExternalStorageWritable() == false) {
    		Log.v("PizzaRecord", "Ho my gosh!!!!!!");
    	}

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "PizzaRecord");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.v("PizzaRecord", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "3GP_record" +".3gp");
        return mediaFile;
    }
    
    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
    	
    	if(isExternalStorageWritable() == false) {
    		Log.v("PizzaPhoto", "Ho my gosh!!!!!!");
    	}

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                  Environment.DIRECTORY_PICTURES), "PizzaPhoto");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.v("PizzaPhoto", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
        } else {
        	Log.v("camera", "null");
            return null;
        }
        return mediaFile;
    }
    
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            	
            	if(data == null) {
            		Toast.makeText(getBaseContext(), "Camera is not available!", Toast.LENGTH_LONG).show();
            	} else {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                         mCurrentPhotoPath, Toast.LENGTH_LONG).show();
                
                //store the photo path in database
            	DatabaseConnector db = new DatabaseConnector(this);
            	
        		db.updatePizza(rowID, name, image, mCurrentPhotoPath);
        		System.out.println("haha" + mCurrentPhotoPath);
        		
                setPic();
            	}
                
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            	 Toast.makeText(this, "Image saved to:\n", Toast.LENGTH_LONG).show();
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                		mCurrentPhotoPath, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }
    
    public void setPic() {
    	/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);
		mImageView.setVisibility(View.VISIBLE);
    }
    
    public void onBack(View view) {
    	finish();
    }
    
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    
    public void chooseFruit (View view){

    	Intent intent = new Intent(this, MiniGameActivity.class);
    	startActivity(intent);
    }
    
}
