package cmu.teamCooper.masterpizza;

import cmu.teamCooper.masterpizza.createpizza.ChooseCrustActivity;
import cmu.teamCooper.masterpizza.savepizza.ListSavedPizzaActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static final String DEBUG_TAG = "main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
        	setContentView(R.layout.activity_main);
        } catch(Exception e) {
			Toast.makeText(getBaseContext(), "Memory problem", Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, MainActivity.class));
        } finally {
        	
        }
        
        Log.v(DEBUG_TAG, "main create");
    }
    
    protected void onDestroy() {
    	super.onDestroy();
    	Log.v(DEBUG_TAG, "main destroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void chooseCrust (View view){
    	Intent intent = new Intent(this, ChooseCrustActivity.class);
    	startActivity(intent);
    }
    
    public void myPizza(View view) {
    	Intent intent = new Intent(this, ListSavedPizzaActivity.class);
    	startActivity(intent);
    }
}
