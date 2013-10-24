package cmu.teamCooper.masterpizza.savepizza;

import cmu.teamCooper.masterpizza.BackgroundMusicService;
import cmu.teamCooper.masterpizza.MainActivity;
import cmu.teamCooper.masterpizza.R;
import cmu.teamCooper.masterpizza.createpizza.PizzaDoneActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SavePizzaActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	setContentView(R.layout.activity_save_pizza);
        } catch(Exception e) {
        	Toast.makeText(getBaseContext(), "Memory problem", Toast.LENGTH_LONG).show();
        	finish();
        }
        
    }

    public void onSave(View view) {
    	
    	Intent intent = getIntent();
    	byte[] byteArray= intent.getByteArrayExtra(PizzaDoneActivity.pizzaPic);
    	
    	//start backgound service
        Intent service = new Intent(SavePizzaActivity.this,BackgroundMusicService.class);  
        stopService(service);
    	
    	//store in database
    	DatabaseConnector db = new DatabaseConnector(this);
    	EditText editText = (EditText)findViewById(R.id.pizzaName);
    	String pizzaName = editText.getText().toString();
		db.insertPizza(pizzaName, byteArray);
		
		try {
			Intent intent2 = new Intent(this, MainActivity.class);
	        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent2);
		} catch(Exception e) {
			Toast.makeText(getBaseContext(), "Memory problem", Toast.LENGTH_LONG).show();
			finish();
		}
		
		finish();
    }
}
