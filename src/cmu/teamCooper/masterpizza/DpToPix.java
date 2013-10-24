package cmu.teamCooper.masterpizza;

public class DpToPix {
	public static int dpToPix(float dp, float density) {
		int pixels = (int)(density * dp + 0.5f);
		return pixels;
	}
}