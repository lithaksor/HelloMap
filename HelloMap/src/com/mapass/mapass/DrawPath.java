package com.mapass.mapass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.util.Log;

public class DrawPath {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    
    int color;
    MainActivity actividadPrincipal;
    
	
	public DrawPath(MainActivity actividadPrincipal, int color){
		this.actividadPrincipal = actividadPrincipal;
		this.color = color;
	}
    
    
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }
	    return poly;
	}
	
    public void drawPath(String  result) {

        try {
                //Tranform the string into a json object
               final JSONObject json = new JSONObject(result);
               JSONArray routeArray = json.getJSONArray("routes");
               JSONObject routes = routeArray.getJSONObject(0);
               JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
               String encodedString = overviewPolylines.getString("points");
               List<LatLng> list = decodePoly(encodedString);

               PolylineOptions options = new PolylineOptions().width(5).color(color).geodesic(true);
               for(int z = 0; z<list.size();z++){ // antes era size -1
                    LatLng point= list.get(z);
                    options.add(point);
                }
               actividadPrincipal.googleMap.addPolyline(options);
        } 
        catch (JSONException e) {

        }
    } 
	
	// Parser Class
    public String getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            json = sb.toString();
            is.close();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;

    }
	
    //First of all we will get source and destination points between which we have to draw route. Then we will pass these attribute to getJSONFromUrl function.
	public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog, String mode){
        String url = "https://maps.googleapis.com/maps/api/directions/json" +
        		"?origin="+Double.toString(sourcelat)+","+Double.toString(sourcelog) +
        		"&destination="+Double.toString(destlat)+","+Double.toString(destlog) +
        		"&sensor=false" +
        		"&mode=" + mode +
        		"&alternatives=true";
        
        return url;
 }
}
