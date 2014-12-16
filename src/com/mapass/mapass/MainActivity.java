package com.mapass.mapass;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ProgressDialog;
/***
 * 
 * @author VictorDeveloper
 * 
 * Prioridades TODO:
 * 
 * 0. Esconder teclado al pulsar algún botón (comprobar antes si está fuera o algo)
 * 1. Solucionar problema con el layout para que se vea bien en los distintos dispositivos moviles
 * 2. Mejorar seguridad del código del SQLite Helper y añadir filtros a los inputs del usuario
 * 3. Poner manual con nuevas opciones
 * 
 ************************************************************************************************************************************************************************************
 * 		
 * 
 * 		 al tocar el botón de modificar punto, que salga obtener dirección, y si le das te sale la dirección del punto en un toast
 * 
 * 		 3) Poner que se pueda compartir un punto
 * 		 
 * 	 	 4) Poder obtener un sitio a partir de una latitud y longitud (y mover el mapa a esa dirección)
 * 
 * 
 * 		 -Añadir marcadores por defecto en la base de datos en la primera ejecución con sitios curiosos
 * 
 * 		
 * 		 -Si metes cierto título que salga un easter egg / meter una palabra y que salga un mensaje (Ejemplo: el titulo contiene casa, 
 * 		  pues mensaje tipo "Bien! añadir tu casa es lo primero!"
 * 		 Detectar idioma automaticamente
 *
 *		 Clave para debug: AIzaSyBC779SVlmsVCfbh9R5wXIqaWxTlr3A4t0
 *		 Clave para release: AIzaSyCwDyaa3WQO4Zqv6VrIrtEQnHW3JG6Cyyw
 *
 *
 */

public class MainActivity extends FragmentActivity implements OnMarkerClickListener, OnClickListener, LocationListener,OnMapLongClickListener, OnInfoWindowClickListener, OnTouchListener   {
    
	
	/**
	 * Globals
	 */
    public GoogleMap googleMap;
    public ArrayList<MarkerWithId> markers;
    private MarkerWithId addMarker;
    private MarkerWithId currentPosition;
    private int currentMarker; 
    public int mapType;
    private LatLng latLng;
    private LatLng onLongClickMarker;
    public MySQLiteHelper handler;
    private String color;
    private int coloruta;
    private boolean modomodificaractivado;
    private boolean modomenuactivado;
    private boolean modotrazarutaactivado;
    private boolean modoaddmarker;
    public String idioma;
    private ViewsInitializer setupViews;
    private MapInitializer setupMap;
    public SharedPreferences prefs;
    SharedPreferences.Editor editor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
        // Initialize variables / ui
        setupViews = new ViewsInitializer(this);
        setupViews.initializeViews();
        setupMap = new MapInitializer(this);
        currentMarker = 0;
        mapType = GoogleMap.MAP_TYPE_HYBRID;
        modomodificaractivado = false;
        modomenuactivado = false;
        modotrazarutaactivado = false;
        modoaddmarker = false;
        
        // Get language
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
        Languages lang = new Languages(this);
        String idiomapreferido = prefs.getString("idioma", "english");
        if( idiomapreferido.equals("english")){
        	lang.toEnglish();
        	idioma = "english";
        }
        else if( idiomapreferido.equals("spanish")){
        	lang.toSpanish();
        	idioma = "spanish";
        }
        else{
        	lang.toSpanish();
        	idioma = "spanish";
        }
     // Find out if its first time executed to show manual or not
        if(prefs.getBoolean("firstTime", true)) {
        	showManual(); 
	        editor.putBoolean("firstTime", false);
	        editor.commit();  
        }
        // Set up google maps
        try{
	        setupMap.initializeMap();
        }
        catch(Exception e){	
        	e.printStackTrace();
        }
    }
    
    /**
     * Buttons actions
     */
	@SuppressWarnings("deprecation")
	public boolean onTouch(View v, MotionEvent event)  {
		int action = event.getAction();
		/**
		 * Insertar el marcador en el mapa (pero sin llegar a guardarlo, sólo a modo de previsualización)
		 */
		//Insert marker
		switch(v.getId()){
			case R.id.buttonAccept:
				if (action==MotionEvent.ACTION_DOWN )
			    {
					v.setBackgroundResource(R.drawable.lightyellowbutton);
			    }
			    if (action==MotionEvent.ACTION_UP)
			    {
			        v.setBackgroundResource(R.drawable.yellowbutton);
			    
					findViewById(R.id.markerTitle).setVisibility(View.INVISIBLE);
			        findViewById(R.id.markerDescription).setVisibility(View.INVISIBLE);
			        findViewById(R.id.buttonAccept).setVisibility(View.INVISIBLE);
			        findViewById(R.id.spinnerColor).setVisibility(View.INVISIBLE);
			        findViewById(R.id.buttonSave).setVisibility(View.VISIBLE);
			        
			        try{
				        // Get chosen color
				        Spinner spinner = (Spinner) findViewById(R.id.spinnerColor);
				        color = spinner.getSelectedItem().toString();
				        float colorChosen = BitmapDescriptorFactory.HUE_RED;
				        if(color.equals("Azul")|| color.equals("Blue"))
				        	colorChosen = BitmapDescriptorFactory.HUE_AZURE;
				        else if (color.equals("Magenta"))
				        	colorChosen = BitmapDescriptorFactory.HUE_MAGENTA;
				        else if (color.equals("Cyan"))
				        	colorChosen = BitmapDescriptorFactory.HUE_CYAN;
				        else if (color.equals("Rosa")|| color.equals("Rose"))
				        	colorChosen = BitmapDescriptorFactory.HUE_ROSE;
				        else if (color.equals("Violeta")|| color.equals("Violet"))
				        	colorChosen = BitmapDescriptorFactory.HUE_VIOLET;
				        else if (color.equals("Rojo")|| color.equals("Red"))
				        	colorChosen = BitmapDescriptorFactory.HUE_RED;
				        else if (color.equals("Verde")|| color.equals("Green"))
				        	colorChosen = BitmapDescriptorFactory.HUE_GREEN;
				        else if (color.equals("Amarillo")|| color.equals("Yellow"))
				        	colorChosen = BitmapDescriptorFactory.HUE_YELLOW;
				        else
				        	colorChosen = BitmapDescriptorFactory.HUE_ORANGE;
				        
				        // Add marker on screen
				        EditText title = (EditText)findViewById(R.id.markerTitle);
				        EditText description = (EditText)findViewById(R.id.markerDescription);
				        addMarker = new MarkerWithId();
				        	addMarker.marker = googleMap.addMarker(new MarkerOptions()
				        	.position(latLng)
				        	.title(title.getText().toString())
				        	.snippet(description.getText().toString())
				        	.icon(BitmapDescriptorFactory.defaultMarker(colorChosen)));
				        	
				        if(idioma.equals("english"))
			            	Toast.makeText(getBaseContext(), "Touch the Save button to keep the marker", Toast.LENGTH_LONG).show();
			            else if(idioma.equals("spanish"))
			            	Toast.makeText(getBaseContext(), "Pulsa el botón Guardar para dejar el marcador", Toast.LENGTH_LONG).show();
				        	
			        }
			        catch(Exception e){
			        	e.printStackTrace();
			        }
			    }
			break;
			
			/**
			 * Guarda el marcador insertado o modificado previamente en la base de datos y en la caché del programa
			 */
			// Save Marker 
			case R.id.buttonSave:
		          if (action==MotionEvent.ACTION_DOWN )
		            {
		                v.setBackgroundResource(R.drawable.lightgreenbutton);
		            }
		            if (action==MotionEvent.ACTION_UP)
		            {
		                v.setBackgroundResource(R.drawable.greenbutton);
		            
					closeMenu();
			        
			        // To save in case its modify/update marker
			        try{
				        EditText editTitle = (EditText) findViewById(R.id.markerTitle);
						EditText editDescription = (EditText) findViewById(R.id.markerDescription);
						Spinner spinner = (Spinner) findViewById(R.id.spinnerColor);
				        color = spinner.getSelectedItem().toString();
						float colorChosen = BitmapDescriptorFactory.HUE_RED; // para hacerlo de los dos idiomas pensar alguna manera
						
				        if(color.equals("Azul")|| color.equals("Blue"))
				        	colorChosen = BitmapDescriptorFactory.HUE_AZURE;
				        else if (color.equals("Rojo")|| color.equals("Red"))
				        	colorChosen = BitmapDescriptorFactory.HUE_RED;
				        else if (color.equals("Magenta"))
				        	colorChosen = BitmapDescriptorFactory.HUE_MAGENTA;
				        else if (color.equals("Cyan"))
				        	colorChosen = BitmapDescriptorFactory.HUE_CYAN;
				        else if (color.equals("Rosa")|| color.equals("Rose"))
				        	colorChosen = BitmapDescriptorFactory.HUE_ROSE;
				        else if (color.equals("Violeta")|| color.equals("Violet"))
				        	colorChosen = BitmapDescriptorFactory.HUE_VIOLET;
				        else if (color.equals("Verde")|| color.equals("Green"))
				        	colorChosen = BitmapDescriptorFactory.HUE_GREEN;
				        else if (color.equals("Amarillo")|| color.equals("Yellow"))
				        	colorChosen = BitmapDescriptorFactory.HUE_YELLOW;
				        else
				        	colorChosen = BitmapDescriptorFactory.HUE_ORANGE;
						
				        if(editTitle.getText().toString().isEmpty()){
				        	if(idioma.equals("spanish"))
					        	editTitle.setText("Sin título");
				        	else
					        	editTitle.setText("Untitled");
				        }
				        
				        addMarker.colorNumber = colorChosen;
						addMarker.colorPosition=spinner.getSelectedItemPosition();
				        
				        // Add marker. If the marker isnt in the array, save it
						if(addMarker!=null && !markers.contains(addMarker)){ //por ahora no se pueden arrastrar porque luego habria que gaurdar la nueva posición, lo dej para el futuro
							addMarker.id=handler.addMarker(addMarker, color);
							
							markers.add(addMarker);
							addMarker=null;	
							modoaddmarker = false;
							
							if(idioma.equals("spanish"))
								Toast.makeText(this, "Marcador guardado", Toast.LENGTH_SHORT).show();
							else
								Toast.makeText(this, "Marker saved", Toast.LENGTH_SHORT).show();
						}
				        
						// Update marker. If the marker is already on the array, it means its being modified
						if (markers.contains(addMarker)){
					        addMarker.marker.setTitle(editTitle.getText().toString());
							addMarker.marker.setSnippet(editDescription.getText().toString());
							addMarker.marker.setIcon(BitmapDescriptorFactory.defaultMarker(colorChosen));
							if(idioma.equals("spanish"))
								Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
							else 
								Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
							
							
							handler.updateMarker(addMarker, color);
							modomodificaractivado=false;
						}
						
						//Make map draggable again
			    		UiSettings mapInterface = googleMap.getUiSettings();
			    		mapInterface.setScrollGesturesEnabled(true);
			    		
						// Reset edittexts values
						if(idioma.equals("spanish")){
							editTitle.setText("Título");
							editDescription.setText("Descripción");
						}
						else{
							editTitle.setText("Title");
							editDescription.setText("Description");
						}
			        }
			        catch(Exception e){
			        	e.printStackTrace();
			        }
			    }
			break;
			
			/**
			 * Cancela operación de insertado o modificado
			 */
			case R.id.buttonCancel:
	            if (action==MotionEvent.ACTION_DOWN )
	            {
	                v.setBackgroundResource(R.drawable.lightredbutton);
	            }
	            if (action==MotionEvent.ACTION_UP)
	            {
	                v.setBackgroundResource(R.drawable.redbutton);
	            
			        closeMenu();
			        View buttonTrazaruta = findViewById(R.id.buttonTrazaruta);
			        View buttonUpdateMarker = findViewById(R.id.buttonUpdateMarker);
			        View buttonStreetView = findViewById(R.id.ButtonStreetView);
			        
			        if (modomodificaractivado){
			    		closeMenu();
			        	buttonTrazaruta.setVisibility(View.VISIBLE);
			    		buttonUpdateMarker.setVisibility(View.VISIBLE);
			    		buttonStreetView.setVisibility(View.VISIBLE);
			    		
			    		modomodificaractivado = false;
			    		modomenuactivado = true;
			    		
			    		 try{
					        	
						        // Undo posible changes from edittexts
						        EditText editTitle = (EditText) findViewById(R.id.markerTitle);
								EditText editDescription = (EditText) findViewById(R.id.markerDescription);
								editTitle.setText(markers.get(currentMarker).getMarker().getTitle());
								editDescription.setText(markers.get(currentMarker).getMarker().getSnippet());
								
					        }
					        catch(Exception e){
					        	e.printStackTrace();
					        }
			    		
			    	}
			        else if (modotrazarutaactivado){
			        	buttonTrazaruta.setVisibility(View.VISIBLE);
			    		buttonUpdateMarker.setVisibility(View.VISIBLE);
			    		buttonStreetView.setVisibility(View.VISIBLE);
			    		
			        	// Cerrar menu trazaruta
			        	View menutrazaruta = findViewById(R.id.buttonCancel);
			        	menutrazaruta.setVisibility(View.GONE);
			        	menutrazaruta = findViewById(R.id.buttonPositionToMarker);
			        	menutrazaruta.setVisibility(View.GONE);
			        	menutrazaruta = findViewById(R.id.buttonMarkerToMarker);
			        	menutrazaruta.setVisibility(View.GONE);
			        	menutrazaruta = findViewById(R.id.spinnerColor);
			        	menutrazaruta.setVisibility(View.INVISIBLE);
			        	menutrazaruta = findViewById(R.id.spinnerMarkers);
			        	menutrazaruta.setVisibility(View.INVISIBLE);
			        	menutrazaruta = findViewById(R.id.radioGroup1);
			        	menutrazaruta.setVisibility(View.GONE);
			        	
			        	modotrazarutaactivado = false;
			    		modomenuactivado = true;
			        }
			        else if(modoaddmarker){
			        	// Make the map draggable again
			        	closeMenu();
			        	modoaddmarker = false;
			    		UiSettings mapInterface = googleMap.getUiSettings();
			    		mapInterface.setScrollGesturesEnabled(true);
			    		
			    		// Delete marker if it was inserted but not saved
				        if (addMarker != null && !markers.contains(addMarker))
				        	addMarker.marker.remove();
			        }
	            }
			break;
			/**
			 * Eliminar marcador seleccionado
			 */
			case R.id.buttonDeletemarker:
	            if (action==MotionEvent.ACTION_DOWN )
	            {
	                v.setBackgroundResource(R.drawable.lightbluebutton);
	            }
	            if (action==MotionEvent.ACTION_UP)
	            {
	                v.setBackgroundResource(R.drawable.bluebutton);
	            
					try{
						AlertDialog confirm = new AlertDialog.Builder(this).create();
						if(idioma.equals("spanish")){
							confirm.setTitle("Confirma");
							confirm.setMessage("¿Estás segur@ de querer eliminar el marcador?");
							confirm.setButton("Sí", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									deleteMarker();
									modomodificaractivado=false;
									closeMenu();
									
									//Make map draggable again
						    		UiSettings mapInterface = googleMap.getUiSettings();
						    		mapInterface.setScrollGesturesEnabled(true);
								}
							});
							confirm.setButton2("No", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {}
							});
						}
						else{
							confirm.setTitle("Confirm");
							confirm.setMessage("Are you sure you want to delete the marker?");
							confirm.setButton("Yes", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									deleteMarker();
									modomodificaractivado=false;
									closeMenu();
									
									//Make map draggable again
						    		UiSettings mapInterface = googleMap.getUiSettings();
						    		mapInterface.setScrollGesturesEnabled(true);
								}
							});
							confirm.setButton2("No", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {}
							});
						}
						confirm.show();
					}
					catch(Exception e){
			        	e.printStackTrace();
					}
				}
			break;
				
			/**
			 * Realiza y muestra el resultado de la dirección buscada
			 */
			case R.id.buttonOK:
				if (action==MotionEvent.ACTION_DOWN )
	            {
	                v.setBackgroundResource(R.drawable.lightgobutton);
	            }
	            if (action==MotionEvent.ACTION_UP)
	            {
	                v.setBackgroundResource(R.drawable.gobutton);
					try{
						if(isNetworkAvailable() || is3gAvailable()){ 
							EditText lugarbuscado = (EditText) findViewById(R.id.editSearch);
							String aBuscar = lugarbuscado.getText().toString().trim();
							aBuscar = lugarbuscado.getText().toString().trim();
							
							if(!aBuscar.equals("")){
								aBuscar = aBuscar.replace(" ", "+");
								new FindByAddress().execute(aBuscar);
								
							}
							else{
								if(idioma.equals("english"))
									Toast.makeText(MainActivity.this, "Input a valid address", Toast.LENGTH_SHORT).show();
								else if(idioma.equals("spanish"))
									Toast.makeText(MainActivity.this, "Introduce una dirección válida", Toast.LENGTH_SHORT).show();
							}
						}
						else{
							if(idioma.equals("english"))
								Toast.makeText(MainActivity.this, "You need to have access to internet in order to find a place", Toast.LENGTH_SHORT).show();
							else if(idioma.equals("spanish"))
								Toast.makeText(MainActivity.this, "Necesitas tener acceso a internet para buscar un lugar", Toast.LENGTH_SHORT).show();
						}
					}catch(Exception e){
							if(idioma.equals("english"))
								Toast.makeText(this, "Error while trying to find location", Toast.LENGTH_SHORT).show();
							else if(idioma.equals("spanish"))
								Toast.makeText(this, "Error al intentar encontrar la dirección", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}		
	            }
			break;
			
			
				// Muestra el menú modificar que antes estaba en onwindowinfo
				case R.id.buttonUpdateMarker:
			        if (action==MotionEvent.ACTION_DOWN )
			        {
			            v.setBackgroundResource(R.drawable.lightyellowbutton);
			        }
			        if (action==MotionEvent.ACTION_UP)
			        {
			            v.setBackgroundResource(R.drawable.yellowbutton);
			        
					if(!markers.isEmpty()){
						// Remove main menu
				        findViewById(R.id.buttonTrazaruta).setVisibility(View.GONE);
				        findViewById(R.id.buttonUpdateMarker).setVisibility(View.GONE);
				        findViewById(R.id.ButtonStreetView).setVisibility(View.INVISIBLE);
				        
				        // Show update menu
						findViewById(R.id.markerTitle).setVisibility(View.VISIBLE);
						findViewById(R.id.buttonDeletemarker).setVisibility(View.VISIBLE);
				        findViewById(R.id.markerDescription).setVisibility(View.VISIBLE);
				        findViewById(R.id.buttonSave).setVisibility(View.VISIBLE);
				        findViewById(R.id.buttonCancel).setVisibility(View.VISIBLE);
				        findViewById(R.id.spinnerColor).setVisibility(View.VISIBLE);
				        findViewById(R.id.buttonLeft).setVisibility(View.INVISIBLE);
				        findViewById(R.id.buttonRight).setVisibility(View.INVISIBLE);
				        
				        modomodificaractivado = true;
				        modomenuactivado = false;
				        
			        }
					else if(idioma.equals("spanish"))
						Toast.makeText(this, "No tienes ningún marcador añadido", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(this, "You have no markers", Toast.LENGTH_SHORT).show();
			        }
				break;
				
				
				// Botón que muestra el menú para trazar ruta
				case R.id.buttonTrazaruta:
		            if (action==MotionEvent.ACTION_DOWN )
		            {
		                v.setBackgroundResource(R.drawable.lightpurplebutton);
		            }
		            if (action==MotionEvent.ACTION_UP)
		            {
		                v.setBackgroundResource(R.drawable.purplebutton);
		            
					// Remove main menu
			        findViewById(R.id.buttonTrazaruta).setVisibility(View.GONE);
			        findViewById(R.id.buttonUpdateMarker).setVisibility(View.GONE);
			        findViewById(R.id.ButtonStreetView).setVisibility(View.INVISIBLE);
			        
			        
					// hacer visibles boton de hacer ruta
					findViewById(R.id.buttonCancel).setVisibility(View.VISIBLE);
			        findViewById(R.id.buttonPositionToMarker).setVisibility(View.VISIBLE);
			        findViewById(R.id.buttonMarkerToMarker).setVisibility(View.VISIBLE);
					findViewById(R.id.spinnerColor).setVisibility(View.VISIBLE);
			        findViewById(R.id.radioGroup1).setVisibility(View.VISIBLE);
			        findViewById(R.id.spinnerMarkers).setVisibility(View.VISIBLE);
			        
			        // Poner el radio button seleccionado

		    		RadioButton r = (RadioButton) findViewById(R.id.radioAndando);
		    		r.setSelected(true);
			        
			        // llenar spinner con los marcadores
			        ArrayList<String> titles = new ArrayList<String>();
			        for (int i = 0; i < markers.size(); i++)
			        {
			        	titles.add(markers.get(i).getMarker().getTitle());
			        }
			        Collections.sort(titles, String.CASE_INSENSITIVE_ORDER);
			        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			                android.R.layout.simple_spinner_item, titles);

			        Spinner spinmarcadores = (Spinner)findViewById(R.id.spinnerMarkers);
			        spinmarcadores.setAdapter(adapter);
			        spinmarcadores.setSelection(0);
			        
					modotrazarutaactivado = true;
					modomenuactivado = false;
		            }
				break;
				
				//Botón para trazar ruta desde posición actual a marcador seleccionado
				case R.id.buttonPositionToMarker:
					if (action==MotionEvent.ACTION_DOWN )
		            {
		                v.setBackgroundResource(R.drawable.lightgreybutton);
		            }
		            if (action==MotionEvent.ACTION_UP)
		            {
		                v.setBackgroundResource(R.drawable.greybutton);
						AlertDialog confirm = new AlertDialog.Builder(this).create();
						if(idioma.equals("spanish")){
							if(currentPosition!=null){
								confirm.setTitle("Confirma");
								confirm.setMessage("¿Trazar ruta desde tu posición actual a " + currentPosition.marker.getTitle() +"?");
								confirm.setButton("Sí", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// Close traceroute menu 
										findViewById(R.id.buttonCancel).setVisibility(View.INVISIBLE);
							    		findViewById(R.id.spinnerColor).setVisibility(View.INVISIBLE);
							    		findViewById(R.id.spinnerMarkers).setVisibility(View.INVISIBLE);
							    		findViewById(R.id.buttonPositionToMarker).setVisibility(View.GONE);
							    		findViewById(R.id.radioGroup1).setVisibility(View.GONE);
							    		findViewById(R.id.buttonMarkerToMarker).setVisibility(View.GONE);
							    		
										final Spinner spinMarcadores = (Spinner) findViewById(R.id.spinnerMarkers);
							    		RadioButton r = (RadioButton) findViewById(R.id.radioAndando);
								        
							    		// obtener color elegido
							    		Spinner spinner = (Spinner) findViewById(R.id.spinnerColor);
								        color = spinner.getSelectedItem().toString();
								        if(color.equals("Azul")|| color.equals("Blue"))
								        	coloruta = Color.BLUE;
								        else if (color.equals("Magenta"))
								        	coloruta = Color.MAGENTA;
								        else if (color.equals("Cyan"))
								        	coloruta = Color.CYAN;
								        else if (color.equals("Rojo")|| color.equals("Red"))
								        	coloruta = Color.RED;
								        else if (color.equals("Verde")|| color.equals("Green"))
								        	coloruta = Color.GREEN;
								        else if (color.equals("Amarillo")|| color.equals("Yellow"))
								        	coloruta = Color.YELLOW;
								        else
								        	coloruta = Color.LTGRAY;
								        
								     // obtener el objeto marcador elegido en el spinner
							    		String destino = spinMarcadores.getSelectedItem().toString();
								        
							    		// Obtener si va andando o en coche
							    		String modo;
							    		if(r.isSelected())
							    			modo = "walking";
							    		else
							    			modo = "driving";
							    		
							    		// Get & Draw path
							    		for(int i=0;i<markers.size();i++){
							    			if (markers.get(i).getMarker().getTitle().equals(destino)){
							    				// Get & Draw path
												DrawPath dPath = new DrawPath(MainActivity.this, coloruta);
							                    String path = dPath.makeURL(
							                    		currentPosition.marker.getPosition().latitude, 
							                    		currentPosition.marker.getPosition().longitude,
							                    		markers.get(i).getMarker().getPosition().latitude,
							                    		markers.get(i).getMarker().getPosition().longitude, modo);
							                    new connectAsyncTask().execute(path);
							    			}		
							    		}
					                    
										//Make map draggable again
							    		UiSettings mapInterface = googleMap.getUiSettings();
							    		mapInterface.setScrollGesturesEnabled(true);
							    		
							    		modotrazarutaactivado = false;
									}
								});
								confirm.setButton2("No", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {}
								});
								confirm.show();
							}
							else
								Toast.makeText(this, "No se encuentra tu posición actual", Toast.LENGTH_SHORT).show();
						}
						else if(idioma.equals("english")){
							if(currentPosition!=null){
								confirm.setTitle("Confirm");
								confirm.setMessage("Trace route from your current position to " + currentPosition.marker.getTitle() +"?");
								confirm.setButton("Yes", new DialogInterface.OnClickListener() {
										
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// Close traceroute menu
									findViewById(R.id.buttonCancel).setVisibility(View.INVISIBLE);
						    		findViewById(R.id.spinnerColor).setVisibility(View.INVISIBLE);
						    		findViewById(R.id.spinnerMarkers).setVisibility(View.INVISIBLE);
						    		findViewById(R.id.buttonPositionToMarker).setVisibility(View.GONE);
						    		findViewById(R.id.radioGroup1).setVisibility(View.GONE);
						    		findViewById(R.id.buttonMarkerToMarker).setVisibility(View.GONE);
	
									final Spinner spinMarcadores = (Spinner) findViewById(R.id.spinnerMarkers);
						    		RadioButton r = (RadioButton) findViewById(R.id.radioAndando);
						    		
						    		// obtener color elegido
						    		Spinner spinner = (Spinner) findViewById(R.id.spinnerColor);
							        color = spinner.getSelectedItem().toString();
							        if(color.equals("Azul")|| color.equals("Blue"))
							        	coloruta = Color.BLUE;
							        else if (color.equals("Magenta"))
							        	coloruta = Color.MAGENTA;
							        else if (color.equals("Cyan"))
							        	coloruta = Color.CYAN;
							        else if (color.equals("Rojo")|| color.equals("Red"))
							        	coloruta = Color.RED;
							        else if (color.equals("Verde")|| color.equals("Green"))
							        	coloruta = Color.GREEN;
							        else if (color.equals("Amarillo")|| color.equals("Yellow"))
							        	coloruta = Color.YELLOW;
							        else
							        	coloruta = Color.LTGRAY;
						    		
							     // obtener el objeto marcador elegido en el spinner
						    		String destino = spinMarcadores.getSelectedItem().toString();
						    		
						    		// Obtener cual radio button está pulsado
						    		String modo;
						    		if(r.isSelected())
						    			modo = "walking";
						    		else
						    			modo = "driving";
						    		
						    		// Get & Draw path
						    		for(int i=0;i<markers.size();i++){
						    			if (markers.get(i).getMarker().getTitle().equals(destino)){
						    				// Get & Draw path
											DrawPath dPath = new DrawPath(MainActivity.this, coloruta);
						                    String path = dPath.makeURL(
						                    		currentPosition.marker.getPosition().latitude, 
						                    		currentPosition.marker.getPosition().longitude,
						                    		markers.get(i).getMarker().getPosition().latitude,
						                    		markers.get(i).getMarker().getPosition().longitude, modo);
						                    new connectAsyncTask().execute(path);
						    			}		
						    		}
						                    
											//Make map draggable again
								    		UiSettings mapInterface = googleMap.getUiSettings();
								    		mapInterface.setScrollGesturesEnabled(true);
								    		
								    		modotrazarutaactivado = false;
										}
									});
									confirm.setButton2("No", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {}
										});
									confirm.show();
							}
							else 
								Toast.makeText(this, "Can't find your current position", Toast.LENGTH_SHORT).show();
						}
		            }
					break;
					//Trazar ruta de marcador a marcador
				case R.id.buttonMarkerToMarker:
			        if (action==MotionEvent.ACTION_DOWN )
			        {
			            v.setBackgroundResource(R.drawable.lightgreybutton);
			        }
			        if (action==MotionEvent.ACTION_UP)
			        {
			            v.setBackgroundResource(R.drawable.greybutton);
			        
						AlertDialog confirm = new AlertDialog.Builder(this).create();
						final Spinner spinMarcadores = (Spinner) findViewById(R.id.spinnerMarkers);
						if(idioma.equals("spanish")){
							if(spinMarcadores.getCount()>1){
								if(isNetworkAvailable()|| is3gAvailable()){
									confirm.setTitle("Confirma");
									confirm.setMessage("¿Trazar ruta desde " + markers.get(currentMarker).marker.getTitle() 
											+ " a " + spinMarcadores.getSelectedItem().toString() +"?");
									confirm.setButton("Sí", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// Close traceroute menu 
											findViewById(R.id.buttonCancel).setVisibility(View.INVISIBLE);
								    		findViewById(R.id.spinnerColor).setVisibility(View.INVISIBLE);
								    		findViewById(R.id.spinnerMarkers).setVisibility(View.INVISIBLE);
								    		findViewById(R.id.buttonPositionToMarker).setVisibility(View.GONE);
								    		findViewById(R.id.radioGroup1).setVisibility(View.GONE);
								    		findViewById(R.id.buttonMarkerToMarker).setVisibility(View.GONE);
								    		RadioButton r = (RadioButton) findViewById(R.id.radioAndando);
									        
								    		// obtener color elegido
								    		Spinner spinner = (Spinner) findViewById(R.id.spinnerColor);
									        color = spinner.getSelectedItem().toString();
									        if(color.equals("Azul")|| color.equals("Blue"))
									        	coloruta = Color.BLUE;
									        else if (color.equals("Magenta"))
									        	coloruta = Color.MAGENTA;
									        else if (color.equals("Cyan"))
									        	coloruta = Color.CYAN;
									        else if (color.equals("Rojo")|| color.equals("Red"))
									        	coloruta = Color.RED;
									        else if (color.equals("Verde")|| color.equals("Green"))
									        	coloruta = Color.GREEN;
									        else if (color.equals("Amarillo")|| color.equals("Yellow"))
									        	coloruta = Color.YELLOW;
									        else
									        	coloruta = Color.LTGRAY;
								    		
								    		// obtener el objeto marcador elegido en el spinner
								    		String destino = spinMarcadores.getSelectedItem().toString();
								    		
								    		String modo;
								    		if(r.isSelected())
								    			modo = "walking";
								    		else
								    			modo = "driving";
								    		
								    		for(int i=0;i<markers.size();i++){
								    			if (markers.get(i).getMarker().getTitle().equals(destino)){
								    				// Get & Draw path
													DrawPath dPath = new DrawPath(MainActivity.this, coloruta);
								                    String path = dPath.makeURL(
								                    		markers.get(currentMarker).marker.getPosition().latitude, 
								                    		markers.get(currentMarker).marker.getPosition().longitude,
								                    		markers.get(i).getMarker().getPosition().latitude,
								                    		markers.get(i).getMarker().getPosition().longitude, modo);
								                    new connectAsyncTask().execute(path);
								    			}		
								    		}
								    		
											//Make map draggable again
								    		UiSettings mapInterface = googleMap.getUiSettings();
								    		mapInterface.setScrollGesturesEnabled(true);
								    		
								    		modotrazarutaactivado = false;
										}
									});
									confirm.setButton2("No", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {}
									});
									confirm.show();
								}
								else
									Toast.makeText(this, "Necesitas almenos dos marcadores en tu lista", Toast.LENGTH_SHORT).show();
							}
							else
								Toast.makeText(this, "Necesitas internet para trazar una ruta", Toast.LENGTH_SHORT).show();
						}
						else if(idioma.equals("english")){
							if(isNetworkAvailable()|| is3gAvailable()){
								if(spinMarcadores.getCount()>1){
									confirm.setTitle("Confirm");
									confirm.setMessage("Trace route from " + markers.get(currentMarker).marker.getTitle() 
											+ " to " + spinMarcadores.getSelectedItem().toString() +"?");
									confirm.setButton("Yes", new DialogInterface.OnClickListener() {
											
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// Close traceroute menu 
										findViewById(R.id.buttonCancel).setVisibility(View.INVISIBLE);
							    		findViewById(R.id.spinnerColor).setVisibility(View.INVISIBLE);
							    		findViewById(R.id.spinnerMarkers).setVisibility(View.INVISIBLE);
							    		findViewById(R.id.buttonPositionToMarker).setVisibility(View.GONE);
							    		findViewById(R.id.radioGroup1).setVisibility(View.GONE);
							    		findViewById(R.id.buttonMarkerToMarker).setVisibility(View.GONE);
							    		RadioButton r = (RadioButton) findViewById(R.id.radioAndando);
	
							    		// obtener color elegido
							    		Spinner spinner = (Spinner) findViewById(R.id.spinnerColor);
								        color = spinner.getSelectedItem().toString();
								        if(color.equals("Azul")|| color.equals("Blue"))
								        	coloruta = Color.BLUE;
								        else if (color.equals("Magenta"))
								        	coloruta = Color.MAGENTA;
								        else if (color.equals("Cyan"))
								        	coloruta = Color.CYAN;
								        else if (color.equals("Rojo")|| color.equals("Red"))
								        	coloruta = Color.RED;
								        else if (color.equals("Verde")|| color.equals("Green"))
								        	coloruta = Color.GREEN;
								        else if (color.equals("Amarillo")|| color.equals("Yellow"))
								        	coloruta = Color.YELLOW;
								        else
								        	coloruta = Color.LTGRAY;
							    		
							    		// obtener el objeto marcador elegido en el spinner
							    		String destino = spinMarcadores.getSelectedItem().toString();
							    		String modo;
							    		if(r.isSelected())
							    			modo = "walking";
							    		else
							    			modo = "driving";
							    		
							    		// Get & Draw path
							    		for(int i=0;i<markers.size();i++){
							    			if (markers.get(i).getMarker().getTitle().equals(destino)){
							    				// Get & Draw path
												DrawPath dPath = new DrawPath(MainActivity.this, coloruta);
							                    String path = dPath.makeURL(
							                    		markers.get(currentMarker).marker.getPosition().latitude, 
							                    		markers.get(currentMarker).marker.getPosition().longitude,
							                    		markers.get(i).getMarker().getPosition().latitude,
							                    		markers.get(i).getMarker().getPosition().longitude, modo);
							                    new connectAsyncTask().execute(path);
							    			}		
							    		}
							    		//Make map draggable again
							    		UiSettings mapInterface = googleMap.getUiSettings();
							    		mapInterface.setScrollGesturesEnabled(true);
							    		
							    		modotrazarutaactivado = false;
											}
										});
										confirm.setButton2("No", new DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {}
											});
										confirm.show();
								}
								else 
									Toast.makeText(this, "You need at least 2 markers in your list", Toast.LENGTH_SHORT).show();
							}
							else 
								Toast.makeText(this, "You need internet to trace a route", Toast.LENGTH_SHORT).show();
						}
			        }
					break;
				case R.id.ButtonStreetView:
					if (action==MotionEvent.ACTION_DOWN )
			        {
			            v.setBackgroundResource(R.drawable.lightgreenbutton);
			        }
			        if (action==MotionEvent.ACTION_UP)
			        {
			            v.setBackgroundResource(R.drawable.greenbutton);

			            modomenuactivado = false;
			            
			            findViewById(R.id.buttonTrazaruta).setVisibility(View.GONE);
				        findViewById(R.id.buttonUpdateMarker).setVisibility(View.GONE);
				        findViewById(R.id.ButtonStreetView).setVisibility(View.INVISIBLE);
			            
				        UiSettings mapInterface = googleMap.getUiSettings();
			    		mapInterface.setScrollGesturesEnabled(true);
				        
			            Intent i = new Intent (this, StreetView.class);
			            i.putExtra("lat", markers.get(currentMarker).getMarker().getPosition().latitude);
			            i.putExtra("long", markers.get(currentMarker).getMarker().getPosition().longitude);
			            startActivity(i);
			        }
					break;
				case R.id.buttonInsertar:
					if (action==MotionEvent.ACTION_DOWN )
			        {
			            v.setBackgroundResource(R.drawable.lightpurplebutton);
			        }
			        if (action==MotionEvent.ACTION_UP)
			        {
			            v.setBackgroundResource(R.drawable.purplebutton);
						findViewById(R.id.markerTitle).setVisibility(View.VISIBLE);
			            findViewById(R.id.markerDescription).setVisibility(View.VISIBLE);
			            findViewById(R.id.buttonAccept).setVisibility(View.VISIBLE);
			            findViewById(R.id.buttonCancel).setVisibility(View.VISIBLE);
			            findViewById(R.id.spinnerColor).setVisibility(View.VISIBLE);
			            findViewById(R.id.buttonDeletemarker).setVisibility(View.INVISIBLE);
			            findViewById(R.id.buttonLeft).setVisibility(View.INVISIBLE);
			            findViewById(R.id.buttonRight).setVisibility(View.GONE);
			            findViewById(R.id.buttonInsertar).setVisibility(View.INVISIBLE);
			            findViewById(R.id.buttonStreetViewNoMarker).setVisibility(View.INVISIBLE);
			    		
			            EditText title = (EditText)findViewById(R.id.markerTitle);
			            EditText description = (EditText)findViewById(R.id.markerDescription);
			            
		            	title.setFocusable(true);
		            	title.setFocusableInTouchMode(true);
		                description.setFocusable(true);
		                description.setFocusableInTouchMode(true);
			            
			            if(idioma.equals("spanish")){
			            	title.setHint("Título");
			                description.setHint("Descripción");
			            }
			            else{
			            	title.setHint("Title");
			                description.setHint("Description");
			            }
			            
			            UiSettings mapInterface = googleMap.getUiSettings();
			    		mapInterface.setScrollGesturesEnabled(false);
			            
			            latLng = onLongClickMarker;
			            modoaddmarker = true;
			            modomenuactivado = false;
			        }
					break;
				case R.id.buttonStreetViewNoMarker:
					if (action==MotionEvent.ACTION_DOWN )
			        {
			            v.setBackgroundResource(R.drawable.lightyellowbutton);
			        }
			        if (action==MotionEvent.ACTION_UP)
			        {
			            v.setBackgroundResource(R.drawable.yellowbutton);
			            findViewById(R.id.buttonInsertar).setVisibility(View.INVISIBLE);
			            findViewById(R.id.buttonStreetViewNoMarker).setVisibility(View.INVISIBLE);
			            
			            Intent i = new Intent (this, StreetView.class);
			            i.putExtra("lat", onLongClickMarker.latitude);
			            i.putExtra("long", onLongClickMarker.longitude);
			            
			            modomenuactivado = false;
			            
			            UiSettings mapInterface = googleMap.getUiSettings();
			    		mapInterface.setScrollGesturesEnabled(true);
			            
			            startActivity(i);
			        	
			        }
					break;
		}
		return true;
    } // End of click actions
    
	/**
	 * Muestra el menú de buscar por dirección
	 */
	private void showSearchMenu() {
		findViewById(R.id.buttonOK);
		
		if(findViewById(R.id.buttonOK).getVisibility() == View.GONE){
			findViewById(R.id.buttonOK).setVisibility(View.VISIBLE);
			findViewById(R.id.editSearch).setVisibility(View.VISIBLE);
			
			if(idioma.equals("spanish"))
				Toast.makeText(this, "Introduce una dirección y/o ciudad / pais", Toast.LENGTH_SHORT).show();
			else if(idioma.equals("english"))
				Toast.makeText(this, "Enter an address and/or city / country", Toast.LENGTH_SHORT).show();
		}
		else{
			findViewById(R.id.buttonOK).setVisibility(View.GONE);
			findViewById(R.id.editSearch).setVisibility(View.GONE);
		}
	}

	/**
	 * Muestra las instrucciones del programa
	 */
	@SuppressWarnings("deprecation")
	private void showManual() {
	 try{
		AlertDialog help = new AlertDialog.Builder(this).create();
		help.setTitle("Manual");
		if(idioma.equals("spanish"))
			help.setMessage("Bienvenid@ a Mapass. Con ésta aplicación podrás crear y personalizar tus marcadores," +
					"además de consultar tu posición actual. Pulsa en la flecha para aprender lo básico del programa.");
		else
			help.setMessage("Welcome to Mapass. This app will allow you to create and customize your very own markers," +
					"plus you will be able to check your current position. Touch on the arrow to learn the basics.");
		help.setButton("-->", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlertDialog morehelp = new AlertDialog.Builder(MainActivity.this).create();
				morehelp.setTitle("Manual");
				if(idioma.equals("spanish"))
					morehelp.setMessage("Recuerda que para que el dispositivo pueda crear un marcador con tu posición actual debes activar el GPS. Hasta que se detecte tu posición pueden pasar unos minutos (a menos que estés en interior). Si ésto no ocurre no desesperes, acabará por encontrarte.\n" +
							"-Añadir un marcador: Deja pulsado donde quieras ponerlo y se abrirá la interfaz de creación de marcador.\n" +
							"-Modificar o eliminar un marcador: Seleccionar el marcador (bien haciendo click sobre él o bien con las flechas laterales) y pulsar en su cartel. Para eliminar \"Título\" y \"Descripción\" de los campos a rellenar hay que tocar dos veces en ellos.\n" +
							"-Cambiar el estilo de mapa: pulsar el botón inferior izquierdo, cuyo símbolo es un minimapa. Se encuentra a la izquierda de la lupa\n" +
							"-Abrir el manual: pulsar el botón que tiene un signo de interrogación.\n" +
							"-Buscar sitio por nombre: Pulsar el botón con el icono de la lupa, introducir una dirección en la caja de texto y pulsar OK.\n" +
							"-Cambiar de idioma: pulsar el botón inferior derecho con una bandera");
				else if(idioma.equals("english"))
					morehelp.setMessage("Remember that you need to have the GPS activated so the program can retrieve your current position. It may take two or three minutes for your position to be found (if you are inside a building this may not even happen).\n" +
							"-Add a marker: Touch for a few seconds wherever you want to set the marker and an interface will pop up.\n" +
							"-Update or delete a marker: Select a marker (touching it or using the arrows) and touch its information window. To quickly erase \"Title\" and \"Description\" from the fields, you have to touch twice.\n" +
							"-Change map style: touch on the bottom left button, whose symbol is a minimap. Its located to the left of the magnifier.\n" +
							"-Open the manual: touch the button with the question mark.\n" +
							"-Find location by address: touch the button with the magnifier icon, then input an address and press OK.\n" +
							"-Change language: touch the lower right button, whose symbol is a flag");
				morehelp.setButton("X", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				morehelp.show();
			}
		});
		help.setButton2("X", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		help.show();
	}
	catch(Exception e){
       	e.printStackTrace();
    }
	
}

	/**
	 * Actualiza el marcador que muestra la posición actual
	 */
    public void onLocationChanged(Location location) {
        // Getting latitude of the current location
        double latitude = location.getLatitude();
 
        // Getting longitude of the current location
        double longitude = location.getLongitude();
 
        // Creating a LatLng object for the current location
        latLng = new LatLng(latitude, longitude);

        //Add marker to current position
        if(currentPosition!=null){
        	markers.remove(currentPosition);
        	currentPosition.marker.remove();
        }
        currentPosition=new MarkerWithId();
        if(idioma.equals("spanish"))
        	currentPosition.marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Estás aquí").snippet("O almenos has estado recientemente").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));    
        else if(idioma.equals("english"))	
        	currentPosition.marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You're here").snippet("Or you have recently been here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));    
        
        markers.add(currentPosition); // TEMPORAL. CAMBIAR POR FLECHA AZUL
    }
    
    /**
     * Elimina de la caché y de la base de datos el marcador
     */
    public void deleteMarker(){
    	if(!markers.isEmpty()){
    		// Borramos del sqlite
    			addMarker=markers.get(currentMarker);
    			handler.deleteMarker(addMarker);

			// Borramos del resto de cosas
			addMarker.marker.remove();
			markers.remove(currentMarker);
			currentMarker=0;
			if(idioma.equals("spanish"))
				Toast.makeText(this, "Marcador eliminado", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(this, "Marker deleted", Toast.LENGTH_SHORT).show();
		}
		else if(idioma.equals("spanish"))
			Toast.makeText(this, "No tienes marcadores", Toast.LENGTH_SHORT).show();
		else if(idioma.equals("english"))
			Toast.makeText(this, "You don't have markers", Toast.LENGTH_SHORT).show();
    }
    /**
     * Añade una reacción al pulsar los marcadores
     */
    public boolean onMarkerClick(final Marker marker) {
    	try{
    		if(!modomenuactivado && !modomodificaractivado && !modotrazarutaactivado && !modoaddmarker){
	    		if(idioma.equals("spanish"))
	    			Toast.makeText(this, "Pulsa en el cartel para mostrar el menú", Toast.LENGTH_SHORT).show();
	    		else
	    			Toast.makeText(this, "Touch on the information window to show the menu", Toast.LENGTH_SHORT).show();
		    	for (MarkerWithId myMarker : markers){
		    		if (marker.equals(myMarker.marker)){
		                try{
			                currentMarker=markers.indexOf(myMarker); 
			                addMarker=markers.get(currentMarker);
			                googleMap.animateCamera(CameraUpdateFactory.newLatLng(myMarker.marker.getPosition()));
			                myMarker.marker.showInfoWindow();
			                
			                // Save current marker in case aplication is closed
			                editor.putLong("id",addMarker.id);
			                editor.commit();
			    	        
			                changeEditTexts();
		    	        }
		            	catch(Exception e){
		            		e.printStackTrace();
		            	}
		            }
		    	}
	    	}
    	}
    	catch(Exception e){
        	e.printStackTrace();
        }
    	return true;
    }
    /**
     * Al hacer long click en el mapa saldrá el menú de creación de marcador
     */
    @Override
	public void onMapLongClick(LatLng marker) {
    	if(!modomodificaractivado && !modomenuactivado && !modotrazarutaactivado && !modoaddmarker){
    		
    		//mostrar los dos nuevos botones del nuevo menú (insertar y ver en street view)
    		findViewById(R.id.buttonInsertar).setVisibility(View.VISIBLE);
    		findViewById(R.id.buttonStreetViewNoMarker).setVisibility(View.VISIBLE);
    		
    		modomenuactivado = true;
    		onLongClickMarker = marker;
    		
    		UiSettings mapInterface = googleMap.getUiSettings();
    		mapInterface.setScrollGesturesEnabled(false);
    	}
	}

    /**
     * Al pulsar en la ventana de información del marcador saldrá el menú de modificación
     */
	@Override
	public void onInfoWindowClick(Marker markertoupdate) {
		if(!modomodificaractivado && !modomenuactivado && !modotrazarutaactivado){
			findViewById(R.id.buttonTrazaruta).setVisibility(View.VISIBLE);
			findViewById(R.id.buttonUpdateMarker).setVisibility(View.VISIBLE);
			findViewById(R.id.ButtonStreetView).setVisibility(View.VISIBLE);
			
			UiSettings mapInterface = googleMap.getUiSettings();
			mapInterface.setScrollGesturesEnabled(false);
			
			modomenuactivado = true;
			
			if(idioma.equals("spanish"))
				Toast.makeText(this, "Pulsa atrás para salir del menú", Toast.LENGTH_SHORT).show();
			else if(idioma.equals("english"))
				Toast.makeText(this, "Press back to exit the menu", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Cambia el comportamiento del botón atrás del móvil; si está abierto el menú de inserción/modificación, se cierra. Y si no, se sale de la app
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK ) {
	    	View search = findViewById(R.id.editSearch);
	    	View buttonok = findViewById(R.id.buttonOK);
	    	View buttonTrazaruta = findViewById(R.id.buttonTrazaruta);
	    	View buttonUpdateMarker = findViewById(R.id.buttonUpdateMarker);
	    	View buttonStreetView = findViewById(R.id.ButtonStreetView);
	    	View buttonStreetViewNoMarker = findViewById(R.id.buttonStreetViewNoMarker);
	    	View buttonInsertar = findViewById(R.id.buttonInsertar);
	    	
	    	if (buttonok.getVisibility() == View.VISIBLE){
	    		buttonok.setVisibility(View.GONE);
	    		search.setVisibility(View.GONE);
	    	}
	    	else if (modomodificaractivado){
	    		closeMenu();
	    		buttonTrazaruta.setVisibility(View.VISIBLE);
	    		buttonUpdateMarker.setVisibility(View.VISIBLE);
	    		modomodificaractivado = false;
	    		modomenuactivado = true;
	    	}
	    	else if (modomenuactivado){
	    		buttonTrazaruta.setVisibility(View.GONE);
	    		buttonUpdateMarker.setVisibility(View.GONE);
	    		buttonStreetView.setVisibility(View.GONE);
	    		buttonStreetViewNoMarker.setVisibility(View.GONE);
	    		buttonInsertar.setVisibility(View.GONE);
	    		modomenuactivado = false;
	    		
	    		// Make the map draggable again
	    		UiSettings mapInterface = googleMap.getUiSettings();
	    		mapInterface.setScrollGesturesEnabled(true);
	    	}
	    	else if (modotrazarutaactivado){
	    		findViewById(R.id.buttonCancel).setVisibility(View.INVISIBLE);
	    		findViewById(R.id.spinnerColor).setVisibility(View.INVISIBLE);
	    		findViewById(R.id.spinnerMarkers).setVisibility(View.INVISIBLE);
	    		findViewById(R.id.buttonPositionToMarker).setVisibility(View.GONE);
	    		findViewById(R.id.buttonMarkerToMarker).setVisibility(View.GONE);
	    		findViewById(R.id.radioGroup1).setVisibility(View.GONE);

	    		buttonTrazaruta.setVisibility(View.VISIBLE);
	    		buttonUpdateMarker.setVisibility(View.VISIBLE);
	    		
	    		modotrazarutaactivado = false;
	    		modomenuactivado = true;
	    	}else if(modoaddmarker){
	    		closeMenu();
	    		modoaddmarker = false;
	    		// Make the map draggable again
	    		UiSettings mapInterface = googleMap.getUiSettings();
	    		mapInterface.setScrollGesturesEnabled(true);
	    		// Delete marker if it was inserted but not saved
		        if (addMarker != null && !markers.contains(addMarker))
		        	addMarker.marker.remove();
	    	}
	    	else
	    		moveTaskToBack(true);

	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Cambia el contenido de los edittexts (visibles o no) al seleccionar un marcador
	 */
	public void changeEditTexts(){
		EditText title = (EditText)findViewById(R.id.markerTitle);
        EditText description = (EditText)findViewById(R.id.markerDescription);
        Spinner spinner = (Spinner)findViewById(R.id.spinnerColor);
        
        addMarker=markers.get(currentMarker);
        title.setText(addMarker.marker.getTitle());
        description.setText(addMarker.marker.getSnippet());
        spinner.setSelection(addMarker.colorPosition);
        //change spinner color
	}
	
	/**
	 * Cierra el menú de inserción/modificación
	 */
	public void closeMenu(){
		findViewById(R.id.markerTitle).setVisibility(View.INVISIBLE);
        findViewById(R.id.markerDescription).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonAccept).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonCancel).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonSave).setVisibility(View.GONE);
        findViewById(R.id.spinnerColor).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonDeletemarker).setVisibility(View.INVISIBLE);
        findViewById(R.id.buttonLeft).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonRight).setVisibility(View.VISIBLE);
	}
	
	/***
	 * 
	 * Clase Async para encontrar unas coordenadas a través de una dirección
	 * 
	 * @param placesName Dirección del lugar a buscar 
	 * @return  Objeto JSON que debemos pasar a la función getLatLng
	 */
	private class FindByAddress extends AsyncTask<String, Void, LatLng> {
		private ProgressDialog progressDialog;
		
		protected void onPreExecute() {
	        super.onPreExecute();
	        progressDialog = new ProgressDialog(MainActivity.this);
	        if(idioma.equals("english"))
	        	progressDialog.setMessage("Finding place, Please wait...");
	        else if(idioma.equals("spanish"))
	        	progressDialog.setMessage("Buscando lugar, espera...");
	        progressDialog.setIndeterminate(true);
	        progressDialog.show();
	    }
		
		@Override
		public LatLng doInBackground(String... placesName) {
			// Get JSON Object
			HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" +placesName[0]+"&ka&sensor=false");
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			try {
				response = client.execute(httpGet);
			    HttpEntity entity = response.getEntity();
			    InputStream stream = entity.getContent();
			    int b;
			    while ((b = stream.read()) != -1) {
			    	stringBuilder.append((char) b);
			    }
			} catch (ClientProtocolException e) {  } catch (IOException e) {  }

			JSONObject jsonObject = new JSONObject();
			    
			try {
				jsonObject = new JSONObject(stringBuilder.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// Get results from it
			Double lon = new Double(0);
			Double lat = new Double(0);
				
			try {
				lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
						.getJSONObject("geometry").getJSONObject("location")
			            .getDouble("lng");

			    lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
			    		.getJSONObject("geometry").getJSONObject("location")
			            .getDouble("lat");

			    } catch (JSONException e) {
			        e.printStackTrace();
			    }
			    return new LatLng(lat, lon);
		}

		/***
		 * Obtiene a partir de un objeto JSON una latitud y una longitud
		 * 
		 * @param jsonObject Objeto obtenido de la función getLocationFromGoogle
		 * @return objeto LatLng con las coordenadas si fueron encontradas
		 */
		@Override
		protected void onPostExecute(LatLng latilen) {
	        progressDialog.hide();       
			if(latilen.latitude!=0 && latilen.longitude!=0){
				if(idioma.equals("english"))
            		Toast.makeText(MainActivity.this, "Location found", Toast.LENGTH_SHORT).show();
            	else if(idioma.equals("spanish"))
            		Toast.makeText(MainActivity.this, "Dirección encontrada", Toast.LENGTH_SHORT).show();
				googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latilen.latitude, latilen.longitude)));
			}
			else if(idioma.equals("english"))
        		Toast.makeText(MainActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
            else if(idioma.equals("spanish"))
            	Toast.makeText(MainActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
			}
	}
	/***
	 * 
	 * Para obtener como llevar a un sitio y dibujar la ruta
	 * 
	 * @param url le pasamos la url a obtener del json
	 * @return  
	 */
	private class connectAsyncTask extends AsyncTask<String, Void, String>{
	    private ProgressDialog progressDialog;
	    

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        progressDialog = new ProgressDialog(MainActivity.this);
	        if(idioma.equals("english"))
	        	progressDialog.setMessage("Fetching route, Please wait...");
	        else if(idioma.equals("spanish"))
	        	progressDialog.setMessage("Buscando ruta, espere...");
	        progressDialog.setIndeterminate(true);
	        progressDialog.show();
	    }
	    @Override
	    protected String doInBackground(String... url) {
	        DrawPath dPath = new DrawPath(MainActivity.this, coloruta);
	        String json = dPath.getJSONFromUrl(url[0]);
	        return json;
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        progressDialog.hide();        
	        if(result!=null){
	        	DrawPath dPath = new DrawPath(MainActivity.this, coloruta);
	            dPath.drawPath(result);
	            if(idioma.equals("english"))
	        		Toast.makeText(MainActivity.this, "Route traced", Toast.LENGTH_SHORT).show();
	            else if(idioma.equals("spanish"))
	            	Toast.makeText(MainActivity.this, "Ruta trazada", Toast.LENGTH_SHORT).show();
	        }
	    }
	}
	
	

    /**
     * Comprueba si el dispositivo tiene acceso a internet
     * 
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private boolean is3gAvailable() {
	    ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	    boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
	            .isConnectedOrConnecting();
	    return is3g;
    }
	public GoogleMap getGoogleMap(){
		return googleMap;
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	@Override
    protected void onResume() {
        super.onResume();
    }

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		/**
		 * botón que abre la barra para buscar una posición a través de una dirección
		 */
		case R.id.buttonSearch:
			showSearchMenu();
		break;
		
		/**
		 * Enseña el manual
		 */
		case R.id.buttonHelp:
			showManual();
		break;
		
		/**
		 * Cambia la vista del mapa
		 */
		case R.id.buttonChangemap:
			try{
				if(googleMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID){
					mapType = GoogleMap.MAP_TYPE_NORMAL;
				}
				else{
					mapType =GoogleMap.MAP_TYPE_HYBRID;
				}
				googleMap.setMapType(mapType);
				
				if(idioma.equals("spanish"))
					Toast.makeText(MainActivity.this, "Tipo de mapa cambiado", Toast.LENGTH_SHORT).show();
				else if(idioma.equals("english"))
					Toast.makeText(MainActivity.this, "Map Type changed", Toast.LENGTH_SHORT).show();
				
				
			}
			catch(Exception e){
	        	e.printStackTrace();
	        }
		break;
		
		/**
		 * Cambia el idioma de la app
		 */
		case R.id.buttonLang:

			Languages lang = new Languages(this);
			
			if(idioma.equals("spanish")){
				idioma="english";
		        editor.putString("idioma","english");
		        editor.commit();
				
		        lang.toEnglish();
				
				Toast.makeText(this, "Language: English", Toast.LENGTH_SHORT).show();
				
			}
			else{
				idioma="spanish";
				editor.putString("idioma","spanish");
		        editor.commit();
				
				lang.toSpanish();
				
				Toast.makeText(this, "Idioma: Español", Toast.LENGTH_SHORT).show();
			}
			break;
			
			/**
			 * Mover la cámara al siguiente marcador
			 */
			case R.id.buttonRight:
				try{
					if(!modomodificaractivado && !modomenuactivado && !modotrazarutaactivado && !modoaddmarker){
						if(!markers.isEmpty()){
							currentMarker++;
							if (currentMarker>=markers.size()) 
								currentMarker=0;
							addMarker = markers.get(currentMarker);
							googleMap.animateCamera(CameraUpdateFactory.newLatLng(addMarker.marker.getPosition()));
							addMarker.marker.showInfoWindow();
							
							// Save current marker in case aplication is closed
			                editor.putLong("id",addMarker.id);
			    	        editor.commit();
			    	        
							changeEditTexts();
						}
						else if(idioma.equals("spanish"))
							Toast.makeText(this, "No tienes marcadores", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(this, "You don't have markers", Toast.LENGTH_SHORT).show();
					}
				}
				catch(Exception e){
		        	e.printStackTrace();
		        }
			break;
			
			/**
			 * Mover la cámara al anterior marcador
			 */
			case R.id.buttonLeft:
				try{
					if(!modomodificaractivado && !modomenuactivado && !modotrazarutaactivado && !modoaddmarker){
						if(!markers.isEmpty()){
							currentMarker--;
							if (currentMarker<0) 
								currentMarker=markers.size()-1;
							addMarker = markers.get(currentMarker);
							googleMap.animateCamera(CameraUpdateFactory.newLatLng(addMarker.marker.getPosition()));
							addMarker.marker.showInfoWindow();
							
							// Save current marker in case aplication is closed
			                editor.putLong("id",addMarker.id);
			                editor.commit();
			    	        
							changeEditTexts();
						}
						else if(idioma.equals("spanish"))
							Toast.makeText(this, "No tienes marcadores", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(this, "You don't have markers", Toast.LENGTH_SHORT).show();
					}
				}
				catch(Exception e){
		        	e.printStackTrace();
		        }
			break;
		}
	}

	
	// Para que al ir cambiando los colores del spinner se vean previsualizando en el marcador
//  spinner.setOnItemSelectedListener( 
//  new OnItemSelectedListener() {
//		@Override
//		public void onItemSelected(AdapterView<?> parent,
//				View view, int position, long id) {
//				Marker marcador = markers.get(currentMarker).marker;
//				if(marcador!=null){
//					colormarcador=marcador.
//					switch(position){
//					case 0:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//						break;
//					case 1:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//						break;
//					case 2:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//						break;
//					case 3:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//						break;
//					case 4:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
//						break;
//					case 5:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//						break;
//					case 6:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//						break;
//					case 7:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
//						break;
//					case 8:
//						marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//						break;
//					}
//				}
//		}
//		
//		
//		@Override
//		public void onNothingSelected(AdapterView<?> parent) {
//		}
//  });

    
}
