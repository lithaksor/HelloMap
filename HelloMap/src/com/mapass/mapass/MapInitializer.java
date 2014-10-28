package com.mapass.mapass;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapInitializer {

	private MainActivity actividadMapa;
	
	public MapInitializer(MainActivity views){
		actividadMapa = views;
	}
	
	public void initializeMap(){
		// Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(actividadMapa.getBaseContext());
        
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, actividadMapa, requestCode);
            dialog.show();
        }else { // Google Play Services are available
        	
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) actividadMapa.getSupportFragmentManager().findFragmentById(R.id.map);
 
            // Getting GoogleMap object from the fragment
            actividadMapa.googleMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            actividadMapa.googleMap.setMyLocationEnabled(true);
            
           // Set clickable markers and other click listeners
            actividadMapa.googleMap.setOnMarkerClickListener(actividadMapa);
            actividadMapa.googleMap.setOnMapLongClickListener(actividadMapa);
            actividadMapa.googleMap.setOnInfoWindowClickListener(actividadMapa);
            
            // Set map type
            actividadMapa.googleMap.setMapType(actividadMapa.mapType);
    	    
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) actividadMapa.getSystemService(actividadMapa.LOCATION_SERVICE);

            // Load markers from SQLite
            actividadMapa.handler = new MySQLiteHelper(actividadMapa);
            actividadMapa.markers = actividadMapa.handler.readMarkers(actividadMapa.googleMap);
            
            // Check if GPS Provider is enabled
            @SuppressWarnings("deprecation")
            String gpsprovider = Settings.Secure.getString(actividadMapa.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (!gpsprovider.equals("")){
            	// Getting Current Location
                locationManager.requestLocationUpdates(gpsprovider, 1000, 0, actividadMapa);
                Location location = locationManager.getLastKnownLocation(gpsprovider);
                if(location!=null){
                	actividadMapa.onLocationChanged(location);
                }
                if(actividadMapa.idioma.equals("spanish"))
                	Toast.makeText(actividadMapa, "Mantén pulsado para añadir un marcador", Toast.LENGTH_LONG).show();
            	else if(actividadMapa.idioma.equals("english"))
                	Toast.makeText(actividadMapa, "Touch and hold to add a marker", Toast.LENGTH_LONG).show();
            }else{
            	if(actividadMapa.idioma.equals("spanish"))
                	Toast.makeText(actividadMapa, "Es necesario que actives el GPS para situar el mapa cerca de donde estés", Toast.LENGTH_LONG).show();
            	else if(actividadMapa.idioma.equals("english"))
                	Toast.makeText(actividadMapa, "You need to activate the GPS in order to locate you in the map", Toast.LENGTH_LONG).show();
            }
        }
        
        // Get last marker selected and show it on the map
        long idtosearch = 0;
        if(actividadMapa.prefs.getLong("id", idtosearch)==0) {
        	if(!actividadMapa.markers.isEmpty()){
        		actividadMapa.googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        		actividadMapa.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(actividadMapa.markers.get(0).marker.getPosition().latitude,actividadMapa.markers.get(0).marker.getPosition().longitude)));
        	}
        }
        else{
        	idtosearch = actividadMapa.prefs.getLong("id", idtosearch);
        	actividadMapa.editor.commit();
            if(!actividadMapa.markers.isEmpty()){
            	for(int i=0;i<actividadMapa.markers.size();i++){
                	if(actividadMapa.markers.get(i).id==idtosearch){
                		actividadMapa.googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                		actividadMapa.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(actividadMapa.markers.get(i).marker.getPosition().latitude,actividadMapa.markers.get(i).marker.getPosition().longitude)));
                	}
                }
            }
        }
	}
	
}
