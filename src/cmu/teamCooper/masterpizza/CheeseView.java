package cmu.teamCooper.masterpizza;

import java.util.ArrayList;

import cmu.teamCooper.masterpizza.R;
import cmu.teamCooper.masterpizza.R.drawable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/*
 * Custom CheeseView extends View class
 */
public class CheeseView extends View {
	
	public static final String DEBUG_TAG = "CheeseView";
	
	
	/* private instance variables */
	private int cheese_id;
	GestureDetector gestures;
	private Bitmap cheese;
	private int cheeseSize;
	private int fingerCount;
	private ArrayList<Float> bitX;
	private ArrayList<Float> bitY;
	private ArrayList<Integer> bitId; 
	private ArrayList<Bitmap> bitmapArray;
	
	private int[] cheeseIds = new int[]{
			R.drawable.cheddar_bitmap, R.drawable.mozzarella_bitmap, R.drawable.monterey_bitmap
	};

	public CheeseView(Context context) {
		super(context);
	}
	
	public CheeseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bitmapArray = new ArrayList<Bitmap>();
		bitX = new ArrayList<Float>();
		bitY = new ArrayList<Float>();
		bitId = new ArrayList<Integer>();
		cheese_id = -1;
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        float density = metrics.density;
        cheeseSize = DpToPix.dpToPix(30, density);
		Log.v(DEBUG_TAG, "2");
	}
	
	 public CheeseView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	        Log.v(DEBUG_TAG, "3");
	    }

	
	protected void onDraw(Canvas canvas) {
		//canvas.drawBitmap(droid, translate, null);
		for(int i = 0; i < bitmapArray.size(); i ++) {
			//Draw the specified bitmap, scaling/translating automatically to fill the destination rectangle.
			canvas.drawBitmap((Bitmap) bitmapArray.get(i), bitX.get(i), bitY.get(i), null);
		}
	}
	
	public void setGesture(Activity activity) {
		gestures = new GestureDetector(activity, new GestureListener(this)); 
	}
	
	public void setCheeseId(int id) {
		cheese_id = id;
	}
	
	public int getCheeseId(int id) {
		return cheese_id;
	}
	
	public void drawBitmap() {
		if(cheese_id != -1) {
    		cheese = BitmapFactory.decodeResource(getResources(), cheeseIds[cheese_id]);
    		cheese = Bitmap.createScaledBitmap(cheese, cheeseSize, cheeseSize, true);
    		bitId.add(cheese_id);
    		bitmapArray.add(cheese);
    		invalidate();
		} 
	}
	
	/* get X coordinate array of bitmap */
	public float[] getXArray() {
		float[] x = new float[bitX.size()];
		for(int i = 0; i < bitX.size(); i ++) {
			x[i] = bitX.get(i);
		}
		return x;
	}
	
	/* get Y coordinate array of bitmap */
	public float[] getYArray() {
		float[] y = new float[bitY.size()];
		for(int i = 0; i < bitY.size(); i ++) {
			y[i] = bitY.get(i);
		}
		return y;
	}
	
	/* get cheeseId array */
	public int[] getCheeseIdArray() {
		int[] id = new int[bitId.size()];
		for(int i = 0; i < bitId.size(); i ++) {
			id[i] = bitId.get(i);
		}
		return id;
	}
	
	@Override
	//Analyzes the given motion event and if applicable triggers the appropriate callbacks on the GestureDetector.OnGestureListener supplied.
	public boolean onTouchEvent(MotionEvent event) {
	//	determine if the event happens inside the view
	return gestures.onTouchEvent(event);
	
	}
	
	/*
	 * GestureListener class, respond to onTouchEvent in CheeseView class with different gesture listener.
	 */
	private class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
		CheeseView view;
		public GestureListener(CheeseView view) {
			this.view = view;
		}
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.v(DEBUG_TAG, "onDoubleTap");
			return false;
		}
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public boolean onDown(MotionEvent e) {
			Log.v(DEBUG_TAG, "onDown");
			bitX.add(e.getX(fingerCount) - cheeseSize/2);
			bitY.add(e.getY(fingerCount) - cheeseSize/2);
			drawBitmap();
			return true;
		}
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2,
				float velocityX, float velocityY) {
			Log.v(DEBUG_TAG, "OnFling");
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
		
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.v(DEBUG_TAG, "onScroll");
			return false;
		}
		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
}
