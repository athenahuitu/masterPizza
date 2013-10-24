package cmu.teamCooper.masterpizza;

import cmu.teamCooper.masterpizza.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class ViewForMeat extends View {
	/* private instance variables */
	private float[] meatXArray;
	private float[] meatYArray;
	private int[] meatIdArray;
	private int meatSize;
	private int[] meatIds = new int[] {
			R.drawable.meat1, R.drawable.meat2, R.drawable.meat3
	};
	
	public ViewForMeat(Context context) {
		super(context);
	}
	
	public ViewForMeat(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        float density = metrics.density;
        meatSize = DpToPix.dpToPix(30, density);
    }
	
	protected void onDraw(Canvas canvas) {
		Bitmap meat;
		for(int i = 0; i < meatXArray.length; i ++) {
			meat = BitmapFactory.decodeResource(getResources(), meatIds[meatIdArray[i]]);
    		meat = Bitmap.createScaledBitmap(meat, meatSize, meatSize, true);
    		canvas.drawBitmap(meat, meatXArray[i], meatYArray[i], null);
		}
	}
	
	public void setMeat(float[] meatXArray, float[] meatYArray, int[] meatIdArray) {
		this.meatXArray = meatXArray;
		this.meatYArray = meatYArray;
		this.meatIdArray = meatIdArray;
	}
}
