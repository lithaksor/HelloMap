package com.mapass.mapass;

import static android.provider.BaseColumns._ID;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MySQLiteHelper extends SQLiteOpenHelper {

	  private static final String DATABASE_NAME = "markers.db";
	  private static final int DATABASE_VERSION = 4; // LA 4 ES LA QUE VALE
	  
	  public MySQLiteHelper(Context context) {
	    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		 String query = "CREATE TABLE IF NOT EXISTS markers("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
					 "title TEXT, description TEXT, lat double, long double, color text);";
	    database.execSQL(query);
	    // Quizás una tabla para guardar rutas?
	  }
	  public ArrayList<MarkerWithId> readMarkers(GoogleMap googleMap){
		  ArrayList<MarkerWithId> marcadores = new ArrayList<MarkerWithId>();
		  
		  String columnas[]= {_ID, "title", "description", "lat","long","color"};
		  Cursor c = this.getReadableDatabase().query("markers",columnas, null, null, null, null, null);
		  
		  if (c.getCount()>0){
			  c.moveToFirst();
			  for (int i=0;i<c.getCount();i++){
				  // Get value from columns
				  String title = c.getString(1);
				  if(title.toString().equals(""))
					  title=" ";
				  String description = c.getString(2);
				  String color = c.getString(5);
				  int colorPosition=0;
				  
				  float colorChosen = BitmapDescriptorFactory.HUE_RED;
				  if(color.equals("Azul") || color.equals("Blue") ){
			        	colorChosen = BitmapDescriptorFactory.HUE_AZURE;
			        	colorPosition = 0;
				  }
			        else if (color.equals("Magenta")){
			        	colorChosen = BitmapDescriptorFactory.HUE_MAGENTA;
			        	colorPosition = 1;
			        }
			        else if (color.equals("Cyan")){

			        	colorChosen = BitmapDescriptorFactory.HUE_CYAN;
			        	colorPosition = 2;
			        }
			        else if (color.equals("Rojo")|| color.equals("Red")){
			        	colorChosen = BitmapDescriptorFactory.HUE_RED;
			        	colorPosition = 3;
			        	
			        }
			        else if (color.equals("Rosa")|| color.equals("Rose")){
			        	colorChosen = BitmapDescriptorFactory.HUE_ROSE;
			        	colorPosition = 4;
			        	
			        }
			        else if (color.equals("Violeta")|| color.equals("Violet")){

			        	colorChosen = BitmapDescriptorFactory.HUE_VIOLET;
			        	colorPosition = 5;
			        }
			        else if (color.equals("Amarillo")|| color.equals("Yellow")){
			        	colorChosen = BitmapDescriptorFactory.HUE_YELLOW;
			        	colorPosition = 6;
			        	
			        }
			        else if (color.equals("Verde")|| color.equals("Green")){
			        	colorChosen = BitmapDescriptorFactory.HUE_GREEN;
			        	colorPosition = 7;
			        }
			        else{
			        	colorChosen = BitmapDescriptorFactory.HUE_ORANGE;
			        	colorPosition = 8;
			        	
			        }

				  long id = c.getInt(0);
				  double latitude =c.getDouble(3);
				  double longitude = c.getDouble(4);
				  
				  LatLng position = new LatLng(latitude,longitude);
				  
				  MarkerWithId marcador = new MarkerWithId();
				  marcador.marker = googleMap.addMarker(new MarkerOptions()
		        	.position(position)
		        	.title(title)
		        	.snippet(description)
		        	.icon(BitmapDescriptorFactory.defaultMarker(colorChosen)));
				  	marcador.id = id;
				    marcador.colorNumber = colorChosen;
				    marcador.colorPosition = colorPosition;
				  marcadores.add(marcador);
				  
				  
				  c.moveToNext();
			  }
			  c.close();
		  }
		  
		  return marcadores;
	  }
	  public long addMarker(MarkerWithId markertoadd, String color){
			ContentValues valores = new ContentValues();
			valores.put("title",markertoadd.marker.getTitle());
			valores.put("description",markertoadd.marker.getSnippet());
			valores.put("lat",markertoadd.marker.getPosition().latitude);
			valores.put("long",markertoadd.marker.getPosition().longitude);
			valores.put("color",color);

			this.getWritableDatabase().insert("markers", null, valores);
			
			// return del _ID del marker recién creado
			String sql = "SELECT * FROM markers WHERE _ID = (SELECT MAX(_ID)  FROM markers);";
			final Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
			long id = 0;
			if (cursor != null) {
			    try {
			        if (cursor.moveToFirst()) {
			        	id = cursor.getInt(0);
			        }
			    } finally {
			        cursor.close();
			    }
			}
        	return id;
	  }
	  public void updateMarker(MarkerWithId markertoupdate, String color){
		  ContentValues valores = new ContentValues();
		  valores.put("title",markertoupdate.marker.getTitle());
		  valores.put("description",markertoupdate.marker.getSnippet());
		  valores.put("lat",markertoupdate.marker.getPosition().latitude);
		  valores.put("long",markertoupdate.marker.getPosition().longitude);
		  valores.put("color",color);

		  //this.getWritableDatabase().update("markers", valores, "lat="+markertoupdate.getPosition().latitude+" AND long="+markertoupdate.getPosition().longitude+";", null);
		  this.getWritableDatabase().update("markers", valores, "_ID="+markertoupdate.id, null);
			 
	  }
	  public void deleteMarker(MarkerWithId markertodelete){
		  this.getWritableDatabase().delete("markers", "_ID="+markertodelete.id, null); 
	  }
	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS markers");
	    
	    onCreate(db);
	  }
}