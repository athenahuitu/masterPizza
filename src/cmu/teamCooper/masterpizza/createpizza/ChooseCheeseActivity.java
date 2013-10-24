package cmu.teamCooper.masterpizza.createpizza;

import cmu.teamCooper.masterpizza.CheeseView;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class ChooseCheeseActivity extends Activity implements SensorEventListener{
	
	public static final String DEBUG_TAG = "cheese";
	public static final String cheeseX = "cmu.teamCooper.masterpizza.cheeseX";
	public static final String cheeseY = "cmu.teamCooper.masterpizza.cheeseY";
	public static final String cheeseType = "cmu.teamCooper.masterpizza.cheeseType";

	/* private instance variables */
	private ViewGroup pizza;
	private ScalableImageView pizzaStep3;
	private ScalableImageView sauceView;
	private CheeseView cheeseView;
	private int crust_id;
	private int sauce_id;
	
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
        
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.ddd_d);
        int maxVolume = 50;
        int currVolume = 10;
        float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
        mediaPlayer.setVolume(0,1-log1);
        mediaPlayer.start();
        
        Intent intent = getIntent();
        
        crust_id = intent.getIntExtra(ChooseCrustActivity.crust_id, 0);
        sauce_id = intent.getIntExtra(ChooseSauceActivity.sauce_id, 0);
        
        //get the accelerator
        mInitialized = false;     
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        // inflate the layout
        LayoutInflater inflater = LayoutInflater.from(ChooseCheeseActivity.this);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.activity_choose_cheese, null);
        
        //inflate the pizza view and add it into the inflated layout
        pizza = (ViewGroup) inflater.inflate(R.layout.pizza_cheese, null);
        pizzaStep3 = (ScalableImageView) pizza.findViewById(R.id.pizza_view);
        sauceView = (ScalableImageView) pizza.findViewById(R.id.sauce_view);
        cheeseView = (CheeseView)pizza.findViewById(R.id.cheese_view);
        pizzaStep3.setImageResource(crustIds[crust_id]);
        sauceView.setImageResource(sauceIds[sauce_id]);
        cheeseView.setGesture(ChooseCheeseActivity.this);
        
        //add pizza to layout
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
			chooseMeat(new View(this));
		} 
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_choose_cheese, menu);
        return true;
    }
    
    
    /* respond to prev button */
    public void chooseSauce(View view) {
    	finish();
    }
    
    /* respond to next button */
    public void chooseMeat(View view){
    	Intent intent = new Intent(this, ChooseMeatActivity.class);
    	intent.putExtra(ChooseCrustActivity.crust_id, crust_id);
    	intent.putExtra(ChooseSauceActivity.sauce_id, sauce_id);
    	intent.putExtra(cheeseX, cheeseView.getXArray());
    	intent.putExtra(cheeseY, cheeseView.getYArray());
    	intent.putExtra(cheeseType, cheeseView.getCheeseIdArray());
    	Log.v(DEBUG_TAG, "choose meat");
    	startActivity(intent);
    }
    
    /* respond to different button's click event */
    public void onCheddar(View view) {
    	Log.v(DEBUG_TAG, "onCheddar");
    	cheeseView.setCheeseId(0);
    }
    
    public void onMozzarella(View view){
    	cheeseView.setCheeseId(1);
    }
    
    public void onMonterey(View view){
    	cheeseView.setCheeseId(2);
    }
    
   
}
