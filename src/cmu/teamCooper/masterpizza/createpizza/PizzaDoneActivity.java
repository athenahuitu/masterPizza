package cmu.teamCooper.masterpizza.createpizza;

import java.io.ByteArrayOutputStream;

import cmu.teamCooper.masterpizza.R;
import cmu.teamCooper.masterpizza.ScalableImageView;
import cmu.teamCooper.masterpizza.ViewForCheese;
import cmu.teamCooper.masterpizza.ViewForMeat;
import cmu.teamCooper.masterpizza.savepizza.SavePizzaActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.Toast;

public class PizzaDoneActivity extends Activity {
	
	public static final String DEBUG_TAG = "Pizza Done";
	public static final String pizzaPic = "cmu.teamCooper.masterpizza.pizzaImage";
	
	/* private instance variables */
	private ViewGroup pizza;
	private ScalableImageView sauceView;
	private ScalableImageView pizzaStep5;

	private int crust_id;
	private int sauce_id;
	private float[] cheeseXArray;
	private float[] cheeseYArray;
	private int[] cheeseIdArray;
	private float[] meatXArray;
	private float[] meatYArray;
	private int[] meatIdArray;

	private int[] crustIds = new int[] {R.drawable.regular2,
            R.drawable.glu, R.drawable.wheat};
	
	private int[] sauceIds = new int[] {
			R.drawable.tomato, R.drawable.pesto, R.drawable.sauce_mix
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
		
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
	        meatXArray = intent.getFloatArrayExtra(ChooseMeatActivity.meatX);
	        meatYArray = intent.getFloatArrayExtra(ChooseMeatActivity.meatY);
	        meatIdArray = intent.getIntArrayExtra(ChooseMeatActivity.meatType);
			
	        // inflate the layout
	        LayoutInflater inflater = LayoutInflater.from(this);
	        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.activity_pizza_done, null);
	        
	        //inflate the pizza view and add it into the inflated layout
	        pizza = (ViewGroup) inflater.inflate(R.layout.pizza_done, null);
	        pizzaStep5 = (ScalableImageView) pizza.findViewById(R.id.pizza_view);
	        sauceView = (ScalableImageView) pizza.findViewById(R.id.sauce_view);
	        pizzaStep5.setImageResource(crustIds[crust_id]);
	        sauceView.setImageResource(sauceIds[sauce_id]);
	        
	        //create view for cheese
	        ViewForCheese cheeseView = (ViewForCheese) pizza.findViewById(R.id.view_for_cheese);
	        cheeseView.setCheese(cheeseXArray, cheeseYArray, cheeseIdArray);
	        
	        //create view for meat
	        ViewForMeat meatView = (ViewForMeat) pizza.findViewById(R.id.view_for_meat);
	        meatView.setMeat(meatXArray, meatYArray, meatIdArray);
	        
	        layout.addView(pizza);
	        setContentView(layout);
		} catch(Exception e) {
			Toast.makeText(getBaseContext(), "Memory problem", Toast.LENGTH_LONG).show();
        	finish();
		}
     
	}
	
	protected void onDestroy() {
		super.onDestroy();
		Log.v(DEBUG_TAG, "destroy pizza done");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_pizza_done, menu);
		return true;
	}

	public void chooseMeat(View v){
		finish();
	}
	
	public void save(View view) {         
				byte[] image = savePizza();
	            Intent intent = new Intent(this, SavePizzaActivity.class);
	            intent.putExtra(pizzaPic, image);
	    		startActivity(intent);
	    		finish();
	}
	
	private byte[] savePizza(){
		
		try {
			//convert the pizza view to bitmap
	    	pizza.setDrawingCacheEnabled(true);
	    	
	    	pizza.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
	                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
	    	
	        pizza.layout(0, 0, pizza.getMeasuredWidth(), pizza.getMeasuredHeight()); 
	        pizza.buildDrawingCache(true);
	    	Bitmap pizzaBitmap = Bitmap.createBitmap(pizza.getDrawingCache());
	    	
	    	pizza.setDrawingCacheEnabled(false);
	    	
	    	//parse pizzaBitmap into byte array
	    	ByteArrayOutputStream output = new ByteArrayOutputStream();
	    	pizzaBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
	    	byte[] byteArray = output.toByteArray();
	    	return byteArray;
		} catch(Exception e) {
			Toast.makeText(getBaseContext(), "Memory problem", Toast.LENGTH_LONG).show();
        	finish();
		}
		return null;
	}

}
