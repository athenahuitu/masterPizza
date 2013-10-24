package cmu.teamCooper.masterpizza;

import cmu.teamCooper.masterpizza.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * create ViewForCheese to draw the cheese created in previous step
 */
public class ViewForCheese extends View {
	
	/* private instance variables */
	private float[] cheeseXArray;
	private float[] cheeseYArray;
	private int[] cheeseIdArray;
	private int cheeseSize;
	private int[] cheeseIds = new int[]{
			R.drawable.cheddar_bitmap, R.drawable.mozzarella_bitmap, R.drawable.monterey_bitmap
	};
	
	public ViewForCheese(Context context) {
		super(context);
	}
	
	public ViewForCheese(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        float density = metrics.density;
        cheeseSize = DpToPix.dpToPix(30, density);
    }
	
	protected void onDraw(Canvas canvas) {
		Bitmap cheese;
		for(int i = 0; i < cheeseXArray.length; i ++) {
			cheese = BitmapFactory.decodeResource(getResources(), cheeseIds[cheeseIdArray[i]]);
    		cheese = Bitmap.createScaledBitmap(cheese, cheeseSize, cheeseSize, true);
    		canvas.drawBitmap(cheese, cheeseXArray[i], cheeseYArray[i], null);
		}
	}
	
	public void setCheese(float[] cheeseXArray, float[] cheeseYArray, int[] cheeseIdArray) {
		this.cheeseXArray = cheeseXArray;
		this.cheeseYArray = cheeseYArray;
		this.cheeseIdArray = cheeseIdArray;
	}
}
