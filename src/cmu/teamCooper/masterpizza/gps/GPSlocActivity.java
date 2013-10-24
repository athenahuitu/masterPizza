package cmu.teamCooper.masterpizza.gps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cmu.teamCooper.masterpizza.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class GPSlocActivity extends MapActivity {
	
	private static final String TAG = "gps";
	
	private MapView mapView;
	private MapController mapController;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private String address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gpsloc);
		
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		  
		mapController = mapView.getController();
		mapController.setZoom(10); 
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		  
		locationListener = new GPSLocationListener();
		  
		locationManager.requestLocationUpdates(
		    LocationManager.GPS_PROVIDER, 
		    0, 
		    0, 
		    locationListener);
		Log.v(TAG, "Listener GPS");
		
		
	}
	
	public void viewPizza(View view) {
    	finish();
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private class GPSLocationListener implements LocationListener {
		  @Override
		  public void onLocationChanged(Location location) {
			    if (location != null) {
			      GeoPoint point = new GeoPoint(
			          (int) (location.getLatitude() * 1E6), 
			          (int) (location.getLongitude() * 1E6));
			      
			      address = ConvertPointToLocation(point);
			      Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
			      
			      mapController.animateTo(point);
			      mapController.setZoom(14);
			      
			      Drawable drawable = getResources().getDrawable(R.drawable.pizzaicon);
		          HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(drawable, GPSlocActivity.this);
		        
		          GeoPoint point1 = new GeoPoint(37398251,-122056217);
		          OverlayItem overlayitem = new OverlayItem(point1, "Pizza Hut!", "Great Pizza! Great Crust! Order call:(650) 968-8100");
		        
		          itemizedoverlay.addOverlay(overlayitem);
		        
		          GeoPoint point2 = new GeoPoint(37388933,-122043343);
		          OverlayItem overlayitem2 = new OverlayItem(point2, "John's Pizza ! ", "Wonderful Pizza Experience! Order call:(650) 969-7272");
		        
		          itemizedoverlay.addOverlay(overlayitem2);
		          
			      //add marker
			      MapOverlay mapOverlay = new MapOverlay();
			      mapOverlay.setPointToDraw(point);
			      List<Overlay> listOfOverlays = mapView.getOverlays();
			      listOfOverlays.clear();
			      listOfOverlays.add(mapOverlay);
			      listOfOverlays.add(itemizedoverlay);
			      mapView.invalidate();
			    }
		    
            }
		  
		  public String ConvertPointToLocation(GeoPoint point) {   
			    String address = "";
			    Geocoder geoCoder = new Geocoder(
			        getBaseContext(), Locale.getDefault());
			    try {
			      List<Address> addresses = geoCoder.getFromLocation(
			        point.getLatitudeE6()  / 1E6, 
			        point.getLongitudeE6() / 1E6, 1);
			 
			      if (addresses.size() > 0) {
			        for (int index = 0; 
				index < addresses.get(0).getMaxAddressLineIndex(); index++)
			          address += addresses.get(0).getAddressLine(index) + " ";
			      }
			    }
			    catch (IOException e) {        
			      e.printStackTrace();
			    }   
			    
			    return address;
			  } 

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
  }

	class MapOverlay extends Overlay
	{
		  private GeoPoint pointToDraw;
		
		  public void setPointToDraw(GeoPoint point) {
		    pointToDraw = point;
		  }
		
		  public GeoPoint getPointToDraw() {
		    return pointToDraw;
		  }
	  
		  @Override
		  public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		    super.draw(canvas, mapView, shadow);           
		
		    // convert point to pixels
		    Point screenPts = new Point();
		    mapView.getProjection().toPixels(pointToDraw, screenPts);
		
		    // add marker
		    Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hearticon);
		    canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 24, null);    
		    return true;
		  }
     } 
	
	protected void onDestroy() {
		locationManager.removeUpdates(locationListener);
    	super.onDestroy();
    }
	
	protected void onPause(){
		locationManager.removeUpdates(locationListener);
		super.onPause();
	}
	
	protected void onStop(){
		locationManager.removeUpdates(locationListener);
		super.onStop();
	}
	
	protected void onResume() {
		super.onResume();
	}
}
