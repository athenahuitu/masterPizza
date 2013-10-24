package cmu.teamCooper.masterpizza.createpizza;




import cmu.teamCooper.masterpizza.BackgroundMusicService;
import cmu.teamCooper.masterpizza.R;
import cmu.teamCooper.masterpizza.ScalableImageView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ChooseCrustActivity extends Activity implements SensorEventListener{
	
	public static final String pizzaPic = "cmu.teamCooper.masterpizza.pizzaPic";
	public static final String crust_id = "cmu.teamCooper.masterpizza.crustid";
	public static final String DEBUG_TAG = "ChooseCurst";
	public int currentPhotoIndex;
	
	
	private int[] crustIds = new int[] {R.drawable.regular2,
            R.drawable.glu, R.drawable.wheat};
	private ViewGroup pizza;
	private ScalableImageView pizzaStep1;
	
	/* private instance variable for accelerator */
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private boolean mInitialized;
	private float mLastX;
	private final float NOISE = (float) 5.0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        

        //play audio
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.ddd_d);
        int maxVolume = 50;
        int currVolume = 10;
        float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
        mediaPlayer.setVolume(0,1-log1);
        mediaPlayer.start();
        
        //start backgound service
        Intent service = new Intent(ChooseCrustActivity.this,BackgroundMusicService.class);  
        startService(service); 
        
        //get the accelerator
        mInitialized = false;     
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        // inflate the layout
        LayoutInflater inflater = LayoutInflater.from(ChooseCrustActivity.this);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.activity_choose_crust, null);
        
        //inflate the pizza view and add it into the inflated layout
        pizza = (ViewGroup) inflater.inflate(R.layout.pizza, null);
        pizzaStep1 = (ScalableImageView) pizza.findViewById(R.id.pizza_view);
        layout.addView(pizza);
        
        //showphoto on inflated pizza view
        currentPhotoIndex = 0;
        showPhoto(currentPhotoIndex);   
        
        setContentView(layout);        
                   
     // Handle clicks on the 'regular' button.
        Button regularButton = (Button) findViewById(R.id.buttonregular);
        regularButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	currentPhotoIndex = 0;
                showPhoto(currentPhotoIndex);
            }
        });
        
     // Handle clicks on the 'glu' button.
        Button gluButton = (Button) findViewById(R.id.buttongluten);
        gluButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	currentPhotoIndex = 1;
                showPhoto(currentPhotoIndex);
            }
        });
        
     // Handle clicks on the 'wheat' button.
        Button wheatButton = (Button) findViewById(R.id.buttonwheat);
        wheatButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	currentPhotoIndex = 2;
                showPhoto(currentPhotoIndex);
            }
        });
    }
    
    @Override
    protected void onResume() {
      super.onResume();
      mSensorManager.registerListener((SensorEventListener) this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onPause() {
      super.onPause();
      mSensorManager.unregisterListener(this);
    }
    
    /*override the method defined in SensorEventListener */
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		if (!mInitialized) {
		mLastX = x;
		mInitialized = true;
		} else {
		float deltaX = mLastX - x;
		if (deltaX < NOISE) deltaX = (float)0.0;
		mLastX = x;
		if(deltaX > 0) {
			chooseSauce(new View(this));
		} 
		}
	}

      
    private void showPhoto(int photoIndex) {
         pizzaStep1.setImageResource(crustIds[photoIndex]);
    }
    
    //Navigate to mainActivity by destroying ChooseCrustActivity
    public void mainActivity (View view){
    	finish();
    }
    
    public void chooseSauce (View view){

    	Intent intent = new Intent(this, ChooseSauceActivity.class);
    	intent.putExtra(crust_id, currentPhotoIndex);
    	startActivity(intent);
    }

}
