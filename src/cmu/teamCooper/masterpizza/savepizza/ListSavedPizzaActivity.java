package cmu.teamCooper.masterpizza.savepizza;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import cmu.teamCooper.masterpizza.MainActivity;
import cmu.teamCooper.masterpizza.R;

public class ListSavedPizzaActivity extends ListActivity 
{
	
	public static final String DEBUG_TAG = "list pizza";
	public static final String ROW_ID = "row_id"; // Intent extra key
	private ListView pizzaListView; // the ListActivity's ListView
	private CursorAdapter pizzaAdapter; // adapter for ListView

// called when the activity is first created
@Override
public void onCreate(Bundle savedInstanceState) 
{
   super.onCreate(savedInstanceState); // call super's onCreate
   
   pizzaListView = getListView(); // get the built-in ListView
   
   //add back button
   Button btn = new Button(this);
   btn.setText("Back");
   btn.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		backToMain();
	}
});
   pizzaListView.addHeaderView(btn);
   
   
   pizzaListView.setOnItemClickListener(viewPizzaListener);      

   // map each pizza's name to a TextView in the ListView layout
   String[] from = new String[] { "name"};
   int[] to = new int[] { R.id.pizzaTextView};	
   pizzaAdapter = new SimpleCursorAdapter(
      ListSavedPizzaActivity.this, R.layout.pizza_list_entry, null, from, to);
   
   // set pizzaView's adapter
   setListAdapter(pizzaAdapter); 
   ((SimpleCursorAdapter) pizzaAdapter).setViewBinder(new MyViewBinder());
   
}

public void backToMain() {
	Intent intent = new Intent(this, MainActivity.class);
	startActivity(intent);
	finish();
}

@Override
protected void onResume() 
{
   super.onResume(); // call super's onResume method
   
    // create new GetPizzasTask and execute it 
    new GetPizzasTask().execute((Object[]) null);
 } 

@Override
protected void onStop() 
{
   Cursor cursor = pizzaAdapter.getCursor(); // get current Cursor
   
   if (cursor != null) 
      cursor.deactivate(); // deactivate it
   
   pizzaAdapter.changeCursor(null); // adapted now has no Cursor
   super.onStop();
} 

protected void onDestroy() {
		super.onDestroy();
		Log.v(DEBUG_TAG, "destroy");
}

// performs database query outside GUI thread
private class GetPizzasTask extends AsyncTask<Object, Object, Cursor> 
{
   DatabaseConnector databaseConnector = 
      new DatabaseConnector(ListSavedPizzaActivity.this);

   // perform the database access
   @Override
   protected Cursor doInBackground(Object... params)
   {
      databaseConnector.open();

      // get a cursor containing call pizzas
      return databaseConnector.getAllPizzas(); 
   } // end method doInBackground

   // use the Cursor returned from the doInBackground method
   @Override
   protected void onPostExecute(Cursor result)
   {
      pizzaAdapter.changeCursor(result); // set the adapter's Cursor
      databaseConnector.close();
   } // end method onPostExecute
} // end class GetPizzasTask
   


// event listener that responds to the user touching a pizza's name
// in the ListView

  OnItemClickListener viewPizzaListener = new OnItemClickListener() 
 {
   public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
      long arg3) 
   {
      // create an Intent to launch the ViewPizza Activity
      Intent viewPizza = 
         new Intent(ListSavedPizzaActivity.this, ViewPizzaActivity.class);
      
      // pass the selected pizza's row ID as an extra with the Intent
      viewPizza.putExtra(ROW_ID, arg3);
      startActivity(viewPizza); 
   } 
}; // end viewPizzaListener


private class MyViewBinder implements SimpleCursorAdapter.ViewBinder {

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		int viewId = view.getId();
		switch(viewId) {
			case R.id.pizzaTextView:
				TextView text = (TextView)view;
				text.setText(cursor.getString(columnIndex));
				break;
		}
		return true;
	}
	
}

} 


