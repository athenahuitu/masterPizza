package cmu.teamCooper.masterpizza.createpizza;

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
import android.view.ViewGroup;

public class ChooseSauceActivity extends Activity implements SensorEventListener {
	
	public static final String DEBUG_TAG = "Sauce";
	public static final String sauce_id = "cmu.teamCooper.masterpizza.sauceid";
	
	/* private instance variables */
	private ViewGroup pizza;
	private ScalableImageView pizzaStep2;
	private ScalableImageView sauceView;
	private int sauceId;
	private int crust_id;
	
	private int[] crustIds = new int[] {R.drawable.regular2,
            R.drawable.glu, R.drawable.wheat};
	
	private int[] sauceIds = new int[] {
			R.drawable.tomato, R.drawable.pesto, R.drawable.sauce_mix
	};
	
	/* private instance variable for accelerator */
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private boolean mInitialized;
	private float mLastX;
	private final float NOISE = (float) 5.0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //get the byteArray from the intent
        Intent intent = getIntent();
        
        //play the audio
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.ddd_d);
        int maxVolume = 50;
        int currVolume = 10;
        float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
        mediaPlayer.setVolume(0,1-log1);
        mediaPlayer.start();
        
        crust_id = intent.getIntExtra(ChooseCrustActivity.crust_id, 0);
        
       //get the accelerator
        mInitialized = false;     
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        //inflate the layout
        LayoutInflater inflater = LayoutInflater.from(ChooseSauceActivity.this);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.activity_choose_sauce, null);
        
        //inflate the pizza view and add it into the inflated layout
        pizza = (ViewGroup) inflater.inflate(R.layout.pizza, null);
        pizzaStep2 = (ScalableImageView) pizza.findViewById(R.id.pizza_view);
        sauceView = (ScalableImageView) pizza.findViewById(R.id.sauce_view);
        
        
        /* set image of pizzaStep2 */
        pizzaStep2.setImageResource(crustIds[crust_id]);
        
        /* set image of sauce */
        sauceId = 0;       
        
        layout.addView(pizza);
        setContentView(layout);
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

    /* respond to prev button */
    public void chooseCrust (View view){
    	finish();
    }
    
    /* respond to next button */
    public void chooseCheese(View view){
    	Intent intent = new Intent(this, ChooseCheeseActivity.class);
    	intent.putExtra(ChooseCrustActivity.crust_id, crust_id);
    	intent.putExtra(sauce_id, sauceId);
    	startActivity(intent);
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
			chooseCheese(new View(this));
		} 
		}
	}
    
    /**
     * Choose sauce picture
     */
    
    public void tomatoOnClick(View v) {
		sauceId = 0;
		showSauce(sauceId);
	}
    
    public void pestoOnclick(View v) {
    	sauceId = 1;
    	showSauce(sauceId);
    }
    
    public void mixOnclick(View v) {
    	sauceId = 2;
    	showSauce(sauceId);
    }
    	
    public void showSauce(int sauceId){
    	sauceView.setImageResource(sauceIds[sauceId]);
    }
    
}
