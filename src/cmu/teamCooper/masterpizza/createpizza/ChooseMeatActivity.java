	package cmu.teamCooper.masterpizza.createpizza;

import cmu.teamCooper.masterpizza.DpToPix;
import cmu.teamCooper.masterpizza.MeatView;
import cmu.teamCooper.masterpizza.R;
import cmu.teamCooper.masterpizza.ScalableImageView;
import cmu.teamCooper.masterpizza.ViewForCheese;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ChooseMeatActivity extends Activity implements SensorEventListener{
	
	public static final String DEBUG_TAG = "meat";
	public static final String cheeseX = "cmu.teamCooper.masterpizza.cheeseX";
	public static final String cheeseY = "cmu.teamCooper.masterpizza.cheeseY";
	public static final String cheeseType = "cmu.teamCooper.masterpizza.cheeseType";
	public static final String meatX = "cmu.teamCooper.masterpizza.meatX";
	public static final String meatY = "cmu.teamCooper.masterpizza.meatY";
	public static final String meatType = "cmu.teamCooper.masterpizza.meatType";
	
	/* private instance variables */
	private ViewGroup pizza;
	private ScalableImageView sauceView;
	private ScalableImageView pizzaStep4;
	private MeatView meatView;
	private ViewForCheese cheeseView;
	private int crust_id;
	private int sauce_id;
	private int pizzaSize;
	private float[] cheeseXArray;
	private float[] cheeseYArray;
	private int[] cheeseIdArray;

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
        try {
	        //play the audio
	        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.ddd_d);
	        int maxVolume = 50;
	        int currVolume = 10;
	        float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
	        mediaPlayer.setVolume(0,1-log1);
	        mediaPlayer.start();
	        
	        Intent intent = getIntent();
	        
	        crust_id = intent.getIntExtra(ChooseCrustActivity.crust_id, 0);
	        sauce_id = intent.getIntExtra(ChooseSauceActivity.sauce_id, 0);
	        cheeseXArray = intent.getFloatArrayExtra(ChooseCheeseActivity.cheeseX);
	        cheeseYArray = intent.getFloatArrayExtra(ChooseCheeseActivity.cheeseY);
	        cheeseIdArray = intent.getIntArrayExtra(ChooseCheeseActivity.cheeseType);
	        
	        //get the accelerator
	        mInitialized = false;     
	        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	        
	        // inflate the layout
	        LayoutInflater inflater = LayoutInflater.from(this);
	        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.activity_choose_meat, null);
	        
	        //inflate the pizza view and add it into the inflated layout
	        pizza = (ViewGroup) inflater.inflate(R.layout.pizza_meat, null);
	        pizzaStep4 = (ScalableImageView) pizza.findViewById(R.id.pizza_view);
	        sauceView = (ScalableImageView) pizza.findViewById(R.id.sauce_view);
	        pizzaStep4.setImageResource(crustIds[crust_id]);
	        sauceView.setImageResource(sauceIds[sauce_id]);
	
	        
	        //set view for cheese
	        cheeseView = (ViewForCheese) pizza.findViewById(R.id.view_for_cheese);
	        cheeseView.setCheese(cheeseXArray, cheeseYArray, cheeseIdArray);
	        
	        //set view for meat
	        meatView = (MeatView) pizza.findViewById(R.id.meat_view);
	        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
	        float density = metrics.density;
	        pizzaSize = DpToPix.dpToPix(230, density);
	        meatView.setPizzaSize(pizzaSize);
	        meatView.setGesture(ChooseMeatActivity.this);
	        //meatView.setBackgroundColor(Color.RED);
	     
	        layout.addView(pizza);
	        setContentView(layout);
        } catch(Exception e) {
        	Toast.makeText(getBaseContext(), "Memory problem", Toast.LENGTH_LONG).show();
        	finish();
        }
        
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
			pizzaDone(new View(this));
		} 
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_choose_meat, menu);
        return true;
    }
    
    /* respond to prev button */
    public void chooseCheese(View view) {
    	finish();
    }
    
    //next button
    public void pizzaDone(View view){
    	Intent intent = new Intent(this, PizzaDoneActivity.class);
    	Log.v(DEBUG_TAG, "BEGIN pizza done");
    	intent.putExtra(ChooseCrustActivity.crust_id, crust_id);
    	intent.putExtra(ChooseSauceActivity.sauce_id, sauce_id);
    	intent.putExtra(cheeseX, cheeseXArray);
    	intent.putExtra(cheeseY, cheeseYArray);
    	intent.putExtra(cheeseType, cheeseIdArray);
    	intent.putExtra(meatX, meatView.getXArray());
    	intent.putExtra(meatY, meatView.getYArray());
    	intent.putExtra(meatType, meatView.getmeatIdArray());
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }
    
    
    /* respond to different meat's button */
    public void onBeef(View view) {
    	meatView.setMeatId(0);
    	meatView.randomDraw();
    }
    
    public void onLamb(View view) {
    	meatView.setMeatId(1);
    	meatView.randomDraw();
    }
    
    public void onShrimp(View view) {
    	meatView.setMeatId(2);
    	meatView.randomDraw();
    }
     
}
