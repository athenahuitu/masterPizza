package cmu.teamCooper.masterpizza.savepizza;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector 
{
   // database name
   private static final String DATABASE_NAME = "SavedPizzas";
   private static final String TABLE_NAME = "pizzas";
   private SQLiteDatabase database; // database object
   private DatabaseOpenHelper databaseOpenHelper; // database helper

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context) 
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   } // end DatabaseConnector constructor

   // open the database connection
   public void open() throws SQLException 
   {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();
   } // end method open

   // close the database connection
   public void close() 
   {
      if (database != null)
         database.close(); // close the database connection
   } // end method close

   // inserts a new pizza in the database
   public void insertPizza(String name, byte[] image) 
   {
      ContentValues newPizza = new ContentValues();
      newPizza.put("name", name);
      newPizza.put("image", image);
      
      open(); // open the database
      database.insert(TABLE_NAME, null, newPizza);
      close(); // close the database
   } // end method insertPizza

   // inserts a new pizza in the database
   public void updatePizza(long id, String name, byte[] image, String photoPath) 
   {
      ContentValues editPizza = new ContentValues();
      editPizza.put("name", name);
      editPizza.put("image", image);
      editPizza.put("photoPath", photoPath);
      
      open(); // open the database
      database.update(TABLE_NAME, editPizza, "_id=" + id, null);
      close(); // close the database
   } // end method updatePontact

   // return a Cursor with all pizza information in the database
   public Cursor getAllPizzas() 
   {
      return database.query(TABLE_NAME, new String[] {"_id", "name"}, 
         null, null, null, null, "name");
   } // end method getAllPizzas

   // get a Cursor containing all information about the pizza specified
   // by the given id
   public Cursor getOnePizza(long id) 
   {
      return database.query(
         TABLE_NAME, null, "_id=" + id, null, null, null, null);
   } 
   
   //insert the image path of the pizza
   public void insertPhoto(long id, String path) {
	   open();
	   database.execSQL("UPDATE pizzas SET photoPath = " + path + " WHERE _id = " + id);
	   close();
   }
   
   //get the image path of the pizza
   public Cursor getPhotoPath(long id) {
	   return database.query(
		         TABLE_NAME, new String[] {"_id","photoPath"}, "_id=" + id, null, null, null, null);
   }

   // delete the pizza specified by the given String name
   public void deletePizza(long id) 
   {
      open(); // open the database
      database.delete(TABLE_NAME, "_id=" + id, null);
      close(); // close the database
   } 
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
      // public constructor
      public DatabaseOpenHelper(Context context, String name,
         CursorFactory factory, int version) 
      {
         super(context, name, factory, version);
      } // end DatabaseOpenHelper constructor

      // creates the pizzas table when the database is created
      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         // query to create a new table named pizzas
         String createQuery = "CREATE TABLE pizzas" +
            "(_id integer primary key autoincrement," +
            "name TEXT, image BLOB, photoPath TEXT);";
                  
         db.execSQL(createQuery); // execute the query
      } // end method onCreate

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
    	  database.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
  		  onCreate(database);
      } // end method onUpgrade
   } // end class DatabaseOpenHelper
} // end class DatabaseConnector