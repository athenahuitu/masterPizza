package cmu.teamCooper.masterpizza.savepizza;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


import oauth.signpost.OAuth;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cmu.teamCooper.masterpizza.R;
import cmu.teamCooper.masterpizza.gps.GPSlocActivity;
import cmu.teamCooper.masterpizza.twitter.PrepareRequestTokenActivity;
import cmu.teamCooper.masterpizza.twitter.TwitterUtils;

public class ViewPizzaActivity extends Activity implements OnTouchListener{
	
	private static final String TAG = "Touch"; 
	  
	   public static final String pizzaName = "cmu.teamCooper.masterpizza.pizza_name";
	   public static final String pizzaImage = "cmu.teamCooper.masterpizza.pizza_image";
	   public static final String DEBUG_TAG = "View Pizza";

	   private long rowID; 
	   private TextView nameTextView; 
	   private ImageView pizzaView; 
	   private String name;
	   private byte[] image;
	   private SharedPreferences prefs;
	   private Bitmap pizza;
	   private String pizzaImagePath;
	   
	   // These matrices will be used to scale points of the image
	   Matrix matrix = new Matrix();
	   Matrix savedMatrix = new Matrix();

	   // these PointF objects are used to record the point(s) the user is touching
	   PointF start = new PointF();
	   PointF mid = new PointF();
	   float oldDist = 1f;

	   // The 3 states (events) which the user is trying to perform
	   static final int NONE = 0;
	   static final int DRAG = 1;
	   static final int ZOOM = 2;
	   int mode = NONE;
	   
	   @Override
	   public void onCreate(Bundle savedInstanceState) 
	   {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_view_pizza);
	      this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
	      
	   // get the selected pizza's row ID
	      Bundle extras = getIntent().getExtras();
	      rowID = extras.getLong(ListSavedPizzaActivity.ROW_ID); 

	      // get the EditTexts
	      nameTextView = (TextView) findViewById(R.id.nameTextView);
	      
	      //background set to front
	      DatabaseConnector databaseConnector = new DatabaseConnector(ViewPizzaActivity.this);
	      databaseConnector.open();
	
	      Cursor result = databaseConnector.getOnePizza(rowID);
	      
	      result.moveToFirst(); // move to the first item 
   
         // get the column index for each data item
         int nameIndex = result.getColumnIndex("name");
         int imageIndex = result.getColumnIndex("image");
   
         // fill TextViews with the retrieved data
         name = result.getString(nameIndex);
         nameTextView.setText(name);
     
         try {
	         //retrive the image
	         image = result.getBlob(imageIndex);
	         pizza = BitmapFactory.decodeByteArray(image, 0, image.length);
	         pizzaView = (ImageView) findViewById(R.id.pizzaImageView);
	         pizzaView.setImageBitmap(pizza);
	         pizzaView.setOnTouchListener(this);
         } catch(Exception e) {
        	 Toast.makeText(getBaseContext(), "Memory problem", Toast.LENGTH_LONG).show();
        	 finish();
         }
	         
         result.close(); 
         databaseConnector.close();
	      
	   } 
	   
	   //called when the order button is clicked
	   public void onMore(View view) {
		   Intent intent = new Intent(this, AddPicAndVoiceActivity.class);
		   intent.putExtra(ListSavedPizzaActivity.ROW_ID, rowID);
		   intent.putExtra(pizzaName, name);
		   intent.putExtra(pizzaImage, image);
	       startActivity(intent);
	   }
	   
	   public void onGps(View view){
	    	Intent intent = new Intent(this, GPSlocActivity.class);
	    	Log.v(DEBUG_TAG, "BEGIN GPS");
	    	startActivity(intent);
	    	Log.v(DEBUG_TAG, "startGPS");
	    }

	   // called when the activity is first created
	   @Override
	   protected void onResume()
	   {
	      super.onResume();
	   } // end method onResume
	      
	   /** Called when the activity is first created. */
	    public boolean onTouch(View v, MotionEvent event) 
	    {
	        ImageView view = (ImageView) v;
	        view.setScaleType(ImageView.ScaleType.MATRIX);
	        float scale;

	        dumpEvent(event);
	        // Handle touch events here...

	        switch (event.getAction() & MotionEvent.ACTION_MASK) 
	        {
	            case MotionEvent.ACTION_DOWN:   // first finger down only
	                                                savedMatrix.set(matrix);
	                                                start.set(event.getX(), event.getY());
	                                                Log.d(TAG, "mode=DRAG"); // write to LogCat
	                                                mode = DRAG;
	                                                break;

	            case MotionEvent.ACTION_UP: // first finger lifted

	            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

	                                                mode = NONE;
	                                                Log.d(TAG, "mode=NONE");
	                                                break;

	            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

	                                                oldDist = spacing(event);
	                                                Log.d(TAG, "oldDist=" + oldDist);
	                                                if (oldDist > 5f) {
	                                                    savedMatrix.set(matrix);
	                                                    midPoint(mid, event);
	                                                    mode = ZOOM;
	                                                    Log.d(TAG, "mode=ZOOM");
	                                                }
	                                                break;

	            case MotionEvent.ACTION_MOVE:

	                                                if (mode == DRAG) 
	                                                { 
	                                                    matrix.set(savedMatrix);
	                                                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
	                                                } 
	                                                else if (mode == ZOOM) 
	                                                { 
	                                                    // pinch zooming
	                                                    float newDist = spacing(event);
	                                                    Log.d(TAG, "newDist=" + newDist);
	                                                    if (newDist > 5f) 
	                                                    {
	                                                        matrix.set(savedMatrix);
	                                                        scale = newDist / oldDist; // setting the scaling of the
	                                                                                    // matrix...if scale > 1 means
	                                                                                    // zoom in...if scale < 1 means
	                                                                                    // zoom out
	                                                        matrix.postScale(scale, scale, mid.x, mid.y);
	                                                    }
	                                                }
	                                                break;
	        }

	        view.setImageMatrix(matrix); // display the transformation on screen

	        return true; // indicate event was handled
	    }

	    /*
	     * --------------------------------------------------------------------------
	     * Method: spacing Parameters: MotionEvent Returns: float Description:
	     * checks the spacing between the two fingers on touch
	     * ----------------------------------------------------
	     */

	    private float spacing(MotionEvent event) 
	    {
	        float x = event.getX(0) - event.getX(1);
	        float y = event.getY(0) - event.getY(1);
	        return FloatMath.sqrt(x * x + y * y);
	    }

	    /*
	     * --------------------------------------------------------------------------
	     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
	     * Description: calculates the midpoint between the two fingers
	     * ------------------------------------------------------------
	     */

	    private void midPoint(PointF point, MotionEvent event) 
	    {
	        float x = event.getX(0) + event.getX(1);
	        float y = event.getY(0) + event.getY(1);
	        point.set(x / 2, y / 2);
	    }

	    /** Show an event in the LogCat view, for debugging */
	    private void dumpEvent(MotionEvent event) 
	    {
	        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
	        StringBuilder sb = new StringBuilder();
	        int action = event.getAction();
	        int actionCode = action & MotionEvent.ACTION_MASK;
	        sb.append("event ACTION_").append(names[actionCode]);

	        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) 
	        {
	            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
	            sb.append(")");
	        }

	        sb.append("[");
	        for (int i = 0; i < event.getPointerCount(); i++) 
	        {
	            sb.append("#").append(i);
	            sb.append("(pid ").append(event.getPointerId(i));
	            sb.append(")=").append((int) event.getX(i));
	            sb.append(",").append((int) event.getY(i));
	            if (i + 1 < event.getPointerCount())
	                sb.append(";");
	        }

	        sb.append("]");
	        Log.d("Touch Events ---------", sb.toString());
	    }	
	  
	    public void onShare(View view) {
	    	File pizzaFile;
	    	//create the file to store the pizza image
	    	File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	                  Environment.DIRECTORY_PICTURES), "PizzaPhoto");
	        if (! mediaStorageDir.exists()){
	            if (! mediaStorageDir.mkdirs()){
	                Log.v("PizzaPhoto", "failed to create directory");
	            }
	        }

	        // Create a media file name
	        pizzaFile = new File(mediaStorageDir.getPath() + File.separator +
	            "pizza_image"+ ".png");
	        pizzaImagePath = pizzaFile.getAbsolutePath();
	        
	        OutputStream outStream = null;
	        
	        try {
	        	outStream = new FileOutputStream(pizzaFile);
		        //decode the pizza image
		        pizza.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		        outStream.flush();
		        outStream.close();
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	           } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	           }
	        
	    	if (TwitterUtils.isAuthenticated(prefs)) {
	    		sendTweet();
	    	} else {
				Intent i = new Intent(getApplicationContext(), PrepareRequestTokenActivity.class);
				i.putExtra(ListSavedPizzaActivity.ROW_ID, rowID);
				i.putExtra("tweet_msg",pizzaImagePath);
				startActivity(i);
	    	}
	    }
	    
	    public void sendTweet() {
	    	Toast.makeText(getBaseContext(), "Tweet sent !", Toast.LENGTH_LONG).show();
			Thread t = new Thread() {
		        public void run() {
		        	
		        	try {
		        		TwitterUtils.sendTweet(prefs, pizzaImagePath);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
		        }

		    };
		    t.start();
		}
	    
	    public void onClear(View view) {
	    	Toast.makeText(getBaseContext(), "Log Out Twitter Successfully!!", Toast.LENGTH_LONG).show();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			final Editor edit = prefs.edit();
			edit.remove(OAuth.OAUTH_TOKEN);
			edit.remove(OAuth.OAUTH_TOKEN_SECRET);
			edit.commit();
		}
	    
	    public void onDelete(View view) {
	    	final DatabaseConnector db = new DatabaseConnector(this);
	    	final Intent intent = new Intent(this, ListSavedPizzaActivity.class);
	    	
	    	android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
	        builder.setTitle("Delete Pizza")
	            .setMessage("Are you sure you want to delete this pizza?")
	            .setCancelable(false)
	            .setIcon(R.drawable.ic_launcher)
	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            	
	            	
	                public void onClick(DialogInterface dialog, int id) {
	                	
	                	
	                	db.open();
	        	    	db.deletePizza(rowID);
	        	    	db.close();
	        	    	
	        	    	finish();
	        	    	startActivity(intent);
	                }
	            })
	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });

	        builder.create().show();
	    	
	    }
	    
}
