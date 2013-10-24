package cmu.teamCooper.masterpizza;

import java.util.ArrayList;
import cmu.teamCooper.masterpizza.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class MeatView extends View {
	
	public static final String DEBUG_TAG = "meat view";
	
	/* private instance variables */
	private Bitmap meat;
	private float currentX;
	private float currentY;
	private Matrix translate;
	GestureDetector gestures;
	private Matrix animateStart;
	private OvershootInterpolator animateInterpolator;
	private long startTime;
	private long endTime;
	private float totalAnimDx;
	private float totalAnimDy;
	private boolean onDown;
	private int currentChosenMeat;
	private int meat_id;
	private int meatSize;
	private int pizzaSize;
	private ArrayList<Float> bitX;
	private ArrayList<Float> bitY;
	private ArrayList<Integer> bitId;
	private ArrayList<Bitmap> bitmapArray;
	
	
	private int[] meatIds = new int[] {
			R.drawable.meat1, R.drawable.meat2, R.drawable.meat3
	};

	public MeatView(Context context) {
		super(context);
	}
	 
	public MeatView(Context context, AttributeSet attrs) {
		super(context, attrs);
		bitX = new ArrayList<Float>();
		bitY = new ArrayList<Float>();
		bitId = new ArrayList<Integer>();
		bitmapArray = new ArrayList<Bitmap>();
		translate = new Matrix();
		currentX = -1;
		currentY = -1;
		currentChosenMeat = -1;
		meat_id = -1;
		
		//define meatSize;
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        float density = metrics.density;
        meatSize = DpToPix.dpToPix(30, density);
	}
	
	public void setPizzaSize(int pizzaSize) {
		this.pizzaSize = pizzaSize;
	}
	
	protected void onDraw(Canvas canvas) {
		for(int i = 0; i < bitmapArray.size(); i ++) {
			//Draw the specified bitmap, scaling/translating automatically to fill the destination rectangle.
			if(bitX.get(i) > pizzaSize || bitX.get(i) < 0 || bitY.get(i) > pizzaSize || bitY.get(i) < 0) {
				bitmapArray.remove(i);
				bitX.remove(i);
				bitY.remove(i);
				bitId.remove(i);
				onDown = false;
				i--; //because remove action in arrayList will shift all the elements to left
				Log.v(DEBUG_TAG, "remove" + i);
			} else {
				canvas.drawBitmap((Bitmap) bitmapArray.get(i), bitX.get(i), bitY.get(i), null);
				Log.v(DEBUG_TAG, "size " + bitmapArray.size());
			}
		}
	}
	
    public void setGesture(Activity activity) {
    	gestures = new GestureDetector(activity, new GestureListener(this));
    }
    
    public void setMeatId(int id){
    	meat_id = id;
    }
    
    public int getMeatId() {
    	return meat_id;
    }
    
	public void randomDraw() {
	 	currentX = (float) ((pizzaSize-meatSize) * Math.random());
    	currentY = (float) ((pizzaSize-meatSize) * Math.random());
    	meat = BitmapFactory.decodeResource(getResources(), meatIds[meat_id]);
    	meat = Bitmap.createScaledBitmap(meat, meatSize, meatSize, true);
    	bitX.add(currentX);
    	bitY.add(currentY);
    	bitId.add(meat_id);
    	bitmapArray.add(meat);
    	invalidate();
	}
		
	public void onMove(float dx, float dy) {
		translate.postTranslate(dx, dy);
	    bitX.set(currentChosenMeat, bitX.get(currentChosenMeat) + dx);
	    bitY.set(currentChosenMeat, bitY.get(currentChosenMeat) + dy);
		invalidate();
	}
	
	public void undo() {
		int  i = bitmapArray.size() - 1;
		bitmapArray.remove(i);
		bitX.remove(i);
		bitY.remove(i);
		bitId.remove(i);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	//	determine if the event happens inside the view
		if(onDown == true) {
			for(int i = bitmapArray.size() - 1; i >= 0; i--) {
	    		if(event.getX() > bitX.get(i) && event.getX() < bitX.get(i) + meatSize &&
	    				event.getY() > bitY.get(i) && event.getY() < bitY.get(i) + meatSize) {
	    				currentChosenMeat = i;
	    		} 
			}
			return gestures.onTouchEvent(event);
		} else {
			for(int i = bitmapArray.size() - 1; i >= 0; i--) {
	    		if(event.getX() > bitX.get(i) && event.getX() < bitX.get(i) + meatSize &&
	    				event.getY() > bitY.get(i) && event.getY() < bitY.get(i) + meatSize) {
	    				currentChosenMeat = i;
	    				return gestures.onTouchEvent(event);
	    		} 
			}
			return false;
		}
	}
	
	public void onAnimateMove(float dx, float dy, long duration) {
		animateStart = new Matrix(translate);
		animateInterpolator = new OvershootInterpolator();
		startTime = System.currentTimeMillis();
		endTime = startTime + duration;
		totalAnimDx = dx;
		totalAnimDy = dy;
		post(new Runnable() {
			@Override
			public void run() {
				onAnimateStep();
			}
			
		});
	}
	
	public void onAnimateStep(){
		long curTime = System.currentTimeMillis();
		float percentTime = (float)(curTime - startTime)/(float)(endTime - startTime);
		float percentDistance = animateInterpolator.getInterpolation(percentTime);
		float curDx = percentDistance * totalAnimDx;
		float curDy = percentDistance * totalAnimDy;
//		translate.set(animateStart);
		onMove(curDx, curDy);
		Log.v(DEBUG_TAG, "We're" + percentDistance + " of the way there!");
		if(percentTime < 1.0f) {
			post(new Runnable() {

				@Override
				public void run() {
					onAnimateStep();
		}
				
			});
		}
	}
	
	
	
	private class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
		MeatView view;
		public GestureListener(MeatView view) {
			this.view = view;
		}
		@Override
		public boolean onDoubleTap(MotionEvent e) {
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
			onDown = true;
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
			onDown = false;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.v(DEBUG_TAG, "onScroll");
			view.onMove(-distanceX, -distanceY);
			return true;
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
	
	/* get meatId array */
	public int[] getmeatIdArray() {
		int[] id = new int[bitId.size()];
		for(int i = 0; i < bitId.size(); i ++) {
			id[i] = bitId.get(i);
		}
		return id;
	}
			
}
